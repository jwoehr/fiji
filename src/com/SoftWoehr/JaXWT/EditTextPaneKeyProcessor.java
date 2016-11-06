/*
 * EditTextPaneKeyProcessor.java
 *
 * Created on December 24, 2000, 7:15 PM
 */

package com.SoftWoehr.JaXWT;

/** Key processor for EditTextPane.
 * @author jax
 * @version $Id: EditTextPaneKeyProcessor.java,v 1.1 2016-11-06 21:20:40 jwoehr Exp $
 */
public interface EditTextPaneKeyProcessor {
    /** Process certain key events before text area gets 'em
     * True if consumed.
     * @param e The key event.
     * @return <CODE>true</CODE> .iff key processed.
     */
  public boolean processKeyEvent(java.awt.event.KeyEvent e);
}
