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

import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class <code>HTMLLayout</code> allows to code the layout of the image panel,
 * control panel and caption in HTML. 
 * 
 * <p>Insances of the class are instantiated with a chunk of HTML code.
 * The elements of the slide show, i.e.
 * control panel, image panel and caption will be attached by to tags (or
 * DOM elements respectively) that have the ids "display", "controlPanel" and 
 * "caption" of the HTML code.
 * 
 * @author ecki
 *
 */
public class HTMLLayout extends Layout {
  
  /** An example HTML chunk that puts the control panel at the top,
   *  the image panel in the middle and the caption at the bottom. */
  public static final String DEFAULT_HTML = 
      "<table class=\"imageBackground\" style=\"width:100%; height:100%;\">" + 
      "<tr><td style=\"width:100%\"><hr class=\"tiledSeparator\" /></td>" + 
      "<td id=\"controlPanel\"></td></tr>" +
      "<tr><td id=\"display\" colspan=\"2\" style=\"width:100%; height:100%;\"></td></tr>" +
      "<tr><td colspan=\"2\"><hr class=\"tiledSeparator\" /></td></tr>" + 
      "<tr><td id=\"caption\" colspan=\"2\" align=\"center\" style=\"color:white;\"></td></tr>" + 
      "</table>";
  
  /** The html panel widget that represents the html chunk. */
  protected HTMLPanel htmlPanel;
  
  /**
   * The constructor of class <code>HTMLLayout</code>. 
   * 
   * @param collection    the image collection info object
   * @param HTMLChunk     a chunk of HTML code that contains tags with
   *                      the id's "display", "controlPanel" and "caption"
   * @param configuration the configuration string of the layout
   */
  public HTMLLayout(ImageCollectionInfo collection, String HTMLChunk, String configuration) {
    super(collection, configuration);
    htmlPanel = new HTMLPanel(HTMLChunk); 
    htmlPanel.add(imagePanel, "display");
    if (caption != null) {
      htmlPanel.add(caption, "caption");
      // caption.setSpacing(true);      
    }
    if (controlPanel != null) {
      htmlPanel.add(controlPanel, "controlPanel");
    }
  }

  /**
   * An alternative constructor that only takes the image collection info
   * object and the HTML chunk as parameters.
   * 
   * @param collection  the image collection info object
   * @param HTMLChunk   the HTML chunk defining the layout
   */
  public HTMLLayout(ImageCollectionInfo collection, String HTMLChunk) {
    this(collection, HTMLChunk, detectConfiguration(HTMLChunk));
  }

  /**
   * An alternative constructor that only takes the image collection info
   * as parameter and otherwise uses <code>DEFAULT_HTML</code> for the 
   * layout.
   * 
   * @param collection  the image collection info object
   */
  public HTMLLayout(ImageCollectionInfo collection) {
    this(collection, DEFAULT_HTML, detectConfiguration(DEFAULT_HTML));
  }  
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.Layout#getRootWidget()
   */
  @Override
  public Widget getRootWidget() {
    return htmlPanel;
  }

  private static String detectConfiguration(String HTMLChunk) {
    String cfg = "I";
    if (HTMLChunk.indexOf("\"caption\"") >= 0) cfg += "C";
    if (HTMLChunk.indexOf("\"controlPanel\"") >= 0) cfg += "P";
    return cfg;
  }
}
