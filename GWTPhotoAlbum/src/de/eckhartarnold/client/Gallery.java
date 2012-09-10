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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;

/**
 * Implements the full gallery widget. 
 * 
 * <p>Class <code>Gallery</code> is
 * an extension to class <code>GalleryWidget</code> that adds a title,
 * a sub title and a bottom line and also a button to start a slide show.
 * 
 * @author ecki
 *
 */
public class Gallery extends GalleryWidget {
  static {
    Image.prefetch("icons/start.png");
    Image.prefetch("icons/start_down.png");
  }
  
  /** The title of the gallery */
  protected String title;
  /** A sub-title for the gallery */
  protected String subtitle;
  /** A bottom line for the gallery */
  protected String bottomLineText;
  
  private HTML bottomLine;
  
  
  /** 
   * Constructor of class <code>Gallery</code>.
   * 
   * @param collection  the image collection info from which to construct
   *                    the gallery
   */
  public Gallery(ImageCollectionInfo collection) {
    super(collection);
    initGallery(collection);
  }
  
  /**
   * A more verbose constructor of class <code>Gallery</code>
   * 
   * @param collection  the image collection info from which to construct
   *                    the gallery
   * @param edgeWidth   the width of the thumbnail images
   * @param edgeHeight  the height of the thumbnail images
   * @param hpadding    the horizontal padding between the thumbnail images
   * @param vpadding    the vertical padding between the thumbnail images
   */
  public Gallery(ImageCollectionInfo collection, int edgeWidth, int edgeHeight,
      int hpadding, int vpadding) {
    super(collection, edgeWidth, edgeHeight, hpadding, vpadding);
    initGallery(collection);
  }  
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.GalleryWidget#onResized()
   */
  public void onResized() {
    removeBottomLine();
    super.onResized();
    addBottomLine();
  }

  private void addBottomLine() {
    if (bottomLineText != null) {
      // HTML btLine = new HTML("<hr class=\"galleryBottomSeparator\" />\n"+this.bottomLineText+"\n<br />");
      // btLine.addStyleName("bottomLine");
      panel.add(bottomLine);
    }
  }  
  
  private void initGallery(ImageCollectionInfo collection) {
    HashMap<String, String> info = collection.getInfo();
    this.title = info.get("title");
    this.subtitle = info.get("subtitle");
    this.bottomLineText = info.get("bottom line");
    if (bottomLineText != null) {
      this.bottomLine = new HTML("<hr class=\"galleryBottomSeparator\" />\n"+
                                 this.bottomLineText+"\n<br />");
      bottomLine.addStyleName("bottomLine");      
    }
    
    if (this.title != null) {
      HTML title = new HTML(this.title);
      title.addStyleName("galleryTitle");
      panel.insert(title, 0);
    }
    
    if (this.subtitle != null) {
      HTML subtitle = new HTML(this.subtitle);
      subtitle.addStyleName("gallerySubTitle");
      panel.insert(subtitle, 1);
    }
    
    PushButton slideshowButton = 
        new PushButton(new Image("icons/start.png"),
        new Image("icons/start_down.png"), new ClickHandler(){
      public void onClick(ClickEvent event) {
        fireStartSlideshow();
      }
    });
    slideshowButton.setPixelSize(64, 32);
    slideshowButton.addStyleName("galleryStartButton");
    Tooltip.addToWidget(new Tooltip(I18N.i18n.runSlideshow()), 
        slideshowButton);
    panel.insert(new HTML("<hr class=\"galleryTopSeparator\" />"), 2);
    panel.insert(slideshowButton, 3);
    panel.insert(new HTML("<br /><br />"), 4);
    
    addBottomLine();
  }
  
  private void removeBottomLine() {
    if (bottomLineText != null) {
      // int cnt = panel.getWidgetCount();
      panel.remove(bottomLine); // remove bottom line
    }
  } 
}
