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

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

/**
 * A simpler version of class {@link GalleryPresentation} which only "presents"
 * the slide show an omits the gallery page.
 * 
 * @author ecki
 *
 */
public class SlideshowPresentation implements ResizeHandler {
  protected Layout layout;
  protected Panel  parent;
  
  public SlideshowPresentation(Panel parent, Layout layout) {
    this.layout = layout;
    this.parent = parent;
    ControlPanel ctrl = layout.getControlPanel();
    if (ctrl != null) {
      ctrl.setButtonShow(ControlPanel.BEGIN|ControlPanel.BACK|ControlPanel.PLAY
                         |ControlPanel.NEXT|ControlPanel.END);
    }      
    parent.add(layout.getRootWidget());
    layout.issueResize();   
    Window.addResizeHandler(this);
    History.addValueChangeHandler(layout.getSlideshow());
    int imageNr = Slideshow.parseHistoryToken(History.getToken());
    if (imageNr >= 0) {
      layout.getSlideshow().showImmediately(imageNr);
    } else {
      layout.getSlideshow().showImmediately(0);
      layout.getSlideshow().start();
    }
  }
  
//  public Layout getLayout() {
//    return layout;
//  }  
  
  public void onResize(ResizeEvent event) {
    layout.issueResize();
  }   
}
