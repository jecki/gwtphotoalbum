/*
 * Copyright 2011 Eckhart Arnold (eckhart_arnold@hotmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


#include "imageio.h"
#include <QCoreApplication>
#include <QMutexLocker>
#include <QStringList>
#include <QNetworkAccessManager>


#ifndef NDEBUG
#include "toolbox.h"
#include <QDebug>
#endif

QThread *ImageIO::mainThread = QThread::currentThread();

struct ImageIO::Request {
	RequestType type;
    ImageIO::CallbackInterface *callback;
    QString	filePath;
    ImageIOCore::UseCache useCache;

    QString key;

    Request(RequestType rtype, CallbackInterface *rcallback, const QString rfilePath,
    		ImageIOCore::UseCache rUseCache)
    {
    	type = rtype;
    	callback = rcallback;
    	filePath = rfilePath;
    	useCache = rUseCache;

    	QStringList serialized;
    	serialized << QString::number((int) type) << QString::number((long) callback) << filePath;
    	key = serialized.join(" ");
    }
};

struct LoadRequest : ImageIO::Request {
	QSize size;

	LoadRequest(ImageIO::RequestType rtype, ImageIO::CallbackInterface *rcallback,
			    const QString rfilePath, const QSize rsize, ImageIOCore::UseCache rUseCache) :
		ImageIO::Request(rtype, rcallback, rfilePath, rUseCache)
	{
		size = rsize;

		QStringList serialized;
    	serialized << key << QString::number(size.width()) << QString::number(size.height());
    	key = serialized.join(" ");
	}
};

//struct ReadFTPRequest : LoadRequest {
//	const QFtp *ftp;
//
//	ReadFTPRequest(ImageIO::RequestType rtype, ImageIO::CallbackInterface *rcallback,
//			    const QString rfilePath, const QSize rsize, const QFtp *ftp,
//			    ImageIOCore::UseCache rUseCache) :
//	    LoadRequest(rtype, rcallback, rfilePath, rsize, rUseCache)
//	{
//		this->ftp = ftp;
//		key += " ftp";
//	}
//};

struct SaveRequest : ImageIO::Request {
	QImage image;
	int quality;

	SaveRequest(ImageIO::RequestType rtype, ImageIO::CallbackInterface *rcallback,
		        const QString rfilePath, const QImage rimage, int rquality, ImageIOCore::UseCache rUseCache) :
        ImageIO::Request(rtype, rcallback, rfilePath, rUseCache)
	{
		image = rimage;
		quality = rquality;

		QStringList serialized;
    	serialized << key << QString::number((long) image.bits()) << QString::number(quality);
    	key = serialized.join(" ");
	}
};

//struct WriteFTPRequest : SaveRequest {
//	const QFtp *ftp;
//
//	WriteFTPRequest(ImageIO::RequestType rtype, ImageIO::CallbackInterface *rcallback,
//	        const QString rfilePath, const QImage rimage, int rquality, const QFtp *ftp,
//	        ImageIOCore::UseCache rUseCache) :
//	    SaveRequest(rtype, rcallback, rfilePath, rimage, rquality, rUseCache)
//	{
//		this->ftp = ftp;
//		key += " ftp";
//	}
//};


//inline QString join(const QStringList stringList) {
//	return stringList.join(" ");
//}


void ImageIO::CallbackInterface::receiveImage(const QString filePath, QImage image)
{ (void) filePath; (void) image; } // suppress unused parameters warnings

void ImageIO::CallbackInterface::imageSaved(const QString filePath)
{ (void) filePath; }; // suppress unused parameter warnings


/*!
 * Returns the instance of the singleton ImageIO.
 * @return  instance of ImageIO class
 */
ImageIO &ImageIO::instance() {
	static ImageIO	singleton;
	return singleton;
}


/*!
 * Constructor for ImageIO: Connects ImageIOCore signals with ImageIO slots ans starts
 * the IO thread.
 * @param parent	Qt parent object, if any
 */
