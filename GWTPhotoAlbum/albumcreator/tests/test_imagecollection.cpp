/*
 * test_imagecollection.cpp
 *
 *  Created on: 08.02.2011
 *      Author: eckhart
 */

#include "test_imagecollection.h"

#include <Qt>
#include <QtTest/QtTest>
#include <QDir>
#include <QFileInfo>
#include <QFileInfoList>
#include <QString>
#include <QStringList>
#include <QMap>
#if QT_VERSION >= 0x050000
#include <QtConcurrent/QtConcurrentRun>
#else
#include <QtCore/QtConcurrentRun>
#endif

#include "toolbox.h"
#include "jsonsupport.h"

#include <iostream>
using namespace std;


bool lessThanImageItem(const ImageItem *it1, const ImageItem *it2) {
	return (it1->destFileName() < it2->destFileName());
}

struct RemoveFile: IWalkDirCallback {
	virtual void processFileInfo(const QFileInfo &fi) {
		QDir dir(fi.dir());

		if (fi.isDir()) {
			QVERIFY(dir.rmdir(fi.fileName()));
		} else {
			QVERIFY(dir.remove(fi.fileName()));
		}
	}
};


/*!
 * Deletes a complete directory tree within the temporary directory.
 */
void delTempTree(QString tempDir) {
	RemoveFile remove;
	QString tempPath = QDir::tempPath()+"/"+tempDir;
	QDir spoils(tempPath);

	if (spoils.exists()) {
		walkDir(tempPath, &remove);
		QDir(QDir::tempPath()).rmdir(tempDir);
	}
}


void test_imagecollection::initTestCase() {
	tempDir  = QString("test_album_please_delete");
	tempPath = QDir::tempPath() + "/" + tempDir;

	ic = new ImageCollection();
	QDir dir("../scripts/example_pictures/");
	// where is the current dir???
	QFileInfoList files = dir.entryInfoList();
	numImages = 0;
	foreach(QFileInfo info, files) {
		if (!info.isDir()) {
			QString path = info.filePath();
			if (QDir::match("*.jpg", path)) {
				ImageItem *it = new ImageItem(path);
				ic->imageList << it;
				if (it->isValid()) numImages++;
			}
		}
	}
	qSort(ic->imageList.begin(), ic->imageList.end(), lessThanImageItem);

#ifndef NDEBUG
//	foreach(ImageItem *it, ic->imageList) {
//		cout << it->destFileName().toStdString() << endl;
//	}
#endif
    QList<QSize> sizesList;
	sizesList << ImageItem::Thumbnail_Size;
	sizesList << QSize(640, 480);
	sizesList << QSize(1024, 768);
	sizesList << QSize(1600, 1200);
	sizesList << ImageCollection::Original_Size;
	ic->setSizes(sizesList);

}


void test_imagecollection::statics() {
	QVERIFY(ImageCollection::Original_Size.width() < 0);
	QVERIFY(ImageCollection::Original_Size.height() < 0);
}

void test_imagecollection::search() {
	int index = 0;
	ImageItem *it = ic->search(QString("Rauchbier"), index);
	QVERIFY(it != NULL);
	QVERIFY(it->destFileName().indexOf("Rauchbier") >= 0);
	index = 0;

	it = ic->search(QString("Karnisch"), index);
	QVERIFY(it != NULL);
	QVERIFY(it->destFileName().indexOf("Karnisch") >= 0);
	int save = index;
	it = ic->search(QString("Karnisch"), index);
	QVERIFY(it != NULL);
	QVERIFY(it->destFileName().indexOf("Karnisch") >= 0);
	QVERIFY(save != index);
}


void test_imagecollection::find() {
	QStringList error;
	bool    	ok = true;
	int 		index = 0;
	QString 	captionsJSON = readTextFile("tests/test_imagecollection_captions.json", error, ok);

	QVERIFY(ok);

	QMap<QString, QString> captions = parseStringMap(captionsJSON, index, captionsJSON.size(), ok);
	QVERIFY(ok);

	foreach(QString key, captions.keys()) {
		ImageItem *it = ic->find(key);
		QVERIFY(it != NULL);
		it->setCaption(captions[key]);
	}
}


void test_imagecollection::fromJSON() {
	QStringList error;
	bool    	ok = true;
	QString 	captionsJSON = readTextFile("tests/test_imagecollection_info.json", error, ok);

	QVERIFY(ok);

	ic->fromJSON(captionsJSON);
	QVERIFY(ic->bottom_line == "<a href=\"http://www.eckhartarnold.de\">www.eckhartarnold.de</a><br />");
	QVERIFY(ic->subtitle == "Pictures from Bayreuth and abroad");
	QVERIFY(ic->title == "GWTPhotoAlbum Demonstration");
	QVERIFY(ic->disable_scrolling == true);
	QVERIFY(ic->layout_type == "tiled");
	QVERIFY(ic->layout_data == "IOF");
	QVERIFY(ic->presentation_type == "gallery");
}


