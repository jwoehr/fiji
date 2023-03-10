/* interpreter.java ...  */
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

import com.SoftWoehr.FIJI.base.Exceptions;
import java.io.*;
import java.util.*;

import com.SoftWoehr.SoftWoehr;
import com.SoftWoehr.util.*;

/**
 * A class to interpret input of a stream of FIJI commands.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.4 $
 */
public final class interpreter implements SoftWoehr, verbose {

    /**
     * Revision level
     */
    private static final String rcsid = "$Id: interpreter.java,v 1.4 2016-11-15 13:42:00 jwoehr Exp $";

    /**
     * Implements com.SoftWoehr.SoftWoehr
     *
     * @return The rcs string
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
    private verbosity v = new verbosity(this);

    /**
     * An execution engine
     */
    private engine myEngine;

    /**
     * An input stream
     */
    private InputStream currentInput;

    /**
     * An output stream
     */
    private OutputStream currentOutput;

    /**
     * Output stream writer to handle host codepage issues.
     */
    private OutputStreamWriter outputStreamWriter;

    /**
     * Encoding used by OutputStreamWriter
     */
    private String outputStreamEncoding;

    /**
     * The tokenizer which gets our next lexeme.
     */
    private StringTokenizer st;

    /**
     * Stack needed so we can nest interpretation, e.g., when we load a file.
     */
    private Stack tokenizerStack;

    /**
     * We want to exit if this is true.
     */
    private boolean killFlag = false;

    /**
     * We want to quit interp loop if this is true.
     */
    private boolean quitFlag = false;

    /**
     * Number base for interpretation.
     */
    private int base = 10;

    private String defaultDelimiters = " \t\n\r";

    /**
     * Arity/0 ctor.
     */
    public interpreter() {
        reinit();
    }

    /**
     * Reset the interpreter, losing all previous state.
     */
    public void reinit() {
        myEngine = new engine(this);
        v = new verbosity(this);
        warmReset();
    }

    /**
     * shutdown() here closes the output streams.
     *
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return always 0
     */
    @Override
    public int shutdown() {
        closeCurrentInput();
        closeCurrentOutput();
        return 0;
    }

    /**
     * Get the engine associated with this interpreter.
     *
     * @return the engine
     */
    public engine getEngine() {
        return myEngine;
    }

    /**
     * Close current input stream.
     */
    public void closeCurrentInput() {
        try {
            if (null != currentInput) {
                currentInput.close();
                currentInput = null;
            }
            /* End if*/
        } /* End try*/ catch (IOException e) {
            e.printStackTrace(System.err);
        }
        /* End catch*/
    }

    /**
     * Close current output stream.
     */
    public void closeCurrentOutput() {
        try {
            if (null != currentOutput) {
                currentOutput.close();
                currentOutput = null;
            }
        } /* End try*/ catch (Exception e) {
            e.printStackTrace(System.err);
        }
        /* End catch*/
    }

    /**
     * Set the kill flag.
     *
     * @param tf <code>true</code> means exit the interpreter loop
     */
    public void setKillFlag(boolean tf) {
        killFlag = tf;
    }

    /**
     * Get the kill flag.
     *
     * @return the flag which means exit the interpreter loop
     */
    public boolean getKillFlag() {
        return killFlag;
    }

    /**
     * Set the quit flag.
     *
     * @param tf <code>true</code> means do a 'quit'
     */
    public void setQuitFlag(boolean tf) {
        quitFlag = tf;
    }

    /**
     * Get the quit flag.
     *
     * @return the flag which means do a 'quit'
     */
    public boolean getQuitFlag() {
        return quitFlag;
    }

    /**
     * Set the interpreter numeric base.
     *
     * @param i the base
     */
    public void setBase(int i) {
        base = i;
    }

    /**
     * Get the interpreter numeric base.
     *
     * @return the base
     */
    public int getBase() {
        return base;
    }

