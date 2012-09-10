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

import java.util.HashMap;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * The interface <code>ImageCollectionInfo</code> is for querying the 
 * configuration data of the photo album.
 * 
 * <p><code>ImageCollectionInfo</code> defines several methods
 * that all together return all the configuration data for an image collection 
 * that is needed for the photo album, such as configuration options, image 
 * locations, file names, resolutions and captions.
 * 
 * @author eckhart
 *
 */
public interface ImageCollectionInfo {
  
  /**
   * Returns the array of caption string for the images. The order
   * of the array corresponds to that of the images, i.e. the
   * first entry of the caption array contains the caption of the
   * first image etc. If an image does not have a caption the array
   * contains an empty String.
   * 
   * @return  the array of caption strings
   */
  SafeHtml[] getCaptions();

  /**
   * Returns a list of directory names. Each directory is to contain
   * all the images scaled to a specific size class. The directory
   * names contain the "full" path starting with the base URL that
   * is passed to the <code>ImageCollectionInfo</code> constructor.
   *  
   * @return  the directory names
   */
  String[] getDirectories();

  /**
   * Returns the list of image filenames. The filenames do not contain
   * any path information. A file with the same name, e.g. 
   * "beautiful_image.jpg" should be contained in every (sub-)directory for
   * different image sizes.
   *  
   * @return the list of image filenames.
   */
  String[] getImageNames();

  /**
   * Returns a dictionary that maps the image file names to their exact sizes.
   * The sizes are pairs of integer values {x,y} and the length and order of
   * the sizes tuple should correspond to the list of directories.
   * 
   * @return the exact sizes of every image.
   */
  HashMap<String, int[][]> getImageSizes();
  
  
  /**
   * Returns a dictionary of information strings. The dictionary may
   * (but does not have to) contain such entries as "title", "subtitle"
   * "bottom line" and any other arbitrary entry. The returned dictionary
   * may be empty.
   * 
   * {@link Gallery} objects use the information that is provided under the 
   * keys: "title", "subtitle", "bottom line".
   * 
   * @return a dictionary with (arbitrary) information on the image collection
   */
  HashMap<String, String> getInfo();
  
  /**
   * Returns true, if at least one image has a caption that is not empty.
   * 
   * @return true, if there are captions
   */
  boolean hasCaptions();
}

