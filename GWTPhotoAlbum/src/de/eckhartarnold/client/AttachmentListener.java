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

import com.google.gwt.user.client.ui.Widget;

/**
 * Listener for the "attachment" events <code>onLoad, onUnload</code>.
 * 
 * <p>Classes implementing the <code>AttachmentListener</code> interface can
 * take notice when another widget becomes "loaded" or "unload", i.e. after
 * the widget is attached to or detached from the document displayed in the
 * browser window.
 * 
 * @author ecki
 *
 */
public interface AttachmentListener {
  /**
   * Takes notice that the widget <code>sender</code> has been attached to
   * the browser window. 
   * @param sender  the widget which has been attached to the browser window
   */
  void onLoad(Widget sender);
  
  /**
   * Takes notice that the widget <code>sender</code> has been removed from
   * the browser window. 
   * @param sender  the widget which has been removed from the browser window
   */  
  void onUnload(Widget sender);
}

