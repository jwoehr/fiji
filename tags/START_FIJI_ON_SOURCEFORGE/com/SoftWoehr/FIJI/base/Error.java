/* Error.java ...  */
/*********************************************/
/* Copyright *C* 1998, All Rights Reserved.  */
/* Jack J. Woehr jax@well.com jwoehr@ibm.net */
/* http://www.well.com/user/jax/rcfb         */
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
 * @version $Revision: 1.1.1.1 $
 */
public class Error extends java.lang.Error implements SoftWoehr, verbose {
    
    /** Revision level */
    private static final String rcsid = "$Id: Error.java,v 1.1.1.1 2001-08-21 02:38:42 jwoehr Exp $";
    
    /** Implements com.SoftWoehr.SoftWoehr
     * @return  */
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
                 * @param t  */
                public BadBase(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public BadBase(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                          /* BadBase*/
            
            /** Invalid character display code page requested from host system.*/
            public static class BadEncoding extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public BadEncoding(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public BadEncoding(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* BadEncoding*/
            
            /** Invalid name offered for a Semantic.*/
            public static class BadName extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public BadName(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public BadName(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* BadEncoding*/
            
            /** Attempt to perform a Variable operation on a non-Variable.*/
            public static class NonVariable extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public NonVariable(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public NonVariable(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* NonVariable*/
            
            /** Attempt to perform a Value operation on a non-Value.*/
            public static class NonValue extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public NonValue(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public NonValue(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                         /* NonValue*/
            
            /** Exception encoutered in execution semantics of a Primitive.*/
            public static class BadPrimitiveExecute extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public BadPrimitiveExecute(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public BadPrimitiveExecute(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                              /* BadPrimitiveExecute*/
            
            /** Exception encoutered in compilation semantics of a Primitive.*/
            public static class BadPrimitiveCompile extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public BadPrimitiveCompile(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public BadPrimitiveCompile(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                              /* BadPrimitiveCompile*/
            
            /** Exception encoutered in compilation semantics of a Primitive.*/
            public static class CompileToValue extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public CompileToValue(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public CompileToValue(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                   /* CompileToValue*/
            
            /** Name not found in the interpreter search order.*/
            public static class NameNotFound extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public NameNotFound(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public NameNotFound(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                     /* NameNotFound*/
            
            /** Control flow stack imbalance.*/
            public static class ControlFlowStackImbalance extends com.SoftWoehr.FIJI.base.Error {
                
                /** Arity/1 Throwable constructor.
                 * @param t  */
                public ControlFlowStackImbalance(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s
                 * @param t  */
                public ControlFlowStackImbalance(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                     /* ControlFlowStackImbalance*/
        }                                                              /* shell*/
    }                                                              /* desktop*/
    
    /** A buncha error types. */
    public static class bAcKtOmain extends com.SoftWoehr.FIJI.base.Error {
        
        /** Arity/1 Throwable constructor.
         * @param t  */
        public bAcKtOmain(Throwable t) {
            super(t);
        }
        
        /** Arity/2 Throwable constructor.
         * @param s
         * @param t  */
        public bAcKtOmain(String s, java.lang.Throwable t) {
            super(s,t);
        }
        
    }
    
    /** A buncha error types. */
    public static class bAdArGtOmain extends com.SoftWoehr.FIJI.base.Error {
        
        /** Arity/1 Throwable constructor.
         * @param t  */
        public bAdArGtOmain(Throwable t) {
            super(t);
        }
        
        /** Arity/2 Throwable constructor.
         * @param s
         * @param t  */
        public bAdArGtOmain(String s, java.lang.Throwable t) {
            super(s,t);
        }
    }
    
    /** Arity/0 ctor. */
    public Error() {
    }
    
    /** Arity/1 Throwable constructor.
     * @param t  */
    public Error(Throwable t) {
        this.t = t;
    }
    
    /** Arity/2 Throwable constructor.
     * @param s
     * @param t  */
    public Error(String s, java.lang.Throwable t) {
        super(s);
        this.t = t;
    }
    
    /**
     * @return  */
    public String toString()
    {return super.toString();}
    
    /** The XZZY notifies subcomponents of shutdown then shuts itself down.
     * @see com.SoftWoehr.SoftWoehr#
     * @return  */
    public int shutdown() {
        shutdownHelper.shutdownClients();
        // Your shutdown code for this object goes here.
        // ...
        
        // ...
        // Your shutdown code for this object went there.
        return 0;
    }
    
    /**
     * @throws Throwable  */
    protected void finalize() throws Throwable {           /* Called by garbage collector in case no longer referenced*/
        super.finalize();
    }
    
    /** Return the Throwable, if any, which occasioned this error.
     * @return  */
    public Throwable getThrowable() {
        return t;
    }
 
    /**
     * @see com.SoftWoehr.util.verbose#
     * @see com.SoftWoehr.util.verbosity#
     * @return  */
    public boolean isVerbose()              {return isverbose;}
    
    /**
     * @see com.SoftWoehr.util.verbose#
     * @see com.SoftWoehr.util.verbosity#
     * @param tf  */
    public void    setVerbose  (boolean tf) {isverbose = tf;  }
    
    /**
     * @see com.SoftWoehr.util.verbose#
     * @see com.SoftWoehr.util.verbosity#
     * @param s  */
    public void    announce    (String s)   {v.announce(s);   }
    
    /** Demonstrate <code>Error</code>.
     * @param argv  */
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
