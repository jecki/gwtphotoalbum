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

import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.safehtml.shared.SafeHtml;


/**
 * A "tool tip" widget.
 * 
 * <p>A tool tip widget pops up a small yellow tool tip window 
 * if the user leaves the mouse still over a widget to which the 
 * tool tip is attached. This works only with widgets that source
 * mouse events.  
 * 
 * <p>Optionally, a limit can be set to how many times the tool
 * tip may appear. The purpose is to avoid menacing the user
 * with constant re-appearances of the tool tip. 
 */
public class Tooltip extends PopupPanel implements MouseDownHandler, 
    MouseOverHandler, MouseOutHandler, MouseMoveHandler, MouseUpHandler {
  private class PopupTimer extends Timer {
    int posX, posY;
    final PositionCallback callback = new PositionCallback() {
      public void setPosition(int offsetWidth, int offsetHeight) {
        int width = Window.getClientWidth();
        int height = Window.getClientHeight();
        if (posX + offsetWidth > width) posX = width-offsetWidth;
        if (posY + offsetHeight > height) posY = height-offsetHeight;
        setPopupPosition(posX, posY);
      }
    };
    public void run() {
      timerExpired = true;
      if (limit > 0) limit--;
      setPopupPositionAndShow(callback);
    }
  }
  
  private static final int X_OFFSET = 10;
  private static final int Y_OFFSET = 10;
  private static final int DEFAULT_DELAY = 1000;
  private static Tooltip   visible = null;
  
  /**
   * Adds a tool-tip to a widget. The widget must have
   * <code>MouseOut</code> and <code>MouseOver</code> handlers. It is 
   * recommended that it also has <code>MouseMove, -Up</code> and
   * <code>-Down</code> handlers. 
   * 
   * <p>Typical coding pattern:
   * <p><code>Tooltip.addToWidget(new Tooltip("tip"), anyWidget)</code>
   * 
   * @param t the tool-tip that shall be added
   * @param w the widget to which the tool-tip shall be added
   */
  public static void addToWidget(Tooltip t, Widget w) {
    assert w instanceof HasMouseOutHandlers;
    assert w instanceof HasMouseOverHandlers;
    ((HasMouseOutHandlers) w).addMouseOutHandler(t);
    ((HasMouseOverHandlers) w).addMouseOverHandler(t);
    if (w instanceof HasMouseDownHandlers) {
      ((HasMouseDownHandlers) w).addMouseDownHandler(t);
    }
    if (w instanceof HasMouseUpHandlers) {
      ((HasMouseUpHandlers) w).addMouseUpHandler(t);
    }
    if (w instanceof HasMouseMoveHandlers) {
      ((HasMouseMoveHandlers) w).addMouseMoveHandler(t);
    }
  }
  
  private int               delay;
  private final  HTML       tooltipText;
  private final  PopupTimer timer = new PopupTimer();
  private boolean           timerExpired = false;
  private int               limit = -1; // < 0 means no limit on the number of appearances!
  
  
  
  /**
   * Constructor of class <code>Tooltip</code>.
   * @param htmlText  the text of the tool tip.
   */
  public Tooltip(String htmlText) {
    super(true);
    tooltipText = new HTML(htmlText);
    this.setWidget(tooltipText);
    addStyleName("tooltip");
    delay = DEFAULT_DELAY;
  }

  /**
   * Constructor of class <code>Tooltip</code>.
   * @param htmlText  the text of the tool tip.
   */
  public Tooltip(SafeHtml htmlText) {
    this(htmlText.asString());
  }  
  
  /**
   * Alternative constructor that allows also determining the delay after which
   * the tool tip appears.
   * 
   * @param htmlText the text of the tool tip
   * @param delay    the delay in milliseconds
   */
  public Tooltip(String htmlText, int delay) {
    this(htmlText);
    this.delay = delay;
  }

  /**
   * Alternative constructor that allows also determining the delay after which
   * the tool tip appears.
   * 
   * @param htmlText the text of the tool tip
   * @param delay    the delay in milliseconds
   */
  public Tooltip(SafeHtml htmlText, int delay) {
    this(htmlText.asString(), delay);
  }  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#hide()
   */
  @Override
  public void hide() {
    super.hide();
    visible = null;    
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseDownHandler#onMouseDown(com.google.gwt.event.dom.client.MouseDownEvent)
   */
  public void onMouseDown(MouseDownEvent event) { 
    timer.cancel();
    hide();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
   */
  public void onMouseOver(MouseOverEvent event) {
    Widget sender = (Widget) event.getSource();
    timer.posX = sender.getAbsoluteLeft() + sender.getOffsetWidth() - X_OFFSET; 
    timer.posY = sender.getAbsoluteTop() + Toolbox.getOffsetHeight(sender) - Y_OFFSET;
    timerExpired = false;
    if (limit != 0) timer.schedule(delay);
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
   */
  public void onMouseOut(MouseOutEvent event) {
    timer.cancel();
    hide();
    timerExpired = false;
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseMoveHandler#onMouseMove(com.google.gwt.event.dom.client.MouseMoveEvent)
   */
  public void onMouseMove(MouseMoveEvent event) {
    if (visible != null) {
      visible.hide(); 
    } else if (!timerExpired) {
      timer.posX = event.getClientX() + X_OFFSET;
      timer.posY = event.getClientY() + Y_OFFSET;
      if (limit != 0) timer.schedule(delay);
    }
  }


  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.MouseUpHandler#onMouseUp(com.google.gwt.event.dom.client.MouseUpEvent)
   */
  public void onMouseUp(MouseUpEvent event) {  
    timer.cancel();
    hide();
  }

  /**
   * Set the maximum number of re-appearances  for the tool tip.
   * @param limit  the maximum number of re-appearances
   */
  public void setMaxAppearances(int limit) {
    this.limit = limit;
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel#show()
   */
  @Override
  public void show() {
    if (visible != null && visible != this) {
      visible.hide();
    }
    visible = this;    
    super.show();
  }
}

