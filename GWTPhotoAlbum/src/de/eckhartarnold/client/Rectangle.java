/*
 * Copyright 2015 Eckhart Arnold (eckhart_arnold@yahoo.de).
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
 * Simple class that stores the x,y,w,h coordinates of a rectangle.
 */
public class Rectangle {
  /** Coordinates of the Rectangle */
  public  int x, y, w, h;
  
  public Rectangle(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
  
  /**
   * Test, if a point lies inside the rectangle.
   * 
   * @param  x the x coordinate of the point
   * ̍@param  y the y coordinate of the point
   * 
   * @returns  true, if point is inside the rectangle, false otherwise
   */
  public boolean inside(int x, int y) {
    return ((x >= this.x) && (x < this.x + this.w) && 
            (y >= this.y) && (y < this.y + this.h));
  }
  
  /**
   * Returns a string representation of the rectangle.
   * 
   * @returns string representation of the recangle
   */
  public String toString() {
    return x + " " + y + " " + w + " " + h;
  }
}