ImageIO::ImageIO(QObject *parent)
    : QThread(parent)
{
	terminateIO = false;
	mainThread = QThread::currentThread();
	connect(&iocore, SIGNAL(imageLoaded(const QString, const QImage)),
			this, SLOT(imageLoaded(const QString, const QImage)), Qt::QueuedConnection);
	connect(&iocore, SIGNAL(imageSaved(const QString)),
			this, SLOT(imageSaved(const QString)), Qt::QueuedConnection);
	connect(&iocore, SIGNAL(ioError(const QString, const QString)),
			this, SLOT(ioError(const QString, const QString)), Qt::QueuedConnection);
	connect(QCoreApplication::instance(), SIGNAL(aboutToQuit()), this, SLOT(cleanUp()));
	start();
	setPriority(QThread::LowPriority); // does this make the gui more responsive during loading?
}


/*!
 * Destructor for ImageIO: stops IO thread, deletes any pending load or save requests.
 */
ImageIO::~ImageIO()
{
	cleanUp();
	Q_ASSERT(pending.isEmpty());
	Q_ASSERT(purgeList.isEmpty());
}

/*!
 * Registeres a network connection under a specific name (e.g. "ftp1:") for sending images
 * via a network protokol to a server. The caller of this method is responsible for setting
 * up the connection and shutting down after deregistering a server.
 *
 * Once a connection has been registered, images can simply be sent or downloaded by
 * calling the saveImage or requestImage method with the connection's name at the beginning
 * of the file path, e.g. "ftp1:/my.website.de/images/img1.jpg"
 *
 * @param name            name under which the server connection shall be registered
 * @param server          QNetworkAccessManager object for the connection
 * @param maxConnections  maximum number files that may be transferred at the same time.
 */
void ImageIO::registerServer(const QString name, QNetworkAccessManager *server, int maxConnections)
{
	iocore.registeredServers.insert(name, server);
	iocore.maxAllowedConnections.insert(name, maxConnections);
	iocore.currentConnections.insert(name, 0);
}

/*!
 * Deregisters a network connection. Deregistering is only allowed after all file transfers
 * have been completed.
 *
 * @param name of the connection to be derigstered.
 */
void ImageIO::deregisterServer(const QString name)
{
	Q_ASSERT(iocore.currentConnections.value(name) == 0);
	iocore.registeredServers.remove(name);

}


/*!
 * Requests the image at 'filePath'. The image is either taken from the cache or loaded.
 * It is not returned directly, but loaded and resized in a separate thread. When loading
 * is finished the 'receiveImage' method of the 'callback' interface is called. If an error
 * occurs, receiveErrorMsg is called instead.
 *
 * ATTENTION: This function must be called from the main thread!!!
 *
 * @param callback  the callback interface
 * @param filePath  the file path of the image
 * @param size      the requested size of the image. If the image on the disk does not
 *                  have the requested, it will be resized so that it fits into size
 *                  while keeping the aspect ratio.
 * @param uc        flag that indicates whether the cache shall be used.
 * @param priority  the priority of the request (in relation to other requests).
 * @return			returns a pointer to the request structure which can be used to identify
 *                  the request or NULL if no more requests are accepted (because application quits).
 */
ImageIO::Request *ImageIO::requestImage(CallbackInterface *callback, const QString filePath,
		                                const QSize size, ImageIOCore::UseCache uc,
		                                RequestPriority priority)
{
	// this restriction is bad (its due to the fact that signals from iocore are always sent to the main event loop)
	Q_ASSERT_X(mainThread == QThread::currentThread(),
			   QString("ImageIO::requestImage "+filePath).toLocal8Bit().data(),
			   "request image must be called from the main thread!");

	if (terminateIO) return NULL;
	LoadRequest *request = new LoadRequest(Load, callback, filePath, size, uc);
	mutex.lock();
	if (checkForDuplicate(request)) {
		mutex.unlock();
		return NULL;
	}
	if (iocore.fromCache(filePath, size, uc)) {
		purgeList.append(request);
	} else {
		if (priority == TopPriority) {
			pending.append(request);
		} else {
			pending.prepend(request);
		}
		moreRequests.wakeOne();
	}
	mutex.unlock();

	return (request);
}


