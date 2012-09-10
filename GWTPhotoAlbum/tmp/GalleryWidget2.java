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

import java.util.HashMap;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author eckhart
 *
 */
public class GalleryWidget extends GalleryBase {
  
  public static final int DEFAULT_EDGE_WIDTH = 120;
  public static final int DEFAULT_EDGE_HEIGHT = 120;
  public static final int DEFAULT_SPACING = 40;
  
  protected VerticalPanel              panel;
  
  private   int                        edgeWidth;
  private   HTML                       filler;
  private   Image[]                    imageList;
  private   HorizontalPanel[]          imageRows;
  private   int                        spacing; // percentage of edge size used for cell spacing
 
  public GalleryWidget(String[] thumbnailURLs, int[][] thumbnailSizes, 
      int edgeWidth, int edgeHeight, int spacing) {
    initRawGallery(thumbnailURLs, thumbnailSizes, edgeWidth, edgeHeight, spacing);
  }

  public GalleryWidget(String[] thumbnailURLs, int[][] thumbnailSizes) {
    this(thumbnailURLs, thumbnailSizes, DEFAULT_EDGE_WIDTH, 
        DEFAULT_EDGE_HEIGHT, DEFAULT_SPACING);
  }
  
  public GalleryWidget(ImageCollectionInfo collection, int edgeWidth,
      int edgeHeight, int spacing) {
    String   imageNames[] = collection.getImageNames();
    HashMap<String, int[][]> imageSizes = collection.getImageSizes();
    String   thumbnailDir = collection.getDirectories()[0];    
    String[] thumbnailURLs = new String[imageNames.length];
    int[][]  thumbnailSizes = new int[thumbnailURLs.length][2];

    for (int i = 0; i < imageNames.length; i++) {
      thumbnailURLs[i] = thumbnailDir + "/" + imageNames[i];
      thumbnailSizes[i] = imageSizes.get(imageNames[i])[0]; 
    }

    initRawGallery(thumbnailURLs, thumbnailSizes, edgeWidth, edgeHeight, spacing);
  }
  
  public GalleryWidget(ImageCollectionInfo collection) {
    this(collection, DEFAULT_EDGE_WIDTH, DEFAULT_EDGE_HEIGHT, DEFAULT_SPACING);
  }
  
  
  @Override
  public void onResized() {
    Widget parent = getParent();
    if (parent == null) return;
    int width = parent.getOffsetWidth();
    HorizontalPanel row = null;
    int spc = edgeWidth * spacing / 100;
    int columns = (width - spc) / (edgeWidth + spc);
    if (columns <= 0) columns = 1;
    int rows = imageList.length / columns;
    if (imageList.length % columns != 0) rows++;
    
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
    
    for (int i = 0; i < imageList.length; i++) {
      if (i % columns == 0) {
        row = new HorizontalPanel();
        row.setStyleName("galleryRow");
        row.setSpacing(spc);
        row.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        row.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        imageRows[i/columns] = row;
      }
      row.add(imageList[i]);
      row.setCellWidth(imageList[i], edgeWidth+"px");
    }
    
    panel.remove(filler);
    for (HorizontalPanel p : imageRows) {
      panel.add(p);
    }    
  }

  @Override
  protected void onAttach() {
    onResized();
    super.onAttach();
  }
  
  private void initRawGallery(String[] thumbnailURLs, int[][] thumbnailSizes, 
      int edgeWidth, int edgeHeight, int spacing) {
    assert thumbnailURLs.length == thumbnailSizes.length;
    assert panel == null;
    
    this.edgeWidth = edgeWidth;
    this.spacing = spacing;
    
    panel = new VerticalPanel();
    panel.addStyleName("gallery");
    panel.getElement().setAttribute("align", "center");
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

    // add a filler so that the vertical panel is not empty (and its 
    // horizontal size not zero) when the thumbnail pictures ar added.
    filler = new HTML("&nbsp;");
    panel.add(filler);
//    HTML heading = new HTML(title);
//    heading.addStyleName("galleryTitle");
//    panel.add(heading);

//    Button slideshowButton = new Button("Start slideshow", new ClickListener(){
//      public void onClick(Widget sender) {
//        fireStartSlideshow();
//      }
//    });
//    panel.add(slideshowButton);
//    slideshowButton.addStyleName("galleryStartButton");
    
    ClickListener imageClickListener = new ClickListener() {
      public void onClick(Widget sender) {
        String id = DOM.getElementAttribute(sender.getElement(), "id");
        firePickImage(Integer.parseInt(id));
      }        
    };
    
    imageList = new Image[thumbnailURLs.length];
    for (int i = 0; i < thumbnailURLs.length; i++) {
      String  url = thumbnailURLs[i];
      int     tnW = thumbnailSizes[i][0];
      int     tnH = thumbnailSizes[i][1];
      int     w, h;
      Image   img = new Image(url);
      
      img.setStyleName("galleryImage");
      Element imgElement = img.getElement();
      DOM.setElementAttribute(imgElement, "id", String.valueOf(i));      
      // DOM.setElementAttribute(imgElement, "style", "margin:5px;");
      
      if (tnW == 0) {
        w = 0;
        h = edgeHeight;
      } else if (tnH == 0) {
        w = edgeWidth;
        h = 0;
      } else {
        w = edgeWidth;
        h = tnH * edgeWidth / tnW;
        if (h > edgeHeight) {
          h = edgeHeight;
          w = tnW * edgeHeight / tnH;
        } 
      }
      img.setPixelSize(w, h);      
      img.addClickListener(imageClickListener);
      imageList[i] = img;
    }
    
    initWidget(panel);
  }
}

