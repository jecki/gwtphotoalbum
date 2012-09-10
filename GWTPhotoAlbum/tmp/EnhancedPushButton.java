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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SourcesMouseEvents;

/**
 * An enhanced version of the GWT
 * <code>PushButton</code> class that also fires mouse events and not just
 * click events. 
 * 
 * <p>This allows to attach tool-tips to "enhanced" push buttons. 
 * 
 * @author ecki
 *
 */
@Deprecated
public class EnhancedPushButton extends PushButton implements
    SourcesMouseEvents {

  private final MouseListenerCollection mouseListeners;
  
  /**
   * Constructor for <code>EnhancedPushButton</code>.
   */  
  public EnhancedPushButton() {
    super();
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }
  
  /**
   * Constructor for <code>EnhancedPushButton</code>.
   * 
   * @param upImage image for the default(up) face of the button
   */  
  public EnhancedPushButton(Image upImage) {
    super(upImage);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }
  
  /**
   * Constructor for <code>EnhancedPushButton</code>. The supplied image is 
   * used to construct the default face of the button.
   * 
   * @param upImage image for the default (up) face of the button
   * @param listener the click listener
   */  
  public EnhancedPushButton(Image upImage, ClickListener listener) {
    super(upImage, listener);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS);   
  }  
  
  /**
   * Constructor for <code>EnhancedPushButton</code>.
   * 
   * @param upImage image for the default(up) face of the button
   * @param downImage image for the down face of the button
   */  
  public EnhancedPushButton(Image upImage, Image downImage) {
    super(upImage, downImage);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }  
  
  /**
   * Constructor for <code>EnhancedPushButton</code>.
   * 
   * @param upImage image for the default(up) face of the button
   * @param downImage image for the down face of the button
   * @param handler  the click handler
   */  
  public EnhancedPushButton(Image upImage, Image downImage, 
      ClickHandler handler) {
    super(upImage, downImage, handler);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS);    
  }
  
  /**
   * Constructor for <code>EnhancedPushButton</code>. The supplied text is used to
   * construct the default face of the button.
   * 
   * @param upText the text for the default (up) face of the button.
   */  
  public EnhancedPushButton(String upText) {
    super(upText);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }
  
  /**
   * Constructor for <code>EnhancedPushButton</code>. The supplied text is used to
   * construct the default face of the button.
   * 
   * @param upText the text for the default (up) face of the button
   * @param listener the click listener
   */  
  public EnhancedPushButton(String upText, ClickListener listener) {
    super(upText, listener);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS);   
  }  
  
  /**
   * Constructor for <code>EnhancedPushButton</code>.
   * 
   * @param upText the text for the default (up) face of the button
   * @param downText the text for down face of the button
   */  
  public EnhancedPushButton(String upText, String downText) {
    super(upText, downText);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS); 
  }  
  
  /**
   * Constructor for <code>EnhancedPushButton</code>.
   * 
   * @param upText the text for the default (up) face of the button
   * @param downText the text for down face of the button
   * @param handler  the click handler
   */  
  public EnhancedPushButton(String upText, String downText, 
      ClickHandler handler) {
    super(upText, downText, handler);
    mouseListeners = new MouseListenerCollection();
    sinkEvents(Event.MOUSEEVENTS);    
  }  
  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.SourcesMouseEvents#addMouseListener(com.google.gwt.user.client.ui.MouseListener)
   */
  public void addMouseListener(MouseListener listener) {
    mouseListeners.add(listener);
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.CustomButton#onBrowserEvent(com.google.gwt.user.client.Event)
   */
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
