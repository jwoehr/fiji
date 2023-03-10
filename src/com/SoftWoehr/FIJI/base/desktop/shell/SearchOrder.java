/* SearchOrder.java ...  */
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

import java.util.*;
import  com.SoftWoehr.SoftWoehr;
import  com.SoftWoehr.util.*;

/** This class provides the SearchOrder entity used by
 * FIJI to collate Wordlists.
 * @author $Author: jwoehr $
 * @version $Revision: 1.1 $
 */
public class SearchOrder implements SoftWoehr, verbose {
    
    /** Revision level */
    private static final String rcsid = "$Id: SearchOrder.java,v 1.1 2016-11-06 21:20:38 jwoehr Exp $";
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
    
    /** Vector which holds the search order */
    private Vector my_vector = new Vector();
    
    
    /** Arity/0 ctor. */
    public SearchOrder() {
        reinit();
    }
    
    /** The SearchOrder notifies subcomponents of shutdown then shuts itself down.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return  always zero 0
     */
    public int shutdown() {
        shutdownHelper.shutdownClients();
        // Your shutdown code for this object goes here.
        // ...
        
        // ...
        // Your shutdown code for this object went there.
        return 0;
    }
    
    /** Reinitialize the SearchOrder, discarding previous state. */
    public void reinit() {
        my_vector.setSize(0);
    }
    
    /** Find a Semantic (or null) somewhere in the search order.
     * @param name Semantic to find.
     * @return the Semantic */
    public Semantic find(String name) {
        Semantic result = null;
        Wordlist w = null;
        for (Enumeration e = my_vector.elements(); e.hasMoreElements();) {
            w = (Wordlist) (e.nextElement());
            result = w.find(name);
            if (null != result) {
                break;
            }
        }                                                          /* End for*/
        return result;
    }                                  /* public Semantic find(String name)*/
    
    /** Pop the active Semantic of a wordlist entry by finding
     * the first occurrence of same in the search order. Remove
     * the entry if no previous Semantic for the name exists.
     * @see com.SoftWoehr.FIJI.base.desktop.shell.Wordlist#forget
     * @param name Semantic to forget
     * @return <code>true</code> if found and forgotten
     */
    public boolean forget(String name) {
        boolean rc = false;
        Semantic result = null;
        Wordlist w = null;
        for (Enumeration e = my_vector.elements(); e.hasMoreElements();) {
            w = (Wordlist) (e.nextElement());
            result = w.find(name);
            if (null != result) {
                w.forget(name);    /* Pop the previous Semantic or discard if none.*/
                rc = true;                           /* Signal that name was found.*/
                break;                                                     /* Done.*/
            }
        }                                                          /* End for*/
        return rc;
    }                                  /* public void boolean (String name)*/
    
    /** Discard utterly a wordlist entry by finding
     * the first occurrence of same in the search order.
     * @see com.SoftWoehr.FIJI.base.desktop.shell.Wordlist#discard
     * @param name Entry to discard
     * @return <code>true</code> if found and discarded
     */
    public boolean discard(String name) {
        boolean rc = false;
        Semantic result = null;
        Wordlist w = null;
        for (Enumeration e = my_vector.elements(); e.hasMoreElements();) {
            w = (Wordlist) (e.nextElement());
            result = w.find(name);
            if (null != result) {
                w.discard(name);   /* Pop the previous Semantic or discard if none.*/
                rc = true;                           /* Signal that name was found.*/
                break;                                                     /* Done.*/
            }
        }                                                          /* End for*/
        return rc;
    }                                 /* public boolean forget(String name)*/
    
    /** Push the search order topped by number of Wordlists in search order.
     * @param z associated engine
     */
    public void getOrder(engine z) {
        for (Enumeration e = my_vector.elements(); e.hasMoreElements();) {
            z.push(e.nextElement());
        }                                                          /* End for*/
        z.push(new Long(my_vector.size()));
    }                                     /* public void getOrder(engine z)*/
    
    /** Grab search order from stack
     * @param z associated engine
     */
    public void setOrder(engine z) {
        reinit();                                        /* Empty search order.*/
        int numWordlists = ((Long) z.pop()).intValue();
        for (int i = 0; i < numWordlists; ++i) {
            my_vector.addElement(z.pop());                       /* Add wordlists to order.*/
        }                                                          /* End for*/
    }                                     /* public void setOrder(engine z)*/
    
    /** Return names of all words in the search order.
     * @return String of words
     */
    public String words() {
        String result = "";
        Wordlist w = null;
        for (Enumeration e = my_vector.elements(); e.hasMoreElements();) {
            w = (Wordlist) (e.nextElement());
            result += w.words();
        }                                                          /* End for*/
        return result;
    }                                             /* public String words () */
    
    
    /** Add a Wordlist to the SearchOrder
     * @param w Wordlist to add
     * @return <code>true</code> on success
     */
    public boolean add(Wordlist w) {
        return my_vector.add(w);
    }
    
    /** Return nth (zero-based) Wordlist.
     * @param i index
     * @return the Wordlist or null */
    public Wordlist nthElement(int i) {
        return (Wordlist) my_vector.elementAt(i);
    }
    
    /** Test verbosity
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return <code>true</code> if verbose */
    public boolean isVerbose()              {return isverbose;}
    
    /** Set verbosity
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param tf <code>true</code> if verbose */
    public void    setVerbose  (boolean tf) {
        isverbose = tf;
        announce("Setting search order verbose.");
        Wordlist w = null;
        for (Enumeration e = my_vector.elements(); e.hasMoreElements();) {
            w = (Wordlist) (e.nextElement());
            w.setVerbose(isVerbose());
        }                                                          /* End for*/
    }
    
    /** Announce a String if verbose.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s  */
    public void    announce    (String s)   {v.announce(s);   }
    
    
    /** Demonstrate <code>SearchOrder</code>.
     * @param argv  Args to test, currently not used.
     */
    public static void main(String argv[]) {
        
        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
        // SearchOrder theSearchOrder = new SearchOrder();         /* Instance of SearchOrder we're demoing.  */
        
        /* GPL'ed SoftWoehr announces itself. */
        System.out.println("SearchOrder, Copyright (C) 1999 Jack J. Woehr.");
        System.out.println("SearchOrder comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");
        
        /* See if user passed in the -v flag to request verbosity. */
        for (int i = 0; i < myArgs.optionCount() ; i++) {
            if (myArgs.nthOption(i).getOption().substring(1,2).equals("v")) {
                // theSearchOrder.setVerbose(true);
            }                                                         /* End if*/
        }
        
        // Your code goes here.
        // -------------------
        SearchOrder searchOrder = new SearchOrder();
        searchOrder.nthElement(0);
        searchOrder.add(new com.SoftWoehr.FIJI.base.desktop.shell.Wordlist());
        // -------------------
        
        return;
    }
}                                                      /* End of SearchOrder class*/

/*  End of SearchOrder.java */
