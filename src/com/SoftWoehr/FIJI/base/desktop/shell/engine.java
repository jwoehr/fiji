/* engine.java ...  an execution engine.     */
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

import java.io.*;
import java.util.*;
import java.lang.reflect.*;

import com.SoftWoehr.SoftWoehr;
import com.SoftWoehr.util.*;

/** The execution engine for our interaction.
 * An instance of com.SoftWoehr.FIJI.base.desktop.shell.interpreter
 * creates itself an instance of com.SoftWoehr.FIJI.base.desktop.shell.engine.
 * The engine is the guts of the shell's work and contains the bodies
 * of the com.SoftWoehr.FIJI.base.desktop.shell.Primitive instances of primitive
 * interpreter functions, since the engine knows how to carry out
 * these tasks.
 *
 *<p>The engine possess an object stack where each shell function
 * gets its parameters and deposits its results.
 *
 *<p>The descriptions of the methods below often contains a Forth-like
 * stack diagram referring to the effect of the operation on the object
 * stack maintained by the engine.
 * @author $Author: jwoehr $
 * @version $Revision: 1.1 $
 */
public class engine implements SoftWoehr, verbose {
    
    /** Interpreting, not compiling. */
    public static final boolean INTERPRETING = false;
    
    /** Compiling, not interpreting. */
    public static final boolean COMPILING = true;
    
    /** Revision level */
    private static final String rcsid = "$Id: engine.java,v 1.1 2016-11-06 21:20:38 jwoehr Exp $";
    
    /** Implements com.SoftWoehr.SoftWoehr
     * @return The RCS id
     */
    public String rcsId() {return rcsid;}
    
    /**  Flags whether we are in verbose mode. */
    public boolean isverbose = false;
    
    /**  Helper for verbose mode. */
    private verbosity v = new verbosity(this);
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cObject    ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cBoolean   ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cString    ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cLong      ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cInteger   ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cClass     ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cJavaParam ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cVariable  ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cValue     ;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cDefinition;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cParameterizedPrimitive;
    
    /** Reference to a Class object for comparisons to avoid
     * frequent calls to Class.forName()
     */
    public static Class cLiteral;
    
    /** Init the static class references. */
    static {
        try {
            cString    = Class.forName("java.lang.String");
            cLong      = Class.forName("java.lang.Long");
            cInteger   = Class.forName("java.lang.Integer");
            cClass     = Class.forName("java.lang.Class");
            cBoolean   = Class.forName("java.lang.Boolean");
            cObject    = Class.forName("java.lang.Object");
            cJavaParam = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.JavaParam");
            cVariable  = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.Variable");
            cValue     = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.Value");
            cDefinition= Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.Definition");
            cParameterizedPrimitive =
            Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.ParameterizedPrimitive");
            cLiteral =   /* Since name resolution by forName() won't work this one.*/
            new ParameterizedPrimitive.Literal("").getClass();
        }                                                            /* End try*/
        catch (Exception e) {
            e.printStackTrace(System.err);
        }                                                          /* End catch*/
    }
    
    /** Object stack. */
    public Stack stack;
    
    /** Inner interpreter is the Definition interpreter.
     * The inner interpreter has the return stack and
     * the current definition under interpretation.
     */
    public InnerInterpreter innerInterpreter;
    
    /** A ref to the current definition under composition. The
     * previous currentDefinition is pushed on the control
     * flow stack.
     */
    private Definition currentDefinition;
    
    /** Stack for unfinished Definitions and unresolved branches. */
    public Stack controlFlowStack;
    
    /** The input interpreter with which this engine is associated.*/
    public interpreter myInterpreter;
    
    /** Compilation state */
    public boolean state;
    
    /** The search order */
    public com.SoftWoehr.FIJI.base.desktop.shell.SearchOrder searchOrder;
    
    /** The current wordlist to which new defs are added. */
    public Wordlist currentWordlist;
    
    /** Arity/0 ctor. Initializes the engine cold. Does not
     * associate the engine with an input interpreter.
     */
    public engine() {
        cold();
    }
    
    /** Open engine on an input interpreter and initialize cold.
     * @param i the associated input interpreter
     */
    public engine(interpreter i) {
        this();
        myInterpreter = i;
    }
    
    /** shutdown() here does nothing.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return Always returns 0
     */
    public int shutdown() { return 0; }
    
    /** Reinit engine like just came up. */
    public void cold() {
        warm();
        searchOrder = new SearchOrder();
        Wordlist w = Wordlist.defaultWordlist();
        searchOrder.add(w);
        currentWordlist = searchOrder.nthElement(0);                    /* == w*/
    }
    
    /** Reinit engine but preserve some state. */
    public void warm() {
        stack = new Stack();
        innerInterpreter = new InnerInterpreter(this);
        controlFlowStack = new Stack();
        state = INTERPRETING;
        if (null != myInterpreter) {     /* Interpreter calls engine.warm() when it wants to reset self.*/
            myInterpreter.warmReset();
        }                                                           /* End if*/
    }
    
    /** Set state INTERPRETING/COMPILING.
     * @param state <CODE>engine.INTERPRETING</CODE> if interpreting; <CODE>engine.COMPILING</CODE> if compiling.
     */
    public void setState(boolean state) {
        this.state = state;
    }
    
    /** Get state INTERPRETING/COMPILING.
     * @return state <CODE>engine.INTERPRETING</CODE> if interpreting; <CODE>engine.COMPILING</CODE> if compiling.
     */
    public boolean getState() {
        return state;
    }
    
    /** Returns verbosity of the debugging output.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return <CODE>true</CODE> iff verbose.
     */
    public boolean isVerbose() {
        return isverbose;
    }
    
    /** Sets verbosity of the debugging output.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param tf <CODE>true</CODE> iff verbose.
     */
    public void    setVerbose  (boolean tf) {
        isverbose = tf;
        searchOrder.setVerbose(isVerbose());
        innerInterpreter.setVerbose(isVerbose());
    }
    
    /** Outputs to verbose stream if verbose.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s Message to announce
     */
    public void    announce    (String s)   {v.announce(s);   }
    
    /** Find a semantic by name in one of the wordlists
     * in the array of same currently searched by this engine.
     * Return either the semantic or 'null'.
     * @param name name of the semantic
     * @return The Semantic object of that name.
     */
    public Semantic findSemantic(String name) {
        return searchOrder.find(name);
    }
    
    /** Push a reference to an object onto the stack.
     * @param o The obj to push
     */
    public void push(Object o) {
        stack.push(o);
    }
    
    /** Pop a reference to an object from the stack.
     * @return The obj popped.
     */
    public Object pop() {
        return stack.pop();
    }
    
    /** Peek at TOS.
     * @return The object from the top of the stack, which still resides there.
     */
    public Object peek() {
        return stack.peek();
    }
    
    /********************************/
  /* Here are the primitives.     */
  /* These all work on the stack. */
    /********************************/
    
    /** ARF */
    public void arf() {
        myInterpreter.output("\nHi there from arf!\n");
    }
    
    /** noop           --  */
    public void noop() {
    }
    
    /** depth      xn .. x1 -- n */
    public void depth() {
        push(new Long(stack.size()));
    }
    
