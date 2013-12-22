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



#ifndef TOOLBOX_H_
#define TOOLBOX_H_

#include <QImage>
#include <QSize>
#include <QString>
#include <QStringList>
#include <QFileInfo>
#include <QPixmap>



QStringList getSupportedImageFormats();
bool		hasSupportedImageFormat(const QString &imageFileName);

// Functions for smart resizing of images

QPixmap sizeGuard(const QPixmap pixmap, const QSize size, const int frame = 0);

QImage toolboxResize(QImage image, const QSize &size, int trigger, int preshrink);

/*!
 * Quickly but smoothly resizes an image. The time advantage is given priority.
 * @param image  the image to be resized
 * @param size   the destination size
 * @return the resized image
 */
inline QImage quickResize(const QImage &image, const QSize &size) { return (toolboxResize(image, size, 2, 2)); };

/*!
 * Quickly but smoothly resizes an image. Slower than @code quickResize @endcode , but
 * potentially retaining a better image quality.
 * @param image  the image to be resized
 * @param size   the destination size
 * @return the resized image
 */
inline QImage smartResize(const QImage &image, const QSize &size) { return (toolboxResize(image, size, 4, 2)); };


/*!
 * Returns true if a just fits into b, i.e. either a's height equals b's height and
 * a's width is smaller or equal than b's width or a's width equals b's width and a's height is
 * smaller or equal than b's height. (If an image is resized while retaining the aspect ratio then
 * it "just fits".)
 * @param a one size object for the comparison
 * @param b the other size object for the comparison
 * @return  true, if the sizes match, false otherwise
 */
inline bool sizeFits(const QSize &a, const QSize &b) {
	return ((a.width() == b.width() && a.height() <= b.height()) ||
		    (a.height() == b.height() && a.width() <= b.width()));
}


int bestMatch(const QSize &target, const QList<QSize> &sizes);
QSize adjustSize(const QSize &imageSize, const QSize &targetSize);
QString sizeStr(const QSize &size);
QString strRepSize(const QList<QSize> &sList);

QString readTextFile(const QString &fileName, QStringList &error, bool &ok);
void    writeTextFile(const QString &text, const QString &fileName, QStringList &error, bool &ok);

/*!
 * Base class for a callback for function walkDir. The method
 * processFileInfo is called by walkDir for each new entry.
 */
struct IWalkDirCallback {
	virtual ~IWalkDirCallback() { };
	virtual void processFileInfo(const QFileInfo &info) = 0;
};

void walkDir(const QString &dirName, IWalkDirCallback *action);
QFileInfoList listTree(const QString &dirName);
//bool isAlbumDirectory(const QString &path);
//bool delAlbumTree(const QString &path);

QString cleanPath(const QString &path);
inline QString cleanPath(const char *path) { return (cleanPath(QString(path))); }

QString deriveFileName(const QString &path);


QString versionNumber();

#endif /* TOOLBOX_H_ */
