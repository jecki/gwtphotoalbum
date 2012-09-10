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
 * Handler for pick image events that are emitted by gallery objects.
 * 
 * @author eckhart
 */
public interface PickImageHandler {
  /**
   * <code>onPickImage</code> is called when the user clicks on an image
   * on the photo gallery.
   * 
   * @param imageNr  the number of the image, the user has clicked on.
   *                 The image numbers always start with 0.
   */
  void onPickImage(int imageNr);
}
