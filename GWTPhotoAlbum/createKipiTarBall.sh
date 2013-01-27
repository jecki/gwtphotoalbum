#!/bin/sh

version=$(cat VERSION.txt)

tar -cvzf ~/tmp/kipiplugin_webslideshow-$version.tgz --transform "s,^,kipiplugin_webslideshow-$version/," --show-transformed-names \
    kipi-plugin.pro \
    README_kipi-plugin.txt \
    INSTALL.txt \
    README.txt \
    VERSION.txt \
    build.xml \
    copyright \
    htaccess-snippet.txt \
    albumcreator/common/data \
    albumcreator/common/*.cpp \
    albumcreator/common/*.h \
    albumcreator/common/*.qrc \
    albumcreator/common/*.pro \
    albumcreator/common_ui/*.cpp \
    albumcreator/common_ui/*.h \
    albumcreator/common_ui/*.ui \
    albumcreator/common_ui/*.pro \
    albumcreator/kipiplugin_webslideshow/*.cpp \
    albumcreator/kipiplugin_webslideshow/*.h \
    albumcreator/kipiplugin_webslideshow/*.desktop \
    albumcreator/kipiplugin_webslideshow/*.pro \
    albumcreator/quickalbum/*.cpp \
    albumcreator/quickalbum/*.h \
    albumcreator/quickalbum/*.ui \
    albumcreator/quickalbum/*.pro \
    iconsets/grey/*.svg \
    iconsets/grey/*.png \
    scripts/README_scripts.txt \
    scripts/GWTPhotoAlbum-Deploy.zip \
    scripts/genDeploymentPackage.py \
    scripts/genNavigationImages.py \
    scripts/createGWTPhotoAlbum.py \
    scripts/GWTPhotoAlbumCreator.py \
    licenses/* \
    src/com/gwt/components/*.xml \
    src/de/eckhartarnold/*.xml \
    src/de/eckhartarnold/client/*.java \
    src/de/eckhartarnold/client/*.properties \
    war/GWTPhotoAlbum.css \
    war/GWTPhotoAlbum.html \
    war/GWTPhotoAlbum_xs.html \
    war/GWTPhotoAlbum_xs \
    war/icons/*.png \

  
