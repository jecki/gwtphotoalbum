#!/bin/bash

# link script to create a kipi-plug in that contains all object files 
# that are needed by kipiplugin_webslideshow
#
# this version of the link script contains the plugin with debug information

gcc -m64 -Wl,-O1,--sort-common,--as-needed,-z,relro,--hash-style=gnu -shared -Wl,-soname,libkipiplugin_webslideshow.so.1 -o kipiplugin_webslideshow.so \
debug/kipiplugin_webslideshow.o \
debug/moc_kipiplugin_webslideshow.o   \
../common_ui/debug/createalbumwizzard.o  \
../common_ui/debug/moc_createalbumwizzard.o  \
../common_ui/debug/moc_progressdialog.o  \
../common_ui/debug/progressdialog.o    \
../common/debug/albumdestination.o  \
../common/debug/imageiocore.o  \
../common/debug/moc_albumdestination.o  \
../common/debug/moc_imageiocore.o  \
../common/debug/qrc_common.o \
../common/debug/htmlprocessing.o  \
../common/debug/imageio.o  \
../common/debug/moc_htmlprocessing.o  \
../common/debug/moc_imageio.o  \
../common/debug/toolbox.o \
../common/debug/iconcache.o  \
../common/debug/imageitem.o  \
../common/debug/moc_iconcache.o  \
../common/debug/moc_imageitem.o  \
../common/debug/zipfile.o \
../common/debug/imagecollection.o  \
../common/debug/jsonsupport.o  \
../common/debug/moc_imagecollection.o  \
../common/debug/moc_zipfile.o

