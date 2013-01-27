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

"""GWTPhotoAlbumCreator.py -- graphical user interface for createGWTPhotoAlbum.py
"""


import os, re
from Tkinter import *
from tkMessageBox import askyesno, showerror
import tkFileDialog
import Image, ImageTk

import createGWTPhotoAlbum
from createGWTPhotoAlbum import read_caption, write_caption, \
                                remove_old_directories, create_directories, \
                                create_noscript_html, create_index_page, \
                                assemble, deploy, THUMBNAIL, FULLSIZE
                                
try:
    import json  # python 2.6 and higher...
    def toJSON(var, indentation=2):
        return json.dumps(var, sort_keys=True, indent=indentation)
    def fromJSON(jsonString):
        return json.loads(jsonString)
except ImportError:
    def toJSON(var, indentation=2):
        return repr(var)
    def fromJSON(jsonString):
        return eval(jsonString)                               


VERSION = "0.8.9"
about_text="GWTPhotoAlbumCreater.py\n\nVersion "+VERSION+"\n\n"+\
"""
A program to create an html/AJAX photo album from a collection of images. 
See: http://code.google.com/p/gwtphotoalbum/ for more information and 
source codes


Copyright (C) 2008-2013 Eckhart Arnold (eckhart_arnold@yahoo.de).


Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License. You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations under
the License.
"""


FRAME_W, FRAME_H = 780, 560

selectableResolutions = ((320, 240), (640, 480), (1024, 768),  
                         (1440, 900), (1920, 1200))


def parseFileNameString(s):
    """Returns a list of files contained in the string."""
    l = [];  i = 0;  N = len(s)
    while i < N:
        while s[i] == " ": i += 1
        if s[i] == "{":
            k = i+1
            while k < N and s[k] != "}": k += 1
            l.append(s[i+1:k])
            i = k+1
        else:
            k = i+1
            while k < N and s[k] != " ": k += 1
            l.append(s[i:k])
            i = k+1
    return l


def compatibleFileDialogResult(listOrString):
    """Makes sure that the return value of a tk file dialog is a list
    of strings and not single string as on Windows platforms."""
    if type(listOrString) == type("") \
            or type(listOrString) == type(u""):
        flist = parseFileNameString(listOrString)            
    else:
       flist = listOrString
    return [os.path.normpath(path) for path in flist]


class ThumbDB:
    def __init__(self, w = 240, h = 240):
        self.W, self.H = w, h
        self.thumbs = {}
        self.quickThumbs = {}
        self.register = {}
        
    def get(self, filepath, width=-1, height=-1):
        if width <= 0: width = self.W
        if height <= 0: height = self.H
        if (width, height, filepath) in self.quickThumbs:
            return self.quickThumbs[(width, height, filepath)]
        if filepath not in self.thumbs \
           or width >= self.W or height >= self.H:
            try:
                img = Image.open(filepath)
                img.load()
                x = max(self.W, width)
                y = max(self.H, height)
                w, h = createGWTPhotoAlbum.adjustSize(img.size, (x, y))    
                img = img.resize((2*w, 2*h), Image.NEAREST)
                self.thumbs[filepath] = img
            except IOError:
                showerror(title = "Error:",
                          message = "could not open file " + 
                          self.album.files(self.selection))
                return None
        img = self.thumbs[filepath]
        w,h = createGWTPhotoAlbum.adjustSize(img.size, (width, height))
        img = img.resize((w, h), Image.BILINEAR)
        tkimg = ImageTk.PhotoImage(img)
        if filepath not in self.register or self.register[filepath] <= 3:
            self.quickThumbs[(width, height, filepath)] = tkimg
            self.register[filepath] = self.register.setdefault(filepath, 0)+1
        return tkimg

thumbDb = ThumbDB()


