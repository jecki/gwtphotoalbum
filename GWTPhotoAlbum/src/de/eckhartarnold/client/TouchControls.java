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

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Simple touch screen controls for the image panel.
 * 
 * Allows the slideshow to be controlled by touching (or clicking) on certain
 * areas of the screen:
 * - a touch on the left edge stops the slideshow and shows the previous image
 * - a touch on the right edge stops the lisdeshow and moves on to the next
 *   image
 * - a touch in the center starts or stops the slideshow
 * - a touch on the upper edge of the screen returns to the gallery or the
 *   first picture depending on the kind of presentation (gallery presentation
 *   or slideshow presentation) that has been configured.  
 * 
 * @author eckhart
 */
public class TouchControls implements AttachmentListener, SlideshowControl,
    TouchStartHandler, MouseDownHandler { // , MouseMoveHandler, MouseUpHandler, ClickHandler {

//  private class FadeOut extends Fade {     
//    FadeOut(Widget widget) {
//      super(widget, 1.0, 0.0, FADE_STEPS);;
//    }
//   
//    @Override
//    protected void onComplete() {
//      super.onComplete();
//    }
//  }
  
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
  
  private int          buttonDown = -1;
  private Fade         fader;
  private ClickHandler homeButtonHandler = null;
  private ImagePanel   imagePanel;
  private Image[]      symbol = new Image[8];
  private Slideshow    slideshow;
  private boolean      touched = false;
  
  public TouchControls(Slideshow slideshow) {
    this.slideshow = slideshow;
    imagePanel = slideshow.getImagePanel();
    imagePanel.addMouseDownHandler(this);
    imagePanel.addTouchStartHandler(this);
    symbol[BACK] = new Image("icons/back.svg");
    symbol[BACK_DOWN] = new Image("icons/back.svg");
    symbol[NEXT] = new Image("icons/next.svg");
    symbol[NEXT_DOWN] = new Image("icons/next.svg");
    symbol[HOME] = new Image("icons/gallery.svg");
    symbol[HOME_DOWN] = new Image("icons/gallery.svg");
    symbol[PLAY] = new Image("icons/play.svg");
    symbol[PAUSE] = new Image("icons/pause.svg");
    for (int i = FIRST; i <= LAST; i++) {
      if (i == BACK_DOWN || i == NEXT_DOWN || i == HOME_DOWN) {
        symbol[i].addStyleName("touchDown");        
      } else {
        symbol[i].addStyleName("touch");
      }
      Fade.setOpacity(symbol[i], 0.0);
    }   
    imagePanel.addAttachmentListener(this);
  }
  
  
 
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google.gwt.event.dom.client.MouseDownEvent)
   */
  @Override
  public void onMouseDown(MouseDownEvent event) {
    if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
      if (touched) {
        touched = false;
      } else {
        buttonDown = buttonTouched(event.getX(), event.getY()); 
        touch(event.getX(), event.getY());
      }
    }
