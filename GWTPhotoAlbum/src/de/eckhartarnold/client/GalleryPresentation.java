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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Panel;

/**
 * Ties together the gallery and the
 * slide show.
 * 
 * <p>The user can switch between the gallery and
 * the slide show by clicking on a picture or on the "start slide show"
 * button in the gallery, or by clicking on the gallery button of the
 * control panel in the slide show.
 * 
 * <p>Thus, class <code>GalleryPresentation</code> selects between two types
 * of web pages, the "gallery page" and the "slide show page". The 
 * "slide show page" is selected when a pick image or start slide show event
 * is received or when an the "slide show page" for a particular image is 
 * found on top of the browser history. The "gallery page" is selected 
 * when an <code>onClick</code> event is received 
 * (usually from the gallery button on the control panel) or when a gallery
 * token is at top of the browser history or when none of the previous 
 * conditions for selecting either the "slide show page" or the "gallery page"
 * holds. 
 * 
 * @author ecki
 *
 */
public class GalleryPresentation extends Presentation implements GalleryListener, 
    ClickHandler, ValueChangeHandler<String>, SlideshowListener {
  
  /** The gallery widget that makes up the gallery page */
  protected GalleryBase gallery;
  
  /** true, if the slideshow has been started from the gallery page; in this
   *  case the slideshow will return to the gallery page after finishing. */
  private boolean slideshowInitiated = false;

  /**
   * The constructor of class <code>GalleryPresentation</code>.
   * 
   * @param parent   the parent widget of the gallery and the slide show
   *                 respectively
   * @param gallery  the gallery widget
   * @param layout   the layout that contains the slide show
   */
  public GalleryPresentation(Panel parent, GalleryBase gallery, Layout layout) {
    super(parent, layout);
    this.gallery = gallery;

    layout.getControlPanel().setHomeButtonListener(this);
    gallery.addGalleryListener(this);
    layout.slideshow.addSlideshowListener(this);
    
    int imageNr = Presentation.parseSlideToken(History.getToken());
    if (imageNr < 0) {
      parent.add(gallery);    
    }
    else onPickImage(imageNr);
    
    History.addValueChangeHandler(this);  
  }
  
  /**
   * Reports whether the gallery page is active at the moment.
   * @return  <code>true</code> if gallery page is active and
   *          <code>false</code> if the slide show page is active  
   */
  public boolean isGalleryActive() {
    return !slideshowActive;
  }
  
  
  /**
   * This method ought to be called in order to request a switch to
   * the gallery page.
   * 
   * @param event   a click event This may also be <code>null</code>
   *                if the caller is not a widget object.
   */
  public void onClick(ClickEvent event) {
    returnToGallery();
  }
  

  /* (non-Javadoc)
   * @see com.google.gwt.event.logical.shared.ValueChangeHandler#onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent)
   */
  @Override
  public void onValueChange(ValueChangeEvent<String> event) {
    String historyToken = event.getValue();
    GWT.log("GalleryPresentation.onValueChanged: "+historyToken);    
    if (historyToken.equals(GalleryBase.GALLERY_TOKEN)) {
      if (slideshowActive) {
        onClick(null);
      }
    } else if (slideshowActive) {
      super.onValueChange(event);
    } else {
      int slideNr = Presentation.parseSlideToken(historyToken);
      if (slideNr >= 0) {
        onPickImage(slideNr);
      } else {
        History.back();
      }
    }
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.GalleryListener#onPickImage(int)
   */
  public void onPickImage(int imageNr) {
    parent.remove(gallery);
    layout.getSlideshow().show(-1);
    layout.getSlideshow().showImmediately(imageNr);
    activateSlideshow();
  }  

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.GalleryListener#onStartSlideshow()
   */
  public void onStartSlideshow() {
    parent.remove(gallery);
    layout.getSlideshow().show(-1);
    activateSlideshow();
    slideshowInitiated = true;    
    layout.getSlideshow().start();    
  }

  /* (non-Javadoc)
   * @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent)
   */
  public void onResize(ResizeEvent event) {
    if (isGalleryActive()) {
      gallery.onResized();
    } else {
      super.onResize(event);
    }
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onFade()
   */  
  public void onFade() { }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onShow()
   */    
  public void onShow(int slideNr) { }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onStart()
   */    
  public void onStart() { }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onStop()
   */    
  public void onStop() { 
    if (layout.slideshow.getCurrentSlide() == layout.slideshow.size()-1) {
      Timer timer = new Timer() {
        public void run() {
          if (slideshowInitiated) {
            returnToGallery();
          }
        }
      };
      timer.schedule(layout.slideshow.getDuration()*120/100); // let the last image stay a little longer... 
    } else {
      slideshowInitiated = false;
    }
  }
  
  /**
   * Switches back to the gallery if slideshow is active
   */
  private void returnToGallery() {
    if (slideshowActive) {
      slideshowInitiated = false;
      deactivateSlideshow();
      parent.add(gallery);
    }    
  }
}
