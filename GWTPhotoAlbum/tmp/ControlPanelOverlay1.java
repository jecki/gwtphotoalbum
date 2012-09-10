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

import java.lang.Math;
import java.util.EnumSet;

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;

/**
 * <code>ControlPanelOverlay</code> puts a {@link ControlPanel} in a popup
 * window. The popup windows opens up whenever the user moves the mouse over
 * a given widget (which usually is the {@link FlipImagePanel}) and stays open 
 * for a few seconds.  
 */
public class ControlPanelOverlay extends MouseListenerAdapter 
    implements PopupPanel.PositionCallback, WindowResizeListener {
  public enum Position { NORTH_WEST, NORTH, NORTH_EAST, EAST, 
        SOUTH_EAST, SOUTH, SOUTH_WEST, WEST; }
  
  private class OverlayPopupPanel extends PopupPanel {
    OverlayPopupPanel() {
      super();
      sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
    }
    
    @Override
    public void onBrowserEvent(Event event) {
      switch (event.getTypeInt()) {
        case Event.ONCLICK:
        case Event.ONMOUSEDOWN:
        case Event.ONMOUSEUP:
        case Event.ONMOUSEMOVE: {
          if (popupVisible) timer.schedule(delay);
        }  
      }
      super.onBrowserEvent(event);
    }  
  }
  
  
  private static int MARGIN = 4;
  /** 
   * The time in milliseconds until the control panel popup is hidden
   * again after showing
   */
  public int delay = 5000;
  
  private ControlPanel       controlPanel;
  private SourcesMouseEvents baseWidget;
  private boolean            connected;
  private PopupPanel         popup;
  private boolean            popupVisible;
  private EnumSet<Position>  possiblePositions = EnumSet.allOf(Position.class);
  private Position           popupPosition;
  private Timer              timer;
  private ControlPanel.ClickSniffer clickSniffer;
  
  /**
   * Creates a new overlay control panel which takes a given control panel
   * an catches it in a popup window. The popup is opened when ever
   * <code>widget</code> fires a mouse event.
   * @param controlPanel  the control panel to be overlaid
   * @param baseWidget        the widget over which the control panel is laid
   */
  public ControlPanelOverlay(ControlPanel controlPanel, 
      SourcesMouseEvents baseWidget) {
    this.baseWidget = baseWidget;
    this.controlPanel = controlPanel;
    popup = new OverlayPopupPanel();
    popup.add(controlPanel);
    popup.setAnimationEnabled(true);
    timer = new Timer() {
      public void run() {
        popup.hide();
        popupVisible = false;
      }
    };
    clickSniffer = new ControlPanel.ClickSniffer () {
      public void click() {
        if (popupVisible) timer.schedule(delay);
      }
    };
    connected = false;
    reconnect();
  }
  
  /**
   * Disconnects the <code>ControlPanelOverlay</code> from widget over which
   * it is laid.
   */
  public void disconnect() {
    if (isConnected()) {
      baseWidget.removeMouseListener(this);
      controlPanel.setClickSniffer(null);
      connected = false;
    }
  }
  
  /**
   * Checks whether the <code>ControlPanelOverlay</code> is currently connected
   * to its base widget. 
   * @return  true, if connected
   */
  public boolean isConnected() {
    return connected;
  }
  
  @Override
  public void onMouseMove(Widget sender, int x, int y) {
    Position position;
    if (!popupVisible) {
      int w = sender.getOffsetWidth();
      int h = sender.getOffsetHeight();
      if (y < h/3) {
        if (x < w/3) position = Position.NORTH_WEST;
        else if (x > w*2/3) position = Position.NORTH_EAST;
        else position = Position.NORTH;
      } else if (y > h*2/3) {
        if (x < w/3) position = Position.SOUTH_WEST;
        else if (x > w*2/3) position = Position.SOUTH_EAST;
        else position = Position.SOUTH;        
      } else if (x < w/3) position = Position.WEST;
      else if (x > w*2/3) position = Position.EAST;
      else position = Position.NORTH;
      popupPosition = getClosestPosition(position, possiblePositions);
      popup.setPopupPositionAndShow(this);
      popupVisible = true;
    }
    // timer.schedule(delay);
  }
  
  public void onWindowResized(int width, int height) {
    if (popupVisible) {
      setPosition(popup.getOffsetWidth(), popup.getOffsetHeight());
    }
  }
  
  /**
   * Reconnects the <code>ControlPanelOverlay</code> to the widget over which
   * it is to be laid.
   */  
  public void reconnect() {
    if (!connected) {
      baseWidget.addMouseListener(this);
      controlPanel.setClickSniffer(clickSniffer);
      connected = true;
    }
  }

  public void setPosition(int offsetWidth, int offsetHeight) {
    int w = ((Widget) baseWidget).getOffsetWidth();
    int h = ((Widget) baseWidget).getOffsetHeight();
    int xpos = ((Widget) baseWidget).getAbsoluteLeft();
    int ypos = ((Widget) baseWidget).getAbsoluteTop();
    switch (popupPosition) {
      case NORTH: { 
        xpos += (w - offsetWidth) / 2;
        ypos += MARGIN;
        break;
      }
      case SOUTH: {
        xpos += (w - offsetWidth) / 2;
        ypos += h - offsetHeight - MARGIN;
        break;
      }
      case EAST: {
        xpos += w - offsetWidth - MARGIN;
        ypos += (h - offsetHeight) / 2;
        break;
      }
      case WEST: {
        xpos += MARGIN;
        ypos += (h - offsetHeight) / 2;
        break;
      }
      case NORTH_EAST: {
        xpos += w - offsetWidth - MARGIN;
        ypos += MARGIN;
        break;
      }
      case NORTH_WEST: {
        xpos += MARGIN;
        ypos += MARGIN;
        break;
      }
      case SOUTH_EAST: {
        xpos += w - offsetWidth - MARGIN;
        ypos += h - offsetHeight - MARGIN;
        break;
      }
      case SOUTH_WEST: {
        xpos += MARGIN;
        ypos += h - offsetHeight - MARGIN;
      }
    }
    popup.setPopupPosition(xpos, ypos);
  }  
  
  public void setPossiblePositions(EnumSet<Position> allowed) {
    assert (!allowed.isEmpty());
    
    possiblePositions = allowed;
  }
   
  protected Position getClosestPosition(Position position, 
      EnumSet<Position> possiblePositions) {
    if (possiblePositions.contains(position)) return position;
    
    Position best = null;
    int cmp, diff = 100;
    int ord = position.ordinal();
    for (Position pos: possiblePositions) {
      cmp = Math.abs(pos.ordinal() - ord);
      if (cmp < diff) {
        best = pos;
        diff = cmp;
      }
    }
    return best;
  }  
}
