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

/**
 * Interface for event listeners of class <code>Slideshow</code>.
 * 
 * @author eckhart
 *
 */
public interface SlideshowListener {
  /**
   * Called when a new image starts fading in. The number of the image
   * that is faded in can be queried via <code>Slideshow.getCurrentSlide</code> 
   * <code>onFade()</code> will not be called if fading is disabled or 
   * skipped for some other reason! 
   */
  void onFade();

  /**
   * Called whenever a new slide is displayed. <code>onShow</code>
   * is only called after fading in has finished and the slide is 
   * fully displayed.
   * @param slideNr the number of the slide that is (fully) displayed
   *                right now.
   */
  void onShow(int slideNr);  
  
  /**
   * Called when the (automatic) slide show is started.
   */
  void onStart();
  
  /**
   * Called when the (automatic) slide show is stopped.
   */
  void onStop();
}