void test_imagecollection::infoJSON() {
	QString info = ic->infoJSON();
	ic->fromJSON(info);
	QString info2 = ic->infoJSON();
	QVERIFY(info == info2);
}


void test_imagecollection::directoriesJSON() {
	int  index = 0;
	bool ok = true;
	QString str = ic->directoriesJSON();
	QStringList dirs = parseStringList(str, index, str.size(), ok);

	QVERIFY(ok);
	QVERIFY(ic->sizes().size() == dirs.size());

	foreach(QSize s, ic->sizes()) {
		str = sizeStr(s);
		QVERIFY(dirs.indexOf(str) >= 0);
	}
}


void test_imagecollection::filenamesJSON() {
	int  index = 0;
	bool ok = true;
	QString str = ic->filenamesJSON();
	QStringList filenames = parseStringList(str, index, str.size(), ok);

	QVERIFY(ok);
	int N  = 0;
	foreach(ImageItem *it, ic->imageList) {
		if (it->isValid()) {
			N++;
		}
	}
	QVERIFY(N == filenames.size());

	foreach(ImageItem *it, ic->imageList) {
		if (it->isValid()) {
			str = it->destFileName();
			QVERIFY(filenames.indexOf(str) >= 0);
		}
	}
}


void test_imagecollection::captionsJSON() {
	int  index = 0;
	bool ok = true;
	QString str = ic->captionsJSON();
	QMap<QString, QString> captions = parseStringMap(str, index, str.size(), ok);

	foreach(ImageItem *it, ic->imageList) {
		QString key = it->destFileName();
		QVERIFY(captions[key] == it->caption());
	}
}



inline bool findNum(const QString text, int number) {
	return (text.indexOf(QString::number(number)) >= 0);
}

void test_imagecollection::resolutionsJSON() {
	QString str = ic->resolutionsJSON();

	// std::cout << str.toStdString() << std::endl;
	foreach(ImageItem *it, ic->imageList) {
		if (it->isValid()) {
			QVERIFY(str.indexOf(it->destFileName()) >= 0);
			QSize imgSize = it->size();
			foreach(const QSize &sz, ic->sizes()) {
				QSize s = adjustSize(imgSize, sz);
				// std::cout << s.width() << std::endl;
				QVERIFY(findNum(str, s.width()));
				QVERIFY(findNum(str, s.height()));
			}
		}
	}
}


Progress::Progress(ImageCollection *ic) : QObject(NULL) {
	this->ic = ic;
	reset();
	connect(ic, SIGNAL(progress(ImageCollection *, int)),
			this, SLOT(progress(ImageCollection *, int)));
	connect(ic, SIGNAL(finished(bool, const QString &)),
			this, SLOT(finished(bool, const QString &)));
}

void Progress::reset() {
	last = -1;
	finishSignal = false;
	completed = false;
	errorMsg = QString();
}

void Progress::progress(ImageCollection *source, int nr) {
	int all = source->imageList.length();
	QVERIFY(nr >= 0);
	QVERIFY(nr <= all);
	QVERIFY(nr > last);
	last = nr;
}

void Progress::finished(bool completed, const QString &errorMsg) {
	finishSignal = true;
	this->completed = completed;
	this->errorMsg = errorMsg;
}

