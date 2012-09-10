#!/bin/sh

# quick test for GWTPhotoAlbum:
# 1. generates a new deployment package
# 2. creates a GWTPhotoAlbum
# 3. runs a web browser to display the GWTPhotoAlbum offline


./genDeploymentPackage.py

./createGWTPhotoAlbum.py -j -n -q -b example_pictures

firefox album/index_offline.html
