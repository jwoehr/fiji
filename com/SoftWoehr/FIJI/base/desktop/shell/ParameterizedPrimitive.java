/* ParameterizedPrimitive.java ...  */
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

/** ParameterizedPrimitive is an on-the-fly Primitive which
 * we declare to provide special runtimes to be compiled by
 * special compilation semantics, e.g., the runtime of
 * compiled 'to'.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.3 $
 */
public class ParameterizedPrimitive extends Primitive implements SoftWoehr, verbose {
    
    /** Revision level */
    private static final String rcsid = "$Id: ParameterizedPrimitive.java,v 1.3 2001-09-15 04:29:56 jwoehr Exp $";
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
    
    /** The object that this prim implicitly operates upon. */
    private Object myObject;
    private Class  myObjectClass;
    
    /** The primitive for a branch. */
    public static class Branch extends ParameterizedPrimitive {
        /**
         * @param name name of prim
         * @param methodName method prim executes
         * @param delta branch offset
         * @throws ClassNotFoundException res ipse loq
         * @throws NoSuchMethodException res ipse loq  */
        public Branch( String name
        , String methodName
        , int delta
        )
        throws java.lang.ClassNotFoundException
        , java.lang.NoSuchMethodException {
            super(name, methodName, new Integer(delta), engine.cInteger);
        }
    }                                                  /* End of Branch class*/
    
    /** The primitive for an unconditional branch. */
    public static class UnconditionalBranch extends Branch {
        /** Construct an unconditional branch.
         * @param delta branch offset
         * @throws ClassNotFoundException res ipse loq
         * @throws NoSuchMethodException res ipse loq  */
        public UnconditionalBranch(int delta)
        throws java.lang.ClassNotFoundException
        , java.lang.NoSuchMethodException {
            super( "unconditionalBranch"
            , "doUnconditionalBranch"
            , delta
            );
        }
        
        /** Return string representation of the primitive.
         * @return string rep
         */
        public String toString() {
            Object  o = super.getObject();
            Integer i = (Integer) o;
            String s = "An UnconditionalBranch of " + i;
            return s;
        }
    }                                     /* End of UnconditionalBranch class*/
    
    /** The primitive for a conditional branch. */
    public static class ConditionalBranch extends Branch {
        /** Construct a conditional branch.
         * @param delta branch offset
         * @throws ClassNotFoundException res ipse loq
         * @throws NoSuchMethodException res ipse loq
         */
        public ConditionalBranch(int delta)
        throws java.lang.ClassNotFoundException
        , java.lang.NoSuchMethodException {
            super( "conditionalBranch"
            , "doConditionalBranch"
            , delta
            );
        }
        
        /** Return string representation of the primitive.
         * @return string rep
         */
        public String toString() {
            Object  o = super.getObject();
            Integer i = (Integer) o;
            String s = "A ConditionalBranch of " + i;
            return s;
        }
    }                                       /* End of ConditionalBranch class*/
    
    /** The primitive for a literal. */
    public static class Literal extends ParameterizedPrimitive {
        /** Construct a literal for any object.
         * @param o the literalized obj
         * @throws ClassNotFoundException res ipse loq
         * @throws NoSuchMethodException res ipse loq  */
        public Literal(Object o)
        throws java.lang.ClassNotFoundException
        , java.lang.NoSuchMethodException {
            super ( "(literal)"
            , "doLiteral"
            , o
            , engine.cObject
            );
        }
        
        /** Return string representation of the primitive.
         * @return  string rep
         */
        public String toString() {
            Object  o = super.getObject();
            String s = "A Literal for " + o;
            return s;
        }
    }                                                 /* End of Literal class*/
    
    /** The primitive for a do. The Integer is the "leave" offset
     * in the Definition.
     */
    public static class Do extends ParameterizedPrimitive {
        /** Instance a prim for setting up a loop.
         * @param offset branch offset
         * @throws ClassNotFoundException res ipse loq
         * @throws NoSuchMethodException res ipse loq  */
        public Do   (int offset)   /* Offset in definition at which 'do' occurs*/
        throws java.lang.ClassNotFoundException
        , java.lang.NoSuchMethodException {
            super("(do)", "doDo", new Integer(offset), engine.cInteger);
        }
    }                                                      /* End of Do class*/
    
    /** The primitive for a loop. */
    public static class Loop extends ParameterizedPrimitive {
        /** Instance a prim for looping by one.
         * @param delta branch offset
         * @throws ClassNotFoundException res ipse loq
         * @throws NoSuchMethodException res ipse loq  */
        public Loop   (int delta)
        throws java.lang.ClassNotFoundException
        , java.lang.NoSuchMethodException {
            super("(loop)", "loop", new Integer(delta), engine.cInteger);
        }
    }                                                    /* End of Loop class*/
    
    /** The primitive for a +loop. */
    public static class PlusLoop extends ParameterizedPrimitive {
        /** Instance a prim for looping by some increment.
         * @param delta branch offset
         * @throws ClassNotFoundException res ipse loq
         * @throws NoSuchMethodException res ipse loq  */
        public PlusLoop   (int delta)
        throws java.lang.ClassNotFoundException
        , java.lang.NoSuchMethodException {
            super("(+loop)", "plusLoop", new Integer(delta), engine.cInteger);
        }
    }                                                    /* End of Loop class*/
    
