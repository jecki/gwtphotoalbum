#!/bin/sh

python -O Configure.py
python Makespec.py -F -K -s -X ../scripts/GWTPhotoAlbumCreator.py
cp ../scripts/pyinstaller/debian-6.0/GWTPhotoAlbumCreator.spec GWTPhotoAlbumCreator
python -O Build.py GWTPhotoAlbumCreator/GWTPhotoAlbumCreator.spec
chmod 755 GWTPhotoAlbumCreator/dist/GWTPhotoAlbumCreator
mv GWTPhotoAlbumCreator/dist/GWTPhotoAlbumCreator GWTPhotoAlbumCreator/dist/GWTPhotoAlbumCreator-0.7.7-amd64.bin
