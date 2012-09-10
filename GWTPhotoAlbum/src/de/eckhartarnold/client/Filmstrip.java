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

import java.util.HashSet;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Displays a row of thumbnail images as if 
 * on a film strip.
 * 
 * @author ecki
 *
 */
public class Filmstrip extends Composite implements ResizeListener {

  /**
   * An interface that encapsulates a callback method which is called when
   * the user clicks on an image of the film strip. 
   */
  public interface IPickImage {
    /**
     * Called when the user clicks on a certain image on the film strip.
     * @param imageNr  the number of the image that has been clicked on by
     *                 the user.
     */
    void onPickImage(int imageNr);
  }
  
  private class Sliding extends Animation {
    private int displacement, current;
    public boolean isComplete() {
      return current == 0;
    }
    public void onComplete() {
      if (current != 0) {
        current = 0;
        redraw(0);      
      }
      thumbnails.get(cursor).setStylePrimaryName("filmstripHighlighted");      
    }
    public void onUpdate(double progress) {
      int pos = (int)((1.0-progress) * displacement);
      if (Math.abs(pos - current) >= 10) {   // do not take every small step!
        current = pos;
        redraw(current);     
      }
    }
    public void setDisplacement(int displacement) {
      cancel();
      current = -1;
      this.displacement = displacement;
    }
  }
  
  private int            width, height = -1;
  private int            borderSize = 2; // should be the same as specified in the css-file!!!
  private int            cursor = 0;
  private int            filmstripBorderSize = 0; // should be the same as specified in the css-file!!!
  private boolean        isLoaded = false; // true, if the filmstrip is visible
  private int            slidingDuration = 200;
  private HashSet<Image> visible;
  private IPickImage     pickImageCallback;
  
  private final SimplePanel   envelope;
  private final AbsolutePanel panel;
  private final Sliding       sliding = new Sliding();  
  private final Thumbnails    thumbnails;
  
  /**
   * The constructor of class <code>Filmstrip</code>
   * @param collection  the image collection info from which to construct the
   *                    film strip
   */
  public Filmstrip(ImageCollectionInfo collection) {
    this(new Thumbnails(collection));
  }

  /**
   * The constructor of class <code>Filmstrip</code>
   * @param thumbnailImages  the thumb nail images to be displayed on the
   *                         film strip
   */
  public Filmstrip(Thumbnails thumbnailImages) {
    this.thumbnails = thumbnailImages;
    envelope = new SimplePanel();
    envelope.addStyleName("filmstripEnvelope");
    panel = new AbsolutePanel();
    panel.addStyleName("filmstripPanel");
    envelope.setWidget(panel);    
    initWidget(envelope);
    // sinkEvents(Event.ONMOUSEWHEEL);

    ClickHandler imageClickHandler = new ClickHandler() {
      public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        if (pickImageCallback != null) {
          pickImageCallback.onPickImage(thumbnails.indexOf((Image) sender));
        }
      }        
    };
    
    MouseDownHandler imageMouseDownHandler = new MouseDownHandler() {
      public void onMouseDown(MouseDownEvent event) {
        Widget sender = (Widget) event.getSource();
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.addStyleName("filmstripPressed");
        }        
      }
    };
    
    MouseOverHandler imageMouseOverHandler = new MouseOverHandler() {
      public void onMouseOver(MouseOverEvent event) {
        Widget sender = (Widget) event.getSource();        
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.addStyleName("filmstripTouched");
        }
      }
    };
    
  
    MouseOutHandler imageMouseOutHandler = new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        Widget sender = (Widget) event.getSource();
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.removeStyleName("filmstripTouched");
          sender.removeStyleName("filmstripPressed");
        }        
      }
    };
    
    MouseUpHandler imageMouseUpHandler = new MouseUpHandler() {
      public void onMouseUp(MouseUpEvent event) {
        Widget sender = (Widget) event.getSource();
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.removeStyleName("filmstripPressed");
        }              
      }
    };
    
    for (int i = 0; i < thumbnails.size(); i++) {
      Image img = thumbnails.get(i);
      if (i == cursor) img.setStyleName("filmstripHighlighted");
      else img.setStyleName("filmstrip");        
      img.addClickHandler(imageClickHandler);
      img.addMouseDownHandler(imageMouseDownHandler);
      img.addMouseOverHandler(imageMouseOverHandler);
      img.addMouseOutHandler(imageMouseOutHandler); 
      img.addMouseUpHandler(imageMouseUpHandler);      
    }
    
    visible = new HashSet<Image>();
  }
  
  /**
   * Returns the duration in milliseconds for sliding from one image
   * to the next.
   * @return Duration in milliseconds
   */
  public int getSlidingDuration() {
    return slidingDuration;
  }
  
 /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event)
   */
