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
import com.google.gwt.dom.client.Element;
//import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author ecki
 *
 */
public class ProgressBar extends Composite {
  public static final int NO_LABEL = 0, PERCENTAGE_LABEL = 1,
                          VALUE_LABEL = 2;
  
  protected int     value, maximum;
  protected HTML    widget;
  protected Element frame, bar;
  protected int     labelType = NO_LABEL;
  
  public ProgressBar(int maximum, String border, String barStyle) {
    this.maximum = maximum;
    this.value = 0;
    barStyle += "width:0%; text-align:left; overflow:visible;";
    widget = new HTML("<div style=\"" + barStyle + "\">&nbsp;</div>");
    frame = widget.getElement();
    bar = frame.getFirstChildElement();
    frame.getStyle().setProperty("border", border);
    initWidget(widget);
  }
  
  public ProgressBar(int maximum) {
    this(maximum, "2px solid #B0B0B0", 
        "background-color:#303060; color:#C8D8FF; font-size:small;");
  }
 
  @Override
  public void setHeight(String height) {
    super.setHeight(height);   
    setStyleProperty(frame, "height", height);
    setStyleProperty(bar, "height", height);     
    updateLabeling();
  }
  
  public void setLabelingType(int type) {
    assert type == NO_LABEL || type == PERCENTAGE_LABEL || 
        type == VALUE_LABEL : "Unknown labeling type!";
    
    if (type != labelType) {
      labelType = type;
      updateLabeling();
    }
  }
  
  @Override
  public void setPixelSize(int width, int height) {
    super.setPixelSize(width, height);
    String w = String.valueOf(width);
    String h = String.valueOf(height);
    setStyleProperty(frame, "width", w);   
    setStyleProperty(frame, "height", h);  
    setStyleProperty(bar, "height", h);     
  }

  @Override
  public void setSize(String width, String height) {
    super.setSize(width, height);
    setStyleProperty(frame, "width", width);   
    setStyleProperty(frame, "height", height);  
    setStyleProperty(bar, "height", height);     
  }

  public void setValue(int value) {
    assert value >= 0 && value <= maximum;
    if (value != this.value) {
      this.value = value;
      String strValue = (value * 100 / maximum) + "%";
      setStyleProperty(bar, "width", strValue);
      updateLabeling();
    }
  }  
  
  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    setStyleProperty(frame, "width", width);    
  }
  
  protected void setStyleProperty(Element element, String property, String value) {
    element.getStyle().setProperty(property, value);
  }
  
  protected void updateLabeling() {
    String text;
    if (labelType == PERCENTAGE_LABEL) {
      text = "&nbsp;" + value * 100 / maximum + " %";
    } else if (labelType == VALUE_LABEL) {
      text = "&nbsp;" + value + "/" + maximum;
    } else {
      text = "&nbsp;";
    }
    bar.setInnerHTML(text); 
  }
}
