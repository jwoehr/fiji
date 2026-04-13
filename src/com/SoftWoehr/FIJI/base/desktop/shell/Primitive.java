/* Primitive.java ...  A builtin semantic.   */
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
import  java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

import  com.SoftWoehr.SoftWoehr;
import  com.SoftWoehr.util.*;

/** A Primitive is a Semantic coded all in Java.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.1 $
 */
class Primitive extends Semantic implements SoftWoehr, verbose, Serializable {
    
    /** Revision level */
    private static final String rcsid = "$Id: Primitive.java,v 1.1 2016-11-06 21:20:38 jwoehr Exp $";
    
    /** Whitelist of allowed method names for reflection-based invocation */
    private static final Set<String> ALLOWED_METHODS = new HashSet<>();
    
    static {
        // Initialize whitelist with all legitimate Engine methods
        ALLOWED_METHODS.add("noop");
        ALLOWED_METHODS.add("arf");
        ALLOWED_METHODS.add("depth");
        ALLOWED_METHODS.add("dup");
        ALLOWED_METHODS.add("drop");
        ALLOWED_METHODS.add("swap");
        ALLOWED_METHODS.add("over");
        ALLOWED_METHODS.add("rot");
        ALLOWED_METHODS.add("roll");
        ALLOWED_METHODS.add("pick");
        ALLOWED_METHODS.add("dot_s");
        ALLOWED_METHODS.add("dot_c");
        ALLOWED_METHODS.add("dot_sc");
        ALLOWED_METHODS.add("dot");
        ALLOWED_METHODS.add("dotdot");
        ALLOWED_METHODS.add("dot_r");
        ALLOWED_METHODS.add("warm");
        ALLOWED_METHODS.add("cold");
        ALLOWED_METHODS.add("quit");
        ALLOWED_METHODS.add("getStackEntryClass");
        ALLOWED_METHODS.add("stackEntryToString");
        ALLOWED_METHODS.add("javaArgs");
        ALLOWED_METHODS.add("accumulateArg");
        ALLOWED_METHODS.add("callJava");
        ALLOWED_METHODS.add("lexeme");
        ALLOWED_METHODS.add("classForName");
        ALLOWED_METHODS.add("bye");
        ALLOWED_METHODS.add("sysexit");
        ALLOWED_METHODS.add("pushTrue");
        ALLOWED_METHODS.add("pushNull");
        ALLOWED_METHODS.add("pushFalse");
        ALLOWED_METHODS.add("castParam");
        ALLOWED_METHODS.add("classToPrimitiveType");
        ALLOWED_METHODS.add("stackEntryToPrimitive");
        ALLOWED_METHODS.add("stackEntryToPrimParam");
        ALLOWED_METHODS.add("longToIntParam");
        ALLOWED_METHODS.add("find");
        ALLOWED_METHODS.add("execute");
        ALLOWED_METHODS.add("compile");
        ALLOWED_METHODS.add("leftBracket");
        ALLOWED_METHODS.add("rightBracket");
        ALLOWED_METHODS.add("popBase");
        ALLOWED_METHODS.add("pushBase");
        ALLOWED_METHODS.add("doState");
        ALLOWED_METHODS.add("isImmediate");
        ALLOWED_METHODS.add("compileOnly");
        ALLOWED_METHODS.add("doubleQuote");
        ALLOWED_METHODS.add("backTick");
        ALLOWED_METHODS.add("comment");
        ALLOWED_METHODS.add("doExit");
        ALLOWED_METHODS.add("not");
        ALLOWED_METHODS.add("and");
        ALLOWED_METHODS.add("or");
        ALLOWED_METHODS.add("xor");
        ALLOWED_METHODS.add("isEqual");
        ALLOWED_METHODS.add("greaterThan");
        ALLOWED_METHODS.add("lessThan");
        ALLOWED_METHODS.add("isUnequal");
        ALLOWED_METHODS.add("add");
        ALLOWED_METHODS.add("sub");
        ALLOWED_METHODS.add("mul");
        ALLOWED_METHODS.add("div");
        ALLOWED_METHODS.add("mod");
        ALLOWED_METHODS.add("array");
        ALLOWED_METHODS.add("dimarray");
        ALLOWED_METHODS.add("newVariable");
        ALLOWED_METHODS.add("store");
        ALLOWED_METHODS.add("fetch");
        ALLOWED_METHODS.add("newValue");
        ALLOWED_METHODS.add("toValue");
        ALLOWED_METHODS.add("newAnonymousDefinition");
        ALLOWED_METHODS.add("newDefinition");
        ALLOWED_METHODS.add("cr");
        ALLOWED_METHODS.add("doLeave");
        ALLOWED_METHODS.add("index");
        ALLOWED_METHODS.add("runtimeVerbose");
        ALLOWED_METHODS.add("decompile");
        ALLOWED_METHODS.add("system");
        ALLOWED_METHODS.add("interpret");
        ALLOWED_METHODS.add("load");
        ALLOWED_METHODS.add("save");
        ALLOWED_METHODS.add("reload");
        ALLOWED_METHODS.add("version");
        ALLOWED_METHODS.add("getOrder");
        ALLOWED_METHODS.add("setOrder");
        ALLOWED_METHODS.add("newWordlist");
        ALLOWED_METHODS.add("setCurrent");
        ALLOWED_METHODS.add("getCurrent");
        ALLOWED_METHODS.add("words");
        ALLOWED_METHODS.add("forget");
        ALLOWED_METHODS.add("discard");
    }
    
