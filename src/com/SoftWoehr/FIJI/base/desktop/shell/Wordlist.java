/* Wordlist.java ... a dictionary of Semantics */
/*********************************************/
/* Copyright *C* 1999, 2001                  */
/* All Rights Reserved.                      */
/* Jack J. Woehr jax@softwoehr.com           */
/* http://www.softwoehr.com                  */
/* http://fiji.sourceforge.net               */
/* P.O. Box 82, Beulah, Colorado 81023 USA  */
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

import com.SoftWoehr.SoftWoehr;
import com.SoftWoehr.util.verbose;
import com.SoftWoehr.util.GetArgs;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

/**
 * A dictionary of Semantics
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.1 $
 */
public class Wordlist extends Semantic implements SoftWoehr, verbose, Serializable {

    /**
     * Revision level
     */
    private static final String rcsid = "$Id: Wordlist.java,v 1.1 2016-11-06 21:20:38 jwoehr Exp $";

    /**
     * Implements com.SoftWoehr.SoftWoehr
     *
     * @return the rcsid
     */
    public String rcsId() {
        return rcsid;
    }

    /**
     * Flags whether we are in verbose mode.
     */
    private boolean isverbose = false;

    /**
     * The storage of the wordlist
     */
    private Hashtable wordlist;

    /**
     * Arity/0 ctor.
     */
    public Wordlist() {
        reinit("Anonymous Wordlist.");
    }

    /**
     * Arity/1 ctor.
     *
     * @param name name of Wordlist (if any)
     */
    public Wordlist(String name) {
        reinit(name);
    }

    /**
     * Reinit discarding previous state.
     *
     * @param name name of Wordlist (if any)
     */
    public void reinit(String name) {
        setName(name);
        wordlist = new Hashtable();
    }

    /**
     * Is this verbose and announcing?
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return true if verbose
     */
    @Override
    public boolean isVerbose() {
        return isverbose;
    }

    /**
     * Set verbose and announcing.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param tf set verbose on (true) or off.
     */
    @Override
    public void setVerbose(boolean tf) {
        isverbose = tf;
    }

    /**
     * Emit a string message if set verbose.
     *
     * @see com.SoftWoehr.util.verbose
     * @param s The string to conditionally announce.
     */
    @Override
    public void announce(String s) {
        if (isVerbose()) {
            System.err.println(s);
            System.err.flush();
        }
    }

    /**
     * Add a Semantic in, comes with its own key. If key already exists, push
     * the previous Semantic for the key (so that it can later be restored if
     * the user 'forget's the active Semantic) and set the active Semantic to
     * the Semantic presented to this method.
     *
     * @param s the Semantic
     */
    public void put(Semantic s) {
        String name = s.getName();
        WordlistEntry entry = findEntry(name);
        if (null == entry) {
            entry = new WordlistEntry(s);
            wordlist.put(name, entry);
        } else {
            entry.push(s);
        }
        /* End if*/
    }

    /* public void put(Semantic s)*/
    /**
     * Find a WordlistEntry by name in a wordlist.
     *
     * @param name The name of the word to find
     * @return the WordlistEntry or null
     */
    private WordlistEntry findEntry(String name) {
        return (WordlistEntry) wordlist.get(name);
    }

    /**
     * Find a Semantic by name in a wordlist.
     *
     * @param name The name of the word to find
     * @return the Semantic or null
     */
    public Semantic find(String name) {
        Semantic s = null;
        WordlistEntry entry = findEntry(name);
        if (null != entry) {
            s = entry.getSemantic();
        }
        /* End if*/
        return s;
    }

    /* public Semantic find(String name)*/
    /**
     * 'Forget' the active Semantic for a name, popping the previous Semantic in
     * its place. If no previous Semantic is stacked, remove the entry from the
     * Wordlist.
     *
     * @param name Semantic to forget.
     */
    public void forget(String name) {
        WordlistEntry entry = (WordlistEntry) wordlist.get(name);
        if (null != entry) {
            Semantic s = entry.pop();
            if (null == s) {
                wordlist.remove(name);
            }
        }
    }

    /**
     * Delete entirely the most recent Semantic keyed by name in a Wordlist.
     *
     * @param name Key to discard
     */
    public void discard(String name) {
        wordlist.remove(name);
    }

    /**
     * Put a Semantic in a wordlist by keying it by name. Note that this adds a
     * new Semantic to a WordlistEntry but doesn't throw out the old Semantic
     * under the same name.
     *
     * @param s The Semantic
     */
    public void insert(Semantic s) {
        WordlistEntry entry = findEntry(s.getName());
        if (null == entry) {
            entry = new WordlistEntry(s);
            wordlist.put(s.getName(), entry);
        } else {
            entry.push(s);
        }
    }

