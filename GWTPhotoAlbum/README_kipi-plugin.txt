kipiplugin_webslideshow is a part of GWTPhotoAlbum, which is a free 
(Apache 2.0 License) AJAX library for an online photo gallery and 
slide show. 


A. GENERAL INFORMATION

The kipiplugin_wbslideshow is a plugin for the KDE imaging infrastructure
(http://sourceforge.net/projects/kipi/) It allows to create nice looking 
slide shows with digikam (www.digikam.org) or any other KDE imaging software. 
The slideshows can be viewed online or offline with most reasonably modern 
internet browsers. Other than the stock HTML export filter from the kipi
package, kipiplugin_webslideshow uses dynamic HTML, which allows for much 
nicer web slide shows, even though the flash plugin is not needed. 

Project website for GWTPhotoAlbum (including kipiplugin_webslideshow): 
http://code.google.com/p/gwtphotoalbum/


B. LICENSE INFORMATION: kipiplugin_webslideshow

Copyright 2008-2012 Eckhart Arnold (eckhart_arnold@yahoo.de).
 
Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.


C. PRIOR TO INSTALLATION

In order to compile the kipi-plugin you need a C++ compiler, the Qt SDK
(qt.nokia.com), the boost::crc library (www.boost.org/doc/libs/1_49_0/libs/crc/) 
and the KDE and kipi devlopment libraries (www.kde.org)

In order to rebuild the AJAX part (usually not necessary as it is supplied
in prebuild form with the kipiplugin_webslideshow sources), you also need
to install the google web toolkit (developers.google.com/web-toolkit/) to 
compile the AJAX part and the python scripting language (www.python.org)
to run the packaging scripts for the AJAX part. 

All of these must be properly set up for your system environment.


D. COMPILING AND INSTALLATION

In order to compile and install the kipi-plugin you need to run the following
commands from the main directory:

qmake
make
sudo make install


If you also want to rebuild the AJAX part of kipiplugin_webslideshow (usually
not needed), you should run the following commands prior to compiling and
installing the kipi-plugin:

and build
cd scripts
python genDeplymentPackage.py
cd ..


D. USING THE kipiplugin_webslideshow

In order to use the plugin you can use any KDE program that makes use of the
kipi interface. For example, if you use digikam to manage your photo archives,
you find the kipiplugin_webslide show in the "Export" menu as entry "Create a
Web-Slideshow".

