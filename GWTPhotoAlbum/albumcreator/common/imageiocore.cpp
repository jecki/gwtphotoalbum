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


#include "imageiocore.h"
#if QT_VERSION >= 0x050000
#include <QtConcurrent/QtConcurrentRun>
#else
#include <QtCore/QtConcurrentRun>
#endif
#include <QMutexLocker>
#include <QVariant>

#include "toolbox.h"

#ifndef NDEBUG
#include <iostream>
#endif


QString MAGIC_KEY("albumcreator:");
QString MAGIC_ORIGINAL_SIZE("original size");
QString MAGIC_SCALED("");


inline bool isOriginalSize(QImage &img) {
	return (img.text(MAGIC_KEY) == MAGIC_ORIGINAL_SIZE);
}

/*!
 * Loads the image at 'filePath' and resizes it to 'size'. Emits signal
 * 'ioError' if an error occurs or signal 'imageLoaded' if successful.
 *
 * @param filePath  the location of the image
 * @param size      size to which the image shall be resized
 * @param uc        Optionally, try to pick image from cache first.
 */
void ImageIOCore::loadImage(const QString filePath, const QSize size, UseCache uc)
{
	QImage img;
	if (cacheLookup(filePath, size, img) || img.load(filePath)) {
		resizeTask(QtConcurrent::run(this, &ImageIOCore::resizeAndNotify,
									 img, filePath, size, uc));
	} else {
		emit ioError(filePath, "could not load image ("+sizeStr(size)+") ");
	}
}


bool ImageIOCore::fromCache(const QString filePath, const QSize size, UseCache uc)
{
	QImage img;
	if (cacheLookup(filePath, size, img)) {
		resizeTask(QtConcurrent::run(this, &ImageIOCore::resizeAndNotify,
									 img, filePath, size, uc));
		return (true);
	}
	return (false);
}


void ImageIOCore::saveImage(const QString filePath, const QImage image, int quality, UseCache uc)
{
	if (uc == Cache) cacheStore(filePath, image);
	QImage img = image;
	img.setText(MAGIC_KEY, QString(""));
	if (img.save(filePath, 0, quality)) {
		emit imageSaved(filePath);
	} else {
		emit ioError(filePath, "could not save image");
	}

}


void ImageIOCore::resizeAndNotify(QImage img, const QString filePath, const QSize size, UseCache uc)
{
	if (size.isEmpty()) {
		img.setText(MAGIC_KEY, MAGIC_ORIGINAL_SIZE);
	} else {
		if (uc == Cache) cacheStore(filePath, img);
		img.setText(MAGIC_KEY, MAGIC_SCALED);
		intermediateStage(img, filePath, size);
		img = quickResize(img, size);
	}
	if (uc == Cache) cacheStore(filePath, img);
	emit imageLoaded(filePath, img);
}


bool ImageIOCore::cacheLookup(const QString filePath, const QSize size, QImage &image)
{
	QMutexLocker lock(&cacheMutex);
	QImage *img;

	// special case if original image is requested:
	if (size.isEmpty()) {
		img = big.object(filePath);
		if (img != NULL && isOriginalSize(*img)) {
			image = QImage(*img);
			return (true);
		}
		img = medium.object(filePath);
		if (img != NULL && isOriginalSize(*img)) {
			image = QImage(*img);
			return (true);
		}
		img = small.object(filePath);
		if (img != NULL && isOriginalSize(*img)) {
			image = QImage(*img);
			return (true);
		}
		return (false);
	}

	if (size.width() < smallW && size.height() < smallH) {
		img = small.object(filePath);
		if (img != NULL && (img->width() >= size.width() || img->height() >= size.height())) {
			image = QImage(*img);
			return (true);
		}
	}
	if (size.width() <= bigW && size.height() <= bigH) {
		img = medium.object(filePath);
		if (img != NULL && (img->width() >= size.width() || img->height() >= size.height())) {
			image = QImage(*img);
			return (true);
		}
	}
	img = big.object(filePath);
	if (img != NULL && (img->width() >= size.width() || img->height() >= size.height())) {
		image = QImage(*img);
		return (true);
	}
	return (false);
}


