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


#include "iconcache.h"
#include "toolbox.h"
//#include "filterproxy_class.h"

#include <QPainter>
#include <QDir>

#ifndef NDEBUG
#include <QDebug>
#endif

/*
 * for debugging: make sure that "active" cache is emptied, after loading has finished...
 */



IconCache::IconCache(QObject *parent)
    : QObject(parent)
{
	iconSize = QSize(100,75);
	pendingCounter = 0;
	dirChanged(QDir::homePath());
}


IconCache::~IconCache()
{
	foreach(QString key, active.keys()) {
		QPixmap *pixmap = active.take(key);
		delete pixmap;
	}

}


bool IconCache::get(const QString filePath, QIcon &icon) {
	return (get(filePath, icon, iconSize));
}


// returns only the path of a fileName
inline QString pathOf(const QString &fileName) {
	QString path = QDir::cleanPath(QDir::fromNativeSeparators(fileName));
	int i = path.lastIndexOf("/");
	if (i < 0) i = 0;
    path.truncate(i);
    return (path);
}


bool IconCache::get(const QString filePath, QIcon &icon, const QSize size) {
	if (pathOf(filePath) != currentDir) return (false);
	if (active.contains(filePath)) {
		icon = QIcon(sizeGuard(*active.value(filePath), size));
		return (true);
	}
	QImage img;
	bool ret = (ImageIO::instance()).quickRequest(this, filePath, img, size);
	if (!ret) {
		pendingCounter++;
	} else {

#ifndef NDEBUG
		if (active.contains(filePath)) {
			qDebug() << "IconCache: active pixmap list confusion; already contains: " << filePath;
		}
#endif
		QPixmap *pixmap = new QPixmap();
		*pixmap = sizeGuard(QPixmap::fromImage(img), size);
		active.insert(filePath, pixmap);
		icon = QIcon(*pixmap);
	}
	return (ret);
}



bool IconCache::readNextErrorMsg(QString &filePath, QString &errorMsg) {
	if (!errorMessages.empty()) {
		QStringList error = errorMessages.takeFirst();
		filePath = error.first();
		errorMsg = error.last();
		return (true);
	} else return (false);
}



void IconCache::setIconSize(const QSize size) {
	iconSize = size;
}


//QPixmap IconCache::sizeGuard(const QPixmap pixmap, const QSize size, const int frame) {
//	QSize pmsize = pixmap.size();
//	if (size != pmsize) {
////	if ( (size.height() == pmsize.height() && size.width() >= pmsize.width()) ||
////		 (size.width() == pmsize.width() && size.height() >= pmsize.height()) ) {
//		QPixmap pm = pixmap.scaled(size, Qt::KeepAspectRatio,
//                                         Qt::FastTransformation);
//		if (size.width() * pmsize.height() ==
//			pmsize.width() * size.height()) {
//			return (pm);
//
//		} else {
//			QPixmap dest(QSize(size.width() + 2 * frame, size.height() + 2 * frame));
//			dest.fill();
//			QPainter painter(&dest);
//
//			painter.drawPixmap((size.width() - pm.width())/2 + frame,
//					           (size.height() - pm.height())/2 + frame, pm);
//			return (dest);
//		}
//	} else {
//		return (pixmap);
//	}
//}


void IconCache::receiveImage(const QString filePath, QImage image) {
	QPixmap *pixmap = new QPixmap();
	*pixmap = sizeGuard(QPixmap::fromImage(image), iconSize);
	active.insert(filePath, pixmap);
	pendingCounter--;
	if (pendingCounter <= 0) {

#ifndef NDEBUG
		if (pendingCounter < 0) {
			qDebug() << "IconCache: pending image counter confused, should't become smaller than zero!";
		}
#endif
		emit finishedLoading();
	} else {
		emit iconReady(filePath, QIcon(sizeGuard(*pixmap, iconSize)));
	}
}


void IconCache::receiveErrorMsg(const QString filePath, const QString errorMsg) {
	QStringList error = QStringList();
	error << filePath << errorMsg;
#ifndef NDEBUG
	qDebug() << filePath << " : " << errorMsg;
#endif
	errorMessages.append(error);
	pendingCounter--;
	if (pendingCounter <= 0) {

#ifndef NDEBUG
		if (pendingCounter < 0) {
			qDebug() << "IconCache: pending image counter confused, should't become smaller than zero!";
		}
#endif

		emit finishedLoading();
	}
}


/* void IconCache::run() {
	exec();
}*/


void IconCache::dirChanged(const QString &path) {
	QString newDir = QDir::cleanPath(QDir::fromNativeSeparators(path));
	if (newDir.endsWith("/")) newDir.chop(1);
	if (newDir != currentDir) {
		currentDir = newDir;
		foreach(QPixmap *pm, active) {
			delete pm;
		}
		active.clear();
	}
}


