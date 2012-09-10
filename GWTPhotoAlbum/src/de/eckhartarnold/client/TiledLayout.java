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
import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Defines a "tiled" layout.
 * 
 * <p>A tiled layout consists
 * of three blocks, namely, the image panel ("I"), the control panel ("P")
 * and the caption ("C"), of which the image panel is obligatory while the
 * other two are optional. These three blocks can be arranged almost 
 * horizontally and vertically in an almost arbitrary manner, with possible
 * horizontal delimiting lines (html "<hr />" tags) in between. However,
 * layouts where the image panel is arranged together with another element
 * horizontally may not work very well. But putting the control panel next to
 * the caption on top and the image panel below for example works quite well.
 *
 * @author ecki
 *
 */
public class TiledLayout extends FullScreenLayout {
 
  protected CellPanel      panel; 
  
  /**
   * Creates a tiled layout. The configuration string indicates the 
   * arrangement of the components "image" (represented by 'I'), 
   * "control panel" (represented by 'P') and "caption" (represented by 
   * 'C') and optional horizontal lines (represented by "-"). 
   * Components bracketed in squared brackets are arranged horizontally.
   * Without squared brackets components are arranged vertically.
   * Example: "[CP]-I" means that the caption and control panel are on top
   * and the control panel is placed to the right of the caption. Below
   * follows a horizontal separator and than the image panel.
   *  
   * @param collection     the information about the image collection
   * @param configuration  the configuration string.
   */
  public TiledLayout(ImageCollectionInfo collection, String configuration) {
    super(collection, configuration);
    panel = new VerticalPanel();
    panel.setStyleName("tiled");
    createTiles(panel, collection, configuration, 0);    
  }

  /**
   * Creates a tiled layout with the configuration: "[CP]I".
   * 
   * @param collection  the information about the image collection
   */
  public TiledLayout(ImageCollectionInfo collection) {
    this(collection, "C-I-P");
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.Layout#getRootWidget()
   */
  @Override
  public Widget getRootWidget() {
    return panel;
  }
  
  @Override
  protected void initOverlayWidgets(ImageCollectionInfo info) {  }
  
  private int createTiles(CellPanel parent, ImageCollectionInfo info,
      String config, int i) {
    while (i < config.length()) {
      switch (config.charAt(i)) {
        case ('C'): {
          parent.add(caption);
          caption.addStyleDependentName("tiled");
          parent.setCellVerticalAlignment(caption, 
              HasVerticalAlignment.ALIGN_MIDDLE);       
          parent.setCellHorizontalAlignment(caption, 
              HasHorizontalAlignment.ALIGN_CENTER);
          parent.setCellWidth(caption, "100%");                  
          break;
        }
        case ('O'): {
          overlay = new CaptionOverlay(caption, imagePanel, slideshow,
              info.getInfo().get(CaptionOverlay.KEY_CAPTION_POSITION));
        }
        case ('I'): {
          parent.add(imagePanel);
          parent.setCellVerticalAlignment(imagePanel, 
              HasVerticalAlignment.ALIGN_MIDDLE);  
          parent.setCellHorizontalAlignment(imagePanel, 
              HasHorizontalAlignment.ALIGN_CENTER);
          parent.setCellWidth(imagePanel, "100%");
          parent.setCellHeight(imagePanel, "100%");                           
          break;
        }
        case ('P'):
        case ('F'): {
          parent.add(controlPanel);
          controlPanel.addStyleDependentName("tiled");
          parent.setCellVerticalAlignment(controlPanel, 
              HasVerticalAlignment.ALIGN_MIDDLE); 
          if (parent instanceof HorizontalPanel 
              && parent.getWidgetCount() == 1) {
            parent.setCellHorizontalAlignment(controlPanel, 
                HasHorizontalAlignment.ALIGN_RIGHT);
          } else {
            parent.setCellHorizontalAlignment(controlPanel, 
                HasHorizontalAlignment.ALIGN_CENTER); 
          }       
          break;
        }
        case ('-'): {
          HTML horizLine = new HTML("<hr class=\"tiledSeparator\" />");
          panel.add(horizLine);
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
          i = createTiles(panel, info, config, i+1);
          parent.add(panel);
          break;
        }
        default:
          // Debugger.print("error");
          // GWT.log("error", null);
          assert false: 
              "Illegal token '"+config.charAt(i)+"' in config string";
      }
      i++;
    }
    return i;
  }
  
}

