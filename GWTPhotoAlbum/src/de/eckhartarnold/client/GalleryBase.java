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

import java.util.ArrayList;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;

/**
 * @author eckhart
 *
 */
public abstract class GalleryBase extends Composite implements ResizeListener {
  /** The token of the gallery page for the browser history */
  static final String GALLERY_TOKEN = "Gallery"; 
  
  private ArrayList<GalleryListener> listenerList = 
    new ArrayList<GalleryListener>();
    
  /**
   * Add a gallery listener.
   * @param listener  the gallery listener to be added
   */
  public void addGalleryListener(GalleryListener listener) {
    listenerList.add(listener);
  }

  /**
   * Fires a start slide show event.
   */
  protected void fireStartSlideshow() {
    for (GalleryListener listener: listenerList) 
      listener.onStartSlideshow();    
  }

  /**
   * fires a pick image event.
   * @param imageNr  the number of the image that was "picked" by the user
   */
  protected void firePickImage(int imageNr) {
    for (GalleryListener listener: listenerList) 
      listener.onPickImage(imageNr);
  }  
  
  /* (non-Javadoc)
   * @see com.google.gwt.user.client.ui.Composite#onAttach()
   */
  @Override
  protected void onAttach() {
    History.newItem(GALLERY_TOKEN, false);
    super.onAttach();
  }  
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#onResized()
   */
  @Override
  public abstract void onResized();

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener#prepareResized()
   */
  @Override
  public abstract void prepareResized();

  /**
   * Removes a gallery listener.
   * @param listener  the gallery listener to be removed
   */
  public void removeGalleryListener(GalleryListener listener) {
    listenerList.remove(listener);
  }
}