void test_imagecollection::createAlbum() {
	Progress 		callback(ic);
	QString 		error;
	ImageItem 		*badItem;
	ImageCollection test_collection;
	Progress		cb2(&test_collection);

	// test: do not overwrite an existing directory
	QDir parent(QDir::tempPath());
	parent.mkdir("test");
	QVERIFY(!ic->createAlbum(parent.path()+"/test"));
	error = ic->errorString();
	QVERIFY(parent.exists("test")); // existing dirs must not be deleted by 'createAlbum'
	parent.rmdir("test");
	QVERIFY(error.contains("already exists"));
	QVERIFY(callback.finishSignal);
	QVERIFY(!callback.completed);
	QVERIFY(callback.errorMsg.contains("already exists"));
	callback.reset();

	badItem = new ImageItem("../scripts/example_pictures/captions.json");
	test_collection.imageList << badItem;
	QVERIFY(test_collection.createAlbum(tempPath)); // non-fatal mistake
	QVERIFY(!test_collection.errorString().isEmpty()); // error string should not be empty
	QVERIFY(cb2.finishSignal);
	QVERIFY(cb2.completed);
	QVERIFY(!cb2.errorMsg.isEmpty());
	test_collection.imageList.removeFirst();
	delete badItem;
	delTempTree(tempDir);
	cb2.reset();

	badItem = new ImageItem("Non_Existing_File");
	test_collection.imageList << badItem;
	QVERIFY(test_collection.createAlbum(tempPath));
	error = test_collection.errorString();
	QVERIFY(cb2.finishSignal);
	QVERIFY(cb2.completed);
	test_collection.imageList.removeFirst();
	delete badItem;
	delTempTree(tempDir);
	cb2.reset();

	QFuture<bool> future = QtConcurrent::run(ic, &ImageCollection::createAlbum, tempPath);
	while (!ic->isRunning()) ;
	ic->stopAlbumCreation();
	QVERIFY(!future.result());
	QCoreApplication::processEvents();
	error = ic->errorString();
	QVERIFY(error == "");
	QVERIFY(callback.finishSignal);
	QVERIFY(!callback.completed);
	QVERIFY(callback.errorMsg.isEmpty());
	delTempTree(tempDir);
	callback.reset();

	ic->archiveSize = QSize(3000, 2250);
	ic->archiveName = "archive.zip";
	QVERIFY(ic->createAlbum(tempPath));
	error = ic->errorString();
	if (error != "") {
		cout << "test_imagecollection::createAlbum " << error.toStdString() << endl;
	}
	QVERIFY(error == "");
	QVERIFY(callback.last == ic->imageList.length());
	QVERIFY(callback.finishSignal);
	QVERIFY(callback.completed);
	QVERIFY(callback.errorMsg.isEmpty());
	callback.reset();

	QDir dir(tempPath);
	QStringList entries = dir.entryList();

	QVERIFY(entries.indexOf("index.html") >= 0);
	// QVERIFY(entries.indexOf("index_offline.html") >= 0);
	QVERIFY(entries.indexOf("GWTPhotoAlbum_xs.html") >= 0);
	QVERIFY(entries.indexOf("GWTPhotoAlbum_fatxs.html") >= 0);
	for (int i = 1; i <= numImages; i++) {
		QVERIFY( entries.indexOf(QString("noscript_image")+QString::number(i)+QString(".html")) >= 0 );
	}
	QVERIFY(entries.indexOf("archive.zip") >= 0);

	dir.setPath(tempPath + "/slides");
	entries = dir.entryList();
	QVERIFY(entries.indexOf("info.json") >= 0);
	QVERIFY(entries.indexOf("directories.json") >= 0);
	QVERIFY(entries.indexOf("filenames.json") >= 0);
	QVERIFY(entries.indexOf("resolutions.json") >= 0);
	QVERIFY(entries.indexOf("captions.json") >= 0);
	delTempTree(tempDir);

	QList<ImageItem *> iml(ic->imageList);
	while (ic->imageList.length() > 3) {
		ic->imageList.removeLast();
	}

	ic->archiveSize = QSize(2048, 1200);
	ic->archiveName = "archive.zip";
	QVERIFY(ic->createAlbum(tempPath));
	error = ic->errorString();
	QVERIFY(error == "");
	QVERIFY(callback.last == ic->imageList.length());
	QVERIFY(callback.finishSignal);
	QVERIFY(callback.completed);
	QVERIFY(callback.errorMsg.isEmpty());
	dir = QDir(tempPath);
	entries = dir.entryList();
	QVERIFY(entries.indexOf("archive.zip") >= 0);
	callback.reset();
	delTempTree(tempDir);

	QList<QSize> sl = ic->sizes();
	sl.removeOne(ic->Original_Size);
	ic->setSizes(sl);
	ic->archiveSize = ic->Original_Size;
	ic->archiveName = "archive.zip";
	QVERIFY(ic->createAlbum(tempPath));
	error = ic->errorString();
	QVERIFY(error == "");
	QVERIFY(callback.last == ic->imageList.length());
	QVERIFY(callback.finishSignal);
	QVERIFY(callback.completed);
	QVERIFY(callback.errorMsg.isEmpty());
	dir = QDir(tempPath);
	entries = dir.entryList();
	QVERIFY(entries.indexOf("archive.zip") >= 0);
	callback.reset();
	delTempTree(tempDir);

	ic->archiveSize = ic->sizes()[2];
	ic->archiveName = "archive.zip";
	QVERIFY(ic->createAlbum(tempPath));
	error = ic->errorString();
	QVERIFY(error == "");
	QVERIFY(callback.last == ic->imageList.length());
	QVERIFY(callback.finishSignal);
	QVERIFY(callback.completed);
	QVERIFY(callback.errorMsg.isEmpty());
	dir = QDir(tempPath);
	entries = dir.entryList();
	QVERIFY(entries.indexOf("archive.zip") >= 0);
	callback.reset();
}


void test_imagecollection::rollback() {
	QVERIFY(ic->rollback());
}


void test_imagecollection::miscTests() {
	//QVERIFY(!ic->archiveName.isNull());
	//QVERIFY(ic->archiveSize.width() > 0 && ic->archiveSize.height() > 0);
	QVERIFY(ic->compression >= 50 && ic->compression <= 98);
}


void test_imagecollection::cleanupTestCase() {
	delTempTree(tempDir);
	delete ic;
}
