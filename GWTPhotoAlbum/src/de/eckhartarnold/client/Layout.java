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

//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract base class for all layout classes for the slide show. 
 * 
 * <p>The layout classes define how and where these elements are 
 * placed. A "layout" for the slide show
 * always contains the image panel on which the slides are shown and it may 
 * contain (usually it does) a control panel to control the slide show and
 * a caption where the captions of those images that have a caption are 
 * displayed. 
 * 
 * @author ecki
 */
public abstract class Layout {
  /** the key in the "info.json" dictionary that determines whether 
   *  by clicking on an image a window with its full sized version is
   *  opened. The value can be either "true" or "false", default is
   *  "true". */
  public static final String  KEY_CLICKABLE   = "image clickable";
  
  /**
   * the key in the "info.json" dictionary that determines whether the
   * window scrollbars shall be turned of when attaching the image
   * panel. (This may be useful for avoiding flickering, but should
   * not be used when the slide show is embedded in a larger html
   * page so that it does not occupy the whole page alone!)
   */
  public static final String  KEY_DISABLE_SCROLLING = "disable scrolling";
  
  /** The standard configuration of the layout: control panel at the top,
   * image panel in the middle, caption at the bottom. */
  public static final String  STD_CFG = "PIC"; // Panel, Image, Caption
  
  
  /** The widget that display the caption. */
  protected Caption      caption;
  /** The control panel widget. */
  protected ControlPanel controlPanel;
  /** 
   * The film strip widget which, if present at all, is usually "swallowed" 
   * by the control panel.
   */
  protected Filmstrip    filmstrip;
  /** The widget on which the images are displayed. */
  protected ImagePanel   imagePanel;
  /** The <code>Slideshow</code> object controlling the slide show. */
  protected Slideshow    slideshow;
  
  
  /**
   * Creates a new layout object.
   * 
   * @param collection    the information about the image collection
   * @param configuration the configuration string for the layout
   */
  public Layout(ImageCollectionInfo collection, String configuration) {
    init(collection, configuration);
  }
  
  /**
   * Creates a new layout object. The layout object created with this 
   * constructor does contain a <code>ControlPanel</code>. It will
   * also contain a <code>Caption</code> unless the captions of 
   * <em>all</em> images are empty.
   * 
   * @param collection the information about the image collection
   */
  public Layout(ImageCollectionInfo collection) {
    String cfg;
    if (collection.hasCaptions()) 
      cfg = "PIC";
    else
      cfg = "PI";
    init(collection, cfg);
  }
  
  /**
   * Returns the <code>Caption</code> object, if the layout contains a
   * caption.
   * @return the <code>Caption</code> object or <code>null</code>
   */
  public Caption getCaption() {
    return caption;
  }

  /**
   * Returns the <code>ControlPanel</code> object, if the layout contains a
   * control panel.
   * @return the <code>ControlPanel</code> object or <code>null</code>
   */
  public ControlPanel getControlPanel() {
    return controlPanel;
  }  
  
  /**
   * Returns the <code>FlipImagePanel</code> object.
   * @return the <code>FlipImagePanel</code> object
   */
  public ImagePanel getImagePanel() {
    return imagePanel; 
  }
  
  /**
   * Returns the <code>Slideshow</code> object.
   * @return the <code>Slideshow</code> object
   */
  public Slideshow getSlideshow() {
    return slideshow;
  }
  
  /**
   * Returns the root widget of the layout. The widget can be inserted anywhere
   * in a web site. After attaching the root widget to the web site, method
   * <code>issueResize</code> should be called to make sure that the layout's
   * size is adjusted properly.
   *  
   * @return the root widget of the layout
   */
  public abstract Widget getRootWidget();
  
  /**
   * Calls the <code>prepareResized</code> and <code>onResized</code> methods 
   * of the widgets associated with this layout. This method should be called  
   * after attaching the layout's root widget to the DOM-tree!
   */
  public void issueResize() {
    imagePanel.prepareResized();
    if (controlPanel != null) controlPanel.prepareResized();
    if (caption != null) { 
      caption.prepareResized();
      caption.onResized();
    }
    if (controlPanel != null) controlPanel.onResized();
    imagePanel.onResized();
  }

  private void init(ImageCollectionInfo collection, String configuration) {
    imagePanel = new ImagePanel(collection);
    /* String clickable = collection.getInfo().get(KEY_CLICKABLE);
    if (clickable == null || clickable.equalsIgnoreCase("true")) {
      imagePanel.addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          Window.open(imagePanel.getImageURL(), "_blank", 
              "menubar=no,toolbar=no,directories=no,personalbar=no,"+
              "location=yes,resizable=yes,scrollbars=yes");
        }
      });
    }*/
    String disableScrolling = collection.getInfo().get(KEY_DISABLE_SCROLLING);
    if (disableScrolling != null && disableScrolling.equalsIgnoreCase("true")){
      imagePanel.addAttachmentListener(new AttachmentListener() {
        public void onLoad(Widget sender) {
          Window.enableScrolling(false);          
        }
        public void onUnload(Widget sender) {
          Window.enableScrolling(true);
        }
      });
    }
    slideshow = new Slideshow(imagePanel, collection.getImageNames(), 
        collection.getDirectories(), collection.getImageSizes());
    if (configuration.contains("F")) {
      controlPanel = new ControlPanel(slideshow);
      filmstrip = new Filmstrip(collection);
      controlPanel.swallowFilmstrip(filmstrip, true);
    } else if (configuration.contains("P")) { 
      controlPanel = new ControlPanel(slideshow);
    }
    if (configuration.contains("C") || configuration.contains("O"))
      caption = new Caption(slideshow, collection.getCaptions());
  }
}

