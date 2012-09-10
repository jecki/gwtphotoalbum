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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * @author ecki
 *
 */
public class ProgressBar extends Composite {
  public static final int NO_LABEL = 0, PERCENTAGE_LABEL = 1,
                          VALUE_LABEL = 2;
  
  protected int       value, maximum;
  protected HTML      widget;
  protected Element   frame, bar;
  protected int       labelType = NO_LABEL;
  
  public ProgressBar(int maximum) {
    this.maximum = maximum;
    this.value = 0;
    String frameId = HTMLPanel.createUniqueId();
    String barId = HTMLPanel.createUniqueId();
//    widget = new HTMLPanel("<div style=\"border-width:2px; border-color:darkblue;\" id=\""+
//        frameId+"\">a<div style=\"width:0%; background-color:light-blue; text-align:center; color:black; font-size:x-small;\" id=\""+
//        barId+"\">&nbsp;</div></div>");
    widget = new HTMLPanel("<div id=\""+frameId+"\"><div id=\""+barId+"\">b&nbsp;</div></div>");    
    frame = widget.getElementById(frameId);
    bar = widget.getElementById(barId);
    setFrameProperty("border-width", "2px");
    setFrameProperty("border-color", "darkblue");
    
    setFrameProperty("background-color", "red");
    setBarProperty("background-color", "green");
    setBarProperty("color", "red");
    String tst = frame.getStyle().getProperty("background-color");
    initWidget(widget);
  }
  
  public void setBarProperty(String styleProperty, String value) {
    assert styleProperty != "width" && styleProperty != "height" : 
      "call setWidth() or setHeight() method to set size properties!";    

    bar.getStyle().setProperty(styleProperty, value);    
  }
  
  public void setBarColor(String color) {
    setBarProperty("background-color", color);
  }

  public void setFontSize(String size) {
    setBarProperty("font-size:", size);
  }  
  
  public void setFrameProperty(String styleProperty, String value) {
    assert styleProperty != "width" && styleProperty != "height" : 
        "call setWidth() or setHeight() method to set size properties!";
    
    frame.getStyle().setProperty(styleProperty, value);
  }  

  public void setFrameColor(String color) {
    setFrameProperty("border-color", color);
  }
  
  public void setFrameWidth(String width) {
    setFrameProperty("border-width", width);
  }
  
  @Override
  public void setHeight(String height) {
    super.setHeight(height);
    frame.getStyle().setProperty("height", height);  
    bar.getStyle().setProperty("height", height);     
  }

  public void setLabelingColor(String color) {
    setBarProperty("color", color);
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
    frame.getStyle().setProperty("width", w);   
    frame.getStyle().setProperty("height", h);  
    bar.getStyle().setProperty("height", h);         
  }

  @Override
  public void setSize(String width, String height) {
    super.setSize(width, height);
    frame.getStyle().setProperty("width", width);   
    frame.getStyle().setProperty("height", height);  
    bar.getStyle().setProperty("height", height);     
  }

  public void setValue(int value) {
    assert value >= 0 && value <= maximum;
    if (value != this.value) {
      this.value = value;
      String strValue = (value * 100 / maximum) + "%";
      bar.getStyle().setProperty("width", strValue);
      updateLabeling();
    }
  }  
  
  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    frame.getStyle().setProperty("width", width);    
  }
  
  protected void updateLabeling() {
    String text;
    if (labelType == PERCENTAGE_LABEL) {
      text = value * 100 / maximum + " %";
    } else if (labelType == VALUE_LABEL) {
      text = value + "/" + maximum;
    } else {
      text = "&nbsp;";
    }
    bar.setInnerHTML(text); 
  }
}
