/*
 * test_imageitem.cpp
 *
 *  Created on: 03.02.2011
 *      Author: eckhart
 */

#include "test_imageitem.h"
#include "toolbox.h"

#include <QtTest/QtTest>
#include <QStringList>
#include <QString>
#include <QList>
#include <QImage>
#include <QSize>

#ifndef NDEBUG
#include <iostream>
#endif


void test_imageitem::initTestCase() {
	QString path("../scripts/example_pictures/01_Bayreuth.jpg");
	it = new ImageItem(path);
}


void test_imageitem::destFileName() {
	QVERIFY(it->destFileName() == "01_Bayreuth.jpg");
}

void test_imageitem::image() {
	QSize  previewSize = it->size(ImageItem::PREVIEW);
	QSize  thumbnailSize = it->size(ImageItem::THUMBNAIL);
	QImage image = it->image();
	QImage preview = it->image(ImageItem::PREVIEW);
	QImage thumbnail = it->image(ImageItem::THUMBNAIL);

	QVERIFY(it->noError());

	QVERIFY(sizeFits(preview.size(), previewSize));
	QVERIFY(sizeFits(thumbnail.size(), thumbnailSize));
}

void test_imageitem::caption() {
	QString capstr("Caption Test");
	it->setCaption(capstr);
	QVERIFY(it->caption() == capstr);
}

void test_imageitem::resized() {
	QList<QImage> il = it->resized(QList<QSize> ());
	QVERIFY(il.size() == 1);
	QVERIFY(il[0].width() > 1600 && il[0].height() > 1200);

	QList<QSize> sl;
	sl.append(QSize(120, 120));
	sl.append(QSize(400, 300));
	sl.append(QSize(800, 600));
	sl.append(QSize(1280, 960));
	sl.append(QSize(1600, 1200));

	il = it->resized(sl);
	QVERIFY(sizeFits(il[0].size(), QSize(120, 120)));
	QVERIFY(sizeFits(il[1].size(), QSize(400, 300)));
	QVERIFY(sizeFits(il[2].size(), QSize(800, 600)));
	QVERIFY(sizeFits(il[3].size(), QSize(1280, 960)));
	QVERIFY(sizeFits(il[4].size(), QSize(1600, 1200)));

	sl.clear();
	sl.append(QSize());
	il = it->resized(sl);
	QVERIFY(il[0].width() > 1600 && il[0].height() > 1200);
}


void test_imageitem::errorHandling()  {
	QList<QSize> sl;
	sl.append(QSize(120, 120));
	sl.append(QSize(640, 480));
	sl.append(QSize(1280, 800));
	sl.append(QSize(1600, 1200));

	ImageItem it2("../scripts/example_pictures/captions.json");
	QVERIFY(!it2.isValid());
	QVERIFY(!it2.noError());
	QVERIFY(!it2.getError().isEmpty());

	QVERIFY(it2.image(it2.THUMBNAIL).isNull());
	QVERIFY(it2.image(it2.PREVIEW).isNull());
	QVERIFY(it2.image(it2.IMAGE).isNull());

	QVERIFY(it2.resized(sl).isEmpty());

	ImageItem it1("Non existent!");
	QVERIFY(!it1.isValid());
	QVERIFY(!it1.noError());
	QVERIFY(!it1.getError().isEmpty());

	QVERIFY(it1.image(it1.THUMBNAIL).isNull());
	QVERIFY(it1.image(it1.PREVIEW).isNull());
	QVERIFY(it1.image(it1.IMAGE).isNull());

	QVERIFY(it1.resized(sl).isEmpty());
}


void test_imageitem::cleanupTestCase() {
	delete it;
}
