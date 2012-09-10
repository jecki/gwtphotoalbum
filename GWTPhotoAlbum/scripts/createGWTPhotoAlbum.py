#!/usr/bin/python2

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


"""createGWTPhotoAlbum.py - creates a GWTPhotoAlbum

Usage:
  python createGWTPhotoAlbum.py [options] picture_directory [output_directory]
  
Options:
  -h --help    print this help
  
  -s --sizes array of sizes
               an array of image sizes, 
               e.g. "[(640, 480), (1024,768), (1280,960), (-1,-1)]"
               (-1,-1) means the original image size
  
  -j --json    add an "offline" html page that includes the json data
               (needed for browsing a GWTPhotoAlbum from local hard disk 
               without running a server, otherwise not recommended,
               because it may cause browser warnings or errors)
               
  -n --noscript create additional html pages for browsers that do not 
               support javascript (strongly recommended!)
               
  -q --quick   use a faster but less acurate method to scale images
                
  -p --package=filename 
               file name of the deployment package (a zip file that contains
               all the compiled GWTPhotoAlbum scripts and other necessary data)
               
  -b --batch   batch mode, i.e. no queries if any directories are going 
               to be deleted. Be careful!
               
  -t --sort    sorts images by date and time before creating the album
  
  -a --archive additionally create a zip archive that contains all images for 
               download. 'quality' [0..100] defines the compression level used 
               for the zip archive. If quality = 100 then the source images
               will not be re-compressed               
"""

import sys, os, re, shutil, getopt, zipfile, codecs, datetime, tempfile
try:
    from zipfile import ZipFile
except (ImportError):
    from zipfile_py2x import ZipFile # use custom version if the system's python interpreter lacks this module
from PIL import Image, ImageFile, ExifTags
try:
    import pyexiv2
except ImportError:
    print "Warning: Could not find pyexiv2 library.\n"+\
          "Program will be unable to read captions from image files!\n" 

try:
    import json
    def toJSON(var, indentation=2):
        return json.dumps(var, sort_keys=True, indent=indentation)
    def fromJSON(jsonString):
        return json.loads(jsonString)
except ImportError:
    def toJSON(var, indentation=2):
        return repr(var)
    def fromJSON(jsonString):
        return eval(jsonString)
    
#try: # does not work for some reason...
#    from multiprocessing import Pool
#except ImportError:
class Pool:
    def __init__(self, numWorkers=1):
        pass
    def apply_async(self, func, arglist=[]):
        func(*arglist)
    def close(self):
        pass
    def join(self):
        pass
    
        
THUMBNAIL = (160, 160)
FULLSIZE = (-1,-1)        
        
source_dir = "example_pictures"
file_list  = []
dest_dir   = "album"
deploy_pack = os.path.join(os.path.abspath(sys.path[0]), "GWTPhotoAlbum-Deploy.zip")
sizes_list = [THUMBNAIL, (480, 320), (960, 640),  (1440, 900), (1920, 1200)]
main_htmlfile = "GWTPhotoAlbum.html"
modify_html = False
add_noscript_html = False
quick_scaling = True
batch_mode = False
add_zipPackage = False
sort_by_datetime = False
create_picture_archive = False
archive_quality = 100
gallery_horizontal_padding = 70
gallery_vertical_padding = 30
directories_json, filenames_json, captions_json, resolutions_json, info_json = "","","","",""

info = {"title":"", 
        "subtitle": "", 
        "bottom line":"",
        "display duration" : "5000", 
        "image fading": "-750",         
        # "image clickable": "false", # has been removed October 2011!
        "layout type":"fullscreen",
        "layout data":"PIC",
        "add mobile layout":"true",
        "presentation type":"gallery",
        "disable scrolling": "true",
        "thumbnail width" : str(THUMBNAIL[0]),
        "thumbnail height" : str(THUMBNAIL[1]),
        "gallery horizontal padding" : str(gallery_horizontal_padding), 
        "gallery vertical padding" : str(gallery_vertical_padding) }
captions = {}

ImageFile.MAXBLOCK = 1024*1024

