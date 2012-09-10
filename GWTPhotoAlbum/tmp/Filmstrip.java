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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;


/**
 * Class <code>FilmStrip</code> displays a row of thumbnail images as if 
 * on a filmstrip.
 * 
 * @author ecki
 *
 */
public class Filmstrip extends Composite implements ResizeListener {
  
  public interface IPickImage {
    void onPickImage(int imageNr);
  }
  
  private class Sliding extends Animation {
    private int displacement, current;
    public void onComplete() {
      if (current != 0) {
        current = 0;
        redraw(0);      
      }
      thumbnails.get(cursor).setStylePrimaryName("filmstripHighlighted");      
    }
    public void onUpdate(double progress) {
      int pos = (int)((1.0-progress) * displacement);
      if (pos != current) {
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
  private int            borderSize = 2; 
  private int            cursor = 0;
  private int            slidingDuration = 100;
  private HashSet<Image> visible;
  private IPickImage     pickImageCallback;
  
  private final AbsolutePanel panel;
  private final Sliding       sliding = new Sliding();  
  private final Thumbnails    thumbnails;
  
  
  public Filmstrip(ImageCollectionInfo collection) {
    this(new Thumbnails(collection));
  }
  
  public Filmstrip(Thumbnails thumbnailImages) {
    this.thumbnails = thumbnailImages;
    panel = new AbsolutePanel();
    panel.addStyleName("filmstripPanel");
    initWidget(panel);
    sinkEvents(Event.ONMOUSEWHEEL);
    super.setSize("100%", (64+2*borderSize)+"px");

    ClickListener imageClickListener = new ClickListener() {
      public void onClick(Widget sender) {
        if (pickImageCallback != null) {
          pickImageCallback.onPickImage(thumbnails.indexOf((Image) sender));
        }
      }        
    };
    
    MouseListener imageMouseListener = new MouseListener() {
      public void onMouseDown(Widget sender, int x, int y) { 
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.addStyleName("filmstripPressed");
        }        
      }
      public void onMouseEnter(Widget sender) {
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.addStyleName("filmstripTouched");
        }
      }
      public void onMouseLeave(Widget sender) {
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.removeStyleName("filmstripTouched");
          sender.removeStyleName("filmstripPressed");
        }        
      }
      public void onMouseMove(Widget sender, int x, int y) {}
      public void onMouseUp(Widget sender, int x, int y) {
        if (pickImageCallback != null && sender != thumbnails.get(cursor)) {
          sender.removeStyleName("filmstripPressed");
        }              
      }
    };
    
    for (int i = 0; i < thumbnails.size(); i++) {
      Image img = thumbnails.get(i);
      img.setStyleName("filmstrip");        
      img.addClickListener(imageClickListener);
      img.addMouseListener(imageMouseListener);
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
  
  @Override
  public void onBrowserEvent(Event event) {
    switch (event.getTypeInt()) {
      case Event.ONMOUSEWHEEL:
    }
  }  

  public void onResized() {
    int height = panel.getOffsetHeight();
    int width = panel.getOffsetWidth();
    boolean doRedraw = false;
    if (width != this.width) {
      this.width = width;
      doRedraw = true;
    }
    if (height != this.height) {
      this.height = height;      
      thumbnails.adjustToHeight(height-2*borderSize); // 3 instead of 2 is a hack so that the lower border of the image frame is displayed!
      doRedraw = true;
    }
    if (doRedraw) redraw(0); 
  }
  
  /**
   * Puts the focus on the given image, i.e. the film strip will be
   * scrolled so that the focused image appears in the center. It's
   * frame will be hilighted.
   * 
   * @param imageNr
   */
  public void focusImage(int imageNr) { 
    assert imageNr >= 0 && imageNr < thumbnails.size();
    if (imageNr != cursor) {
      thumbnails.get(cursor).setStylePrimaryName("filmstrip");
      if (slidingDuration > 0) {
        sliding.setDisplacement(displacement(imageNr));
        int duration = slidingDuration*Math.abs(imageNr-cursor); 
        cursor = imageNr;
        sliding.run(duration);
      } else {
        cursor = imageNr;        
        thumbnails.get(cursor).setStylePrimaryName("filmstripHighlighted");      
        if (height > 0) redraw(0);
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
  
  @Override
  public void setPixelSize(int width, int height) {
    super.setPixelSize(width, height);
    onResized();
  }
  
  @Override
  public void setSize(String width, String height) {
    super.setSize(width, height);
    onResized();
  }
  
  /**
   * Sets the duration in milliseconds for sliding from one image
   * to the next.
   * @parameter duration  Duration in milliseconds
   */
  public void setSlidingDuration(int duration) {
    slidingDuration = duration;
  }
  
  @Override
  protected void onLoad() { 
    onResized();
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
   *                slide somwhere either to the left or to the 
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

