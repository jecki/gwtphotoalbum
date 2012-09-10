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

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ecki
 *
 */
public class TiledLayout extends Layout {

  protected CellPanel panel; 
  
  /**
   * @param collection
   * @param configuration
   */
  public TiledLayout(ImageCollectionInfo collection, String configuration) {
    super(collection, configuration);
    panel = new VerticalPanel();
    panel.setStyleName("tiled");
    createTiles(panel, configuration, 0);
  }

  /**
   * @param collection
   */
  public TiledLayout(ImageCollectionInfo collection) {
    this(collection, "[CP]I");
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.Layout#getRootWidget()
   */
  @Override
  public Widget getRootWidget() {
    return panel;
  }

  
  private int createTiles(CellPanel parent, String config, int i) {
    while (i < config.length()) {
      switch (config.charAt(i)) {
        case ('C'): {
          parent.add(caption);
          break;
        }
        case ('I'): {
          parent.add(display);
          parent.setCellWidth(display, "100%");
          parent.setCellHeight(display, "100%");          
          break;
        }
        case ('P'): {
          parent.add(controlPanel);
          break;
        }
        case (']'): {
          return i;
        }
        case ('['): {
          CellPanel panel;
          if (parent instanceof VerticalPanel) { 
            panel = new HorizontalPanel();
            panel.setStyleName("tiled");
          } else {
            panel = new VerticalPanel();
            panel.setStyleName("tiled");
          }
          i = createTiles(panel, config, i+1);
          parent.add(panel);
          break;
        }
        default: 
          assert false: "Malformed configuration (sub-)string: "+config;
      }
      i++;
    }
    return i;
  }
  
}
