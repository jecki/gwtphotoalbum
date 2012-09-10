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

//import java.lang.Iterable;
import java.util.HashMap;
//import java.util.Iterator;
//import java.util.NoSuchElementException;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

/**
 * Administers a collection of thumbnail images.
 * 
 * <p>The images are read upon instancing the class. Individual 
 * thumbnails can be
 * picked from the collection with the method <code>get</code>; the number of 
 * thumbnails within the collection can be queried via <code>size</code>; 
 * finally, the sizes of all thumbnails can be adjusted with the method 
 * <code>adjustSize</code>. The index of each thumbnail is stored in the
 * "id" field of the <code>\<img\></code>-Tag and can be queried via
 * the <code>indexOf</code>-method. The "id"-field of the thumbnail image should
 * should be considered as being "owned" by the <code>Thumbnail</code> object
 * and should not be overwritten.
 * 
 * @author ecki
 *
 */
public class Thumbnails {
  
//  private class Itr implements Iterator<Image> {
//    int cursor = 0;
//    
//    public boolean hasNext() {
//      return cursor < size(); 
//    }
//    
//    public Image next() {
//      if (hasNext()) {
//        return get(cursor++);
//      } else throw new NoSuchElementException();
//    }
//    
//    public void remove() {
//      assert false: "Thumbnail iterator does not allow removal of images!";
//    }
//  }
  
  private static String[] cache_thumbnailURLs;
  private static int[][]  cache_thumbnailSizes;
  private static ImageCollectionInfo cache_collection;
  
//  /**
//   * Determines the best ratio (width, height) for a given
//   * array or thumbnail sizes. Uses a "quick and dirty" algorithm, where 
//   * the "best" ratio more or less reflects the frequency of ratios in 
//   * this array.
//   *   
//   * @param thumbnailSizes the array of sizes of all thumbnails
//   * @return an array of two integers where the first is the width
//   *         of the "best" ratio and the second its "height"
//   */
//  private static int[] determineBestRatio(int[][] thumbnailSizes) {
//    int avW = 0, avH = 0;
//    int ret[] = new int[2];
//    for (int i = 0; i < thumbnailSizes.length; i++) {
//      avW += thumbnailSizes[i][0];
//      avH += thumbnailSizes[i][1];
//    }
//    avW /= thumbnailSizes.length;
//    avH /= thumbnailSizes.length;
//    ret[0] = thumbnailSizes[0][0];
//    ret[1] = thumbnailSizes[0][1];
//    int diff = Math.abs(avW - ret[0]) + Math.abs(avH - ret[1]);
//    for (int i = 1; i < thumbnailSizes.length; i++) {
//      int cmp = Math.abs(avW - thumbnailSizes[i][0]) + 
//          Math.abs(avH - thumbnailSizes[i][1]);
//      if (cmp < diff) {
//        ret[0] = thumbnailSizes[i][0];
//        ret[1] = thumbnailSizes[i][1];
//        diff = cmp;
//      }
//    }
//    return ret;
//  }
  
  private Image[] imageList;
  private int[][] thumbnailSizes;
  private int[][] actualSizes;
  // private int[]   bestRatio;
  
  /**
   * Reads the thumbnails from the given image collection. It is assumed
   * that the smallest version of each image in the collection is its
   * thumbnail.
   * 
   * @param collection  the image collection from which the thumbnails are
   *                    to be collected
   */
  public Thumbnails(ImageCollectionInfo collection) {
    String[] thumbnailURLs;
    int[][]  thumbnailSizes;
    
    if (collection == cache_collection) {
      thumbnailURLs = cache_thumbnailURLs;
      thumbnailSizes = cache_thumbnailSizes;
    } else {
      String   imageNames[] = collection.getImageNames();
      HashMap<String, int[][]> imageSizes = collection.getImageSizes();
      String   thumbnailDir = collection.getDirectories()[0];    
      thumbnailURLs = new String[imageNames.length];
      thumbnailSizes = new int[thumbnailURLs.length][2];
      for (int i = 0; i < imageNames.length; i++) {
        thumbnailURLs[i] = thumbnailDir + "/" + imageNames[i];
        thumbnailSizes[i] = imageSizes.get(imageNames[i])[0]; 
      }
      cache_collection = collection;
      cache_thumbnailURLs = thumbnailURLs;
      cache_thumbnailSizes = thumbnailSizes;
    }
    init(thumbnailURLs, thumbnailSizes);
  }
  
  /**
   * Collects the thumbnails the URLs of which are stored in the string array
   * <code>thumnailsURLs</code>.
   * 
   * @param thumbnailURLs  the URLs of the thumbnails to be collected
   * @param thumbnailSizes the respective sizes of the thumbnails
   */
  public Thumbnails(String[] thumbnailURLs, int[][] thumbnailSizes) {
    init(thumbnailURLs, thumbnailSizes);
  }
  
