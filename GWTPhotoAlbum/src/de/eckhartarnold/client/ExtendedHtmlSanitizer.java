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

import com.google.gwt.safehtml.shared.HtmlSanitizer;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;

/**
 * @author eckhart
 *
 * <code>ExtendedHtmlSanitizer</code> is an extended version of 
 * <code>com.google.gwt.safehtml.shared.HtmlSanitizer</code>. In contrast to 
 * the latter it also leaves line break "br" tags in the code. 
 * 
 * Thus, captions, titles and subtitle may contain line breaks as well.
 */
public class ExtendedHtmlSanitizer implements HtmlSanitizer {
  /** The instance variable of the singelton object ExtendedHtmlSanitizer */
  private static ExtendedHtmlSanitizer singleton;
  
  public static ExtendedHtmlSanitizer getInstance() {
    if (singleton == null) {
      singleton = new ExtendedHtmlSanitizer();
    }
    return singleton;
  }
  
  /**
   * HTML sanitizes a string, i.e. escapes all HTML tags and characters except
   * a small number like b, br.
   * @param html  the yet "unsanitized" string
   * @return the HTML-sanitized string.
   */
  public static SafeHtml sanitizeHTML(String html) {
    return getInstance().sanitize(html);
  }
   
  /* (non-Javadoc)
   * @see com.google.gwt.safehtml.shared.HtmlSanitizer#sanitize(java.lang.String)
   */
  @Override
  public SafeHtml sanitize(String html) {
    String s = html.replaceAll("\n", "").replaceAll("<br>", "\n").replaceAll("<br />", "\n");
    String sanitized = SimpleHtmlSanitizer.sanitizeHtml(s).asString();
    return new OnlyToBeUsedInGeneratedCodeStringBlessedAsSafeHtml( 
      sanitized.replaceAll("\n", "<br />"));
  }

}
