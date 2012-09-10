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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * PreviewPanel displays a row of thumbnail images as if on a filmstrip.
 * 
 * @author ecki
 *
 */
public class PreviewPanel extends Composite implements SlideshowListener, ResizeListener {
  private int             width, height = -1;
  private int             imgWidth, imgSpc = 2;
  private int             cursor;
  private AbsolutePanel   panel;
  private HorizontalPanel filmstrip;
  private Thumbnails      thumbnails;

  
  
  public PreviewPanel(ImageCollectionInfo collection) {
    this(new Thumbnails(collection));
  }
  
  public PreviewPanel(Thumbnails thumbnails) {
    this.thumbnails = thumbnails;
    panel = new AbsolutePanel();
    panel.addStyleName("imageBackground");
    initWidget(panel);
    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.ONMOUSEWHEEL);
    super.setSize("100%", (64+imgSpc)+"px");    
    filmstrip = new HorizontalPanel();
    filmstrip.setStyleName("previewFilmstrip");
    filmstrip.setSpacing(0);
    filmstrip.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
    filmstrip.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);    
    int i = 0;
    for (Image img: thumbnails) {
      i++;
      if (i < 20) {
      img.setStyleName("previewImage");     
      filmstrip.add(img); }
    }    
  }
  
  public void onFade() { }

  public void onResized() {
    
  }
  
  public void onShow(int slideNr) { 
    cursor = slideNr;
    panel.setWidgetPosition(filmstrip, deltaX(cursor), 0);
  }
  
  public void onStart() { }
  
  public void onStop() { }
  
  @Override
  public void setPixelSize(int width, int height) {
    panel.remove(filmstrip);
    super.setPixelSize(width, height);
    this.width = panel.getOffsetWidth();
    if (height != this.height) {
      this.height = height;      
      this.imgWidth = thumbnails.bestWidth(height-imgSpc);
      // thumbnails.adjustSize(this.imgWidth, height-imgSpc);
      for (int i = 0; i < filmstrip.getWidgetCount(); i++) {
        filmstrip.getWidget(i).setPixelSize(20, 20);
      }
    }
//    for (Image img: thumbnails) {     
//      filmstrip.setCellWidth(img, (imgWidth+imgSpc)+"px");
//    }
    for (int i = 0; i < filmstrip.getWidgetCount(); i++) {
      filmstrip.setCellWidth(filmstrip.getWidget(i), "160px");
    }    
    //panel.add(filmstrip, deltaX(cursor), 0);
    RootPanel.get().add(filmstrip);
  HorizontalPanel h = new HorizontalPanel();
  h.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  h.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);        
  Image i1 = new Image("slides/120x120/A04_Grabeskirche.JPG");
  i1.setPixelSize(45, 60);
  h.add(i1);
  h.setCellWidth(i1, "80px");
  Image i2 = new Image("slides/120x120/E01_Nazareth.JPG");
  i2.setPixelSize(80, 60);
  h.add(i2);
  h.setCellWidth(i2, "80px");
  RootPanel.get().add(h);    
  }
  
  @Override
  protected void onLoad() {
    setPixelSize(panel.getOffsetWidth(), panel.getOffsetHeight());
  }
  
  private int deltaX(int cursor) {
    return width/2 - cursor * (imgWidth +  imgSpc) - (imgWidth + imgSpc)/2;
  }
}

