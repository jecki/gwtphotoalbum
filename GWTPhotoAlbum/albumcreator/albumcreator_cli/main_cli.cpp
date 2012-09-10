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



#include <QCoreApplication>
#include <QDir>
#include <QSize>
#include <iostream>
#include <string>
#include <functional>

#include "imagecollection.h"
#include "toolbox.h"


struct AddImage: IWalkDirCallback {
	ImageCollection *ic;
	QStringList     supportedFormats;

	AddImage(ImageCollection *ic) {
		this->ic = ic;
		supportedFormats = getSupportedImageFormats();
	}

	virtual ~AddImage() { }

	bool checkFormat(const QString &fileName) {
		QString name = fileName.toUpper();
        foreach(QString format, supportedFormats) {
            if (name.endsWith(format))
                return true;
        }
        return false;
	}

	virtual void processFileInfo(const QFileInfo &info) {
		QString path = info.filePath();
		if (checkFormat(path)) {
			std::cout << "adding image: " << path.toStdString() << std::endl;
			ic->imageList.append(new ImageItem(path));
		}
	}
};



void progressFunction(ImageCollection *source, int nr) {
	int all = source->imageList.length();
	std::string str = source->imageList.at(nr-1)->destFileName().toStdString();
	std::cout << "image " << nr << "/" << all << " converted: " << str << std::endl;
}


int main(int argc, char *argv[])
{

#ifndef NDEBUG
	std::cout << "debugging is on!" << std::endl;
#endif

	if (argc == 3){
		QCoreApplication a(argc, argv);
		QDir source(cleanPath(argv[1])), dest(cleanPath(argv[2]));

		if (!source.exists()) {
			std::cout << "Error: Source directory " << source.path().toStdString() << " does not exist!" << std::endl;
		} else if (dest.exists()) {
			std::cout << "Error: Destination directory " << dest.path().toStdString() << " already exists!" << std::endl;
		} else {
			ImageCollection ic;
			AddImage        add(&ic);
			ProgressAdaptor progress(&ic, &progressFunction);

			QList<QSize>    sizesList;
			sizesList << ImageItem::Thumbnail_Size;
			sizesList << QSize(320, 240);
			sizesList << QSize(640, 480);
			sizesList << QSize(1024, 768);
			sizesList << QSize(1440, 900);
			sizesList << QSize(1920, 1200);
			sizesList << ImageCollection::Original_Size;
			ic.setSizes(sizesList);

			walkDir(source.path(), &add);
			std::cout << "Creating album: " << dest.absolutePath().toStdString() << std::endl;
			if (!ic.createAlbum(dest.path())) {
				std::cout << ic.errorString().toStdString() << std::endl;
			}
		}


	} else {
		std::cout << "Usage: createalbum sourcedir destdir" << std::endl;
	}

	return 0;
}