DESC_TAG = 0x010e
COMMENT_TAG = 0x9286


def adjustSize(imageSize, targetSize):
    """-> the size (x,y) the image must be scaled to in order to fit into the
    target size, while keeping the aspect ratio.
    """
    sx, sy = imageSize
    tx, ty = targetSize
    x = tx
    y = sy*tx/sx
    if y > ty:
        y = ty
        x = sx*ty/sy
    return x,y

def resizeAndSave(im, w, h, method, filename, compression):
    """Resizes image 'im' to size w,h with scaling method 'method' and saves
    the resized image to file 'filename' with quality 'compression'."""
    # print("creating: "+filename+ "...")
    iw, ih = im.size
    if quick_scaling and iw >= 2*w and ih >= 2*h:
        dest = im.resize((2*w, 2*h), Image.NEAREST)
        dest = dest.resize((w,h), method)
    else:
        dest = im.resize((w, h), method) 
    dest.save(filename, "JPEG", optimize=1, quality=compression, progressive=1)    

def toTuple(nestedList):
    """Converts a nested list into a nested tuple."""
    for i in range(len(nestedList)):
        item = nestedList[i]
        if isinstance(item, list) or isinstance(item, tuple):
            nestedList[i] = toTuple(item)
    return tuple(nestedList)

resDict = {}
def checkInRes(res):
    def rescode(i):
        s = str(i)
        if len(s) >= 2: 
            return s
        else:
            return "0"+s
    global resDict
    t = toTuple(res)
    if t not in resDict:
        resDict[t] = "res"+rescode(len(resDict))
    return resDict[t]
    
def invertedResDict():
    """->inverted resolutions dictionary: set name -> resolutions list"""
    invDict = {}
    for key, value in resDict.items():
        invDict[value] = key
    return invDict


def remove_old_directories(outputdir):
    def del_tree(path):
        if os.path.isdir(path):
            files = os.listdir(path)
            for f in files:
                p = os.path.join(path, f)
                del_tree(p)
            os.rmdir(path)
        else:
            os.remove(path)

    outputdir = os.path.normpath(outputdir)
    if os.path.exists(outputdir):
        if not batch_mode:
            answer = raw_input("Really delete %s [yes/no] ? "%outputdir)
            if answer.lower() != "yes": sys.exit(2)
        print("Removing directory tree: %s"%outputdir)
        del_tree(outputdir)

        
def create_directories(outputdir):
    try:
        outputdir = os.path.normpath(outputdir)
        outputdir = os.path.join(outputdir, "slides")
        stack = []
        while outputdir and not os.path.exists(outputdir):
            outputdir, basename = os.path.split(outputdir)
            stack.append(basename)
        while len(stack) > 0:
            outputdir = os.path.join(outputdir, stack.pop())
            print("Creating directory: "+outputdir)
            os.mkdir(outputdir)
    except OSError:
        print "Error while creating output directories"
        return


def read_metadata(filename):
    try:
        try:
            exif = pyexiv2.Image(filename)
            exif.readMetadata()
        except AttributeError:  # newer version of pyexiv2
            exif = pyexiv2.ImageMetadata(filename)
            exif.read()            
    except NameError:
        print "image metadata for file "+filename+" could not be read!"
        return 0
    except IOError:
        print "IO error while reading metadat of file "+filename
        return 0    
    return exif

def get_metadata_keys(metadata):
    if metadata:
        try:
            return metadata.exifKeys()
        except AttributeError:
            return metadata.exif_keys
    else:
        return {}
    
def read_datetime(filename):
    metadata = read_metadata(filename)
    keys = get_metadata_keys(metadata)
    if "Exif.Photo.DateTimeOriginal" in keys:
        dt = metadata["Exif.Photo.DateTimeOriginal"]
    elif "Exif.Image.DateTime" in keys:
        dt = metadata["Exif.Image.DateTime"]
    else:
        dt = datetime.datetime(1972, 11, 11, 12, 0, 0)
    # again: pyexiv2 interversion incompatibilities
    if type(dt) != type(datetime.datetime(1972, 11, 11)):
        dt = dt.value        
    return dt
    