    /**
     * Set the string tokenizing default delimiters.
     *
     * @param delims the delimiters for lexing
     */
    public void setDefaultDelimiters(String delims) {
        defaultDelimiters = delims;
    }

    /**
     * Get the string tokenizing default delimiters.
     *
     * @return the delimiters for lexing
     */
    public String getDefaultDelimiters() {
        return defaultDelimiters;
    }

    /**
     * Set the current input
     *
     * @param i current input stream
     */
    public void setInput(InputStream i) {
        currentInput = i;
    }

    /**
     * Get the current input
     *
     * @return current input stream
     */
    public InputStream getInput() {
        return currentInput;
    }

    /**
     * Set the current output
     *
     * @param o current output stream
     */
    public void setOutput(OutputStream o) {
        currentOutput = o;
        if (getOutputStreamEncoding() == null) {
            outputStreamWriter = new OutputStreamWriter(currentOutput);
        } else {
            try {
                outputStreamWriter
                        = new OutputStreamWriter(currentOutput,
                                getOutputStreamEncoding()
                        );
            } /* End try*/ catch (Exception e) {
                e.printStackTrace(System.err);
            }
            /* End catch*/
        }
        /* End if*/
    }

    /**
     * Get the current output
     *
     * @return current output stream
     */
    public OutputStream getOutput() {
        return currentOutput;
    }

    /**
     * Get the output stream writer
     *
     * @return current output stream writer
     */
    protected OutputStreamWriter getOutputStreamWriter() {
        return outputStreamWriter;
    }

    /**
     * Set output stream encoding to a given codepage. One must subsequently do
     * a <code>setOutput()</code> to make the codepage take effect.
     *
     * @see setOutput
     * @param codepage the codepage
     */
    public void setOutputStreamEncoding(String codepage) {
        outputStreamEncoding = codepage;
    }

    /**
     * Get output stream codepage name.
     *
     * @return output stream codepage name.
     */
    public String getOutputStreamEncoding() {
        return outputStreamEncoding;
    }

    /**
     * Get next lexeme in string being interpret()'ed using the delimiter set
     * passed in the 'delims' argument.
     *
     * @param delims the delimiters
     * @return the string
     */
    public String nextLexeme(String delims) {
        String s = null;
        if (null != st) {
            try {
                s = st.nextToken(delims);
            } /* End try*/ catch (NoSuchElementException e) {
                /* Do nothing .. thus method returns null. */
                s = null;
                /* For "good luck".*/
 /* Does nextToken() "damage" `s' before throw? Spec unclear.*/
            }
            /* End catch*/
        }
        /* End if*/
        return s;
    }

    /* public String nextLexeme(String delims)*/
    /**
     * Get next lexeme in string being interpret()'ed using default delims.
     *
     * @return the next lexeme string
     */
    public String nextLexeme() {
        return nextLexeme(defaultDelimiters);
    }

    /* public String nextLexeme()*/
    /**
     * Get next lexeme in string being interpret()'ed using the delimiter set
     * passed in the 'delims' argument, with the option of consuming the
     * delimiter. <b> NOTE </b> that this is only useful for non-blank delims,
     * since it presumes a blank will be left over at the front of the string.
     *
     * <code>class StringTokenizer</code> in Java 1.1.7 and before is not a
     * full-bodied lexing facility.
     *
     * @param delims the delimiters
     * @param consumeDelim <code>true</code> means consume delims
     * @return the next lexeme string
     */
    public String nextLexeme(String delims, boolean consumeDelim) {
        String s = null;
        if (null != st) {
            s = st.nextToken(delims);
            /* Get actual token*/
            if (null != s) {
                s = s.substring(1, s.length());
                /* Strip leading blank*/
            }
            /* End if*/
            if ((0 != st.countTokens()) && (true == consumeDelim))/* Consume delim.*/ {
                st.nextToken(defaultDelimiters);
            }
            /* End if*/
        }
        /* End if*/
        return s;
    }

