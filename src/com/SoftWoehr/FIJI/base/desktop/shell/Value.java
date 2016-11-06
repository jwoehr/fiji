/* Value.java ...  */
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

import java.io.Serializable;
import  com.SoftWoehr.SoftWoehr;
import  com.SoftWoehr.util.*;

/** Value is a self-fetching Variable.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.1 $
 */
public class Value extends Semantic implements SoftWoehr, verbose, Serializable {
    
    /** Revision level */
    private static final String rcsid = "$Id: Value.java,v 1.1 2016-11-06 21:20:38 jwoehr Exp $";
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
    
    /** The value of the Value */
    private Object datum = null;
    
    /** Arity/0 ctor, anonymous */
    public Value() {
        super("Anonymous Value");
    }
    
    /** Arity/1 ctor, the most useful one.
     * @param name  Name of this Value
     */
    public Value(String name) {
        super(name);
    }
    
    /** Return a String representation of the object.
     * @return  String representation */
    public String toString() {
        return "A Value named " + getName() + " whose value is " + datum;
    }
  
    /** The Value notifies subcomponents of shutdown then shuts itself down.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return  always 0
     */
    public int shutdown() {
        shutdownHelper.shutdownClients();
        // Your shutdown code for this object goes here.
        // ...
        
        // ...
        // Your shutdown code for this object went there.
        return 0;
    }
    
    /** Reinitialize the Value, discarding previous state. */
    //public void reinit() {
    //  }
    
    /** Execution semantics are to push own datum.
     * @param e  The engine against which to execute self.
     */
    public void execute(engine e) {
        e.push(datum);
    }
    
    /** Get the datum.
     * @return the value of this Value. */
    public Object getDatum() {
        return datum;
    }
    
    /** Set the datum.
     * @param o Set the value of this Value to this. */
    public void setDatum(Object o) {
        datum = o;
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
     * @param tf  set verbose on (true) or off.
     */
    public void    setVerbose  (boolean tf) {isverbose = tf;  }
    
    /** Emit a string message if set verbose.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s  The string to conditionally announce.
     */
    public void    announce    (String s)   {v.announce(s);   }
    
    /** Demonstrate <code>Value</code>. Not currently used.
     * @param argv  Args to the main() function ... not currently used.
     */
    public static void main(String argv[]) {
        
        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
        Value theValue = new Value();      /* Instance of Value we're demoing. */
        
    /* GPL'ed SoftWoehr announces itself. */
        System.out.println("Value, Copyright (C) 1999 Jack J. Woehr.");
        System.out.println("Value comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");
        
    /* See if user passed in the -v flag to request verbosity. */
        for (int i = 0; i < myArgs.optionCount() ; i++) {
            if (myArgs.nthOption(i).getOption().substring(1,2).equals("v")) {
                theValue.setVerbose(true);
            }                                                         /* End if*/
        }
        
        // Your code goes here.
        // -------------------
        
        // -------------------
        
        return;
    }
}                                                      /* End of Value class*/

/*  End of Value.java */
