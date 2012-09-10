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

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Puts a {@link ControlPanel} that also contains filmstrip widget in a pop-up
 * window. 
 * 
 * <p>The pop-up window opens up whenever the user moves the mouse over
 * a given widget (which usually is the {@link ImagePanel}) and stays open 
 * for a few seconds. It is opened either at the top or at the bottom. Other
 * than the ordinary {@link ControlPanelOverlay} it cannot be dragged around by
 * the user.
 */
public class FilmstripOverlay extends PanelOverlayBase {

  private class OverlayPopupPanel extends PopupPanel 
      implements MouseOverHandler, MouseOutHandler {
    OverlayPopupPanel() {
      super();
      addDomHandler(this, MouseOverEvent.getType());
      addDomHandler(this, MouseOutEvent.getType());
      addStyleName("filmstripPopup");
    }

    public void onMouseOver(MouseOverEvent event) {
      if (popupVisible) timer.cancel();      
    }
    
    public void onMouseOut(MouseOutEvent event) {
      if (popupVisible) timer.schedule(delay);      
    }
  }  
  
  /** Constants for the <code>initialPosition</code> of the constructor */ 
  public final static String TOP    = "top", 
                             BOTTOM = "bottom";
 
  private String          position;  
  private boolean         switchCaption = false;  
  private CaptionOverlay  syncedCaption;

  
  /**
   * Creates a new overlay control panel which takes a given control panel
   * an catches it in a popup window. The popup is opened when ever
   * <code>widget</code> fires a mouse event.
   * 
   * @param controlPanel    the control panel to be overlaid
   * @param baseWidget      the widget over which the control panel is laid
   * @param initialPosition integer value 1 for top or 3 for bottom position
   */
  public FilmstripOverlay(ControlPanel controlPanel, 
      Widget baseWidget, String position) {
    super(controlPanel, baseWidget);
    
    if (position == TOP) {
      this.position = TOP;
    } else {
      this.position = BOTTOM;
    }
    popup = new OverlayPopupPanel();
    popup.add(controlPanel);
    popup.setAnimationEnabled(true);
    
    // this.delay = 1500;    
    timer = new Timer() {
      public void run() {
        hidePopup();
      }
    };    
  }
  
  /**
   * Turns on synchronization with a caption overlay, i.e. the caption is
   * moved on screen, if the panel overlaps the caption.
   * @param caption
   */
  public void syncWithCaption(CaptionOverlay caption) {
    if ( (position == TOP     && caption.getShowAtTop()) ||
         (position == BOTTOM  && !caption.getShowAtTop()) ) {
      this.syncedCaption = caption;      
      this.switchCaption = true;
    } else {
      this.switchCaption = false;
    }
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
    hidePopup();
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#onResized()
   */
  @Override
  public void onResized() {   
    if (popupVisible) { 
      int width = popup.getOffsetWidth();
      int height = Toolbox.getOffsetHeight(popup);
      placePopup(width, height);
    }    
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#prepareResized()
   */
  @Override
  public void prepareResized() { }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel.PositionCallback#setPosition(int, int)
   */
  public void setPosition(int offsetWidth, int offsetHeight) {
    if (switchCaption) {
      this.syncedCaption.setShowAtTop(position == BOTTOM);
    }
    placePopup(offsetWidth, offsetHeight);
  }   
  
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.PanelOverlayBase#hidePopup()
   */
  protected void hidePopup() { 
    //TODO disable sliding for hidden popup!
    if (switchCaption) {
      this.syncedCaption.setShowAtTop(position == TOP);
    }
    super.hidePopup();
  }  
  
  /**
   * Positions the popup inside the base widget. The parameter <code>width</code>
   * does not determine the popup's width. If <code>width</code> is
   * unequal the base widget's width then the popup's width is overwritten
   * with the base widget's width.
   */
  protected void placePopup(int width, int height) {
    int w = ((Widget) baseWidget).getOffsetWidth();
    int h = Toolbox.getOffsetHeight((Widget) baseWidget);
    int x = ((Widget) baseWidget).getAbsoluteLeft();
    int y = ((Widget) baseWidget).getAbsoluteTop();
    if (w != width) {
      popup.setWidth(w+"px");
      controlPanel.prepareResized();
      controlPanel.onResized();      
    }
    if (height == 0) height = Toolbox.getOffsetHeight(controlPanel);
    if (position == BOTTOM) {
      y += h-height;
    }
    popup.setPopupPosition(x, y);
  }
}

