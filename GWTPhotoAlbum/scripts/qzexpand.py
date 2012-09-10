#!/usr/bin/python

"""qzexpand.py 

Script from GWTPhotoAlbum (http://code.google.com/p/gwtphotoalbum/)

Expands a .qz archive to the current directory.
.qz is a simple archive format for album creator.

Usage:
  python qzexpand.py [qzarchive] [destDir]
"""

import sys, os
from PyQt4.Qt import QByteArray, qUncompress

destDir = "."
archive = "GWTPhotoAlbum-Deploy.qz"


def expand(archive, destDir):
    f = open(archive, "rb")
    data = f.read()
    f.close()
    
    currDir = os.getcwd()
    os.chdir(destDir)
    
    data = qUncompress(QByteArray(data)).data()
    i = 0
    while i < len(data):
        N = int(data[i:i+10], 16);  i += 10
        path = data[i:i+N];  i += N
        
        dirname, filename = os.path.split(path)
        if dirname != "" and not os.path.exists(dirname):
            os.makedirs(dirname)
        
        L = int(data[i:i+10], 16);  i += 10
        content = data[i:i+L]; i += L
        
        print ("writing file: "+filename+" ("+str(L/1024)+" kBytes)")
        
        f = open(path, "wb")
        f.write(content)
        f.close()
    
    os.chdir(currDir)


if __name__ == "__main__":
    if "-h" in sys.argv or "--help" in sys.argv:
        print __doc__
    else:
        if len(sys.argv) > 1:
            archive = sys.argv[1]
            if len(sys.argv) > 2:
                destDir = sys.argv[2]
        expand(archive, destDir)