/*!
 * 'Quickly' requests an image. If an image with 'filePath' already exists in the cache in
 * more or less the right size, it is returned directly through the reference parameter
 * 'image'. Otherwise it is loaded (and/or resized) in the separate imageIO thread just as when
 * calling requestImage.
 *
 * ATTENTION: This function must be called from the main thread!!!
 *
 * @param callback  the callback interface
 * @param filePath  the file path of the image
 * @param image     a reference parameter through which the image is returned if it could be
 *                  loaded quickly. (If this was not possible, then image is set to zero.)
 * @param size      the requested size of the image. If the image on the disk does not
 *                  have the requested, it will be resized so that it fits into size
 *                  while keeping the aspect ratio.
 * @param uc        flag that indicates whether the cache shall be used.
 * @param priority  the priority of the request (in relation to other requests).
 * @return          true, if the image has been returned directly. In this case
 *                  parameter 'image' contains the requested image.
 */
bool ImageIO::quickRequest(CallbackInterface *callback, const QString filePath, QImage &image,
		                const QSize size, ImageIOCore::UseCache uc, RequestPriority priority)
{
	// this restriction is bad (its due to the fact that signals from iocore are always sent to the main event loop)
	Q_ASSERT_X(mainThread == QThread::currentThread(),
			   QString("ImageIO::quickRequest "+filePath).toLocal8Bit().data(),
			   "request image must be called from the main thread!");

	QImage img;
	mutex.lock();
	if (iocore.cacheLookup(filePath, size, img) && (size.isEmpty() ||
		(img.width() <= 2 * size.width() && img.height() <= 2 * size.height())))
	{
		mutex.unlock();
		if (!size.isEmpty() && img.size() != size) {
			image = img.scaled(size, Qt::KeepAspectRatio, Qt::SmoothTransformation);
		} else {
			image = img;
		}
		return (true);
	} else {
		mutex.unlock();
		image = QImage();
		requestImage(callback, filePath, size, uc, priority);
		return (false);
	}
}


/*!
 * Loads an image immediately without delegating the task to another thread. This
 * function can be called from any thread, not just the main thread.
 * The method is thread safe.
 *
 * @param  filePath  the path of the image
 * @param  size      the desired image size. (The image will be scaled to this
 *                   size, preserving the aspect ratio.)
 *
 * @return the loaded image. In case of an error, the image is null.
 */
QImage ImageIO::loadImmediately(const QString filePath, const QSize size)
{
	QImage img;
	if (iocore.cacheLookup(filePath, size, img) && (size.isEmpty() ||
		(img.width() <= 2 * size.width() && img.height() <= 2 * size.height())))
	{
		if (!size.isEmpty() && img.size() != size) {
			img = img.scaled(size, Qt::KeepAspectRatio, Qt::SmoothTransformation);
		}
	} else {
		if (img.load(filePath)) {
			if (!size.isEmpty() && img.size() != size) {
				img = img.scaled(size, Qt::KeepAspectRatio, Qt::SmoothTransformation);
			}
		}
	}
	return (img);
}


/*!
 * Saves an image to 'filePath'. When saving is finished the 'receiveImage'
 * method of the 'callback' interface is called. If an error occurs, receiveErrorMsg
 * is called instead.
 *
 * ATTENTION: This function must be called from the main thread!!!
 *
 * @param callback  the callback interface
 * @param filePath  the file path of the image
 * @param image     the image to be saved
 * @param quality   quality parameter for image compression (0 to 100,
 * 					or -1 for default quality)
 * @param uc        flag that indicates whether the cache shall be used.
 * @param priority  the priority of the request (in relation to other requests).
 * @return			returns a pointer to the request structure which can be used to identify
 *                  the request or NULL if no more requests are accepted (because application quits).
 */
ImageIO::Request *ImageIO::saveImage(CallbackInterface *callback, const QString filePath,
		                             const QImage image, int quality, ImageIOCore::UseCache uc,
		                             RequestPriority priority)
{
	// this restriction is bad (its due to the fact that signals from iocore are always sent to the main event loop)
	Q_ASSERT_X(mainThread == QThread::currentThread(),
			   QString("ImageIO::saveImage "+filePath).toLocal8Bit().data(),
			   "request image must be called from the main thread!");

	if (terminateIO) return NULL;
	SaveRequest *request = new SaveRequest(Save, callback, filePath, image, quality, uc);
	mutex.lock();
	if (checkForDuplicate(request)) {
		mutex.unlock();
		return NULL;
	}
	if (priority == TopPriority) {
		pending.append(request);
	} else {
		pending.prepend(request);
	}
	moreRequests.wakeOne();
	mutex.unlock();
	return request;
}


