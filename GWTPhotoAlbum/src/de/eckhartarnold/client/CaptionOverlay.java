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
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Encapsulates a caption in a transparent 
 * pop-up window over the image panel. 
 * 
 * <p>For better visibility, a
 * shadow is added to the text (by placing under the overlay popup a second
 * overlay popup with the same caption text but in black color and slightly
 * moved to the lower right direction). 
 * 
 * Note: The CaptionOverlay should be added as WindowListener only after the
 * caption itself has been added!
 */
public class CaptionOverlay implements SlideshowListener,
    PopupPanel.PositionCallback, ResizeListener, AttachmentListener {
  
  public final static String  KEY_CAPTION_POSITION = "caption position";
  public final static String  TOP = "top",
                              BOTTOM = "bottom";
  
  private static int MARGIN = 4;
  private Caption    caption;
  private Widget     baseWidget;
  private boolean    showAtTop = false;
  private PopupPanel popup;
  private CaptionOverlay shadow;
  
  /**
   * Creates a new overlay caption. An overlay caption is laid over some
   * other widget, which is usually the <code>FlipImagePanel</code> which
   * displays the slides.
   * 
   * @param caption    the caption which shall be overlaid
   * @param baseWidget the base widget over which the caption shall be overlaid
   * @param slideshow  the slide show to which the captioned images belong
   */
  public CaptionOverlay(Caption caption, Widget baseWidget, 
      Slideshow slideshow, String position) {
    if (position == TOP) { // NO_SPACING leads to misplacement of shadows in some cases
      caption.setSpacing(Caption.TOP_SPACING);
    } else {
      caption.setSpacing(Caption.BOTTOM_SPACING);      
    }
    shadow = new CaptionOverlay(new Caption(caption), baseWidget, slideshow, position, true);
    caption.addStyleDependentName("overlay");
    init(caption, baseWidget, slideshow, position);
    slideshow.addSlideshowListener(this);    
  }
  
  /**
   * Creates the shadow for an overlay caption. With this constructor the
   * <code>CaptionOverlay</code> object functions as shadow. The difference
   * to a normal <code>CaptionOverlay</code> object is that a shadow does
   * not listen to the <code>Slideshow</code> events and that a shadow does
   * not itself have a shadow (of course!). 
   * 
   * @param caption    the caption which shall be overlaid
   * @param baseWidget the base widget over which the caption shall be overlaid
   *                   (usually an image panel)
   * @param slideshow  the slide show to which the captioned images belong
   * @param dropShadow this parameter is only meant for differentiating the
   *                   shadow constructor from the normal constructor. No
   *                   matter what the value of this parameter is, an overlay
   *                   object created with this constructor will always be a
   *                   shadow! 
   */
  protected CaptionOverlay(Caption caption, Widget baseWidget,
      Slideshow slideshow, String position, boolean dropShadow) {
    shadow = null;
    caption.addStyleDependentName("overlay-shadow");
    init(caption, baseWidget, slideshow, position);    
  }
  
  /**
   * Returns the <code>showAtTop</code> property of the caption
   * @return true if caption appears at the top, false otherwise
   */
  public boolean getShowAtTop() {
    return showAtTop;
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onLoad()
   */     
  public void onLoad(Widget sender) {
    if (!caption.isEmpty()) {
      popup.show();
    }
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#onResized()
   */     
  public void onResized() {
    caption.onResized(); // do this again, because the image panel size might have been changed meanwhile
    if (shadow != null) {
      shadow.onResized();
    }
    setPosition(popup.getOffsetWidth(), Toolbox.getOffsetHeight(popup));
  }  
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onFade()
   */     
  public void onFade() {
    popup.hide();
    if (shadow != null) shadow.onFade();
    caption.onFade();
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onStart()
   */     
  public void onStart() {  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onStop()
   */     
  public void onStop() {  } 
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onShow()
   */     
  public void onShow(int slideNr) {
    popup.hide();
    if (shadow != null) shadow.onShow(slideNr);  
    if (!caption.isEmpty()) 
      popup.setPopupPositionAndShow(this);
  }  

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onUnload()
   */       
  public void onUnload(Widget sender) {
    popup.hide();
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#prepareResized()
   */      
  public void prepareResized() { 
    if (shadow != null) shadow.caption.prepareResized();    
  }  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.PopupPanel.PositionCallback#setPosition()
   */      
  public void setPosition(int offsetWidth, int offsetHeight) {
    int w = baseWidget.getOffsetWidth();
    int h = Toolbox.getOffsetHeight(baseWidget);
    int xpos = baseWidget.getAbsoluteLeft();
    int ypos = baseWidget.getAbsoluteTop();
    
    String align = caption.getElement().getStyle().getProperty("TextAlign");
    if (align == "left") xpos += MARGIN;
    else if (align == "right") xpos += w - offsetWidth - MARGIN;
    else xpos += (w - offsetWidth) / 2;
    if (showAtTop) ypos += MARGIN;
    else ypos += h - offsetHeight - MARGIN;

    int dX = 0, dY = 0;
    if (shadow == null) {
      if (caption.getFontSize() <= 14) {
        dX = 1;
        dY = 1;
      } else {
        dX = 2;
        dY = 2;
      }
    }
        
    popup.setPopupPosition(xpos+dX, ypos+dY);
  }
  
  /** 
   * Tells the <code>CaptionOverlay</code> whether the caption shall be shown
   * at the top or at the bottom of the image.
   * 
   * @param showAtTop  true, if the caption is to be shown at the top 
   */
  public void setShowAtTop(boolean showAtTop) {
    if (showAtTop != this.showAtTop) {
      this.showAtTop = showAtTop;
      if (showAtTop) {
        caption.setSpacing(Caption.TOP_SPACING);
      } else {
        caption.setSpacing(Caption.BOTTOM_SPACING);
      }
      if (shadow != null) shadow.setShowAtTop(showAtTop);
      if (popup.isShowing()) {
        popup.hide();    
        popup.setPopupPositionAndShow(this);
      }
    }
  }

  /**
   * An initialization function that contains code that would otherwise be
   * common to several constructors.
   * 
   * @param caption    the caption
   * @param baseWidget the base widget over which the caption shall be laid
   * @param slideshow  the slide show
   */
  private void init(Caption caption, Widget baseWidget, Slideshow slideshow,
      String position) {
    this.caption = caption;
    this.baseWidget = baseWidget;  
    popup = new PopupPanel();
    popup.add(caption);
    popup.addStyleName("captionPopup");
    popup.setAnimationEnabled(false);
    if (baseWidget instanceof SourcesAttachmentEvents) {
      ((SourcesAttachmentEvents) this.baseWidget).addAttachmentListener(this);
    }
    showAtTop = position == TOP;
  }
}
