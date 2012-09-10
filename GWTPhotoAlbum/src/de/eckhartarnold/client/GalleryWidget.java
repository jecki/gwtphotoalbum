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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * presents the user with a table of 
 * thumbnail images that can be clicked by the user. 
 * 
 * <p>The thumbnail
 * images contain the caption of the image as a tool-tip. The number
 * of rows and columns of the image table is determined dynamically
 * based on the size of the widget (or browser window resp.) and 
 * changes whenever it is resized.  
 * 
 * Class <code>GalleryWidget</code> does not add slide show button and
 * thus only fires <code>onPickImage</code> events.
 * 
 * @author eckhart
 *
 */
public class GalleryWidget extends GalleryBase {
  /** The default with of the thumbnail images of the gallery */
  public static final int DEFAULT_THUMBNAIL_WIDTH = 160;
  /** The default height of the thumnail images of the gallery */
  public static final int DEFAULT_THUMBNAIL_HEIGHT = 160;  
  /** The default horizontal padding between the thumbnail images of the gallery */
  public static final int DEFAULT_GALLERY_HPADDING = 50;
  /** The default vertical padding between the thumbnail images of the gallery */
  public static final int DEFAULT_GALLERY_VPADDING = 30;  
  
  // padding does not work really well, because horizontal and vertical padding are somwhow correlated.
  
  /** The key in the "info.json" file for the thumnbail width */
  public static final String KEY_THUMBNAIL_WIDTH = "thumbnail width";
  /** The key in the "info.json" file for the thumnbail height */
  public static final String KEY_THUMBNAIL_HEIGHT = "thumbnail height";
  /** The key in the "info.json" file for horizontal padding */
  public static final String KEY_GALLERY_HPADDING = "gallery horizontal padding";
  /** The key in the "info.json" file for horizontal padding */
  public static final String KEY_GALLERY_VPADDING = "gallery vertical padding";
  
  private static int readKey(ImageCollectionInfo collection, String keyName, int defaultValue) {
    if (collection.getInfo().containsKey(keyName)) {
      return Integer.valueOf(collection.getInfo().get(keyName));
    } else {
      return defaultValue;
    }
  }
  
  /** 
   * The "main" panel of the gallery widget. Basically, the gallery widget
   * consists of a vertical panel that contains a number of horizontal
   * panels which represent the rows of the image table. The horizontal
   * panels are disposed and recreated when ever the size changes. 
   */
  protected VerticalPanel     panel;
  
  private   SafeHtml[]        captions;
  private   int               edgeWidth, edgeHeight;
//  private   HTML              filler;
  private   Thumbnails        thumbnails;          
  private   HorizontalPanel[] imageRows;
  private   int               paddingH, paddingV;
 

  /**
   * Constructor of class <code>GalleryWidget</code>
   * 
   * @param collection  the image collection info from which to construct the
   *                    gallery.
   */
  public GalleryWidget(ImageCollectionInfo collection) {   
    this(collection, readKey(collection, KEY_THUMBNAIL_WIDTH, DEFAULT_THUMBNAIL_WIDTH),
                     readKey(collection, KEY_THUMBNAIL_HEIGHT, DEFAULT_THUMBNAIL_HEIGHT),
                     readKey(collection, KEY_GALLERY_HPADDING, DEFAULT_GALLERY_HPADDING),
                     readKey(collection, KEY_GALLERY_VPADDING, DEFAULT_GALLERY_VPADDING));
  }  
  
  /**
   * An alternative constructor of class <code>GalleryWidget</code> which
   * allows setting the with, height and spacing of the thumbnail images
   * according to user preferences. It is advisable to make sure that
   * these values correspond more of less to the extensions of the images
   * in the thumbnail image directory (i.e. the image directory with the
   * smalles sized images). Aspect ratios are taken care of by the
   * <code>GalleryWidget</code> class.
   * 
   * @param collection  the image collection info record
   * @param edgeWidth   the with of the thumbnail images
   * @param edgeHeight  the height of the thumbnail images
   * @param hpadding    the horizontal padding between the thumbnail images
   * @param vpadding    the vertical padding between the thumbnail images
   */
  public GalleryWidget(ImageCollectionInfo collection, int edgeWidth,
      int edgeHeight, int hpadding, int vpadding) {
    this (new Thumbnails(collection), collection.getCaptions(), edgeWidth, 
        edgeHeight, hpadding, vpadding);
  }
  
