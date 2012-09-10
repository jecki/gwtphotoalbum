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

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ecki
 *
 */
public class Tooltip extends PopupPanel implements MouseListener {
  protected class PopupTimer extends Timer {
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
      setPopupPositionAndShow(callback);
    }
  }
  
  private static final int X_OFFSET = 10;
  private static final int Y_OFFSET = 10;
  private static final int DELAY = 1000;
  
  private final HTML       tooltipText;
  private final PopupTimer timer = new PopupTimer();
  private boolean visible = false;
  
  public Tooltip(String htmlText) {
    super(true);
    tooltipText = new HTML(htmlText);
    this.setWidget(tooltipText);
    addStyleName("tooltip");
  }

  @Override
  public void hide() {
    super.hide();
    visible = false;    
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseDown(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseDown(Widget sender, int x, int y) { 
    timer.cancel();
    hide();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseEnter(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseEnter(Widget sender) {
    timer.posX = sender.getAbsoluteLeft() + sender.getOffsetWidth() - X_OFFSET; 
    timer.posY = sender.getAbsoluteTop() + sender.getOffsetHeight() - Y_OFFSET;
    timer.schedule(DELAY);
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseLeave(com.google.gwt.user.client.ui.Widget)
   */
  public void onMouseLeave(Widget sender) {
    timer.cancel();
    hide();
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseMove(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseMove(Widget sender, int x, int y) {
    if (visible) {
      hide(); 
    } else {
      timer.posX = sender.getAbsoluteLeft() + X_OFFSET + x;
      timer.posY = sender.getAbsoluteTop() + Y_OFFSET + y;
      timer.schedule(DELAY);
    }
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.MouseListener#onMouseUp(com.google.gwt.user.client.ui.Widget, int, int)
   */
  public void onMouseUp(Widget sender, int x, int y) {  
    timer.cancel();
    hide();
  }

  @Override
  public void show() {
    visible = true;    
    super.show();
  }
}
