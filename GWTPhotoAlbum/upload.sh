#!/bin/bash

version=$(cat VERSION.txt)

python2 scripts/upload.py -p gwtphotoalbum -j "[
['~/tmp/kipiplugin_webslideshow-$version.tgz', 'Sources for a KDE KIPI web slideshow plugin', 'Type-Source,OpSys-All'],
['~/tmp/GWTPhotoAlbum-executables-$version.tgz', 'Some Python scripts to create web slideshows', 'Type-Executable,OpSys-All'],
['~/tmp/GWTPhotoAlbum-sources-$version.tgz', 'GWTPhotoAlbum sources without examples', 'Type-Source,OpSys-All'],
['~/tmp/GWTPhotoAlbum-fat-sources-$version.tgz', 'GWTPhotoAlbum sources including an example HTML gallery', 'Type-Source,OpSys-All'],
['~/tmp/GWTPhotoAlbum-demo-$version.tgz', 'Just an example HTML gallery made with GWTPhotoAlbum', 'Type-Archive,OpSys-All']
]"