    /**
     * Return names of all words in the wordlist.
     *
     * @return String of words
     */
    public String words() {
        String result = "";
        WordlistEntry entry;
        for (Enumeration e = wordlist.elements(); e.hasMoreElements();) {
            entry = (WordlistEntry) (e.nextElement());
            if (null != entry) {
                result += entry.getName() + " ";
                announce(result + "\n");
            }
        }
        return result;
    }

    /* public String words ()*/
    /**
     * Do a string dump wordlist.
     *
     * @return String representing wordlist.
     */
    public String dump() {
        String result = "Dumping Wordlist:\n";
        WordlistEntry entry;
        for (Enumeration e = wordlist.elements(); e.hasMoreElements();) {
            entry = (WordlistEntry) (e.nextElement());
            if (null != entry) {
                result += entry.dump() + "\n";
                announce(result + "\n");
            }
        }
        return result;
    }

    /* public String dump ()*/
    /**
     * shutdown() here does nothing.
     *
     * @see com.SoftWoehr.SoftWoehr#shutdown()
     * @return always 0
     */
    @Override
    public int shutdown() {
        return 0;
    }

    /**
     * Save a Wordlist to a file using Java serialization.
     *
     * @param f The file to save to
     * @throws java.io.FileNotFoundException if file cannot be created
     * @throws java.io.IOException if I/O error occurs
     */
    public void save(java.io.File f)
            throws java.io.FileNotFoundException,
            java.io.IOException {
        java.io.FileOutputStream fos = new java.io.FileOutputStream(f);
        java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
        fos.close();
    }

    /**
     * Load a saved Wordlist from a file using Java serialization.
     *
     * @param f The file to load from
     * @return The loaded Wordlist
     * @throws java.io.FileNotFoundException if file not found
     * @throws java.io.IOException if I/O error occurs
     * @throws java.lang.ClassNotFoundException if class cannot be found during deserialization
     */
    public static Wordlist reload(java.io.File f)
            throws java.io.FileNotFoundException,
            java.io.IOException,
            java.lang.ClassNotFoundException {
        java.io.FileInputStream fis = new java.io.FileInputStream(f);
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(fis);
        Wordlist w = (Wordlist) ois.readObject();
        ois.close();
        fis.close();
        return w;
    }

