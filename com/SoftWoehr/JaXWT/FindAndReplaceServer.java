/*
 * FindAndReplaceServer.java
 *
 * Created on October 22, 2000, 1:11 PM
 */

package com.SoftWoehr.JaXWT;

/**
 * A find-and-replace engine for a text area.
 * @author jax
 * @version $Id: FindAndReplaceServer.java,v 1.3 2001-09-15 07:30:04 jwoehr Exp $
 */
public interface FindAndReplaceServer {
    
    /** Find backwards and hilite. False iff not found
     * @param sought String sought
     * @return False iff not found */
    public boolean find_forward_and_select(String sought);
    
    /** Find backwards and hilite. False iff not found
     * @param sought String sought
     * @return False iff not found */
    public boolean find_backward_and_select(String sought);
    
    /** Replace selected with replacement. False iff nothing selected.
     * @param replacement Replacement text
     * @return False iff nothing selected. */
    public boolean replace_selected(String replacement);
    
    /** Replace all matches with replacement. False iff nothing replaced.
     * @param sought String sought
     * @param replacement Replacement text
     * @return False iff nothing replaced.
     */
    public int replace_globally(String sought, String replacement);
}
