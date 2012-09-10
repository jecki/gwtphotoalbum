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

import com.google.gwt.user.client.ui.CellPanel;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
  
  public static final int BACK = 1, GALLERY = 2, PLAY = 4, NEXT = 8;
  
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
      Image img = new Image(name);
      // img.addStyleName("icon");
      is.put(bn, img);
    }
    return is;
  }
  
  private ClickListener           galleryButtonListener;
  private HashMap<String, Image>  icons;
  private Filmstrip               filmstrip;
  private ProgressBar             progress;
  private PushButton              back, next, gallery;
  private ToggleButton            play;
  private SimplePanel             wrapper;
  // private HorizontalPanel         buttonPanel; 
  //private CellPanel               panel;  
  private Slideshow               slideshow;
  private int                     buttonSize;
  private boolean                 hasProgressBar = true;
  private boolean                 autoResize = true;
  private int                     buttonsEnabled = NEXT|GALLERY|PLAY|BACK;
  private int                     buttonsShown = NEXT|GALLERY|PLAY|BACK;
  
  
  public ControlPanel(Slideshow slideshow) {
    this.slideshow = slideshow;
    this.slideshow.addSlideshowListener(this);    
    wrapper = new SimplePanel();
    wrapper.setStyleName("controlPanel");
    buttonSize = ICON_SIZES[0];
    icons = iconSets.get(buttonSize);
    composePanel();
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
  
  public Filmstrip getFilmstrip() {
    return filmstrip;
  }
  
  /**
   * Hides the progress bar.
   */
  public void hideProgressBar() {
    if (hasProgressBar) {
      hasProgressBar = false;
      composePanel();      
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
    if (filmstrip != null) {
      filmstrip.focusImage(slideshow.getCurrentSlide());
    }
  }  
  
  public void onResized() { 
    if (autoResize) {
      final int[][] resolutions = {{800, 600}, {1024,768}, 
          {1280, 1024}, {1680, 1050}, {1600, 1200}};
      final int[] btnSizes = {16, 24, 32, 40, 48, 64, 64};
      
      int w = slideshow.getImagePanel().getOffsetWidth();
      int h = slideshow.getImagePanel().getOffsetHeight();
      int i;
      for (i = 0; i < resolutions.length; i++) {   
        int res[] = resolutions[i];
        if (w < res[0] || h < res[1]) break;
      }
      if (filmstrip != null) i++;
      setCtrlBtnSizePx(btnSizes[i]);
    }
    if (filmstrip != null) filmstrip.onResized();    
  }
  
  public void onShow(int slideNr) {
    progress.setValue(slideNr+1);
    if (filmstrip != null) {
      int save = filmstrip.getSlidingDuration();
      filmstrip.setSlidingDuration(0);
      filmstrip.focusImage(slideNr);
      filmstrip.setSlidingDuration(save);
    }    
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
  public void setButtonEnabled(int buttonFlags) {
    buttonsEnabled = buttonFlags;
    if ((buttonsEnabled & PLAY) == 0) slideshow.stop();
    enableOrDisableButtons();
  }
  
  public void prepareResized() { 
    if (filmstrip != null) filmstrip.prepareResized();
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
  public void setButtonShow(int buttonFlags) {
    boolean redoPanel;
    if (buttonsShown != buttonFlags) {
      redoPanel = true;
    } else {
      redoPanel = false;
    }
    buttonsShown = buttonFlags;
    if ((buttonsShown & PLAY) == 0) slideshow.stop();
    if (redoPanel) composePanel();      
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
    if (buttonSize == size) return;
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
  
  public void swallowFilmstrip(Filmstrip filmstrip, boolean selectableImages) {   
    if (filmstrip != null && selectableImages) {
      filmstrip.setPickImageCallback(new Filmstrip.IPickImage() {
        public void onPickImage(int imageNr) {
          slideshow.stop();
          slideshow.show(imageNr);        
        }
      });
    }
    if (this.filmstrip != filmstrip) {
      this.filmstrip = filmstrip;
      composePanel();
    }    
  }
  
  private void enableOrDisableButtons() {
    back.setEnabled((buttonsEnabled & BACK) != 0);
    gallery.setEnabled((buttonsEnabled & GALLERY) != 0);
    play.setEnabled((buttonsEnabled & PLAY) != 0);
    next.setEnabled((buttonsEnabled & NEXT) != 0);
  }
  
  /**
   * (Re-)creates the button panel using the images stored in the 
   * <code>icons</code> map as symbols on the buttons.
   */
  protected void composePanel() { 
    CellPanel panel = new VerticalPanel();
    
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
    
    // button panel
    HorizontalPanel buttonPanel = new HorizontalPanel();
    buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
    
    back = new PushButton(icons.get("back"), icons.get("back_down"), this);
    if (galleryButtonListener != null) {
      gallery = new PushButton(icons.get("gallery"), icons.get("gallery_down"), 
          galleryButtonListener);
    } else {
      gallery = new PushButton(icons.get("gallery"), icons.get("gallery_down"));      
    }
    play = new ToggleButton(icons.get("play"), icons.get("pause"), this);
    if (slideshow.isRunning()) play.setDown(true);
    next = new PushButton(icons.get("next"), icons.get("next_down"), this);
    
    if ((buttonsShown & BACK) != 0) buttonPanel.add(back);
    if ((buttonsShown & GALLERY) != 0) buttonPanel.add(gallery);
    if (filmstrip != null) {
      back.addStyleName("controlAlternateButton");
      gallery.addStyleName("controlAlternateButton");
      play.addStyleName("controlAlternateButton");
      next.addStyleName("controlAlternateButton");      
      filmstrip.setHeight(buttonSize*2+"px");
      panel.setWidth("100%");    
      //panel.setCellWidth(buttonPanel, "100%");      
      //buttonPanel.setWidth("100%");      
      buttonPanel.add(filmstrip);
      buttonPanel.setCellWidth(filmstrip, "100%");
    }    
    if ((buttonsShown & PLAY) != 0) buttonPanel.add(play);
    if ((buttonsShown & NEXT) != 0) buttonPanel.add(next);
    
    enableOrDisableButtons();
    panel.add(buttonPanel);
    panel.add(progress);
    wrapper.setWidget(panel);
  }
}


