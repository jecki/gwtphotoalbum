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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;

/**
 * Common root of the presentation classes. A presentation manages the
 * slideshow's layout, but it may also manage alternative objects that
 * are displayed while the slideshow is not visible, like, for example,
 * a gallery of images.
 * 
 * @author eckhart
 *
 */
public abstract class Presentation implements ResizeHandler, ValueChangeHandler<String> {  
  /**
   * Parses a <code>Slideshow</code> browser-history token and returns
   * the number of the slide of that token.
   * @param historyToken  the history token
   * @return slide number or -1, if the history token was not a 
   *         <code>Slideshow</code> token
   */
  static int parseSlideToken(String historyToken) {
    if (historyToken.startsWith(Slideshow.SLIDE_TOKEN)) {
      String slideStr = historyToken.substring(Slideshow.SLIDE_TOKEN.length());
      return Integer.parseInt(slideStr)-1;
    } else {
      return -1;
    }
  }   
  
  protected static final int SMALL_SCREEN_HEIGHT_THRESHOLD = 380;
  protected static final int SMALL_SCREEN_WIDTH_THRESHOLD = 600;
  
  protected Layout  layout;
  protected Layout  normalLayout;
  /** Layout for small screens like those on mobile devices */ 
  protected Layout  mobileLayout;
  protected Panel   parent;
  
  protected boolean slideshowActive = false;
  
  /**
   * Constructor of class <code>Presentation</code>. Prepares everything
   * for the presentation, but does not activate the add the layout to the
   * parent panel or activate the slideshow.
   * @param parent  the parent panel
   * @param layout  the layout object.
   */
  Presentation(Panel parent, Layout layout) {
    this.parent = parent;
    this.normalLayout = layout;
    this.mobileLayout = layout;
    this.layout = layout; 
    Window.addResizeHandler(this);
    History.addValueChangeHandler(this);    
  }
   

  /* (non-Javadoc)
   * @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent)
   */
  @Override
  public void onResize(ResizeEvent event) {
    if (slideshowActive) {
      boolean running = layout.getSlideshow().isRunning();
      layout.issueResize();
      int h = Toolbox.getOffsetHeight(parent);
      int w = Toolbox.getOffsetWidth(parent);
      // GWT.log("height: "+String.valueOf(h)+"  "+String.valueOf(parent.getOffsetHeight())+" "+Window.getClientHeight());
      if (switchLayouts(w, h)) {
        activateSlideshow();
        if (running) 
          layout.getSlideshow().start();
      } else {
        // layout.issueResize();
      }
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
   */
  @Override
  public void onValueChange(ValueChangeEvent<String> event) {
    String historyToken = event.getValue();
    GWT.log("Slideshow.onValueChanged:\n"+historyToken);
    int slideNr = Presentation.parseSlideToken(historyToken);
    if (slideNr >= 0) {
      layout.getSlideshow().stop();
      layout.getSlideshow().showImmediately(slideNr);
    } else {
      History.back();       
    }
  }  
  
  /**
   * Sets a mobile layout that will be activated automatically on small
   * screens like those on mobile devices. The mobile layout should best
   * be a full screen layout with overlay control panel and no filmstrip.
   * 
   * @param mobileLayout the layout to be used for mobile devices.
   */
  public void setMobileLayout(Layout mobileLayout) {
    if (mobileLayout != null) {
      this.mobileLayout = mobileLayout;
    } else {
      this.mobileLayout = this.normalLayout;
    }
    onResize(null);
  }
  
  
  /**
   * Activates the slideshow and adds the layout to the parent panal.
   */
  protected void activateSlideshow() {
    if (!slideshowActive) {
      // switchLayouts(Window.getClientHeight());
      switchLayouts(Toolbox.getOffsetWidth(parent), 
                    Toolbox.getOffsetHeight(parent));
      parent.add(layout.getRootWidget());
      layout.issueResize();    
      slideshowActive = true;
    }
  }  

  /**
   * Deactivates the slideshow and removes the layout from the parent panel.
   */
  protected void deactivateSlideshow() {
    if (slideshowActive) {
      layout.getSlideshow().stop(); // <- just to make sure in case the browser "forgets" to send an unload message
      parent.remove(layout.getRootWidget());    
      slideshowActive = false;
    }
  }
 
  
  /**
   * Switches between mobile and normal layout if necessary. A switch is
   * necessary if the current layout is not the layout appropriate to the 
   * screen size. If a switch is necessary the slideshow will be deactivated
   * and needs to be reactivated after calling this function! 
   * @return true, if the layout has been switched, false otherwise.
   */
  private boolean switchLayouts(int width, int height) {
    if (( (height <= SMALL_SCREEN_HEIGHT_THRESHOLD || 
           width <= SMALL_SCREEN_WIDTH_THRESHOLD ) 
          && layout != mobileLayout) ||
        ( (height > SMALL_SCREEN_HEIGHT_THRESHOLD && 
           width > SMALL_SCREEN_WIDTH_THRESHOLD ) 
          && layout != normalLayout) ) {
      
      Slideshow slideshow = layout.getSlideshow(); 
      int duration = slideshow.getDuration();
      int imageNr = slideshow.getCurrentSlide();
      
      deactivateSlideshow();
      
      if (layout != mobileLayout) 
        layout = mobileLayout;
      else 
        layout = normalLayout;
      
      slideshow = layout.getSlideshow();
      slideshow.setDuration(duration);
      slideshow.show(-1);
      slideshow.showImmediately(imageNr);
      
      return true;
    } else {
      return false;
    }
  }
}
