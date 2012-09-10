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

import java.lang.String;
import java.lang.Integer;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author eckhart
 *
 */
public class Gallery extends Composite implements ResizeListener {
  public static int DEFAULT_EDGE_WIDTH = 100, DEFAULT_EDGE_HEIGHT = 100;
  
  protected VerticalPanel              panel;
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
    
  }
  
  private void initGallery(String title, String[] thumbnailURLs, int[][] thumbnailSizes, 
      int edgeWidth, int edgeHeight) {
    assert thumbnailURLs.length == thumbnailSizes.length;
    assert panel == null;
    
    panel = new VerticalPanel();
    panel.addStyleName("gallery");
    panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

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
    
    FlowPanel thumbnails = new FlowPanel();
    
    for (int i = 0; i < thumbnailURLs.length; i++) {
      String  url = thumbnailURLs[i];
      int     tnW = thumbnailSizes[i][0];
      int     tnH = thumbnailSizes[i][1];
      int     x, y, w, h;
      Image   img = new Image(url);
      Element imgElement = img.getElement();
      DOM.setElementAttribute(imgElement, "id", String.valueOf(i));      
      DOM.setElementAttribute(imgElement, "style", "margin:5px;");
      
      if (tnW == 0) {
        w = 0;
        h = edgeHeight;
        x = edgeWidth/2;
        y = 0;
      } else if (tnH == 0) {
        w = edgeWidth;
        h = 0;
        x = 0;
        y = edgeHeight/2;
      } else {
        w = edgeWidth;
        h = tnH * edgeWidth / tnW;
        if (h > edgeHeight) {
          h = edgeHeight;
          w = tnW * edgeHeight / tnH;
          x = (edgeWidth-w)/2;
          y = 0;
        } else {
          x = 0;
          y = (edgeHeight-h)/2;
        }
      }
      img.setPixelSize(w, h);      
      img.addClickListener(new ClickListener() {
        public void onClick(Widget sender) {
          String id = DOM.getElementAttribute(sender.getElement(), "id");
          firePickImage(Integer.parseInt(id));
        }        
      });
      AbsolutePanel wrapper = new AbsolutePanel();
      wrapper.setPixelSize(edgeWidth, edgeHeight);
      wrapper.add(img, x, y);
      thumbnails.add(wrapper);
      img.getParent();
    }
    panel.add(thumbnails);
    initWidget(panel);
  }
}