    /** dup          o -- o o
     * @throws StackUnderflow If stack is empty.
     */
    public void dup()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow {
        if (stack.size() > 0) {
            push(stack.peek());
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }
    
    /** drop          o --
     * @throws StackUnderflow If stack is empty.
     */
    public void drop()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow {
        if (stack.size() > 0) {
            stack.pop();
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }
    
    /** swap      o1 o2 -- o2 o1
     * @throws StackUnderflow If stack is empty.
     */
    public void swap()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow {
        int sz = stack.size();
        if (sz > 1) {
            stack.insertElementAt(stack.pop(), sz - 2);
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }
    
    /** over        o1 o2 -- o1 o2 o1
     * @throws StackUnderflow If stack is too shallow for operation.
     */
    public void over()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow {
        int sz = stack.size();
        if (sz > 1) {
            push(stack.elementAt(sz - 2));
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }
    
    /** rot        o1 o2 o3 -- o2 o3 o1
     * @throws StackUnderflow If stack is too shallow for operation.
     */
    public void rot()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow {
        int sz = stack.size();
        if (sz > 2) {
            Object o = stack.elementAt(sz - 3);
            stack.removeElementAt(sz - 3);
            push(o);
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }
    
    /** roll        om .. o((m-n)+1) om-n  m -- .. o((m-n)+1) om
     * @throws StackUnderflow If stack is too shallow for operation.
     */
    public void roll()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow {
        int sz = stack.size();
        if (sz > 0) {
            int item = ((Long)pop()).intValue();
            sz = stack.size();
            if (sz > item) {
                Object o = stack.elementAt((sz - 1) - item);
                stack.removeElementAt((sz - 1) - item);
                push(o);
            }
            else {
                String s = "Stack under.";
                announce(s);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
            }                                                         /* End if*/
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }                                                /* public void roll ()*/
    
    /** pick      om .. om-n m -- om .. om-n om
     * @throws StackUnderflow If stack is too shallow for operation.
     */
    public void pick()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow {
        int sz = stack.size();
        if (sz > 0) {
            int item = ((Long)pop()).intValue();
            sz = stack.size();
            if (sz > item) {
                Object o = stack.elementAt((sz - 1) - item);
                push(o);
            }
            else {
                String s = "Stack under.";
                announce(s);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
            }                                                         /* End if*/
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }
    
    /** .s        o1 .. oN -- o1 .. oN */
    public void dot_s() {
        if (stack.size() > 0) {
            for (Enumeration e = stack.elements(); e.hasMoreElements();) {
                Object o = e.nextElement();
                if (null != o) {
                    myInterpreter.output("`" + o + "` ");
                }
                else {
                    myInterpreter.output("`null` ");
                }                                                       /* End if*/
                
            }
        }
        else {
            myInterpreter.output("Empty stack. ");
        }
    }
    
    /** .c        o1 .. oN -- o1 .. oN */
    public void dot_c() {
        if (stack.size() > 0) {
            for (Enumeration e = stack.elements(); e.hasMoreElements();) {
                Object o = e.nextElement();
                if (null != o) {
                    myInterpreter.output("`" + o.getClass() + "` ");
                }
                else {
                    myInterpreter.output("`null` ");
                }                                                       /* End if*/
            }
        }
        else {
            myInterpreter.output("Empty stack. ");
        }
    }
    
    /** Primitive to display all stack entries and their classes.
     * .sc        o1 .. oN -- o1 .. oN
     */
    public void dot_sc() {
        if (stack.size() > 0) {
            for (Enumeration e = stack.elements(); e.hasMoreElements();) {
                Object o = e.nextElement();
                if (null != o) {
                    myInterpreter.output("`" + o + " /*" + o.getClass() + "*/` ");
                }
                else {
                    myInterpreter.output("`null` ");
                }                                                       /* End if*/
            }
        }
        else {
            myInterpreter.output("Empty stack. ");
        }
    }
    
    /** .         o1 -- */
    public void dot() {
        if (stack.size() > 0) {
            Object o = pop();
            if (null != o) {
                myInterpreter.output(o.toString() + " ");
            }
            else {
                myInterpreter.output("`null` ");
            }                                                         /* End if*/
        }
        else {
            myInterpreter.output("Empty stack. ");
        }                                                           /* End if*/
    }
    
    /** Display destructively all stack entries.
     * ..  o1 oN --
     */
    public void dotdot() {
        String s = "";
        Object o = null;
        if (stack.size() > 0) {
            while (stack.size() > 0) {
                o = stack.pop();
                if (null != o) {
                    s = o.toString();
                }
                else {
                    s = "`null`";
                }                                                       /* End if*/
                myInterpreter.output(s + " ");
            }                                                      /* End while*/
        }                                                           /* End if*/
        else {
            myInterpreter.output("Empty stack. ");
        }                                                           /* End if*/
    }                                                           /* dotdot()*/
    
    /** Print out the inner interpreter state.
     * .r --        R: --
     */
    public void dot_r() {
        myInterpreter.output(innerInterpreter.toString());
    }
    
    /** Trigger a quit of the input interpreter loop
     * quit      o1 ... oN -- o1 ... oN
     */
    public void quit() {
        myInterpreter.setQuitFlag(true);
    }
    
    /** Turn a name into a class.
     * class     s1 -- c1
     * @throws StackUnderflow if no name on stack
     * @throws NotClassName if name on stack not a class name
     * @throws NotClassInstance of tos is a builtin type and can't be tested for String-ness */
    public void classForName()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassInstance {
        if (stack.size() > 0) {
            try {
                if (peek().getClass() == cString) {
                    String name = (String) pop();
                    try {
                        push(Class.forName(name));
                    }                                                    /* End try*/
                    catch (Exception e) {
                        String s = name + " is not a class name.";
                        announce(s);
                        e.printStackTrace(System.err);
                        throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName(s, e);
                    }                                                  /* End catch*/
                }
                else {
                    pop();
                    push(null);
                }                                                       /* End if*/
            }                                                        /* End try*/
            catch (Exception e) {
                String s = "classForName() couldn't getClass() on top-of-stack item.";
                e.printStackTrace(System.err);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassInstance(s, e);
            }                                                      /* End catch*/
        }                                                           /* End if*/
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }                                              /* End of classForName()*/
    
    /** Convert an arg to a JavaParam with the class signature set.
     * ()        o stringClassName|Class -- javaParam
     * with special handling for the primitive type names:
     * <ul>
     * <li>boolean</li>
     * <li>byte</li>
     * <li>char</li>
     * <li>double</li>
     * <li>float</li>
     * <li>int</li>
     * <li>long</li>
     * <li>short</li>     
     * </ul>
     * @throws NotClassName If argument not identifiable as a class or class name.
     */
    public void castParam()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName {
        Object o = peek();
        Class c = null;
        try {                                   /* Is TOS a Class object already?*/
            if (o.getClass() == cClass) {
                c = (Class) pop();                                  /* Yes, use it.*/
            }
	      /* TOS is presumed a string naming a class or a primitive type */
            else if (o.toString().equals("boolean")) {
		    pop(); c = java.lang.Boolean.TYPE;
	    } else if (o.toString().equals("byte")) {
		    pop(); c = java.lang.Byte.TYPE;
	    } else if (o.toString().equals("char")) {
		    pop(); c = java.lang.Character.TYPE;
	    } else if (o.toString().equals("double")) {
		    pop(); c = java.lang.Double.TYPE;
	    } else if (o.toString().equals("float")) {
		    pop(); c = java.lang.Float.TYPE;
	    } else if (o.toString().equals("int")) {
		    pop(); c = java.lang.Integer.TYPE;
	    } else if (o.toString().equals("long")) {
		    pop(); c = java.lang.Long.TYPE;
	    } else if (o.toString().equals("short")) {
		    pop(); c = java.lang.Short.TYPE;
	    } else {
                classForName();         /* TOS presumed to be string name of class.*/
                c = (Class) pop();                         /* Grab resultant class.*/
            }                                                         /* End if*/
        }                                                          /* End try*/
        catch (Exception e) {
      /* The only thing likely to throw above is classForName() */
            String s = "castParam() couldn't classForName() on top-of-stack item.";
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName(s, e);
        }                                                        /* End catch*/
        push(new JavaParam(pop(), c));                   /* Create a JavaParam.*/
    }                                                 /* End of castParam()*/
    
    /** Convert a reflection type to its primitive type.
     * This is necessary for methods type as 'int', etc.
     * primitive   class1 -- class2
     * @throws StackUnderflow If stack empty
     * @throws NonReflectedType If type argument indicates a non-reflected type.
     */
    public void classToPrimitiveType()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonReflectedType {
        if (stack.size() > 0) {
            Class c = (Class) pop();
            try {
                Field f = c.getField("TYPE");
                push(f.get(c));
            }                                                        /* End try*/
            catch (Exception e) {
                String s = "classToPrimitiveType() found a  non-reflected type.";
                announce(s);
                e.printStackTrace(System.err);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonReflectedType(s, e);
            }                                                      /* End catch*/
        }
        else {
            String s = "Stack under.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow(s, null);
        }                                                           /* End if*/
    }
    
    /** Convert a stack entry to the class object of its primitive type.
     * This had rather better be coded as a definition than as a Forth prim.
     * >primitive     o -- c
     * @throws StackUnderflow If stack empty.
     * @throws NonReflectedType If type argument indicates non-reflected type.
     */
    public void  stackEntryToPrimitive()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonReflectedType {
        getStackEntryClass();
        classToPrimitiveType();
    }
    
    /** Create a JavaParam with the object characterized
     * as its primitive type. Again, this were better
     * a definition than a Forth prim.
     * (primitive)    o1 -- javaParam
     * @throws StackUnderflow If stack empty.
     * @throws NonReflectedType If type argument indicates non-reflected type.
     * @throws NotClassName  If argument not a class name.
     */
    public void stackEntryToPrimParam()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonReflectedType
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName {
        dup();
        stackEntryToPrimitive();
        castParam();
    }
    
    /** Create a JavaParam for an Integer characterized
     * as an int from the typical Long stack entry.
     * This had rather better be coded as a definition than as a Forth prim.
     * Long>int      L -- javaParam
     * @throws NonReflectedType If type argument indicates non-reflected type.
     * @throws StackUnderflow  If stack empty.
     * @throws NotClassName   If argument not a class name.
     */
    public void longToIntParam()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonReflectedType
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName {
        push(new Integer(((Long)pop()).intValue()));
        stackEntryToPrimParam();
    }
    
    /** FIND in the searchOrder.
     * find   s -- semantic
     */
    public void find() {
        push(findSemantic((String)pop()));
    }
    
    /** Execute a semantic from the stack.
     * execute   x1 .. xn semantic -- ??
     * @throws BadPrimitiveExecute If execution of primitive fails.
     * @throws BadDefinitionExecute If execution of definition fails.
     */
    public void execute()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute
    
    {
        ((Semantic)pop()).execute(this);
    }
    
    /** Compile a semantic from the stack.
     * compile   semantic --
     * @throws BadPrimitiveCompile If compile of primitive fails.
     * @throws BadDefinitionCompile  If compile of definition fails.
     * @throws BadPrimitiveExecute If execution of primitive fails.
     * @throws BadDefinitionExecute If execution of definition fails.
     */
    public void compile()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        ((Semantic)pop()).compile(this);
    }
    
    /** Temporarily unset compile state.
     * Shouldn't be used unless a definition
     * is under composition.
     */
    public void leftBracket() {
        setState(INTERPRETING);
    }
    
    /** Set compile state. Shouldn't be used
     * unless a definition is under composition.
     */
    public void rightBracket() {
        setState(COMPILING);
    }
    
    /** Test input base.
     * base? -- L
     */
    public void pushBase() {
        push(new Long(myInterpreter.getBase()));
    }
    
    /** Set input base.
     * base L --
     */
    public void popBase() {
        myInterpreter.setBase(((Long)pop()).intValue());
    }
    
    /** Test compile state.
     * state   --- t|f
     */
    public void doState() {
        push(new Boolean(getState()));
    }
    
    /** Makes current definition immediate.
     */
    public void setCurrentImmediate() {
        currentDefinition.setImmediate(true);
    }
    
    /** Tests Definition on top of stack for immediacy.
     */
    public void isImmediate() {
        push(new Boolean(((Definition)pop()).getImmediate()));
    }
    
    /** >class   o1 -- c1 */
    public void getStackEntryClass() {
        push(pop().getClass());
    }
    
    /** Transform a stack entry into its representative string.
     * toString      o1 -- s1
     */
    public void stackEntryToString() {
        push(pop().toString());
    }
    
    /** Push a javaArgs object to accumulate arguments.
     * (      -- javaArgs
     */
    public void javaArgs() {
        push(new JavaArgs());
    }
    
    /** Pop an object off the stack and add it to the
     * JavaArgs vector underneath. Converts it to
     * a JavaParam if it's not one already. The reason
     * it would be one already would be that it had
     * been converted by castParam().
     * ,     o --
     */
    public void accumulateArg() {
        Object o = pop();  /* The actual argument, possibly already a JavaParam*/
        Class c = null;
        
        if (o.getClass() == cJavaParam) {                   /* If it's already a JavaParam, accumulate as-is.*/
            ((JavaArgs) peek()).addElement(o);
        }
        else                 /* It's not yet a JavaParam, accumulate it as one.*/ {
            ((JavaArgs) peek()).addElement(new JavaParam(o));
        }                                                           /* End if*/
    }
    
    /** Resove and make the call.
     * )     o1 methodname  javaArgs -- o2
     */
    public void callJava() {
        JavaArgs javaArgs = (JavaArgs) pop();
        String methodName = (String)   pop();
        Object o          = pop();
        if (methodName.equals("new")) {
            callJavaConstructor(o, javaArgs);
        }
        else {
            callJavaMethod(o, methodName, javaArgs);
        }                                                           /* End if*/
    }                                                     /* End callJava()*/
    
    /** Resolve and call a method, not a constructor.
     * @param o The object on which to call the (static or instance) method.
     * @param methodName Name of method.
     * @param javaArgs The arg array to pass to the method.
     */
    public void callJavaMethod(
    Object o
    , String methodName
    , JavaArgs javaArgs
    ) {
        Method method     = null;          /* The method we'll invoke.         */
        try {                              /* Resolve the method.*/
            if (cClass == o.getClass()) {  /* It's a class object, not an instance object.*/
                try {                       /* Try to resolve it as an object method.*/
                    method     =
                    o.getClass().getMethod(methodName, javaArgs.toClassArray());
                }                                                      /* End try*/
                catch (java.lang.NoSuchMethodException e) {     /* Oops, wasn't an object method. Try it as a class method.*/
                    method     =                              /* Try a static method.*/
                    ((Class)o).getMethod(methodName, javaArgs.toClassArray());
                }                                                    /* End catch*/
            }
            else                  /* It's an instance object, not a class object.*/ {
                method     =
                o.getClass().getMethod(methodName, javaArgs.toClassArray());
            }                                                         /* End if*/
            Object oo = method.invoke(o, javaArgs.toObjectArray());  /* invoke   */
            push(oo);
        }                                                          /* End try*/
        catch (Exception e) {
            e.printStackTrace(System.err);
        }                                                        /* End catch*/
    }                                                 /* End callJavaMethod*/
    
    /** Resolve and call a constructor, not a method.
     * @param o The object on which to call the constructor.
     * @param javaArgs The arg array to pass to the method.
     */
    public void callJavaConstructor( Object o
    , JavaArgs javaArgs
    ) {
        Constructor constructor = null;    /* The constructor we'll invoke.    */
        
        try                                          /* Resolve the constructor*/ {
            constructor  =
            ((Class) o).getConstructor(javaArgs.toClassArray());
        }                                                          /* End try*/
        catch (Exception e) {
            e.printStackTrace(System.err);
        }                                                        /* End catch*/
        try {                                               /* Execute the call*/
            push(constructor.newInstance(javaArgs.toObjectArray()));
        }
        
        catch (Exception e) {
            e.printStackTrace(System.err);
        }                                                        /* End catch*/
    }                                                 /* End callJavaMethod*/
    
    /** Return a java.lang.Field for an object instance.
     * @param o  The object whose field to find.
     * @param fieldName Name of field to find.
     * @throws NoSuchFieldException Field doesn't exist.
     * @return The Field instance.
     */
    public Field findField(Object o, String fieldName)
    throws java.lang.NoSuchFieldException {
        Class c;                         /* The class we'll find the field for.*/
        Class x = o.getClass();         /* To check if this is a class already.*/
        if (x == cClass) {
            c = (Class) o;                           /* It is, just use it as is.*/
        }
        else {
            c = x;                     /* It's an instance, use the gotten class.*/
        }                                                           /* End if*/
        Field f = c.getField(fieldName);   /* Find the field name.             */
        return f;
    }                 /* public Field findField(Object o, String fieldName)*/
    
    /** Push next lexeme as a string literal.
     * '   TIB: lexeme --      -- s
     */
    public void lexeme() {
        push(myInterpreter.nextLexeme());
    }
    
    /** Grab a lexeme delimited specifically.
     * This being Arity/1 can't be a primitive,
     * which are all Arity/0.
     * @param delimiters Delimiters for the tokenizer.
     */
    public void lexeme(String delimiters) {
        push(myInterpreter.nextLexeme(delimiters));
    }
    
    /** Grab a lexeme delimited specifically,
     * possibly consuming the delimiter in the process.
     * This being Arity/1 can't be a primitive,
     * which are all Arity/0.
     * @param delimiters Delimiters for the tokenizer.
     * @param consumeDelim <CODE>true</CODE> if delimiter is consumed in the parsing.
     */
    public void lexeme(String delimiters, boolean consumeDelim) {
        push(myInterpreter.nextLexeme(delimiters, consumeDelim));
    }
    
    /** Parse a string up to the next doublequote or EOL
     * "      lexeme --   -- s
     */
    public void doubleQuote() {
        lexeme("\"\n\r", true);
    }
    
    /** Parse a string up to the next backtick or EOL
     * `      lexeme --   -- s
     */
    public void backTick() {
        lexeme("`\n\r", true);
    }
    
    /** Parse and discard text between backslashes. */
    public void comment() {
        lexeme("\\", true);
        pop();
    }
    
    /** Signal interpreter that bye is requested. */
    public void bye() {
        myInterpreter.setKillFlag(true);
    }
    
    /** System exit. */
    public void sysexit() {
        int rc = ((Long)pop()).intValue();
        System.exit(rc);
    }
    
    /** Leave boolean true on stack */
    public void pushTrue() {
        push(new Boolean(true));
    }
    
    /** Leave boolean false on stack */
    public void pushFalse() {
        push(new Boolean(false));
    }
    
    /** Leave null on stack */
    public void pushNull() {
        push(null);
    }
    
    /**************************/
  /* Comparison and Logical */
    /**************************/
    
    /** Invert the boolean on top of stack. */
    public void not() {
        push( new Boolean(
        ((Boolean) pop()).booleanValue() == false
        )
        );
    }                                                 /* public void not ()*/
    
    /** AND two booleans on top of stack. */
    public void and() {
        boolean lhs = ((Boolean) pop()).booleanValue();
        boolean rhs = ((Boolean) pop()).booleanValue();
        push(new Boolean(lhs && rhs));
    }                                                 /* public void and ()*/
    
    /** OR two booleans on top of stack. */
    public void or() {
        boolean lhs = ((Boolean) pop()).booleanValue();
        boolean rhs = ((Boolean) pop()).booleanValue();
        push(new Boolean(lhs || rhs));
    }                                                  /* public void or ()*/
    
    /** XOR two booleans on top of stack. */
    public void xor() {
        boolean lhs = ((Boolean) pop()).booleanValue();
        boolean rhs = ((Boolean) pop()).booleanValue();
        push(new Boolean(lhs ^ rhs));
    }                                                 /* public void xor ()*/
    
    /** Compare two objects for equality. */
    public void isEqual() {
        Object a = pop();
        Object b = pop();
        if (a == null || b == null) {
            if (a == null && b == null) {
                push(new Boolean(true));
            }
            else {
                push(new Boolean(false));
            }                                                         /* End if*/
        }
        else {
            push(new Boolean(a.equals(b)));
        }                                                           /* End if*/
    }                                             /* public void isEqual ()*/
    
    /** Compare two objects for inequality. */
    public void isUnequal() {
        Object a = pop();
        Object b = pop();
        if (a == null || b == null) {
            if (a == null && b == null) {
                push(new Boolean(false));
            }
            else {
                push(new Boolean(true));
            }                                                         /* End if*/
        }
        else {
            push(new Boolean(!a.equals(b)));
        }                                                           /* End if*/
    }                                           /* public void isUnequal ()*/
    
    /** Compare two numbers (i.e., java.lang.Long's) or
     * two strings for greater-than.
     * > o1 o2 -- Boolean
     */
    public void greaterThan() {
        Object o = pop();
        final Class c  = o.getClass();
        if (c == cString) {
            push( new Boolean(0
            <
            ((String) pop()).compareTo((String) o)
            ));
        }
        else if (c == cLong) {
            push( new Boolean( ((Long)pop()).longValue()
            > ((Long)o)    .longValue()
            ));
        }
        else {
            // throw bAdAdD (if not both same class)
        }                                                           /* End if*/
    }                                         /* public void greaterThan ()*/
    
    /** Compare two numbers (i.e., java.lang.Long's) or
     * two strings for less-than
     * < o1 o2 -- Boolean
     */
    public void lessThan() {
        Object o = pop();
        final Class c  = o.getClass();
        if (c == cString) {
            push( new Boolean(0
            >
            ((String) pop()).compareTo((String) o)
            ));
        }
        else if (c == cLong) {
            push( new Boolean( ((Long)pop()).longValue()
            < ((Long)o)    .longValue()
            ));
        }
        else {
            // throw bAdAdD (if not both same class)
        }                                                           /* End if*/
    }                                         /* public void greaterThan ()*/
    
    /**************/
  /* Arithmetic */
    /**************/
    
    /** Add two numbers or two strings.
     * Should also work on ints, doubles, floats, etc., but doesn't yet.
     */
    public void add() {
        Object o = pop();
        final Class c  = o.getClass();
        if (c == cString) {
            push(new String((String) pop() + (String) o));
        }
        else if (c == cLong) {
            push(new Long(((Long)o).longValue() + ((Long)pop()).longValue()));
        }
        else {
            // throw bAdAdD (if not both same class)
        }                                                           /* End if*/
    }
    
    /** Multiply two longs.
     * *  l1 l2 -- l1/l2
     * Should work on some other classes.
     */
    public void mul() {
        Long multiplier   = (Long) pop();
        Long multiplicand = (Long) pop();
        push(new Long(multiplicand.longValue() * multiplier.longValue()));
    }
    
    /** Divide two longs.
     * /  l1 l2 -- l1/l2
     *  Should work on some other classes.
     */
    public void div() {
        Long divisor = (Long) pop();
        Long dividend = (Long) pop();
        push(new Long(dividend.longValue() / divisor.longValue()));
    }
    
    /** Subtract two longs.
     * -  l1 l2 -- (l1 - l2)
     *  Should work on some other classes.
     */
    public void sub() {
        Long subtrahend = (Long) pop();
        Long sum        = (Long) pop();
        push(new Long(sum.longValue() - subtrahend.longValue()));
    }
    
    /** Modulus of two longs.
     * %  l1 l2 -- l1%l2
     *  Should work on some other classes.
     */
    public void mod() {
        Long divisor = (Long) pop();
        Long dividend = (Long) pop();
        push(new Long(dividend.longValue() % divisor.longValue()));
    }
    
    /** Create a new java.lang.reflect.Array of one dimension.
     * array   Long StringClassname -- Array
     * -or-
     * array   Long ClassObject -- Array
     * @throws NonReflectedType If second-on-stack is non-reflected type.
     * @throws StackUnderflow If stack depth < 2.
     * @throws NotClassName If second-on-stack is not the String name of a Class.
     * @throws NotClassInstance If second-on-stack is not a class type. */
    public void array()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonReflectedType
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassInstance {
        Class aClass = null;
        Object o = peek();
        if (o.getClass() == cString)          /* It's a string name of a class.*/ {
            classForName();              /* Convert string to class on the stack.*/
        }
        
        aClass = (Class) pop();    /* class object for the type desired.*/
        
        int n = ((Long)pop()).intValue();  /* Convert long to Integer          */
        push(Array.newInstance(aClass, n));
    }
    
    /** Create a new java.lang.reflect.Array of n dimensions.
     * dimarray   L1 .. Ln nL StringClassname -- Array
     * -or-
     * dimarray   L1 .. Ln nL ClassObject -- Array
     * @throws NonReflectedType If second-on-stack is non-reflected type.
     * @throws StackUnderflow If stack depth < 2.
     * @throws NotClassName If second-on-stack is not the String name of a Class.
     * @throws NotClassInstance If second-on-stack is not a class type
     */
    public void dimarray()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonReflectedType
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.StackUnderflow
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassName
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NotClassInstance {
        Class aClass = null;
        Object o = pop();
        if (o.getClass() == cString)          /* It's a string name of a class.*/ {
            push(o);
            classForName();              /* Convert string to class on the stack.*/
            aClass = (Class)pop();
        }
        else {
            aClass = (Class) o;    /* It's the class object for the type desired.*/
        }                                                           /* End if*/
        int nDims = ((Long)pop()).intValue(); /* Convert Long to int: num dims.*/
        int dimArray [] = new int [nDims];
        for (int i = 0; i < nDims; ++i) {
            dimArray[i] = ((Long)pop()).intValue();
        }                                                          /* End for*/
        push(Array.newInstance(aClass, dimArray));
    }
    
    /**********************/
  /* Variables & Values */
    /**********************/
    
    /** Parse input for a valid name.
     * @throws BadName If no valid name found.
     * @return The name.
     */
    public String parseValidName()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName {
        String s = myInterpreter.nextLexeme();
        if (null != s) {
            if (s.equals("")) {
                String t = "Invalid null name offered.";
                announce(t);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName(t, null);
            }                                                         /* End if*/
        }
        else {
            String t = "Invalid empty String name offered.";
            announce(t);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName(t, null);
        }                                                           /* End if*/
        return s;
    }                                                   /* parseValidName()*/
    
    /** Create a new variable and add it to the current wordlist.
     * @throws BadName  if no valid name found.
     */
    public void newVariable()
    throws  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName {
        String s = parseValidName();
        currentWordlist.put(new Variable(s));
    }
    
    /** Store an object into a variable.
     * @throws NonVariable If object for store isn't a variable.
     */
    public void store()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonVariable {
        Object o = pop();
        if (o.getClass() == cVariable) {
            ((Variable) o).store(this);
        }
        else /* It's not a variable, assume it's an object field store request.*/ {
            String s =(String) pop();        /* Field name                       */
            try {
                Field f = findField(o, s);
                f.set(o, pop());                              /* set field contents*/
            }                                                        /* End try*/
            catch (Exception e) {
                String err = "Trouble setting field " + s + " of object " + o;
                announce(err);
                e.printStackTrace(System.err);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonVariable(s, null);
            }                                                      /* End catch*/
        }                                                           /* End if*/
    }
    
    /** Fetch an object from its storage in a variable.
     * @throws NonVariable If object for store isn't a variable.
     */
    public void fetch()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonVariable {
        Object o = pop();
        if (o.getClass() == cVariable) {
            ((Variable) o).fetch(this);
        }
        else /* It's not a variable, assume it's an object field fetch request.*/ {
            String s =(String) pop();        /* Field name                       */
            try {
                Field f = findField(o, s);
                push(f.get(o));
            }                                                        /* End try*/
            catch (Exception e) {
                String err = "Trouble getting field " + s + " of object " + o;
                announce(err);
                e.printStackTrace(System.err);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonVariable(s, null);
            }                                                      /* End catch*/
        }                                                           /* End if*/
    }
    
    /** Fetch a wordlist from the search order, converting it to a value. */
    
    /** Create a new value and add it to the current wordlist.
     * @throws BadName If no valid name found.
     */
    public void newValue()
    throws  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName {
        String s = parseValidName();
        currentWordlist.put(new Value(s));
    }
    
    /** Stuff a Value.
     * @throws NonValue If target is not a Value
     * @throws NameNotFound If name not found in wordlist(s).
     * @throws BadName If no valid name found.  */
    public void toValue()
    throws  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonValue
    ,  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NameNotFound
    ,  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName {
        parseValue().setDatum(pop());
    }
    
    /** Stuff a Value at runtime, a Value which we
     * tucked into an on-the-fly ParameterizedPrimitive.
     * @param p ParamterizedPrimitive to received value.
     * @throws NonValue  If p is a non-Value.
     */
    public void runtimeTo(ParameterizedPrimitive p)
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonValue {
        if (!p.validate()) {
            String s = " ParameterizedPrimitive posseses no valid Value.";
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonValue(s, null);
        }
        ((Value) p.getObject()).setDatum(pop());
    }
    
    /** Create a non-wordlist primitive on the fly to handle
     * compile-time use of "to".
     * @throws CompileToValue If the compile-to-value-operation fails.
     * @throws NonValue If target is not a Value
     */
    public void compileToValue()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.CompileToValue
    ,  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonValue {
        try {
            Value v = parseValue();
            new ParameterizedPrimitive("(to)"
            ,"runtimeTo"
            ,v
            ,cValue
            ).compile(this);
        }                                                          /* End try*/
        catch (Exception e) {
            String s = "Problem with compileToValue()";
            announce(s);
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.CompileToValue(s, e);
        }                                                        /* End catch*/
    }
    
    /** Grab the next lexeme, find it in the search order and determine
     * that it is a Value.
     * @throws NameNotFound If name not found in wordlist(s)
     * @throws NonValue If name does not represent a Value.
     * @throws BadName If no valid name found.
     * @return The Value.
     */
    public Value parseValue()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NameNotFound
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonValue
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName {
        String aName = parseValidName();
        Semantic aSemantic = findSemantic(aName);
        if (null == aSemantic) {
            String s = "Name " + aName + " not found.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NameNotFound(s, null);
        }                                                           /* End if*/
        
        if (aSemantic.getClass() != cValue) {
            String s = aName + " is not a Value.";
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.NonValue(s, null);
        }                                                           /* End if*/
        return (Value) aSemantic;
    }
    
    /*************/
  /* Compiling */
    /*************/
    
    /** Return the current definition under composition.
     * @return  the current definition. */
    public Definition getCurrentDefinition() {
        return currentDefinition;
    }
    
    /** Push the current definition under composition to the control flow
     * stack and make the new definition the current definition.
     * @param d the current definition.
     */
    public void pushCurrentDefinition(Definition d) {
        pushControl
        (new ControlFlowElement( getCurrentDefinition()/* null if no current def.*/
        , this
        , cDefinition   /* Class to be recognized as.*/
        ));
        currentDefinition = d;                    /* Set new current definition*/
    }
    
    /** An interpret-time action for compile-only words, it throws.
     * @throws CompileOnly If a compile-only Semantic is invoked outside compile mode.
     */
    public void compileOnly()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.CompileOnly {
        String s = "Interpretive use of a compile-only word.";
        announce(s);
        throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.CompileOnly(s, null);
    }
    
    /** Set up engine for compilation.
     * @param d The definition into which Semantics will be compiled.
     */
    public void commenceDefinition(Definition d) {
    /* Save current definition, usually null, on control flow stack. */
        pushCurrentDefinition(d);
        d.commence();                                   /* Init the definition.*/
        state = COMPILING;                     /* We'd need a state stack, too.*/
    }
    
    /** Finish compilation and link new definition into current wordlist.
     * @throws ControlFlowStackImbalance If definition didn't balance out during compilation. */
    public void concludeDefinition()
    throws  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance {
        getCurrentDefinition().complete();
        if (0 < controlFlowStack.size()) {
            ControlFlowElement c = popControl();
            if (c.elementClass() != cDefinition) {
                String s = "Control flow error concluding a Definition.";
                announce(s);
                throw new
                com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance(s, null);
            }                                                         /* End if*/
            currentWordlist.put(getCurrentDefinition());   /* Link into wordlist.*/
            currentDefinition/* This is really mooted by innerInterpreter.getCurrentDefinition()*/
            = (Definition) c.element;/* Pop back next outer definition, maybe null.*/
            state = c.state;                          /* Pop back previous state.*/
        }
        else {
            String s = "Control flow stack empty concluding a Definition.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance(s, null);
        }                                                           /* End if*/
    }
    
    /** Finish compilation and leave token on the stack. Don't link
     * into any wordlist.
     * @throws ControlFlowStackImbalance If definition didn't balance out during compilation.  */
    public void concludeAnonymousDefinition()
    throws  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance {
        getCurrentDefinition().complete();
        if (0 < controlFlowStack.size()) {
            ControlFlowElement c = popControl();
            if (c.elementClass() != cDefinition) {
                String s = "Control flow error concluding an anonymous Definition.";
                announce(s);
                throw new
                com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance(s, null);
            }                                                         /* End if*/
            push(getCurrentDefinition());   /* Leave token on reference stack.*/
            currentDefinition = (Definition) c.element;/* Pop back next outer definition, maybe null.*/
            state = c.state;                          /* Pop back previous state.*/
        }
        else {
            String s = "Control flow stack empty concluding an anonymous Definition.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance(s, null);
        }                                                           /* End if*/
    }
    
    /** Create a new Definition in the current wordlist.
     * Immediacy applied by setImmediate().
     * @param name Name of the new definition.
     */
    public void newDefinition(String name) {
        Definition d = new Definition(name);     /* Need nesting stack of same.*/
        commenceDefinition(d);                 /* Init engined for compilation.*/
    }
    
    /** Create a new Definition in the current wordlist
     * parsing name from input.
     *
     * NOTE that we're not dealing with immediacy yet.
     * @throws BadName If no valid name found.  */
    public void newDefinition()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName {
        String name = parseValidName();
        newDefinition(name);
    }
    
    /** Create a new anonmous Definition in the current wordlist.
     * NOTE that we're not dealing with immediacy yet.
     */
    public void newAnonymousDefinition() {
        Definition d = new Definition();     /* Need nesting stack of same.*/
        commenceDefinition(d);                 /* Init engined for compilation.*/
    }
    
    /************/
  /* Literals */
    /************/
    
    /** Runtime for pushing a compiled literal on the stack.
     * @param l The literal to push.
     */
    public void doLiteral(ParameterizedPrimitive.Literal l) {
        push(l.getObject());
    }
    
    /** Compile a literal already found by the interpreter or some other
     * entity into a Definition. The literal object will push itself
     * at runtime of the Definition.
     * @param o The literal object.
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileLiteral(Object o)
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        new ParameterizedPrimitive.Literal(o).compile(this);
    }
    
    /** Parse up to next doublequote and compile
     * stack --       input: text" --
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileDoubleQuote()
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        doubleQuote();
        compileLiteral(pop());
    }
    
    /** Parse up to next backtick and compile
     * stack --       input: text` --
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileBackTick()
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        backTick();
        compileLiteral(pop());
    }
    
    /********************/
  /* Control flow     */
    /********************/
    
    /** Exit the currently executing Definition.*/
    public void doExit() {
        innerInterpreter.exitCurrentDefinition();
    }
    
    /** Execute a control flow change within the currently executing definition.
     * @param delta delta by which to inc/dec the instruction pointer.
     */
    public void bump(Integer delta) {
        innerInterpreter.bump(delta.intValue());
    }
    
    /** Save a control flow entry on the control flow stack.
     * @param e the control flow entry
     */
    public void pushControl(ControlFlowElement e) {
        controlFlowStack.push(e);
    }
    
    /** Retrieve a control flow entry from the control flow stack.
     * @return the control flow entry
     */
    public ControlFlowElement popControl() {
        return (ControlFlowElement) controlFlowStack.pop();
    }
    
    /** Retrieve a control flow entry guaranteed to contain a
     * ParameterizedPrimitive. Returns null on empty stack.
     *
     * @exception com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance If the control flow stack wasn't balanced.
     * @return the control flow entry
     */
    public ControlFlowElement popParamPrimControl()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance {
        ControlFlowElement c = null;
        if (0 < controlFlowStack.size()) {
            c = popControl();
            if (c.elementClass() != cParameterizedPrimitive) {
                String s = "Control flow stack imbalance resolving a branch.";
                announce(s);
                throw new
                com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance(s, null);
            }                                                         /* End if*/
        }                                                           /* End if*/
        return c;
    }                     /* public ControlFlowElement popParamPrimControl()*/
    
    /** Execute the runtime of an unconditional branch. This is called from
     * a ParameterizedPrimitive so has an embedded object, the Integer which
     * is stored in the prim object indicating the index bump.
     * @param p Parameter to a primitive.
     * @throws InvalidParameterObject self-explanatory
     */
    public void doUnconditionalBranch(ParameterizedPrimitive.Branch p)
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.InvalidParameterObject {
        if (!p.validate()) {
            String s = "A branch had a non-Integer value.";
            announce(s);
            throw
            new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.InvalidParameterObject
            (s, null);
        }                                                           /* End if*/
        Integer i = (Integer) p.getObject();
        bump(i);
    }                     /* public void doBranch(ParameterizedPrimitive p)*/
    
    /** This method is here just as an alias so that strict resolution can occur
     * from within the ctor for ParameterizedPrimitive.UnconditionalBranch p).
     * The point is that ParameterizedPrimitive.ConditionalBranch() calls
     * doUnconditionalBranch() so the signature of the latter must be Branch.
     * @param p parameter for primitive
     * @throws InvalidParameterObject self-explanatory
     */
    public void doUnconditionalBranch(ParameterizedPrimitive.UnconditionalBranch p)
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.InvalidParameterObject {
        doUnconditionalBranch((ParameterizedPrimitive.Branch) p);
    }
    
    /** Execute the runtime of a conditional branch. This is called from
     * a ParameterizedPrimitive so has an embedded object, the Integer which
     * is stored in the prim object indicating the index bump.
     * @param p param for primitive
     * @throws InvalidParameterObject self-explanatory
     * @throws ConditionalNonBoolean self-explanatory
     */
    public void doConditionalBranch(ParameterizedPrimitive.ConditionalBranch p)
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.InvalidParameterObject
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ConditionalNonBoolean {
        Object o = stack.pop();            /* First, check flag.               */
        if (!(cBoolean == o.getClass())) {
            String s =
            "Non-Boolean at 'if' in '" + getCurrentDefinition() + "'";
            s += "\n" + innerInterpreter.toString();/* Throw something, it's not a boolean.*/
            announce(s);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ConditionalNonBoolean(s, null);
        }                                                           /* End if*/
        
        if (!((Boolean)o).booleanValue()) {           /* If true, don't branch.*/
            doUnconditionalBranch(p);
        }                                                           /* End if*/
    }                   /* public void doIfBranch(ParameterizedPrimitive p)*/
    
    /** Compile an unresolved conditional branch.
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileConditionalBranch()
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        ParameterizedPrimitive p;
        announce("Creating parameterized primitive for conditional branch.");
        try {
            p =
            new ParameterizedPrimitive
            .ConditionalBranch(getCurrentDefinition()
            .compositionLength()
            );
        }                                                          /* End try*/
        catch (Exception e) {
            String s = "Problem opening a conditional branch in current definition."
            + getCurrentDefinition();
            announce(s);
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch(s, e);
        }                                                        /* End catch*/
        p.compile(this);
        pushControl(new ControlFlowElement(p, this, cParameterizedPrimitive));
        // relocList.ifBranches.markReloc(p);
    }
    
    /** Compile an unresolved unconditional branch.
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileBranch()
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        ParameterizedPrimitive p;
        announce("Creating parameterized primitive for unconditional branch.");
        try {
            p = new ParameterizedPrimitive
            .UnconditionalBranch(getCurrentDefinition()
            .compositionLength()
            );
        }                                                          /* End try*/
        catch (Exception e) {
            String s = "Problem opening an unconditional branch in current definition."
            + getCurrentDefinition();
            announce(s);
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch(s, e);
        }                                                        /* End catch*/
        p.compile(this);
        pushControl(new ControlFlowElement(p, this, cParameterizedPrimitive));
        // relocList.ifBranches.markReloc(p);
    }                                       /* public void compileBranch ()*/
    
    /** Resolve a conditional or unconditional branch by popping control stack,
     * evaluating the integer offset at which the Parameterized
     * Primitive recorded itself in the (to resolution) unidentified
     * definition, and change the Primitive to hold the bump delta.
     * This works because the reference on the control flow stack
     * is a reference to the same object that got compiled into the
     * definition.
     * @throws ControlFlowStackImbalance self-explanatory
     * @throws BranchResolution self-explanatory
     */
    public void resolveBranch()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution {
        ControlFlowElement c = popParamPrimControl();/* Throws if non a ParamPrim.*/
        
        if (null == c)                                  /* Did we get anything?*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
        
        ParameterizedPrimitive p =  /* Manipulate branch in definition via ref.*/
        (ParameterizedPrimitive) c.getElement();
        
        if (p.validate()) {/* Now resolve the branch via the ref from the stack.*/
            int origin = ((Integer) p.getObject()).intValue();
            int destination =   /* Okay - inner interpreter while loop tests .LT.*/
            getCurrentDefinition().compositionLength();
            p.setObject(new Integer((destination - origin) - 1));  /* Resolution.*/
      /* "Minus one" because this is the bump delta to the instruction
       * pointer, which latter has already been post-incremented in the
       * inner interpreter loop.
       */
        }
        
        else                                                      /* Not valid.*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
    }                                        /* public void resolveBranch()*/
    
    /** Make an 'else' by laying down an unconditional branch out
     * of the true-clause of the 'if' and then resolving the 'if'
     * to point just past it.
     * @throws ControlFlowStackImbalance self-explanatory
     * @throws BranchResolution self-explanatory
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileAndResolveBranch()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution
    , java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
    /* Retrieve the previously laid down unresolved branch. */
        ControlFlowElement c = popParamPrimControl();/* Throws if non a ParamPrim.*/
        if (null == c)                                  /* Did we get anything?*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
        
    /* Keep unresolved in hand while laying down a new unresolved unconditional. */
        compileBranch();   /* Now quick like a bunny lay down a new unresolved.*/
        pushControl(c);/* Sneakily push back the previously retrieved unresolved.*/
        resolveBranch();/* Resolve the previously unresolved, leaving new unres.*/
    }                              /* public void compileAndResolveBranch()*/
    
    /** Push an unresolved unconditional branch. I.e., this is 'begin'.
     * The unconditional branch can be discarded for a conditional branch
     * if it turns out the the 'begin' is resolved by an 'until' instead
     * of an 'again' or 'repeat'.
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     */
    public void pushUnconditionalBranch()
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch {
        ParameterizedPrimitive p;
        announce("Creating parameterized primitive for unconditional branch.");
        try {
            p = new ParameterizedPrimitive
            .UnconditionalBranch(getCurrentDefinition()
            .compositionLength()
            );
        }                                                          /* End try*/
        catch (Exception e) {
            String s = "Problem opening an unconditional branch in current definition."
            + getCurrentDefinition();
            announce(s);
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch(s, e);
        }                                                        /* End catch*/
        pushControl(new ControlFlowElement(p, this, cParameterizedPrimitive));
    }                             /* public void pushUnconditionalBranch ()*/
    
    /** Compile an 'again', that is, resolve and compile
     * the unconditional branch pushed to the control
     * flow stack by 'begin'.
     * @throws ControlFlowStackImbalance self-explanatory
     * @throws BranchResolution self-explanatory
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileUnconditionalBackwardsBranch()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution
    , java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
    /* Retrieve the previously pushed unresolved branch. */
        ControlFlowElement c = popParamPrimControl();/* Throws if non a ParamPrim.*/
        if (null == c)                                  /* Did we get anything?*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
        ParameterizedPrimitive p =         /* Grab branch to be resolved.      */
        (ParameterizedPrimitive) c.getElement();
        if (p.validate()) {/* Now resolve the branch via the ref from the stack.*/
            int destination = ((Integer) p.getObject()).intValue();
            int origin =
            getCurrentDefinition().compositionLength();
            p.setObject(new Integer((destination-origin) - 1));/* Resolution backwards.*/
            p.compile(this);                                      /* Lay it down.*/
        }
        else                                                      /* Not valid.*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
    }                  /* public void compileUnconditionalBackwardsBranch()*/
    
    /** Compile an 'until', that is, calc the delta from the unconditional
     * backwards branch pushed to the control flow stack by 'begin' and
     * compile a conditional branch.
     * @throws ControlFlowStackImbalance self-explanatory
     * @throws BranchResolution self-explanatory
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileConditionalBackwardsBranch()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution
    , java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
    /* Retrieve the previously pushed unresolved branch. */
        ControlFlowElement c = popParamPrimControl();/* Throws if non a ParamPrim.*/
        if (null == c)                                  /* Did we get anything?*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
        ParameterizedPrimitive p =         /* Grab branch to be resolved.      */
        (ParameterizedPrimitive) c.getElement();
        if (p.validate()) {/* Now resolve the branch via the ref from the stack.*/
            int destination = ((Integer) p.getObject()).intValue();
            int origin =
            getCurrentDefinition().compositionLength();
            try {
                p =/* Discard the unconditional pushed by 'begin', create new branch.*/
                new ParameterizedPrimitive   /* Resolution backwards.            */
                .ConditionalBranch((destination-origin) - 1);
            }                                                        /* End try*/
            catch (Exception e) {
                String s = "Problem creating a conditional branch in current definition."
                + getCurrentDefinition();
                announce(s);
                e.printStackTrace(System.err);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch(s, e);
            }                                                      /* End catch*/
            p.compile(this);                                      /* Lay it down.*/
        }
        else                                                      /* Not valid.*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
    }                    /* public void compileConditionalBackwardsBranch()*/
    
    /** Compile an 'repeat', that is, resove the unconditional
     * backwards branch pushed to the control flow stack by 'begin' and
     * compile it, all after resolving the conditional forward branch
     * pushed by 'while' ( == 'if').
     * @throws ControlFlowStackImbalance self-explanatory
     * @throws BranchResolution self-explanatory
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void resolveTwoBranches()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution
    , java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        ControlFlowElement cfb = popControl();     /* We have to swap these ...*/
        ControlFlowElement ubb = popControl();    /* ... so that the 'while ...*/
        pushControl(cfb);                       /* ... will branch out past ...*/
        pushControl(ubb);                                  /* ... the 'repeat'.*/
        compileUnconditionalBackwardsBranch();/* Resolve the 'repeat' to 'begin'.*/
        resolveBranch();            /* Resolve the 'while' to past the 'begin'.*/
    }                                   /* public void resolveTwoBranches()*/
    
    /** The runtime for 'do', i.e., push the indices to control stack.
     * @param p The <CODE>Do</CODE> Semantic we are going to run.
     */
    public void doDo(ParameterizedPrimitive.Do p) {
        int index = ((Long)pop()).intValue();
        announce("'do' index is " + new Integer(index));
        int limit = ((Long)pop()).intValue();
        announce("'do' limit is " + new Integer(limit));
        int egress = ((Integer) p.getObject()).intValue();
        announce("'do' egress is " + new Integer(egress));
        innerInterpreter.startLoop(limit, index, egress);
    }
    
    /** Push control flow entry representing an unresolved 'do' and compile
     * the runtime for it.
     * @throws NoSuchMethodException self-explanatory
     * @throws ClassNotFoundException self-explanatory
     * @throws OpenIfBranch self-explanatory
     * @throws BadPrimitiveCompile self-explanatory
     * @throws BadDefinitionCompile self-explanatory
     * @throws BadPrimitiveExecute self-explanatory
     * @throws BadDefinitionExecute self-explanatory
     */
    public void compileDo()
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        announce("Creating primitive for 'do'");
        ParameterizedPrimitive p;  /* Compile current offset for branch marker,*/
    /* later resolved to 'leave' offset.*/
        announce("Creating parameterized primitive for 'do'.");
        try {/* If it turns out to be a +loop we'll discard this later, using delta.*/
            p = new ParameterizedPrimitive/* Not compiled, just used as a marker.*/
            .Do(getCurrentDefinition()
            .compositionLength() + 1/* To right after the 'do' runtime.*/
            );
        }                                                          /* End try*/
        catch (Exception e) {
            String s = "Problem opening an unconditional branch in current definition."
            + getCurrentDefinition();
            announce(s);
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.OpenIfBranch(s, e);
        }                                                        /* End catch*/
        pushControl(new ControlFlowElement(p, this, cParameterizedPrimitive));
        p.compile(this);
        // relocList.ifBranches.markReloc(p);
    }
    
    /** Runtime for 'loop'. Called from a ParameterizedPrimitive having
     * embedded object, the Integer stored indicating the bump value.
     * @param p The <code>Loop</code> Semantic we are going to execute.
     */
    public void loop(ParameterizedPrimitive.Loop p) {
        boolean done = innerInterpreter.loop();
        if (!done) {
            Integer i = (Integer) p.getObject();
            bump(i);
        }                                                           /* End if*/
    }                   /* public void loop (ParameterizedPrimitive.Loop p)*/
    
    /** Runtime for '+loop'. Called from a ParameterizedPrimitive having
     * embedded object, the Integer stored indicating the bump value.
     * @param p PlusLoop object */
    public void plusLoop(ParameterizedPrimitive.PlusLoop p) {
        int increment = ((Long)pop()).intValue();/* Get loop increment from stack.*/
        boolean done = innerInterpreter.plusLoop(increment);
        if (!done) {
            Integer i = (Integer) p.getObject();
            bump(i);
        }                                                           /* End if*/
    }                   /* public void loop (ParameterizedPrimitive.Loop p)*/
    
    /** Compile a 'loop' resolved to where the 'do' was encountered by
     * popping control stack,  evaluating the integer offset at
     * which the ParameterizedPrimitive recorded from the
     * current definition, changing the Primitive to hold the bump delta,
     * and compiling it into the current definition.
     * Doesn't validate that it's a Loop yet, should do that.
     * @throws ControlFlowStackImbalance on exception
     * @throws BranchResolution on exception
     * @throws ClassNotFoundException on exception
     * @throws NoSuchMethodException on exception
     * @throws BadPrimitiveCompile on exception
     * @throws BadDefinitionCompile on exception
     * @throws BadPrimitiveExecute on exception
     * @throws BadDefinitionExecute on exception
     */
    public void compileLoop()
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution
    , java.lang.ClassNotFoundException
    , java.lang.NoSuchMethodException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        ControlFlowElement c = popParamPrimControl();/* Throws if non a ParamPrim.*/
        
        if (null == c)                                  /* Did we get anything?*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
        
        ParameterizedPrimitive p =  /* Manipulate branch in definition via ref.*/
        (ParameterizedPrimitive) c.getElement();
        
        if (p.validate()) {/* Now resolve the back branch via the ref from stack.*/
            int destination = ((Integer) p.getObject()).intValue();
            int origin =                /* Add one because inner interp will have*/
      /*  postincremented  past this compilation. */
            getCurrentDefinition().compositionLength() + 1;
            int delta = destination - origin;/* Backwards value.                 */
            p.setObject(new Integer(origin)); /* 'leave' Resolution for the 'do'.*/
            p = new ParameterizedPrimitive.Loop(delta);/* Create the resolved Loop.*/
            p.compile(this);                        /* Compile the resolved loop.*/
        }
        
        else                                                      /* Not valid.*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
    }                                          /* public void compileLoop()*/
    
    /** Compile a '+loop' resolved to where the 'loop' was encountered by
     * popping control stack,  evaluating the integer offset at
     * which the ParameterizedPrimitive recorded from the
     * current definition, changing the Primitive to hold the bump delta,
     * and compiling it into the current definition.
     * Doesn't validate that it's a Loop yet, should do that.
     * @throws NoSuchMethodException on exception
     * @throws ClassNotFoundException on exception
     * @throws ControlFlowStackImbalance on exception
     * @throws BranchResolution on exception
     * @throws BadPrimitiveCompile on exception
     * @throws BadDefinitionCompile on exception
     * @throws BadPrimitiveExecute on exception
     * @throws BadDefinitionExecute on exception
     */
    public void compilePlusLoop()
    throws java.lang.NoSuchMethodException
    , java.lang.ClassNotFoundException
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.ControlFlowStackImbalance
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        ControlFlowElement c = popParamPrimControl();/* Throws if non a ParamPrim.*/
        
        if (null == c)                                  /* Did we get anything?*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
        
        ParameterizedPrimitive p =  /* Manipulate branch in definition via ref.*/
        (ParameterizedPrimitive) c.getElement();
        
        if (p.validate()) {/* Now resolve the back branch via the ref from stack.*/
            int destination = ((Integer) p.getObject()).intValue();
            int origin =                /* Add one because inner interp will have*/
      /*  postincremented  past this compilation. */
            getCurrentDefinition().compositionLength() + 1;
            int delta = destination - origin;/* Backwards Resolution.            */
            announce("Delta compiling +loop is " + new Integer(delta));
            p.setObject(new Integer(origin)); /* 'leave' Resolution for the 'do'.*/
            p =                             /* Now create the PlusLoop, resolved.*/
            new ParameterizedPrimitive     /* Create new PlusLoop.             */
            .PlusLoop(delta);                       /* Backwards Resolution.*/
            p.compile(this);                                 /* Compile the loop.*/
        }
        
        else                                                      /* Not valid.*/ {
            String s = "Branch resolution failure.";
            announce(s);
            throw new
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BranchResolution(s, null);
        }                                                           /* End if*/
    }                                      /* public void compilePlusLoop()*/
    
    /** Return a loop index. */
    public void index() {
        push(new Long(innerInterpreter.ithIndex(((Long)pop()).intValue())));
    }                                               /* public void index ()*/
    
    /** Perform a leave at runtime */
    public void doLeave() {
        innerInterpreter.leaveLoop();
    }
    
    /*************/
    /* Utilities */
    /*************/
    
    /** Do a newline on the output. */
    public void cr() {
        myInterpreter.output("\n");
    }
    
    /** Set the interpreter verbose or non- at runtime. */
    public void runtimeVerbose() {
        setVerbose(((Boolean)pop()).booleanValue());
    }
    
    /** Decompile and print out. */
    public void decompile() {
        Semantic s = (Semantic) pop();
        Semantic semantics[] = s.decompile();
        myInterpreter.output(": " + s.getName() + " ");
        for (int i = 0; i < semantics.length ; ++i) {
            Semantic x = semantics[i];
            myInterpreter.output(x.getName() + " ");
            if (x.getClass() == cLiteral) {
                myInterpreter
                .output(((ParameterizedPrimitive.Literal)x).getObject() + " ");
            }                                                         /* End if*/
        }                                                          /* End for*/
        myInterpreter.output(";\n");
    }                                           /* public void decompile ()*/
    
    /** Execute a host command.
     * @throws IOException if error on execution
     */
    public void system()
    throws java.io.IOException {
        String s = (String) pop();
        Runtime.getRuntime().exec(s);
    }
    
    /** Interpret a string as fiji input. */
    public void interpret() {
        myInterpreter.interpret((String)pop());
    }                                           /* public void interpret ()*/
    
    /** Load FIJI source from a file. */
    public void load() {
        String fileName = (String) pop();
        File file = new File(fileName);
        long length = file.length();
        char input[] = new char[(int)length];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            try {
                inputStreamReader.read(input, 0, (int)length);
                String sourceCode = new String(input);
                myInterpreter.interpret(sourceCode);
            }                                                        /* End try*/
            catch (Exception e) {
                e.printStackTrace(System.err);
            }                                                      /* End catch*/
            finally {
                fileInputStream  .close();
                inputStreamReader.close();
            }                                                    /* End finally*/
        }                                                          /* End try*/
        catch (Exception e) {
            e.printStackTrace(System.err);
        }                                                        /* End catch*/
    }                                                 /* public void load()*/
    