    /**
     * Create a default wordlist for initial engine. Each invocation of this
     * method results in a unique instance. Alternatively, the list could be a
     * static member, but then it had better be read-only to interpreter
     * instances due to its being shared.
     *
     * @return The default Wordlist itself
     */
    public static Wordlist defaultWordlist() {
        Wordlist defaultList = new Wordlist("FIJI");
        try {
            defaultList.put(new Primitive("arf", "arf"));
            defaultList.put(new Primitive("noop", "noop"));
            defaultList.put(new Primitive("depth", "depth"));
            defaultList.put(new Primitive("dup", "dup"));
            defaultList.put(new Primitive("drop", "drop"));
            defaultList.put(new Primitive("swap", "swap"));
            defaultList.put(new Primitive("over", "over"));
            defaultList.put(new Primitive("rot", "rot"));
            defaultList.put(new Primitive("roll", "roll"));
            defaultList.put(new Primitive("pick", "pick"));
            defaultList.put(new Primitive(".s", "dot_s"));
            defaultList.put(new Primitive(".c", "dot_c"));
            defaultList.put(new Primitive(".sc", "dot_sc"));
            defaultList.put(new Primitive(".", "dot"));
            defaultList.put(new Primitive("..", "dotdot"));
            defaultList.put(new Primitive(".r", "dot_r"));
            defaultList.put(new Primitive("warm", "warm"));
            defaultList.put(new Primitive("cold", "cold"));
            defaultList.put(new Primitive("quit", "quit"));
            defaultList.put(new Primitive(">class", "getStackEntryClass"));
            defaultList.put(new Primitive(">string", "stackEntryToString"));
            defaultList.put(new Primitive("(", "javaArgs"));
            defaultList.put(new Primitive(",", "accumulateArg"));
            defaultList.put(new Primitive(")", "callJava"));
            defaultList.put(new Primitive("'", "lexeme"));
            defaultList.put(new Primitive("class", "classForName"));
            defaultList.put(new Primitive("bye", "bye"));
            defaultList.put(new Primitive("sysexit", "sysexit"));
            defaultList.put(new Primitive("true", "pushTrue"));
            defaultList.put(new Primitive("null", "pushNull"));
            defaultList.put(new Primitive("false", "pushFalse"));
            defaultList.put(new Primitive("()", "castParam"));
            defaultList.put(new Primitive("primitive", "classToPrimitiveType"));
            defaultList.put(new Primitive(">primitive", "stackEntryToPrimitive"));
            defaultList.put(new Primitive("(primitive)", "stackEntryToPrimParam"));
            defaultList.put(new Primitive("Long>intparam", "longToIntParam"));
            defaultList.put(new Primitive("find", "find"));
            defaultList.put(new Primitive("execute", "execute"));
            defaultList.put(new Primitive("compile", "compile"));
            defaultList.put(new Primitive("[" /* Special compilation semantics.*/,
                    "leftBracket"));
            defaultList.put(new Primitive("]" /* Special compilation semantics.*/,
                    "rightBracket"));
            defaultList.put(new Primitive("base", "popBase"));
            defaultList.put(new Primitive("base?", "pushBase"));
            defaultList.put(new Primitive("state", "doState"));
            defaultList.put(new Primitive("immediate?", "isImmediate"));
            defaultList.put(new Primitive("immediate"/* Special compilation semantics.*/,
                    "setCurrentImmediate"));
            defaultList.put(new Primitive("\"" /* Special compilation semantics.*/,
                    "doubleQuote", "compileDoubleQuote"));
            defaultList.put(new Primitive("`" /* Special compilation semantics.*/,
                    "backTick", "compileBackTick"));
            defaultList.put(new Primitive("\\" /* Special compilation semantics.*/,
                    "comment",
                    "comment"));
            defaultList.put(new Primitive("exit", "doExit"));
            defaultList.put(new Primitive("not", "not"));
            defaultList.put(new Primitive("and", "and"));
            defaultList.put(new Primitive("or", "or"));
            defaultList.put(new Primitive("xor", "xor"));
            defaultList.put(new Primitive("=", "isEqual"));
            defaultList.put(new Primitive(">", "greaterThan"));
            defaultList.put(new Primitive("<", "lessThan"));
            defaultList.put(new Primitive("<>", "isUnequal"));
            defaultList.put(new Primitive("+", "add"));
            defaultList.put(new Primitive("-", "sub"));
            defaultList.put(new Primitive("*", "mul"));
            defaultList.put(new Primitive("/", "div"));
            defaultList.put(new Primitive("mod", "mod"));
            defaultList.put(new Primitive("array", "array"));
            defaultList.put(new Primitive("dimarray", "dimarray"));
            defaultList.put(new Primitive("variable", "newVariable"));
            defaultList.put(new Primitive("!", "store"));
            defaultList.put(new Primitive("@", "fetch"));
            defaultList.put(new Primitive("value", "newValue"));
            defaultList.put(new Primitive("to" /* Special compilation semantics.*/,
                    "toValue", "compileToValue"));
            defaultList.put(new Primitive("{", "newAnonymousDefinition"));
            defaultList.put(new Primitive("}",
                    "concludeAnonymousDefinition" /* Special compilation semantics.*/));
            defaultList.put(new Primitive(":", "newDefinition"));
            defaultList.put(new Primitive(";" /* Special compilation semantics.*/,
                    "compileOnly", "concludeDefinition"));
            defaultList.put(new Primitive("if" /* Special compilation semantics.*/,
                    "compileConditionalBranch"));
            defaultList.put(new Primitive("else"/* Special compilation semantics.*/,
                    "compileAndResolveBranch"));
            defaultList.put(new Primitive("then"/* Special compilation semantics.*/,
                    "resolveBranch"));
            defaultList.put(new Primitive("begin"/* Special compilation semantics.*/,
                    "pushUnconditionalBranch"));
            defaultList.put(new Primitive("again"/* Special compilation semantics.*/,
                    "compileUnconditionalBackwardsBranch"));
            defaultList.put(new Primitive("until"/* Special compilation semantics.*/,
                    "compileConditionalBackwardsBranch"));
            defaultList.put(new Primitive("while"/* Special compilation semantics.*/,
                    "compileConditionalBranch"));
            defaultList.put(new Primitive("repeat"/* Special compilation semantics.*/,
                    "compileUnconditionalBackwardsBranch"));
            defaultList.put(new Primitive("cr", "cr"));
            defaultList.put(new Primitive("do" /* Special compilation semantics.*/,
                    "compileDo"));
            defaultList.put(new Primitive("loop"/* Special compilation semantics.*/,
                    "compileLoop"));
            defaultList.put(new Primitive("+loop"/* Special compilation semantics.*/,
                    "compilePlusLoop"));
            defaultList.put(new Primitive("leave", "doLeave"));
            defaultList.put(new Primitive("index", "index"));
            defaultList.put(new Primitive("verbose", "runtimeVerbose"));
            defaultList.put(new Primitive("decompile", "decompile"));
            defaultList.put(new Primitive("system", "system"));
            defaultList.put(new Primitive("interpret", "interpret"));
            defaultList.put(new Primitive("load", "load"));
            defaultList.put(new Primitive("save", "save"));
            defaultList.put(new Primitive("reload", "reload"));
            defaultList.put(new Primitive("version", "version"));
            defaultList.put(new Primitive("getorder", "getOrder"));
            defaultList.put(new Primitive("setorder", "setOrder"));
            defaultList.put(new Primitive("words", "words"));
            defaultList.put(new Primitive("forget", "forget"));
            defaultList.put(new Primitive("discard", "discard"));
            defaultList.put(new Primitive("see", "decompile"));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            System.err.println("Catastrophe in Wordlist.defaultWordlist() " + ex);
        }
        return defaultList;
    }

