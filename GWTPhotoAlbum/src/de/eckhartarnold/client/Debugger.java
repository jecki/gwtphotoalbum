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

import java.lang.String;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.InlineHTML;
// import com.google.gwt.user.client.ui.Label;
// import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * A very simple "printf"-type debugger.
 * 
 * <p>Class <code>Debugger</code> opens a scrollable popup up window, the 
 * first time its 
 * <code>print</code>method is called and displays all subsequent messages 
 * that will be output via the print message. Class <code>Debugger</code> is
 * useful for doing rudimentary debuggers in browsers other than the GWT
 * hosted mode browser (for which the eclipse debugger and GWT.log() can be
 * used).
 * 
 * @author ecki
 */
public class Debugger implements ResizeHandler {
  /** the close button of the debugger */
  protected static Button        closeButton;
  /** the sole instance of class debugger */
  protected static Debugger      singleton;
  /** the dialog box that the debugger pops up */
  protected static DialogBox     dialog;
  /** the panel on which the debuggers messages are displayed */
  protected static VerticalPanel messagePanel;  
  /** the scroll panel which contains the message panel */
  protected static ScrollPanel   scrollPanel;
  /** the master panel containing the scroll panel and the
   * close button*/
  protected static VerticalPanel panel;
  /** flag to indicate whether the debugger was closed by the user */
  protected static boolean       closedManually = false;
  private   static int           width = 0, height = 0;
  private   static boolean       snapRight, snapBottom;
  
  /**
   * Hides the debugger if it is visible.
   */
  public static void hide() {
    if (singleton == null) init();    
    dialog.hide();
    closedManually = false;
  }
  
  /**
   * Opens the debugger window (if it was closed) and prints a message in the
   * debugger window.
   * @param txt  the message to be printed in the debugger's window
   */
  public static void print(String txt) {
    // assert singleton != null : 
    //   "Call Debugger.init() before using the Debugger!";
    if (singleton == null || closedManually) show();
    
    if (messagePanel != null) {
      messagePanel.add(new InlineHTML(txt+"<br />"));
      scrollPanel.scrollToBottom();
    }
    //singleton.onWindowResized(Window.getClientWidth(), 
    //    Window.getClientHeight());      
  }

  /**
   * Sets the size of the debugger
   * @param width  the width of the debugger in pixel
   * @param height the height of the debugger in pixel
   */
  public static void setSize(int width, int height) {  
    Debugger.width = width+18;
    Debugger.height = height+40;
    if (singleton != null) {
      scrollPanel.setPixelSize(width, height);      
      singleton.resize(Window.getClientWidth(), Window.getClientHeight()); 
    }
  }
  
  /**
   * Shows the debugger window, if it was hidden or instantiates the debugger
   * and opens the debugger window, if the debugger was not yet instatiated.
   */
  public static void show() {
    if (singleton == null) init();
    dialog.show();
  }  
  
  /** 
   * Determines the corner to which the debugger window shall "snap".
   * 
   * @param right  if true, the debugger window "snaps" to the right
   * @param bottom if true, the debugger window "snaps" to the bottom
   */
  public static void snapDialog(boolean right, boolean bottom) {
    snapRight = right;
    snapBottom = bottom;
  }  
  
  /**
   * Instantiates the debugger class. Because debugger is a singleton, an error
   * will be raised, if this method is called for the second time. 
   */
  protected static void init() {
    assert singleton == null: "Tried to create the debugger twice!";
    singleton = new Debugger();     
  }

  /**
   * The constructor of the debugger. As class <code>Debugger</code> is 
   * instantiated by the first call to its static print method, this
   * constructor is a <code>protected</code> constructor.
   */
  protected Debugger() {
    dialog = new DialogBox(false, false);
    dialog.addStyleName("debugger");
    dialog.setText("Debug");
    panel = new VerticalPanel();
    messagePanel = new VerticalPanel();
    scrollPanel = new ScrollPanel(messagePanel);
    panel.add(scrollPanel);
    closeButton = new Button("close", new ClickHandler() {
      public void onClick(ClickEvent event) {
        Debugger.hide();
        closedManually = true;
      }
    });
    panel.add(closeButton);
    dialog.setWidget(panel);
    snapRight = true;
    snapBottom = true;
    if (width <= 0) width = 400;
    if (height <= 0) height = 300;
    // dialog.getElement().getStyle().setProperty("z-index", "100");    
    dialog.show();  
    scrollPanel.setPixelSize(width-18, height-40);  
    resize(Window.getClientWidth(), Window.getClientHeight());
    Window.addResizeHandler(this);    
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.WindowResizeListener#onWindowResized(int, int)
   */
  public void onResize(ResizeEvent event) {
    resize(event.getWidth(), event.getHeight());
  }
  
  private void resize(int newWidth, int newHeight) {
    int x, y;
    if (snapRight) 
      // x = width - dialog.getOffsetWidth() - 5;
      x = newWidth - Debugger.width - 25;
    else
      x = dialog.getPopupLeft();
    if (snapBottom)
      // y = height - dialog.getOffsetHeight() - 5;
      y = newHeight - Debugger.height - 55;
    else
      y = dialog.getPopupTop();
    dialog.setPopupPosition(x, y);     
  }
}

