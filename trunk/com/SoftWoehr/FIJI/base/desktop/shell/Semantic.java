/* Semantic.java ...  */
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
import  com.SoftWoehr.*;
import  com.SoftWoehr.util.*;

/**
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.5 $
 */
public class Semantic implements SoftWoehr, verbose, Serializable {
    /** Revision level */
    private static final String rcsid = "$Id: Semantic.java,v 1.5 2001-09-16 21:00:15 jwoehr Exp $";
    /** Implements com.SoftWoehr.SoftWoehr
     * @return the rcsid
     */
    public String rcsId() {return rcsid;}
    
    /**  Flags whether we are in verbose mode. */
    public boolean isverbose = false;
    
    /**  Helper for verbose mode. */
    private verbosity v = new verbosity(this);
    
    private String myName;
    
    /** Arity/0 ctor. */
    public Semantic() {
        this("Anonymous Semantic");
    }
    
    /** Arity/1 ctor.
     * @param name Name of the Semantic
     */
    public Semantic(String name) {
        myName = name;
    }
    
    /** Return a string representation of the object.
     * @return string rep */
    public String toString()
    {return "A Semantic named " + getName();}
    
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
    
    /** Identification.
     * @return Name of the Semantic
     */
    public String getName() {
        return myName;
    }
    
    /** Set string id.
     * @param s Name of the Semantic
     */
    public void setName(String s) {
        myName = s;
    }
    
    /** Execution semantics, the default behavior
     * being to push self to stack.
     * @param e associated engine
     * @throws BadPrimitiveExecute res ipse loq
     * @throws BadDefinitionExecute res ipse loq
     */
    public void execute(engine e)
    throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
    , com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        e.push(this);
    }
    
    /** Compilation semantics, the default behavior
     * being to append self to the current definition.
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
        e.getCurrentDefinition().append(this);
    }
    
    /** Decompilation semantics. The default is to
     * push a Semantic array with 'this' as only entry.
     * @return the Semantic array representing this Semantic
     */
    public Semantic[] decompile() {
        Semantic decompilation [] = new Semantic[1];
        decompilation[0] = this;
        return decompilation;
    }
}                                                  /* End of Semantic class*/

/*  End of Semantic.java */
