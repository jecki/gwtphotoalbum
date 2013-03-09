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


#include <Qt>
#if QT_VERSION >= 0x050000
#include <QApplication>
#else
#include <QtGui/QApplication>
#endif
#include <QMainWindow>
#include <QFileDialog>
#include <QDir>
#include <iostream>

#include "imagecollection.h"
#include "createalbumwizzard.h"
#include "toolbox.h"


int main(int argc, char *argv[])
{

	(void) argc; (void) argv;
#ifndef NDEBUG
	std::cout << "debugging is on!" << std::endl;
#endif
	ImageCollection ic;
	int 	ret;
	QString error;

	QApplication a(argc, argv);
//	QuickAlbumMainWin w;
//	w.show();
//	a.exec();

	QFileDialog fd;
	fd.setAcceptMode(QFileDialog::AcceptOpen);
	fd.setFilter(QDir::AllDirs|QDir::AllEntries);
	fd.setDirectory("/home/eckhart/.local/share/geeqie/collections");
	QStringList filters;
	filters << "Image Files (*.jpeg *.jpg *.png *.gqv)";
	filters << "Any Files (*)";
	fd.setNameFilters(filters);
	fd.setWindowTitle("Pick image files or image list...");
	fd.setFileMode(QFileDialog::ExistingFiles);
	fd.exec();
	QStringList paths = fd.selectedFiles();

#ifndef NDEBUG
	foreach(QString path, paths) {
		std::cout << path.toStdString() << std::endl;
		if (path.endsWith(".gqv", Qt::CaseInsensitive)) {
			ic.addImageList(path);
		} else {
			ic.imageList.append(new ImageItem(path));
		}
	}
	std::cout << "That was all..." << std::endl;
#endif

	if (!ic.imageList.isEmpty()) {
		CreateAlbumWizzard w(ic);
		w.setAboutTab(true);
		ret = w.exec();
	} else {
		ret = 1;
	}

#ifndef NDEBUG
	std::cout << "program finished!" << std::endl;
#endif

	return ret;
	// return w.returnValue;
}