    /**
     * Save a compiled wordlist to a file.
     * Wordlist java.io.File --
     * @throws FileNotFoundException res ipse loq
     * @throws IOException res ipse loq
     */
    public void save()
    throws java.io.FileNotFoundException,
    java.io.IOException
    
    {
        File f = (File) pop();
        Wordlist w = (Wordlist) pop();
        w.save(f);
    }
    
    /**
     * Load a saved wordlist.
     * java.io.File -- Wordlist
     * @throws FileNotFoundException res ipse loq
     * @throws IOException res ipse loq
     * @throws ClassNotFoundException res ipse loq
     */
    public void reload()
    throws java.io.FileNotFoundException,
    java.io.IOException,
    java.lang.ClassNotFoundException {
        File f = (File) pop();
        push(Wordlist.reload(f));
        // System.err.println("Not implemented yet.");
    }
    
    /** The version of FIJI
     * @return The FIJI version
     */
    public static String fijiVersion() {
        return  "1.2";
    }
    
    /** Push the version of FIJI */
    public void version() {
        push(fijiVersion());
    }
    
    /*************/
    /* Wordlists */
    /*************/
    
    /** Create a new Wordlist and add it to the current wordlist.
     * @throws BadName If no valid name found.  */
    public void newWordlist()
    throws  com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadName {
        String s = parseValidName();
        currentWordlist.put(new Wordlist(s));
    }
    
