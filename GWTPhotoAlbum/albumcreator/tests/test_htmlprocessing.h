/*
 * test_htmlprocessing.h
 *
 *  Created on: 27.01.2011
 *      Author: eckhart
 */

#ifndef TEST_HTMLPROCESSING_H_
#define TEST_HTMLPROCESSING_H_

#include "htmlprocessing.h"
#include <QObject>

class test_htmlprocessing: public QObject {
	Q_OBJECT

	QString startPage;
	HTMLProcessing *hp;

private slots:
	void initTestCase();

	void readTemplates();
	void indexPage();
	void offlineIndex();
	void noscriptGallery();
	void noscriptImagePage();
	void fatStartPage();

	void cleanupTestCase();
};

#endif /* TEST_HTMLPROCESSING_H_ */