class WebAlbum:
    def __init__(self):
        self.entries = []
        self.files = {}
        self.captions = {}
        self.captionsTouched = set()        
        self.compression = (85, 70)
        sr = selectableResolutions
        self.resolutions = set(sr)
        self.gallery = True
        self.title = ""
        self.subtitle = ""
        self.bottomline = ""
        self.fullscreen = True
        self.filmstrip = True
        self.originals = False
        self.overblend = False
        self.createhtml = True
        self.addjsontohtml = True
        self.destfolder = "album"
        
    def createWebAlbum(self, logger = lambda name: 0):
        """Creates the GWTPhotoAlbum. Returns True, if everything went fine."""
        if os.path.exists(self.destfolder):
            showerror(title="GWTPhotoAlbumCreator.py - error!",
                      message="Directory:\n%s\n"%self.destfolder+
                              "already exists!\nPlease chose a different "+
                              "output directory name.")
            return False      
        save_info = createGWTPhotoAlbum.info
        info = {}; info.update(save_info)
                
        info["title"] = self.title
        info["subtitle"] = self.subtitle
        info["image clickable"] = "true"
        if self.gallery: info["presentation type"] = "gallery"
        else: info["presentation type"] = "slideshow"
        info["disable scrolling"] = "true"
        if self.fullscreen:
            if self.filmstrip:
                info["layout type"] = "fullscreen"
                info["layout data"] = "IOF"
            else:
                info["layout type"] = "fullscreen"
                info["layout data"] = "CIP"
        else:
            if self.filmstrip:
                info["layout type"] = "tiled"
                info["layout data"] = "ICF"
            else:
                info["layout type"] = "tiled"
                info["layout data"] = "ICP"
                
        if self.overblend:
            info["image fading"] = "1000"
        else:
            info["image fading"] = "-750"
                            
        filelist = [self.files[entry] for entry in self.entries]
        sizes = list(self.resolutions)
        sizes.sort()
        sizes.insert(0, THUMBNAIL)
        if self.originals:
            createGWTPhotoAlbum.create_picture_archive = True
            #createGWTPhotoAlbum.archive_quality = 80
            if self.bottomline == "":
                self.bottomline = '<a href="pictures.zip">download all pictures</a>'
            #sizes.append(FULLSIZE)
        else:
            createGWTPhotoAlbum.create_picture_archive = False            
        info["bottom line"] = self.bottomline
        
        #for key in self.captions:
        #    self.captions[key] = re.sub("\\\n", "<br />", self.captions[key])
        
        createGWTPhotoAlbum.info.update(info)
        logger("creating directory: "+ self.destfolder)
        remove_old_directories(self.destfolder)
        create_directories(self.destfolder)
        createGWTPhotoAlbum.quick_scaling = False
        logger("assembling images...")        
        assemble(filelist, self.destfolder, sizes, self.compression[0], 
                 self.compression[1], self.captions, logger)
        logger("deploying AJAX scripts in: "+ self.destfolder)        
        deploy(createGWTPhotoAlbum.deploy_pack, self.destfolder, 
               self.addjsontohtml)
        if self.createhtml:
            logger("creating static html pages for browsers without javascript.")
            create_noscript_html(filelist, self.destfolder, sizes)
        create_index_page(self.destfolder, self.createhtml, self.addjsontohtml)
        createGWTPhotoAlbum.info.update(save_info)
        return True
              
        
    def add(self, filepath):
        """Adds the image at location 'filepath'. Returns file name w/o path.
        """
        name = os.path.basename(filepath)
        if name in self.entries:
            showerror(title="GWTPhotoAlbumCreator.py - Error!",
                      message="An image named\n%s\nalready "%name+
                      "exists in the gallery!")
            return
        caption = read_caption(filepath)
        if caption: self.captions[name] = caption
        else: self.captions[name] = ""      
        self.entries.append(name)
        self.files[name] = filepath
        return name
    
    def remove(self, name):
        """Removes the image with the filename 'name' (not the full path!).
        """
        self.entries.remove(name)
        del self.files[name]
        if name in self.captions: del self.captions[name]
        if name in self.captionsTouched: self.captionsTouched.remove(name)
        
    def getCaption(self, name):
        """Returns the caption associated with the image 'name'."""
        if name in self.captions:
            return re.sub("(<br />)|(<br>)", "\n", self.captions[name])
            return self.captions[name]
        else:
            return ""
        
    def changeCaption(self, name, caption, writeToFile = False):
        """Changes the caption of image 'name' to 'caption'. 
        Optionally, writes the new caption to the image file after changeing.
        """
        assert name in self.entries
        while len(caption) > 0 and caption[-1] == "\n": caption = caption[:-1]        
        caption = re.sub("\\\n", "<br />", caption)
        if name not in self.captions or self.captions[name] != caption:
            self.captionsTouched.add(name)
            self.captions[name] = caption
            if writeToFile:
                write_caption(self.files[name], caption)
            
    def writeBackCaptions(self):
        for name in self.captionsTouched:
            write_caption(self.files[name], self.captions[name])
        self.captionsTouched = set()
            
    def strippedCaptions(self):
        """Returns the captions dictionary with all empty captions eliminated.
        """
        stripped = {}
        for key, value in self.captions.items():
            if value:
                stripped[key] = value
        return stripped
    
    def pick(self, centerImg):
        """Returns the paths names of the images: 
        centerImg-1, centerImg, centerImg+1"""
        ret = []
        if centerImg > 0: 
            ret.append(self.files[self.entries[centerImg-1]])
        else:
            ret.append(None)
        ret.append(self.files[self.entries[centerImg]])
        if centerImg < len(self.entries)-1:
            ret.append(self.files[self.entries[centerImg+1]])
        else:
            ret.append(None)
        return ret            