    /** Fetch the search order to stack. */
    public void getOrder() {
        searchOrder.getOrder(this);
    }                                             /* public void getOrder()*/
    
    /** Set search order from stack. */
    public void setOrder() {
        searchOrder.setOrder(this);
    }                                             /* public void setOrder()*/
    
    /** Set current Wordlist to the Wordlist on the top of the stack. */
    public void setCurrent() {
        currentWordlist = (Wordlist) pop();
    }
    
    /** Get current Wordlist. */
    public void getCurrent() {
        push(currentWordlist);
    }
    
    /** List all the words in the accessed Wordlists. */
    public void words() {
        myInterpreter.output(searchOrder.words());
    }
    
    /** Pop the active Semantic of a wordlist entry in first
     * place found in the search order, discarding the entry
     * if no previous Semantic exists by that name.
     *
     * @see com.SoftWoehr.FIJI.base.desktop.shell.SearchOrder#forget
     */
    public void forget() {
        boolean result;
        String name = (String)pop();
        result = searchOrder.forget(name);
        if (false == result) {
            myInterpreter.output(name + " not found.\n");
        }                                                           /* End if*/
    }                                              /* public void forget ()*/
    
    /** Discard a wordlist entry in first
     * place found in the search order.
     *
     * @see com.SoftWoehr.FIJI.base.desktop.shell.SearchOrder#discard
     */
    public void discard() {
        boolean result;
        String name = (String)pop();
        result = searchOrder.discard(name);
        if (false == result) {
            myInterpreter.output(name + " not found.\n");
        }                                                           /* End if*/
    }                                              /* public void discard()*/
    
}                                                    /* End of engine class*/

