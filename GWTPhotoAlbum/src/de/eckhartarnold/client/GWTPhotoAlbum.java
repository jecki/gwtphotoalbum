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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
//import com.google.gwt.i18n.client.LocaleInfo;
//import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.HasHorizontalAlignment;
//import com.google.gwt.user.client.ui.HasVerticalAlignment;
//import com.google.gwt.user.client.ui.HorizontalPanel;
//import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
//TODO: Make use of CssResources: http://code.google.com/intl/de-DE/webtoolkit/doc/latest/DevGuideClientBundle.html#CssResource
/**
 * Main class of GWTPhotoAlbum and entry point
 * for the online photo album.
 * 
 * <p><code>GWTPhotoAlbum</code> instantiates an 
 * <code>ImageCollectionReader</code> that reads the
 * .json files which describe the photo album from the sub directory 
 * <code>IMAGE_COLLECTION_DIR</code> (usually named "slides") and then
 * builds up the widgets according to the description in the JSON files.
 * 
 * @see ImageCollectionReader
 */
public class GWTPhotoAlbum implements EntryPoint {
  /* static {
    if (!Resources.INSTANCE.css().ensureInjected()) {
      Debugger.print("Couldn't inject css stylesheet");
    }
  }*/  
  /** 
   * name of the sub directory where the .json files describing the photo
   * album reside.
   * @see ImageCollectionReader
   */
  public static final String  IMAGE_COLLECTION_DIR = "slides";
  
  /** name of the key in the info dictionary for the layout type */
  public static final String  KEY_LAYOUT_TYPE = "layout type";
  /** name of the key in the info dictionary for the layout data */ 
  public static final String  KEY_LAYOUT_DATA = "layout data";
  /** name of the key in the info dictionary for the presentation type */   
  public static final String  KEY_PRESENTATION_TYPE = "presentation type";
  /** 
   * name of the key in the info dictionary that indicates 
   * whether an optimized layout for small screen or window 
   * sizes (like on mobile devices) shall be added.
   */
  public static final String  KEY_ADD_MOBILE_LAYOUT = "add mobile layout";
  
  /** entry name for the full screen layout */
  public static final String  LAYOUT_FULLSCREEN = "fullscreen";
  /** entry name for the tiled layout */
  public static final String  LAYOUT_TILED      = "tiled";
  /** entry name for the html layout */
  public static final String  LAYOUT_HTML       = "html";
  
  /** entry name for a gallery presentation */
  public static final String  PRESENTATION_GALLERY   = "gallery";
  /** entry name for a slide show presentation */
  public static final String  PRESENTATION_SLIDESHOW = "slideshow";   
  
  /** reference to the root panel */
  private RootPanel           root;
//  /** the image collection info object for this photo album */
//  private ImageCollectionInfo info;
  /** the layout for the slide show part of this photo album */
  private Layout              layout;
  /** the gallery part of this photo album if present */
  private GalleryBase         gallery;
  /** the presentation of this photo album */
  private Presentation        presentation;
//  /** reference to the presentation object for this photo album, e.g. 
//   *  a <code>GallerPresentation</code> object or
//   *  a <code>SlideshowPresentation</code> object */
//  private Object              presentation; 
  
	/**
   * This is the entry point method. (See the class description.)
   */
	public void onModuleLoad() {  
	  StatusTag.setHTML("initializing...");
    root = RootPanel.get();
    
		new ImageCollectionReader(GWT.getHostPageBaseURL() + IMAGE_COLLECTION_DIR, 
		    new ImageCollectionReader.ICallback() {
		  public void callback(ImageCollectionReader src) {
		    
		    // create layout
		    String layoutType = src.getInfo().get(KEY_LAYOUT_TYPE);
		    String layoutData = src.getInfo().get(KEY_LAYOUT_DATA);

		    if (layoutType == null 
		        || layoutType.equalsIgnoreCase(LAYOUT_FULLSCREEN)) {
		      if (layoutData != null) {
		        layout = new FullScreenLayout(src, layoutData);
		      } else {
		        layout = new FullScreenLayout(src);
		      }
		    } else if (layoutType.equalsIgnoreCase(LAYOUT_TILED)) {
          if (layoutData != null) {
            layout = new TiledLayout(src, layoutData);
          } else {
            layout = new TiledLayout(src);
          }		        
		    } else if (layoutType.equalsIgnoreCase(LAYOUT_HTML)) {
          if (layoutData != null) {
            layout = new HTMLLayout(src, layoutData);
          } else {
            layout = new HTMLLayout(src);
          }		      
		    } else {
		      ImageCollectionReader.ERROR_DIALOG.message("Illegal layout type: " + 
		          layoutType);
		      return;
		    }

		    StatusTag.remove();
		    // create presentation
		    String presentationType = src.getInfo().get(KEY_PRESENTATION_TYPE);
		    if (presentationType == null || 
		        presentationType.equalsIgnoreCase(PRESENTATION_GALLERY)) {
	        gallery = new Gallery(src);
	        presentation = new GalleryPresentation(root, gallery, layout);		      
		    } else if (presentationType.equalsIgnoreCase(PRESENTATION_SLIDESHOW)) {
		      presentation = new SlideshowPresentation(root, layout);   
		    } else {
          ImageCollectionReader.ERROR_DIALOG.message("Illegal presentation " +
              "type: " + layoutType);
		    }
		    if (src.getInfo().get(KEY_ADD_MOBILE_LAYOUT) != "false" && 
		        presentation != null) {
		      Layout mobileLayout = new FullScreenLayout(src);
		      presentation.setMobileLayout(mobileLayout);
		      if (presentation instanceof GalleryPresentation) {
		        GalleryPresentation gp = (GalleryPresentation)presentation;
		        mobileLayout.getControlPanel().setHomeButtonListener(gp);
		      }
		    }
		  }
		}, ImageCollectionReader.ERROR_DIALOG);
  }
}
