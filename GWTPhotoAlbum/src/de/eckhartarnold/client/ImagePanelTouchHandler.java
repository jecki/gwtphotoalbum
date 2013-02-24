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

import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;

/**
 * Handles touch events for the image panel.
 * 
 * @author eckhart
 *
 */
public class ImagePanelTouchHandler implements TouchStartHandler,
    TouchEndHandler, TouchMoveHandler {

  private Slideshow slideshow;
  
  /**
   * The constructor of class <code>ImagePanelTouchHandler</code>. 
   * The touch handler is initialized with a slide show.
   *  
   * @param slideshow the slide show to which this control panel shall be 
   *                  attached
   */
  public ImagePanelTouchHandler(Slideshow slideshow) {
    this.slideshow = slideshow;
    ImagePanel imp = this.slideshow.getImagePanel();
    imp.addTouchStartHandler(this);
    imp.addTouchMoveHandler(this);
    imp.addTouchEndHandler(this);
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.TouchMoveHandler#onTouchMove(com.google.gwt.event.dom.client.TouchMoveEvent)
   */
  @Override
  public void onTouchMove(TouchMoveEvent event) {
    event.preventDefault();
    // TODO Auto-generated method stub    
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.TouchEndHandler#onTouchEnd(com.google.gwt.event.dom.client.TouchEndEvent)
   */
  @Override
  public void onTouchEnd(TouchEndEvent event) {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.dom.client.TouchStartHandler#onTouchStart(com.google.gwt.event.dom.client.TouchStartEvent)
   */
  @Override
  public void onTouchStart(TouchStartEvent event) {
    // TODO Auto-generated method stub

  }

}
