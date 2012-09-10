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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;



/**
 * Displays the image caption of the currently
 * displayed slide of a {@link Slideshow}. 
 * 
 * <p>The caption may contain
 * simple text or HTML code. 
 */
public class Caption extends Composite implements SlideshowListener, 
    ResizeListener {
  public static final int  NO_SPACING = 0, TOP_SPACING = 1, BOTTOM_SPACING = 2; 
  
  private Slideshow  slideshow;
  private SafeHtml[] captions;
  private SafeHtml[] stuffings;
  private HTML       htmlLabel;
  private int        fontSize;
  private int        current = -1;
  private OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml spacer, emptySpacer; 
  private int        spacing = -1;
  
  
  /**
   * Creates a new <code>Caption</code> object. The <code>captions</code>
   * parameter must contain the caption strings for the images of the
   * <code>slideshow</code> in the same order as the images in the slide show.
   *   
   * @param slideshow the <code>Slideshow</code> for which the captions are
   *                  to be displayed
   * @param captions  an array of safe HTML strings that contains the HTML formatted 
   *                  captions. The length of the array must be the same as
   *                  number of images in the slide show.
   */
  public Caption(Slideshow slideshow, SafeHtml[] captions) {
    assert slideshow.size() == captions.length;

    this.slideshow = slideshow;
    this.captions = captions;
    htmlLabel = new HTML();
    htmlLabel.setStyleName("caption");   
    fontSize = 9;
    htmlLabel.addStyleDependentName(fontSize+"px");
    setupSpacers();
    setSpacing(BOTTOM_SPACING);     
    initWidget(htmlLabel);
    onResized();
    slideshow.addSlideshowListener(this);
  }  
  
  /**
   * Copy constructor for cloning captions. Cloning captions is used by
   * {@link CaptionOverlay} in order to imitate shadowed text, which 
   * unfortunately some browsers do not support natively. 
   */
  public Caption(Caption other) {
    this.slideshow = other.slideshow;
    this.captions = other.captions;
    this.stuffings = other.stuffings;
    this.fontSize = other.fontSize;
    this.current = other.current;
    this.spacing = other.spacing;
    this.spacer = other.spacer;
    this.emptySpacer = other.emptySpacer;
    htmlLabel = new HTML();
    htmlLabel.setStyleName("caption"); 
    htmlLabel.addStyleDependentName(fontSize+"px");
    initWidget(htmlLabel);
    htmlLabel.setHTML(getText());    
    onResized();
    slideshow.addSlideshowListener(this);  
  }
  
//  /**
//   * Clones the <code>Caption</code> object. Cloning captions is used by
//   * {@link CaptionOverlay} in order to imitate shadowed text, which 
//   * unfortunately most browsers do not support natively. 
//   */
//  public Caption clone() {
//    Caption copy = new Caption(slideshow, captions);
//    copy.fontSize = fontSize;
//    copy.htmlLabel.setHTML(getText());
//    return copy;
//  }  
  
  /**
   * Returns the font size of the caption.
   * 
   * @return font size in pixel
   */
  public int getFontSize() {
    return fontSize;
  }

  /**
   * Returns the spacing property, which can be either NO_SPACING,
   * TOP_SPACING or BOTTOM_SPACING depending on whether spacings
   * is used and whether it is tuned for captions appearing above or below
   * the slides.
   */
  public int getSpacing() {
    return spacing;
  }  
  
  /**
   * Returns the current Text the caption displays.
   * 
   * @return the HTML formatted Text of the caption
   */
  public SafeHtml getText() {
    if (current == -1) return (spacing == NO_SPACING) ? emptySpacer : spacer;
    else {
      SafeHtmlBuilder builder = new SafeHtmlBuilder();
      if (spacing == BOTTOM_SPACING) {
        builder.append(stuffings[current]);
        builder.append(captions[current]);
        return builder.toSafeHtml();
      } else if (spacing == TOP_SPACING) {
        builder.append(captions[current]);        
        builder.append(stuffings[current]);
        return builder.toSafeHtml();        
      } else {
        return captions[current];
      } 
    }
  }

  /**
   * Returns true, if the caption is empty (at the moment).
   */
  public boolean isEmpty() {
    SafeHtml text = getText();
    return text.equals(spacer) || text.equals(emptySpacer);
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onFade()
   */  
  public void onFade() {
    current = -1;
    htmlLabel.setHTML(getText());
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener#onShow()
   */    
  public void onShow(int slideNr) {
    current = slideNr;
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
   * @see de.eckhartarnold.client.ResizeListener#onResized()
   */    
  public void onResized() {
    int w = slideshow.getImagePanel().getOffsetWidth();
    int h = Toolbox.getOffsetHeight(slideshow.getImagePanel());
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
   * Sets the spacing property. Spacers ("<br />&nbsp;") are used to make
   * sure that every caption has the same height, in case different captions
   * contain different numbers of line breaks. They will be added either
   * before the caption text (BOTTOM_SPACING) or after the caption text
   * (TOP_SPACING) or not at all (NO_SPACING).
   * 
   * @param TOP_SPACING, BOTTOM_SPACING or NO_SPCAING depending on the type
   *        of spacing to be used.
   */
  public void setSpacing(int spacing) {
    assert spacing == BOTTOM_SPACING || spacing == TOP_SPACING || 
           spacing == NO_SPACING;
    this.spacing = spacing;
    htmlLabel.setHTML(getText());      
  } 

  
  /**
   * Initializes the fields <code>stuffings, spacer, emptySpacer</code>
   * with spacers (concatenations of "<br />&nbsp;"). To be called by
   * the constructor.
   */
  private void setupSpacers() {
    int[] lineBreaks = new int[captions.length];
    int maxLineBreaks = 0;
    for (int k = 0; k < captions.length; k++) {
      String cap = captions[k].asString();
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
    String[] brCascade = new String[maxLineBreaks+1];
    brCascade[0] = "";
    for (int k = 1; k < brCascade.length; k++) 
        brCascade[k] = brCascade[k-1] + "<br />&nbsp;";
    spacer = new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
        brCascade[brCascade.length-1]);
    emptySpacer = new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml("");
    stuffings = new SafeHtml[captions.length];
    for (int k = 0; k < captions.length; k++) {
      stuffings[k] =  new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml(
          brCascade[maxLineBreaks-lineBreaks[k]]);
    }    
  }
}

