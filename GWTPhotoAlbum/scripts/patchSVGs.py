#!/usr/bin/python

#
# Copyright 2015 Eckhart Arnold (eckhart_arnold@yahoo.de).
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

"""patchSVGs.py

Reads all SVG files in a given directory (default = current directory) and
adds a viewBox attribute to the svg-tag. This is required to make SVG images
scalable in Internet Explorer browsers:
http://www.seowarp.com/blog/2011/06/svg-scaling-problems-in-ie9-and-other-browsers/

Usage:
    python patchSVGs.py [directory]
"""

import os
import re
import sys

rx_width = re.compile('<svg.*?width *?= *?"(?P<value>.*?)".*?>',
                      re.IGNORECASE | re.DOTALL)
rx_height = re.compile('<svg.*?height *?= *?"(?P<value>.*?)".*?>',
                       re.IGNORECASE | re.DOTALL)
rx_viewPort = re.compile('<svg.*?viewPort *?= *?"(?P<value>.*?)".*?>',
                         re.IGNORECASE | re.DOTALL)

rx_inkscape_secret = re.compile('\n *?inkscape:export-filename.*?= *?".*?"',
                                re.IGNORECASE | re.DOTALL)


def patchSVGs(directory):
    olddir = os.curdir
    os.chdir(dir)

    files = os.listdir(".")

    for f in files:
        if f.lower().endswith(".svg"):
            with open(f) as svg_file:
                svg_data = f.read()
                TO BE CONTINUED...

    os.chdir(olddir)


if __name__ == "__main__":
    if len(sys.argv) > 1:
        directory = sys.argv[1]
    else:
        directory = "."
    patchSVGs(directory)
