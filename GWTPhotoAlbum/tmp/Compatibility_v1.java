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

/*
 * // Browser Detection Javascript
// copyright 1 February 2003, by Stephen Chapman, Felgall Pty Ltd

// You have permission to copy and use this javascript provided that
// the content of the script is not changed in any way.

function whichBrs() {
var agt=navigator.userAgent.toLowerCase();
if (agt.indexOf("opera") != -1) return 'Opera';
if (agt.indexOf("staroffice") != -1) return 'Star Office';
if (agt.indexOf("webtv") != -1) return 'WebTV';
if (agt.indexOf("beonex") != -1) return 'Beonex';
if (agt.indexOf("chimera") != -1) return 'Chimera';
if (agt.indexOf("netpositive") != -1) return 'NetPositive';
if (agt.indexOf("phoenix") != -1) return 'Phoenix';
if (agt.indexOf("firefox") != -1) return 'Firefox';
if (agt.indexOf("safari") != -1) return 'Safari';
if (agt.indexOf("skipstone") != -1) return 'SkipStone';
if (agt.indexOf("msie") != -1) return 'Internet Explorer';
if (agt.indexOf("netscape") != -1) return 'Netscape';
if (agt.indexOf("mozilla/5.0") != -1) return 'Mozilla';
if (agt.indexOf('\/') != -1) {
if (agt.substr(0,agt.indexOf('\/')) != 'mozilla') {
return navigator.userAgent.substr(0,agt.indexOf('\/'));}
else return 'Netscape';} else if (agt.indexOf(' ') != -1)
return navigator.userAgent.substr(0,agt.indexOf(' '));
else return navigator.userAgent;
}
 */

package de.eckhartarnold.client;

import java.lang.String;
import java.util.HashSet;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.LoadListener;

/**
 * @author ecki
 *
 */
public class Compatibility {
  private static HashSet<String>  cached = new HashSet<String>();
  static boolean isIE;
  
  static {
    if (isInternetExplorer() == 1) isIE = true;
    else isIE = false;
  }
  
  /**
   * Fires the <code>onLoad</code> events of the <code>LoadListeners</code>
   * manually in case the browser used is the MS Internet Explorer and the
   * image has already been preloaded into the cache. This workaround is
   * necessary, because the Internet Explorer omits to fire <code>onLoad</code>
   * events for cached images. 
   * 
   * <p><code>ieFireLoad</code> should be called <em>after</em> the 
   * {@link Image.setUrl} method has been called if the image was not created
   * with a specific URL already.
   * 
   * <p>This method works only in collaboration with the {@link prefetchImage}
   * method, i.e. in order for it to work properly, <code>Image.prefetch</code>
   * must never be called directly, but prefetching images must always be done
   * with the static <code>prefetchImage()</code> method of class 
   * <code>Compatibility</code> 
   * 
   * @param img  the image for which the <code>LoadListeners</code> are to be
   *             notified of the image's being loaded.
   * @param url  the URL of the image. This is needed to keep trace of the 
   *             image that IE (supposedly) has cached. 
   * @param l1   the first listener to be notified or <code>null</code>.
   * @param l2   the second listener to be notified or <code>null</code>
   */
  public static void fireLoadOnIE (Image img, String url, LoadListener l1,
      LoadListener l2) {
    if (isIE) {
      if (cached.contains(url)) {
        Debugger.print("call listeners!");
        
        if (l1 != null) l1.onLoad(img);
        if (l2 != null) l2.onLoad(img);
      } else cached.add(url);
    }
  }
  
  /**
   * Prefeteches the image at the given URL. If the browser used is the
   * Internet Explorer then the URL will be registered as cached.
   * Method {@link ieFireLoad} uses this information to call the image's
   * <code>LoadListeners</code> manually, because the Internet Explorer
   * omits to notify the listeners of images that are loaded from the
   * cache.
   * 
   * @param url  the image's URL
   */
  public static void prefetchImage(String url) {
    Image.prefetch(url);
    if (isIE) cached.add(url);    
  }

  private static native int isInternetExplorer() /*-{
    var agent = navigator.userAgent.toLowerCase();
    if (agent.indexOf("msie") != -1) return 1;
    return 0;    
  }-*/; 
}
