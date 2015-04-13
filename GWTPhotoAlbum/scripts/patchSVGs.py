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
rx_viewBox = re.compile('<svg.*?viewBox *?= *?"(?P<value>.*?)".*?>',
                        re.IGNORECASE | re.DOTALL)
rx_svg = re.compile('<svg.*?(?P<value>\B)>')

rx_inkscape_secret = re.compile('\n *?inkscape:export-filename.*?= *?".*?"',
                                re.IGNORECASE | re.DOTALL)


def patchSVGs(directory):
    olddir = os.curdir
    os.chdir(dir)

    files = os.listdir(".")

    for f in files:
        if f.lower().endswith(".svg"):
            with open(f) as svg_file:
                svg = f.read()
                width = rx_width.search(svg).group('value')
                height = rx_height.search(svg).group('value')
                vb_match = rx_viewBox.search(svg)
                viewBox = vb_match.group('value')
                if viewBox:
                    vb = viewBox.split(' ')
                    if width != vb[2] or height != vb[3]:
                        vb[2] = width
                        vb[3] = height
                        viewBox = ' '.join(vb)
                        a, b = vb_match.span('value')
                        svg = svg[:a] + viewBox + svg[b:]
                else:
                    a, b = rx_svg.search(svg).span('value')
                    svg = svg[:a] + ('\n   viewBox="0 0 %s %s"' %
                                     (width, height)) + svb[b:]
            TO BE CONTINUED
    os.chdir(olddir)


if __name__ == "__main__":
    if len(sys.argv) > 1:
        directory = sys.argv[1]
    else:
        directory = "."
    patchSVGs(directory)