    /** Arity/4 ctor, passing in a name for the primitive,
     * a method name to resolve for class engine, an Object
     * (possibly null) to be the datum for this primitive instance,
     * and an object class referring to the datum to allow validation
     * of dynamically initialized ParameterizedPrimitive's.
     * @param name name of the prim
     * @param methodName method prim executes
     * @param object parameter of method
     * @param objectClass class of method
     * @throws ClassNotFoundException res ipse loq
     * @throws NoSuchMethodException res ipse loq res ipse loq
     */
    public ParameterizedPrimitive(String name
    ,String methodName
    ,Object object
    ,Class objectClass
    )
    throws java.lang.ClassNotFoundException
    , java.lang.NoSuchMethodException {
        reinit(name, methodName, object, objectClass);
    }
    
    /** Reinitialize object setting name, method name, object, object class.
     * @param name name of the prim
     * @param methodName method prim executes
     * @param obj object parameter of method
     * @param objectClass class of method
     * @throws ClassNotFoundException res ipse loq
     * @throws NoSuchMethodException res ipse loq
     */
    public void reinit( String name
    , String methodName
    , Object obj
    , Class objectClass
    )
    throws java.lang.ClassNotFoundException
    , java.lang.NoSuchMethodException {
        myObject = obj;
        myObjectClass = objectClass;
        setName(name);
        Class signature[] = new Class[1];
        signature[0] = this.getClass();
        Class c = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.engine");
        method = c.getMethod(methodName, signature);     /* All Arity/0*/
    }
    
    /** Return string representation of object. Doesn't do any
     * checking for object validity.
     * @return  string rep
     */
    public String toString() {
        String s = "";
        s += "A ParameterizedPrimitive named " + getName();
        s += " with an Object of " + getObject().toString();
        s += " with a real Class of " + getObject().getClass().toString();
        s += " with a declared Class of " + getObjectClass().toString();
        return s;
    }
    
    /** The ParameterizedPrimitive notifies subcomponents of shutdown then shuts itself down.
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
    
    /** Reinitialize the ParameterizedPrimitive, discarding previous state. */
    // public void reinit() {
    //  }
    
    /** Invoke engine method on has'd Object.
     * @param e  associated engine
     */
    public void execute(engine e) {
        Object argArray[] = new Object[1];
        argArray[0] = this;
        try {
            method.invoke(e, argArray);
        }                                                          /* End try*/
        catch (Exception ex) {                                           /* ! Clean this up later!*/
            ex.printStackTrace(System.err);
        }                                                        /* End catch*/
    }
    
    /** Get the object this paramprim operates on implicitly.
     * @return  the object
     */
    public Object getObject() {
        return myObject;
    }
    
    /** Set the object this paramprim operates on implicitly.
     * @param o object value
     */
    public void setObject(Object o) {
        myObject = o;
    }
    
    /** Get the Class this paramprim operates on implicitly.
     * @return type of param
     */
    public Class getObjectClass() {
        return myObjectClass;
    }
    
    /** Set the Class this paramprim operates on implicitly.
     * @param o type of param
     */
    public void setObjectClass(Class o) {
        myObjectClass = o;
    }
    
    /** Validate that object and object class are instanced
     * and that object class equals declared object class.
     * The validation check presumes the programmer was right,
     * i.e., that the declared object class is what is intended.
     * This method validate() thus only guards against runtime
     * non-instantiation or instantiation with the wrong type
     * of object. Cf. engine.runtimeTo() which calls validate()
     * but doesn't compare to class Value because it's assumed
     * that the code creating the primitive knew what class was meant.
     * @return true .iff valid
     */
    public boolean validate() {
        boolean result = true;
        if (null == myObject || null == myObjectClass) {
            result = false;
        }                                                           /* End if*/
        else {
            result= myObject.getClass().equals(myObjectClass);
        }
        return result;
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
    
    /** Demonstrate <code>ParameterizedPrimitive</code>.
     * @param argv  args to main -- not used
     */
    public static void main(String argv[]) {
        
        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
        //    ParameterizedPrimitive theParameterizedPrimitive = new ParameterizedPrimitive();         /* Instance of ParameterizedPrimitive we're demoing.  */
        
    /* GPL'ed SoftWoehr announces itself. */
        System.out.println("ParameterizedPrimitive, Copyright (C) 1999, 2000 by Jack J. Woehr.");
        System.out.println("ParameterizedPrimitive comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");
        
    /* See if user passed in the -v flag to request verbosity. */
        for (int i = 0; i < myArgs.optionCount() ; i++) {
            if (myArgs.nthOption(i).getOption().substring(1,2).equals("v")) {
                //      theParameterizedPrimitive.setVerbose(true);
            }                                                         /* End if*/
        }
        
        // Your code goes here.
        // -------------------
        
        // -------------------
        
        return;
    }
}                                                      /* End of ParameterizedPrimitive class*/

/*  End of ParameterizedPrimitive.java */
