GWTPhotoAlbum is a free (Apache 2.0 License) AJAX library for an online 
photo gallery and slide show. 

Project website: http://code.google.com/p/gwtphotoalbum/


Copyright 2008-2012 Eckhart Arnold (eckhart_arnold@yahoo.de).
 
Licensed under the Apache License, Version 2.0 (the "License") 
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.


Features:
---------

1. Uses the full browser window size, i.e. automatic resizing of images
   depending on screen size and connection speed.
   
2. Highly configurable, i.e. different layouts for the slide show,
   embeddable in html pages
   
3. Supports the display of image captions

4. Realized in the Java language using google web toolkit; sources are
   well documented (ahm, mostly well documented that is ;) ) and easy 
   to adapt to different needs

5. comes with a few python scripts and a simple gui front end that 
   make creating an online photo album from a collection of images
   extremely easy
   
6. comes with plugins for photo album managing software like KDE
   digikam.
   
7. allows the creation of a javascript free html-only version alongside
   the AJAX photoalbum to which the browser is automatically redirected
   if it does not support javascript
   
8. Supports all majow browsers: Firefox 2.0 and higher, Opera 9.5 and 
   higher, Internet Explorer 7 and higher, Safari 3 and higher,
   Google Chrome; (unfortunately it does not work with the 
   KDE-Konqueror browser :( )
   
      
How to start from here:
-----------------------

First, you may want to look at some examples to see what it looks like:

http://www.eckhartarnold.de/apppages/demos/GWTPhotoAlbum/GWTPhotoAlbum_Demo1.html
http://www.eckhartarnold.de/apppages/demos/GWTPhotoAlbum/GWTPhotoAlbum_Demo2.html

If you want to create your own album, run the program "GWTPhotoAlbumCreator.py" 
from the "scripts" subdirectory which leads you through the creation of a 
photo album in a similar manner as the well known "assistants" of popular 
office suites.

IMPORTANT: Before you can run "GWTPhotoAlbumCreator.py" the following 
software must be installed on your system first:

Python 2.6 or higher (www.python.org)
The python imaging library PIL (http://www.pythonware.com/products/pil/)
The pyexiv2 library (http://tilloy.net/dev/pyexiv2/)
      

How to use the sources:
-----------------------

If you want to use GWTPhotoAlbum in your own AJAX projects, you can browse
the source code in the "src/de.eckhartarnold.client" directory. Please
read "INSTALL.txt" for for information on how to compile and use GWTPhotoAlbum.
Further documentation can be found in the documentation directory. The 
javadocs of the AJAX part of GWTPhotoAlbum can be found in the sub-directory
"documentation/javadocs".


How to join in:
---------------

You can always help me developing this program by writing me bug 
reports or giving comments about what you like and what you do not like
about the program.

Also, if you happen to be a good artist, some help regarding the design of
the control buttons, the photo gallery and other aspect would be greatly
appreciated. 

Finally, why not help programming it? It does not need to be the core 
components, but some plug-ins/export filters for popular imaging software 
like Picasa or F-Spot would be nice, wouldn't they?

