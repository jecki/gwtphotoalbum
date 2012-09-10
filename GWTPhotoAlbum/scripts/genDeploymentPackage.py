#!/usr/bin/python

#
# Copyright 2008 Eckhart Arnold (eckhart_arnold@hotmail.com).
# 
# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#

"""genDeploymentPackage.py

Script from GWTPhotoAlbum (http://code.google.com/p/gwtphotoalbum/)

Creates a zip file that contains all files (html files with the embedded 
GWT classes compiled to javascript, icons, further gwt files) that are needed
for the deployment of a GWTPhotoAlbum. It does not contain the "slides" 
sub-directory with the photos and the .json configuration files itself.
The GWTPhotoAlbum sources must have been compiled with the compile script,
before building the zip-Archive with this script!

Usage:
  python genDeploymentPackage.py [source directory] [destination file name] [qz archive name]
"""

import sys, os
try:
    import zipfile
except (ImportError):
    import zipfile_py2x as zipfile


class emptyCompressor(object):
    def __init__(self, fileName):
        pass
    def write(self, src, arcName):
        pass
    def close(self):
        pass
        

try:
    from PyQt4.Qt import QByteArray, qCompress
    
    class QtCompressor(object):
        def __init__(self, fileName):
            # fileName = os.path.splitext(fileName)[0] + ".qz"
            self.fileName = fileName
            self.byteArray = QByteArray()
        def toHexStr(self, i):
            s = hex(i)
            k = 10 - len(s)
            return s[0:2] + "0"*k + s[2:]
        def write(self, src, arcName):
            g = open(src, "rb")
            data = g.read()
            g.close()
            self.byteArray.append(self.toHexStr(len(arcName)))
            self.byteArray.append(arcName)
            self.byteArray.append(self.toHexStr(len(data)))
            self.byteArray.append(data)
        def close(self):
            f = open(self.fileName, "wb")
            # don't use compression, because resource files are compressed by Qt anyway
            data = self.byteArray.data() #data = qCompress(self.byteArray, 9).data()
            f.write(data)
            f.close()
            
except ImportError:
    QtCompressor = emptyCompressor


source_dir = "../war"
version_file_path = "../VERSION.txt"
dest_file_name  = "GWTPhotoAlbum-Deploy.zip"
qz_file_name = "../albumcreator/common/data/GWTPhotoAlbum-Deploy.qa"

if not os.path.exists(os.path.dirname(qz_file_name)):
    print ("No albumcreator sources: Won't create GWTPhotoAlbum-Deploy.qa!")
    QtCompressor = emptyCompressor


def verify_source_dir():
    if not os.path.isdir(source_dir):
        print ("error: "+source_dir+" is not a valid directory")
        return False;
    names = os.listdir(source_dir)
    if "icons" not in names:
        print ("error: "+source_dir+"does not contain an 'icons' subdirctory")
        return False
    if "GWTPhotoAlbum" not in names:
        print ("error: "+source_dir+"does not contain an 'GWTPhotoAlbum' subdirctory")
        return False
    l = [entry for entry in names if entry.endswith(".html")]
    if len(l) == 0:
        print ("error: "+source_dir+"does not contain any html files")
        return False
    return True

def assemble():
    f = zipfile.ZipFile(dest_file_name, "w", zipfile.ZIP_DEFLATED)
    g = QtCompressor(qz_file_name)
    f.write(version_file_path, "VERSION.txt")
    g.write(version_file_path, "VERSION.txt")
    try:
        def visit(arg, dirname, names):
            pathname = (os.path.normpath(dirname[len(source_dir):]), "")
            while pathname[0] not in ["", "/"]:
                pathname = os.path.split(pathname[0])
            if pathname[1] not in ["GWTPhotoAlbum_xs", "icons", "."] or \
                    dirname.find(".svn") >= 0:
                print ("ignoring directory: " + dirname)
                return
            else:
                print ("including directory: " + dirname)
            if os.path.samefile(dirname, source_dir):
                for entry in names[:]:
                    if entry.find("Demo") >= 0 \
                            or (os.path.isdir(os.path.join(dirname, entry)) \
                            and entry not in ("gwt", "icons",  # "GWTPhotoAlbum"  
                                              "GWTPhotoAlbum_xs")) \
                            or entry in ["index.html", "index_offline.html", 
                                         "GWTPhotoAlbum_offline.html",
                                         "GWTPhotoAlbum.html"] \
                            or entry.find("_fatxs") >= 0 \
                            or entry.startswith("noscript_") :
                        print ("ignoring: "+entry)
                        names.remove(entry)
            for entry in names[:]:
                if entry.find(".svn") >= 0 or entry in ["hosted.html"]:
                    print ("ignoring: "+entry)
                    names.remove(entry)
            for entry in names:
                absolute_path = os.path.join(dirname, entry)                
                if not os.path.isdir(absolute_path):              
                    relative_path = absolute_path[len(source_dir)+1:]
#                    if entry == "GWTPhotoAlbum.html" or entry == "GWTPhotoAlbum.css":
#                        relative_path = os.path.join("GWTPhotoAlbum", relative_path)            
                    print ("adding: "+relative_path)
                    f.write(absolute_path, relative_path)
                    g.write(absolute_path, relative_path)
        try:                 
            os.path.walk(source_dir, visit, None)
        except (AttributeError): # python3 !
            for dirpath, dirnames, filenames in os.walk(source_dir):
                visit(0, dirpath, dirnames + filenames)            
#    except zipfile.error, IOError:
#        print ("Error writing zip-file!")
    finally:
        f.close()
        g.close()

if __name__ == "__main__":
    if "-h" in sys.argv or "--help" in sys.argv:
        print (__doc__)
    else:
        if len(sys.argv) > 1:
            source_dir = sys.argv[1]
        if len(sys.argv) > 2:
            dest_file_name = sys.argv[2]
        if len(sys.argv) > 3: 
            qz_file_name = sys.argv[3]
        if verify_source_dir():
            print ("source directory: " + source_dir)
            print ("destination file: " + dest_file_name)
            print ("qz archive file:  " + qz_file_name)
            assemble()
