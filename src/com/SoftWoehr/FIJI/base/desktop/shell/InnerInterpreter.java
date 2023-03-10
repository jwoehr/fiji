/* InnerInterpreter.java ...  */
/** ****************************************** */
/* Copyright *C* 1999, 2001, 2016             */
 /* All Rights Reserved.                      */
 /* Jack J. Woehr jax@softwoehr.com           */
 /* http://www.softwoehr.com                  */
 /* http://fiji.sourceforge.net               */
 /* P.O. Box 51, Golden, Colorado 80402-0051  */
/** ****************************************** */
/*                                           */
 /*    This Program is Free SoftWoehr.        */
 /*                                           */
 /* THERE IS NO GUARANTEE, NO WARRANTY AT ALL */
/** ****************************************** */
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

import java.util.*;

import com.SoftWoehr.SoftWoehr;
import com.SoftWoehr.util.*;

/**
 * This is the interpreter of Definitions.
 *
 * This is the oddest class of the suite.
 *
 * Consider possible implementations of Definition interpretation.
 *
 * The Definition could keep its own interpretive pointer, but that would not be
 * re-entrant, since a Definition instance is unitary in a Wordlist no matter
 * who calls it.
 *
 * Going to the other extreme, interpretation implementation could easily grow
 * tediously procedural.
 *
 * What we have here is the phenomenon of two parallel return stacks, the Java
 * VM stack for one, and the other our stack of activation records which both
 * get popped at the tail of the (effectively) recursive call to
 * InnerInterpreter.interpret().
 *
 * This all could have been shoehorned into the engine, but breaking it out into
 * its own class makes it easier to keep straight while coding.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.3 $
 */
public final class InnerInterpreter implements SoftWoehr, verbose {

    /**
     * Revision level
     */
    private static final String rcsid = "$Id: InnerInterpreter.java,v 1.3 2016-11-08 02:30:24 jwoehr Exp $";

    /**
     * Implements com.SoftWoehr.SoftWoehr
     *
     * @return rcs string
     */
    @Override
    public String rcsId() {
        return rcsid;
    }

    /**
     * Flags whether we are in verbose mode.
     */
    private boolean isverbose = false;
    /**
     * Helper for verbose mode.
     */
    private final verbosity v = new verbosity(this);

    /**
     * Does the work of notifying shutdown clients.
     */
    private final ShutdownHelper shutdownHelper = new ShutdownHelper();

    /**
     * Nesting definitions.
     */
    private Stack returnStack;

    /**
     * Nested LOOPs.
     */
    private Stack loopStack;

    /**
     * Current definition + instruction pointer
     */
    private Interpretation interpretation;

    /**
     * The engine we're associated with.
     */
    private engine myEngine;

    /**
     * Arity/1 ctor. InnerInterpreter must be associated with an engine.
     *
     * @param e engine associated with this inner interpreter.
     */
    public InnerInterpreter(engine e) {
        reinit(e);
    }

    /**
     * Dump the return stack, effectively.
     *
     * @return String representation of this object.
     */
    @Override
    public String toString() {
        String result = "An InnerInterpreter with the following status:\n";

        result += "Current definition: ";
        if (null == interpretation) {
            result += "No current definition.";
        } else {
            result += interpretation.definition.toString();
        }
        /* End if*/
        result += "\nReturn stack:";

        Enumeration e = returnStack.elements();
        if (e.hasMoreElements()) {
            result += "\n";
            while (e.hasMoreElements()) {
                Interpretation i = (Interpretation) e.nextElement();
                result += i.definition.toString() + "\n";
            }
        } else {
            result += " Stack empty.\n";
        }
        /* End if*/
        return result;
    }

    /**
     * The InnerInterpreter notifies subcomponents of shutdown then shuts itself
     * down.
     *
     * @return <CODE>true</CODE> if okay to shutdown.
     */
    @Override
    public int shutdown() {
        shutdownHelper.shutdownClients();
        // Your shutdown code for this object goes here.
        // ...

        // ...
        // Your shutdown code for this object went there.
        return 0;
    }

    /**
     * Reinitialize the InnerInterpreter, discarding previous state.
     *
     * @param e The engine associated with this instance.
     */
    public void reinit(engine e) {
        myEngine = e;
        returnStack = new Stack();
        loopStack = new Stack();
        interpretation = null;
    }

