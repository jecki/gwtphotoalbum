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

import com.google.gwt.core.client.GWT;

/**
 * Contains an instance of class <code>I18NConstans</code> with the
 * translations of all string constants that GWTPhotoAlbum uses.
 * 
 * <p><em>Presently internationalization does not work due because language
 * detection of the client is still missing. (Any volunteers?)</em>
 * 
 * @see I18NConstants
 * @author ecki
 *
 */
public class I18N {
  /** Instance of the class containing the string constants used in 
   * the user interaction of GWTPhotoAlbum */
  public static final I18NConstants i18n = GWT.create(I18NConstants.class);;
}