def sort_images(file_list):
    """Returns the list of images sorted by date and time."""
    sl = [(read_datetime(im), im) for im in file_list]
    sl.sort()
    fl = [entry[1] for entry in sl]
    return fl
    

def read_caption(filename):
    try:
        exif = read_metadata(filename)
        if exif:
            comment = unicode(exif.getComment(), "utf-8", "ignore")
            if comment:
                if comment[-1] == "\n": comment = comment[:-1]
                comment = re.sub("\\\\n", u"<br />", comment)
            return comment
    except AttributeError:
        print "Could not extract exif comment from file "+filename        
    except NameError:
        print "Caption for file "+filename+" could not be read!"
    return ""


def write_caption(filename, caption):
    try:
        try:
            exif = pyexiv2.Image(filename)
            exif.readMetadata()
        except AttributeError: # newer version of pyexiv2
            exif = pyexiv2.ImageMetadata(filename)
            exif.read()
        caption = re.sub("(<br />)|(<br>)", "\n", caption)
        exif.setComment(caption.decode("utf8", "ignore").encode("utf8","ignore"))
    except NameError:
        print "Caption for file "+filename+" could no be written!"
        return ""


#index.html if no "noscript" pages are generated
checkJSPageHTML = u"""<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="de" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>GWTPhotoAlbum Redirect Page</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <meta name="generator"  content="GWTPhotoAlbum creator" />
  <script type="text/javascript">
  <!--
  window.location = "$ALBUM"
  //-->
  </script>
</head>
<body>
<div style="text-align:center;">
<h1>GWTPhotoGallery</h1>
<p>Sorry, but your browser does not seem to support javascript. Without
javascript the photo album cannot be displayed. If you would like to
try it none the less, please click on the link below:
</p>
<br />
<p><a href="$ALBUM">javascript version (recommended)</a></p>
</div>
</body>
</html>
"""

# index.html if "noscript" pages are generated as well
redirectPageHTML = u"""<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="de" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>GWTPhotoAlbum Redirect Page</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <meta name="generator"  content="GWTPhotoAlbum creator" />
  <script type="text/javascript">
  <!--
  window.location = "$ALBUM"
  //-->
  </script>
  <noscript>
  <meta http-equiv="REFRESH" content="0;noscript_$ENTRY.html">
  </noscript>
</head>
<body>
<div style="text-align:center;">
<h1>GWTPhotoGallery</h1>
<p>Sorry, but your browser does not support automatic redirection to another html-page.
Please, use one of the links below to get to the photo album:
</p>
<br />
<p><a href="$ALBUM">javascript version (recommended)</a></p>
<p><a href="noscript_$ENTRY.html">javascript free version</a></p>
</div>
</body>
</html>
"""

galleryHTML = u"""<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="de" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>$TITLE</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <meta name="generator"  content="GWTPhotoAlbum creator" />  
  <link rel="stylesheet" type="text/css" href="GWTPhotoAlbum.css" />     
</head>
<body>
<h1 style="text-align:center;">$TITLE</h1>
<h2 style="text-align:center;">$SUBTITLE</h2>
<table align="center" cellpadding="0" cellspacing="0">
$GALLERYROWS
</table>
<p style="text-align:center;">$BOTTOMLINE</p>
<br /><br />
<div style="text-align:center;">
  <a style="color:#4090FF; text-size:10px;" href="GWTPhotoAlbum_xs.html#Gallery">javascript version</a>
</div> 
</body>
</html>
"""


galleryCellHTML = u"""<td align="center" valign="middle" width="$THUMB_W" height="$THUMB_H">
  <a href="$LINK">
  <img src="$IMG" alt="$IMG" class="galleryImage" />
  </a>
</td>
"""
galleryCellHTML = re.sub("\$THUMB_W", str(THUMBNAIL[0]+2*30), galleryCellHTML)
galleryCellHTML = re.sub("\$THUMB_H", str(THUMBNAIL[1]+2*30), galleryCellHTML)