/*  @Override
  public void onBrowserEvent(Event event) {
    switch (event.getTypeInt()) {
      case Event.ONMOUSEWHEEL:
    }
  }*/  

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#onResized()
   */
  public void onResized() {
    //if (!loaded) return;
    // envelope.clear();
    assert envelope.getWidget() == null : "prepareResized() must be called before onResized()!";  
    
    int height = Toolbox.getOffsetHeight(envelope);
    int width = envelope.getOffsetWidth();    
    envelope.setWidget(panel);
    if (width == this.width && height == this.height) return;
    panel.setPixelSize(width-filmstripBorderSize*2, height);
    if (width != this.width) this.width = width;
    if (height != this.height && height != 0) {
      this.height = height;      
      thumbnails.adjustToHeight(height-2*borderSize); 
    }
    redraw(0); 
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
    panel.setSize("100%", "100%");  // required for Internet Explorer 7 compatibility!
    envelope.clear();
  }  
  
  /**
   * Puts the focus on the given image, i.e. the film strip will be
   * scrolled so that the focused image appears in the center. It's
   * frame will be highlighted.
   * 
   * @param imageNr
   */
  public void focusImage(int imageNr) { 
    assert imageNr >= 0 && imageNr < thumbnails.size();
    if (imageNr != cursor || !sliding.isComplete()) {
      sliding.cancel();
      thumbnails.get(cursor).setStylePrimaryName("filmstrip");
      if (slidingDuration > 0 && isLoaded) {       
        sliding.setDisplacement(displacement(imageNr));
        int duration = slidingDuration*Math.min(10, Math.abs(imageNr-cursor)); 
        cursor = imageNr;
        sliding.run(duration);
      } else {
        cursor = imageNr;        
        thumbnails.get(cursor).setStylePrimaryName("filmstripHighlighted");      
        if (height > 0 && isLoaded) redraw(0);
      }
    }
  }
  
  /** 
   * Sets the callback that is called when the user selects an image by 
   * clicking on a thumbnail or by turning the mouse wheel while the mouse
   * pointer is located over the preview filmstrip.
   * 
   * @param callback an Interface of the respective "callback" interface
   *                 or <code>null</code>. 
   */
  public void setPickImageCallback(IPickImage callback) {
    pickImageCallback = callback;
    if (callback != null) {
      for (int i = 0; i < thumbnails.size(); i++) {
        Image img = thumbnails.get(i);
        img.addStyleDependentName("selectable");
      }
    } else {
      for (int i = 0; i < thumbnails.size(); i++) {
        Image img = thumbnails.get(i);
        img.removeStyleDependentName("selectable");        
      }      
    }
  }
   
  /**
   * Sets the duration in milliseconds for sliding from one image
   * to the next.
   * @param duration  Duration in milliseconds
   */
  public void setSlidingDuration(int duration) {
    slidingDuration = duration;
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Widget#onLoad()
   */
  @Override
  protected void onLoad() {  
    if (envelope.getWidget() != null) {    
      prepareResized();
      onResized();
    } // else prepare resize has already been called!
    isLoaded = true;
    redraw(0);
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Widget#onUnload()
   */
  @Override
  protected void onUnload() {
    isLoaded = false;
  }  
  
  /**
   * Moves a thumbnail image to the given position within the widget's 
   * absolute panel. Automatically adds an image to the widget's panel
   * if it was not visible before, or removes it, if it is not visible
   * any more (due to its position lying outside the panel's boundaries). 
   * 
   * @param imgIndex the index number of the thumbnail image
   * @param left     the position of the image on the absolute panel
   */
  private void move(int imgIndex, int left) {
    Image img = thumbnails.get(imgIndex);
    int imgWidth = thumbnails.imageSize(imgIndex)[0];
    if (visible.contains(img)) {
      if (left < this.width && left+imgWidth > 0) {
        panel.setWidgetPosition(img, left, 0);
      } else {
        panel.remove(img);      
        visible.remove(img);
      }
      img.removeStyleName("filmstripTouched");
      img.removeStyleName("filmstripPressed");        
    } else {
      if (left < this.width && left+imgWidth > 0) {      
        panel.add(img, left, 0);
        visible.add(img);
      }
    }
  }
  
  /**
   * Determines the displacement of the currently focused slide to the
   * given slide. 
   * @param slideNr the number of the slide in relation to which the 
   *                displacement shall be calculated, if the slide
   *                <code>slideNr</code> were focused and the focused
   *                slide somewhere either to the left or to the 
   *                right of it.
   * @return the displacement
   */
  private int displacement(int slideNr) {
    if (slideNr == cursor) return 0;
    int delta = 0;
    delta += thumbnails.imageSize(cursor)[0] / 2;
    delta += thumbnails.imageSize(slideNr)[0] / 2;
    if (slideNr > cursor) {
      for (int i = cursor + 1; i < slideNr; i++) {
        delta += thumbnails.imageSize(i)[0];
      }
      return delta;
    } else {
      for (int i = slideNr + 1; i < cursor; i++) {
        delta += thumbnails.imageSize(i)[0];
      }
      return -delta;
    }
  }
  
  /**
   * Redraws the film strip. if parameter <code>displacement</code> is
   * unequal zero, the row of images will be displaced by a certain number of
   * pixels. This is used to implement an animated change of the selected 
   * image changes.
   * 
   * @param displacement  the number of pixels by which the thumbnails shall be 
   *                      displaced. (0 means no displacement, a negative 
   *                      number results in a displacement to the left a 
   *                      positive number in a displacement to the right.)
   */
  private void redraw(int displacement) {
    int c1, c2;
    int left, right;
    int center = width/2 + displacement;
    
    int w = thumbnails.imageSize(cursor)[0];
    move(cursor, center - w/2);

    c1 = cursor-1; 
    c2 = cursor+1;
    left = center - w/2 - borderSize;
    right = center + w/2 + borderSize;
    
//    while ((right < width && c2 < thumbnails.size()) ||  
//        (left >= 0 && c1 >= 0)) {
    while (c1 >= 0 || c2 < thumbnails.size()) {
      if (c1 >= 0) {
        left -= thumbnails.imageSize(c1)[0] + 2*borderSize;
        move(c1, left + borderSize);
        c1--;
      }
      if (c2 < thumbnails.size()) {
        move(c2, right + borderSize);
        right += thumbnails.imageSize(c2)[0] + 2*borderSize;
        c2++;
      }
    }
  }
}

