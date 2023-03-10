/* ShellTextArea.java ...  */
/** ****************************************** */
/* Copyright *C* 1999, 2001, 2016            */
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
package com.SoftWoehr.FIJI.base.desktop;

import java.awt.*;
import com.SoftWoehr.SoftWoehr;
import com.SoftWoehr.util.*;
import com.SoftWoehr.FIJI.base.awt.*;
import com.SoftWoehr.FIJI.base.desktop.shell.*;

/**
 * The AWT Text area inside the older FIJI AWT gui
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.2 $
 */
public class ShellTextArea extends TextArea implements SoftWoehr, verbose {

    /**
     * Revision level
     */
    private static final String rcsid = "$Id: ShellTextArea.java,v 1.2 2016-11-10 02:07:44 jwoehr Exp $";

    /**
     * Implements com.SoftWoehr.SoftWoehr
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
     * Interpreter instance associated with this ShellTextArea.
     */
    private interpreter myInterpreter;

    /**
     * TextOutputStream in use.
     */
    private TextAreaOutputStream myTextAreaOutputStream;

    /**
     * ShellFrame instance associated with this ShellTextArea.
     */
    private ShellFrame myShellFrame;

    /**
     * Arity/0 ctor.
     */
    public ShellTextArea() {
        this(null);
    }

    /**
     * Arity/1 ctor
     *
     * @param frame
     */
    public ShellTextArea(ShellFrame frame) {
        reinit(frame);
    }

    /**
     * Make sure shutdown-aware member objects know we're quitting.
     *
     * @see com.SoftWoehr.SoftWoehr
     */
    @Override
    public int shutdown() {
        myInterpreter.shutdown();
        return 0;
    }

    /**
     * Re-initialize object discarding previous state.
     *
     * @param frame
     */
    public final void reinit(ShellFrame frame) {
        myShellFrame = frame;
        myInterpreter = new interpreter();
        myTextAreaOutputStream = new TextAreaOutputStream(this);
        myInterpreter.setOutput(myTextAreaOutputStream);
        myInterpreter.output("FIJI ForthIsh Java Interpreter "
                + engine.fijiVersion()
                + "\n"
        );
        myInterpreter.output("Copyright 1999, 2000 by Jack J. Woehr\n");
        myInterpreter.output("FIJI comes with ABSOLUTELY NO WARRANTY.\n");
        myInterpreter.output("Please see the file COPYING and/or COPYING.LIB which\n");
        myInterpreter.output("you should have received with this software.\n");
        myInterpreter.output("This is free software, and you are welcome to redistribute it\n");
        myInterpreter.output("under certain conditions enumerated in COPYING and/or COPYING.LIB.\n");
    }

    /**
     * Load a file in the interpreter.
     *
     * @param filename
     */
    public void load(String filename) {
        myInterpreter.load(filename);
    }

    /**
     * Set interpreter numeric base.
     *
     * @param base
     */
    public void setBase(int base) {
        myInterpreter.setBase(base);
    }

    /**
     * Pass selected text to interpreter, then prompt.
     */
    public void interpretSelected() {
        String text = getSelectedText();
        interpret(text);
    }

    /**
     * Pass a string to the interpreter, then prompt.
     *
     * @param text
     */
    public void interpret(String text) {
        setCaretPosition(getText().length());
        myInterpreter.output(" ");
        myInterpreter.interpret(text);
        prompt(false);
        if (testKillFlag()) /* ! Crude, should be part of Shutdown.*/ {
            if (null != myShellFrame) {
                myShellFrame.shutdown();
                /* Crude, should be part of Shutdown.*/
            }
        }
        /* End if*/
    }

    /* End interpret()*/
    /**
     * Pass line the user just hit Enter on to the interpter, then prompt.
     */
    public void interpretLastLine() {
        String text = getLastLine();
        interpret(text);
    }

    /**
     * Extract the most recent line before an Enter hit.
     *
     * @return
     */
    public String getLastLine() {
        myInterpreter.output(" "); // HACK! HACK! HACK!
        /*! There seems to be a difference in caret */
 /*! positioning in different JDKs' AWTs.    */
 /*! This hack fixes truncated calc'ings of  */
 /*! The last line of text in TextArea.      */
        announce("Caret position is: " + getCaretPosition());
        String text = getText();
        /* Text in the buffer.              */
        text = text.substring(0 /* Only before caret.*/,
                Math.max(0, getCaretPosition() - 1))
                .trim();

        /* Backwards-moving head-of-text pointer.*/
        int i = text.length();
        for (; i > 0; --i) /* find previous newline*/ {
            char c = text.charAt(i - 1);
            if ('\r' == c || '\n' == c) {
                break;
            }
        }
        /* End for*/
        text = text.substring(i);
        /* Extract the last line.*/
        if (text.equals("oK")) {
            text = "";
            /* Don't interpret our prompt!*/
        }
        /* End if*/
        return text;
    }

    /**
     * Display a prompt with an optional prepended newline
     *
     * @param newlineOrNone
     */
    public void prompt(boolean newlineOrNone) {
        if (newlineOrNone) {
            myInterpreter.output("\n");
        }
        myInterpreter.prompt();
        myInterpreter.output("\n");
        setCaretPosition(getText().length()); // Is this really necessary?
    }

    /**
     * See if interpreter has had bye called. true means bye.
     *
     * @return
     */
    public boolean testKillFlag() {
        return myInterpreter.getKillFlag();
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
     * Sets the ShellTextArea and its interpreter verbose.
     *
     * @param tf
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     */
    @Override
    public void setVerbose(boolean tf) {
        isverbose = tf;
        myInterpreter.setVerbose(tf);
    }

    /**
     * Emit a string message if set verbose.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s string to announce if verbose
     */
    @Override
    public void announce(String s) {
        v.announce(s);
    }

    /**
     * Demonstrate <code>ShellTextArea</code>.
     *
     * @param argv
     */
    public static void main(String argv[]) {

        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */

 /* GPL'ed SoftWoehr announces itself. */
        System.out.println("ShellTextArea, Copyright (C) 1999 Jack J. Woehr.");
        System.out.println("ShellTextArea comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");

        // Test code goes here.
        // -------------------
        GetArgs.main(argv);
        // -------------------
    }
}
/* End of ShellTextArea*/
 /*  End of ShellTextArea.java */