    /* nextLexeme(String delims, boolean consumeDelim)*/
    /**
     * Number of lexemes left in string being interpret()'ed .
     *
     * @return num lexemes left
     */
    public int countLexemes() {
        int count = 0;
        if (null != st) {
            count = st.countTokens();
        }
        /* End if*/
        return count;
    }

    /**
     * Output a string
     *
     * @param s The String
     */
    public void output(String s) {
        try {
            getOutputStreamWriter().write(s);
            getOutputStreamWriter().flush();
        } /* End try*/ catch (IOException e) {
            e.printStackTrace(System.err);
        }
        /* End catch*/
    }

    /**
     * Issue the prompt as appropriate
     */
    public void prompt() {
        if (engine.INTERPRETING == myEngine.state) /* We're interpreting*/ {
            output("\noK ");
        } else /* We're compiling.*/ {
            output("\n(...) ");
        }
        /* End if*/
    }

    /**
     * Something for the engine to call when it does a warm().
     */
    public void warmReset() {
        st = null;
        tokenizerStack = new Stack();
        setKillFlag(false);
        setQuitFlag(false);
        setBase(10);
    }

    /**
     * Interpret one String.
     *
     * @param s String to interpret as program text
     */
    public void interpret(String s) {
        announce("String to interpret is: " + s);
        String aLexeme;
        /* Holds a lexeme as we examine it. */
        Semantic semantic;/* Holds a semantic as we decide what to do with it.*/

        setKillFlag(false);
        /* Indicate we're in business.*/
        setQuitFlag(false);
        /* Indicate we're in business.*/

 /* Interpret the passed-in string */
        if (s != null) /* Don't try to tokenize a null string.*/ {
            tokenizerStack.push(st);
            /* Save (possibly null) current tokenizer.*/
            st = new StringTokenizer(s /* Open on the input string.*/,
                    defaultDelimiters /* List of delimiters.*/,
                    false /* Delim not in return.*/
            );

            while ((countLexemes() > 0) /* For all lexemes in string.*/
                    && !killFlag
                    && !quitFlag) {
                aLexeme = nextLexeme();
                /* Grab next one.*/
                announce("Lexeme is: " + aLexeme);
                semantic = myEngine.findSemantic(aLexeme);
                /* Find in wordlist(s).*/
                announce("Semantic is: " + semantic);

                if (null != semantic) /* We found lexeme as dictionary entry.*/ {
                    if (engine.INTERPRETING == myEngine.state) /* We're interpreting*/ {
                        try {
                            announce("Executing interpretive semantics for "
                                    + semantic.toString()
                            );
                            semantic.execute(myEngine);
                            /* So do it.*/
                        } /* End try*/ catch (Exceptions.desktop.shell.BadDefinitionExecute | Exceptions.desktop.shell.BadPrimitiveExecute e) {
                            e.printStackTrace(System.err);
                            output(e.getMessage());
                            myEngine.warm();
                        }
                        /* End catch*/
                    } /* End if*/ else /* We're compiling.*/ {
                        try {
                            announce("Executing compile semantics for "
                                    + semantic.toString()
                            );
                            semantic.compile(myEngine);
                            /* So compile it.*/
                        } /* End try*/ catch (Exception e) {
                            e.printStackTrace(System.err);
                            output(e.getMessage());
                            myEngine.warm();
                        }
                        /* End catch*/
                    }
                    /* End if*/
                } else /* We didn't find the lexeme as a dictionary entry.*/ {
                    if (engine.INTERPRETING == myEngine.state)/* We're interpreting.*/ {
                        Long a;
                        try /* Try to make it a long.*/ {
                            a = Long.valueOf(aLexeme, base);
                            myEngine.stack.push(a);
                            /* Push the long.*/
                        } /* End try*/ catch (NumberFormatException e) /* Wasn't a long.*/ {
                            myEngine.stack.push(aLexeme);
                            /* Push the lexeme.*/
                        }
                        /* End catch*/
                    } else /* We're compiling.*/ {
                        long a = 0;
                        try /* Try to make it a long.*/ {
                            a = Long.parseLong(aLexeme);
                            try /* Try to compile the long as a literal Long.*/ {
                                myEngine.compileLiteral(new Long(a));
                            } /* End try*/ catch (Exception x) {
                                announce("interpreter had problem compiling literal Long.");
                                announce("Lexeme was: " + aLexeme);
                                x.printStackTrace(System.err);
                                myEngine.warm();
                            }
                            /* End catch*/
                        } /* End try*/ catch (NumberFormatException e) /* Wasn't a long.*/ {
                            try /* Try to compile the lexeme as a string literal.*/ {
                                myEngine.compileLiteral(aLexeme);
                            } /* End try*/ catch (Exceptions.desktop.shell.BadDefinitionCompile | Exceptions.desktop.shell.BadDefinitionExecute | Exceptions.desktop.shell.BadPrimitiveCompile | Exceptions.desktop.shell.BadPrimitiveExecute | ClassNotFoundException | NoSuchMethodException x) {
                                announce("interpreter had problem compiling literal String.");
                                announce("Lexeme was: " + aLexeme);
                                x.printStackTrace(System.err);
                                myEngine.warm();
                            }
                            /* End catch*/
                        }
                        /* End catch*/
                    }
                    /* End "we're compiling" */ /* End if*/
                }
                /* End "didn't find" */ /* End if*/
            }
            /* End while*/
            try {
                if (!tokenizerStack.isEmpty()) {
                    st = (StringTokenizer) tokenizerStack.pop();/* Previous tokenizer.*/
                }
            } /* End try*/ catch (Exception e) {
                e.printStackTrace(System.err);
                myEngine.warm();
            }
            /* End catch*/
        }
        /* End if (interpretation string non-null)*/
    }

