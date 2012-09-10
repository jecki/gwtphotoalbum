/*
 * test_imageitem.h
 *
 *  Created on: 03.02.2011
 *      Author: eckhart
 */

#ifndef TEST_ZIPFILE_H_
#define TEST_ZIPFILE_H_

#include <QObject>
#include <QString>
#include "zipfile.h"

class test_zipfile: public QObject {
	Q_OBJECT

private:
	QString		tempPath;

private slots:
	void initTestCase();

	void addData();

	void cleanupTestCase();
};

#endif /* TEST_ZIPFILE_H_ */
