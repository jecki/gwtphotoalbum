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


#ifndef TEST_JSONSUPPORT_H_
#define TEST_JSONSUPPORT_H_

#include <QObject>
#include <QVariant>

class test_jsonsupport: public QObject {
	Q_OBJECT
private:
	QVariant	tree;
	QString     treeOutput;
	QString		roundTripString;

private slots:
	void initTestCase();

	void parseMultiLineString();
	void parseString();
	void parseStringList();
	void parseStringMap();

	void writeJSON();

	void roundTrip();
};

#endif /* TEST_JSONSUPPORT_H_ */
