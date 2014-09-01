/*
 * Copyright 2013 Eckhart Arnold (eckhart_arnold@hotmail.com).
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
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchMoveHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

/**
 * Low level touch event handler.
 * 
 * Interprets touch screen events and sends high level events to a receiver.
 * 
 * @author eckhart
 *
 */
public class TouchScreen implements TouchStartHandler,
    TouchEndHandler, TouchMoveHandler {
  
  /** distance threshold before a touch is interpreted as the beginning of a 
   * move event
   */
  private static int MOVE_THRESHOLD = 32;
  private static int NO_DIRECTION = 0, HORIZONTAL = 1, VERTICAL = -1;
  private static int DOUBLE_TAP_TIME = 1000;
  
  private Element element;
  private TouchScreenReceiver receiver;
  private int id, tapX, tapY, startX, startY, moveDelta;
  private boolean tapped = false;
  private int direction = NO_DIRECTION;

  
  /**
   * The constructor of class <code>TouchScreen</code>.
   *  
   * Class <code>TouchScreen</code> is initialized with a widget that must 
   * implement the HasTouchStartHandlers, HasTouchMoveHandlers and 
   * HasTouchEndHandlers.
   *  
   * @param widget   the widget for which touch screen events shall be 
   *                 interpreted
   * @param receiver the touch screen receiver that receives the high
   *                 level events.
   */
  public TouchScreen(Widget widget, TouchScreenReceiver receiver) {
    this.receiver = receiver;
    this.element = widget.getElement();
    ((HasTouchStartHandlers) widget).addTouchStartHandler(this);
    ((HasTouchMoveHandlers) widget).addTouchMoveHandler(this);
    ((HasTouchEndHandlers) widget).addTouchEndHandler(this);
  }
 
  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.TouchMoveHandler#onTouchMove(com.google.gwt.event.dom.client.TouchMoveEvent)
   */
  @Override
  public void onTouchMove(TouchMoveEvent event) {
    event.preventDefault();
    Touch t = filterTouch(event.getChangedTouches(), id);
    if (t != null) {
      int x = t.getRelativeX(element);
      int y = t.getRelativeY(element);
      
      if (direction == NO_DIRECTION) {
        int absDX = Math.abs(x-startX);
        int absDY = Math.abs(y-startY);
        if (absDX > MOVE_THRESHOLD  || 
            absDY > MOVE_THRESHOLD) {
          if (absDX >= absDY) {
            direction = HORIZONTAL;
          } else {
            direction = VERTICAL;
          }
          tapped = false;
        }
      }
      
      int dX = x - startX;
      int dY = y - startY;
      
      if (direction == VERTICAL && dX != moveDelta) {
        moveDelta = dX;
        receiver.leftRightSwipe(dX);
      } else if (direction == HORIZONTAL && dY != moveDelta) {
        moveDelta = dY;
        receiver.upDownSwipe(dY);
      }
    }
  }

  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.TouchEndHandler#onTouchEnd(com.google.gwt.event.dom.client.TouchEndEvent)
   */
  @Override
  public void onTouchEnd(TouchEndEvent event) {
    Touch t = filterTouch(event.getChangedTouches(), id);
    if (t != null) {
      int x = t.getRelativeX(element);
      int y = t.getRelativeY(element);
      if (tapped && Math.abs(x-tapX) <= MOVE_THRESHOLD 
          && Math.abs(y-tapY) <= MOVE_THRESHOLD) {
        tapped = false;
        receiver.doubleTap(x, y);
      } else if (direction == NO_DIRECTION) {
        if (Math.abs(x-startX) <= MOVE_THRESHOLD  && 
            Math.abs(y-startY) <= MOVE_THRESHOLD) {
          if (!receiver.tap(x, y)) {
            tapX = startX;
            tapY = startY;            
            tapped = true;
            Timer doubleTapTimer = new Timer() {
              @Override
              public void run() {
                tapped = false;
              }
            };
            doubleTapTimer.schedule(DOUBLE_TAP_TIME);
          }
        }
      } else {
        if (direction == HORIZONTAL) {
          receiver.endLeftRightSwipe(x - startX);
        } else {
          receiver.endUpDownSwipe(y - startY);
        }
        direction = NO_DIRECTION;
      }
    } else GWT.log("touch end event, but no touch with the same identifier");
  }

  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.TouchStartHandler#onTouchStart(com.google.gwt.event.dom.client.TouchStartEvent)
   */
  @Override
  public void onTouchStart(TouchStartEvent event) {
    JsArray<Touch> touches = event.getTouches();
    for (int i = 0; i < touches.length(); i++) {
      Touch t = touches.get(i);
      EventTarget tgt = t.getTarget();
      if (Element.is(tgt) && Element.as(tgt) == element) {
        id = t.getIdentifier();
        startX = t.getRelativeX(element);
        startY = t.getRelativeY(element);
        direction = NO_DIRECTION;
        moveDelta = 0;
        break;
      }
    }
  } 

  
  /**
   * Returns the first touch in an array of touches with the same
   * identifier as a given identifier.
   * @param touches  the array of touches to be searched
   * @param id       the identifier for which a touch shall be returned
   * @return   the touch with identifier <code>id</code> or <code>null</code>
   */
  private static Touch filterTouch(JsArray<Touch> touches, int id) {
    for (int i = 0; i < touches.length(); i++) {
      Touch t = touches.get(i);
      if (t.getIdentifier() == id) return t;
    }
    return null;
  }
}
