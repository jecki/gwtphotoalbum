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
import java.util.ArrayList;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;


/**
   * Reads the information about the image collection from a number of 
   * .json files. 
   * 
   * <p>When the information is ready it can be queried with the 
   * <code>getCaptions</code>, <code>getDirectories</code>, 
   * <code>getImageNames</code>, <code>getImageSizes</code>, 
   * <code>getInfo</code> methods. The information on the 
   * image collection must contained in five files with the following hard 
   * coded names:
   * <p><ol>
   * <li>"directories.json" - a list of directories
   * <li>"filenames.json"   - a list of image file names
   * <li>"captions.json"    - a dictionary that associates the image file names 
   *                          with the captions of the images
   * <li>"resolutions.json" - Either a list with two dictionaries, where the
   *                          defines a certain number "resolution sets" (i.e.
   *                          lists of image sizes that are associated with a
   *                          name, e.g. "landscape" or "portrait") and
   *                          the second associates each image name with the
   *                          key of a resolution set <em>or</em>
   *                          a dictionary that associates the image file names
   *                          directly with list of image sizes, each size 
   *                          entry corresponding to one directory and 
   *                          consisting itself of a two entry list containing 
   *                          the width and height of the image in the 
   *                          respective directory.
   * <li>"info.json"        - a dictionary with arbitrary additional
   *                          information on the image collection. It should
   *                          (but need not) contain at least the fields: 
   *                          "title", "subtitle" and "bottom line"
   * </ol>
 * 
 * @author ecki
 *
 */
public class ImageCollectionReader implements ImageCollectionInfo {
  /**
   * This interface defines a call back function that is called with the
   * <code>ImageCollectionReader</code> object as parameter. This is 
   * used for calling back into the main program by the time all the
   * .json files defining the photo album have been read and parsed.
   * 
   */
  public interface ICallback {
    /**
     * A call back function called 
     * from the <code>ImageCollectionReader</code> object 
     * @param src  the caller object of the call back 
     */
    void callback(ImageCollectionReader src);
  }
  
  /**
   * This interface defines a call back that receives a string message.
   * It is used for transmitting error messages to clients of class 
   * <code>ImageCollectionReader</code>
   */
  public interface IMessage {
    /**
     * A call back function that receives a string message as parameter.
     * @param msg  the message
     */
    void message(String msg);
  }
  
  private interface JSONDelegate {
    void process(JSONValue json);
  }  

  /**
   * This is a helper class that allows displaying an error message in
   * a dialog so that the user can be notified of any error that occurred
   * during reading the configuration data from the .json files.
   * It is in the discretion of the client of class 
   * <code>ImageCollectionReader</code> whether the user is notified or not,
   * for which purpose, however, this class may be helpful.  
   */
  public static class MessageDialog implements IMessage {
    private DialogBox dialog;
    
    /**
     * Displays a message in a pop up dialog.
     * 
     * @param msg  the message to be displayed.
     */
    public void message(String msg) {
      dialog = new DialogBox(true);
      dialog.setText("Error!");      
      dialog.addStyleName("debugger");
      VerticalPanel panel = new VerticalPanel();
      panel.setSpacing(4);
      panel.add(new HTML(msg));
      Button button = new Button("close", new ClickHandler() {
        public void onClick(ClickEvent event) {
          dialog.hide();
          dialog = null;
        }
      });
      panel.add(button);
      panel.setCellHorizontalAlignment(button, 
          HasHorizontalAlignment.ALIGN_CENTER);      
      dialog.setWidget(panel);
      dialog.center();
      dialog.show();
    }
  }
  
  
  private class JSONReceiver implements RequestCallback {
    String       url;
    JSONDelegate task;
    IMessage     errorReporting;
    
    JSONReceiver(String url, JSONDelegate task, IMessage error) {
      this.url = url;
      this.task = task;
      this.errorReporting = error;
    }
    
