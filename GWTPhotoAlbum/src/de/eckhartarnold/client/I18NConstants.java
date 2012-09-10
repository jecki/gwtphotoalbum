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

import com.google.gwt.i18n.client.Constants;

/**
 * Class <code>I18NConstants</code> and defines some string constants 
 * for the user interface that
 * ought to be language dependent.
 * 
 * @see Constants
 * @author ecki
 *
 */
public interface I18NConstants extends Constants {

  @DefaultStringValue("Run Slideshow")
  String runSlideshow();
  
  @DefaultStringValue("Previous Picture")
  String back();

  @DefaultStringValue("First Picture")
  String begin();  
  
  @DefaultStringValue("Last Picture")
  String end();    
  
  @DefaultStringValue("Back to start")  
  String home();
  
  @DefaultStringValue("Play / Pause")  
  String playPause();
  
  @DefaultStringValue("Next Picture")  
  String next();
}  
