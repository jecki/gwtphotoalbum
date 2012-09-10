#!/bin/bash

# link script to create a kipi-plug in that contains all object files 
# that are needed by kipiplugin_webslideshow
#
# this version of the link script contains the plugin with debug information

gcc -m64 -Wl,-O1,--sort-common,--as-needed,-z,relro,--hash-style=gnu -shared -Wl,-soname,libkipiplugin_webslideshow.so.1 -o kipiplugin_webslideshow.so \
release/kipiplugin_webslideshow.o \
release/moc_kipiplugin_webslideshow.o   \
../common_ui/release/createalbumwizzard.o  \
../common_ui/release/moc_createalbumwizzard.o  \
../common_ui/release/moc_progressdialog.o  \
../common_ui/release/progressdialog.o    \
../common/release/albumdestination.o  \
../common/release/imageiocore.o  \
../common/release/moc_albumdestination.o  \
../common/release/moc_imageiocore.o  \
../common/release/qrc_common.o \
../common/release/htmlprocessing.o  \
../common/release/imageio.o  \
../common/release/moc_htmlprocessing.o  \
../common/release/moc_imageio.o  \
../common/release/toolbox.o \
../common/release/iconcache.o  \
../common/release/imageitem.o  \
../common/release/moc_iconcache.o  \
../common/release/moc_imageitem.o  \
../common/release/zipfile.o \
../common/release/imagecollection.o  \
../common/release/jsonsupport.o  \
../common/release/moc_imagecollection.o  \
../common/release/moc_zipfile.o

