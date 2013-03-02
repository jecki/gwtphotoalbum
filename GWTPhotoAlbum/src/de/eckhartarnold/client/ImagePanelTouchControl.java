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

import com.google.gwt.core.client.GWT;

/**
 * @author eckhart
 *
 */
public class ImagePanelTouchControl implements TouchScreenReceiver {

  private Slideshow slideshow;
  
  /**
   * The constructor of class <code>ImagePanelTouchHandler</code>. 
   * The touch handler is initialized with a slide show.
   *  
   * @param slideshow the slide show to which this control panel shall be 
   *                  attached
   */
  public ImagePanelTouchControl(Slideshow slideshow) {
    this.slideshow = slideshow;
    ImagePanel imp = this.slideshow.getImagePanel();
  }

  public boolean tap(int x, int y) {
    GWT.log("Single Touch - Position: " + x + "," + y);
    return false;
  }
  
  public void endLeftRightSwipe(int delta) {
    GWT.log("Horizontal Swipe End - Delta: " + delta);
  }
  
  public void endUpDownSwipe(int delta) {
    GWT.log("Vertical Swipe End - Delta: " + delta);
  }
  
  public void doubleTap(int x, int y) {
    GWT.log("Double Tap - Position: " + x + "," + y);
  }

  public void leftRightSwipe(int delta) {
    GWT.log("Horizontal Swipe - Delta: " + delta);    
  }
  
  public void upDownSwipe(int delta) {
    GWT.log("Vertical Swipe - Delta: " + delta);    
  }
  
}