galleryEmptyCellHTML = u"""<td align="center" valign="middle" width="$THUMB_W" height="$THUMB_H">

</td>
"""
galleryEmptyCellHTML = re.sub("\$THUMB_W", str(THUMBNAIL[0]+2*30), galleryEmptyCellHTML)
galleryEmptyCellHTML = re.sub("\$THUMB_H", str(THUMBNAIL[1]+2*30), galleryEmptyCellHTML)


imagePageHTML = u"""<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html lang="de" xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>$TITLE</title>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
  <meta name="generator"  content="GWTPhotoAlbum creator" />
  <link rel="stylesheet" type="text/css" href="GWTPhotoAlbum.css" />
</head>
<body class="imageBackground">
    <div style="text-align:center;">
    $PANEL
    </div>
    <div style="text-align:center; padding:5px;">
    $IMAGE
    </div>
    <div class="caption-tiled" style="text-align:center;">
    $CAPTION
    </div>
    <br /><br /><br />
    <div style="text-align:center;">
    <a style="color:#40A0FF; text-size:9px;" href="GWTPhotoAlbum_xs.html#Slide_$SLIDENR">javascript version</a>
    </div>
</body>
</html>
"""

panelHTML = u"""<table align="center" cellspacing="0" cellpadding="0">
<tr>
  <td>
    <a href="$BACKLINK">
    <img src="icons/back_32.png" alt="back" width="16" height="32" border="0" />
    </a>
  </td>
  <td>
  $GALLERYBUTTON
  </td>
  <td>
    <a href="$NEXTLINK">  
    <img src="icons/next_32.png" alt="back" width="16" height="32" border="0" />
    </a>
  </td>
</tr>
</table>
"""

galleryButtonHTML = u"""<a href="noscript_gallery.html">
<img src="icons/gallery_32.png" alt="back" width="16" height="32" border="0" />
</a>
"""

imageWithLinkHTML = u"""<a target="_blank" href="original_size/$IMG">
<img src="$SIZE/$IMG" alt="$IMG not found!" style="pointer:cursor; border:0px none #000000;" />
</a>
"""

imageHTML = u"""<img src="$SIZE/$IMG" alt="$IMG not found!" style="border:0px none #000000;" />"""



def create_noscript_html(filenames, outputdir, sizes):
    global captions
    filenames = [os.path.basename(name) for name in filenames]
    cmpv = 1000000
    for s in sizes:
        d = abs(s[0]*s[1] - 800*600)
        if d < cmpv:
            bestsize = s; cmpv = d
    sizeprefix = "slides/%ix%i"%bestsize
    hasGallery = info["presentation type"] == "gallery"
    hasImageLinks = False # info["image clickable"] == "true"
    def createGallery():
        cells = []
        for i in range(len(filenames)):
            imgName = filenames[i]
            slidessub = "slides/"+str(THUMBNAIL[0])+"x"+str(THUMBNAIL[1])+"/"
            cell = re.sub("\$IMG", slidessub+imgName, galleryCellHTML)
            cell = re.sub("\$LINK", "noscript_image%i.html"%i, cell)
            cells.append(cell)
        rows = []
        cells.reverse()
        while len(cells) > 0:
            rows.append("<tr>")
            for i in range(4):
                if len(cells) > 0:
                    rows.append(cells.pop())
                else:
                    rows.append(galleryEmptyCellHTML)
            rows.append("</tr>")
        galleryrows = "\n".join(rows)
        gallery = re.sub("\$TITLE", info["title"], galleryHTML)
        gallery = re.sub("\$SUBTITLE", info["subtitle"], gallery)
        gallery = re.sub("\$BOTTOMLINE", info["bottom line"], gallery)
        gallery = re.sub("\$GALLERYROWS", galleryrows, gallery)
        return gallery
    def createImagePage(imgNr):
        b = imgNr-1 
        n = imgNr+1
        if b < 0: 
            b = len(filenames)-1
        if n >= len(filenames): 
            n = 0

        backLink="noscript_image%i.html"%b
        nextLink="noscript_image%i.html"%n
        imgName=filenames[imgNr]

        if hasImageLinks: chunk = imageWithLinkHTML
        else: chunk = imageHTML
        # img = re.sub("\$IMG", imgName, chunk) # this is never needed!?
        if hasGallery:
            chunk = re.sub("\$GALLERYBUTTON", galleryButtonHTML, panelHTML)
        else:
            chunk = re.sub("\$GALLERYBUTTON", "", panelHTML)
        chunk = re.sub("\$BACKLINK", backLink, chunk)
        panel = re.sub("\$NEXTLINK", nextLink, chunk)

        chunk = re.sub("\$SIZE", sizeprefix, imageHTML)
        image = re.sub("\$IMG", imgName, chunk)

        chunk = re.sub("\$TITLE", "Image_%i"%(imgNr+1), imagePageHTML)
        chunk = re.sub("\$SLIDENR", str(imgNr+1), chunk)
        chunk = re.sub("\$PANEL", panel, chunk)
        if imgName in captions:
            chunk = re.sub("\$CAPTION", captions[imgName], chunk)
        else:
            chunk = re.sub("\$CAPTION", "", chunk)
        page = re.sub("\$IMAGE", image, chunk)
        return page
    current_dir = os.getcwd()
    os.chdir(outputdir)
    if hasGallery:
        f = codecs.open("noscript_gallery.html", "w", "utf-8")
        try:
            page = createGallery()
            f.write(page)
        finally:
            f.close()
    for i in range(len(filenames)):
        f = codecs.open("noscript_image%i.html"%i, "w", "utf-8")
        try:
            page = createImagePage(i)
            f.write(page)
        finally:
            f.close()
    os.chdir(current_dir)