    /* End of interpreter.interpret*/
    /**
     * Load a file as FIJI source.
     *
     * @param filename file to load
     */
    public void load(String filename) {
        myEngine.push(filename);
        myEngine.load();
    }

    /**
     * Display main() command line usage.
     */
    public static void usage() {
        System.err.println("Usage:");
        System.err.println(" java com.SoftWoehr.FIJI.base.desktop.shell.interpreter [-b base] [-o output_codepage] [-v] [file file ...]");
        System.err.println(" -q                 .. enables quiet mode, disable startup banner messages. ");
        System.err.println(" -v                 .. enables verbose mode, weird, unintelligible debug msgs.");
        System.err.println(" -o output_codepage .. for VM/ESA with Java 1.1.4 use Cp1407.");
        System.err.println(" -b base            .. where base is numeric input base, e.g. 8 16 0x10 etc.");
        System.err.println(" file file ...      .. these files will be loaded as FIJI source code.");
    }

    /**
     * true if verbose
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return verbosity
     */
    @Override
    public boolean isVerbose() {
        return isverbose;
    }

    /**
     * As this class overloads setVerbose() it control engine's verbosity, too.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param tf verbosity
     */
    @Override
    public void setVerbose(boolean tf) {
        isverbose = tf;
        if (myEngine != null) {
            myEngine.setVerbose(tf);
            announce("Setting engine verbose.");
        }
        /* End if*/
    }

    /**
     * Announce a string if verbose.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s string to announce
     */
    @Override
    public void announce(String s) {
        v.announce(s);
    }

