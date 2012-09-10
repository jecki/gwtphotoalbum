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
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author ecki
 *
 */
public class ImageCollectionReader implements ImageCollectionInfo {
  public interface ICallback {
    void callback(ImageCollectionReader src);
  }
  
  public interface IMessage {
    void message(String msg);
  }
  
  protected interface JSONDelegate {
    void process(JSONValue json);
  }  

  
  public static class MessageDialog implements IMessage {
    protected DialogBox dialog;
    
    public void message(String msg) {
      dialog = new DialogBox(true);
      VerticalPanel panel = new VerticalPanel();
      dialog.setHTML("Error!");
      panel.setSpacing(4);
      panel.add(new HTML(msg));
      Button button = new Button("close", new ClickListener() {
        public void onClick(Widget widget) {
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
  
  
  protected class JSONReceiver implements RequestCallback {
    String       url;
    JSONDelegate task;
    IMessage     errorReporting;
    
    public JSONReceiver(String url, JSONDelegate task, IMessage error) {
      this.url = url;
      this.task = task;
      this.errorReporting = error;
    }
    
    public void onError(Request request, Throwable exception) {
      errorReporting.message("Couldn't retrieve JSON: " + url +
          "<br />" + exception.getMessage());           
    }

    public void onResponseReceived(Request request, Response response) {
      if (200 == response.getStatusCode()) {
        try {
          JSONValue jsonValue = JSONParser.parse(response.getText());
          task.process(jsonValue);
        } catch (JSONException e) {
          errorReporting.message("Could not parse JSON: " + url + 
              "<br />" + e.getMessage());
        }
      } else {
        errorReporting.message("Couldn't retrieve JSON: " + url + 
            "<br />" + response.getStatusText());
      }     
    }
  }

  
  public static final IMessage ERROR_DIALOG = new MessageDialog();
  public static final IMessage ERROR_SILENT = new IMessage() {
    public void message(String msg) { }
  };
  
  protected String[] captions;
  protected String[] directories;
  protected String[] imageNames;
  protected HashMap<String, int[][]> imageSizes;
  
  private HashMap<String, String> captionDictionary;
  
  /**
   * <p>Reads the information about the image collection from json files 
   * contained in the baseURL directory. When the information is
   * ready it can be queried with the {@link getCaptions}, 
   * {@link getDirectories}, {@link getImageNames}, {@link getImageSizes}
   * methods. The information on the image collection must contained in four
   * files with the following hard coded names:
   * <p><ol>
   * <li>"directories.json" - a list of directories
   * <li>"filenames.json" - a list of image file names
   * <li>"captions.json"  - a dictionary that associates the image file names 
   *                        with the captions of the images
   * <li>"resolutions.json" - a dictionary that associates the image file names
   *                          with list of image sizes, each size entry 
   *                          corresponding to one directory and consisting 
   *                          itself of a two entry list containing the width
   *                          and height of the image in the respective 
   *                          directory.
   * </ol>
   * When the json files have finished loading <code>readyReport</code> is 
   * issued. If any error occurs it is reported via <code>errorReport</code>.
   * For error reporting the class <code>ImageCollectionInfo</code> offers
   * two stock objects: <code>ERROR_DIALOG</code> and <code>ERROR_SILET</code>,
   * the first of which reports the error in a pop up dialog, while the other
   * simply igonres the error (dangerous!).  
   * 
   * @param baseURL     the base URL of the image collection
   * @param readyReport the callback that is to be issued when loading the
   *                    information about the image collection is ready. 
   * @param errorReport the callback for reporting errors.
   */
  public ImageCollectionReader(String baseURL, ICallback readyReport, 
      IMessage errorReport) {
    final ICallback ready = readyReport;
    final ImageCollectionReader src = this;
    final String url = baseURL;
    
    readJSON(baseURL+"/directories.json", new JSONDelegate() {
      public void process(JSONValue json) {
        directories = interpretStringArray(json);
        for (int i = 0; i < directories.length; i++) 
          directories[i] = url + "/" + directories[i];
        if (isReady()) ready.callback(src);
      }
    }, errorReport);
    
    readJSON(baseURL+"/filenames.json", new JSONDelegate() {
      public void process(JSONValue json) {
        imageNames = interpretStringArray(json);
        if (isReady()) ready.callback(src);
      }
    }, errorReport);    

    readJSON(baseURL+"/captions.json", new JSONDelegate() {
      public void process(JSONValue json) {
        captionDictionary = interpretStringDictionary(json);
        if (isReady()) ready.callback(src);
      }
    }, errorReport);       

    readJSON(baseURL+"/resolutions.json", new JSONDelegate() {
      public void process(JSONValue json) {
        imageSizes = interpretSizes(json);
        if (isReady()) ready.callback(src);
      }
    }, errorReport);      
  } 
  
  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ImageCollectionInterface#getCaptions()
   */
  public String[] getCaptions() {
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
  
  /**
   * Returns true, if loading the information about the image collection has
   * finished and it is ready to be queried.
   * 
   * @return true, if the information about the image collection is ready
   */
  public boolean isReady() {
    if (captionDictionary != null && directories != null 
        && imageNames != null && imageSizes != null) {
      if (captions == null) {
        captions = new String[imageNames.length];
        for (int i = 0; i < imageNames.length; i++) {
          if (captionDictionary.containsKey(imageNames[i]))
            captions[i] = captionDictionary.get(imageNames[i]);            
          else
            captions[i] = "";
        }
      }
      return true;
    } else return false;
  }
  
  protected HashMap<String, int[][]> interpretSizes(JSONValue json) 
      throws JSONException {
    JSONObject dict = json.isObject();
    HashMap<String, int[][]> sizes = new HashMap<String, int[][]>();
    ArrayList<int[]> resolutions = new ArrayList<int[]>();
    for (String key: dict.keySet()) {
      JSONArray list = dict.get(key).isArray();
      resolutions.clear();
      for (int i = 0; i < list.size(); i++ ) {
        JSONArray xy = list.get(i).isArray();
        int res[] = new int[2];
        res[0] = (int) xy.get(0).isNumber().doubleValue();
        res[1] = (int) xy.get(1).isNumber().doubleValue();
        resolutions.add(res);
      }
      sizes.put(key, resolutions.toArray(new int[resolutions.size()][]));
    }  
    return sizes;
  }
    
  protected String[] interpretStringArray(JSONValue json)
      throws JSONException {
    JSONArray array = json.isArray();
    ArrayList<String> stringList = new ArrayList<String>();
    for (int i = 0; i < array.size(); i++) {
      stringList.add(array.get(i).isString().stringValue());
    }
    return stringList.toArray(new String[stringList.size()]);
  }
  
  protected HashMap<String, String> interpretStringDictionary(JSONValue json) 
      throws JSONException {
    JSONObject dict = json.isObject();
    HashMap<String, String> stringDict = new HashMap<String, String>();    
    for (String key: dict.keySet()) {
      stringDict.put(key, dict.get(key).isString().stringValue());
    }
    return stringDict;
  }
  
  protected void readJSON(String url, JSONDelegate task, IMessage error) {
    RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
    try {
      builder.sendRequest(null, new JSONReceiver(url, task, error));
    } catch (RequestException e) {
      error.message("Couldn't retrieve JSON: " + url + 
          "<br />" + e.getMessage());         
    }    
  }
}
