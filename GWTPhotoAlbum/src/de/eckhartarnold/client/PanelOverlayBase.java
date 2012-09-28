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

//import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract base class for the control panel overlay classes.
 *
 */
public abstract class PanelOverlayBase implements MouseMoveHandler,
  ClickHandler, ResizeListener, PopupPanel.PositionCallback,
  AttachmentListener {
  
  /** the key in the "info.json" dictionary that determines the  
   *  panel position. Possible values: "top", "bottom", 
   *  "upper left", "upper right", "lower left", "lower right" 
   */
  public static final String  KEY_PANEL_POSITION = "panel position";  
  
  /** 
   * The time in milliseconds until the control panel popup is hidden
   * again after showing
   */
  public int delay = 2500;  
  
  protected ControlPanel         controlPanel;
  protected Widget               baseWidget;
  protected PopupPanel           popup;
  protected boolean              popupVisible;
  protected Timer                timer;
  protected int                  lastMouseX = -1, lastMouseY = -1;
  
  public PanelOverlayBase(ControlPanel controlPanel, Widget baseWidget) {
    this.controlPanel = controlPanel;
    this.baseWidget = baseWidget;
    if (baseWidget instanceof SourcesAttachmentEvents)
      ((SourcesAttachmentEvents) this.baseWidget).addAttachmentListener(this);    
    ((HasMouseMoveHandlers)baseWidget).addMouseMoveHandler(this);
    ((HasClickHandlers)baseWidget).addClickHandler(this);
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseMoveHandler#onMouseMove(com.google.gwt.event.dom.client.MouseMoveEvent)
   */
  @Override
  public void onClick(ClickEvent event) {
    // GWT.log("clicked!");
    showPopup(event.getX(), event.getY());
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseMoveHandler#onMouseMove(com.google.gwt.event.dom.client.MouseMoveEvent)
   */
  // TODO: Change this behavior for better use with touch-screens and small screen handhelds!
  @Override
  public void onMouseMove(MouseMoveEvent event) {
    int x = event.getX();
    int y = event.getY();
    if (lastMouseX != x || lastMouseY != y) {    
      showPopup(x, y);
      lastMouseX = x;
      lastMouseY = y;
    }
  }
  
//  /* (non-Javadoc)
//   * @see com.google.gwt.event.dom.client.MouseMoveHandler#onMouseMove(com.google.gwt.event.dom.client.MouseMoveEvent)
//   */
//  @Override
//  public void onTouchEnd(TouchEndEvent event) {
//    JsArray<Touch> touches = event.getTargetTouches();
//    Touch touch = touches.get(0);
//    EventTarget target = touch.getTarget();
//    showPopup(touch.getRelativeX(target), event.getRelativeY(target));   
//  }
  
  /**
   * Hides the popup panel.
   */
  protected void hidePopup() {
    popup.hide();
    popupVisible = false;    
  }
  
  /**
   * Shows the popup panel.
   * @param x   x position where the popup shall be placed
   * @param y   y position where the popup shall be placed
   */
  protected void showPopup(int x, int y) {
    if (!popupVisible) {
      popup.setPopupPositionAndShow(this);
      popupVisible = true;
    }
    timer.schedule(delay);   
  }
}
