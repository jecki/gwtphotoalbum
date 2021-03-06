# Copyright 2011 Eckhart Arnold (eckhart_arnold@hotmail.com).
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.

TEMPLATE = lib
TARGET = kipiplugin_webslideshow
CONFIG += shared \ # staticlib 
    plugin \
    no_plugin_name_prefix \
    debug_and_release
QT += core \
	gui
INCLUDEPATH += ../common \
	../common_ui
HEADERS += kipiplugin_webslideshow.h
SOURCES += kipiplugin_webslideshow.cpp

# common_ui and common must already have been compiled for this...
CONFIG(debug, debug|release) {
  OBJECTS += ../common_ui/debug/createalbumwizzard.o  \
../common_ui/debug/moc_createalbumwizzard.o  \
../common/debug/imageiocore.o  \
../common/debug/moc_imageiocore.o  \
../common/debug/qrc_common.o \
../common/debug/htmlprocessing.o  \
../common/debug/imageio.o  \
../common/debug/moc_htmlprocessing.o  \
../common/debug/moc_imageio.o  \
../common/debug/toolbox.o \
../common/debug/imageitem.o  \
../common/debug/moc_imageitem.o  \
../common/debug/zipfile.o \
../common/debug/imagecollection.o  \
../common/debug/jsonsupport.o  \
../common/debug/moc_imagecollection.o
}

CONFIG(release, debug|release) {
  OBJECTS += ../common_ui/release/createalbumwizzard.o  \
../common_ui/release/moc_createalbumwizzard.o  \
../common/release/imageiocore.o  \
../common/release/moc_imageiocore.o  \
../common/release/qrc_common.o \
../common/release/htmlprocessing.o  \
../common/release/imageio.o  \
../common/release/moc_htmlprocessing.o  \
../common/release/moc_imageio.o  \
../common/release/toolbox.o \
../common/release/imageitem.o  \
../common/release/moc_imageitem.o  \
../common/release/zipfile.o \
../common/release/imagecollection.o  \
../common/release/jsonsupport.o  \
../common/release/moc_imagecollection.o
}

PREFIX = $$system(kde4-config --prefix)

desktop.path = $$PREFIX/share/kde4/services
desktop.files = kipiplugin_webslideshow.desktop

INSTALLS += desktop

plugin.path = $$PREFIX/lib/kde4
plugin.files = kipiplugin_webslideshow.so

INSTALLS += plugin

#message("kipi plugin install path: $$desktop.path")
#message(".desktop file install path: $$plugin.path")
