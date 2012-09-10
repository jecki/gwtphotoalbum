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



#include "toolbox.h"

#include <QIODevice>
#include <QTextStream>
#include <QDir>
#include <QFile>
#include <QFileInfo>
#include <QString>
#include <QStringList>
#include <QByteArray>
#include <QImageReader>

#include <iostream>

/*!
 *  \fn QList<QString> getSupportedImageFormats()
 *
 *  Returns a list of suffixes of supported image formats.
 */
QStringList getSupportedImageFormats()
		{
	QList<QByteArray> formatList = QImageReader::supportedImageFormats();
	QStringList supportedFormats;
	foreach(QByteArray item, formatList) {
		QString strItem(item);
		supportedFormats.append(strItem.toUpper());
	}
	return supportedFormats;
}


QStringList toolbox_supportedFileFormats = getSupportedImageFormats();

/*!
 * Checks, if, judging by the file name's suffix it is an image file with
 * a supported file format.
 * @param  imageFileName  file name of an image file.
 * @return true, if file has a supported image format.
 */
bool hasSupportedImageFormat(const QString &imageFileName)
{
	QFileInfo fi(imageFileName);
	QString   ext(fi.suffix().toUpper());
	foreach(QString fmtStr, toolbox_supportedFileFormats) {
		if (fmtStr == ext) return true;
	}
	return false;
}


/*!
 * Resizes an image smoothly, but tries to reduce the speed penalty associated with
 * smooth transformation by resizing much larger images to an intermediate size using
 * a fast transformation.
 * @param image		the image to be resized
 * @param size		the size to which the image shall be resized
 * @param trigger	if both edges of the image are at least 'trigger' times larger than
 *                  the destination size than a fast transformation resize is applied first.
 * @param preshrink edge size multiplier for the intermediate representation
 * @return the resized image
 */
QImage toolboxResize(QImage image, const QSize &size, int trigger, int preshrink) {
	Q_ASSERT_X(preshrink <= trigger, "toolboxResize",
			   "'trigger' must not be smaller than 'preshrink'!");

	if (image.width() > trigger * size.width() || image.height() > trigger * size.height()) {
		image = image.scaled(preshrink * size.width(), preshrink * size.height(),
						 Qt::KeepAspectRatio, Qt::FastTransformation);
	}
	if (image.size() != size) {
		image = image.scaled(size.width(), size.height(), Qt::KeepAspectRatio, Qt::SmoothTransformation);
	}

	return image;
}


/*!
 * Returns a string representation of a size object.
 * @param size the size object to be converted to a string.
 * @return the size as string, e.g. "1024x768"
 */
QString sizeStr(const QSize &size) {
	if (size.isEmpty()) {
		return QString("original_size");
	} else {
		return QString::number(size.width()) + QString("x") + QString::number(size.height());
	}
}


/*!
 * Returns the index of the item in a list of sizes that matches a given
 * target size best. A match is the better the lower (deltaX+1)*(deltaY+1) is.
 * If target has a width or height < 0 (indicating "original size",
 * the index of the biggest size in the list is returned.
 *
 * @param target  the target size to be matched
 * @return the index of the item in the list of sizes that matches best
 */
int bestMatch(const QSize &target, const QList<QSize> &sizes) {
	int bestMatch = 0;
	if (target.width() < 0 || target.height() < 0) {
		int maxW = -1, maxH = -1;
		for (int i = 0; i < sizes.count(); i++) {
			if (sizes[i] == target) {
				return i;
			} else {
				int w = sizes[i].width();
				int h = sizes[i].height();
				if (w > maxW || h > maxH) {
					maxW = w;
					maxH = h;
					bestMatch = i;
				}
			}
		}
	} else {
		int delta = 1000*1000;
		for (int i = 0; i < sizes.count(); i++) {
			int dx = abs(target.width() - sizes[i].width());
			int dy = abs(target.height() - sizes[i].height());
			int dxy = (dx+1)*(dy+1);
			if (dxy < delta) {
				delta = dxy;
				bestMatch = i;
			}
		}
	}
	return bestMatch;
}

/*!
 * Adjusts a size so that it fits into a given target size while
 * keeping the aspect ratio.
 * @param imageSize the image's size
 * @param targetSize the size to which the image's size is to be adapted
 * @return the adjusted size
 */
QSize adjustSize(const QSize &imageSize, const QSize &targetSize) {
	if (targetSize.isEmpty()) return imageSize;

	int sx = imageSize.width();
	int sy = imageSize.height();

	Q_ASSERT_X(sx > 0 && sy > 0, "toolbox adjustSize()", "image width or height is < 0 !");

	int tx = targetSize.width();
	int ty = targetSize.height();

	int x = tx;
	int y = sy*tx/sx;

	if (y > ty) {
		y = ty;
		x = sx*ty/sy;
	}

	return QSize(x, y);
}


/*!
 * Generates a string representation of a list of sizes
 * @return string representation
 */
QString strRepSize(const QList<QSize> &sList) {
	QString rep;
	QTextStream stream(&rep);
	foreach(const QSize &s, sList) {
		stream << s.width() << 'x' << s.height() << ";";
	}
	stream.flush();
	return rep;

/*	QStringList rep;
	foreach(const QSize &s, sList) {
		rep.append(QString::number(s.width()));
		rep.append("x");
		rep.append(QString::number(s.height()));
	}
	return rep.join(";");*/
}



