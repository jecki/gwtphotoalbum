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
//import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.Image;

/**
 * Compatibility layer to evade a bug or feature (?) of the Internet Explorer
 * Browser.
 */
public class Compatibility {
  /** true, if the browser is an Internet Explorer older than version 9 */
  static final boolean oldIE;
  /** true, if the browser supports shadowed text */
  static final boolean supportsTextShadow;
  
  static {
    int i = isInternetExplorer(); 
    if (i > 0 && i < 9) oldIE = true;
    else oldIE = false;
    
    if (checkSupportsTextShadow() != 0)
      supportsTextShadow = true;
    else
      supportsTextShadow = false;
  }
  
  /**
   * Fires the <code>onLoad</code> events of the <code>LoadListeners</code>
   * manually in case the browser used is the MS Internet Explorer. This 
   * workaround is necessary, because the Internet Explorer sometimes omits 
   * to fire <code>onLoad</code>events.
   * 
   * <p><code>ieFireLoad</code> should be called <em>after</em> the 
   * <code>Image.setUrl()</code> method has 
   * been called if the image was not created
   * with a specific URL already.
   *  
   * @param img  the image for which the <code>LoadListeners</code> are to be
   *             notified of the image's being loaded.
   * @param url  the URL of the image. This is needed to keep trace of the 
   *             image that IE (supposedly) has cached. 
   * @param handler a {@link LoadHandler} to be notified or <code>null</code>
   */
  public static void fireLoadOnIE (Image img, String url, 
      LoadHandler handler) {
    if (oldIE) {
      if (handler != null) {
        // handler.onLoad(img);
        NativeEvent loadEvent = Document.get().createLoadEvent();
        DomEvent.fireNativeEvent(loadEvent, img);
      }
    }
  }
    
  /**
   * Checks, if the browser used is the Microsoft Internet Explorer.
   * @return  Internet Explorer version or 0 if the browser used is not
   *          the Internet Explorer.
   */
  private static native int isInternetExplorer() /*-{
    var ua = navigator.userAgent;
    var re  = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
    if (re.exec(ua) != null)
      return parseInt(RegExp.$1);
    else 
    return 0;    
  }-*/; 

//    var agent = navigator.userAgent.toLowerCase();
//    var version=parseFloat(navigator.appVersion.split("MSIE")[1]);
//    if ((agent.indexOf("msie") != -1) && version < 8.0) return 1;
//    return 0;      

  
  /**
   * Checks whether the browser is new enough to support shadowed text.
   * The implementation of this method is OUTDATED
   * @return  -1, if the browser supports shadowed text, 0 otherwise
   */
  private static native int checkSupportsTextShadow() /*-{
    var agent = navigator.userAgent.toLowerCase();
    if (agent.indexOf("msie") != -1) return -11;
    return 0;    
  }-*/; 
  
}
