/*
 * Copyright 2008 Eckhart Arnold (eckhart_arnold@hotmail.com).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.eckhartarnold.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

// TODO: Configurable duration, either in info.json file (done!) or even better by the user (to do!)

/**
 * Plays a slide show on an {@link ImagePanel}. 
 * 
 * <p>The slide show can be stepped through
 * via the <code>show()</code> and <code>next()</code> methods or
 * run automatically via the <code>start()</code> methods. 
 * 
 * <p>Class <code>Slideshow</code> "controls" the slide show, i.e.
 * if a <code>Slideshow</code> object is instantiated, displaying an image 
 * should always be done through a call its <code>shod()</code>-method
 * an never by calling the <code>showImage()</code> methods of the image 
 * panel directly. Instances of class slide show also register listeners
 * that "listen" to slide show events like starting, stopping, fading and
 * showing of a particular slide.
 * 
 * @author eckhart
 */
public class Slideshow implements AttachmentListener {
//  public static String[] urlList(String txt) {
//
//  }
  
  static final String  SLIDE_TOKEN = "Slide_";

  
  protected class ImageDisplayListener 
      implements ImagePanel.DisplayListener {
    public void onDisplay() {
      if (isRunning()) {
        if (current == terminal && !loop) stop();
        else timer.schedule(imagePanel.getDuration());      
      }
      // Debugger.print("fireShow: "+current);
      // GWT.log("fireShow: "+current, null);
      fireShow(current);
    }
    public void onFade() {
      fireFade();
    }
  }
  
  protected class SlideshowTimer extends Timer {
    public void run() {
      next();
    }
  }
 
  ImagePanel                        imagePanel; // package private so that it can be manipulated by the control panel    
  
  private String[]                  directories;
  private String[]                  slides;
  private HashMap<String, int[][]>  sizes;
  private int                       current = -1;
  private int                       terminal;
  private int                       firedShowNr = -1;
  private boolean                   loop = false, running = false;
  private Timer                     timer = new SlideshowTimer();  
  private ImageDisplayListener      loadListener = new ImageDisplayListener();
  private ArrayList<SlideshowListener> listenerList = 
    new ArrayList<SlideshowListener>();
  
  /**
   * Constructor for class <code>Slideshow</code>. Takes a
   * {@link ImagePanel}, where the slide show is to be displayed and
   * a list of image file names as argument.
   * 
   * @param imagePanel  the panel where the slides will be displayed 
   * @param urlList     a list of URLs pointing to the images to be displayed
   */
  public Slideshow(ImagePanel imagePanel, String[] urlList) {
    assert urlList.length > 0;
    
    this.imagePanel = imagePanel;
    this.imagePanel.addAttachmentListener(this);
    slides = urlList;
    directories = null;
    sizes = null;
  }

  /**
   * Constructor for class <code>Slideshow</code>. Takes a
   * {@link ImagePanel}, where the slide show is to be displayed and
   * a list of image names, directory names, where different directories are 
   * to contain differently sized versions of the same image and a dictionary 
   * that contains information on the image sizes of the different version of
   * each image.
   * 
   * The images listed in the <code>names</code> parameter must be contained 
   * in each of the directories in a different size, i.e. if the directories
   * are "http://.../640x480" and "http://.../800x600", a version of the same 
   * image with a different size should be contained in each of these 
   * directories. The images in one directory do not need to be all of the
   * same size, but they may vary in size and aspect ratio. The exact sizes
   * must be contained in the <code>sizes</code> tuple, which contains for
   * each directory a map that associates to each image name the exact size of
   * the image version in the corresponding directory. 
   * 
   * @param imagePanel  the panel where the slides will be displayed 
   * @param names       a list of image names. Only the base name should be
   *                    given, not the full URL, i.e. "image.jpg"
   * @param directories the URLs of one or more directories corresponding to
   *                    different resolutions.
   * @param sizes       a dictionary that assigns every image name a list
   *                    of size tuples.
   */
  public Slideshow(ImagePanel imagePanel, String[] names, 
      String[] directories, HashMap<String, int[][]> sizes) {
    assert names.length > 0;
    assert sizes.size() == names.length : 
      "names: "+names.length+", but sizes: "+sizes.size();
    
    this.imagePanel = imagePanel;
    this.imagePanel.addAttachmentListener(this); 
    slides = names;
    this.directories = directories;
    this.sizes = sizes;
  }  
  
  /**
   * Adds a <code>SlideshowListener</code> to the listener list.
   * 
   * @param listener  the <code>SlideshowListener</code> to be added 
   */
  public void addSlideshowListener(SlideshowListener listener) {
    listenerList.add(listener);
  }
  
  /**
   * Show the previous slide. If the slide show was already at the beginning
   * then the last slide if picked again. 
   */
  public void back() {
	  int sequel = current-1;
    if (sequel < 0) sequel = slides.length-1;
    show(sequel);
  }  
  
  /**
   * Returns the number of the currently displayed slide. In case the slide
   * show has not yet started -1 is returned
   * 
   * @return  number of the currently displayed slide
   */
  public int getCurrentSlide() {
    return current;
  }
  
