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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.ToggleButton;

/**
 * An enhanced version of the GWT
 * <code>ToggleButton</code> class that also fires mouse events and not just
 * click events. 
 * 
 * <p>This allows to attach tool-tips to "enhanced" toggle buttons.
 *  
 * @author ecki
 */
@Deprecated
public class EnhancedToggleButton extends ToggleButton implements
    SourcesMouseEvents {

  private final MouseListenerCollection mouseListeners;
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>.
   */  
  public EnhancedToggleButton() {
    super();
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>.
   * 
   * @param upImage image for the default(up) face of the button
   */  
  public EnhancedToggleButton(Image upImage) {
    super(upImage);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>. The supplied image is 
   * used to construct the default face of the button.
   * 
   * @param upImage image for the default (up) face of the button
   * @param listener the click listener
   */  
  public EnhancedToggleButton(Image upImage, ClickListener listener) {
    super(upImage, listener);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS);   
  }  
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>.
   * 
   * @param upImage image for the default(up) face of the button
   * @param downImage image for the down face of the button
   */  
  public EnhancedToggleButton(Image upImage, Image downImage) {
    super(upImage, downImage);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }  
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>.
   * 
   * @param upImage image for the default(up) face of the button
   * @param downImage image for the down face of the button
   * @param handler the click handler
   */  
  public EnhancedToggleButton(Image upImage, Image downImage, 
      ClickHandler handler) {
    super(upImage, downImage, handler);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS);    
  }
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>. The supplied text is used to
   * construct the default face of the button.
   * 
   * @param upText the text for the default (up) face of the button.
   */  
  public EnhancedToggleButton(String upText) {
    super(upText);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>. The supplied text is used to
   * construct the default face of the button.
   * 
   * @param upText the text for the default (up) face of the button
   * @param handler the click handler
   */  
  public EnhancedToggleButton(String upText, ClickHandler handler) {
    super(upText, handler);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS);   
  }  
  
  /**
   * Constructor for <code>EnhancedToggleButton</code>.
   * 
   * @param upText the text for the default (up) face of the button
   * @param downText the text for down face of the button
   */  
  public EnhancedToggleButton(String upText, String downText) {
    super(upText, downText);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }  
  
//  /**
//   * Constructor for <code>EnhancedToggleButton</code>.
//   * 
//   * @param upText the text for the default (up) face of the button
//   * @param downText the text for down face of the button
//   * @param listener the click listener
//   */  
//  public EnhancedToggleButton(String upText, String downText, 
//      ClickListener listener) {
//    super(upText, downText, listener);
//    mouseListeners = new MouseListenerCollection();
//    sinkEvents(Event.MOUSEEVENTS);    
//  }  
  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesMouseEvents#addMouseListener(com.google.gwt.user.client.ui.MouseListener)
   */
  public void addMouseListener(MouseListener listener) {
    mouseListeners.add(listener);
  }

  @Override
  public void onBrowserEvent(Event event) {
    switch (DOM.eventGetType(event)) {
    case Event.ONMOUSEDOWN:
    case Event.ONMOUSEUP:
    case Event.ONMOUSEMOVE:
    case Event.ONMOUSEOVER:
    case Event.ONMOUSEOUT:
      mouseListeners.fireMouseEvent(this, event);
      break;
    }
    super.onBrowserEvent(event);    
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesMouseEvents#removeMouseListener(com.google.gwt.user.client.ui.MouseListener)
   */
  public void removeMouseListener(MouseListener listener) {
    mouseListeners.remove(listener);
  }
  
}