/*!
 * Reads a string to file.
 * @param fileName  the file name.
 * @param error     a reference to a string list to which an error message is
 *                  appended if an error occurs.
 * @param ok		set to false if an error is encountered; if already
 *                  false when calling this function, it will return immediately
 * @return			the contents of the file as text string.
 */
QString readTextFile(const QString &fileName, QStringList &error, bool &ok) {
	QString text;
	if (ok) {
		QFile file(fileName);
		if (file.open(QIODevice::ReadOnly)) {
			QTextStream stream(&file);
			text = stream.readAll();
			file.close();
			if (file.error() != QFile::NoError) {
				error << file.errorString();
				ok = false;
			}
		} else {
			error << file.errorString()+"\n";
			ok = false;
		}
	}
	return text;
}


/*!
 * Writes a string to a file.
 * @param text		the string to be written.
 * @param fileName  the file name.
 * @param error		a reference to a string list to which an error message is
 *                  appended if an error occurs.
 * @param ok		set to false if an error is encountered; if already
 *                  false when calling this function, it will return immediately
 * @return			true, if no error occurred.
 */
void writeTextFile(const QString &text, const QString &fileName, QStringList &error, bool &ok) {
	if (ok) {
		QFile file(fileName);
		if (file.open(QIODevice::WriteOnly)) {
			QTextStream stream(&file);
			stream << text;
			file.close();
			if (file.error() != QFile::NoError) {
				error << file.errorString();
				ok = false;
			}
		} else {
			error << file.errorString();
			ok = false;
		}
	}
}


/*!
 * Walks a directory tree stepping through all sub-directories and calls
 * 'doSomething' for each QFileInfo object in the tree. Uses a pre-order
 * traversal so that the content of sub-directories is walked before the
 * sub-directory itself is returned.
 * @param dirName		the name of the directory to be traversed
 * @param doSomething   a function called for each QFileInfo object
 *                      in the tree
 */
void walkDir(const QString &dirName, IWalkDirCallback *action) {
	QDir dir(dirName);
	QFileInfoList contents = dir.entryInfoList(QDir::AllEntries|QDir::NoDotAndDotDot,
			QDir::DirsFirst|QDir::Name|QDir::IgnoreCase);
	foreach(QFileInfo entry, contents) {
		if (entry.isDir()) walkDir(entry.filePath(), action);
		action->processFileInfo(entry);
	}
}


struct ListTreeCallback : IWalkDirCallback {
	QFileInfoList list;
	virtual void processFileInfo(const QFileInfo &info) {
		list.append(info);
	}
};

/*!
 * Returns the complete directory tree including all sub-directories.
 * @param dirName  the root directory of the tree
 * @return a flat list of FileInfo objects representing the entries of the tree.
 */
QFileInfoList listTree(const QString &dirName) {
	ListTreeCallback registerFileInfo;
	walkDir(dirName, &registerFileInfo);
	return registerFileInfo.list;
}

///*!
// * Returns true, if path seems to be a photo album directory (i.e.
// * must not contain any alien files)
// * @param path the path to a directory
// * @return true, if path seems to contain a photo album, false otherwise.
// */
//bool isAlbumDirectory(const QString &path) {
//	(void) path;
//	return false;
//}
//
///*!
// * Deletes a complete album directory (but only if it really seems to be
// * an album directory).
// * @param path the path to a directory
// * @return true, if deletion was successful, false otherwise.
// */
//bool delAlbumTree(const QString &path) {
//	(void) path;
//	return false;
//}

/*!
 * Does the same as QDir::cleanPath(), only that it correctly interprets
 * a leading "~" symbol as the home path.
 * @param path
 * @return
 */
QString cleanPath(const QString &path) {
	if (path.at(0) == '~') {
		return QDir::cleanPath(QDir::homePath()+path.mid(1));
	} else {
		return QDir::cleanPath(path);
	}
}


QString &replace_problematic_letters(QString &s)
{
	s.replace("ä", "ae", Qt::CaseSensitive);
	s.replace("ö", "oe", Qt::CaseSensitive);
	s.replace("ü", "ue", Qt::CaseSensitive);
	s.replace("ß", "ss", Qt::CaseSensitive);
	s.replace("Ä", "Ae", Qt::CaseSensitive);
	s.replace("Ö", "Oe", Qt::CaseSensitive);
	s.replace("Ü", "Ue", Qt::CaseSensitive);
	return s;
}

/*!
 * Derives a file name from a given string. Ideally just copies the string,
 * but if the String is longer than 32 characters than it is capped, and also
 * any illegal characters are replaced by '_'.
 */
QString deriveFileName(const QString &str) {
	QChar array[64];
	QString s = str.left(32);
	// replace_problematic_letters(s);
	int N = s.length();
	for (int i = 0; i < N; i++) {
		if (s[i].isLetterOrNumber() || s[i] == '-') {
			array[i] = s[i];
		} else {
			array[i] = '_';
		}
	}

	return QString(array, N);
}


/*!
 * Returns the version number of GWTPhotoAlbum as a text string
 */
QString versionNumber() {
	QFile file(":/data/VERSION.txt");
	if (file.open(QIODevice::ReadOnly|QIODevice::Text)) {
		QTextStream stream(&file);
		return stream.readLine();
	} else {
		return QString("unknown version");
	}
}