  /**
   * This is the most verbose constructor of class <code>GalleryWidget</code>.
   * It allows using this class without an image collection info object.
   * This requires not only to pass the thumbnail image's width and height
   * explicitly, but also their captions displayed as tooltips. 
   * 
   * @param thumbnails  the collection of thumbnail images
   * @param captions    the captions of the thumbnail images
   * @param edgeWidth   the width of the thumbnail images
   * @param edgeHeight  the height of the thumbnail images
   * @param hpadding    the horizontal padding between the thumbnail images
   * @param vpadding    the vertical padding between the thumbnail images
   */
  public GalleryWidget(Thumbnails thumbnails, SafeHtml[] captions, int edgeWidth,
      int edgeHeight, int hpadding, int vpadding) {
    this.captions = captions;
    this.edgeWidth = edgeWidth;
    this.edgeHeight = edgeHeight;
    this.paddingH = hpadding;
    this.paddingV = vpadding;
    thumbnails.adjustToRectangle(edgeWidth, edgeHeight);
    this.thumbnails = thumbnails;
    initRawGallery();    
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SourcesGalleryEvents#onResized()
   */
  @Override
  public void onResized() {
    Widget parent = getParent();
    if (parent == null) return;
    int width = parent.getOffsetWidth();
    HorizontalPanel row = null;
    int columns = (width - paddingH) / (edgeWidth + paddingH);
    if (columns <= 0) columns = 1;
    int rows = thumbnails.size() / columns;
    if (thumbnails.size() % columns != 0) rows++;
    
    if (imageRows != null) {
      if (rows == imageRows.length && 
          imageRows[0].getWidgetCount() == columns) {
        return;   // layout did not change!
      }
      
      for (HorizontalPanel p: imageRows) {
        panel.remove(p);
      }
    }    
    
    imageRows = new HorizontalPanel[rows];
    
    for (int i = 0; i < thumbnails.size(); i++) {
      if (i % columns == 0) {
        row = new HorizontalPanel();
        row.setStyleName("galleryRow");
        //row.setSpacing(paddingV);
        row.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        row.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);        
        imageRows[i/columns] = row;
      }
      Image img = thumbnails.get(i);
      if (captions[i].asString().length() > 0) {
        Tooltip.addToWidget(new Tooltip(captions[i]), img); // sometimes wrong tooltip position!?
      }
      row.add(img);
      //int delta = 0;
      //if (paddingH > paddingV) delta = 2*(paddingH-paddingV);
      row.setCellWidth(img, edgeWidth + 2*paddingH + "px");     
      row.setCellHeight(img, edgeHeight + 2*paddingV +"px");  
    }
    
    for (HorizontalPanel r : imageRows) {
      panel.add(r);
    }    
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Composite#onAttach()
   */
  @Override
  protected void onAttach() {
    prepareResized();
//    if (filler != null) {
//      panel.remove(filler);
//      filler = null;
//    }
    onResized();
    super.onAttach();
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#prepareResized()
   */
  public void prepareResized() { }
  
  private void initRawGallery() {
    assert panel == null;

    panel = new VerticalPanel();
    panel.addStyleName("gallery");
    panel.getElement().setAttribute("align", "center");
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    // add a filler so that the vertical panel is not empty (and its 
    // horizontal size not zero) when the thumbnail pictures are added.
    // filler = new HTML("&nbsp;");
    // panel.add(filler);
    
    ClickHandler imageClickHandler = new ClickHandler() {
      public void onClick(ClickEvent event) {
        Widget sender = (Widget) event.getSource();
        String id = DOM.getElementAttribute(sender.getElement(), "id");
        firePickImage(Integer.parseInt(id));
        sender.removeStyleName("galleryTouched");
        sender.removeStyleName("galleryPressed");        
      }        
    };
    
    MouseDownHandler imageMouseDownHandler = new MouseDownHandler() {
      public void onMouseDown(MouseDownEvent event) {
        Widget sender = (Widget) event.getSource();
        sender.addStyleName("galleryPressed");        
      }
    };
    
    MouseOverHandler imageMouseOverHandler = new MouseOverHandler() {
      public void onMouseOver(MouseOverEvent event) {
        Widget sender = (Widget) event.getSource();        
        sender.addStyleName("galleryTouched");
      }
    };
    
  
    MouseOutHandler imageMouseOutHandler = new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        Widget sender = (Widget) event.getSource();
        sender.removeStyleName("galleryTouched");
        sender.removeStyleName("galleryPressed");
      }
    };    
    
    for (int i = 0; i < thumbnails.size(); i++) {
      Image img = thumbnails.get(i);
      img.setStyleName("galleryImage");
      Element imgElement = img.getElement();
      DOM.setElementAttribute(imgElement, "id", String.valueOf(i));           
      img.addClickHandler(imageClickHandler);
      img.addMouseDownHandler(imageMouseDownHandler);
      img.addMouseOverHandler(imageMouseOverHandler);
      img.addMouseOutHandler(imageMouseOutHandler);
    }
    
    initWidget(panel);
  }
}

