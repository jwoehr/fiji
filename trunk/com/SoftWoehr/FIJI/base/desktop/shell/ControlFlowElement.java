/* ControlFlowElement.java ...               */
/* FIJI representation of control flow.      */
/*********************************************/
/* Copyright *C* 1999, 2001                  */
/* All Rights Reserved.                      */
/* Jack J. Woehr jax@softwoehr.com           */
/* http://www.softwoehr.com                  */
/* http://fiji.sourceforge.net               */
/* P.O. Box 51, Golden, Colorado 80402-0051  */
/*********************************************/
/*                                           */
/*    This Program is Free SoftWoehr.        */
/*                                           */
/* THERE IS NO GUARANTEE, NO WARRANTY AT ALL */
/*********************************************/
/*
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package com.SoftWoehr.FIJI.base.desktop.shell;

import  com.SoftWoehr.SoftWoehr;
import  com.SoftWoehr.util.*;

/** ControlFlowElement wrappers a Semantic pushed on the control flow
 * stack along with information about the context when the entry
 * was pushed. Definitions may nest and the current definition is
 * pushed to support this, but the real nice thing aside from the
 * marginally useful ability to nest definitions is that it's easy
 * for the compiler to know a definition was messed up when it
 * doesn't find a Definition control flow element on popping the
 * control flow stack.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.5 $
 */
public class ControlFlowElement implements SoftWoehr, verbose {
    
    /** Revision level */
    private static final String rcsid = "$Id: ControlFlowElement.java,v 1.5 2001-09-15 05:25:48 jwoehr Exp $";
    /** Implements com.SoftWoehr.SoftWoehr
     * @return the rcsid
     */
    public String rcsId() {return rcsid;}
    
    /**  Flags whether we are in verbose mode. */
    private boolean isverbose = true;
    /**  Helper for verbose mode. */
    private verbosity v = new verbosity(this);
    
    /** Does the work of notifying shutdown clients. */
    private ShutdownHelper shutdownHelper = new ShutdownHelper();
    
    /** The Semantic this entry represents. */
    public Semantic element;
    
    /** Interpret/compile state at time this entry was created. */
    public boolean state;
    
    /** The Class this ControlFlowElement represents, since
     * sometimes this.element is null, e.g., in the case
     * of a pushed null currentDefinition.
     */
    public Class semanticClass;
    
    /** Creates a ControlFlowElement for a
     * specific Semantic  with a null element.
     * @param s Semantic associated with this ControlFlowElement
     * @param e Engine against which to run
     */
    public ControlFlowElement(Semantic s, engine e) {
        reinit(s,e);
    }
    
    /** Creates a ControlFlowElement representing specific
     * type of Semantic with a non-null element.
     * @param s Semantic associated with this ControlFlowElement
     * @param e Engine against which to run
     * @param c Class of Semantic
     */
    public ControlFlowElement(Semantic s, engine e, Class c) {
        reinit(s,e,c);
    }
    
    /** The ControlFlowElement notifies subcomponents of shutdown then shuts itself down.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return  Always false.
     */
    public int shutdown() {
        shutdownHelper.shutdownClients();
        // Your shutdown code for this object goes here.
        // ...
        
        // ...
        // Your shutdown code for this object went there.
        return 0;
    }
    
    /** Reinitialize the ControlFlowElement, discarding previous state.
     * @param s Semantic associated with this ControlFlowElement
     * @param e Engine against which to run
     */
    public void reinit(Semantic s, engine e) {
        reinit(s, e, s.getClass());
    }
    
    /** Reinitialize the ControlFlowElement, discarding previous state.
     * @param s Semantic associated with this ControlFlowElement
     * @param e Engine against which to run
     * @param c Class of the Semantic
     */
    public void reinit(Semantic s, engine e, Class c) {
        element = s;
        state = e.state;
        semanticClass = c;
    }
    
    /** Return the class of the Semantic element this entry represents.
     * @return  the class
     */
    public Class elementClass() {
        return semanticClass;
    }
    
    /** Return the Semantic element this entry represents.
     * @return the Semantic
     */
    public Semantic getElement() {
        return element;
    }
    
    /** Is this verbose and announcing?
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return true if verbose
     */
    public boolean isVerbose()              {return isverbose;}
    
    /** Set verbose and announcing.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param tf true to set verbose
     */
    public void    setVerbose  (boolean tf) {isverbose = tf;  }
    
    /** Emit a string message if set verbose.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s string to announce if verbose
     */
    public void    announce    (String s)   {v.announce(s);   }
    
    /** Demonstrate <code>ControlFlowElement</code>.
     * @param argv Args to main () not currently used.
     */
    public static void main(String argv[]) {
        
        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
        // ControlFlowElement theControlFlowElement = new ControlFlowElement();         /* Instance of ControlFlowElement we're demoing.  */
        
    /* GPL'ed SoftWoehr announces itself. */
        System.out.println("ControlFlowElement, Copyright (C) 1999 Jack J. Woehr.");
        System.out.println("ControlFlowElement comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");
        
    /* See if user passed in the -v flag to request verbosity. */
        for (int i = 0; i < myArgs.optionCount() ; i++) {
            if (myArgs.nthOption(i).getOption().substring(1,2).equals("v")) {
                // theControlFlowElement.setVerbose(true);
            }                                                         /* End if*/
        }
        
        // Your code goes here.
        // -------------------
        
        // -------------------
        
        return;
    }
}                                        /* End of ControlFlowElement class*/

/*  End of ControlFlowElement.java */