def create_index_page(outputdir, noscript, offline_page):
    current_dir = os.getcwd()    
    os.chdir(outputdir)
    if noscript:
        indexPage = redirectPageHTML
        if info["presentation type"] == "gallery":
            indexPage = re.sub("\$ENTRY", "gallery", indexPage)
        else:
            indexPage = re.sub("\$ENTRY", "image0", indexPage)
    else:
        indexPage = checkJSPageHTML
        
    if offline_page: # assume offline output
        offline_indexPage = re.sub("\$ALBUM", 
                                   main_htmlfile[:-5]+"_fatxs.html", 
                                   indexPage)
        f = open("index_offline.html", "w")
        try:
            f.write(offline_indexPage)
        finally:
            f.close()
        
    indexPage = re.sub("\$ALBUM", main_htmlfile[:-5]+"_xs.html", indexPage)
    f = open("index.html", "w")
    try:
        f.write(indexPage)
    finally:
        f.close()    
    os.chdir(current_dir)


def printLogger(filename):
    print("converting file "+filename)

def assemble(filenames, outputdir, sizes, lowResComp=85, hiResComp=70, 
             customCaptions={}, logger = printLogger):
    global directories_json, filenames_json, captions_json, resolutions_json, info_json, captions
    current_dir = os.getcwd()
    os.chdir(os.path.join(outputdir, "slides"))
    
    subdirs = []
    for x,y in sizes:
        if x < 0 or y < 0:
            name = "original_size"
        else:
            name = str(x) + "x" + str(y)
        os.mkdir(name)
        subdirs.append(name)
    directories_json = toJSON(subdirs)
    f = open("directories.json", "w")
    try:
        f.write(directories_json)
    finally:
        f.close()
    
    if quick_scaling:
        scaling_method = Image.BICUBIC # alt: Image.BICUBIC or Image.BILINEAR
    else:
        scaling_method = Image.ANTIALIAS
    
    files = []
    captions.update(customCaptions)
    resolutions = {}
    if create_picture_archive:
        zipfile = ZipFile("../pictures.zip", "w") 
    
    pool = Pool()
           
    for name in filenames:
        logger(name)
        
        basename = os.path.basename(name)         
        im = Image.open(name)
        
        if create_picture_archive:
            if archive_quality < 100:
                fname = os.path.join(tempfile.gettempdir(), 
                                     "gwtphotoalbum_temporary_image.jpg")
                im.load()
                #ImageFile.MAXBLOCK = 1000000
                im.save(fname, "JPEG", optimize=1, quality=archive_quality,
                        progressive=1)
                zipfile.write(fname, os.path.join(os.path.basename(outputdir), 
                                                  basename))         
                os.remove(fname)       
            else:
                zipfile.write(name, os.path.join(os.path.basename(outputdir), 
                                                 basename))
        
        if not customCaptions:
            comment = read_caption(name)
            if comment: captions[basename] = comment

        files.append(basename)
        
        compression = {}
        comp = float(lowResComp)
        if len(sizes) > 1:
            delta = float((hiResComp-lowResComp))/(len(sizes)-1)
        else: delta = 0.0
        for dim in sizes:
            compression[dim] = int(comp+0.5)
            comp += delta

        pool = Pool()
        res = []            
        for dimensions, dirname in zip(sizes, subdirs):
            if tuple(dimensions) == tuple(FULLSIZE):
                shutil.copy(name, dirname);
                res.append([im.size[0], im.size[1]])
            else:
                x, y = dimensions
                w, h = adjustSize(im.size, (x, y))
                
                filename = os.path.join(dirname, basename)
                pool.apply_async(resizeAndSave, (im, w, h, scaling_method,
                                                 os.path.join(dirname, basename), 
                                                 compression[dimensions]))
                #resizeAndSave(im, w, h, scaling_method, 
                #              os.path.join(dirname, basename), 
                #              compression[dimensions])                
                res.append([w,h])
            #if tuple(dimensions) == tuple(sizes[-1]) \
            #        and basename in captions:
            #    pass
                #write_caption(os.path.join(dirname, basename), 
                #              captions[basename])
        pool.close()
        pool.join()          
        resolutions[basename] = checkInRes(res)

    if create_picture_archive:    
        zipfile.close()
    
    pool.close()
    pool.join()
    
    filenames_json = toJSON(files)    
    f = open("filenames.json", "w")
    try:
        f.write(filenames_json)
    finally:
        f.close()
    
    strippedCaptions = {}
    for fn in files:
        if fn in captions:
            strippedCaptions[fn] = captions[fn]
                    
    captions_json = toJSON(strippedCaptions)    
    f = open("captions.json", "w")
    try:
        f.write(captions_json)
    finally:
        f.close()        

    res_jsn = [invertedResDict(), resolutions]
    resolutions_json = toJSON(res_jsn, None)    
    f = open("resolutions.json", "w")
    try:
        f.write(resolutions_json)
    finally:
        f.close()
    
    info_json = toJSON(info)
    f = open("info.json", "w")
    try:
        f.write(info_json)
    finally:
        f.close()
        
    os.chdir(current_dir)   