/*!
 * Waits until a particular request has been finished.
 * NOT YET IMPLEMENTED!
 *
 * @param request  the request to be waited for
 */
void ImageIO::waitForRequest(Request *request)
{
  // TO BE IMPLEMENTED
  (void)request;
}


bool ImageIO::cancelRequest(Request *&request)
{
	mutex.lock();
	bool result = pending.removeOne(request);
	if (result) {
		requestBook.remove(request->key);
		delete request;
	}
	mutex.unlock();
	return result;
}


bool ImageIO::cancelAllRequests(CallbackInterface *callback)
{
	mutex.lock();
	bool ret = false;
	foreach(Request *r, pending) {
		if (r->callback == callback || callback == NULL) {
			pending.removeOne(r);
			requestBook.remove(r->key);
			delete r;
			ret = true;
		}
	}
	mutex.unlock();
	return (ret);
}


void ImageIO::run()
{
	mutex.lock();
	while (!terminateIO) {
		mutex.unlock();

		mutex.lock();
		while (!pending.isEmpty()) {
			Request *request = pending.takeLast();
			purgeList.append(request);
			mutex.unlock();

			if (request->type == Load) {
				iocore.loadImage(request->filePath, ((LoadRequest *)request)->size);
			} else {
				iocore.saveImage(request->filePath, ((SaveRequest *)request)->image,
													 ((SaveRequest *)request)->quality);
			}

			mutex.lock();
		}
		if (!terminateIO) {
			moreRequests.wait(&mutex);
		}
		mutex.unlock();

		mutex.lock();
	}
	mutex.unlock();
}



bool ImageIO::checkForDuplicate(Request *request)
{
	if (requestBook.contains(request->key)) {
		delete request;
		return (true);
	} else {
		requestBook.insert(request->key);
		return (false);
	}
}


ImageIO::Request *ImageIO::findRequest(ImageIO::RequestType type, const QString filePath) {
	Request	*ret = NULL;

	mutex.lock();
	QList<Request *> copy(purgeList);
	mutex.unlock();

	foreach(Request *r, copy) {
		if ( (r->type & type) && r->filePath == filePath) {
			ret = r;
			mutex.lock();
			purgeList.removeOne(r);
			requestBook.remove(r->key);
			mutex.unlock();
			break;
		}
	}

	return ret;
}




void ImageIO::imageLoaded(const QString filePath, const QImage image)
{
	LoadRequest *request = (LoadRequest *)(findRequest(Load, filePath));

	if (request != NULL) {
		request->callback->receiveImage(filePath, image);
		delete request;
	} else {

#ifndef NDEBUG
		if (!terminateIO) {
			qDebug() << "LoadRequest object for: " << filePath << " missing!";
		}
#endif

	}
}



void ImageIO::imageSaved(const QString filePath)
{
	SaveRequest *request = (SaveRequest *)(findRequest(Save, filePath));

	if (request != NULL) {
		request->callback->imageSaved(filePath);
		delete request;
	} else {

#ifndef NDEBUG
		if (!terminateIO) {
			qDebug() << "SaveRequest object for: " << filePath << " missing!";
		}
#endif

	}
}


void ImageIO::ioError(const QString filePath, const QString errorMsg)
{
	Request *request = findRequest(RequestType(Load|Save), filePath);

	if (request != NULL) {
		request->callback->receiveErrorMsg(filePath, errorMsg);
		delete request;
	} else {

#ifndef NDEBUG
		if (!terminateIO) {
			qDebug() << "Request object for: " << filePath << " missing!";
		}
#endif

	}

#ifndef NDEBUG
//	std::cout << "error " << errorMsg.toStdString() << " while processing: " << filePath.toStdString() << std::endl;
#endif

}


/*!
 * Cancels all requests and waits until all imageIO related threads
 * have terminated.
 */
void ImageIO::cleanUp()
{
//#ifndef NDEBUG
//	std::cout << "ImageIO::cleanUp()" << std::endl;
//#endif

	cancelAllRequests(NULL);
	mutex.lock();
	terminateIO = true;
	mutex.unlock();
	moreRequests.wakeAll();
	if (isRunning()) {
		wait();
	}
	iocore.waitForPendingResizeTasks();
	QCoreApplication::processEvents();
	// quit();
}

