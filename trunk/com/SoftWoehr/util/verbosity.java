/* verbosity.java ... support opt. verbose.  */
/* jack j. woehr jax@well.com jwoehr@ibm.net */
/* http://www.well.com/user/jax/rcfb         */
/* P.O. Box 51, Golden, Colorado 80402-0051  */
/*********************************************/
/* Copyright *C* 1998, 2001 Jack J. Woehr    */
/*	      All Rights Reserved	     */
/* PO Box 51 Golden, Colorado 80402-0051 USA */
/*	    http://www.softwoehr.com	     */
/*	  http://fiji.sourceforge.net	     */
/*                                           */
/*       This Program is Free                */
/*            Softwoehr                      */
/*                                           */
/* Permission to distribute this Softwoehr   */
/* with copyright notice attached is granted.*/
/*                                           */
/* Permission to modify for personal use at  */
/* at home or for your personal use on the   */
/* job is granted, but you may not publicly  */
/* make available modified versions of this  */
/* program without asking and getting the    */
/* permission of the author, Jack Woehr.     */
/*                                           */
/* The permission will usually be granted if */
/* granted reciprocally by you for the mods. */
/*                                           */
/* THERE IS NO GUARANTEE, NO WARRANTY AT ALL */
/*********************************************/
package com.SoftWoehr.util;
import  com.SoftWoehr.SoftWoehr;

/**
 * Class verbosity provides routines for
 * programming of applications which
 * implement <code>com.SoftWoehr.util.verbose</code>.
 * @author $Author: jwoehr $
 * @version $Revision: 1.3 $
 * @see com.SoftWoehr.util.verbose
 */
public class verbosity implements SoftWoehr {
    /** Identifies revision level of source for SoftWoehr interface */
    private static final String rcsid = "$Id: verbosity.java,v 1.3 2001-09-10 17:20:10 jwoehr Exp $";
    
    /** Returns revision info for SoftWoehr interface.
     * @return the rcsid
     */
    public  String              rcsId() { return rcsid; }
    
    /** shutdown() here does nothing.
     * @see com.SoftWoehr.SoftWoehr#
     * @return Always 0
     */
    public int shutdown() { return 0; }
    
    /**
     * The verbose is a class which implements the interface
     * and on whose behalf <code>verbosity.announce()</code>
     * is performed.
     */
    verbose v;
    
    /** Open on the <code>verbose</code> instance.
     * @param v The verbose instance.
     */
    public verbosity(verbose v) {
        this.v = v;
    }
    
    /** Unimplemented arity/0 ctor. */
    private verbosity() {}
    
    /** Say something on behalf of the <code>verbosity</code> implementation
     * if it's being verbose at that instant. The point of
     * <code>verbose</code>/<code>verbosity</code>.
     * @param s String to announce.
     */
    public void announce(String s) {
        if (v.isVerbose()) {
            System.err.println(s);
            System.err.flush();
        }                                                           /* End if*/
    }
    
    /** A test to demo <code>verbosity</code>.
     * @param NOISYorQuiet Iff <CODE>true</CODE> be verbose.
     */
    public static void test(boolean NOISYorQuiet) {
        verbose v = new verbose() {
            verbosity yack = new verbosity(this);
            boolean isverbose = false;
            public boolean isVerbose()              {return isverbose;}
            public void    setVerbose  (boolean tf) {isverbose = tf;  }
            public void    announce    (String s)   {yack.announce(s);}
        };
        
        v.setVerbose(NOISYorQuiet);
        System.err.println("Ah, love! Could you and I with Him conspire");
        v.announce("To grasp this sorry Scheme of Things entire");
        System.err.println("Would not we shatter it to bits - and then");
        v.announce("Re-mould it nearer to the Heart's Desire!");
        System.err.println("-- The Rubaiyat of Omar Khayyam, Fitzgerald Translation");
        System.err.println("(If the above doesn't make sense, try the -v option!)");
    }
    
    /** Calls <code>test()</code> to demo <code>verbosity</code>.
     * @param argv switch to the test: if "-v" then be verbose
     */
    public static void main(String argv[]) {
        
        boolean NOISYorQuiet = false;
        
        if (argv.length > 0) {
            if (argv[0].equals("-v")) {
                NOISYorQuiet = true;
            }
        }                                                           /* End if*/
        verbosity.test(NOISYorQuiet);
    }
}                                               /* End of verbosity class*/
/*  End of verbosity.java */
