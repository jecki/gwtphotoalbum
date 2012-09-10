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

// import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
// import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines a movable "pop up" panel.
 * 
 * <p>A movable "pop up" panel can be 
 * moved on the screen by the user by dragging its border. Other than the
 * gwt-<code>DialogBox</code> it does not have a title bar. Also the
 * "movability" of the <code>MovablePopupPanel</code> can be turned on or
 * off (default is on).
 *
 */
public class MovablePopupPanel extends PopupPanel implements
  MouseDownHandler, MouseMoveHandler, MouseUpHandler {
  
  private boolean dragging = false;
  private int     dragStartX, dragStartY;
  private MouseListenerCollection mouseListeners;
  private boolean movable = true;
  
  /**
   * The constructor for class <code>MovablePopupPanel</code>.
   */
  public MovablePopupPanel() {
    super();
    mouseListeners = new MouseListenerCollection();      
    mouseListeners.add(this);
    sinkEvents(Event.MOUSEEVENTS);
  }

  /**
   * Returns true, if the popup panel is movable.
   * @return true, if the popup panel is movable, false otherwise
   */
  public boolean getMovable() {
    return movable;
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user.client.Event)
   */
  @Override
  public void onBrowserEvent(Event event) { 
    super.onBrowserEvent(event);  
    
    if (!dragging && isTargetChildWidget(event))// !isTargetFrame(event.getTarget()))
      return;
    
    switch (event.getTypeInt()) {
      case Event.ONMOUSEDOWN:
      case Event.ONMOUSEUP:
      case Event.ONMOUSEMOVE:
      case Event.ONMOUSEOVER:       
      case Event.ONMOUSEOUT:       
        mouseListeners.fireMouseEvent(this.getWidget(), event); 
        break;
    }
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#onEventPreview(com.google.gwt.user.client.Event)
   */
  @Override
  public boolean onEventPreview(Event event) {
    if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
      if (!DOM.isOrHasChild(this.getWidget().getElement(), DOM.eventGetTarget(event))) {
        DOM.eventPreventDefault(event);
      }
    }
    return super.onEventPreview(event);
  }      
  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google.gwt.event.dom.client.MouseDownEvent)
   */
  public void onMouseDown(MouseDownEvent event) {
    if (movable) {
      dragging = true;
      DOM.setCapture(getElement());
      dragStartX = event.getX();
      dragStartY = event.getClientY();
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseMove(MouseMoveEvent event) {
    if (dragging) {
      // this is a work around, because this.getAbsoluteWidth() causes an "uncaught exception" error
      Widget sender = (Widget) event.getSource();
      int x = event.getX();
      int y = event.getY();
      int absX = x + sender.getAbsoluteLeft() - 
          (getOffsetWidth() - sender.getOffsetWidth())/2;
      int absY = y + sender.getAbsoluteTop() - 
          (getOffsetWidth() - sender.getOffsetWidth())/2;
      int xpos = absX - dragStartX;
      int ypos = absY - dragStartY;
      if (xpos + getOffsetWidth() > Window.getClientWidth()) {
        xpos = Window.getClientWidth() - getOffsetWidth();
      }
      if (ypos + getOffsetHeight() > Window.getClientHeight()) {
        ypos = Window.getClientHeight() - getOffsetHeight();
      }
      if (xpos < 0) xpos = 0;
      if (ypos < 0) ypos = 0;
      setPopupPosition(absX - dragStartX, absY - dragStartY);
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseUpHandler#onMouseUp(com.google.gwt.event.dom.client.MouseUpEvent)
   */
  public void onMouseUp(MouseUpEvent event) {
    if (dragging) DOM.releaseCapture(getElement());
    dragging = false;
  }
  
  /**
   * Turns the movability of the popup panel on or off.
   * @param movable if true, the popup panel is movable.
   */
  public void setMovable(boolean movable) {
    this.movable = movable;
  }
  
  private boolean isTargetChildWidget(Event event) {
    Widget child = getWidget();
    int x = child.getAbsoluteLeft();
    int y = child.getAbsoluteTop();
    int w = child.getOffsetWidth();
    int h = child.getOffsetHeight();
    int ex = event.getClientX();
    int ey = event.getClientY();
    
    return (ex >= x && ey >= y && ex < x+w && ey < y+h) ||
        this.getWidget().getElement().isOrHasChild(event.getTarget());
  }
  
//  private boolean isTargetFrame(Element target) {
//    return !this.getWidget().getElement().isOrHasChild(target);
//    return getCellElement(0, 0).getParentElement().isOrHasChild(target) ||
//        getCellElement(0, 1).getParentElement().isOrHasChild(target) ||       
//        getCellElement(0, 2).getParentElement().isOrHasChild(target) ||
//        getCellElement(1, 0).getParentElement().isOrHasChild(target) ||
//        getCellElement(1, 2).getParentElement().isOrHasChild(target) ||
//        getCellElement(2, 0).getParentElement().isOrHasChild(target) ||
//        getCellElement(2, 1).getParentElement().isOrHasChild(target) ||
//        getCellElement(2, 2).getParentElement().isOrHasChild(target);           
//  }  
   
}