    /**
     * Checks whether the JSON data is stored 
     * in a (hidden) tag, the id of which must be the file name,
     * e.g. <div id="resolutions.json" style="display:none">JSON</div>
     * @return true, if successful
     */
    public boolean extractJSONfromHTML() {
      JSONValue jsonValue;
      String tagId = url.substring(url.lastIndexOf('/')+1);
      Element dataTag = Document.get().getElementById(tagId);        
      if (dataTag != null) {
        jsonValue = JSONParser.parseStrict(dataTag.getInnerHTML());
        task.process(jsonValue);
        return true;
      } else {
        String location = Window.Location.getHref();
        if (!location.contains("GWTPhotoAlbum_fatxs.html")) {
          String target = "GWTPhotoAlbum_fatxs.html";
          int i = location.lastIndexOf("#");
          if (i >= 0) {
            target += location.substring(i);
          }
          redirect(GWT.getHostPageBaseURL()+target);
        }
        return false;
      }    
    }    
    
    /* (non-Javadoc)
     * @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable)
     */
    public void onError(Request request, Throwable exception) {
//      errorReporting.message("Couldn't retrieve JSON: " + url +
//          "<br />" + exception.getMessage());           
    }

    /* (non-Javadoc)
     * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request, com.google.gwt.http.client.Response)
     */
    public void onResponseReceived(Request request, Response response) {
      JSONValue jsonValue;
      int statusCode = response.getStatusCode();
      
      try {
        if (statusCode == Response.SC_OK) { // SC_OK == 200 !?
          jsonValue = JSONParser.parseStrict(response.getText());
          task.process(jsonValue);
          GWT.log("JSON read: "+url);      
        } else {
          // if no file is found, check whether the JSON data is stored
          // in a (hidden) tag of the html master file. 
          if (!extractJSONfromHTML()) {
            errorReporting.message("Couldn't retrieve JSON from HTML: " + url + 
                "<br /> after previous error " + statusCode + ": " + 
                response.getStatusText());            
          }
          GWT.log("JSON extracted from html: " + url.substring(url.lastIndexOf('/')+1));
        }
      } catch (JSONException e) {
        errorReporting.message("Could not parse JSON: " + url + 
            "<br />" + e.getMessage());
      }  
    }
    
  }
 
  /**
   * The name of the html meta-tag that contains an alternative file name 
   * for file "info.json". This meta-tag allows to switch the info.json,
   * file that control the appearance of the photo album, from within the
   * the html page that contains the photo album script.  
   */
  public static final String METANAME_INFO = "info";
  
  /** An instance of the inner <code>MessageDialog</code> class. */
  public static final IMessage ERROR_DIALOG = new MessageDialog();
  /** An instanca of interface <code>IMessage</code> that silently 
   * passes over the message */
  public static final IMessage ERROR_SILENT = new IMessage() {
    public void message(String msg) { }
  };
  
  
  private static native void redirect(String url)/*-{
  $wnd.location = url;
}-*/;   
  
  
  private SafeHtml[] captions;
  private HashMap<String, String> captionDictionary;
  private String[] directories;
  private boolean finished = false;  
  private String[] imageNames;
  private HashMap<String, int[][]> imageSizes;
  private HashMap<String, String> info;
  private String infoFileName;
  
