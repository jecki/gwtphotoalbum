#!/bin/sh

python -O Configure.py
python Makespec.py -F -K -s -X ../scripts/GWTPhotoAlbumCreator.py
cp ../scripts/pyinstaller/ubuntu-10.4/GWTPhotoAlbumCreator.spec GWTPhotoAlbumCreator
python -O Build.py GWTPhotoAlbumCreator/GWTPhotoAlbumCreator.spec
chmod 755 GWTPhotoAlbumCreator/dist/GWTPhotoAlbumCreator
mv GWTPhotoAlbumCreator/dist/GWTPhotoAlbumCreator GWTPhotoAlbumCreator/dist/GWTPhotoAlbumCreator-0.7.7-i386.bin

