#!/bin/bash

ant build
ant -f javadoc.xml

cd scripts
python genDeploymentPackage.py
cd ..

rm -r documentation/GWTPhotoAlbum_docs/skeleton/GWTPhotoAlbum_xs
rm -r documentation/GWTPhotoAlbum_docs/skeleton/icons
unzip -o scripts/GWTPhotoAlbum-Deploy.zip -x VERSION.txt -d documentation/GWTPhotoAlbum_docs/skeleton

cp VERSION.txt albumcreator/common/data
cd albumcreator

qmake-qt4 -recursive
#qmake -recursive
make debug

#cd scripts
#quick_test.sh

cd ..

#bash createTarBalls.sh
#bash createKipiTarBall.sh

