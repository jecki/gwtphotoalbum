#! /usr/bin/python2

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

"""genNavigationImages.py

Reads all navigation images with a horizontal size of 64 pixel (e.g. "play_64.png") 
and produces scaled down versions with horizontal sizes of 48, 32, 24, 16 pixel.
"""

import os, sys
from math import sqrt
from PIL import Image, ImageFile

src_prefix = "../iconsets/"
src_dir = os.path.join(src_prefix, "grey")
dst_dirs = [ "../war/icons" ]
#dst_dir = "/home/eckhart/tmp"

base_names = ["begin", "begin_down", "back", "back_down",
              "gallery", "gallery_down", "play", "pause", 
              "next", "next_down", "end", "end_down"]
file_type = ".png"
copy_only_names = ["start.png", "start_down.png"]

im_sizes = [64, 48, 32, 24, 16]
 
 
def greyFilter(rgba, weight):
    m = sum(rgba[0:3])/3.0
    r = int(m * weight + rgba[0]*(1.0-weight))
    g = int(m * weight + rgba[1]*(1.0-weight))
    b = int(m * weight + rgba[2]*(1.0-weight))        
    return (r,g,b,rgba[3])

def brighten(rgba, e):
    r,g,b,a = rgba
    r = int(r**e * (256/(256**e)))
    g = int(g**e * (256/(256**e)))
    b = int(b**e * (256/(256**e)))    
    return (r,g,b,a)

def myfilter(rgba):
    return brighten(greyFilter(rgba, 0.5), 0.6)

def apply(image, filter):
    pix = image.load()
    for x in range(image.size[0]):
        for y in range(image.size[1]):
            pix[x,y] = filter(pix[x,y])
            
 
def createSmallSizes(base_name):
    try:
        im = Image.open(os.path.join(src_dir, base_name+file_type))
        srcW, srcH = im.size
        for destH in im_sizes:
            if destH != im.size[1]:
                destW = srcW * destH / srcH
                dest = im.resize((destW, destH), Image.ANTIALIAS)
            else:
                dest = im.copy()
            if src_dir.find("colored") >= 0: 
                apply(dest, myfilter)
            for dst_dir in dst_dirs:
                dest.save(os.path.join(dst_dir,base_name+"_"+str(destH)+file_type),
                          optimize=1, transparacy = (255, 255, 255)) # transparacy, bits
    except (IOError, err):
        print ("IOError", err)
    

def genNavigationImages():
    for name in base_names:
        createSmallSizes(name)
    for name in copy_only_names:
        for dst_dir in dst_dirs:
            with open(os.path.join(src_dir, name), "rb") as src:
                with open(os.path.join(dst_dir, name), "wb") as dst:
                    data = src.read()
                    dst.write(data)    

if __name__ == "__main__":
    if len(sys.argv) > 1:
        src_dir = os.path.join(src_prefix, sys.argv[1])
    genNavigationImages()

