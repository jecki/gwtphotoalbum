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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;

/**
 * This class gives access to the status tag of the main html page. The
 * status tag is identified by the id "statusTag". 
 * 
 * <p>The purpose of the status tag is to inform the user about the ongoing
 * loading and initialisation process before the widget tree is build up.
 * It is suggested that its default value is the string "loading..." or 
 * something similar, e.g. 
 * <code>'\<div id="statusTag"\>Loading...<\/div\>'</code>.
 * Once initialisation is completed, the status tag should be removed
 * via the <code>remove()</code> method.
 * 
 * @author eckhart
 *
 */
public class StatusTag {
  /** The id of the status tag. */
  public static final String  STATUS_TAG_NAME = "statusTag";
 
  private static boolean removed = false;
  private static Element tag;
  
  /**
   * Removes the status tag from the DOM tree once and for all.
   * 
   * <p>Subsequent calls to <code>getHTML()</code> return the empty
   * string "" and calls to <code>setHTML()</code> will be ignored.
   */
  public static void remove() {
    if (create()) {
      DOM.removeChild((com.google.gwt.user.client.Element) 
                          tag.getParentElement(), 
                      (com.google.gwt.user.client.Element) tag);
      tag = null;
      removed = true;
    }
  }
  
  /**
   * Returns the inner HTML of the status tag if it (still) exists
   * or the empty string "" otherwise.
   * @return  the inner HTML code of the status tag
   */
  public static String getHTML() {
    if (create()) {
      return tag.getInnerHTML();
    }
    return "";
  }
  
  /**
   * Sets the status tag if it (still) exists. Does nothing
   * otherwise.
   * 
   * @param html  the html code for the status tag
   */
  public static void setHTML(String html) {
    if (create()) {
      tag.setInnerHTML(SimpleHtmlSanitizer.sanitizeHtml(html).asString());
    }
  }
  
  /**
   * Creates an HTML widget that wraps the tag with the id "statusTag",
   * if it exists.
   */
  private static boolean create() {
    if (removed) return false;
    else if (tag != null) return true;
    else {
      tag = DOM.getElementById(STATUS_TAG_NAME);
      if (tag != null) {
        return true;
      } else {
        removed = true;
        return false;
      }
    } 
  }
}