  /**
   * Reads the information about the image collection from several json files 
   * contained in the <code>baseURL</code> directory.
   * When the json files have finished loading <code>readyReport</code> is 
   * issued. If any error occurs it is reported via <code>errorReport</code>.
   * For error reporting the class <code>ImageCollectionInfo</code> offers
   * two stock objects: <code>ERROR_DIALOG</code> and <code>ERROR_SILET</code>,
   * the first of which reports the error in a pop up dialog, while the other
   * simply ignores the error (dangerous!).  
   * 
   * @param baseURL      the base URL of the image collection
   * @param readyReport  the callback that is to be issued when loading the
   *                     information about the image collection is ready. 
   * @param errorReport  the callback for reporting errors.
   */
  public ImageCollectionReader(String baseURL, ICallback readyReport, 
      IMessage errorReport) {
    // Element info = DOM.getElementById("info");
    NodeList<Element> metaTags = Document.get().getElementsByTagName("meta");
    this.infoFileName = "info.json";    
    int length = metaTags.getLength();
    for (int i = 0; i < length; i++) {
      Element item = metaTags.getItem(i);
      if (item.getAttribute("name").equalsIgnoreCase(METANAME_INFO)) {
        this.infoFileName = item.getAttribute("content");
        break;
      }
    } 
    retrieveSequentially(baseURL, readyReport, errorReport);      
  } 
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ImageCollectionInterface#getCaptions()
   */
  public SafeHtml[] getCaptions() {
    assert captions != null : "captions not loaded yet!";
    return captions;
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ImageCollectionInterface#getDirectories()
   */
  public String[] getDirectories() {
    assert directories != null : "directory information not loaded yet!";
    return directories;
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ImageCollectionInterface#getImageNames()
   */
  public String[] getImageNames() {
    assert imageNames != null: "image names not loaded yet!";
    return imageNames;    
  }
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ImageCollectionInterface#getImageSizes()
   */
  public HashMap<String, int[][]> getImageSizes() {
    assert imageSizes != null: "information about image sizes not loaded yet!";
    return imageSizes;
  }

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ImageCollectionInterface#getInfo()
   */
  public HashMap<String, String> getInfo() {
    if (info == null) GWT.log("Info not available!!!");
    assert info != null: "information on the image collection not yet loaed!";
    return info;
  }
  
  public boolean hasCaptions() {
    return captionDictionary.isEmpty();
  }
  
  /**
   * Returns true, if loading the information about the image collection has
   * finished and it is ready to be queried.
   * 
   * @return true, if the information about the image collection is ready
   */
  public boolean isReady() {
    if (captionDictionary != null && directories != null 
        && imageNames != null && imageSizes != null && info != null) {
      if (captions == null) {
        captions = new SafeHtml[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
          if (captionDictionary.containsKey(imageNames[i]))
            captions[i] = ExtendedHtmlSanitizer.sanitizeHTML(
                captionDictionary.get(imageNames[i]));            
          else
            captions[i] = new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml("");
        }
      }
      return true;
    } else return false;
  }
  
  private HashMap<String, int[][]> interpretSizes(JSONValue json) 
      throws JSONException {
    HashMap<String, int[][]> resolutions = new HashMap<String, int[][]>();    
    HashMap<String, int[][]> sizes = new HashMap<String, int[][]>();
    JSONObject dict = json.isObject();
    JSONArray array = json.isArray();    
 
    if (array != null) {
      JSONObject resDict = array.get(0).isObject();
      for (String key: resDict.keySet()) {
        JSONArray resSet = resDict.get(key).isArray();
        resolutions.put(key, interpretResolutionsArray(resSet));
      }
      dict = array.get(1).isObject();
    } 
    
    for (String key: dict.keySet()) {
//      JSONArray list = dict.get(key).isArray();
//      resolutions.clear();
//      for (int i = 0; i < list.size(); i++ ) {
//        JSONArray xy = list.get(i).isArray();
//        int res[] = new int[2];
//        res[0] = (int) xy.get(0).isNumber().doubleValue();
//        res[1] = (int) xy.get(1).isNumber().doubleValue();
//        resolutions.add(res);
//      }
//      sizes.put(key, resolutions.toArray(new int[resolutions.size()][]));
      JSONValue value = dict.get(key);
      array = value.isArray();
      if (array != null) {
        sizes.put(key, interpretResolutionsArray(array));
      } else {
        sizes.put(key, resolutions.get(value.isString().stringValue()));
      }
    }  
    return sizes;
  }
    
  private String[] interpretStringArray(JSONValue json)
      throws JSONException {
    JSONArray array = json.isArray();
    ArrayList<String> stringList = new ArrayList<String>();
    for (int i = 0; i < array.size(); i++) {
      stringList.add(array.get(i).isString().stringValue());
    }
    return stringList.toArray(new String[stringList.size()]);
  }
  
  private HashMap<String, String> interpretStringDictionary(JSONValue json) 
      throws JSONException {
    JSONObject dict = json.isObject();
    HashMap<String, String> stringDict = new HashMap<String, String>();    
    for (String key: dict.keySet()) {
      stringDict.put(key, dict.get(key).isString().stringValue());
    }
    return stringDict;
  }
  
  private int[][] interpretResolutionsArray(JSONArray array) {
    ArrayList<int[]> resolutions = new ArrayList<int[]>();
    for (int i = 0; i < array.size(); i++ ) {
      JSONArray xy = array.get(i).isArray();
      int res[] = new int[2];
      res[0] = (int) xy.get(0).isNumber().doubleValue();
      res[1] = (int) xy.get(1).isNumber().doubleValue();
      resolutions.add(res);
    }
    return resolutions.toArray(new int[resolutions.size()][]);    
  }
  
  private void readJSON(String url, JSONDelegate task, IMessage error) {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
    JSONReceiver receiver = new JSONReceiver(url, task, error);
    try {
      builder.sendRequest(null, receiver);
    } catch (RequestException e) {
      if (!receiver.extractJSONfromHTML()) {
        error.message("Couldn't retrieve JSON: " + url + 
            "<br />" + e.getMessage());
      }
    }    
  }  
  
  private void retrieveSequentially(String baseURL, ICallback readyReport, 
      IMessage errorReport) {
    final ICallback ready = readyReport;
    final IMessage error = errorReport;
    final ImageCollectionReader src = this;
    final String url = baseURL;
    
    if (directories == null) {
      readJSON(baseURL+"/directories.json", new JSONDelegate() {
        public void process(JSONValue json) {
          directories = interpretStringArray(json);
          for (int i = 0; i < directories.length; i++) 
            directories[i] = url + "/" + directories[i];
          if (isReady() && !finished) {
            finished = true;
            ready.callback(src);
          }
          else retrieveSequentially(url, ready, error);
        }
      }, errorReport);
      
    } else if (imageNames == null) {
      readJSON(baseURL+"/filenames.json", new JSONDelegate() {
        public void process(JSONValue json) {
          imageNames = interpretStringArray(json);
          if (isReady() && !finished) {
            finished = true;
            ready.callback(src);
          }
          else retrieveSequentially(url, ready, error);
        }
      }, errorReport);    
    
    } else if (captionDictionary == null) {
      readJSON(baseURL+"/captions.json", new JSONDelegate() {
        public void process(JSONValue json) {
          captionDictionary = interpretStringDictionary(json);
          if (isReady() && !finished) {
            finished = true;
            ready.callback(src);
          }
          else retrieveSequentially(url, ready, error);
        }
      }, errorReport);
    
    } else if (imageSizes == null) {
      readJSON(baseURL+"/resolutions.json", new JSONDelegate() {
        public void process(JSONValue json) {        
          imageSizes = interpretSizes(json);
          if (isReady() && !finished) {
            finished = true;
            ready.callback(src);
          }
          else retrieveSequentially(url, ready, error);          
        }
      }, errorReport);
      
    } else if (info == null) {
      readJSON(baseURL+"/" + infoFileName, new JSONDelegate() {
        public void process(JSONValue json) {
          GWT.log(json.toString());          
          info = interpretStringDictionary(json);
          if (isReady() && !finished) {
            finished = true;
            ready.callback(src);
          }
          else retrieveSequentially(url, ready, error);          
        }
      }, errorReport);
    }
  }
  
//  private HashMap<String, SafeHtml> sanitzeDict(HashMap<String, String> dict) {
//    HashMap<String, SafeHtml> sanitized = new HashMap<String, SafeHtml>();
//    for (String key: dict.keySet()) {
//      sanitized.put(key, ExtendedHtmlSanitizer.sanitizeHTML(dict.get(key)));
//    }
//    return sanitized;
//  }
}