def ImagesToCanvas(canvas, imgPaths):
    """Adds the 3 images from imgPaths (list of filepaths) to a canvas, 
    the center image slightly bigger. Returns a list of tuples (tag, img)."""
    ret = []
    w = int(canvas["width"])/4
    h = int(canvas["height"])
    ww = [w*2/3, 2*w, w*2/3]
    hh = [h*2/3, h, h*2/3]
    for i in range(3):
        if imgPaths[i]: 
            img = thumbDb.get(imgPaths[i], ww[i], hh[i])
            tag = canvas.create_image(w/2 + i*3*w/2, h/2, image = img)
            ret.append((tag, img))
        else:
            ret.append((-1, None))
    return ret
        
        
class Card:
    def __init__(self, frame):
        self.frame = frame
    def activate(self):
        pass
    def deactivate(self):
        pass
    def validate(self):
        return True
        

class CardStack(Frame):
    def __init__(self, parent, action = None):
        Frame.__init__(self, parent)
        self.config(relief=FLAT, bd=0)
        self.buttonrow = Frame(self, relief=SUNKEN, bd=2)
        self.buttonrow.pack(side=TOP, fill=X)
        self.cardstack = Frame(self, relief=RAISED, bd=2)
        self.cardstack.pack(side=BOTTOM, fill=BOTH, expand=1)
        self.cardstack.config(width=FRAME_W, height=FRAME_H)
        self.buttons = {}
        self.cards = {}
        self.cardList = []
        self.last = None
        self.action = action

    def add(self, name, createCardFunc):
        button = Button(self.buttonrow, bd=2, text=name, relief = SUNKEN,
          command=lambda self=self,t=name: self.switch(t))
        button.pack(side=LEFT, fill=Y)
        self.buttons[name] = button
        frame = Frame(self.cardstack, bd=0, relief=FLAT)
        frame.place(relx=0.0, rely=0.0, relwidth=1.0, relheight=1.0)
        #card.pack(fill=BOTH, expand=1)
        card = createCardFunc(frame)
        self.cards[name] = card
        self.cardList.append(name)
        return card

    def currentCard(self):
        return self.last
        
    def getCard(self, name):
        return self.cards[name]

    def switch(self, name):
        if self.last:
            self.buttons[self.last]["relief"] = SUNKEN
            self.cards[self.last].deactivate()
        self.buttons[name]["relief"] = RAISED
        self.last = name
        card = self.cards[name]
        card.frame.tkraise()
        card.activate()
        if self.action:  self.action()


class AboutCard(Card):
    def __init__(self, parent, start = lambda:1, quit = lambda:1):
        Card.__init__(self, parent)
        self.parent = parent
        label = Label(parent, text='About "GWTPhotoAlbumCreator.py":')
        label.pack()        
        self.text=Text(parent)
        self.text.pack(fill=BOTH, expand=1)
        f = Frame(parent); f.pack()
        Button(f, text="Start ->", command=start).pack(side=LEFT)
        Button(f, text="Quit!", command=quit).pack(side=RIGHT)        
        
        
    def activate(self):
        self.text.delete("1.0", END)
        self.text.insert(END, about_text)   
        
    def clear(self):
        self.text.delete("1.0", END)        
        
    def logger(self, text):
        self.text.insert(END, text+"\n")
        #self.parent.update_idletasks()
        self.parent.update()        
       
       