  /**
   * Returns the time delay in milliseconds between the display of two
   * slides. 
   * @return time delay in milliseconds.
   */
  public int getDuration() {
    return imagePanel.getDuration();
  }
  
  /**
   * Returns the image panel that is used by the slide show.
   * @return the image panel of the slide show
   */
  public ImagePanel getImagePanel() {
    return imagePanel;
  }
  
  /**
   * Returns true, if the slide show is running.
   * @return  true, if slide show is running, false otherwise
   */
  public boolean isRunning() {
    return running;
  }
  
  /** 
   * Show the next slide. If the last slide of the slide show was shown 
   * before, then the first slide is displayed again.
   */
  public void next() {
	  int sequel = current+1;
    if (sequel >= size()) sequel = 0;
    show(sequel);
  }
  
  public void onLoad(Widget sender) {
    
  } 
  
  public void onUnload(Widget sender) {
    stop();
  }

  /** 
   * Removes a <code>SlideshowListener</code> from the listener list.
   * 
   * @param listener  the <code>SlideshowListener</code> to be removed
   */
  public void removeSlideshowListener(SlideshowListener listener) {
    listenerList.remove(listener);
  }    
  
  /**
   * Returns the number of slides.
   * 
   * @return the number of slides
   */
  public int size() {
    return slides.length;
  }
  
  /**
   * Sets the duration how long each slide is shown during the slide show.
   * @param duration  the duration in milliseconds
   */
  public void setDuration(int duration) {
    imagePanel.setDuration(duration); 
  }
  
  /**
   * Enables or disables infinite loops. If enabled, the slide show will not
   * stop automatically after the last slide has been shown. The default value
   * is false.  
   * @param enable  true, if "infinite" repetition of the slide show shall be
   *                enabled  
   */
  public void setLoop(boolean enable) {
    loop = enable;
  }
  
  /**
   * Shows a specific slide from the list.
   * 
   * @param slideNr  the number of the slide that is shown
   */
  public void show(int slideNr) {
    assert slideNr < slides.length && slideNr >= -1: "Slide index out of bounds!";
    if (slideNr == current) return;
    
    current = slideNr;
    
    if (current == -1) {
      imagePanel.clear();
      return;
    }
    
    if (directories == null) {
      imagePanel.showImage(slides[current], loadListener);
      if (current < size()-1) 
        Image.prefetch(slides[current+1]);
    } else {
      String[] urls = new String[directories.length];
      int[][] sizes = new int[urls.length][];
      for (int i = 0; i < urls.length; i++) {
        urls[i] = directories[i] + "/" + slides[current];
        sizes[i] = this.sizes.get(slides[current])[i];
      }
      imagePanel.showImage(urls, sizes, loadListener);
      if (current < size()-1) {
        String dir = directories[imagePanel.getSizeStep()];
        Image.prefetch(dir + "/" + slides[current+1]);
      }
    }
    
    History.newItem(SLIDE_TOKEN+(current+1), false);
  }
  
  /**
   * Shows a specific slide without fading in. 
   *  
   * @param slideNr the number of the slide that is to be shown.
   */
  public void showImmediately(int slideNr) {
    int fading = imagePanel.getFading();
    imagePanel.setFading(0);
    show(slideNr);
    imagePanel.setFading(fading);
  }
  
  /** 
   * Starts an automatic slide show. The slide show runs from the currently
   * displayed slide (or from the first slide if no slide is displayed
   * currently) to the first slide before the current slide (one round trip!).
   * The delay between the slides is the default value of 5000 ms or,
   * if the <code>start</code> method has been called before with a duration 
   * value, the duration the start method was called with last time. 
   */
  // TODO: if started from gallery, slideshow should return to the gallery when finished
  public void start() {
    stop();
    running = true;
    if (current < 0) {
      terminal = size() - 1;
      next();
    }
    else {
      terminal = current-1;
      if (terminal < 0) terminal = size()-1;
      timer.schedule(imagePanel.getDuration());
    }
    fireStart();
  }
  
//  /**
//   * Starts an automatic slide show that shows all slides beginning with 
//   * the currently displayed slide (or with the first slide, if no slide
//   * is displayed currently). 
//   *   
//   * @param duration  the delay in milliseconds before displaying the
//   *                  following slide
//   */
//  public void start(int duration) {
//    this.duration = duration;
//    start();
//  }
  
  /**
   * Stops a currently running slide show. If the slide show was not running
   * nothing happens.
   */
  public void stop() {
    if (isRunning()) {
      timer.cancel();
      running = false;
      fireStop();
    }
  }
  
  private void fireFade() {
    firedShowNr = -1;
    for (SlideshowListener listener: listenerList)
      listener.onFade();    
  }
  
  private void fireStart() {
    for (SlideshowListener listener: listenerList)
      listener.onStart();
  }
  
  private void fireStop() {
    for (SlideshowListener listener: listenerList)
      listener.onStop();
  }
  
  private void fireShow(int slideNr) {
    if (slideNr != firedShowNr) {
      firedShowNr = slideNr;
      for (SlideshowListener listener: listenerList)
        listener.onShow(slideNr);
    }
  }
}
