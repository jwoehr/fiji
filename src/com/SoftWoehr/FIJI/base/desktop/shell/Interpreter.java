/**
 * interpreter.java - A class to interpret input of a stream of FIJI commands.
 *
 * Copyright (C) 1999, 2001, 2016
 * All Rights Reserved.
 * Jack J. Woehr jax@softwoehr.com
 * http://www.softwoehr.com
 * http://fiji.sourceforge.net
 * P.O. Box 82, Beulah, Colorado 81023 USA
 *
 * This Program is Free SoftWoehr.
 * THERE IS NO GUARANTEE, NO WARRANTY AT ALL
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package com.SoftWoehr.FIJI.base.desktop.shell;

import com.SoftWoehr.FIJI.base.Exceptions;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.SoftWoehr.SoftWoehr;
import com.SoftWoehr.util.*;

/**
 * A class to interpret input of a stream of FIJI commands.
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.4 $
 */
public final class Interpreter implements SoftWoehr, verbose {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(Interpreter.class.getName());

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


    /**
     * An execution engine
     */
    private Engine myEngine;

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
     * Current tokens being processed.
     */
    private String[] tokens;

    /**
     * Current position in tokens array.
     */
    private int tokenIndex;

    /**
     * Original input string being interpreted.
     */
    private String inputString;

    /**
     * Current position in the input string.
     */
    private int inputPosition;

    /**
     * Stack for nested interpretation contexts (tokens and indices).
     */
    private Stack<TokenizerContext> tokenizerStack;

    /**
     * Inner class to hold tokenizer context for nested interpretation.
     */
    private static class TokenizerContext {
        String[] tokens;
        int tokenIndex;
        String inputString;
        int inputPosition;

        TokenizerContext(String[] tokens, int tokenIndex, String inputString, int inputPosition) {
            this.tokens = tokens;
            this.tokenIndex = tokenIndex;
            this.inputString = inputString;
            this.inputPosition = inputPosition;
        }
    }

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

    // Default delimiters for tokenization
    private String defaultDelimiters = " \t\n\r";

    /**
     * Arity/0 ctor.
     */
    public Interpreter() {
        reinit();
    }

    /**
     * Reset the Interpreter, losing all previous state.
     */
    public void reinit() {
        myEngine = new Engine(this);
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
     * Get the engine associated with this Interpreter.
     *
     * @return the engine
     */
    public Engine getEngine() {
        return myEngine;
    }

    /**
     * Close current input stream.
     */
    public void closeCurrentInput() {
        if (null != currentInput) {
            try {
                currentInput.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error closing input stream", e);
            } finally {
                currentInput = null;
            }
        }
    }

    /**
     * Close current output stream.
     */
    public void closeCurrentOutput() {
        if (null != currentOutput) {
            try {
                currentOutput.close();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error closing output stream", e);
            } finally {
                currentOutput = null;
            }
        }
    }

    /**
     * Set the kill flag.
     *
     * @param tf <code>true</code> means exit the Interpreter loop
     */
    public void setKillFlag(boolean tf) {
        killFlag = tf;
    }

    /**
     * Get the kill flag.
     *
     * @return the flag which means exit the Interpreter loop
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
     * Set the Interpreter numeric base.
     *
     * @param i the base
     */
    public void setBase(int i) {
        base = i;
    }

    /**
     * Get the Interpreter numeric base.
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
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
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
        if (tokens != null && tokenIndex < tokens.length) {
            String token = tokens[tokenIndex++];
            // Update inputPosition to point after this token in the input string
            if (inputString != null && token != null) {
                int pos = inputString.indexOf(token, inputPosition);
                if (pos >= 0) {
                    inputPosition = pos + token.length();
                }
            }
            return token;
        }
        return null;
    }

    /**
     * Get next lexeme in string being interpret()'ed using default delims.
     *
     * @return the next lexeme string
     */
    public String nextLexeme() {
        return nextLexeme(defaultDelimiters);
    }

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
        if (inputString == null || inputPosition >= inputString.length()) {
            return null;
        }
        
        // Find the next delimiter from current position
        int delimPos = inputPosition;
        while (delimPos < inputString.length() &&
               delims.indexOf(inputString.charAt(delimPos)) == -1) {
            delimPos++;
        }
        
        // Extract the lexeme (everything up to the delimiter)
        String result = inputString.substring(inputPosition, delimPos);
        
        // Strip leading blank if present (mimics old StringTokenizer behavior)
        if (result.length() > 0 && result.charAt(0) == ' ') {
            result = result.substring(1);
        }
        
        // Update position to delimiter
        inputPosition = delimPos;
        
        // Consume the delimiter if requested
        if (consumeDelim && inputPosition < inputString.length() &&
            delims.indexOf(inputString.charAt(inputPosition)) != -1) {
            inputPosition++;
        }
        
        // Synchronize token-based parsing with input position
        // Re-tokenize the remaining input from current position
        if (inputPosition < inputString.length()) {
            String remaining = inputString.substring(inputPosition);
            // Use split with limit -1 to preserve trailing empty strings
            String[] newTokens = remaining.split("[" + defaultDelimiters + "]+", -1);
            // Filter out leading empty string if present
            if (newTokens.length > 0 && newTokens[0].isEmpty()) {
                tokens = new String[newTokens.length - 1];
                System.arraycopy(newTokens, 1, tokens, 0, tokens.length);
            } else {
                tokens = newTokens;
            }
            tokenIndex = 0;
        } else {
            // No more input, clear tokens
            tokens = new String[0];
            tokenIndex = 0;
        }
        
        return result;
    }