    /**
     * Validates that a method name is in the whitelist of allowed methods.
     * @param methodName the method name to validate
     * @throws SecurityException if the method name is not whitelisted
     */
    private static void validateMethodName(String methodName) throws SecurityException {
        if (methodName == null || !ALLOWED_METHODS.contains(methodName)) {
            throw new SecurityException("Method name '" + methodName + "' is not in the whitelist of allowed methods");
        }
    }
    
    /** Implements com.SoftWoehr.SoftWoehr
     * @return the rcsid
     */
    public String rcsId() {return rcsid;}
    
    /**  Flags whether we are in verbose mode. */
    public boolean isverbose = false;
    
    /**  Helper for verbose mode. */
    private verbosity v = new verbosity(this);
    
    /** Resolved method that represents execution semantics. */
    public Method method;
    
    /** Resolved method that represents compilation semantics. */
    public Method compilationMethod;
    
    /** Arity/0 ctor.
     * @throws ClassNotFoundException res ipse loq
     * @throws NoSuchMethodException res ipse loq
     */
    public Primitive()
    throws java.lang.ClassNotFoundException
    , java.lang.NoSuchMethodException {
        this("noop", "noop");
    }
    
    /** Arity/2 ctor. This creates
     * a named, resolved primitive, the only
     * useful constructor.
     * @param name name of prim
     * @param methodName method to execute
     * @throws ClassNotFoundException res ipse loq
     * @throws NoSuchMethodException res ipse loq
     */
    public Primitive(String name, String methodName)
    throws java.lang.ClassNotFoundException
    , java.lang.NoSuchMethodException {
        this.setName(name);
        validateMethodName(methodName);
        Class c = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.Engine");
        method = c.getMethod(methodName, new Class[0]);          /* All Arity/0*/
    }
    
    /** Arity/2 ctor. This creates
     * a named, resolved primitive, the only
     * useful constructor.
     * @param name name of prim
     * @param methodName method to execute
     * @param compilationMethodName compilation semantic
     * @throws ClassNotFoundException res ipse loq
     * @throws NoSuchMethodException res ipse loq
     */
    public Primitive(String name, String methodName, String compilationMethodName)
    throws java.lang.ClassNotFoundException
    , java.lang.NoSuchMethodException {
        this.setName(name);
        validateMethodName(methodName);
        validateMethodName(compilationMethodName);
        Class c = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.Engine");
        method = c.getMethod(methodName, new Class[0]);          /* All Arity/0*/
        compilationMethod = c.getMethod(compilationMethodName
        , new Class[0]);  /* All Arity/0*/
    }
    
    /** shutdown() here does nothing.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return always zero 0
     */
    public int shutdown() { return 0; }
    
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
    
    /** Execution semantics
     * @param anEngine associated engine
     * @throws BadPrimitiveExecute res ipse loq
     */
    public void execute(Engine anEngine)
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute {
        try {
            method.invoke(anEngine, new Object[0]);      /* zero-arity invocation*/
        }                                                          /* End try*/
        catch (Exception e) {
            String s = "Problem executing method from FIJI primitive " + getName();
            System.out.println(s);
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute(s, e);
        }                                                        /* End catch*/
    }                                                            /* execute*/
    
    /** Compilation semantics, the default behavior
     * being to append self to the current definition.
     * If the Primitive has special compilation semantics,
     * carry them out instead.
     * @param e associated engine
     * @throws BadPrimitiveCompile res ipse loq
     * @throws BadDefinitionCompile res ipse loq
     * @throws BadPrimitiveExecute res ipse loq
     * @throws BadDefinitionExecute res ipse loq
     */
    
    public void compile(Engine e)
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionCompile
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        if (null == compilationMethod) {
            super.compile(e);
        }
        else                                                           /* If we*/ {
            try {
                compilationMethod.invoke(e, new Object[0]);
            }                                                        /* End try*/
            catch (Exception ex) {
                String s = "Problem executing compilation method from FIJI primitive " + getName();
                System.out.println(s);
                ex.printStackTrace(System.err);
                throw new com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveCompile(s, ex);
            }                                                      /* End catch*/
        }                                                           /* End if*/
    }                                                            /* compile*/
    
    /** Decompilation semantics.
     * A primitive just returns itself.
     * @return self
     */
    public Semantic[] decompile() {
        Semantic result [] = new Semantic[1];
        result[0] = this;
        return result;
    }
}                                                 /* End of Primitive class*/

/*  End of Primitive.java */