/** A class to represent arguments passed to Java method resolution,
 * and thence to a Java method invocation. Necessitated by the fact
 * that arguments sometimes must be of one class but cast as another,
 * e.g., java.lang.String.equals(java.lang.Object o) but the Object
 * is a String.
 */
class JavaArgs extends Vector {
    /** Create a JavaArgs object to pass args to java method invocation.
     */
    public JavaArgs() {}                     /* Needs an arity/1 on Object[]*/
    
    /** Return an object array of the elements of the vector.
     * All the objects in the JavaArgs vector are stored in JavaParam's,
     * along with their nominal class for the purpose of method signature
     * resolution.
     * @see com.SoftWoehr.FIJI.base.desktop.shell.JavaParam
     * @return An array of objects representing the arguments stored
     * in a JavaArgs
     */
    public synchronized Object[] toObjectArray() {
        int sz = size();
        Object result[] = new Object[sz];
        Enumeration e = elements();
        for (int i = 0; i < sz ; ++i) {
            result[i] = ((JavaParam) (e.nextElement())).getRealObject();
        }                                                          /* End for*/
        return result;
    }
    
    /** Return a Class array of the classes of the elements of the vector.
     * All the objects and their nominal classes for the purpose of
     * method signature resolution are stored as JavaParams in the vector.
     * @see com.SoftWoehr.FIJI.base.desktop.shell.JavaParam
     * @return The array of classes from the vector.
     */
    public synchronized Class[] toClassArray() {
        int sz = size();
        Class result[] = new Class[sz];
        Enumeration e = elements();
        for (int i = 0; i < sz ; ++i) {
            result[i] = ((JavaParam) (e.nextElement())).getSignatureClass();
        }                                                          /* End for*/
        return result;
    }
}                                                /* End of JavaArgs class*/
/*  End of engine.java */
