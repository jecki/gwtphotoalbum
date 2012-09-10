This directory contains two scripts to create a GWTPhotoAlbum site, i.e.
a directory that contains alle the necessary images and compiled scripts
for a GWTPhotoAlbum.
 
   1) genDeploymentPackage.py 
   2) createPhotoAlbum.py
   3) GWTPhotoAlbumCreator.py

The scripts are written in the python programming language. The
following software must be installed prior to running the scripts:

1. Python 2.6 or higher (www.python.org)
2. The python imaging library PIL (http://www.pythonware.com/products/pil/)
3. The pyexiv2 library (http://tilloy.net/dev/pyexiv2/)
    


createPhotoAlbum.py
-------------------

takes a directory with images (jpg or png) as input
and creates a complete GWTPhotoAlbum from these images. The GWTPhotoAlbum then
only needs to be uploaded to a server. That's all. 

Before uploading the type of the photo album and other adjustments, like the
order of the pictures (which is alphabetically by default) can be made
by editing the json files, especially "info.json" in the "album/slides" 
sub- directory.!

The script requires that a deployment package has been created 
with "genDeploymentPackage.py", before.


Eventually, these scripts will be replaced by a proper front-end for 
GWTPhotoAlbum or by plug-ins for common photo software like 
digikam, f-spot and others.


GWTPhotoAlbumCreator.py
-----------------------

WebAlbumCreator.py is a simple GUI front end for createPhotoAlbum.py that
allows the user to pick images, select a configuration and then save
the photo album in a directory. To upload the photo album a separate ftp
client is needed.


genNavigationImages.py
----------------------

creates scaled down version of the navgation image .png files in the
../iconsets directory and stores them in war/icons.


genDeploymentPackage.py 
-----------------------

creates a zip file that collects all the compiled
scripts for a GWTPhotoAlbum and other necessary files like the button images,
css styles etc. Obviously, the GWTPhotoAlbum sources must have been compiled
before genDeploymentPackage.py can create a deployment package.

