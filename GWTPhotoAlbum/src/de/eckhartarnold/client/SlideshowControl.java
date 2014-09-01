package de.eckhartarnold.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * A control for the slide show. 
 * 
 * <p>The interface <code>SlideshowControl</code> is implemented by classes
 * that monitor user reactions and issue changes in the state of the slideshow.
 * These classes may also reflect changes in state of the slideshow if these
 * have been issued from another source (e.g. resizing of the screen).
 * An implementation of this interface could be a panel of control buttons,
 * but also a touch screen control with no visible user interface. 
 * 
 * @see de.eckhartarnold.client.ControlPanel
 * 
 * @author eckhart
 *
 */
public interface SlideshowControl {

  /** 
   * This click handler should resemble a home button.
   */
  public abstract void onClick(ClickEvent event);

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onFade()
   */
  public abstract void onFade();

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onLoad(com.google.gwt.user.client.ui.Widget)
   */
  public abstract void onLoad(Widget sender);

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener.onResized()
   */
  public abstract void onResized();

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onShow()
   */
  public abstract void onShow(int slideNr);

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onStart()
   */
  public abstract void onStart();

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.SlideshowListener.onStop()
   */
  public abstract void onStop();

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.AttachmentListener#onUnload(com.google.gwt.user.client.ui.Widget)
   */
  public abstract void onUnload(Widget sender);

  /* (non-Javadoc)
   * @see de.eckhartarnold.client.ResizeListener.prepareResized()
   */
  public abstract void prepareResized();

  /**
   * Sets the listener for the "back to the gallery button". In order to listen
   * to any of the other buttons, add a {@link SlideshowListener} to the
   * slide show.
   * @param handler the <code>ClickHandler</code> for the gallery button
   */
  public abstract void setHomeButtonListener(ClickHandler handler);

}