class CaptionsCard(Card):
    def __init__(self, parent, album):
        Card.__init__(self, parent)
        self.album = album
        
        label = Label(parent, text="Add images and enter image captions:")
        label["bg"] = "#FFFF50"
        label.pack(side=TOP)        
        
        self.top = Canvas(parent)
        self.top.pack(side=TOP)
        self.top["width"] = FRAME_W-40
        self.top["height"] = FRAME_H/2
        w = int(self.top["width"])
        h = int(self.top["height"])
        self.images = [(-1, None), (-1, None), (-1, None)]   
        
        bottom = Frame(parent)
        bottom.pack(side=BOTTOM, fill=BOTH, expand=1)
        
        upper_bottom = Frame(bottom)
        upper_bottom.pack(side=TOP, fill=BOTH, expand=1)        
        
        left = Frame(upper_bottom)
        left.pack(side=LEFT, fill=BOTH, expand=1)
        
        buttonRow = Frame(left)
        buttonRow.pack(side=TOP, fill=X)
        self.add = Button(buttonRow, text="Add ...", command=self.onAdd)
        self.add["width"] = 6
        self.add.pack(side=LEFT)
        self.sort = Button(buttonRow, text="Sort by Date & Time", command=self.onSort)
        self.sort["width"] = 15
        self.sort.pack(side=LEFT)                
        self.sortAlpha = Button(buttonRow, text="Sort by Name", command=self.onSortAlpha)
        self.sortAlpha["width"] = 12
        self.sortAlpha.pack(side=LEFT)  
        self.remove = Button(buttonRow, text="Remove", command=self.onRemove)
        self.remove["width"] = 6
        self.remove.pack(side=RIGHT)        
        
        lbox = Frame(left)
        lbox.pack(side=BOTTOM, fill=BOTH, expand=1)        
        scrollbar = Scrollbar(lbox, orient=VERTICAL)
        self.listbox = Listbox(lbox, selectmode = EXTENDED, 
                               yscrollcommand=scrollbar.set)
        scrollbar.config(command=self.listbox.yview)
        scrollbar.pack(side=RIGHT, fill=Y)
        self.listbox.pack(side=TOP, fill=BOTH, expand=1)
        self.listbox.bind("<ButtonRelease-1>", lambda event: self.onSelect())              
        self.listbox.bind("<Up>", lambda event: self.onKeyUp())   
        self.listbox.bind("<Down>", lambda event: self.onKeyDown()) 
        if self.album.entries:
            self.selection = self.album.entries[0]
        else:
            self.selection = ""           

        buttonCol = Frame(upper_bottom)
        buttonCol.pack(side= RIGHT)
        self.up = Button(buttonCol, text="up", command=self.onUp)
        self.up["width"] = 5
        self.up.pack()
        self.down = Button(buttonCol, text="down", command=self.onDown)
        self.down["width"] = 5
        self.down.pack()
        
        
        lower_bottom = Frame(bottom)
        lower_bottom.pack(side=BOTTOM)
        
        lower_left = Frame(lower_bottom)
        lower_left.pack(side=LEFT)
        self.loadCapJSON = Button(lower_left, text="Load captions...", command=self.loadFromJSON)
        self.loadCapJSON.pack(side=TOP)
        self.saveCapJSON = Button(lower_left, text="Save captions...", command=self.writeToJSON)
        self.saveCapJSON.pack(side=BOTTOM)
        
        self.textedit = Text(lower_bottom)
        self.textedit["height"] = 5
        self.textedit["width"] = 70
        self.textedit["background"] = "#FFFFCF"
        self.textedit.pack(side=LEFT)
        
        right = Frame(lower_bottom)
        right.pack(side=RIGHT)
        self.reset = Button(right, text="Reset", command=self.onReset)
        self.reset.pack(side=TOP)
        self.saveIt = BooleanVar()
        self.saveCheck = Checkbutton(right, text="Add to image file",
                                     var = self.saveIt)
        self.saveCheck["width"]=20
        self.saveCheck.pack(side=BOTTOM)
        
        
    def onUp(self):
        items = [int(s) for s in self.listbox.curselection()]
        items.sort()
        i = 0
        while len(items) > 0 and items[0] == i:
            del items[0]
            i += 1
        for i in items:
            name = self.album.entries[i]
            del self.album.entries[i]
            self.album.entries.insert(i-1, name)
            self.listbox.delete(i)
            self.listbox.insert(i-1, name)
        for i in items:
            self.listbox.select_set(i-1)
        self.onSelect()            
    
    def onDown(self):
        items = [int(s) for s in self.listbox.curselection()]
        items.sort()
        items.reverse()
        i = len(self.album.entries)-1
        while len(items) > 0 and items[0] == i:
            del items[0]
            i -= 1
        for i in items:
            name = self.album.entries[i]
            del self.album.entries[i]
            self.album.entries.insert(i+1, name)
            self.listbox.delete(i)
            self.listbox.insert(i+1, name)
        for i in items:
            self.listbox.select_set(i+1)
        self.onSelect()            
        
    def onAdd(self):
        formats = [("JPEG", "*.jpg"), 
                   ("Portable Network Graphics", "*.png"),
                   ("All file types", "*")]
        flist = tkFileDialog.askopenfilenames(title="Please select images",
                                             filetypes = formats)
        flist = compatibleFileDialogResult(flist)
        for path in flist:
            if os.path.exists(path):
                name  = self.album.add(path)
                self.listbox.insert(END, name)
        if self.album.entries and not self.selection:
            self.select(self.album.entries[0])
    
    def onRemove(self):
        items = [int(s) for s in self.listbox.curselection()]
        items.sort()
        items.reverse()
        for i in items:
            name = self.album.entries[i]
            self.selection = name
            self.storeCaption()            
            self.album.remove(name)
            self.listbox.delete(i)
        if items and items[-1] < len(self.album.entries):
            self.selection = self.album.entries[items[-1]]
            self.select(self.selection)
        else:
            self.selection = ""
            
    def onSort(self):
        file_list = [self.album.files[entry] for entry in self.album.entries]
        file_list = createGWTPhotoAlbum.sort_images(file_list)
        self.album.entries = [os.path.basename(path) for path in file_list]
        for entry in self.album.entries:
            self.listbox.delete(0)        
            self.listbox.insert(END, entry)
        self.select(self.selection)

    def onSortAlpha(self):
        self.album.entries.sort()
        for entry in self.album.entries:
            self.listbox.delete(0)        
            self.listbox.insert(END, entry)   
        self.select(self.selection)             
        
        
    def select(self, name):
        self.selection = name
        self.listbox.select_set(self.album.entries.index(name))
        self.onSelect()
        
    def activate(self):
        self.listbox.delete(0, END)
        for e in self.album.entries:
            self.listbox.insert(END, e)
        if self.selection in self.album.entries:
            self.select(self.selection)
        elif len(self.album.entries) > 0:
            self.selection = self.album.entries[0]
            self.select(self.album.entries[0])
        else: 
            self.selection = ""        
        if self.selection:
            self.textedit.delete("1.0", END)
            self.textedit.insert(END, self.album.getCaption(self.selection))
            
    def storeCaption(self):
        if self.selection:
            caption = self.textedit.get("1.0", END)
            self.album.changeCaption(self.selection, caption, 
                                     False) # self.saveIt.get()    

    def deactivate(self):
        self.storeCaption()
        if self.saveIt.get() and self.album.captionsTouched and \
                askyesno(title="Question:",
                         message="Do you really want to write back\n"+
                         "changed captions to the original\n"+
                         "image files?"): 
            self.album.writeBackCaptions()
        self.textedit.delete("1.0", END)
       
    def updateCanvas(self, selected):
        self.top.delete(ALL)
        newSelection = ""        
        if selected >= 0:
            self.images = ImagesToCanvas(self.top, self.album.pick(selected))
            newSelection = self.album.entries[selected]            
        if newSelection != self.selection:
            self.storeCaption()
            self.selection = newSelection
            caption = self.album.getCaption(newSelection)
            self.textedit.delete("1.0", END)
            self.textedit.insert(END, caption)
            
    def readSelection(self):
        items = self.listbox.curselection()
        if items:
            return int(items[0])
        else:
            return -1        
    
    def onSelect(self):
        self.updateCanvas(self.readSelection())
            
    def onKeyUp(self):
        nr = self.readSelection()
        if nr > 0:
            self.updateCanvas(nr-1)

    def onKeyDown(self):
        nr = self.readSelection()
        if nr < len(self.album.entries)-1:
            self.updateCanvas(nr+1)            
            
    def onReset(self):
        if self.selection:
            caption = self.album.getCaption(self.selection)
        else:
            caption = ""        
        self.textedit.delete("1.0", END)
        self.textedit.insert(END, caption)
        
    def loadFromJSON(self):
        flist = tkFileDialog.askopenfilenames(title="Open captions file...",
                                             filetypes = [("JSON file", "*.json"),
                                                          ("All files", "*")],
                                             initialfile = "captions.json",
                                             multiple = False)
        if flist:  
            name = compatibleFileDialogResult(flist)[0]
        try:
            f = open(name)
            str = f.read()
            f.close()
            try:
                cap = fromJSON(str)
            except ValueError:
                showerror(title = "Error:",
                          message = "file: "+name+" is malformed!")                
            if type(cap) == type({}):
                self.album.captions = cap
                caption = self.album.getCaption(self.selection)
                self.textedit.delete("1.0", END)
                self.textedit.insert(END, caption)                
            else:
                showerror(title = "Error:",
                          message = "file "+name+" does not contain a\n"+
                                    "captions dictionary")                
        except IOError:
            showerror(title = "Error:",
                      message = "could not read file: "+name)
    
    def writeToJSON(self):
        name = tkFileDialog.asksaveasfilename(title="Write captions file...",
                                             filetypes = [("JSON file", "*.json"),
                                                          ("All files", "*")],
                                             initialfile = "captions.json")
        if name:  
            #name = compatibleFileDialogResult(flist)[0]
            if not name.lower().endswith(".json"):
                name += ".json"
        else: name = ""
        if name and (not os.path.exists(name) or \
           askyesno(title="Question:",
                    message="Do you really want to overwrite\n"+
                    "file: '"+name+"' ?")):
            str = toJSON(self.album.captions)
            try:
                f = open(name, "w")
                f.write(str)
                f.close()
            except IOError:
                showerror(title = "Error:",
                          message = "could not read file: "+name)                

    def validate(self):
        if len(self.album.entries) == 0:
            showerror(title="GWTPhotoAlbumCreator.py - Missing data!",
                      message="Please add some images first!")
            return False
        else:
            return True


            
