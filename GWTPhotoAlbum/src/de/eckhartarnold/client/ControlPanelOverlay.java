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

//import java.lang.Math;
//import java.util.EnumSet;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Puts a {@link ControlPanel} in a popup
 * window. 
 * 
 * <p>The popup windows opens up whenever the user moves the mouse over
 * a given widget (which usually is the {@link ImagePanel}) and stays open 
 * for a few seconds. The popup panel can be dragged by the user by grabbing 
 * its border. 
 */
public class ControlPanelOverlay extends PanelOverlayBase {
  
  private class OverlayPopupPanel extends MovablePopupPanel 
      implements MouseOverHandler, MouseOutHandler {
    OverlayPopupPanel() {
      super();
      addDomHandler(this, MouseOverEvent.getType());
      addDomHandler(this, MouseOutEvent.getType());
      addStyleName("controlPanelPopup");
    }
    
    public void onMouseOver(MouseOverEvent event) {
      if (popupVisible) timer.cancel();      
    }
    
    public void onMouseOut(MouseOutEvent event) {
      if (popupVisible) timer.schedule(delay);      
    }
  }
  
  /** Constants for the <code>initialPosition</code> of the constructor */ 
  public final static String UPPER_LEFT  = "upper left", 
                             UPPER_RIGHT = "upper right",
                             LOWER_RIGHT = "lower right", 
                             LOWER_LEFT  = "lower left";
 
  private static int MARGIN = 2;
  
  /** 
   * The time in milliseconds until the control panel popup is hidden
   * again after showing
   */
  public int delay = 5000;
  
  private int                  baseLeft, baseTop;
  private int                  baseWidth, baseHeight;  
  private int                  xpos, ypos;
  private int                  width, height;
  
  /**
   * Creates a new overlay control panel which takes a given control panel
   * an catches it in a popup window. The popup is opened when ever
   * <code>widget</code> fires a mouse event.
   * 
   * @param controlPanel    the control panel to be overlaid
   * @param baseWidget      the widget over which the control panel is laid
   * @param initialPosition integer value from 1 to 4 that indicates the corner
   *                        where the popup panel is to appear initially:
   *                        1 = upper left, 2 = upper right, 3 = lower right,
   *                        4 = lower left 
   */
  public ControlPanelOverlay(ControlPanel controlPanel, 
      Widget baseWidget, String initialPosition) {
    super(controlPanel, baseWidget);
    
    popup = new OverlayPopupPanel();
    popup.add(controlPanel);
    popup.setAnimationEnabled(true);
    
    this.delay = 5000;    
    timer = new Timer() {
      public void run() {
        hidePopup();
      }
    };
    
    if (initialPosition == LOWER_LEFT) {
      xpos = MARGIN;
      ypos = Window.getClientHeight();
    } else if (initialPosition == UPPER_RIGHT) {
      xpos = Window.getClientWidth();
      ypos = MARGIN;
    } else if (initialPosition == LOWER_RIGHT) {
      xpos = Window.getClientWidth();
      ypos = Window.getClientHeight();
    } else { // initialPosition == UPPER_LEFT 
      xpos = MARGIN; 
      ypos = MARGIN;
    } 
  }
   
  
  /**
   * Checks if the control panel popup is currently visible.
   * @return true, if the control panel popup is visible.
   */
  public boolean isVisible() {
    return popupVisible;
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener.onLoad()
   */   
  public void onLoad(Widget sender) { }
  
//  @Override
//  public void onMouseMove(MouseMoveEvent event) {
//    // the comparison with the last mouse position is a work around for
//    // some browsers like Opera
//    int x = event.getX();
//    int y = event.getY();
//    if (lastMouseX != x || lastMouseY != y) {
//      if (!popupVisible) {
//        popup.setPopupPositionAndShow(this);
//        popupVisible = true;
//      }
//      timer.schedule(delay);
//      lastMouseX = x;
//      lastMouseY = y;
//    }
//  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#onResized()
   */
  public void onResized() {
    int btnSize = controlPanel.getCrtlBtnSize();
    
    String style = popup.getStyleName();
    int pos = style.indexOf("border-");
    if (pos >= 0) {
      style = style.substring(pos, pos+10);
      popup.removeStyleName(style);
    }
    
    if (btnSize <= 16) {
      popup.addStyleName("border-2px");
      MARGIN = 3;
    } else if (btnSize <= 32) {
      popup.addStyleName("border-4px");
      MARGIN = 5;
    } else if (btnSize <= 48) {
      popup.addStyleName("border-6px");
      MARGIN = 7;
    } else {
      popup.addStyleName("border-8px");
      MARGIN = 8;
    }
    
    int w = ((Widget) baseWidget).getOffsetWidth();
    int h = Toolbox.getOffsetHeight((Widget) baseWidget);
    int x = ((Widget) baseWidget).getAbsoluteLeft();
    int y = ((Widget) baseWidget).getAbsoluteTop();
    int pw = width;
    int ph = height;
    
    if (popupVisible) {
      xpos = popup.getPopupLeft();
      ypos = popup.getPopupTop();   
      width = popup.getOffsetWidth();
      height = Toolbox.getOffsetHeight(popup);
    }
    
    if (baseWidth == 0) baseWidth = w;
    if (baseHeight == 0) baseHeight = h;
    
    xpos = (xpos-baseLeft+pw/2) * w / baseWidth + x - width/2;
    ypos = (ypos-baseTop+ph/2) * h / baseHeight + y - height/2;
   
    baseLeft = x;
    baseTop = y;
    baseWidth = w;
    baseHeight = h;
    
    if (popupVisible) placePopup();
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onUnload(com.google.gwt.user.client.ui.Widget)
   */
  public void onUnload(Widget sender) {
    hidePopup();
  }  

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#prepareResized()
   */
  public void prepareResized() { }  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel.PositionCallback#setPosition(int, int)
   */
  public void setPosition(int offsetWidth, int offsetHeight) {
    width = offsetWidth;
    height = offsetHeight;
    placePopup();
  }

  
  /**
   * Saves the popup position and then hides the popup.
   */
  protected void hidePopup() { 
    xpos = popup.getPopupLeft();
    ypos = popup.getPopupTop();
    super.hidePopup();
  }  
  
  /**
   * Assures that the popup panel is always fully visible inside the 
   * base widget's area and sets the popup position.
   */
  protected void placePopup() {
    if (xpos - baseLeft + width > baseWidth) {
      xpos = baseLeft + baseWidth - width - MARGIN;
    }
    if (ypos - baseTop + height > baseHeight) {
      ypos = baseTop + baseHeight - height - MARGIN; 
    }
    if (xpos < 0) xpos = MARGIN;
    if (ypos < 0) ypos = MARGIN;

    popup.setPopupPosition(xpos, ypos);
  }

}

