/*
 * EditPanelKeyProcessor.java
 *
 * Created on October 23, 2000, 5:52 PM
 */

package com.SoftWoehr.JaXWT;

/** An interface for key processors associated with com.SoftWoehr.JaXWT.EditTextAreas
 *
 * @author jax
 * @version $Id: EditTextAreaKeyProcessor.java,v 1.2 2001-09-14 23:29:24 jwoehr Exp $
 */
public interface EditTextAreaKeyProcessor {
    /** Process certain key events before text area gets 'em
     * True if consumed.
     * @param e the KeyEvent
     * @return <CODE>true</CODE> .iff consumed.
     */
  public boolean processKeyEvent(java.awt.event.KeyEvent e);
}
