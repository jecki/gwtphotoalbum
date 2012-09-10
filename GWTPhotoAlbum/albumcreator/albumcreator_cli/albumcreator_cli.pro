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
TARGET = createalbum
QT += core
INCLUDEPATH += ../common
LIBS += -L \
    ../common \
    -lcommon
HEADERS += ../common/toolbox.h \
    ../common/imagecollection.h
SOURCES += main_cli.cpp
RESOURCES += ../common/common.qrc