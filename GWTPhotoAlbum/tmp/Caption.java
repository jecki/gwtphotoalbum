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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
 
/**
 * Displays the image caption of the currently
 * displayed slide of a {@link Slideshow}. 
 * 
 * <p>The caption may contain
 * simple text or HTML code. 
 */
public class Caption extends Composite implements SlideshowListener, 
    ResizeListener {
  private Slideshow slideshow;
  private String[]  captions;
  private String[]  originalCaptions;
  private HTML      htmlLabel;
  private int       fontSize;
  private int       current = -1;
  private String    spacer = "";
  private boolean   spacing = false;
  
  /**
   * Creates a new <code>Caption</code> object. The <code>captions</code>
   * parameter must contain the caption strings for the images of the
   * <code>slideshow</code> in the same order as the images in the slide show.
   *   
   * @param slideshow the <code>Slideshow</code> for which the captions are
   *                  to be displayed
   * @param captions  an array of string that contains the HTML formatted 
   *                  captions. The length of the array must be the same as
   *                  number of images in the slide show.
   */
  public Caption(Slideshow slideshow, String[] captions) {
    assert slideshow.size() == captions.length;

    this.slideshow = slideshow;
    this.originalCaptions = captions;
    htmlLabel = new HTML();
    htmlLabel.setStyleName("caption");
    setSpacing(true);    
    fontSize = 9;
    htmlLabel.addStyleDependentName(fontSize+"px");
    initWidget(htmlLabel);
    onResized();
    slideshow.addSlideshowListener(this);
  }

  /**
   * Clones the <code>Caption</code> object. Cloning captions is used by
   * {@link CaptionOverlay} in order to imitate shadowed text, which 
   * unfortunately most browsers do not support natively. 
   */
  public Caption clone() {
    Caption copy = new Caption(slideshow, captions);
    copy.fontSize = fontSize;
    copy.htmlLabel.setHTML(getText());
    return copy;
  }  
  
  /**
   * Returns the font size of the caption.
   * 
   * @return font size in pixel
   */
  public int getFontSize() {
    return fontSize;
  }

  /**
   * Returns the current Text the caption displays.
   * 
   * @return the HTML formatted Text of the caption
   */
  public String getText() {
    if (current == -1) return spacer;
    else return captions[current];
  }

  /**
   * Returns true, if the caption is empty (at the moment).
   */
  public boolean isEmpty() {
    return getText().equals(spacer);
  }
  
  /**
   * Returns true, if spacers ("&nbsp;") are used as a substitute for the 
   * caption string, in case the caption is empty.
   */
  public boolean isSpacing() {
    return spacing;
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onFade()
   */  
  public void onFade() {
    current = -1;
    htmlLabel.setHTML(getText());
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
    current = slideNr;
    htmlLabel.setHTML(getText());
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#onResized()
   */    
  public void onResized() {
    int w = slideshow.getImagePanel().getOffsetWidth();
    int h = slideshow.getImagePanel().getOffsetHeight();
    if (w < h) h = w;
    h /= 32;
    int[] steps = { 9, 10, 12, 14, 18, 24, 32, 40, 48, 64 };
    int i = 0;
    while (h > steps[i] && i < steps.length) i++;
    htmlLabel.removeStyleDependentName(fontSize+"px");
    fontSize = steps[i];
    htmlLabel.addStyleDependentName(fontSize+"px");
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#prepareResized()
   */     
  public void prepareResized() { }
  
  /**
   * Sets the usage of a spacer (e.g. "&nbsp;") whenever the caption is empty.
   * Otherwise the caption is simply omitted. Spacers are needed in some
   * cases in order to preserve the screen layout. By default the use
   * of a spacer is turned on.
   * 
   * @param on true, if a spacer ("&nbsp;") shall be used.
   */
  public void setSpacing(boolean on) {
    if (this.spacing == on) return;
    this.spacing = on;
    
    int[] lineBreaks = new int[originalCaptions.length];
    
    if (this.spacing) {
      this.captions = new String[originalCaptions.length];
      int maxLineBreaks = 0;
      for (int k = 0; k < originalCaptions.length; k++) {
        String cap = originalCaptions[k];
        int i = 0, count = 0;
        while (i < cap.length() && i >= 0) {
          i = cap.indexOf("<br", i);
          if (i >= 0) {
            count++;
            i++;
          }
        }
        lineBreaks[k] = count;
        if (count > maxLineBreaks) maxLineBreaks = count;
      }
      String[] stuffings = new String[maxLineBreaks+1];
      stuffings[0] = "";
      for (int k = 1; k < stuffings.length; k++) 
          stuffings[k] = stuffings[k-1] + "<br />&nbsp;";
      spacer = stuffings[stuffings.length-1];
      for (int k = 0; k < originalCaptions.length; k++) {
        captions[k] = stuffings[maxLineBreaks-lineBreaks[k]] +
            originalCaptions[k];
      }
      htmlLabel.setHTML(getText());   
      
    } else {
      this.captions = this.originalCaptions;
      spacer = "";
      htmlLabel.setHTML(getText());
    }
  } 

}

