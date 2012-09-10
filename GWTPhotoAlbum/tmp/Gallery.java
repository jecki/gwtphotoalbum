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

import java.util.ArrayList;
import java.util.HashMap;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author eckhart
 *
 */
public class Gallery extends Composite implements ResizeListener {
  
  public static int DEFAULT_EDGE_WIDTH = 120, DEFAULT_EDGE_HEIGHT = 120;
  
  private static final int H_SPACING = 40, V_SPACING = 10; // percentage of edge size used for cell spacing
  
  protected VerticalPanel              panel;
  protected HorizontalPanel            wrapper;
  
  private   int                        edgeWidth;
  private   Image[]                    imageList;
  private   HorizontalPanel[]          imageRows;
  private   ArrayList<GalleryListener> listenerList = new ArrayList<GalleryListener>();

 
  public Gallery(String title, String[] thumbnailURLs, int[][] thumbnailSizes, 
      int edgeWidth, int edgeHeight) {
    initGallery(title, thumbnailURLs, thumbnailSizes, edgeWidth, edgeHeight);
  }

  public Gallery(String title, String[] thumbnailURLs, int[][] thumbnailSizes) {
    this(title, thumbnailURLs, thumbnailSizes, DEFAULT_EDGE_WIDTH, 
        DEFAULT_EDGE_HEIGHT);
  }
  
  public Gallery(String title, ImageCollectionInfo collection, int edgeWidth,
      int edgeHeight) {
    String   imageNames[] = collection.getImageNames();
    HashMap<String, int[][]> imageSizes = collection.getImageSizes();
    String   thumbnailDir = collection.getDirectories()[0];    
    String[] thumbnailURLs = new String[imageNames.length];
    int[][]  thumbnailSizes = new int[thumbnailURLs.length][2];

    for (int i = 0; i < imageNames.length; i++) {
      thumbnailURLs[i] = thumbnailDir + "/" + imageNames[i];
      thumbnailSizes[i] = imageSizes.get(imageNames[i])[0]; 
    }

    initGallery(title, thumbnailURLs, thumbnailSizes, edgeWidth, edgeHeight);
  }
  
  public Gallery(String title, ImageCollectionInfo collection) {
    this(title, collection, DEFAULT_EDGE_WIDTH, DEFAULT_EDGE_HEIGHT);
  }
  
  
  public void addGalleryListener(GalleryListener listener) {
    listenerList.add(listener);
  }
  
  public void removeGalleryListener(GalleryListener listener) {
    listenerList.remove(listener);
  }
  
  private void fireStartSlideshow() {
    for (GalleryListener listener: listenerList) 
      listener.onStartSlideshow();    
  }
  
  private void firePickImage(int imageNr) {
    for (GalleryListener listener: listenerList) 
      listener.onPickImage(imageNr);
  }

  
  public void onResized() {
    Widget parent = getParent();
    if (parent == null) return;
    int width = parent.getOffsetWidth();
    HorizontalPanel row = null;
    int spacing = edgeWidth * H_SPACING / 100;
    int columns = (width - spacing) / (edgeWidth + spacing);
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
        row.setSpacing(spacing);
        row.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        row.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        imageRows[i/columns] = row;
      }
      row.add(imageList[i]);
      row.setCellWidth(imageList[i], edgeWidth+"px");
    }
    
    for (HorizontalPanel p : imageRows) {
      panel.add(p);
    }    
  }
  
  @Override
  protected void onAttach() {
    onResized();
    super.onAttach();
  }
  
  private void initGallery(String title, String[] thumbnailURLs, int[][] thumbnailSizes, 
      int edgeWidth, int edgeHeight) {
    assert thumbnailURLs.length == thumbnailSizes.length;
    assert panel == null;
    
    this.edgeWidth = edgeWidth;
    
    wrapper = new HorizontalPanel();
    wrapper.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    
    panel = new VerticalPanel();
    panel.addStyleName("gallery");
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    panel.setSpacing(edgeHeight * V_SPACING / 100);

    Label heading = new Label(title);
    heading.addStyleName("galleryTitle");
    panel.add(heading);

    Button slideshowButton = new Button("Start slideshow", new ClickListener(){
      public void onClick(Widget sender) {
        fireStartSlideshow();
      }
    });
    panel.add(slideshowButton);
    slideshowButton.addStyleName("galleryStartButton");
    
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
      img.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
          String id = DOM.getElementAttribute(sender.getElement(), "id");
          firePickImage(Integer.parseInt(id));
        }        
      });
      imageList[i] = img;
    }
    
    wrapper.add(panel);
    initWidget(wrapper);
  }
}

