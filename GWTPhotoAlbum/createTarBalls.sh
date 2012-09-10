#!/bin/bash

version=$(cat VERSION.txt)

if [ $# -ge 1 ]
then
  version="$1"
fi

cd scripts
python genDeploymentPackage.py
cd ..

cd ..

tar -czvf ~/tmp/GWTPhotoAlbum-sources-$version.tgz GWTPhotoAlbum/copyright GWTPhotoAlbum/README.txt GWTPhotoAlbum/INSTALL.txt GWTPhotoAlbum/licenses GWTPhotoAlbum/src GWTPhotoAlbum/war/icons GWTPhotoAlbum/scripts --exclude-vcs --exclude=WEB-INF --exclude=*.jpg --exclude=.* --exclude=example_pictures --exclude=*.pyc

tar -czvf ~/tmp/GWTPhotoAlbum-fat-sources-$version.tgz GWTPhotoAlbum/copyright GWTPhotoAlbum/README.txt GWTPhotoAlbum/INSTALL.txt GWTPhotoAlbum/licenses GWTPhotoAlbum/src GWTPhotoAlbum/war GWTPhotoAlbum/scripts --exclude-vcs --exclude=WEB-INF --exclude=.* --exclude=example_pictures --exclude=*.pyc

tar -czvf ~/tmp/GWTPhotoAlbum-executables-$version.tgz GWTPhotoAlbum/copyright GWTPhotoAlbum/README.txt GWTPhotoAlbum/licenses GWTPhotoAlbum/scripts --exclude-vcs --exclude=example_pictures --exclude=.* --exclude=*.pyc

tar -czvf ~/tmp/GWTPhotoAlbum-example_pictures-$version.tgz GWTPhotoAlbum/scripts/example_pictures --exclude-vcs --exclude=.*

tar -cvf ~/tmp/GWTPhotoAlbum-demo-$version.tgz GWTPhotoAlbum/war/ --exclude-vcs --exclude=.* --exclude=WEB-INF

cd GWTPhotoAlbum

