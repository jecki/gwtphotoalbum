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
TEMPLATE = lib
TARGET = common
CONFIG += precompile_header \
    debug_and_release \
    shared # staticlib # shared
QT += core \
    network
HEADERS += zipfile.h \
    albumdestination.h \
    htmlprocessing.h \   
    imagecollection.h \
    imageio.h \
    imageiocore.h \
    imageitem.h \
    jsonsupport.h \
    threadsafeset_class.h \
    toolbox.h
SOURCES += zipfile.cpp \
    albumdestination.cpp \
    htmlprocessing.cpp \  
    imagecollection.cpp \
    imageio.cpp \
    imageiocore.cpp \
    imageitem.cpp \
    jsonsupport.cpp \
    toolbox.cpp
system(cp ../../VERSION.txt data)
RESOURCES += common.qrc
