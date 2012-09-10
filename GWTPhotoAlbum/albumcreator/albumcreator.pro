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
TEMPLATE = subdirs
CONFIG += debug_and_release
SUBDIRS = common \
    common_ui \
    tests \
    albumcreator_gui \
    albumcreator_cli \
    quickalbum \
    kipiplugin_webslideshow
tests.depends = common
quickalbum.depends = common \
    common_ui
albumcreator_gui.depends = common \
    common_ui
albumcreator_cli.depends = common

