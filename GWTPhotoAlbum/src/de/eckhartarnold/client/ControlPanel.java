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
import com.google.gwt.event.shared.HandlerRegistration;
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
 * The control panel of the slide show. 
 * 
 * <p>The control panel consists of up to
 * four buttons: back, gallery, play, next and an optional progress bar. 
 * In addition to that the control panel can "swallow" a film strip which 
 * will be placed in the middle between the gallery and the play button.
 * The buttons can be disabled or completely be hidden (methods 
 * <code>setButtonEnable</code> and <code>setButtonShow</code>). 
 * 
 * @author eckhart
 *
 */
public class ControlPanel extends Composite implements ClickHandler,
    SlideshowListener, ResizeListener, AttachmentListener {
  
  /** Constants (bit values) for the control panel buttons */
  public static final int BEGIN = 1, BACK = 2, HOME = 4, PLAY = 8, NEXT = 16, END = 32;

  /** an array of the possible pixel sizes of the buttons */
  protected static final int[]    ICON_SIZES = { 16, 24, 32, 48, 64 };
  
  /** 
   * the base names (without size appendices, e.g. "_32" for the 32 pixel size
   * version of a button) of image files of the control buttons 
   */
  protected static final String[] BASE_NAMES = { "begin", "begin_down",
      "back", "back_down", "gallery", "gallery_down", "play", "pause", 
      "next", "next_down", "end", "end_down" };
  
  /** a map that contains for each possible pixel size of the buttons another
   * map which maps the base name of the button to its image object
   */
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
  
  private ClickHandler            homeButtonHandler;
  private HandlerRegistration     homeButtonRegistration;
  private HashMap<String, Image>  icons;
  private Filmstrip               filmstrip;
  private ProgressBar             progress;
  private PushButton              begin, back, home, next, end;
  private ToggleButton            play;
  private SimplePanel             wrapper;
  private Slideshow               slideshow;
  private Tooltip                 beginTooltip, backTooltip, 
                                  homeTooltip, playPauseTooltip,
                                  nextTooltip, endTooltip;
  private int                     buttonSize;
  private boolean                 hasProgressBar = true;
  private boolean                 autoResize = true;
  private int                     buttonsEnabled = BEGIN|NEXT|HOME|PLAY|BACK|END;
  private int                     buttonsShown = BEGIN|NEXT|HOME|PLAY|BACK|END;
  
  /**
   * The constructor of class <code>ControlPanel</code>. The control panel
   * is initialized with a slide show. By default it features a progress bar
   * and all buttons are visible and enabled.
   *  
   * @param slideshow the slide show to which this control panel shall be 
   *                  attached
   */
  public ControlPanel(Slideshow slideshow) {
    this.slideshow = slideshow;
    this.slideshow.addSlideshowListener(this);
    this.slideshow.getImagePanel().addAttachmentListener(this);
    wrapper = new SimplePanel();
    wrapper.setStyleName("controlPanel");
    buttonSize = ICON_SIZES[0];
    icons = iconSets.get(buttonSize);
    beginTooltip = new Tooltip(I18N.i18n.begin());
    endTooltip = new Tooltip(I18N.i18n.end());    
    backTooltip = new Tooltip(I18N.i18n.back());
    nextTooltip = new Tooltip(I18N.i18n.next());
    homeTooltip = new Tooltip(I18N.i18n.home());
    playPauseTooltip = new Tooltip(I18N.i18n.playPause());
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
   * automatically adjusted by the <code>onResized</code> method. It is 
   * guaranteed to have a value greater or equal than 8 and smaller or equal 
   * than 128, but usually it is one of the values: 16,24,32,40,48,64.
   * @return the edge size of the control buttons in pixel.
   */
  public int getCrtlBtnSize() {
    return buttonSize;
  }
  
//  /**
//   * Returns the "swallowed" film strip or <code>null</code> if the control
//   * panel does not contain a film strip.
//   * @return reference to the film strip of the panel or <code>null</code>
//   */
//  public Filmstrip getFilmstrip() {
//    return filmstrip;
//  }
  
  /**
   * Hides the progress bar.
   */
  public void hideProgressBar() {
    if (hasProgressBar) {
      hasProgressBar = false;
      composePanel();      
    }
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.ClickListener.onClick()
   */    
  public void onClick(ClickEvent event) {
    Object sender = event.getSource();
    if (sender == back) {
      slideshow.stop();
      slideshow.back();
    } else if (sender == next) {
      slideshow.stop();
      slideshow.next();
    } else if (sender == begin) {
      slideshow.stop();
      slideshow.show(0);
    } else if (sender == end) {
      slideshow.stop();
      slideshow.show(slideshow.size()-1);
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
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onFade()
   */      
  public void onFade() {
    progress.setValue(slideshow.getCurrentSlide()+1);     
    if (filmstrip != null) filmstrip.focusImage(slideshow.getCurrentSlide());
  }  

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onLoad(com.google.gwt.user.client.ui.Widget)
   */
  public void onLoad(Widget sender) {
    beginTooltip.setMaxAppearances(2);
    endTooltip.setMaxAppearances(2);
    backTooltip.setMaxAppearances(2);
    nextTooltip.setMaxAppearances(2);
    homeTooltip.setMaxAppearances(4);
    playPauseTooltip.setMaxAppearances(3);    
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener.onResized()
   */    
  public void onResized() { 
    if (autoResize) {
      // final int[][] resolutions = {{640,480}, {800, 600}, {1024,768}, 
      //    {1280, 1024}, {1680, 1050}};
      final int[][] resolutions = {{320,240}, {640, 480}, {1024, 600}, 
          {1440, 1050}, {1920, 1200}};
      final int[] btnSizes = {16, 24, 32, 40, 48, 64, 64};
      
      int w = slideshow.getImagePanel().getOffsetWidth();
      int h = Toolbox.getOffsetHeight(slideshow.getImagePanel());
      // GWT.log("ControlPanel.onResized: panel dimensions: "+String.valueOf(w)+"x"+String.valueOf(h));
      int i;
      for (i = 0; i < resolutions.length; i++) {   
        int res[] = resolutions[i];
        if (w < res[0] || h < res[1]) break;
      }
      if (filmstrip != null) i++; // don't remove this line or switching from normal ctrl panel to filmstrip might get problems!
      // GWT.log("ControlPanel.onResized: ButtonSize: "+String.valueOf(btnSizes[i]));
      setCtrlBtnSizePx(btnSizes[i]);
    }
    if (filmstrip != null) filmstrip.onResized();
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onShow()
   */   
  public void onShow(int slideNr) {
    progress.setValue(slideNr+1);
    if (filmstrip != null) {
      filmstrip.focusImage(slideNr);
    }    
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onStart()
   */   
  public void onStart() {
    if (!play.isDown()) {
      play.setDown(true);    
    }
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onStop()
   */   
  public void onStop() {
    if (play.isDown()) {
      play.setDown(false);     
    }
  }  

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onUnload(com.google.gwt.user.client.ui.Widget)
   */
  public void onUnload(Widget sender) {  
    beginTooltip.hide();
    endTooltip.hide();
    backTooltip.hide();
    nextTooltip.hide();
    homeTooltip.hide();
    playPauseTooltip.hide();     
  }  
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener.prepareResized()
   */    
  public void prepareResized() { 
    if (filmstrip != null) filmstrip.prepareResized();
  }   
  
  /**
   * Determines which buttons are enabled on the panel. If the play button is
   * disabled, the slide show will be stopped in case it was running.
   * 
   * @param buttonFlags  the bit set of the buttons that are to be enabled 
   */
  public void setButtonEnabled(int buttonFlags) {
    buttonsEnabled = buttonFlags;
    if ((buttonsEnabled & PLAY) == 0) slideshow.stop();
    enableOrDisableButtons();
  } 
  
  /**
   * Determines which buttons are shown on the panel. If the play button is
   * hidden the slide show will be stopped in case it was running.
   *
   * @param buttonFlags  the bit set of the buttons that are to be shown
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
    assert size >= 16 && size <= 128;
    if (buttonSize == size) return;
    buttonSize = size;
    HashMap<String, Image> match = iconSets.get(ICON_SIZES[ICON_SIZES.length-1]);
    for (int i: ICON_SIZES) {
      if (size <= i) {
        match = iconSets.get(i);
        break;
      }
    }
    // need to set sizes for images that are still loading
    // using im.getHeight() to query sizes is a really bad idea before image 
    // has been fully loaded!
    for (Image im: match.values()) im.setPixelSize(size/2, size); // size/2 only works with the grey icons set! for the colored icon set use size * 3 / 4 
    if (match != icons || filmstrip != null) {
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
  public void setHomeButtonListener(ClickHandler handler) {
    if (home != null) {
      if (homeButtonRegistration != null) {
        homeButtonRegistration.removeHandler();
      }
      homeButtonRegistration = home.addClickHandler(handler);
    }
    homeButtonHandler = handler;
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
  
  /**
   * Swallows a {@link Filmstrip} widget. The film strip will then become part
   * of the control panel. The control panel will automatically be resized so
   * that it covers the full width of the browser window (or the parent widget).
   * @param filmstrip        the film strip widget
   * @param selectableImages true, if the images of the film strip shall be 
   *                         made selectable so that by clicking on one them
   *                         the slide show jumps to that image
   */
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
  
  
  /**
   * (Re-)creates the button panel using the images stored in the 
   * <code>icons</code> map as symbols on the buttons.
   */
  protected void composePanel() { 
    VerticalPanel vpanel = new VerticalPanel();
    HorizontalPanel buttonPanel = new HorizontalPanel();
    buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);    
    
    // progress bar   
    progress = new ProgressBar(slideshow.size());
    int btnSize;
    if (filmstrip != null) btnSize = buttonSize / 2;
    else btnSize = buttonSize;
    if (btnSize >= 24) progress.setLabelingType(ProgressBar.VALUE_LABEL);
    else progress.setLabelingType(ProgressBar.NO_LABEL);
    if (btnSize <= 32) progress.getFrame().addStyleDependentName("thin");
    if(btnSize > 48) progress.getBar().addStyleDependentName("16px");
    else if (btnSize > 32) progress.getBar().addStyleDependentName("12px");
    else if (btnSize >= 28) progress.getBar().addStyleDependentName("10px");  
    else if (btnSize >= 24) progress.getBar().addStyleDependentName("9px");
    else if (btnSize >= 20) progress.getBar().addStyleDependentName("4px");
    else progress.getBar().addStyleDependentName("3px");
    int value = slideshow.getCurrentSlide();
    if (value >= 0) progress.setValue(value+1);    
    
    // button panel
    begin = new PushButton(icons.get("begin"), icons.get("begin_down"), this);
    Tooltip.addToWidget(beginTooltip, begin);    
    back = new PushButton(icons.get("back"), icons.get("back_down"), this);
    Tooltip.addToWidget(backTooltip, back);
    
    if (homeButtonHandler != null) {
      home = new PushButton(icons.get("gallery"), icons.get("gallery_down"),
          homeButtonHandler);
    } else {
      home = new PushButton(icons.get("gallery"), 
          icons.get("gallery_down"));      
    }
    Tooltip.addToWidget(homeTooltip, home);
    play = new ToggleButton(icons.get("play"), icons.get("pause"), this);
    Tooltip.addToWidget(playPauseTooltip, play);
    if (slideshow.isRunning()) play.setDown(true);
    
    next = new PushButton(icons.get("next"), icons.get("next_down"), this);
    Tooltip.addToWidget(nextTooltip, next);
    end = new PushButton(icons.get("end"), icons.get("end_down"), this);
    Tooltip.addToWidget(endTooltip, end);
    
    // if ((buttonsShown & BEGIN) != 0) buttonPanel.add(begin);
    if ((buttonsShown & BACK) != 0) buttonPanel.add(back);
    if ((buttonsShown & HOME) != 0) buttonPanel.add(home);
    if (filmstrip != null) {          
      filmstrip.setHeight(buttonSize*2+"px");
      vpanel.add(filmstrip);
      vpanel.add(progress);  
      vpanel.setWidth("100%");
      buttonPanel.add(vpanel);
      buttonPanel.setCellWidth(vpanel, "100%");
      buttonPanel.addStyleName("controlFilmstripBackground");
      addButtonStyles("controlFilmstripButton");       
    } else {
      buttonPanel.addStyleName("controlPanelBackground");
      addButtonStyles("controlPanelButton");      
    }
    if ((buttonsShown & PLAY) != 0) buttonPanel.add(play);
    if ((buttonsShown & NEXT) != 0) buttonPanel.add(next);
    // if ((buttonsShown & END) != 0) buttonPanel.add(end);    
    
    enableOrDisableButtons();
    if (filmstrip != null) {
      wrapper.setWidget(buttonPanel);
    } else {
      vpanel.add(buttonPanel);
      vpanel.add(progress);
      wrapper.setWidget(vpanel);      
    }
  }

  /**
   * Transfers the state of the <code>buttonsEnabled</code> flags which
   * can be set with method <code>setButtonEnabled</code> to the button
   * widgets.
   */
  protected void enableOrDisableButtons() {
    begin.setEnabled((buttonsEnabled & BEGIN) != 0);    
    back.setEnabled((buttonsEnabled & BACK) != 0);
    home.setEnabled((buttonsEnabled & HOME) != 0);
    play.setEnabled((buttonsEnabled & PLAY) != 0);
    next.setEnabled((buttonsEnabled & NEXT) != 0);
    end.setEnabled((buttonsEnabled & END) != 0);    
  }
  
  
  /**
   * Sets the stylenames for all buttons and the panel with the 
   * addStyleName method.
   * @param buttonStyle     css style for the buttons 
   */
  private void addButtonStyles(String buttonStyle) {
    begin.addStyleName(buttonStyle);      
    back.addStyleName(buttonStyle);
    home.addStyleName(buttonStyle);
    play.addStyleName(buttonStyle);
    next.addStyleName(buttonStyle);
    end.addStyleName(buttonStyle);     
  }  
}