class ResolutionsCard(Card):
    def __init__(self, parent, album):
        Card.__init__(self, parent)
        self.album = album
        self.resolutions = selectableResolutions
        self.rack = {}
        for res in self.resolutions: 
            self.rack[res] = BooleanVar()         
            
        label = Label(parent, text="Select destination image resolutions "+\
                                   "and compression:")
        label["bg"] = "#FFFF50"
        label.pack()               
        Label(parent, text=" ").pack()
        
        f1 = Frame(parent)
        f1.pack()
        for res in self.resolutions:
            cb = Checkbutton(f1, text="%sx%s pixel"%res,
                             var = self.rack[res])
            cb.pack(anchor=W)
            
        Label(parent, text=" ").pack()

        Label(parent, text="Compression:").pack()
        f2 = Frame(parent)
        f2.pack()
        
        f3 = Frame(f2)
        f3.pack(side=LEFT)
        Label(f3, text="LowRes").pack()
        self.lowResComp = IntVar()
        self.lowResComp.set(85)
        self.lowResScale = Scale(f3, var=self.lowResComp)
        self.lowResScale["from"] = 50
        self.lowResScale["to"] = 95
        self.lowResScale.pack()
        
        f4 = Frame(f2)
        f4.pack(side=RIGHT)
        Label(f4, text="HiRes").pack()
        self.hiResComp = IntVar()
        self.hiResComp.set(70)
        self.hiResScale = Scale(f4, var=self.hiResComp)
        self.hiResScale["from"] = 50
        self.hiResScale["to"] = 95
        self.hiResScale.pack()        
        Label(parent, text="Smaller values mean smaller image sizes\n"+
                           "but also more compression artifacts!").pack()
                           
    def activate(self):
        for res in self.resolutions:
            self.rack[res].set(res in self.album.resolutions)
        self.lowResComp.set(self.album.compression[0])
        self.hiResComp.set(self.album.compression[1])     
        
    def deactivate(self):
        self.album.compression = (self.lowResComp.get(), self.hiResComp.get())
        self.album.resolutions = set([res for res in self.rack.keys()
                                      if self.rack[res].get()])
        
    def validate(self):
        if len(self.album.resolutions) == 0:
            showerror(title="GWTPhotoAlbumCreator.py - Missing data!",
                      message="Please select one or more image resolutions!")
            return False
        else:
            return True
        

