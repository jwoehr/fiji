/* Exceptions.java ...  */
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

/** An Exception base class for SoftWoehr.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.3 $
 */
public class Exceptions extends java.lang.Exception implements SoftWoehr, verbose {
    
    /** Revision level */
    private static final String rcsid = "$Id: Exceptions.java,v 1.3 2001-09-16 04:10:15 jwoehr Exp $";
    /** Implements com.SoftWoehr.SoftWoehr
     * @return The rcsid
     */
    public String rcsId() {return rcsid;}
    
    /**  Flags whether we are in verbose mode. */
    private boolean isverbose = true;
    /**  Helper for verbose mode. */
    private verbosity v = new verbosity(this);
    
    /** Does the work of notifying shutdown clients. */
    private ShutdownHelper shutdownHelper = new ShutdownHelper();
    
    private Throwable t;
    
    /** Exceptions pertaining to com.SoftWoehr.desktop. */
    public static class desktop {
        /** Exceptions pertaining to com.SoftWoehr.desktop.shell. */
        public static class shell {
            
            /** Stack too shallow for operation. */
            public static class StackUnderflow extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public StackUnderflow(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public StackUnderflow(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                   /* StackUnderflow*/
            
            /** An entity was passed as an instance of a class but is not. */
            public static class NotClassInstance extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload
                 */
                public NotClassInstance(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public NotClassInstance(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                 /* NotClassInstance*/
            
            /** An entity was passed as an instance of a reflected class but is not. */
            public static class NonReflectedType extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public NonReflectedType(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public NonReflectedType(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                 /* NonReflectedType*/
            
            /** A String was passed to represent a class name but doesn't. */
            public static class NotClassName extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public NotClassName(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public NotClassName(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                     /* NotClassName*/
            
            /** Invalid numeric conversion base for interpreter. */
            public static class BadBase extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BadBase(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BadBase(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                          /* BadBase*/
            
            /** Invalid character display code page requested from host system.*/
            public static class BadEncoding extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BadEncoding(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BadEncoding(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* BadEncoding*/
            
            /** Invalid name offered for a Semantic.*/
            public static class BadName extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BadName(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BadName(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* BadEncoding*/
            
            /** Attempt to perform a Variable operation on a non-Variable.*/
            public static class NonVariable extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public NonVariable(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public NonVariable(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* NonVariable*/
            
            /** Attempt to perform a Value operation on a non-Value.*/
            public static class NonValue extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public NonValue(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public NonValue(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                         /* NonValue*/
            
            /** Exception encoutered in execution semantics of a Primitive.*/
            public static class BadPrimitiveExecute extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BadPrimitiveExecute(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BadPrimitiveExecute(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                              /* BadPrimitiveExecute*/
            
            /** Exception encoutered in execution semantics of a Definition.*/
            public static class BadDefinitionExecute extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BadDefinitionExecute(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BadDefinitionExecute(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                             /* BadDefinitionExecute*/
            
            /** Exception encoutered in compilation semantics of a Primitive.*/
            public static class BadPrimitiveCompile extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BadPrimitiveCompile(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BadPrimitiveCompile(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                              /* BadPrimitiveCompile*/
            
            /** Exception encoutered in compilation semantics of a Definition.*/
            public static class BadDefinitionCompile extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BadDefinitionCompile(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BadDefinitionCompile(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                             /* BadDefinitionCompile*/
            
            /** Exception encoutered in compilation semantics of a Value.*/
            public static class CompileToValue extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public CompileToValue(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public CompileToValue(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                   /* CompileToValue*/
            
            /** Name not found in the interpreter search order.*/
            public static class NameNotFound extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload
                 */
                public NameNotFound(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public NameNotFound(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                     /* NameNotFound*/
            
            /** Control flow stack imbalance.*/
            public static class ControlFlowStackImbalance extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public ControlFlowStackImbalance(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public ControlFlowStackImbalance(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                        /* ControlFlowStackImbalance*/
            
            /** ParamaterizedPrimitive possess useless parameter object.*/
            public static class InvalidParameterObject extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public InvalidParameterObject(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public InvalidParameterObject(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                           /* InvalidParameterObject*/
            
            /** Non-Boolean passed to a conditional. */
            public static class ConditionalNonBoolean extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public ConditionalNonBoolean(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public ConditionalNonBoolean(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                            /* ConditionalNonBoolean*/
            
            /** Problem during branch resolution, bad branch, bad object types, etc. */
            public static class BranchResolution extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload  */
                public BranchResolution(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload  */
                public BranchResolution(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                 /* BranchResolution*/
            
            /** Interpret-time use of compile-only syntax. */
            public static class CompileOnly extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload
                 */
                public CompileOnly(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload
                 */
                public CompileOnly(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                      /* CompileOnly*/
            
            /** Problem compiling an if-branch. */
            public static class OpenIfBranch extends com.SoftWoehr.FIJI.base.Exceptions {
                
                /** Arity/1 Throwable constructor.
                 * @param t Throwable payload
                 */
                public OpenIfBranch(Throwable t) {
                    super(t);
                }
                
                /** Arity/2 Throwable constructor.
                 * @param s String message
                 * @param t Throwable payload
                 */
                public OpenIfBranch(String s, java.lang.Throwable t) {
                    super(s,t);
                }
            }                                                     /* OpenIfBranch*/
        }                                                              /* shell*/
    }                                                              /* desktop*/
    
    /** A buncha Exceptions types. */
    public static class bAcKtOmain extends com.SoftWoehr.FIJI.base.Exceptions {
        
        /** Arity/1 Throwable constructor.
         * @param t Throwable payload
         */
        public bAcKtOmain(Throwable t) {
            super(t);
        }
        
        /** Arity/2 Throwable constructor.
         * @param s String message
         * @param t Throwable payload
         */
        public bAcKtOmain(String s, java.lang.Throwable t) {
            super(s,t);
        }
        
    }
    
    /** A buncha Exceptions types. */
    public static class bAdArGtOmain extends com.SoftWoehr.FIJI.base.Exceptions {
        
        /** Arity/1 Throwable constructor.
         * @param t Throwable payload
         */
        public bAdArGtOmain(Throwable t) {
            super(t);
        }
        
        /** Arity/2 Throwable constructor.
         * @param s String message
         * @param t Throwable payload
         */
        public bAdArGtOmain(String s, java.lang.Throwable t) {
            super(s,t);
        }
    }
    
    /** Arity/0 ctor. */
    public Exceptions() {
    }
    
    /** Arity/1 Throwable constructor.
     * @param t Throwable payload
     */
    public Exceptions(Throwable t) {
        this.t = t;
    }
    
    /** Arity/2 Throwable constructor.
     * @param s String message
     * @param t Throwable payload
     */
    public Exceptions(String s, java.lang.Throwable t) {
        super(s);
        this.t = t;
    }
    
    /** String representation of object
     * @return string rep
     */
    public String toString() {
        String result = super.toString();
        if (null != t) {
            result += "Embedded exception: " + t.toString();
        }                                                           /* End if*/
        return result;
    }
    
    /** Some controller notifies subcomponents of shutdown then shuts itself down.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return always 0
     */
    public int shutdown() {
        shutdownHelper.shutdownClients();
        // Your shutdown code for this object goes here.
        // ...
        
        // ...
        // Your shutdown code for this object went there.
        return 0;
    }
    
    /** Return the Throwable, if any, which occasioned this Exceptions.
     * @return The Throwable, if any, which occasioned this Exceptions.
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
     * @param s String to announce.
     */
    public void    announce    (String s)   {v.announce(s);   }
    
    /** Demonstrate <code>Exceptions</code>.
     * @param argv Args for exception demo. Not currently used.
     */
    public static void main(String argv[]) {
        
        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
        
    /* GPL'ed SoftWoehr announces itself. */
        System.out.println("Exceptions, Copyright (C) 1988 Jack J. Woehr.");
        System.out.println("Exceptions comes with ABSOLUTELY NO WARRANTY;");
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
}                                                     /* End of Exceptions class*/

/*  End of Exceptions.java */
