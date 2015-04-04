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
 * Receiver Interface for high level touch screen events.
 * 
 * @author eckhart
 *
 */
public interface TouchScreenReceiver {

  /**
   * Reports a single tap by the user. Unless this functions returns false,
   * a second tap that occurs shortly afterwards at the same position or a
   * position close by is not reported by this function, but by function
   * <code>doubleTap</code>.
   * 
   * @param x  the horizontal position on the screen where the tap occurred
   * @param y  the vertical position on the screen where the tap occurred
   * @return  true, if the event has been consumed. In this case the
   *          event is considered as complete and not as the first part of
   *          a double tap.
   */  
  public boolean tap(int x, int y);

  
  /**
   * Reports a double tap by the user. The reported x and y values will 
   * always be the same as those of the first tap, even if the second tap 
   * occurred at a slightly different location.
   * 
   * @param x  the horizontal position on the screen where the tap occurred
   * @param y  the vertical position on the screen where the tap occurred
   */  
  public void doubleTap(int x, int y);
  
  
  /**
   * Reports the end of a horizontal swipe gesture. 
   * 
   * @param delta delta the horizontal delta from the beginning of the swipe gesture.
   *        negative values indicate a direction from right to left, positive
   *        values indicate a left to right direction.
   */
  public void endLeftRightSwipe(int delta);
  
  
  /**
   * Reports the end of a vertical swipe gesture.
   * 
   * @param delta the vertical delta from the beginning of the swipe gesture.
   *        negative values indicate a direction from bottom to top, positive
   *        values indicate a top to bottom direction.
   */
  public void endUpDownSwipe(int delta);  
  
  /** 
   * Reports a horizontal swipe gesture by the user. Once the beginning of a 
   * horizontal swipe gesture has been detected, horizontal swipe events
   * will continuously be reported until either the user takes her finger off
   * the touch screen or this functions "consumes" the event by returning true.
   * Once a horizontal swipe has started, up or down swipe events will not
   * be reported, even if the user changes to moving her finger vertically. 
   * The delta value reports the
   * horizontal delta from the very start of the swipe gesture, i.e. if the 
   * user moves to and from the horizontal distance from the beginning to the
   * last touch event of same the swipe sequence will be reported.
   * 
   * @param delta the horizontal delta from the beginning of the swipe gesture.
   *        negative values indicate a direction from right to left, positive
   *        values indicate a left to right direction.
   */  
  public void leftRightSwipe(int delta);

  
  /** 
   * Reports a vertical swipe gesture by the user. Once the beginning of a 
   * vertical swipe gesture has been detected, horizontal swipe events
   * will continuously be reported until either the user takes her finger off
   * the touch screen or this functions "consumes" the event by returning true.
   * Once a vertical swipe has started, left or right swipe events will not
   * be reported, even if the user changes to moving her finger vertically. 
   * The delta value reports the
   * vertical delta from the very start of the swipe gesture, i.e. if the 
   * user moves to and from the vertical distance from the beginning to the
   * last touch event of same the swipe sequence will be reported.
   * 
   * @param delta the vertical delta from the beginning of the swipe gesture.
   *        negative values indicate a direction from bottom to top, positive
   *        values indicate a top to bottom direction.
   */   
  public void upDownSwipe(int delta);

}