class ConfigurationCard(Card):
    def __init__(self, parent, album):
        Card.__init__(self, parent)        
        self.album = album
        
        label = Label(parent, text="Adjust the configuration, please:")
        label["bg"] = "#FFFF50"
        label.pack()               
        Label(parent, text=" ").pack()        
        
        self.gallery = BooleanVar()
        self.galleryTitle = StringVar()
        self.gallerySubtitle = StringVar()
        self.galleryBottomLine = StringVar()
        
        self.fullscreen = BooleanVar()
        self.filmstrip = BooleanVar()
        self.fullSizeImages = BooleanVar()
        self.overblend = BooleanVar()
        self.createhtml = BooleanVar()
        
        self.outputFolder = StringVar()
        
        
        f1 = Frame(parent)
        f1.pack()
        
        Checkbutton(f1, text="with gallery", var = self.gallery).pack(anchor=W)
        Label(f1, text=" ").pack()        
        f2 = Frame(f1)
        f2.pack(expand=1, fill=X)
        f = Frame(f2); f.pack(expand=1, fill=X)
        Label(f, text="gallery title: ").pack(anchor=W, side=LEFT)
        Entry(f, textvariable=self.galleryTitle, width="50").pack(side=RIGHT)
        f = Frame(f2); f.pack(expand=1, fill=X)
        Label(f, text="gallery sub-title:").pack(anchor=W, side=LEFT)        
        Entry(f, textvariable=self.gallerySubtitle, width="50").pack(side=RIGHT)
        f = Frame(f2); f.pack(expand=1, fill=X)
        Label(f,  text="gallery bottom line:").pack(anchor=W, side=LEFT)
        Entry(f, textvariable=self.galleryBottomLine, width="50").pack(side=RIGHT)
        
        Label(f1, text=" ").pack()
        #Checkbutton(f1, text="full screen slide show", var = self.fullscreen).pack(anchor=W)
        Checkbutton(f1, text="add a film strip", var = self.filmstrip).pack(anchor=W)
        #Checkbutton(f1, text="include original images", var = self.fullSizeImages).pack(anchor=W)
        Checkbutton(f1, text="overblend when changing images", var = self.overblend).pack(anchor=W)
        Checkbutton(f1, text="add javascript free version", var = self.createhtml).pack(anchor=W)
        
        Label(f1, text=" ").pack()
        f = Frame(f1); f.pack()
        Label(f, text="output folder:   ").pack(side=LEFT)
        Entry(f, textvariable=self.outputFolder, width="45").pack(side=LEFT)
        Button(f, text="Browse ...", command=self.onBrowseOutputFolder).pack(side=LEFT)
        
    def activate(self):
        self.gallery.set(self.album.gallery)
        self.galleryTitle.set(self.album.title)
        self.gallerySubtitle.set(self.album.subtitle)
        self.galleryBottomLine.set(self.album.bottomline)
        self.fullscreen.set(self.album.fullscreen)
        self.filmstrip.set(self.album.filmstrip)
        self.fullSizeImages.set(self.album.originals)
        self.overblend.set(self.album.overblend)
        self.outputFolder.set(self.album.destfolder)
        self.createhtml.set(self.album.createhtml)
    
    def deactivate(self):
        self.album.gallery = self.gallery.get()
        self.album.title = self.galleryTitle.get()
        self.album.subtitle = self.gallerySubtitle.get()
        self.album.bottomline = self.galleryBottomLine.get()
        self.album.fullscreen = self.fullscreen.get()
        self.album.filmstrip = self.filmstrip.get()
        self.album.originals = self.fullSizeImages.get()
        self.album.overblend = self.overblend.get()
        self.album.destfolder = self.outputFolder.get()
        self.album.createhtml = self.createhtml.get()
        
    def onBrowseOutputFolder(self):
        outdir = tkFileDialog.askdirectory(title="Choose output directory")
        if outdir and type(outdir) == type(""): 
            outdir = os.path.normpath(outdir)
        if outdir: self.outputFolder.set(outdir)    
        
    def validate(self):
        if self.album.gallery and not self.album.title:
            return askyesno(title="GWTPhotoAlbumCreator.py - Missing data?",
                            message="Do you really want to leave the title\n"+
                                    "of your gallery empty?")
        else:
            return True
                    

