/*
 * test_imageitem.h
 *
 *  Created on: 03.02.2011
 *      Author: eckhart
 */

#ifndef TEST_IMAGEITEM_H_
#define TEST_IMAGEITEM_H_

#include <QObject>
#include "imageitem.h"

class test_imageitem: public QObject {
	Q_OBJECT

private:
	ImageItem *it;

private slots:
	void initTestCase();

	void destFileName();
	void image();
	void caption();
	void resized();
	void errorHandling();

	void cleanupTestCase();
};

#endif /* TEST_IMAGEITEM_H_ */
