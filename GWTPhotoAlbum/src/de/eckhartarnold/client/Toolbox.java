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

// import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Some tool box functions used at several places in the code. Usually, these
 * provide workarounds for known issues and browsers incompatibilities.
 *
 */
public final class Toolbox {
  /** Constants user device classes */
  public final static int PC = 1, PAD = 2, PHONE = 3;
  
  /**
   * Returns the offset height of an UI object. For some reason, this is often
   * zero in non-quirks mode. In this case the offset height will be guessed
   * with a very crude algorithm based on the client window's size.
   * @param uiobject the UIObject, the height of which shall be determined
   * @return the (assumed) offset height of an UI object.
   */
  public static int getOffsetHeight(UIObject uiobject) {
    int height = uiobject.getOffsetHeight();
    if (height == 0) {
      int clientW = Window.getClientWidth();
      int clientH = Window.getClientHeight();
      int width = uiobject.getOffsetWidth();
      height = clientH * width / clientW;
    }
    return height;
  }
  
  /**
   * Returns the offset width of an UI object. Usually, the same as
   * <code>uiobject.getOffsetWidth()</code>
   * @param uiobject the UIObject for which the width shall be determined
   * @return the offset width of uiobject.
   */
  public static int getOffsetWidth(UIObject uiobject) {
    return uiobject.getOffsetWidth();
  }
  
  /**
   * Determines the kind of device (personal computer, smartphone or
   * touchpad) that the user has.
   * @return  constant describing the device; either PC, PAD, or PHONE.
   */
  public static int detectUserDeviceType() {
    // TODO: Determine user device type
    return PC;
  }
  
  /**
   * Returns the user's device's screen size in inches. Most of the
   * time this will be just an informed guess.
   * 
   * @return screen size of the user's device in inches.
   */
  public static float detectUserScreenSize() {
    // TODO: Determine user screen size in inches! HOW?
    int device = detectUserDeviceType();
    if (device == PC) {
      return 22.0f;
    } else if (device == PHONE) {
      return 4.0f;
    } else {
      return 10.0f;
    }
    
  }
}