class WebAlbumCreator(Tk):
    def __init__(self):
        Tk.__init__(self)
        self.title("GWTPhotoAlbumCreator %s" % VERSION)
        # self.config(width=800, height=600)
        self.protocol("WM_DELETE_WINDOW", self.onQuit)
        
        self.changes = False        
        
        self.frame = Frame(self)
        self.frame.pack()
        self.cardStack = CardStack(self.frame, self.onSelectCard)
        self.cardStack.pack(side=TOP, fill=BOTH, expand=1)
        
        self.buttonRow = Frame(self.frame)
        self.buttonRow.pack(fill=X)
        self.next = Button(self.buttonRow, text="Next Step ->", 
                           command=self.onNext)
        self.next["width"] = 14
        self.next.pack(side=RIGHT, anchor=E)
        self.previous = Button(self.buttonRow, text="<- Previous Step", 
                               command=self.onPrev)
        self.previous["width"] = 14
        self.previous.pack(side=RIGHT, anchor=E)
        
        self.album = WebAlbum()
        self.cardStack.add("About",
                           lambda frame: AboutCard(frame, self.jumpStart, self.onQuit))
        self.cardStack.add("Images",
                           lambda frame: CaptionsCard(frame, self.album))
        self.cardStack.add("Resolutions",
                           lambda frame: ResolutionsCard(frame, self.album))
        self.cardStack.add("Configuration",
                           lambda frame: ConfigurationCard(frame, self.album))
        self.cardStack.switch("About")
                
    def onNext(self):
        name = self.cardStack.currentCard()
        i = self.cardStack.cardList.index(name)
        if i == len(self.cardStack.cardList)-1:
            self.cardStack.getCard(name).deactivate()
            for n in self.cardStack.cardList:
                if not self.cardStack.getCard(n).validate():
                    self.cardStack.switch(n)
                    break
            else:
                self.cardStack.switch("About") 
                self.cardStack.getCard("About").clear()                            
                self.cardStack.getCard("About").logger("creating web album; please wait...")
                if self.album.createWebAlbum(self.cardStack.getCard("About").logger):
                    self.changes = False
                    self.cardStack.getCard("About").logger("finished creating web album.")                   
                    self.openBrowserWithAlbum()
                else:
                    self.cardStack.getCard("About").logger("error while creating web album!")                         
                        
                    
        else:
            self.cardStack.switch(self.cardStack.cardList[i+1])

    def openBrowserWithAlbum(self):
        if self.album.addjsontohtml:
            path = os.path.join(self.album.destfolder, "index_offline.html")
            try:
                os.startfile(path)
            except AttributeError:
                if os.system("firefox "+path+" &") != 0:
                    os.system("iceweasel "+path+" &")                    
    
    def onPrev(self):
        name = self.cardStack.currentCard()
        i = self.cardStack.cardList.index(name)
        if i > 0:
            self.cardStack.switch(self.cardStack.cardList[i-1])
        else:
            self.cardStack.switch(self.cardStack.cardList[-1])            

    def onSelectCard(self):
        cardName = self.cardStack.currentCard()
        if cardName != "About": self.changes = True
        if cardName == "Configuration":
            self.next["text"] = "Finish"
        elif self.next["text"] == "Finish":
            self.next["text"] = "Next Step ->" 

    def jumpStart(self):
        self.cardStack.switch("Images")
        self.cardStack.getCard("Images").onAdd()

    def onQuit(self):
        if not self.changes or askyesno(title="GWTPhotoAlbumCreator.py - quit?",
                                        message="Do you really want to quit?"):
            self.destroy()
            self.quit()

    
main = WebAlbumCreator()
main.mainloop()
