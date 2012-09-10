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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.History;

/**
 * A simpler version of class {@link GalleryPresentation} which only "presents"
 * the slide show an omits the gallery page.
 * 
 * @author ecki
 *
 */
public class SlideshowPresentation extends Presentation implements ClickHandler { 
  
  public SlideshowPresentation(Panel parent, Layout layout) {
    super(parent, layout);
    ControlPanel ctrl = layout.getControlPanel();
    if (ctrl != null) {
      ctrl.setButtonShow(ControlPanel.BACK|ControlPanel.HOME|ControlPanel.PLAY
                         |ControlPanel.NEXT);
    }
    activateSlideshow();
    int imageNr = Presentation.parseSlideToken(History.getToken());
    if (imageNr >= 0) {
      layout.getSlideshow().showImmediately(imageNr);
    } else {
      layout.getSlideshow().showImmediately(0);
      layout.getSlideshow().start();
    }
    ctrl.setHomeButtonListener(this);    
  }

  /**
   * Click handler for the HOME-Button of the control panel. Jumps back to the
   * first picture. (In the galler presentation it jumps to the gallery instead.
   * @see GalleryPresentation)
   * 
   * @param event   a click event This may also be <code>null</code>
   *                if the caller is not a widget object.
   */
  public void onClick(ClickEvent event) {
    Slideshow slideshow = layout.getSlideshow();
    slideshow.stop();    
    slideshow.show(0);
  }
}
