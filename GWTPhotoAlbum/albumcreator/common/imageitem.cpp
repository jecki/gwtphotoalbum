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


#include "imageitem.h"

#include <Qt>
#include <QCoreApplication>
#include <QEventLoop>
#include <QDir>
#include <QDebug>

#include "toolbox.h"

#ifndef NDEBUG
#include <iostream>
#endif

static QImage ImageItem_nullImage;

QSize ImageItem::Thumbnail_Size = QSize(160, 160);
QSize ImageItem::Preview_Size   = QSize(1280, 960);
QList<ImageItem *> ImageItem::cachedImages;
QList<ImageItem *> ImageItem::cachedPreviews;
int ImageItem::maxImages = 20;
int ImageItem::maxPreviews = 50;
QThread *ImageItem::mainThread = QThread::currentThread();


/*!
 * Returns the original size of the image if class is IMAGE,
 * or the predefined size for class THUMBNAIL or PREVIEW respectively.
 * @param c  the image class the size of which shall be returned
 * @return the size
 */
QSize ImageItem::size(ImageItem::ImageClass c)
{
	if (c == THUMBNAIL) {
		return Thumbnail_Size;
	} else if (c == PREVIEW) {
		return Preview_Size;
	} else {
		if (originalSize.isEmpty()) {
#ifndef NDEBUG
			std::cout << "ImageItem::size "<< this->destName.toStdString() << std::endl;
#endif
			QImage img = image(IMAGE);
			return img.size();
		} else{
			return originalSize;
		}
	}
}


/*!
 * Sets the predefined sizes for image classes PREVIEW or THUMBNAIL
 * @param c  the image class, the size of which shall be set
 * @param size  the size for this image class
 */
void ImageItem::setSize(ImageItem::ImageClass c, QSize size)
{
	Q_ASSERT_X(c != IMAGE, "ImageItem::setSize",
			   "only thumbnails and preview images can have a predefined size!");

	if (c == THUMBNAIL) {
		Thumbnail_Size = size;
	} else {
		Preview_Size = size;
	}
}



/*!
 * Sets the cache size for the given image class.
 * @param c  the image class for which the cache size shall be set
 * @param size  the maximum number of images to be held in the cache
 */
void ImageItem::setCacheSize(ImageClass c, int size)
{
	Q_ASSERT_X(c != THUMBNAIL, "ImageItem::setCacheSize",
			   "Cache size for thumbnails cannot be set, because thumbnails are never cached!");
	Q_ASSERT_X(size < 10000, "ImageItem::setCacheSize",
			   "Cache size must be smaller than 10.000 pics. (Should be much smaller, really!)");
	Q_ASSERT_X(size >= 1, "ImageItem::setCacheSize",
			   "Cache size must at least be 1!");

	if (c == PREVIEW) {
		maxPreviews = size;
		while (cachedPreviews.size() > maxPreviews) {
			ImageItem *item = cachedPreviews.takeFirst();
			item->original = ImageItem_nullImage;
		}
	} else {
		maxImages = size;
		while (cachedImages.size() > maxImages) {
			ImageItem *item = cachedImages.takeFirst();
			item->original = ImageItem_nullImage;
		}
	}
}


/*!
 * Constructor of class ImageItem.
 * @param path  the file path to an image. The image will not be loaded
 *              immediately, but on demand later.
 */
ImageItem::ImageItem(const QString path)
    : QObject(0)
{
	filePath = QDir::fromNativeSeparators(path);
	requestsIssued = 0;
	requestedClasses = 0;
	int i = filePath.lastIndexOf('/');
	destName = filePath.right(path.size() - (i+1));
}


ImageItem::~ImageItem()
{
	cachedImages.removeOne(this);
	cachedPreviews.removeOne(this);
}


/*!
 * Notifies the ImageItem object that some specific class of the image will
 * be needed soon. This allows the ImageItem object to already load (and
 * possibly resize) the image in a separate thread.
 * @param imageClass  the image class to be prefetched, i.e. IMAGE, THUMBNAIL or PREVIEW
 */
