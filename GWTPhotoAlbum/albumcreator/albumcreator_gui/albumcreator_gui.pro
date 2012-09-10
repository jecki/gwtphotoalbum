# Copyright 2010 Eckhart Arnold (eckhart_arnold@hotmail.com).
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
# http://www.apache.org/licenses/LICENSE-2.0
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
TEMPLATE = app
CONFIG += debug_and_release
TARGET = albumcreator

# CONFIG += debug
QT += core \
    gui
INCLUDEPATH += ../common \
    ../common_ui
LIBS += -L../common \
    -L../common_ui \
    -lcommon \
    -lcommon_ui
HEADERS += imageitemdelegate.h \
    filebrowserlistview.h \
    albumview.h \
    imagelistmodel.h \
    iconcache.h \
    albumcreator.h \
    filterproxy_class.h \
    ../common/htmlprocessing.h \
    ../common/imagecollection.h \
    ../common/imageio.h \
    ../common/imageiocore.h \
    ../common/imageitem.h \
    ../common/jsonsupport.h \
    ../common/threadsafeset_class.h \
    ../common/toolbox.h \
    ../common_ui/createalbumwizzard.h \
    ../common_ui/progressdialog.h \
    albumcreator.h \
    filterproxy_class.h
SOURCES += imageitemdelegate.cpp \
    filebrowserlistview.cpp \
    albumview.cpp \
    imagelistmodel.cpp \
    iconcache.cpp \
    main_gui.cpp \
    albumcreator.cpp
FORMS += albumcreator.ui \
    ../common_ui/createalbumwizzard.ui \
    ../common_ui/progressdialog.ui \
    albumcreator.ui
RESOURCES += albumcreator_gui.qrc
