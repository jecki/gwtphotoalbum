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
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * A progress bar widget.
 * 
 * <p>The progress bar is initialized with a "maximum" value (integer). 
 * And the progress is then updated with method <code>setValue</code>.
 * An optional label displays the progress in text from, e.g. "5/12".
 * 
 * @author ecki
 *
 */
public class ProgressBar extends Composite {
  protected class Bar extends Widget {
    Bar() {
      setElement(Document.get().createDivElement());
      setStyleName("progressBar");
    }
  }
   
  /** Constant describing the labeling type. */
  public static final int NO_LABEL = 0, PERCENTAGE_LABEL = 1,
                          VALUE_LABEL = 2;
  
  private int         maximum;
  private int         value;
  private SimplePanel frame;
  private Bar         bar;
  private int         labelType = NO_LABEL;
  
  /**
   * Constructor of class <code>ProgressBar</code>
   * 
   * @param maximum  the maximum progress value 
   */
  public ProgressBar(int maximum) {
    this.maximum = maximum;
    this.value = 0;
    frame = new SimplePanel();
    frame.setStyleName("progressFrame");
    bar = new Bar();
    bar.setWidth("0%");    
    frame.setWidget(bar);
    initWidget(frame);
  }
 
  /**
   * Returns the widget for the bar of the progress bar.
   * @return  the widget that represents the bar
   */
  public Widget getBar() {
    return bar;
  }
  
  /**
   * Returns the widget that represents the frame of the
   * progress bar.
   * @return  the frame of the progress bar
   */
  public Widget getFrame() {
    return frame;
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.UIObject#setHeight(java.lang.String)
   */
  @Override
  public void setHeight(String height) {
    super.setHeight(height);   
    frame.setHeight(height);
    bar.setHeight(height);
    bar.getElement().getStyle().setProperty("font-size", height);
    // updateLabeling();
  }
  
  /**
   * Sets the labeling type.
   * @param type  the labeling type, i.e. one of the integer constants
   *              <code>NO_LABEL, PERCENTAGE_LABEL, VALUE_LABEL</code>
   */
  public void setLabelingType(int type) {
    assert type == NO_LABEL || type == PERCENTAGE_LABEL || 
        type == VALUE_LABEL : "Unknown labeling type!";
    
    if (type != labelType) {
      labelType = type;
      updateLabeling();
    }
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
   */
  @Override
  public void setPixelSize(int width, int height) {
    super.setPixelSize(width, height);
    frame.setPixelSize(width, height);
    bar.setHeight(height + "px");
    bar.getElement().getStyle().setPropertyPx("font-size", height);
  }

  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.UIObject#setSize(java.lang.String, java.lang.String)
   */
  @Override
  public void setSize(String width, String height) {
    super.setSize(width, height);
    frame.setSize(width, height);
    bar.setHeight(height);
    bar.getElement().getStyle().setProperty("font-size", height);
  }

  /**
   * Updates the progress bar with a new progress value.
   * @param value  the new progress value
   */
  public void setValue(int value) {
    assert value >= 0 && value <= maximum;
    if (value != this.value) {
      this.value = value;
      String strValue = (value * 100 / maximum) + "%";
      bar.setWidth(strValue);
      updateLabeling();
    }
  }  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.UIObject#setWidth(java.lang.String)
   */
  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    frame.setWidth(width);
  }
  
 
  /** 
   * Updates the labeling of the progress bar; called after the
   * progress value has been set with <code>setValue</code>
   */
  protected void updateLabeling() {
    String text;
    if (labelType == PERCENTAGE_LABEL) {
      text = "&nbsp;" + value * 100 / maximum + " %";
    } else if (labelType == VALUE_LABEL) {
      text = "&nbsp;" + value + "/" + maximum;
    } else {
      text = "&nbsp;";
    }
    bar.getElement().setInnerHTML(text); 
  }
}
