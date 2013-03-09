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
#include <QCoreApplication>
#include <QDir>
#include <iostream>
#include <functional>

#include "albumcreator.h"


int main(int argc, char *argv[])
{

#ifndef NDEBUG
	std::cout << "debugging is on!" << std::endl;
#endif

	QApplication a(argc, argv);
	AlbumCreator w;
	w.show();

    return a.exec();
}