    /**
     * Return engine with which this instance is associated.
     *
     * @return engine associated with this instance.
     */
    public engine getEngine() {
        return myEngine;
    }

    /**
     * Return current definition being interpreted by this instance.
     *
     * @return The definition under interpretation.
     */
    public Definition getCurrentDefinition() {
        Definition d = null;
        if (null != interpretation) {
            d = interpretation.definition;
        }
        /* End if*/
        return d;
    }

    /* public Definition getCurrentDefinition ()*/
    /**
     * Push into a Definition.
     *
     * @param d New definition for interpretation.
     */
    public void nest(Definition d) {
        if (null != interpretation) /* If we're in a Definition.*/ {
            returnStack.push(interpretation);/* Push it so we can nest into this one.*/
        }
        /* End if*/
        interpretation = new Interpretation(d);
        /* Now this becomes current.*/
    }

    /**
     * Pop out of a Definition.
     *
     * @return The Definition which had been under interpretation but is no
     * longer, having been discarded in favor of the popped def resuming
     * interpretation.
     */
    public Definition unnest() {
        Definition wasCurrent = interpretation.definition;
        if (returnStack.empty()) {
            interpretation = null;
        } else {
            interpretation = (Interpretation) returnStack.pop();
        }
        /* End if*/
        return wasCurrent;
    }

    /**
     * Run the current Definition
     *
     * @param d Definition to interpret
     * @throws
     * com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute
     * @throws
     * com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute
     */
    public void interpret(Definition d)
            throws com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadPrimitiveExecute,
            com.SoftWoehr.FIJI.base.Exceptions.desktop.shell.BadDefinitionExecute {
        nest(d);
        while (interpretation.index <= interpretation.definition.lastIndex()) {
            Semantic s
                    = /* Here's where the instruction pointer gets bumped.*/ interpretation.definition.nthSemantic(interpretation.index++);
            s.execute(myEngine);
        }
        /* End while*/
        unnest();
    }

    /**
     * Exit the current Definition.
     */
    public void exitCurrentDefinition() {
        interpretation.index = interpretation.definition.lastIndex() + 1;
    }

    /**
     * Add positive or negative delta to the instruction pointer. This is how
     * control flow works, in conjunction with ParameterizedPrimitive.
     *
     * Bump it past end and the definition just exits on the 'while' loop in
     * interpret().
     *
     * @param delta the increment to the IP.
     */
    public void bump(int delta) {
        interpretation.index
                = Math.min(Math.max(interpretation.index + delta, 0)/* Maybe throw on <0?*/,
                        interpretation.definition.lastIndex() + 1 /* Past end.*/
                );
        /* No real reason to min() it, 'while' loop just exits.*/
    }

    /**
     * Push the indices for a loop, checking for and avoiding loops commencing
     * with identical indices (32 bit loops are long!!!).
     *
     * @param limit Loop limit
     * @param index Loop index
     * @param egress Exit point for IP.
     */
    public void startLoop(int limit, int index, int egress) {
        announce("Pushing Loop on loop stack with limit "
                + limit
                + " and index "
                + index
                + " and egress "
                + egress
        );
        if (limit != index) {
            /* No '0 0 do .. loop' !!!*/
            loopStack.push(new Loop(limit, index, egress));
        } else {
            interpretation.index = egress;
            /* If loop indices identical, skip.*/
        }
        /* End if*/
    }

    /* public void startLoop (int limit, int index, int egress)*/
    /**
     * Exit the loop on hitting a 'leave'.
     */
    public void leaveLoop() {
        Loop l = (Loop) loopStack.pop();
        interpretation.index = l.egress;
    }

    /**
     * Increment a loop record and return true if done.
     *
     * @return <CODE>true</CODE> iff the loop is done.
     */
    public boolean loop() {
        boolean result = false;
        Loop l = (Loop) loopStack.pop();
        result = l.loop();
        if (!result) {
            loopStack.push(l);
        }
        /* End if*/
        return result;
    }

    /* public boolean loop ()*/
    /**
     * Increment a loop record by an increment and return true if done.
     *
     * @param increment increment to plusloop by
     * @return <CODE>true</CODE> iff done.
     */
    public boolean plusLoop(int increment) {
        boolean result;
        Loop l = (Loop) loopStack.pop();
        result = l.plusLoop(increment);
        announce("PlusLoop index="
                + l.index
                + " increment was "
                + increment
                + " limit= "
                + l.limit
                + " result is "
                + result
        );
        if (!result) {
            loopStack.push(l);
        }
        /* End if*/
        return result;
    }