void ImageItem::prefetchImage(int imageClass)
{
	Q_ASSERT_X(imageClass == IMAGE || imageClass == PREVIEW || imageClass == THUMBNAIL,
			   "ImageItem::prefetchImage", "illegal image class");

	if (!noError()) return;  // if there was already an error, then do not try fetch images

	QImage *img = &original;
	QSize size = QSize();
	int request = requestedClasses;

    if (imageClass == PREVIEW) {
		img = &previewImg;
		size = Preview_Size;
		request |= PREVIEW;
	} else if (imageClass == THUMBNAIL) {
		img = &thumbnailImg;
		size = Thumbnail_Size;
		request |= THUMBNAIL;
	} else {
		request |= IMAGE;
	}

	if (img->isNull()) {
		requestsIssued++;
		ImageIO::instance().quickRequest(this, filePath, *img, size);
		if (img->isNull()) {
			requestedClasses = request;
		} else if (!original.isNull()) {
			originalSize = original.size();
		}
	}
}


/*!
 * Fetches a specific version of the image, i.e. THUMBNAIL,
 * PREVIEW or IMAGE, the latter being the original image itself.
 * @c  the class of the image to be fetched
 */
QImage ImageItem::image(ImageClass c)
{
	prefetchImage((int) c);
	QImage img = fetch(c);
	if (img.isNull()) {
		while(noError() && fetch(c).isNull()) {  // need to implement aboutToQuit-Slot to get out of this loop!?
			QCoreApplication::instance()->processEvents(QEventLoop::WaitForMoreEvents);
		}
		return fetch(c);
	} else {
		return img;
	}
}


/*!
 * Writes the caption string back to the original image file.
 * NOT YET IMPLEMENTED
 */
void ImageItem::storeCaptionInOriginal()  {
	// to be implemented...
}


/*!
 * Returns the image size for the given class. An empty size means the
 * original of the image whatever its value may be.
 * @param  c  image class for which the size is to be determined.
 * @return the appropriate size as a QSize object.
 */
QSize imgSize(ImageItem::ImageClass c) {
	if (c == ImageItem::PREVIEW) {
		return ImageItem::Preview_Size;
	} else if (c == ImageItem::THUMBNAIL) {
		return ImageItem::Thumbnail_Size;
	} else {
		return QSize();
	}
}


/*!
 * Returns a list of resized versions of the image ordered from the smallest to the largest.
 * If an error occured (i.e. the image could no be loaded), an empty list is returned.
 * @param sizes  a list of sizes to which the image shall be resized. Size(-1,-1) means
 *               that the original image size should be kept.
 * @return the list of resized images
 */
QList<QImage> ImageItem::resized(QList<QSize> sizes) {
	QList<QImage> ret;
	QImage 		  img;

	if (QThread::currentThread() == mainThread) {
		img = image(IMAGE);
	} else { // poor workaround !!!
		// mutex.lock();
		if (noError()) {
			requestsIssued++;
			requestedClasses |= IMAGE;
			img = ImageIO::instance().loadImmediately(filePath, imgSize(IMAGE));
			if (img.isNull()) {
				receiveErrorMsg(filePath, "could not load image");
				return ret;
			} else {
				receiveImage(filePath, img);
			}
		}
		// mutex.unlock();
	}
	if (!noError()) return ret;

	int N = sizes.length();
	int i = N - 1;

	if (N == 0) {
		ret.prepend(img);
		return ret;
	}

#ifndef NDEBUG
//	foreach (QSize sz, sizes) {
//		qDebug() << sz;
//	}
	int l = i;
	if (sizes[i].isEmpty()) l--;
	while (l > 0) {
		Q_ASSERT_X((sizes[l].width() > sizes[l-1].width() && sizes[l].height() >= sizes[l-1].height()) ||
				   (sizes[l].width() >= sizes[l-1].width() && sizes[l].height() > sizes[l-1].height()),
				   "ImageItem::resized", "sizes list wasn't ordered from smallest to largest!");
		l--;
	}
#endif // NDEBUG

	if (sizes[i].isEmpty()) {
		ret.prepend(img);
		sizes[i].setHeight(img.height());
		sizes[i].setWidth(img.width());
		i--;
	}

	while (i >= 0 && img.height() <= sizes[i].height() && img.width() <= sizes[i].width()) {
		ret.prepend(img);
		i--;
	}

	while (i >= 0) {
		QImage slide;
		int k = i+1;
		while (k < N && sizes[k].width() < 2*sizes[i].width()
				     && sizes[k].height() < 2*sizes[i].height()) k++;
		if (k < N) {
			slide = smartResize(ret[k-i-1], sizes[i]);
		} else {
			slide = smartResize(img, sizes[i]);
		}
		ret.prepend(slide);
		i--;
	}

	return ret;
}