def addJSONtoMainPage(filename, addJSON = True, outfile = None):
    """Packs the description of the photo album that is usually kept in
    separate JSON files into the main page, enclosed by <script> tags.
    As this does not conform to the HTML standard, it should be used with
    care. (It is best not to use it at all!)
    'filename' is the name of the PhotoAlbum main html page. If 'outfile'
    is given and(!) 'addJSON' is True, the "patched" page will be written 
    to 'outfile'. If 'addJSON' is 'False', 'outfile' will be ignored and 
    any existing JSON-<script>-tags  will be removed, but none will be added.
    """
    f = open(filename, "r")    
    try:
        inputData = f.readlines()
    except IOError, (errno, strerr): 
        print("Error %s %s while reading file: "%(errno, strerr)+filename)
        f.close()
        return
    f.close()
        
    if not (addJSON and outfile):
         os.rename(filename, filename+".tmp");
    
    output = []
    oldJSONBlock = False
    for line in inputData:
        if oldJSONBlock:
            if line.find("</script>") >= 0:
                oldJSONBlock = False
        elif line.find("<script") >= 0 and line.find("display:none") >= 0:
            oldJSONBlock = True
        elif line.find('src="GWTPhotoAlbum/GWTPhotoAlbum.nocache.js"') >= 0:
            newLine = re.sub("GWTPhotoAlbum/GWTPhotoAlbum.nocache.js", 
                "GWTPhotoAlbum_xs/GWTPhotoAlbum_xs.nocache.js", line)
            output.append(newLine)
        elif addJSON and line.find("<meta") >= 0 and line.find('"info"') >= 0:
            pass
        elif addJSON and line.find("</head>") >= 0:
            def addJSONBlock(name, block):
                output.append('<script id="'+name+'" style="display:none;">\n')
                output.append(block)
                output.append('</script>\n')
            addJSONBlock("info.json", info_json)
            addJSONBlock("directories.json", directories_json)
            addJSONBlock("filenames.json", filenames_json)
            addJSONBlock("resolutions.json", resolutions_json)
            addJSONBlock("captions.json", captions_json)
            output.append('<meta name="info" content="info.json">\n')
            output.append(line)
        else:
            output.append(line)
    
    if addJSON and outfile:
        f = open(outfile, "w")
    else:
        f = open(filename, "w")
    try:
        f.writelines(output)
        if not (addJSON and outfile):
            os.remove(filename+".tmp")        
    except IOError, (errno, strerr):
        print("Error %s %s while writing file: "%(errno, strerr)+filename)
        if addJSON and outfile:
            os.remove(outfile)
        else:
            os.remove(filename)
            os.rename(filename+".tmp", filename)
        f.close()
        return
    f.close()



