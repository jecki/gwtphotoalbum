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

import com.google.gwt.animation.client.Animation;
//import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provides a special kind of animation that lets 
 * a widget fade in or fade out.
 * 
 * @author eckhart
 */
class Fade extends Animation {
  /**
   * Sets the opacity (i.e. translucency) of a widget to a specified value
   * between 0.0 (invisible) and 1.0 (fully visible, i.e. not translucent).
   * @param widget  the widget the opacity of which is to be changed
   * @param opacity the opacity value ranging from 0.0 to 1.0
   */
  public static void setOpacity(Widget widget, double opacity) {
    assert opacity >= 0.0 && opacity <= 1.0;
    
    Style  style = widget.getElement().getStyle();
    String opStr = String.valueOf(opacity);
    String ieOpStr = String.valueOf((int) (opacity*100+0.5));
        
    style.setProperty("opacity", opStr);
    style.setProperty("MozOpacity", opStr);
    style.setProperty("KhtmlOpacity", opStr);
    style.setProperty("filter", "alpha(opacity=" + ieOpStr + ")");
  }    
  
//  public static void setOpacity(Widget widget, double opacity) {
//    setOpacityJS(widget.getElement(), (int) (opacity*100+0.5));
//  }
//  
//  public static native void setOpacityJS(Element element, int opacity) /*-{
//    style = element.style
//    style.opacity = (opacity / 100);
//    style.MozOpacity = (opacity / 100);
//    style.KhtmlOpacity = (opacity / 100);
//    style.filter = "alpha(opacity=" + opacity + ")"; 
//  }-*/;
  
  private double from, to, threshold, _opacity = -1.0;
  private Widget widget;
  private boolean completeOnCancel = true;
  
  /**
   * Default constructor for class <code>Fade</code>. The default
   * constructor takes the widget to fade as argument. 
   * Often, it is advisable to create the instance of the 
   * <code>Fade</code> class before the widget is added to DOM tree
   * to avoid any jump cuts when fading in the widget. 
   * 
   * <p>The default constructor assumes the fading to range from an
   * opacity of 0.0 to 1.0. So, before any of the {@link Animation.run}
   * methods is called the widget is invisible.
   * 
   * @param widget  the widget that is going to be faded in or out. 
   */
  public Fade(Widget widget) { 
    this(widget, 0.0, 1.0);
  }

  /**
   * Constructor for class <code>Fade</code>. Takes the widget to fade as
   * argument as well as the opacity range for fading. The opacity of the
   * widget is immediately set to the beginning (<code>from</code>) of
   * the range.
   * 
   * @param widget  the widget that is going to be faded in or out. 
   * @param from    opacity value to start with
   * @param to      final opacity value
   */
  public Fade(Widget widget, double from, double to) { 
    this(widget, from, to, 0.0);
  }  
  
  /**
   * Constructor for class <code>Fade</code>. Takes the widget to fade as
   * argument as well as the opacity range for fading. The opacity of the
   * widget is immediately set to the beginning (<code>from</code>) of
   * the range.
   * 
   * @param widget    the widget that is going to be faded in or out. 
   * @param from      opacity value to start with
   * @param to        final opacity value
   * @param threshold threshold, before opacity changes become visible
   */
  public Fade(Widget widget, double from, double to, double threshold) { 
    this.widget = widget;
    setFadingSpan(from, to, threshold);
  }  
  
  /**
   * Turns completion of fading when fading is canceled on or off. 
   * If the parameter <code>on</code> is true, the opacity will be set to 
   * the target value when the animation is canceled (this is also the 
   * default behavior). Otherwise it will be left at its last value.
   * @param on if true, completion on cancel is turned on.
   */
  public void setCompleteOnCancel(boolean on) {
    completeOnCancel = on; 
  }
  
  /**
   * Sets the range of opacity values over which the widget shall be faded.
   * The opacity values must always be >= 0.0 and <= 1.0.
   * 
   * @param from  opacity when the fading begins
   * @param to    final opacity of the widget when the fading ends
   * @param threshold threshold, before opacity changes become visible
   */
  public void setFadingSpan(double from, double to, double threshold) {
    assert from >= 0.0 && from <= 1.0 && to >= 0.0 && to <= 1.0;
    assert threshold <= Math.abs(to-from);
    this.threshold = threshold;    
    this.from = from;
    this.to = to;
    changeOpacity(from);
  } 
  
  /**
   * Changes the opacity and registers the new opacity level.
   * @param opacity  the new opacity of the imgae.
   */
  protected void changeOpacity(double opacity) {
    if (_opacity != opacity) {
      _opacity = opacity;
      Fade.setOpacity(widget, _opacity);      
    }
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.animation.client.Animation#onCancel()
   */
  @Override
  protected void onCancel() {
    if (completeOnCancel) {
      onComplete();
    }
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.animation.client.Animation#onComplete()
   */
  @Override
  protected void onComplete() {
    changeOpacity(to);
  }
  
  /* (non-Javadoc)
   * @see com.google.gwt.animation.client.Animation#onUpdate(double)
   */
  @Override
  protected void onUpdate(double progress) {
    double opacity = from + (to-from)*progress;
    if (Math.abs(opacity - _opacity) > threshold) {
      changeOpacity(opacity);
    }
  }
}
