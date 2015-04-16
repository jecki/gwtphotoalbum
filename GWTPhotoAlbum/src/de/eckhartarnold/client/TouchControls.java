/*
 * Copyright 2015 Eckhart Arnold (eckhart_arnold@hotmail.com).
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

import sun.security.ssl.Debug;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author 
 * eckhart
 *
 */
public class TouchControls implements AttachmentListener, SlideshowControl,
    ClickHandler, MouseDownHandler {

  private class FadeOut extends Fade {     
    FadeOut(Widget widget) {
      super(widget, 1.0, 0.0, FADE_STEPS);;
    }
   
    @Override
    protected void onComplete() {
      super.onComplete();
    }
  }
  
  private class FadeHomeButton extends Fade {
    private ClickEvent event;
    
    FadeHomeButton(Widget widget, ClickEvent event) {
      super(widget);
    }
    
    @Override
    protected void onComplete() {
      super.onComplete();      
      // panel.remove(widget);     
      if (homeButtonHandler != null) {
        homeButtonHandler.onClick(event);
      }      
    }    
  }
  
  protected static final double  FADE_STEPS = 0.10; 
  
  private static final int  FIRST = 0, BACK = 0, BACK_DOWN = 1, NEXT = 2, 
      NEXT_DOWN = 3, HOME = 4, HOME_DOWN = 5, PLAY = 6, PAUSE = 7, LAST = 7; 
  
  private ClickHandler homeButtonHandler = null;
  private Fade         fader;
  private ImagePanel   imagePanel;
  private Image[]      symbol = new Image[8];
  private Slideshow    slideshow;
  
  public TouchControls(Slideshow slideshow) {
    this.slideshow = slideshow;
    imagePanel = slideshow.getImagePanel();
    imagePanel.addClickHandler(this);    
    symbol[BACK] = new Image("icons/back.svg");
    symbol[BACK_DOWN] = new Image("icons/back_down.svg");
    symbol[NEXT] = new Image("icons/next.svg");
    symbol[NEXT_DOWN] = new Image("icons/next_down.svg");
    symbol[HOME] = new Image("icons/gallery.svg");
    symbol[HOME_DOWN] = new Image("icons/gallery_down.svg");
    symbol[PLAY] = new Image("icons/play.svg");
    symbol[PAUSE] = new Image("icons/pause.svg");
    for (int i = FIRST; i <= LAST; i++) {
      symbol[i].addStyleName("touch");
      Fade.setOpacity(symbol[i], 0.0);
    }   
    imagePanel.addAttachmentListener(this);
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google.gwt.event.dom.client.MouseDownEvent)
   */
  @Override
  public void onMouseDown(MouseDownEvent event) {

  }
//
//  /* (non-Javadoc)
//   * @see com.google.gwt.event.dom.client.MouseMoveHandler#onMouseMove(com.google.gwt.event.dom.client.MouseMoveEvent)
//   */
//  @Override
//  public void onMouseMove(MouseMoveEvent event) {
//
//  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowControl#onClick(com.google.gwt.event.dom.client.ClickEvent)
   */
  @Override
  public void onClick(ClickEvent event) {
    int x = event.getX();
    int y = event.getY();
    int w = imagePanel.getOffsetWidth();
    int h = Toolbox.getOffsetHeight(this.imagePanel);
    
    if (y < h * 7 / 8) {
      if (x < w / 4) {
        // back
        visualFeedBack(symbol[BACK], 0, h/8, w/5, h*6/8);
        slideshow.stop();
        slideshow.back();        
      } else if (x > w * 3 / 4) {
        // next
        visualFeedBack(symbol[NEXT], w*4/5, h/8, w/5, h*6/8);
        slideshow.stop();
        slideshow.next();        
      } else if (y < h / 4) {
        // home
        hideAllWidgets();
        showWidget(symbol[HOME], w/4, 0, w/2, h/4);
        fader = new FadeHomeButton(symbol[HOME], event);
        fader.run(100);               
      } else {
        // play pause
        if (slideshow.isRunning()) {
          visualFeedBack(symbol[PAUSE], w/4, h/4, w/2, h/2);            
          slideshow.stop();
        } else {
          visualFeedBack(symbol[PLAY], w/4, h/4, w/2, h/2);           
          slideshow.next();
          slideshow.start();
        }        
      }
    }
  }


  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowControl#setHomeButtonListener(com.google.gwt.event.dom.client.ClickHandler)
   */
  @Override
  public void setHomeButtonListener(ClickHandler handler) {
    homeButtonHandler = handler;
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onLoad(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onLoad(Widget sender) {
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onUnload(com.google.gwt.user.client.ui.Widget)
   */
  @Override
  public void onUnload(Widget sender) {
    hideAllWidgets();
  }

  private void hideAllWidgets() {
    AbsolutePanel panel = imagePanel.getPanel();   
    for (int i = FIRST; i <= LAST; i++) {
      if (panel.getWidgetIndex(symbol[i]) >= 0) {
        Fade.setOpacity(symbol[i], 0.0);      
        panel.remove(symbol[i]);
      }         
    }
  }
  
  private void showWidget(Image img, int x, int y, int w, int h) {
    int ww = w, hh = h, dx = 0, dy = 0;
    if (w > h / 2) {
      ww = h / 2;
      dx = (w - ww) / 2;
    } else {
      hh = w * 2;
      dy = (h - hh) / 2;
    }
    AbsolutePanel panel = imagePanel.getPanel();
    panel.add(img);
    img.setPixelSize(ww, hh);
    panel.setWidgetPosition(img, x + dx, y + dy);
  }
  
  private void visualFeedBack(Image img, int x, int y, int w, int h) {
    hideAllWidgets();
    showWidget(img, x, y, w, h);
    fader = new FadeOut(img);
    fader.run(500);
  }
}
