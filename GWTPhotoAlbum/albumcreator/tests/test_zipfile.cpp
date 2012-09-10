#include "test_zipfile.h"

#include <QtTest/QtTest>
#include <QDir>
#include <QImage>
#include <QByteArray>
#include <QBuffer>
#include <QProcess>
#include <QStringList>

#include <iostream>


void test_zipfile::initTestCase()
{
	tempPath = QDir::tempPath();
}


void test_zipfile::addData()
{
	QString zipFileName("test_zipfile.zip");
	QString zipFilePath(tempPath + "/" + zipFileName);
	ZipFile zipFile(zipFilePath);

	QFileInfo info1("../scripts/example_pictures/01_Bayreuth.jpg");
	QFileInfo info2("../scripts/example_pictures/07_Tee.jpg");

	QImage img1(info1.filePath());
	QImage img2(info2.filePath());

	QByteArray data1, data2;
	QBuffer	   buffer1(&data1), buffer2(&data2);

	buffer1.open(QIODevice::WriteOnly);
	img1.save(&buffer1, "jpg");
	buffer1.close();

	buffer2.open(QIODevice::WriteOnly);
	img2.save(&buffer2, "jpg");
	buffer2.close();

	zipFile.open();
	zipFile.addData("01_Bayreuth.jpg", data1, info1.created());
	zipFile.addData("07_Tee.jpg", data2);
	zipFile.close();

//	QProcess unzip(this);
//	QStringList arguments;
//	arguments << zipFilePath << " -d "+tempPath;
//	unzip.start("unzip", arguments);
//	foreach(QString s, arguments) {
//		std::cout << s.toStdString() << std::endl;
//	}
//	std::cout << unzip.error() << std::endl;
//	QVERIFY(unzip.waitForFinished());
	QProcess::execute("unzip "+zipFilePath+" -d "+tempPath);

	QFileInfo cmp1(tempPath + "/" + "01_Bayreuth.jpg");
	QFileInfo cmp2(tempPath + "/" + "07_Tee.jpg");

	QVERIFY(cmp1.exists());
	QVERIFY(cmp2.exists());

	QImage cmpimg1(cmp1.filePath());
	QImage cmpimg2(cmp2.filePath());

	QVERIFY(!cmpimg1.isNull());
	QVERIFY(!cmpimg2.isNull());
}


void test_zipfile::cleanupTestCase()
{
	QDir temp = QDir::temp();

	temp.remove("test_zipfile.zip");
	temp.remove("01_Bayreuth.jpg");
	temp.remove("07_Tee.jpg");
}