    /* public boolean plusLoop (int increment)*/
    /**
     * Return the loop index for the i'th loop.
     *
     * @param i nesting level of the desired loop index, zero 0 based.
     * @return The index for that i'th loop.
     */
    public int ithIndex(int i) {
        int size = loopStack.size();
        int desired = (size - 1) - i;
        Loop l = (Loop) loopStack.elementAt(desired);
        return l.index;
    }

    /**
     * Tests verbosity.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return <CODE>true</CODE> if this object is verbose
     */
    public boolean isVerbose() {
        return isverbose;
    }

    /**
     * Set this object verbose.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param tf <CODE>true</CODE> for verbose, <CODE>false</CODE> otherwise
     */
    @Override
    public void setVerbose(boolean tf) {
        isverbose = tf;
    }

    /**
     * Announce (output) if verbose.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s String to announce if verbose
     */
    @Override
    public void announce(String s) {
        v.announce(s);
    }

    /**
     * Demonstrate <code>InnerInterpreter</code>. Does nothing much currently.
     *
     * @param argv Argument to the inner interpreter test.
     */
    public static void main(String argv[]) {

        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
        //   InnerInterpreter theInnerInterpreter = new InnerInterpreter();/* Instance of InnerInterpreter we're demoing.*/

        /* GPL'ed SoftWoehr announces itself. */
        System.out.println("InnerInterpreter, Copyright (C) 1999, 2001, 2016 Jack J. Woehr.");
        System.out.println("InnerInterpreter comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");

        /* See if user passed in the -v flag to request verbosity. */
        for (int i = 0; i < myArgs.optionCount(); i++) {
            if (myArgs.nthOption(i).getOption().substring(1, 2).equals("v")) {
                //     theInnerInterpreter.setVerbose(true);
            }
            /* End if*/
        }

        // Your code goes here.
        // -------------------
        // -------------------
        return;
    }
}

/* End of InnerInterpreter class*/
/**
 * The entry on the return stack.
 */
class Interpretation {

    /**
     * Instruction pointer, i.e., offset in Semantic list.
     */
    public int index;

    /**
     * Current Definition.
     */
    public Definition definition;

    /**
     * Construct an inner interpretive entry from a Definition to be executed.
     *
     * @param d Definition to be contained in inner interpretive entry.
     */
    public Interpretation(Definition d) {
        definition = d;
        index = 0;
    }
}

/**
 * The entry on the loop stack.
 */
class Loop {

    /**
     * The loop limit .
     */
    public int limit;

    /**
     * The loop index.
     */
    public int index;

    /**
     * The one-past-the-loop offset in the definition.
     */
    public int egress;

    /**
     * Instance object with limit, index and place to exit the loop.
     *
     * @param limit Loop limit, loop exits when boundary between this limit and
     * (this limit - 1) is crossed in either direction.
     * @param index Start index of the loop.
     * @param egress IP offset in current def to jump to when done, point past
     * end of loop.
     */
    public Loop(int limit, int index, int egress) {
        this.limit = limit;
        this.index = index;
        this.egress = egress;
    }

    /**
     * Bumps index and returns true if the new index is equal to the limit.
     *
     * @return true if the new index is equal to the limit.
     */
    public boolean loop() {
        boolean result = false;
        if (++index == limit) {
            result = true;
        }
        /* End if*/
        return result;
    }

    /* public boolean loop ()*/
    /**
     * Bumps index by increment and returns true if index is crossed the
     * boundary between the loop limit and the loop limit minus one. Increment
     * can be pos or neg.
     *
     * @param increment Increment to plusloop by.
     * @return true if the exit condition has been reached by crossing the
     * boundary between the loop imit and the loop limit minus one.
     */
    public boolean plusLoop(int increment) {
        boolean result = false;
        if (index < limit) {
            index = index + increment;
            if (index >= limit) {
                result = true;
            }
            /* End if*/
        } else {
            /* Index >= limit*/
            index = index + increment;
            if (index < limit) {
                result = true;
            }
            /* End if*/
        }
        /* End if*/
        return result;
    }
    /* public boolean plusLoop(int increment)*/
}
/*  End of InnerInterpreter.java */