def deploy(pack, dir, modify):
    pack = os.path.abspath(pack)
    current_dir = os.getcwd()
    os.chdir(dir)
    f = zipfile.ZipFile(pack, "r")    
    try:
        f.extractall()
    finally:
        f.close()
    addJSONtoMainPage(main_htmlfile[:-5]+"_xs.html", modify, 
                      main_htmlfile[:-5]+"_fatxs.html")
    os.chdir(current_dir)


if __name__ == "__main__":
    opts, args = getopt.getopt(sys.argv[1:], "hbjnqts:p:a", 
            ["help", "batch", "json", "noscript", "quick", "sort", "sizes=", "package=", "archive"])
    
    for opt, arg in opts:
        if opt in ("-h", "--help"):
            print __doc__
            sys.exit(2)
        elif opt in ("-s", "--sizes"):
            sizes_list = [tuple(entry) for entry in fromJSON(arg)]
        elif opt in ("-p", "--package"):
            deploy_pack = arg 
        elif opt in ("-j", "--json"):
            modify_html = True
        elif opt in ("-n", "--noscript"):
            add_noscript_html = True
        elif opt in ("-q", "--quick"):
            quick_scaling = True
        elif opt in ("-b", "--batch"):         
            batch_mode = True
        elif opt in ("-t", "--sort"):
            sort_by_datetime = True   
        elif opt in ("-a", "--archive"):
            create_picture_archive = True
            #archive_quality = int(arg)
            if archive_quality < 0 or archive_quality > 100:
                print("argument for option 'archive' must be > 0 and < 100!")
                sys.exit(2)         
        else:
            print "unrecognized option: "+opt
            print __doc__
            sys.exit(2)
            
    if len(args) > 0:
        source_dir = args[0]
        if len(args) > 1:
            dest_dir = args[1]
    if os.path.isdir(source_dir):
        file_list = [os.path.abspath(os.path.join(source_dir,fn)) \
                     for fn in os.listdir(source_dir) \
                     if fn[-4:].lower() in (".jpg", ".png")]
        file_list.sort()
    else:
        f = open(source_dir)
        file_list = [fn for fn in f]
    
    if len(file_list) == 0:
        print("No pictures found in directory: %s"%source_dir)
        sys.exit(2)
      
    if not os.path.exists(deploy_pack):
        print("Could not find deployment pack: %s"%deploy_pack)
        sys.exit(2)
      
    remove_old_directories(dest_dir)  
    create_directories(dest_dir)
    if sort_by_datetime:
        file_list = sort_images(file_list)
    assemble(file_list, dest_dir, sizes_list)    
    deploy(deploy_pack, dest_dir, modify_html)
    if add_noscript_html:
        create_noscript_html(file_list, dest_dir, sizes_list)
    create_index_page(dest_dir, add_noscript_html, modify_html)

