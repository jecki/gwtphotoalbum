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
TARGET = quickalbum
CONFIG += debug_and_release
QT += core \
    gui
INCLUDEPATH += ../common \
    ../common_ui
LIBS += -L../common \
    -L../common_ui \
    -lcommon \
    -lcommon_ui
SOURCES += quickalbummainwin.cpp \
    main_quickalbum.cpp
FORMS = quickalbummainwin.ui
HEADERS = quickalbummainwin.h
#PRE_TARGETDEPS += ../common_ui/libcommon_ui.a \
#	../common/libcommon.a