    /**
     * Demonstrate <code>interpreter</code>.
     *
     * @param argv Arguments to interpreter
     */
    public static void main(String argv[]) {
        interpreter i;
        InputStreamReader isr;
        BufferedReader br = null;
        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
 /* ATH */
        boolean quiet = false;

        /* Create the interpreter instance. */
        try {
            i = new interpreter();
        } /* End try*/ catch (Exception e) {
            e.printStackTrace(System.err);

            /*  Does java reflect to which class main() belongs to without
         *  exhaustively enumerating candidate classes and testing them?
         *  The stack trace.
             */
            throw new com.SoftWoehr.FIJI.base.Error.bAcKtOmain(e);
        }
        /* End catch*/

 /* Examine the arguments. */
        //    try
        //      {
        for (int x = 0; x < myArgs.optionCount(); x++) {
            Argument a = myArgs.nthOption(x);

            switch (a.option) {
                case "-v":
                    i.setVerbose(true);
                    i.announce("Verbose mode set.\n");
                    break;
                case "-b":
                    if (a.argument != null) {
                        try {
                            i.setBase(Integer.decode(a.argument));
                        } /* End try*/ catch (NumberFormatException e) {
                            e.printStackTrace(System.err);
                            throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase(e);
                        }
                        /* End catch*/
                    } else {
                        String s = "(null) presented for interpreter numeric base.";
                        System.out.println(s);
                        throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase(s, null);
                    }
                    /* End if*/
                    break;
                /* End if*/
                case "-o":
                    if (a.argument != null) {
                        i.setOutputStreamEncoding(a.argument);
                    } else {
                        String s = "Bad output stream encoding proposed: " + a.option;
                        System.out.println(s);
                        throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadEncoding(s, null);
                    }
                    /* End if*/
                    break;
                case "-h":
                case "--":
                    interpreter.usage();
                    return;
                /* End if*/
                case "-q":
                    quiet = true;
                    break;
                default:
                    String s = "Bad option " + a.option + " " + a.argument;
                    System.err.println(s);
                    usage();
                    throw new com.SoftWoehr.FIJI.base.Error.bAdArGtOmain(s, null);
                /* End if*/
            }
        }
        /* End for*/
        //      }                                                          /* End try*/
        //    catch (Exception e)
        //      {
        //        e.printStackTrace(System.err);
        //        throw new com.SoftWoehr.FIJI.base.Error.bAcKtOmain(e);
        //      }                                                        /* End catch*/

        /* GPL announces itself. */
        if (quiet == false) {
            System.out.println("FIJI ForthIsh Java Interpreter " + engine.fijiVersion());
            System.out.println("Copyright (C) 1999, 2001, 2008, 2016 by Jack J. Woehr.");
            System.out.println("FIJI comes with ABSOLUTELY NO WARRANTY;");
            System.out.println("Please see the file COPYING and/or COPYING.LIB");
            System.out.println("which you should have received with this software.");
            System.out.println("This is free software, and you are welcome to redistribute it");
            System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");
        }
        /* Set up to run. */
        try {
            i.setOutput(System.out);/* Note this occurs after setOutputEncoding()*/
            i.setInput(System.in);
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
        } /* End try*/ catch (Exception e) {
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Error.bAcKtOmain(e);
        }
        /* End catch*/

 /* Now treat every argument as a file to load. */
        for (int j = 0; j < myArgs.argumentCount(); j++) {
            try {
                Argument a = myArgs.nthArgument(j);
                i.load(a.argument);
            } /* End try*/ catch (Exception e) {
                e.printStackTrace(System.err);
                break;
            }
            /* End catch*/
        }
        /* Done loading any source arguments.*/

 /* Begin to parse interactive input. */
        String tib = "";

        i.prompt();
        while (!i.killFlag) {
            try {
                tib = br.readLine();
            } /* End try*/ catch (EOFException e) {
                /* No more input.*/
                i.setKillFlag(true);
                break;
            } catch (IOException e) {
                e.printStackTrace(System.err);
                break;
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            /* End catch*/

            i.interpret(tib);
            if (!i.killFlag) {
                i.prompt();
            }
            /* End if*/
        }
        /* End while*/
 /* End while*/
        try {
            br.close();
        } /* End try*/ catch (IOException e) {
            e.printStackTrace(System.err);
        }
        // -------------------
        /* End catch*/
    }
}
/* End of interpreter class*/

 /*  End of interpreter.java */
