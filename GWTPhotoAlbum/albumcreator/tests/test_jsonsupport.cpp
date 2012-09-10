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


#include "jsonsupport.h"
#include "test_jsonsupport.h"

#include <QtTest/QtTest>
#include <QStringList>
#include <QMap>
#include <QList>

#ifndef NDEBUG
#include <iostream>
#endif


void test_jsonsupport::initTestCase() {
	QList<QVariant> sizesList;
	QMap<QString, QVariant> resolutions;
	QMap<QString, QVariant> names;
	QList<QVariant> resolutionsDict;

	sizesList.append(QSize(400, 300));
	sizesList.append(QSize(640, 480));
	sizesList.append(QSize(1024, 768));

	resolutions["res0"] = sizesList;
	names["Bild1"] = "res0";
	names["Bild2"] = "res0";
	names["Bild3"] = "res1";

	resolutionsDict.append(resolutions);
	resolutionsDict.append(names);

	tree = QVariant(resolutionsDict);

	treeOutput = "[{ \"res0\": [[400, 300], [640, 480], [1024, 768]] },\n"
                 "{ \"Bild1\": \"res0\",\n"
                 "\"Bild2\": \"res0\",\n"
                 "\"Bild3\": \"res1\" }]";

	roundTripString =
	"{"
	  "\"bottom line\": \"<a href=\\\"http://www.eckhartarnold.de\\\">www.eckhartarnold.de</a><br />\",\n"
	  "\"subtitle\": \"Pictures from Bayreuth and abroad\",\n"
	  "\"title\": \"GWTPhotoAlbum Demonstration\",\n"
	  "\"image clickable\": \"true\",\n"
	  "\"disable scrolling\": \"true\",\n"
	  "\"layout type\":\"tiled\",\n"
	  "\"layout data\":\"IOF\",\n"
	  "\"presentation type\":\"gallery\"\n"
	"}";

}


void test_jsonsupport::parseMultiLineString() {
	QString multiline("\"\"\"line1\nline2\"\"\"");
	bool ok = true;
	int  index = 0;
	QVERIFY(ok);
	QVERIFY(::parseMultiLineString(multiline, index, multiline.size()-1, ok) == multiline.mid(3, multiline.size()-6));
}


void test_jsonsupport::parseString() {
	QString testJSON1("123'test\"string'321");
	QString testJSON2("123\"test'string\"321");
	QString noDelimiters("noDelimiters!");
	QString quotationMarks("\"<a href=\\\"www.xyz.de\\\">link</a>\"");

	int index;
	bool ok = true;

	index = 3;
	QVERIFY(::parseString(testJSON1, index, testJSON1.length(), ok)
			== QString("test\"string"));
	QVERIFY(index == testJSON1.length()-3);
	QVERIFY(ok);

	index = 3;
	QVERIFY(::parseString(testJSON2, index, testJSON2.length(), ok)
			== QString("test'string"));
	QVERIFY(index == testJSON2.length()-3);
	QVERIFY(ok);

	index = 0;
	::parseString(noDelimiters, index, noDelimiters.length(), ok);
	QVERIFY(ok == false);

	index = 0; ok = true;
	QVariant ret = ::parseString(quotationMarks, index, quotationMarks.length(), ok);
	//std::cout << ret.toString().length() << ", " << quotationMarks.length() << std::endl;
	//std::cout << ret.toString().toStdString() << std::endl << std::endl;
	QVERIFY(ok);
	QVERIFY(index == quotationMarks.length());
}


void test_jsonsupport::parseStringList() {
	QString str("[ \"120x120\", \"640x480\", \"1024x768\", \"1600x1200\", \"original_size\" ]");
	int index = 0;
	bool ok = true;
	QStringList strList = ::parseStringList(str, index, str.length(), ok);
	QVERIFY(ok);
	QVERIFY(index == str.length());

	QVERIFY(strList[0] == "120x120");
	QVERIFY(strList[1] == "640x480");
	QVERIFY(strList[2] == "1024x768");
	QVERIFY(strList[3] == "1600x1200");
	QVERIFY(strList[4] == "original_size");
}


void test_jsonsupport::parseStringMap() {
	QString str("{ \"title\" : \"Neue Gallerie\", \"subtitle\" : \"Winterbilder\" }");
	int index = 0;
	bool ok = true;

	QMap<QString, QString> strMap = ::parseStringMap(str, index, str.length(), ok);
	QVERIFY(ok);
	QVERIFY(index == str.length());

	QVERIFY(strMap["title"] == "Neue Gallerie");
	QVERIFY(strMap["subtitle"] == "Winterbilder");
}


void test_jsonsupport::writeJSON() {
	QString str;
	QTextStream stream(&str);
	bool ok = true;

	::writeJSON(stream, tree, ok);
	//std::cout << str.toStdString() << std::endl << treeOutput.toStdString() << std::endl;
	QVERIFY(ok);
	QVERIFY(str == treeOutput);

	::writeJSON(stream, QStringList(), ok);
	QVERIFY(ok);

	::writeJSON(stream, QList<QVariant>(), ok);
	QVERIFY(ok);

	::writeJSON(stream, QMap<QString, QVariant>(), ok);
	QVERIFY(ok);
}



inline QVariant testjs_convert(QMap<QString, QString> map) {
	QVariantMap data;
	foreach(QString key, map.keys()) {
		data[key] = QVariant(map[key]);
	}
	return QVariant(data);
}

void test_jsonsupport::roundTrip() {
	QMap<QString, QString> map;
	QVariant roundTripData;
	QString station1, station2;
	QTextStream stream1(&station1), stream2(&station2);
	int index;
	bool ok = true;

	// std::cout << roundTripString.toStdString() << std::endl;

	index = 0;
	roundTripData = testjs_convert(::parseStringMap(roundTripString, index, roundTripString.length(), ok) );
	QVERIFY(ok);
	// std::cout << roundTripString.length() << " " << index << std::endl;
	QVERIFY(index == roundTripString.length());
	::writeJSON(stream1, roundTripData, ok);
	QVERIFY(ok);

	// std::cout << station1.length() << " : " << station1.toStdString() << std::endl;

	index = 0;
	roundTripData = testjs_convert(::parseStringMap(station1, index, station1.length(), ok) );
	QVERIFY(ok);
	QVERIFY(index == station1.length());
	::writeJSON(stream2, roundTripData, ok);
	QVERIFY(ok);

	QVERIFY(station1 == station2);
}


//QTEST_MAIN(test_jsonsupport)
//#include "../debug/test_jsonsupport.moc"

