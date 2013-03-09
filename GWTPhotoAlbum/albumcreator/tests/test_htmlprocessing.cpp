/*
 * test_htmlprocessing.cpp
 *
 *  Created on: 27.01.2011
 *      Author: eckhart
 */

#include "test_htmlprocessing.h"
#include "htmlprocessing.h"
#include "imageitem.h"
#include "toolbox.h"

#include <QtTest/QtTest>
#include <QString>
#include <QFileInfo>
#include <QMap>
#include <QTextStream>

#ifndef NDEBUG
#include <iostream>
#endif


QMap<QString, QString> extractDeploymentFiles() {
	bool ok = true;
	QMap<QString, QString> map;

//#ifndef NDEBUG
//	QFileInfoList tree = listTree(":/");
//	foreach(QFileInfo item, tree) {
//		std::cout << item.absoluteFilePath().toStdString() << std::endl;
//	}
//#endif

	QFile archive(":/data/GWTPhotoAlbum-Deploy.qa");
	archive.open(QIODevice::ReadOnly);
	QByteArray data = archive.readAll(); // qUncompress(archive.readAll());
	archive.close();

	ok = archive.error() == QFile::NoError;

#ifndef NDEBUG
	std::cout << archive.error() << std::endl;
#endif

	const char *dPtr = data.constData();
	int i = 0, end = data.size();

	while (i < end && ok) {
		int N = data.mid(i, 10).toInt(&ok, 16);
		i += 10;
		if (ok) {
			QString path(data.mid(i, N));
			i += N;
			if (ok) {
				int L = data.mid(i, 10).toInt(&ok, 16);
				i += 10;
				if (ok) {
					map[path] = QString(QByteArray(dPtr + i, L));
					i += L;
				}
			}
		}
	}
	return map;
}


bool contains_still_templates(const QString &str)
{
	int i = str.indexOf("$");
	if (i >= 0 && i < str.length()-1) {
		char ch = str[i+1].toLatin1();
		if (ch >= 'A' && ch <= 'Z') {
			return true;
		}
	}
	return false;
}


void test_htmlprocessing::initTestCase() {
	QMap<QString, QString> files = extractDeploymentFiles();
	startPage = files["GWTPhotoAlbum_xs.html"];
	QVERIFY(!startPage.isEmpty());
	try {
		hp = new HTMLProcessing(); // implicitly tests "readTemplates"
	} catch (QString &error) {
		QVERIFY(false);
	}
}


void test_htmlprocessing::readTemplates() {
	try {
		// testing just the constructor which incidently calls readChunks
		QString schrott("test: HTMLProcessing::readTemplates failure!");
		QTextStream stream1(&schrott);
		QTextStream stream2(&schrott);

		QVERIFY(!hp->readTemplates(stream1));

		HTMLProcessing test_hp(stream2);

	} catch (QString &error) {
		QVERIFY(true);
	}
}


void test_htmlprocessing::indexPage() {
	QString index = hp->indexPage();
}


void test_htmlprocessing::offlineIndex() {
	QString offline_index = hp->offlineIndex();
}


void test_htmlprocessing::fatStartPage() {
	QString osp = hp->fatStartPage(startPage,
			"INFO", "DIRECTORIES", "FILENAMES", "CAPTIONS",
			"RESOLUTIONS");
	QVERIFY(osp.indexOf("INFO") >= 0);
	QVERIFY(osp.indexOf("script id=\"info.json\"") >= 0);
	QVERIFY(osp.indexOf("DIRECTORIES") >= 0);
	QVERIFY(osp.indexOf("\"directories.json\"") >= 0);
	QVERIFY(osp.indexOf("FILENAMES") >= 0);
	QVERIFY(osp.indexOf("\"filenames.json\"") >= 0);
	QVERIFY(osp.indexOf("CAPTIONS") >= 0);
	QVERIFY(osp.indexOf("\"captions.json\"") >= 0);
	QVERIFY(osp.indexOf("RESOLUTIONS") >= 0);
	QVERIFY(osp.indexOf("\"resolutions.json\"") >= 0);
	// QVERIFY(osp.indexOf("$DOCTYPE") < 0);
	QVERIFY(!contains_still_templates(osp));
}


void test_htmlprocessing::noscriptGallery() {
	QList<QString> sl;
	sl << "IMG1" << "IMG2" << "IMG3" << "IMG4" << "IMG5" << "IMG6" << "IMG7";
	QString gallery = hp->noscriptGallery("test_title", "test_subtitle",
			"test_bottomline", sl, ImageItem::Thumbnail_Size);
	QVERIFY(gallery.indexOf("test_title") >= 0);
	QVERIFY(gallery.indexOf("test_subtitle") >= 0);
	QVERIFY(gallery.indexOf("test_bottomline") >= 0);
	// QVERIFY(gallery.indexOf("$DOCTYPE") < 0);
	QVERIFY(!contains_still_templates(gallery));
	foreach(QString s, sl) {
		QVERIFY(gallery.indexOf(s) >= 0);
	}
}


void test_htmlprocessing::noscriptImagePage() {
	QString ip = hp->noscriptImagePage(5,
			"test_sizeDir",	"test_fileName","test_caption");
	QVERIFY(ip.indexOf("test_sizeDir") >= 0);
	QVERIFY(ip.indexOf("test_fileName") >= 0);
	QVERIFY(ip.indexOf("test_caption") >= 0);
	// QVERIFY(ip.indexOf("$DOCTYPE") < 0);
	QVERIFY(!contains_still_templates(ip));
}


void test_htmlprocessing::cleanupTestCase() {
	delete hp;
}

