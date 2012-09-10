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

//import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ecki
 *
 */
public class HTMLLayout extends Layout {

  protected RootPanel rootPanel;
  
  /**
   * @param collection
   * @param configuration
   */
  public HTMLLayout(ImageCollectionInfo collection, String configuration) {
    super(collection, configuration);
    rootPanel = RootPanel.get("photoViewer");
    RootPanel.get("display").add(display);
    if (caption != null) {
      RootPanel.get("caption").add(caption);
      caption.setSpacing(true);      
      caption.addStyleDependentName("tiled");
    }
    if (controlPanel != null) RootPanel.get("controlPanel").add(controlPanel);
    
  }

  /**
   * @param collection
   */
  public HTMLLayout(ImageCollectionInfo collection) {
    this(collection, detectConfiguration());
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.Layout#getRootWidget()
   */
  @Override
  public Widget getRootWidget() {
    return rootPanel;
  }

  private static String detectConfiguration() {
    String cfg = "I";
    if (RootPanel.get("caption") != null) cfg += "C";
    if (RootPanel.get("controlPanel") != null) cfg += "P";
    return cfg;
  }
}