  /**
   * Resizes all thumbnail images so they have the height 
   * <code>edgeHeight</code> whilst keeping the aspect ratio.
   * 
   * @param edgeHeight the height to which the thumbnails shall
   * be resized.
   */
  public void adjustToHeight(int edgeHeight) {
    for (int i = 0; i < imageList.length; i++) {
      int tnW = thumbnailSizes[i][0];
      int tnH = thumbnailSizes[i][1];
      int w = tnW * edgeHeight / tnH;
      actualSizes[i][0] = w;
      actualSizes[i][1] = edgeHeight;
      imageList[i].setPixelSize(w, edgeHeight);        
    }
  }
  
  /**
   * Resizes the all thumbnails within the collection so that they fit in
   * a rectangle of the size (<code>edgeWidth, edgeHeight</code>. 
   * The aspect ratio is preserved.
   * 
   * @param edgeWidth  the (maximum) with of the thumbnails
   * @param edgeHeight the (maximum) height of the thumbnails
   */
  public void adjustToRectangle(int edgeWidth, int edgeHeight) {
    for (int i = 0; i < imageList.length; i++) {
      int tnW = thumbnailSizes[i][0];
      int tnH = thumbnailSizes[i][1];
      int w, h; 
      if (tnW == 0) {
        w = 0;
        h = edgeHeight;
      } else if (tnH == 0) {
        w = edgeWidth;
        h = 0;
      } else {
        w = edgeWidth;
        h = tnH * edgeWidth / tnW;
        if (h > edgeHeight) {
          h = edgeHeight;
          w = tnW * edgeHeight / tnH;
        } 
      }
      actualSizes[i][0] = w;
      actualSizes[i][1] = h;
      imageList[i].setPixelSize(w, h);      
    }
  }
  
//  /**
//   * Returns the "best" height for adjusting the thumbnail sizes assuming that
//   * the width is fixed. 
//   * @param edgeWidth the (fixed) width, the thumbnails shall be adjusted to 
//   * @return the associated height for the "best" width/height ratio
//   */
//  public int bestHeight(int edgeWidth) {
//    return ((bestRatio[1]*edgeWidth) / bestRatio[0]);
//  }  
//  
//  /**
//   * Returns the "best" width for adjusting the thumbnail sizes assuming that
//   * the height is fixed. 
//   * @param edgeHeight the (fixed) height, the thumbnails shall be adjusted to 
//   * @return the associated width for the "best" width/height ratio
//   */
//  public int bestWidth(int edgeHeight) {
//    return ((bestRatio[0]*edgeHeight) / bestRatio[1]);
//  }
  
  /**
   * Get the thumbnail image with the index <code>i</code>
   * @param index the index of the image to be collected
   * @return the image corresponding to the given index
   */
  public Image get(int index) {
    assert index >= 0 && index < size();
    return imageList[index];
  }
  
  /**
   * Returns the actual size of image <code>img</code>. This is the size
   * after <code>adjustSize</code> has been called.
   * @param index the index of the image for which the size shall be determined
   * @return the actual size of the image
   */
  public int[] imageSize(int index) {
    assert index >= 0 && index < size();
    return actualSizes[index];
  }
  
//  public Iterator<Image> iterator() {
//    return new Itr();
//  }
  
  /**
   * Returns the index of the given thumbnail image. (Indices are stored
   * as string representation in the "id" field of the thumbnail.)
   * @return the index of the thumbnail
   */
  public int indexOf(Image img) {
    String id = DOM.getElementAttribute(img.getElement(), "id");
    int index = Integer.parseInt(id);
    assert index >= 0 && index < size() : 
         "Image is not a thumbnail or \"id\" was manipulated!";
    return index;
  }
  
  public void setHooverStyle(String style) {
    
  }
  
  /**
   * Returns the number ot thumbnails within the collection.
   * @return the number of thumbnails
   */
  public int size() {
    return imageList.length;
  }
  
  
  private void init(String[] thumbnailURLs, int[][] thumbnailSizes) {
    assert thumbnailURLs.length == thumbnailSizes.length;    
    this.thumbnailSizes = thumbnailSizes;
    imageList = new Image[thumbnailURLs.length]; 
    actualSizes = new int[thumbnailURLs.length][2];
    for (int i = 0; i < thumbnailURLs.length; i++) {
      imageList[i] = new Image(thumbnailURLs[i]);
      Element imgElement = imageList[i].getElement();
      DOM.setElementAttribute(imgElement, "id", String.valueOf(i));        
      actualSizes[i][0] = thumbnailSizes[i][0];
      actualSizes[i][1] = thumbnailSizes[i][1];     
    }
    // bestRatio = determineBestRatio(thumbnailSizes);
  }
  
}