//    int button = buttonTouched(event.getX(), event.getY());
//    buttonDown = button;
//    Rectangle r = buttonBoundary(button);   
//    if (button >= FIRST && button <= LAST) {
//      if (button == BACK || button == NEXT || button == HOME) {
//        button++;
//      } else if (button == PLAY) {
//        if (slideshow.isRunning()) {
//          button = PAUSE;
//        }
//      }
//      showWidget(symbol[button], r.x, r.y, r.w, r.h);
//      hideAllOtherWidgets(symbol[button]);
//      fader = new Fade(symbol[button], 0.0, 1.0, FADE_STEPS);
//      fader.run(100);      
//    }
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
    hideAllOtherWidgets(null);
  }
  
  @Override
  public void onTouchStart(TouchStartEvent event) {
    JsArray<Touch> touches = event.getTouches();
    // Debugger.print("TouchControls.onTouchStart() touches.length = " + touches.length());
    if (touches.length() > 0) {
      // Debugger.print("TouchControls.onTouchStart() x = " + touches.get(0).getClientX() + "  y = " + touches.get(0).getClientY());
      int x = touches.get(0).getClientX();
      int y = touches.get(0).getClientY();
      buttonDown = buttonTouched(x, y); 
      touched = true;
      touch(x, y);
    }
  }  
  
  /**
   * Returns the screen boundary for the visual feedback of a particular touch
   * button.
   * 
   * @param button  the button for which the boundary rectangle shall be
   *                returned
   * @return the boundary rectangle for the given button.
   */
  protected Rectangle buttonBoundary(int button) {
    int w = imagePanel.getOffsetWidth();
    int h = Toolbox.getOffsetHeight(this.imagePanel);
    
    if (button == BACK || button == BACK_DOWN) {
      return new Rectangle(0, h/8, w/5, h*6/8);
    } else if (button == NEXT || button == NEXT_DOWN) {
      return new Rectangle(w*4/5, h/8, w/5, h*6/8);
    } else if (button == HOME || button == HOME_DOWN) {
      return new Rectangle(w/4, 0, w/2, h/4);
    } else if (button == PLAY || button == PAUSE) {
      return new Rectangle(w/4, h/4, w/2, h/2);
    } else {
      return new Rectangle(0, 0, 0, 0);
    }    
  }
  
  /**
   * Determines which touch area, if any, has been hit by a touch or click
   * on the screen.
   * 
   * @param x  the x coordinate of the click event.
   * @param y  the y coordinate of the click event.
   * @return BACK, NEXT, HOME, PLAY if any of the respective areas has been
   *         hit or -1 if no area has been hit.
   */
  protected int buttonTouched(int x, int y) {
    int w = imagePanel.getOffsetWidth();
    int h = Toolbox.getOffsetHeight(this.imagePanel);
    
    if (y < h * 7 / 8) {
      if (x < w / 4) {
        return BACK;      
      } else if (x > w * 3 / 4) {
        return NEXT;
      } else if (y < h / 4) {
        return HOME;
      } else {
        return PLAY;
      }
    }
    return -1;
  }
  
  /**
   * Reaction for a touch event on the panel.
   * 
   * @param x  x-position of the event
   * @param y  y-position of the event
   */
  protected void touch(int x, int y) {
    int button = buttonTouched(x, y);    
    if (buttonDown == -1 || button != buttonDown) {
      hideAllOtherWidgets(null);
      buttonDown = -1;
      return;
    }    
    Rectangle r = buttonBoundary(button);
    
    if (button == BACK) {
      visualFeedBack(symbol[BACK], r.x, r.y, r.w, r.h);
      slideshow.stop();
      slideshow.back();        
    } else if (button == NEXT) {
      visualFeedBack(symbol[NEXT], r.x, r.y, r.w, r.h);
      slideshow.stop();
      slideshow.next();        
    } else if (button == HOME) {
      showWidget(symbol[HOME], r.x, r.y, r.w, r.h);      
      hideAllOtherWidgets(symbol[HOME]);
      // homeButtonHandler.onClick(null);
      fader = new FadeHomeButton(symbol[HOME], null); // TODO: using null here is a hack!!! Need to refactor home button listener logic.
      fader.run(200);               
    } else if (button == PLAY){
      if (slideshow.isRunning()) {
        visualFeedBack(symbol[PAUSE],r.x, r.y, r.w, r.h);            
        slideshow.stop();
      } else {
        visualFeedBack(symbol[PLAY], r.x, r.y, r.w, r.h);           
        slideshow.next();
        slideshow.start();
      }        
    } else {
      hideAllOtherWidgets(null);
    }
    buttonDown = -1;    
  }
  
  private void hideAllOtherWidgets(Image except) {
    AbsolutePanel panel = imagePanel.getPanel();   
    for (int i = FIRST; i <= LAST; i++) {
      if (symbol[i] != except && panel.getWidgetIndex(symbol[i]) >= 0) {
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
    showWidget(img, x, y, w, h);    
    hideAllOtherWidgets(img);
    fader = new Fade(img, 1.0, 0.0, FADE_STEPS);
    fader.run(500);
  }

}
