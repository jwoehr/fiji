/* Error.java ...  */
/*********************************************/
/* Copyright *C* 1998, 2001                  */
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

package com.SoftWoehr.FIJI.base;
import com.SoftWoehr.SoftWoehr;
import  com.SoftWoehr.util.*;

/** An Error class for SoftWoehr
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.3 $
 */
public class Error extends java.lang.Error implements SoftWoehr, verbose {
    
    /** Revision level */
    private static final String rcsid = "$Id: Error.java,v 1.3 2001-09-15 05:25:48 jwoehr Exp $";
    
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
    
    private Throwable t;
    
    /** Errors pertaining to com.SoftWoehr.desktop. */
    public static class desktop {
        /** Errors pertaining to com.SoftWoehr.desktop.shell. */
        public static class shell {
            
            /** Invalid numeric conversion base for interpreter. */
            public static class BadBase extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public BadBase(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s the String message
                 * @param t The throwable to carry along  */
                public BadBase(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                          /* BadBase*/
            
            /** Invalid character display code page requested from host system.*/
            public static class BadEncoding extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public BadEncoding(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public BadEncoding(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* BadEncoding*/
            
            /** Invalid name offered for a Semantic.*/
            public static class BadName extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public BadName(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public BadName(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* BadEncoding*/
            
            /** Attempt to perform a Variable operation on a non-Variable.*/
            public static class NonVariable extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public NonVariable(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public NonVariable(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* NonVariable*/
            
            /** Attempt to perform a Value operation on a non-Value.*/
            public static class NonValue extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public NonValue(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public NonValue(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                         /* NonValue*/
            
            /** Exception encoutered in execution semantics of a Primitive.*/
            public static class BadPrimitiveExecute extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public BadPrimitiveExecute(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public BadPrimitiveExecute(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                              /* BadPrimitiveExecute*/
            
            /** Exception encoutered in compilation semantics of a Primitive.*/
            public static class BadPrimitiveCompile extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public BadPrimitiveCompile(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public BadPrimitiveCompile(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                              /* BadPrimitiveCompile*/
            
            /** Exception encoutered in compilation semantics of a Primitive.*/
            public static class CompileToValue extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along The throwable to carry along  */
                public CompileToValue(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along The throwable to carry along  */
                public CompileToValue(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                   /* CompileToValue*/
            
            /** Name not found in the interpreter search order.*/
            public static class NameNotFound extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public NameNotFound(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public NameNotFound(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                     /* NameNotFound*/
            
            /** Control flow stack imbalance.*/
            public static class ControlFlowStackImbalance extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t The throwable to carry along  */
                public ControlFlowStackImbalance(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                  * @param s the String message
                 * @param t The throwable to carry along  */
                public ControlFlowStackImbalance(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                     /* ControlFlowStackImbalance*/
        }                                                              /* shell*/
    }                                                              /* desktop*/
    
    /** A buncha error types. */
    public static class bAcKtOmain extends com.SoftWoehr.FIJI.base.Error {
        
        /** Arity/1 Throwable constructor.
         * @param t The throwable to carry along  */
        public bAcKtOmain(Throwable t) {
            super(t);
        }
        
        /** Arity/2 Throwable constructor.
          * @param s the String message
         * @param t The throwable to carry along  */
        public bAcKtOmain(String s, java.lang.Throwable t) {
            super(s,t);
        }
        
    }
    
    /** A buncha error types. */
    public static class bAdArGtOmain extends com.SoftWoehr.FIJI.base.Error {
        
        /** Arity/1 Throwable constructor.
         * @param t The throwable to carry along  */
        public bAdArGtOmain(Throwable t) {
            super(t);
        }
        
        /** Arity/2 Throwable constructor.
          * @param s the String message
         * @param t The throwable to carry along  */
        public bAdArGtOmain(String s, java.lang.Throwable t) {
            super(s,t);
        }
    }
    
    /** Arity/0 ctor. */
    public Error() {
    }
    
    /** Arity/1 Throwable constructor.
     * @param t The throwable to carry along  */
    public Error(Throwable t) {
        this.t = t;
    }
    
    /** Arity/2 Throwable constructor.
      * @param s the String message
     * @param t The throwable to carry along  */
    public Error(String s, java.lang.Throwable t) {
        super(s);
        this.t = t;
    }
    
    /** The XZZY notifies subcomponents of shutdown then shuts itself down.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return zero always
     */
    public int shutdown() {
        shutdownHelper.shutdownClients();
        // Your shutdown code for this object goes here.
        // ...
        
        // ...
        // Your shutdown code for this object went there.
        return 0;
    }
    
    /** Return the Throwable, if any, which occasioned this error.
     * @return  the throwable
     */
    public Throwable getThrowable() {
        return t;
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
    
    /** Demonstrate <code>Error</code>.
     * @param argv  args to main() -- not used
     */
    public static void main(String argv[]) {
        
        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
        
    /* GPL'ed SoftWoehr announces itself. */
        System.out.println("Error, Copyright (C) 1988 Jack J. Woehr.");
        System.out.println("Error comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");
        
        // Test code goes here.
        // -------------------
        
        GetArgs.main(argv);       /* Delete this stub when you write some code.*/
        
        // -------------------
        
        return;
    }
}                                                     /* End of Error class*/

/*  End of Error.java */