    /**
     * Demonstrate <code>Wordlist</code>.
     *
     * @param argv passed to GetArgs
     */
    public static void main(String argv[]) {

        GetArgs myArgs = new GetArgs(argv);
        /* Assimilate the command line. */
        Wordlist theWordlist = Wordlist.defaultWordlist();
        /* Instance of Wordlist we're demoing.*/

        // GPL'ed SoftWoehr announces itself.
        System.out.println("Wordlist, Copyright (C) 1999, 2016 Jack J. Woehr.");
        System.out.println("Wordlist comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");

        // See if user passed in the -v flag to request verbosity.
        for (int i = 0; i < myArgs.optionCount(); i++) {
            if (myArgs.nthOption(i).getOption().substring(1, 2).equals("v")) {
                theWordlist.setVerbose(true);
            }
            /* End if*/
        }

        // Your code goes here.
        // -------------------
        theWordlist.discard("arf");
        System.out.println("Discard completed.");
        System.out.println("Words are " + theWordlist.words());
        System.out.println("Re-instating arf.");
        try {
            theWordlist.put(new Primitive("arf", "arf"));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            System.err.println("Catastrophe in Wordlist.main() " + ex);
        }
        System.out.println("Words are " + theWordlist.words());
        System.out.println(theWordlist.dump());
        // -------------------

    }
}
/* End of Wordlist class*/

/**
 * Class representing a wordlist entry. A wordlist entry has a name and a stack
 * of definitions.
 */
class WordlistEntry implements Serializable {

    private Semantic semantic;
    /**
     * This is where previous Semantics for a name are stored when a new
     * Semantic for the same name is pushed.
     */
    private Stack semanticStack;

    /**
     * Create a WordlistEntry on a Semantic.
     *
     * @param s The semantic.
     */
    public WordlistEntry(Semantic s) {
        semantic = s;
        semanticStack = null;
    }

    /**
     * Get the active semantic for the word.
     *
     * @return the active semantic for the word.
     */
    public Semantic getSemantic() {
        return semantic;
    }

    /**
     * Get name of word.
     *
     * @return name of word.
     */
    public String getName() {
        return semantic.getName();
    }

    /**
     * Push a new Semantic for the word.
     *
     * @param s the new semantic
     */
    public void push(Semantic s) {
        if (null == semanticStack) {
            semanticStack = new Stack();
        }
        semanticStack.push(semantic);
        semantic = s;
    }

    /**
     * Pop a Semantic for the word. If no previous Semantic is stacked, return
     * null to signal that the WordlistEntry itself should be removed from the
     * Wordlist.
     *
     * @return the popped semantic
     */
    public Semantic pop() {
        Semantic previousTop = semantic;
        if (null != semanticStack)/* If we have a stack of previous Semantic(s) ...*/ {
            if (!semanticStack.empty()) /* ... and there's one on the stack ...*/ {
                semantic = (Semantic) semanticStack.pop();
                /* ... then pop it to be the new active Semantic.*/
            }
            if (semanticStack.empty()) /* Don't keep empty stacks, memory impact.*/ {
                semanticStack = null;
            }
        } else /* No stack of previous Semantics.*/ {
            previousTop = null;
            /* Signal to caller to remove this entry from the Wordlist.*/
        }
        return previousTop;
    }

    /**
     * String dump of the word list entry.
     *
     * @return the dump
     */
    public String dump() {
        return "Word: " + getName() + " " + getSemantic().toString();
    }
}
/* End of WordlistEntry class*/

 /*  End of Wordlist.java */
