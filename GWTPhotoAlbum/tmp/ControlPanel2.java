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

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author eckhart
 *
 */
public class ControlPanel extends Composite implements ClickListener,
    SlideshowListener, ResizeListener {
  
  protected static final String[] BASE_NAMES = { "back", "back_down", "gallery", 
      "gallery_down", "next", "next_down", "play", "pause" };
  protected static final int[]    ICON_SIZES = { 16, 24, 32, 48, 64 };
  protected static HashMap<Integer, HashMap<String, Image>> iconSets;
  
  static {
    iconSets = new HashMap<Integer, HashMap<String, Image>>();
    for (int i: ICON_SIZES)
      iconSets.put(i, ControlPanel.loadImageSet(i));   
  }
  
  /**
   * Loads a set of icons for the control buttons corresponding to the given
   * <code>size</code>. The icons must be stored in the "icons" sub-directory
   * and be named appropriately, e.g. "next_down_24.png" is the icon with an
   * edge size of 24 pixels for the "next" button when it is pressed.
   * @param size  the size of the icons.
   * @return      a map where the respective images are indexed by their
   *              name (without suffix or size indicator, e.g. a proper
   *              index would be "next_down" but not "next_down_24.png"
   */
  protected static HashMap<String, Image> loadImageSet(int size) {
    HashMap<String, Image> is = new HashMap<String, Image>();
    String suffix = "_" + String.valueOf(size) + ".png";
    
    for (String bn: BASE_NAMES) {
      String name = "icons/" + bn + suffix;
      is.put(bn, new Image(name));
    }
    return is;
  }
  
  private ClickListener           galleryButtonListener;
  private HashMap<String, Image>  icons;
  private VerticalPanel           panel;
  private ProgressBar             progress;
  private PushButton              back, next, gallery;
  private ToggleButton            play;
  private SimplePanel             wrapper;
  private Slideshow               slideshow;
  private int                     buttonSize;
  private boolean                 hasProgressBar = true;
  private boolean                 autoResize = true;
  private boolean                 enableNext = true, enableBack = true,
                                  enableGallery = true, enablePlay = true;
  private boolean                 showNext = true, showBack = true,
                                  showGallery = true, showPlay = true;
  
  
  public ControlPanel(Slideshow slideshow) {
    this.slideshow = slideshow;
    this.slideshow.addSlideshowListener(this);    
    wrapper = new SimplePanel();
    wrapper.setStyleName("controlPanel");
    buttonSize = ICON_SIZES[0];
    icons = iconSets.get(buttonSize);
    composePanel();
    wrapper.setWidget(panel);
    initWidget(wrapper);
  }
  
  /**
   * Allows automatic adjustment of the size of the control panel if the size
   * of the image panel changes.
   */
  public void allowAutoResize() {
    autoResize = true;
  }

  /**
   * Disallows automatic adjustment of the size of the control panel if the size
   * of the image panel changes.
   */  
  public void disallowAutoResize() {
    autoResize = false;
  }
  
  /**
   * Returns the size of the control buttons as integer value. The size is 
   * automatically adjusted by the {@link onResize} method. It is guaranteed
   * to have a value greater or equal than 8 and smaller or equal than 128,
   * but usually it is one of the values: 16,24,32,40,48,64.
   * @return the edge size of the control buttons in pixel.
   */
  public int getCrtlBtnSize() {
    return buttonSize;
  }
  
  /**
   * Hides the progress bar.
   */
  public void hideProgressBar() {
    if (hasProgressBar) {
      hasProgressBar = false;
      composePanel();
      wrapper.setWidget(panel);      
    }
  }
  
  public void onClick(Widget sender) {
    if (sender == back) {
      slideshow.stop();
      slideshow.back();
    } else if (sender == next) {
      slideshow.stop();
      slideshow.next();
    } else if (sender == play) {
      if (play.isDown()) {
        slideshow.next();
        slideshow.start();
      } else {
        slideshow.stop();
      }
    } // else { // sender == gallery
    // }
  }  
  
  public void onFade() {  
  }  
  
  public void onResized() {
    if (autoResize) {
      final int[][] resolutions = {{800, 600}, {1024,768}, 
          {1280, 1024}, {1680, 1050}, {1600, 1200}};
      final int[] btnSizes = {16, 24, 32, 40, 48, 64};
      
      int w = slideshow.getImagePanel().getOffsetWidth();
      int h = slideshow.getImagePanel().getOffsetHeight();
      int i;
      for (i = 0; i < resolutions.length; i++) {   
        int res[] = resolutions[i];
        if (w < res[0] || h < res[1]) break;
      }
      setCtrlBtnSizePx(btnSizes[i]);
    }
  }
  
  public void onShow(int slideNr) {
    progress.setValue(slideshow.getCurrentSlide()+1);
  }
  
  public void onStart() {
    if (!play.isDown()) {
      play.setDown(true);
    }
  }
  
  public void onStop() {
    if (play.isDown()) {
      play.setDown(false);
    }
  }  
  
  /**
   * Determines which buttons are enabled on the panel. If the play button is
   * disabled, the slide show will be stopped in case it was running.
   * @param back    if false, the back button will be disabled
   * @param gallery if false, the button to return to the gallery will be 
   *                disabled
   * @param play    if false, the play button will be disabled
   * @param next    if false, the next button will be disabled
   */
  public void setButtonEnabled(boolean back, boolean gallery, boolean play, 
      boolean next) {
    enableBack = back;
    enableGallery = gallery;
    enablePlay = play;
    enableNext = next;
    if (!enablePlay) slideshow.stop();
    enableOrDisableButtons();
  }
  
  /**
   * Determines which buttons are shown on the panel. If the play button is
   * hidden the slide show will be stopped in case it was running.
   * @param back    if false, the back button will not be shown
   * @param gallery if false, the button to return to the gallery will not be 
   *                shown
   * @param play    if false, the play button will not be shown
   * @param next    if false, the next button will not be shown
   */
  public void setButtonShow(boolean back, boolean gallery, boolean play, 
      boolean next) {
    boolean redoPanel;
    if (back != showBack || gallery != showGallery || play != showPlay ||
        next != showNext) {
      redoPanel = true;
    } else {
      redoPanel = false;
    }
    showBack = back;
    showGallery = gallery;
    showPlay = play;
    showNext = next;
    if (!showPlay) slideshow.stop();
    if (redoPanel) {
      composePanel();
      wrapper.setWidget(panel);      
    }
  }
  
  /**
   * Sets the size of the control buttons. If the new size is widely different
   * form the old one, this involves picking a fresh set of icons for the 
   * control buttons.
   * 
   * @param size  the edge size of each control button in pixel.
   */
  public void setCtrlBtnSizePx(int size) {
    assert size >= 8 && size <= 128;
    buttonSize = size;
    HashMap<String, Image> match = iconSets.get(ICON_SIZES[ICON_SIZES.length-1]);
    for (int i: ICON_SIZES) {
      if (size <= i) {
        match = iconSets.get(i);
        break;
      }
    }
    for (Image im: match.values()) im.setPixelSize(size, size);    
    if (match != icons) {
      icons = match;
      // wrapper.remove(panel);
      composePanel();
      wrapper.setWidget(panel);
    }
  }
  
  /**
   * Sets the listener for the "back to the gallery button". In order to listen
   * to any of the other buttons, add a {@link SlideshowListener} to the
   * slide show.
   * @param listener the <code>ClickListener</code> for the gallery button
   */
  public void setGalleryButtonListener(ClickListener listener) {
    if (gallery != null) {
      gallery.removeClickListener(galleryButtonListener);
      gallery.addClickListener(listener);
    }
    galleryButtonListener = listener;
  }  
  
  /**
   * Shows the progress bar.
   */
  public void showProgressBar() {
    if (!hasProgressBar) {
      hasProgressBar = true;
      composePanel();
      wrapper.setWidget(panel);      
    }
  }  
  
//  /**
//   * Sets the <code>ClickSniffer</code>, i.e. an object that gets informed if
//   * any of the buttons of the <code>ControlPanel</code> has been clicked.
//   * @param sniffer  the object that gets informed about button clicks
//   */
//  void setClickSniffer(ClickSniffer sniffer) {
//    clickSniffer = sniffer;
//  }
  
  private void enableOrDisableButtons() {
    back.setEnabled(enableBack);
    gallery.setEnabled(enableGallery);
    play.setEnabled(enablePlay);
    next.setEnabled(enableNext);
  }
  
  /**
   * (Re-)creates the button panel using the images stored in the 
   * <code>icons</code> map as symbols on the buttons.
   */
  protected void composePanel() { 
    panel = new VerticalPanel();
    
    // button panel
    HorizontalPanel buttonPanel = new HorizontalPanel();     
    back = new PushButton(icons.get("back"), icons.get("back_down"), this);
    if (showBack) buttonPanel.add(back);
    if (galleryButtonListener != null) {
      gallery = new PushButton(icons.get("gallery"), icons.get("gallery_down"), 
          galleryButtonListener);
    } else {
      gallery = new PushButton(icons.get("gallery"), icons.get("gallery_down"));      
    }
    if (showGallery) buttonPanel.add(gallery);
    play = new ToggleButton(icons.get("play"), icons.get("pause"), this);
    if (slideshow.isRunning()) play.setDown(true);
    if (showPlay) buttonPanel.add(play);
    next = new PushButton(icons.get("next"), icons.get("next_down"), this);
    if (showNext) buttonPanel.add(next);
    panel.add(buttonPanel);
    enableOrDisableButtons();
    
    
    // progress bar   
    progress = new ProgressBar(slideshow.size());
    if (buttonSize >= 24) progress.setLabelingType(ProgressBar.VALUE_LABEL);
    else progress.setLabelingType(ProgressBar.NO_LABEL);
    if (buttonSize <= 32) progress.getFrame().addStyleDependentName("thin");
    if(buttonSize > 48) progress.getBar().addStyleDependentName("16px");
    else if (buttonSize > 32) progress.getBar().addStyleDependentName("12px");
    else if (buttonSize >= 28) progress.getBar().addStyleDependentName("10px");  
    else if (buttonSize >= 24) progress.getBar().addStyleDependentName("9px");
    else if (buttonSize >= 20) progress.getBar().addStyleDependentName("4px");
    else progress.getBar().addStyleDependentName("3px");
    int value = slideshow.getCurrentSlide();
    if (value >= 0) progress.setValue(value+1);
    panel.add(progress);
  }
}


