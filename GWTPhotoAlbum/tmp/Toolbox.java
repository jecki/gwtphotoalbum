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
 * Class <code>Toolbox</code> implements typical algorithms that may be of use
 * at several points in the package.
 */
class Toolbox {
  /**
   * Interprets a boolean string as either true or false. As true counts a 
   * string value of "true", "yes", "on", "1" or any upper/lowercase version
   * of these. A string is interpreted as false, if it contains "false", "no",
   * "off", "0" or any upper/lowercase version of these. If it is impossible
   * to determine whether the string is to be interpreted as "true" or "false"
   * or if <code>boolStr</code> is <code>null</code>,
   * <code>defaultValue</code> is returned.
   *   
   * @param boolStr       the string to be interpreted as a boolean
   * @param defaultValue  the default value in case the string cannot
   *                      meaningfully be interpreted
   * @return the boolean value contained in <code>boolStr</code>
   */
  static boolean interpretBooleanString(String boolStr, boolean defaultValue) {
    boolStr = boolStr.toLowerCase();
    if (boolStr == "true" || boolStr == "yes" || boolStr == "on" 
        || boolStr == "1") {
      return true;
    } else if (boolStr == "false" || boolStr == "no" || boolStr == "off" 
      || boolStr == "0") {
      return false;
    } else return defaultValue;
  }
}