/*!
 * Returns true, if the image is valid, which is the case if:
 * a) the image item's path points to an image file and
 * b) that image file can be loaded
 * @return true if image item contains a valid image!
 */
bool ImageItem::isValid() {
	return noError() && (!error.isNull() || !image().isNull());
}


void ImageItem::cache(ImageItem::ImageClass c) {
	Q_ASSERT_X(c == IMAGE || c == PREVIEW,
			   "ImageItem::cache", "image class must be IMAGE or PREVIEW");

	if (c == IMAGE) {
		Q_ASSERT_X(!original.isNull(), "ImageItem::cache",
				   "trying to cache non existent original image");
		Q_ASSERT_X(!cachedImages.contains(this), "ImageItem::cache",
				   "trying to cache and image that has already been cached.");

		cachedImages.append(this);
		if (cachedImages.size() > maxImages) {
			ImageItem *item = cachedImages.takeFirst();
			item->original = ImageItem_nullImage;
		}

	} else {
		Q_ASSERT_X(!original.isNull(), "ImageItem::cache",
				   "trying to cache non existent preview image");
		Q_ASSERT_X(!cachedImages.contains(this), "ImageItem::cache",
				   "trying to cache a preview image that has already been cached.");

		cachedPreviews.append(this);
		if (cachedPreviews.size() > maxPreviews) {
			ImageItem *item = cachedPreviews.takeFirst();
			item->original = ImageItem_nullImage;
		}
	}
}


QImage ImageItem::fetch(ImageClass c) const {
	Q_ASSERT_X(c == IMAGE || c == PREVIEW || c == THUMBNAIL,
			   "ImageItem::fetch", "image class must be IMAGE or PREVIEW or THUMBNAIL");

	if (c == THUMBNAIL) {
		return thumbnailImg;
	} else if (c == PREVIEW) {
		return previewImg;
	} else {
		return original;
	}
}


/*!
 * This callback handler receives an image from the imageio and assigns it to
 * the proper image class.
 * @param filePath  the file path of the image
 * @param image     the image itself
 */
void ImageItem::receiveImage(const QString filePath, QImage image) {
	bool flag = false;
	if (filePath == this->filePath) {
		requestsIssued--;
		error = QString("");
		if ( (requestedClasses & THUMBNAIL) && sizeFits(image.size(), Thumbnail_Size)) {
			thumbnailImg = image;
			requestedClasses = requestedClasses & ~THUMBNAIL;
		} else if ( (requestedClasses & PREVIEW) && sizeFits(image.size(), Preview_Size)) {
			if (previewImg.isNull()) flag = true;
			previewImg = image;
			if (flag) cache(PREVIEW);
			requestedClasses = requestedClasses & ~PREVIEW;
		} else if (requestedClasses & IMAGE) { // might involuntarily return a thumbnail image, if a) both IMAGE and THUMBNAIl have been requested and the thumbnail size been changed meanwhile!!!
			if (original.isNull()) flag = true;
			original = image;
			originalSize = image.size();
			if (flag) cache(IMAGE);
			requestedClasses = requestedClasses & ~IMAGE;
		} else {
			// ERROR!!! Might happen if preview or thumbnail size has been changed between request and reception of a preview or thumbnail image!!!
		}
	}
}


void ImageItem::imageSaved(const QString filePath) {
	if (filePath == this->filePath) {
		requestsIssued--;

	}
}


void ImageItem::receiveErrorMsg(const QString filePath, const QString errorMsg) {
	if (filePath == this->filePath) {
		requestsIssued--;
		error = errorMsg + " " + filePath;
		if (requestsIssued == 0) {
			requestedClasses = 0;
		}
	}
}

