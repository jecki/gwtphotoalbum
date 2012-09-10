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

#ifndef TEST_FILEJOURNAL_H_
#define TEST_FILEJOURNAL_H_

#include <QObject>
#include "filejournal.h"

class test_filejournal: public QObject {
	Q_OBJECT

private:
	FileJournal *j;
	QString 	tempDir;
	QString		tempPath;

private slots:
	void initTestCase();

	void registerFile();
	void registerDir();

	void cleanupTestCase();
};

#endif /* TEST_FILEJOURNAL_H_ */
