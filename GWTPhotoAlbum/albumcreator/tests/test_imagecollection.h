/*
 * test_imagecollection.h
 *
 *  Created on: 08.02.2011
 *      Author: eckhart
 */

#ifndef TEST_IMAGECOLLECTION_H_
#define TEST_IMAGECOLLECTION_H_


#include <QObject>
#include <Qt>

#include "imagecollection.h"

class test_imagecollection: public QObject {
	Q_OBJECT

private:
	ImageCollection *ic;
	int	numImages;
	QString tempDir;
	QString tempPath;

private slots:
	void initTestCase();

	void statics();

	void search();
	void find();

	void fromJSON();
	void infoJSON();
	void directoriesJSON();
	void filenamesJSON();
	void captionsJSON();
	void resolutionsJSON();
	void createAlbum();
	void rollback();

	void miscTests();

	void cleanupTestCase();
};

class Progress: public QObject {
	Q_OBJECT
public:
	ImageCollection *ic;
	int 			last;
	bool			finishSignal, completed;
	QString			errorMsg;

	Progress(ImageCollection *ic);
	void reset();

public Q_SLOTS:
	void progress(ImageCollection *source, int nr);
	void finished(bool completed, const QString &errorMsg);
};

#endif /* TEST_IMAGECOLLECTION_H_ */