void ImageIOCore::cacheStore(const QString filePath, const QImage image)
{
	cacheMutex.lock();
	QImage *img = new QImage(image);
	QCache<QString, QImage> *cache = selectCache(image.size());
	QImage *old = cache->object(filePath);
	if (old == NULL || old->width()*old->height() < image.width()*image.height()) {
		cache->insert(filePath, img, cost(image.size()));
/*#ifndef NDEBUG
		std::cout << "inserted : ";
#endif*/
	}
	cacheMutex.unlock();

/*#ifndef NDEBUG
	QString str;
	if (cache == &big) str = " big ";
	else if (cache == &medium) str = " medium ";
	else str = " small ";
	std::cout << filePath.toStdString() << " : "<< str.toStdString() << cache->totalCost() << std::endl;
#endif*/
}


void ImageIOCore::setCacheSizes(int bigKb, int mediumKb, int smallKb)
{
	cacheMutex.lock();
	big.setMaxCost(bigKb);
	medium.setMaxCost(mediumKb);
	small.setMaxCost(smallKb);
	cacheMutex.unlock();
}


void ImageIOCore::setCacheMetrics(int bigW, int bigH, int smallW, int smallH,
		                          bool intermediates)
{
	cacheMutex.lock();
	if (bigW < smallW) {
		int swap = bigW;
		bigW = smallW;
		smallW = swap;
	}
	if (bigH < smallH) {
		int swap = bigH;
		bigH = smallH;
		smallH = swap;
	}

	if (bigW > 0) this->bigW = bigW;
	if (bigH > 0) this->bigH = bigH;
	if (smallW > 0) this->smallW = smallW;
	if (smallH > 0) this->smallH = smallH;

	createIntermediateStage = intermediates;
	cacheMutex.unlock();
}


/*!
 *	Waits until all pending resize-tasks have finished.
 */
void ImageIOCore::waitForPendingResizeTasks()
{
	resizeMutex.lock();
	foreach (QFuture<void> item, resizeTasks) {
		if (item.isFinished()) {
			resizeTasks.removeOne(item);
		} else {
			item.waitForFinished();
		}

	}
	resizeMutex.unlock();
}


QCache<QString, QImage> *ImageIOCore::selectCache(QSize size)
{
	if (size.width() > bigW || size.height() > bigH) {
		return (&big);
	} else if (size.width() < smallW && size.height() < smallH) {
		return (&small);
	} else {
		return (&medium);
	}
}




ImageIOCore::ImageIOCore(QObject *parent) : QObject(parent) {
	bigW = 1920; bigH = 1200; smallW = 480; smallH = 360;
	setCacheSizes(65536, 65536, 32768);
	setCacheMetrics(1600, 1200, 320, 240, true);  // smallW and smallH should not be larger than ThumbnailSize.x and y times 2!!!
}

ImageIOCore::~ImageIOCore() {
	waitForPendingResizeTasks();
}


void ImageIOCore::intermediateStage(QImage img, const QString filePath, const QSize size)
{
	if (createIntermediateStage &&
		(selectCache(img.size()) == &big) && (selectCache(size)) == &small) {
		img = quickResize(img, QSize(smallW, smallH));
		cacheStore(filePath, img);
	}
}

/*!
 * Adds a QFuture object to the list of resize tasks that have been started
 * with QtConcurrent::run.
 *
 * @param task	the QFuture object to be added.
 */
void ImageIOCore::resizeTask(const QFuture<void> task) {
	resizeMutex.lock();
	foreach (QFuture<void> item, resizeTasks) {
		if (item.isFinished()) {
			resizeTasks.removeOne(task);
		}
	}
	resizeTasks.append(task);
	resizeMutex.unlock();
}
