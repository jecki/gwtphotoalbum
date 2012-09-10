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

/**
 * Interface to be implemented by widgets that
 * will be informed if their parent widget has been resized. 
 */
public interface ResizeListener {
  /**
   * Called after a parent widget has been resized.
   */
  void onResized();
  
  /**
   * Called after a parent widget has been resized, but before the new size
   * is processed via <code>onResized</code>. The method <code>prepareResized</code>
   * is needed to allow composite widgets that contain absolute panels to
   * temporarily detach these absolute panels in order to allow the browser
   * to calculate widget sizes correctly. The implementation of 
   * <code>onResized</code> can then re-attach the detached absolute panels.  
   * Methods <code>prepareResized</code> and <code>onResized</code> should
   * therefore always be used in conjunction.
   */
  void prepareResized();
}
