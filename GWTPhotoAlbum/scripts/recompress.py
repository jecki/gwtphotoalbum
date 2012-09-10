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

"""recompress.py

Re-compresses all jpeg images in a directory by calling 'convert -quality 75'.
The converted images will be stored in a sub-directory: 'recompressed-0'.

Usage:
    python recompress.py [directory] [quality]
"""

import sys, os


def recompress(dir = ".", quality = "75"):
    olddir = os.curdir
    os.chdir(dir)
    
    files = os.listdir(".")
    i = 0
    convdir = "recompressed-"
    while convdir+str(i) in files:
        i += 1
    convdir += str(i)
    os.mkdir(convdir)
    
    for f in files:
        if f.lower().endswith(".jpg"):
            os.system("convert -quality "+quality+" "+f+" "+os.path.join(convdir,f.lower()))
    
    os.chdir(olddir)
    

if __name__ == "__main__":
    if len(sys.argv) > 1: 
        dir = sys.argv[1]
    else:
        dir = "."
    if len(sys.argv) > 2:
        quality = sys.argv[2]
    else:
        quality = "75" 
    recompress(dir, quality)