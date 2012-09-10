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

import com.google.gwt.user.client.ui.Widget;

/**
 * Implements a "full screen" layout for
 * the slide show.
 * 
 * In the full screen layout, the image panel covers the whole
 * browser window (not the whole screen strictly speaking, because it is 
 * usually not possible to hide the border, title bar, menu etc. with 
 * javascript commands). The captions (if present) are laid over the image
 * (the default position is the bottom of the image panel), while the control
 * panel is hidden and pops up up only when the user moves the mouse. (The default
 * position of the control panel is the upper right corner, but the control
 * panel can be dragged to any other position by the user).  
 * 
 * @author ecki
 */
public class FullScreenLayout extends Layout {
  
  /** The overlay object that captures the caption */
  protected CaptionOverlay    overlay;
  /** The overlay object that captures the control panel */
  protected PanelOverlayBase  popup;

  /**
   * Creates a new "full screen" layout.
   * 
   * @param collection    the collection of images
   * @param configuration the configuration string which indicates whether
   *                      the slide show also shows captions and if a control
   *                      panel pops up when the user moves the mouse.
   */
  public FullScreenLayout(ImageCollectionInfo collection, 
      String configuration) {
    super(collection, configuration);
    initOverlayWidgets(collection);
  } 

  /**
   * Creates a new "full screen" layout. The image caption will appear
   * at the bottom of the image panel and a control panel will pop up 
   * when the user moves the mouse.
   * 
   * @param collection    the collection of images
   */  
  public FullScreenLayout(ImageCollectionInfo collection) {
    this(collection, "PIC");
  }
  
//  /**
//   * Returns the {@link CaptionOverlay} object.
//   * @return the transparent caption popup window
//   */
//  public CaptionOverlay getCaptionOverlay() {
//    return overlay;
//  }
//  
//  /**
//   * Returns the {@link ControlPanelOverlay} object.
//   * @return the control panel popup window
//   */
//  public ControlPanelOverlay getControlPanelOverlay() {
//    return popup;
//  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.Layout#getRootWidget()
   */
  @Override
  public Widget getRootWidget() {
    return imagePanel;
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.Layout#issueResize()
   */  
  @Override
  public void issueResize() {
    super.issueResize();
    if (overlay != null) overlay.onResized();
    if (popup != null) popup.onResized();
  }
  
  /**
   * Initializes the overlay objects. This method is meant to be overridden by 
   * descendant classes which do not really need overlay objects by an empty
   * method. 
   * @see TiledLayout
   */
  protected void initOverlayWidgets(ImageCollectionInfo info) {
    if (caption != null) 
      overlay = new CaptionOverlay(caption, imagePanel, slideshow, 
          info.getInfo().get(CaptionOverlay.KEY_CAPTION_POSITION));
    String panelPos = info.getInfo().get(PanelOverlayBase.KEY_PANEL_POSITION);
    if (controlPanel != null) {
      if (filmstrip != null) {
        popup = new FilmstripOverlay(controlPanel, imagePanel, panelPos);
        ((FilmstripOverlay) popup).syncWithCaption(overlay);
      } else {
        popup = new ControlPanelOverlay(controlPanel, imagePanel, panelPos);
      }
//      popup.setPossiblePositions(ControlPanelOverlay.CORNERS);
//      if (caption != null) 
//        popup.setPossiblePositions(ControlPanelOverlay.BOTTOM_CAPTION);
//      else
//        popup.setPossiblePositions(ControlPanelOverlay.NORTH_AND_SOUTH);
    }    
  }
}

