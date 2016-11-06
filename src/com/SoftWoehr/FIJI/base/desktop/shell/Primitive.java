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
        Class c = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.engine");
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
        Class c = Class.forName("com.SoftWoehr.FIJI.base.desktop.shell.engine");
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
    public void execute(engine anEngine)
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
    
    public void compile(engine e)
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
