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

import java.lang.String;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays a photo on an absolute panel. 
 * 
 * <p>The image is scaled so as to use as much space of the
 * panel as possible without distorting the aspect ratio of the image. 
 * When the image is exchanged the old image is faded out while
 * the new image is faded in smoothly.
 * 
 * @author eckhart
 */
public class ImagePanel extends Composite implements HasClickHandlers, 
    HasMouseMoveHandlers, HasMouseWheelHandlers, SourcesAttachmentEvents, 
    ResizeListener {
  
  /**
   * Interface for event listeners of <code>ImagePanel</code>.
   */
  public interface DisplayListener {
    /**
     * Called by the time a new image is
     * fully displayed (i.e. loaded and faded in).
     */
    void onDisplay();
    
    /**
     * Called when image starts fading. <code>onFade()</code>
     * will not be called if fading is disabled or skipped for some other 
     * reason!
     */
    void onFade();
  }
  
  private class ImageLoadHandler  implements LoadHandler {
    private int           fading; 
    private NotifyingFade fader = null;
    private Widget        lastSender = null;
    private Duration      loadDuration = new Duration();
    
    public ImageLoadHandler(int fading) {
      this.fading = fading;
    }
    
    public boolean isLoaded() {
      return lastSender != null;
    }
    
    public void onLoad(LoadEvent event) {
      Widget sender = (Widget) event.getSource();
      if (sender == active && sender != lastSender) {  // for some reason LOAD events are sometimes fired twice (due to bad IE compatitibilty tests?)!?
        lastSender = sender;
        // adjustSize((Image) sender); <- already done by exchangeImage()!
        if (fading == 0) {
          Fade.setOpacity((Image) sender, 1.0);
          if (passive != null) Fade.setOpacity(passive, 0.0);
          fireDisplay();
        } else if (fading > 0) {
          startFading(this);
        } else { // fading < 0
          if (fader != null && fader.isComplete()) {
            fireDisplay();
          }
        }
        // use smaller sized images in the future if connection is slow...
        int elapsed = loadDuration.elapsedMillis();
        GWT.log("Duration : " + String.valueOf(elapsed));
        if (elapsed > duration) {
          GWT.log(String.valueOf(elapsed) + "; " + String.valueOf(duration));
          if (sizeBias < sizes.length) sizeBias++;
        } else {
          while (sizeBias > 0 && elapsed < duration / 3) {
            sizeBias--; 
            elapsed = elapsed * 3;
          }
        }
        GWT.log("sizeBias : " + String.valueOf(sizeBias));                  
      }
    }
    
    public void connectFader(NotifyingFade fader) {
      if (this.fader == null) {
        this.fader = fader;
        if (fader.isComplete() && isLoaded()) fireDisplay();
      }
    }
  }
  
  
  private class ImageErrorHandler implements ErrorHandler {
    public void onError(ErrorEvent event) {
      Image img = (Image) event.getSource();
      // DEBUG
      // Debugger.print("Image loading error:\n  "+img.getUrl());
      // DEBUG
      GWT.log("ImagePanel.ImageErrorHandler.onError:\n  "+img.getUrl(), null);
    }    
  }
  
  
  private class NotifyingFade extends Fade {
    private ImageLoadHandler loadListener;
    private boolean completed = false;
    
    NotifyingFade(Widget widget, ImageLoadHandler loadHandler) {
      super(widget, 0.0, 1.0, FADE_IN_STEPS);
      this.loadListener = loadHandler;
      loadHandler.connectFader(this);
    }
    
    public boolean isComplete() {
      return completed;
    }
    
    @Override
    protected void onComplete() {
      super.onComplete();
      completed = true;
      if (loadListener.isLoaded()) fireDisplay();
    }
  }
  
  /**
   * A Fade class for fading out the old image that triggers fading in the new
   * image only after the old image has faded out completely.
   */
  private class ChainedFade extends Fade {
    private Fade nextFade;
    
    ChainedFade(Widget widget, Fade nextFade, double steps) {
      super(widget, 1.0, 0.0, steps);
      this.nextFade = nextFade;
    }
    
    @Override
    protected void onComplete() {
      super.onComplete();
      nextFade.run(Math.abs(fading));
    }
  }
  
  private static final double  FADE_IN_STEPS = 0.10;
  private static final double  FADE_OUT_STEPS = 0.13;

  /** 
   * The currently active image. When a new photo is loaded the image that
   * was displayed previously is declared passive and a new active image
   * is created in the front that takes the new photo and is faded in. 
   */
  protected Image         active;
  /** 
   * The image that was displayed out and is either already invisible, 
   * i.e. faded out, or just about to be faded out.
   */
  protected Image         passive;
  /**
   * The main widget of the image panel. The images are placed on panel and 
   * their size adjusted.
   */
  protected AbsolutePanel	panel;
  /**
   * An envelope panel that wraps the <code>AbsolutePanel</code>. This is
   * necessary for working around some browser differences in connection with
   * resizing.
   */
  protected SimplePanel   envelope;

  private   int           duration = 5000; // duration in msecs for which the an image will be displayed
  private   int           fading = -750; // a negative value indicates that fading in and fading out should take place in sequence
  
	private   NotifyingFade fadeIn;
	private   Fade          fadeOut;
	private   int			      panelW, panelH;

	private   int           sizeStep = -1;      // a negative value means: no multiple image sizes present
  private   int           sizeBias = 0;       // bias in case of slow connection
	private   String[]      imageNames;
  private   int[][]       sizes;
  
  private ArrayList<AttachmentListener> attachmentListeners;
  private DisplayListener   displayListener;
  private boolean           hasClickHandlers = false;
  private ImageErrorHandler stdImageErrorHandler = new ImageErrorHandler();
	
	/**
   * Constructor of class <code>ImagePanel</code>.  Before the flip panel becomes
   * visible, it must be added somewhere to the DOM tree (e.g. to the root panel)
   * <em>and</em> the <code>onResized</code> method must be called.
   */
  public ImagePanel(ImageCollectionInfo collection) {
    HashMap<String, String> info = collection.getInfo();
//    for (String key: info.keySet()) {
//      GWT.log(key+": "+info.get(key));
//    }
    String numStr = info.get("display duration");
    if (numStr != null && !numStr.isEmpty()) {
      duration = Integer.parseInt(numStr);
      assert duration > 500;
    }
    numStr = info.get("image fading");
    if (numStr != null && !numStr.isEmpty()) {
      fading = Integer.parseInt(numStr);
    }
    envelope = new SimplePanel();
  	panel = new AbsolutePanel();
  	panel.addStyleName("imageBackground");
  	envelope.setWidget(panel);
    // envelope.setSize("100%", "100%");
  	initWidget(envelope);
  	setSize("100%", "100%");
    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONMOUSEWHEEL);
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SourcesAttachmentEvents#addAttachmentListener(de.eckhartarnold.client.AttachmentListener)
   */
  public void addAttachmentListener(AttachmentListener listener) {
    if (attachmentListeners == null)
      attachmentListeners = new ArrayList<AttachmentListener>();
    attachmentListeners.add(listener);
  }
  

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
   */
  public HandlerRegistration addClickHandler(ClickHandler handler) {
    hasClickHandlers = true;
    return addHandler(handler, ClickEvent.getType());
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler(com.google.gwt.event.dom.client.MouseMoveHandler)
   */
  public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
    return addHandler(handler, MouseMoveEvent.getType());
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler(com.google.gwt.event.dom.client.MouseWheelHandler)
   */
  public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
    return addHandler(handler, MouseWheelEvent.getType());   
  }

  
  /**
   * Removes the image from the panel so that an empty panel
   * is shown.
   */
  public void clear() {
    cancelFading(false);
    if (active != null) {
      panel.remove(active);
      active = null;
    }
    if (passive != null) {
      panel.remove(passive);
      passive = null;
    }
  }
  
  /**
   * Returns the duration for which an image will be displayed without fading.
   * @return The duration for image display in milliseconds.
   */
  public int getDuration() {
    return duration;
  }
  
  /**
   * Returns the fading duration. A negative value indicates that fading is
   * sequentially, only after the last image has faded out completely,
   * the new image fades in. In this case the actual fading time is twice
   * the absolute value of <code>fading</code>
   * @return The duration of fading in milliseconds.
   */
  public int getFading() {
    return fading;
  }
  
  /**
   * Returns the URL of the <em>largest size version(!)</em> of the currently
   * displayed image. 
   * @return the image's URL
   */
  public String getImageURL() {
    return imageNames[imageNames.length-1];
  }
  
//  /* (non-Javadoc)
//   * @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event)
//   */
//  @Override
//  public void onBrowserEvent(Event event) {
//    switch (event.getTypeInt()) {
//      case Event.ONCLICK: {
//        if (clickListeners != null) {
//          clickListeners.fireClick(this);
//        }
//        break;
//      }
//      case Event.ONMOUSEDOWN:
//      case Event.ONMOUSEUP:
//      case Event.ONMOUSEMOVE:
//      case Event.ONMOUSEOVER:
//      case Event.ONMOUSEOUT: {
//        if (mouseListeners != null) {
//          mouseListeners.fireMouseEvent(this, event);
//        }
//        break;
//      }
//      case Event.ONMOUSEWHEEL: {
//        if (mouseWheelListeners != null) {
//          mouseWheelListeners.fireMouseWheelEvent(this, event);
//        }
//        break;
//      }  
//    }
//  }  

  /**
   * Resizes the displayed image(s), if the containing 
   * <code>FlipImagePanel</code> has been resized.
   * The <code>resize</code> method is called automatically when any of
   * the methods <code>setSize, setPixelSize, setWidth, setHeight</code>
   * is called. 
   */
  public void onResized() {
    assert envelope.getWidget() == null : "prepareResized() must be called before onResized()!";
    
    int lastPanelW = panelW;
    int lastPanelH = panelH;
    
    // envelope.clear();    
    panelW = envelope.getOffsetWidth();
    panelH = envelope.getOffsetHeight();
    if (panelH <= 100) { // unlikely small height: non-quirks mode !?
      panelH = Window.getClientHeight();
      if (lastPanelH == panelH) lastPanelH--;
    }
    // GWT.log("ImagePanel.onResized: panelH: "+String.valueOf(panelH));
    envelope.setWidget(panel);
    
    if (lastPanelW != panelW || lastPanelH != panelH) {
      panel.setPixelSize(panelW, panelH);        
      if (active != null) adjustSize(active);      
      if (sizeStep >= 0) {
        int newStep = pickSize(sizes);
        if (newStep != sizeStep) {
          sizeStep = newStep;
          quickExchangeImage(imageNames[sizeStep]);
          return;
        }
      }
      if (passive != null) adjustSize(passive);    
    } 
  }    
  
  /**
   * Removes the {@link AbsolutePanel} so that the image panels size
   * can be determined correctly by the browser before calling 
   * <code>onResized</code>. The <code>AbsolutePanel</code>
   * is restored by method <code>onResized</code>. This method therefore 
   * should always be used in conjunction with the method 
   * <code>onResized</code>. 
   */
  public void prepareResized() {
    panel.setSize("100%", "100%"); // required for Internet Explorer 7 compatibility!
    envelope.clear();
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SourcesAttachmentEvents#removeAttachmentListener(de.eckhartarnold.client.AttachmentListener)
   */
  public void removeAttachmentListener(AttachmentListener listener) {
    if (attachmentListeners != null) {
      attachmentListeners.remove(listener);
      if (attachmentListeners.isEmpty()) attachmentListeners = null;
    }
  }
    

  /**
   * Sets the duration for which an image will be displayed without fading.
   * @param msecs  The duration for image display in milliseconds.
   */
  public void setDuration(int msecs) {
    assert msecs > 500;
    duration = msecs;
  }
  
  /**
   * Sets the duration for fading in a new image. A negative value of the
   * parameter <code>msecs</code> indicates that fading in and fading out does
   * not occur synchronously but in sequence. Please observe that in this case
   * the total fading phase takes twice as long!
   *  
   * @param msecs  The duration of fading in or out in milliseconds. 
   */
  public void setFading(int msecs) {
    this.fading = msecs;
    if (msecs == 0) {
      cancelFading(true);
    }
  }
  
//  @Override
//  public void setHeight(String height) {
//  	super.setHeight(height);
//  	onResized();
//  }
//
//  @Override
//  public void setPixelSize(int width, int height) {
//  	super.setPixelSize(width, height);
//  	onResized();
//  }
//
//  @Override
//  public void setSize(String width, String height) {
//  	super.setSize(width, height);
//  	onResized();		
//  }
//
//  @Override
//  public void setWidth(String width) {
//  	super.setWidth(width);
//  	onResized();
//  }
  
  /**
   * Shows the image that the given URl points to. 
   * @param url       the URL of the image to show
   * @param notifier  a callback that is issued when the image is loaded and
   *                  has faded in                 
   */ 
  public void showImage(String url, DisplayListener notifier) {
    sizeStep = -1;
    imageNames[imageNames.length-1] = url;
    exchangeImage(url, notifier);
  }

  /**
   * Shows a picture for which representations with different image sizes 
   * exist. The representation, the size of which best fits the 
   * <code>FlipImagePanel</code> widget's size is picked. If the widget is 
   * resized so that another representation would be more appropriate, 
   * the image is automatically exchanged.
   *  
   * @param urls      the URL of different sized versions of the same image,
   *                  each of them corresponding to one of the sizes
   * @param sizes     the sizes of the different versions of the images. 
   * @param notifier  a callback that is issued when the image is loaded and
   *                  has faded in
   */
  public void showImage(String urls[], int[][] sizes,
                        DisplayListener notifier) {
    assert sizes.length == urls.length;
    
    imageNames = urls;
    this.sizes = sizes;
    sizeStep = pickSize(sizes);
    // Debugger.print(urls[sizeStep]);
    // GWT.log(urls[sizeStep], null);
    exchangeImage(urls[sizeStep], notifier);
  }
  
  /**
   * Returns the currently selected size step, if image URLs for several
   * size steps have been specified by the last call of method 
   * <code>showImage()</code>. Returns -1 otherwise.
   * 
   * This method has been made package private, because the information about 
   * the selected size step is needed for prefetching. See {@link Slideshow.show}. 
   * 
   * @return the currently selected size step or -1, if not several size steps
   *         have been specified.
   */
   int getSizeStep() {
     return sizeStep;
   }
   
  /**
   * Exchanges a currently displayed image with the image that the given URL 
   * points to. If fading is enabled the old image will be faded out and
   * the new image faded in simultaneously. 
   * @param url      the URL of the image to show
   * @param notifier  a callback that is issued when the image is loaded and
   *                  has faded in
   */ 
  protected void exchangeImage(String url, DisplayListener notifier) {
    cancelFading(false);   
    Image discard = passive;
    passive = active;
    active = new Image();
    if (hasClickHandlers) active.addStyleName("imageClickable");
    active.addStyleName("slide");
    Fade.setOpacity(active, 0.0);
    displayListener = notifier;    
    ImageLoadHandler loadHandler = new ImageLoadHandler(fading);
    active.addLoadHandler(loadHandler);
    active.addErrorHandler(stdImageErrorHandler);
    if (discard != null) {
      panel.remove(discard);
      discard = null;
    }      
    active.setUrl(url);
    panel.add(active); 
    adjustSize(active);    
    if (fading < 0) startFading(loadHandler);
    Compatibility.fireLoadOnIE(active, url, loadHandler);    
  }  
  
  @Override
  protected void onLoad() {
    for (AttachmentListener a: attachmentListeners)
      a.onLoad(this);
  }

  @Override
  protected void onUnload() {
    cancelFading(false);    
    for (AttachmentListener a: attachmentListeners)
      a.onUnload(this);
  }  
  
  /**
   * Exchanges the current image quickly without fading. 
   * 
   * @param url  the URL of the new image to be displayed
   */
  protected void quickExchangeImage(String url) { 
    int fading = getFading();
    setFading(0);
    exchangeImage(url, displayListener);
    setFading(fading);
//    cancelFading(false);
//    Image discard = passive;
//    passive = active;
//    active = new Image();
//    Fade.setOpacity(active, 0.0);
//    active.addLoadListener(quickLoadListener);
//    if (discard != null) panel.remove(discard);
//    active.setUrl(url);
//    panel.add(active);  
//    // Fade.setOpacity(passive, 1.0);
//    Compatibility.fireLoadOnIE(active, url, quickLoadListener, null);    
  }  
  
  /**
   * Adjusts the size of an image in the panel so that it fits the size of the panel.
   * The aspect ration of the image is preserved. 
   * @param img  the image the size of which is to be adjusted.
   */
  private void adjustSize(Image img) {
  	int w,h, imgW,imgH;
  
  	if (img == null) return;
  	if (sizeStep >= 0) {
  	  imgW = sizes[sizeStep][0];
  	  imgH = sizes[sizeStep][1];
  	} else {
  	  imgW = img.getWidth();
  	  imgH = img.getHeight();
  	}
  	
  	if (imgW == 0 || imgH == 0) return;
  	w = panelW;
  	h = imgH * panelW / imgW;
  	if (h > panelH) {
  		h = panelH;
  		w = imgW * panelH / imgH;
  		panel.setWidgetPosition(img, (panelW-w)/2, 0);
  	} else {
  		panel.setWidgetPosition(img, 0, (panelH-h)/2);
  	}
  	img.setPixelSize(w, h);
  }

  /**
   * Calls the display listener. Makes sure that the display listener
   * is never called twice.
   */
  private void fireDisplay() {
    if (displayListener != null) {
      displayListener.onDisplay();
      displayListener = null;
    }
  }
  
  private void fireFade() {
    if (displayListener != null) {
      displayListener.onFade();
    }
  }
  
  /**
   * Cancels any ongoing image fading process and sets the opacity to the
   * target value. 
   * 
   * @param complete  if true, the opacity of the image will be set to its
   *                  target value, otherwise it will be left where it is
   *                  at the moment of canceling.
   */
  private void cancelFading(boolean complete) {
    if (fadeOut != null) {
      fadeOut.setCompleteOnCancel(complete);
      fadeOut.cancel();
      fadeOut = null;
    }
    if (fadeIn != null) {
      fadeIn.setCompleteOnCancel(complete);
      fadeIn.cancel();
      fadeIn = null;
    }
  }  
  
  
  /**
   * Picks the most suitable of several steps of image sizes for the current
   * size of the <code>FlipImagePanel</code>. The <code>sizes</code> must be
   * a two dimensional array of integers containing one or more discrete steps
   * of width,height values.
   * 
   * @param sizes  an array of discrete display size steps. Each step is
   *               described by two integer values resembling the width and
   *               height. The size steps must be ordered from smallest to 
   *               largest.
   * @return the size step which is most suitable
   */
  private int pickSize(int[][] sizes) {
    for (int i = 0; i < sizes.length - sizeBias; i++) {
      if (sizes[i][0] >= panelW || sizes[i][1] >= panelH) {
        return i;
      }
    }
    return Math.max(0, sizes.length - sizeBias - 1);
      
// // alternative algorithm:
      
//    int ret, diff = Integer.MAX_VALUE;
//    for (int i = 0; i <= sizes.length - sizeBias; i++) {
//      int dx = sizes[i][0] - panelW;
//      int dy = sizes[i][0] - panelH;
//      int cmp = dx*dx + dy*dy;
//      if (cmp < diff) {
//        ret = i;
//        diff = cmp;
//      }
//    }
//    return Math.max(0, ret);
  }    
  
  /**
   * Starts the fading out of the last ("passive") image and the fading in
   * of the current ("active") image.
   */
  private void startFading(ImageLoadHandler loadListener) { 
    cancelFading(true);
    fadeIn = new NotifyingFade(active, loadListener);
    if (passive != null) {
      if (fading > 0) {
        fadeOut = new Fade(passive, 1.0, 0.0, FADE_OUT_STEPS);
        fadeIn.run(fading);
      } else {
        fadeOut = new ChainedFade(passive, fadeIn, FADE_OUT_STEPS);
      }
      fadeOut.run(Math.abs(fading));
    } else {
      fadeIn.run(Math.abs(fading));
    } 
    fireFade();
  }    

}