    /**
     * Number of lexemes left in string being interpret()'ed .
     *
     * @return num lexemes left
     */
    public int countLexemes() {
        if (tokens != null) {
            return tokens.length - tokenIndex;
        }
        return 0;
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
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Issue the prompt as appropriate
     */
    public void prompt() {
        if (Engine.INTERPRETING == myEngine.state) {
            // We're interpreting
            output("\noK ");
        } else {
            // We're compiling
            output("\n(...) ");
        }
    }

    /**
     * Something for the engine to call when it does a warm().
     */
    public void warmReset() {
        tokens = null;
        tokenIndex = 0;
        inputString = null;
        inputPosition = 0;
        tokenizerStack = new Stack<>();
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
        String aLexeme; // Holds a lexeme as we examine it
        Semantic semantic; // Holds a semantic as we decide what to do with it

        setKillFlag(false);
        setQuitFlag(false);

        // Interpret the passed-in string
        if (s != null) {
            // Save current tokenizer context
            tokenizerStack.push(new TokenizerContext(tokens, tokenIndex, inputString, inputPosition));
            // Store the input string and reset position
            inputString = s;
            inputPosition = 0;
            // Split string into tokens efficiently
            tokens = s.split("[" + defaultDelimiters + "]+");
            tokenIndex = 0;

            while ((countLexemes() > 0)
                    && !killFlag
                    && !quitFlag) {
                aLexeme = nextLexeme();
                announce("Lexeme is: " + aLexeme);
                semantic = myEngine.findSemantic(aLexeme);
                announce("Semantic is: " + semantic);

                if (null != semantic) {
                    // We found lexeme as dictionary entry
                    if (Engine.INTERPRETING == myEngine.state) {
                        // We're interpreting
                        try {
                            announce("Executing interpretive semantics for "
                                    + semantic.toString()
                            );
                            semantic.execute(myEngine);
                        } catch (Exceptions.desktop.shell.BadDefinitionExecute | Exceptions.desktop.shell.BadPrimitiveExecute e) {
                            e.printStackTrace(System.err);
                            output(e.getMessage());
                            myEngine.warm();
                        }
                    } else {
                        // We're compiling
                        try {
                            announce("Executing compile semantics for "
                                    + semantic.toString()
                            );
                            semantic.compile(myEngine);
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                            output(e.getMessage());
                            myEngine.warm();
                        }
                    }
                } else {
                    // We didn't find the lexeme as a dictionary entry
                    if (Engine.INTERPRETING == myEngine.state) {
                        // We're interpreting
                        Long a;
                        try {
                            // Try to make it a long
                            a = Long.valueOf(aLexeme, base);
                            myEngine.stack.push(a);
                        } catch (NumberFormatException e) {
                            // Wasn't a long, push the lexeme
                            myEngine.stack.push(aLexeme);
                        }
                    } else {
                        // We're compiling
                        long a = 0;
                        try {
                            // Try to make it a long
                            a = Long.parseLong(aLexeme);
                            try {
                                // Try to compile the long as a literal Long
                                myEngine.compileLiteral(Long.valueOf(a));
                            } catch (Exception x) {
                                announce("interpreter had problem compiling literal Long.");
                                announce("Lexeme was: " + aLexeme);
                                x.printStackTrace(System.err);
                                myEngine.warm();
                            }
                        } catch (NumberFormatException e) {
                            // Wasn't a long
                            try {
                                // Try to compile the lexeme as a string literal
                                myEngine.compileLiteral(aLexeme);
                            } /* End try*/ catch (Exceptions.desktop.shell.BadDefinitionCompile | Exceptions.desktop.shell.BadDefinitionExecute | Exceptions.desktop.shell.BadPrimitiveCompile | Exceptions.desktop.shell.BadPrimitiveExecute | ClassNotFoundException | NoSuchMethodException x) {
                                announce("interpreter had problem compiling literal String.");
                                announce("Lexeme was: " + aLexeme);
                                x.printStackTrace(System.err);
                                myEngine.warm();
                            }
                        }
                    }
                }
            }
            try {
                if (!tokenizerStack.isEmpty()) {
                    TokenizerContext context = tokenizerStack.pop();
                    tokens = context.tokens;
                    tokenIndex = context.tokenIndex;
                    inputString = context.inputString;
                    inputPosition = context.inputPosition;
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
                myEngine.warm();
            }
        }
    }

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
    }

    /**
     * Announce a string if verbose.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param s string to announce
     */
    /**
     * Demonstrate <code>Interpreter</code>.
     *
     * @param argv Arguments to Interpreter
     */
    public static void main(String argv[]) {
        GetArgs myArgs = new GetArgs(argv);
        Interpreter i = initializeInterpreter();
        boolean quiet = parseArguments(myArgs, i);
        
        if (!quiet) {
            printBanner();
        }
        
        BufferedReader br = setupIO(i);
        loadSourceFiles(myArgs, i);
        runREPL(i, br);
    }

    /**
     * Initialize the Interpreter instance.
     *
     * @return initialized Interpreter
     */
    private static Interpreter initializeInterpreter() {
        try {
            return new Interpreter();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Error.bAcKtOmain(e);
        }
    }

    /**
     * Parse command line arguments and configure Interpreter.
     *
     * @param myArgs parsed command line arguments
     * @param i Interpreter instance to configure
     * @return true if quiet mode is enabled
     */
    private static boolean parseArguments(GetArgs myArgs, Interpreter i) {
        boolean quiet = false;
        
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
                        } catch (NumberFormatException e) {
                            e.printStackTrace(System.err);
                            throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase(e);
                        }
                    } else {
                        String s = "(null) presented for interpreter numeric base.";
                        System.out.println(s);
                        throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase(s, null);
                    }
                    break;
                case "-o":
                    if (a.argument != null) {
                        i.setOutputStreamEncoding(a.argument);
                    } else {
                        String s = "Bad output stream encoding proposed: " + a.option;
                        System.out.println(s);
                        throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadEncoding(s, null);
                    }
                    break;
                case "-h":
                case "--":
                    Interpreter.usage();
                    System.exit(0);
                    break;
                case "-q":
                    quiet = true;
                    break;
                default:
                    String s = "Bad option " + a.option + " " + a.argument;
                    System.err.println(s);
                    usage();
                    throw new com.SoftWoehr.FIJI.base.Error.bAdArGtOmain(s, null);
            }
        }
        
        return quiet;
    }

    /**
     * Print GPL banner and version information.
     */
    private static void printBanner() {
        System.out.println("FIJI ForthIsh Java Interpreter " + Engine.fijiVersion());
        System.out.println("Copyright (C) 1999, 2001, 2008, 2016 by Jack J. Woehr.");
        System.out.println("FIJI comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");
    }

    /**
     * Set up input/output streams for the Interpreter.
     *
     * @param i Interpreter instance to configure
     * @return BufferedReader for reading input
     */
    private static BufferedReader setupIO(Interpreter i) {
        try {
            i.setOutput(System.out);
            i.setInput(System.in);
            InputStreamReader isr = new InputStreamReader(System.in);
            return new BufferedReader(isr);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            throw new com.SoftWoehr.FIJI.base.Error.bAcKtOmain(e);
        }
    }

    /**
     * Load source files specified in command line arguments.
     *
     * @param myArgs parsed command line arguments
     * @param i Interpreter instance
     */
    private static void loadSourceFiles(GetArgs myArgs, Interpreter i) {
        for (int j = 0; j < myArgs.argumentCount(); j++) {
            try {
                Argument a = myArgs.nthArgument(j);
                i.load(a.argument);
            } catch (Exception e) {
                e.printStackTrace(System.err);
                break;
            }
        }
    }

    /**
     * Run the Read-Eval-Print Loop (REPL).
     *
     * @param i Interpreter instance
     * @param br BufferedReader for reading input
     */
    private static void runREPL(Interpreter i, BufferedReader br) {
        String tib;

        i.prompt();
        while (!i.killFlag) {
            try {
                tib = br.readLine();
            } catch (EOFException e) {
                i.setKillFlag(true);
                break;
            } catch (IOException e) {
                e.printStackTrace(System.err);
                break;
            } catch (Exception e) {
                e.printStackTrace(System.err);
                continue;
            }

            i.interpret(tib);
            if (!i.killFlag) {
                i.prompt();
            }
        }
        
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
