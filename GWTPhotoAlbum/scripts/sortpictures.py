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


"""sortimages.py - sorts the pictures in a directory by their date and assigns numbers 
as name prefixes so that the alphabetical order of the filenames reflects the file
dates.
"""

import os, getopt, sys, datetime, math
import pyexiv2


def getdate(image_file):
    "returns the date when a picture was taken"
    tagName = "Exif.Photo.DateTimeOriginal"
    try:
        pyexiv2.version_info
        try:
            metadata = pyexiv2.ImageMetadata(image_file)
            metadata.read()
            tag = metadata[tagName]
            return tag.value
        except (KeyError, IOError):
            pass
    except AttributeError:
        try:
            img = pyexiv2.Image(image_file)
            img.readMetadata()
            value = img[tagName]
            return value
        except (KeyError, IOError):
            pass
    return datetime.datetime.fromtimestamp(os.stat(image_file).st_mtime)
    

def stripOrderPrefix(filename):
    """returns the file name without any order prefix 
    (i.e. a number followed by '_'.
    """
    n = filename.find("_")
    if n > 0:
        try:
            int(filename[:n])
            return filename[n+1:]
        except ValueError:
            pass
    return filename


def prefixed(filename, i, digits):
    """Adds a number prefix to the filename, e.g. '0001_'.
    If the number of digits in 'i' is higher than 'digits',
    the number gets truncated.
    """
    s = str(i)
    prefix = "0"*(max(0,digits-len(s))) + s + "_"
    return prefix + filename
     

if __name__ == "__main__":
    opt, args = getopt.getopt(sys.argv[1:], "", [])
    cwd = os.getcwd()
    if len(args) > 0:
        dir = args[0]
    else:
        dir = "."
    os.chdir(dir)
    files = [(getdate(file), file) for file in os.listdir(".")]
    files.sort()    
    sorted_list = [f[1] for f in files]
    digits = int(math.log(len(sorted_list),10))+1
    for i,file in enumerate(sorted_list):
        os.rename(file, prefixed(stripOrderPrefix(file),i,digits))
    os.chdir(cwd)
    