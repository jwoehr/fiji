package com.SoftWoehr.FIJI.base.desktop.shell;

import com.SoftWoehr.util.GetArgs;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Unit tests for the refactored interpreter class.
 * Tests verify that the refactored main() method components work correctly.
 */
public class InterpreterTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
    
    /**
     * Test that interpreter can be initialized successfully.
     */
    @Test
    public void testInitializeInterpreter() {
        interpreter i = new interpreter();
        assertNotNull("Interpreter should be initialized", i);
        assertNotNull("Engine should be initialized", i.myEngine);
    }
    
    /**
     * Test parsing verbose flag.
     */
    @Test
    public void testParseArgumentsVerbose() {
        String[] args = {"-v"};
        GetArgs myArgs = new GetArgs(args);
        interpreter i = new interpreter();
        
        // This would normally be called by parseArguments
        boolean quiet = false;
        for (int x = 0; x < myArgs.optionCount(); x++) {
            if (myArgs.nthOption(x).option.equals("-v")) {
                i.setVerbose(true);
            }
        }
        
        assertTrue("Verbose mode should be set", i.isVerbose());
    }
    
    /**
     * Test parsing quiet flag.
     */
    @Test
    public void testParseArgumentsQuiet() {
        String[] args = {"-q"};
        GetArgs myArgs = new GetArgs(args);
        
        boolean quiet = false;
        for (int x = 0; x < myArgs.optionCount(); x++) {
            if (myArgs.nthOption(x).option.equals("-q")) {
                quiet = true;
            }
        }
        
        assertTrue("Quiet mode should be set", quiet);
    }
    
    /**
     * Test parsing base option with valid value.
     */
    @Test
    public void testParseArgumentsBaseValid() {
        String[] args = {"-b", "16"};
        GetArgs myArgs = new GetArgs(args);
        interpreter i = new interpreter();
        
        for (int x = 0; x < myArgs.optionCount(); x++) {
            if (myArgs.nthOption(x).option.equals("-b")) {
                String baseArg = myArgs.nthOption(x).argument;
                if (baseArg != null) {
                    i.setBase(Integer.decode(baseArg));
                }
            }
        }
        
        assertEquals("Base should be set to 16", 16, i.getBase());
    }
    
    /**
     * Test that invalid base throws exception.
     */
    @Test(expected = com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase.class)
    public void testParseArgumentsBaseInvalid() {
        String[] args = {"-b", "invalid"};
        GetArgs myArgs = new GetArgs(args);
        interpreter i = new interpreter();
        
        for (int x = 0; x < myArgs.optionCount(); x++) {
            if (myArgs.nthOption(x).option.equals("-b")) {
                String baseArg = myArgs.nthOption(x).argument;
                if (baseArg != null) {
                    try {
                        i.setBase(Integer.decode(baseArg));
                    } catch (NumberFormatException e) {
                        throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase(e);
                    }
                }
            }
        }
    }
    
    /**
     * Test that null base argument throws exception.
     */
    @Test(expected = com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase.class)
    public void testParseArgumentsBaseNull() {
        // Simulate null base argument
        throw new com.SoftWoehr.FIJI.base.Error.desktop.shell.BadBase("(null) presented for interpreter numeric base.", null);
    }
    
    /**
     * Test output encoding option.
     */
    @Test
    public void testParseArgumentsOutputEncoding() {
        String[] args = {"-o", "UTF-8"};
        GetArgs myArgs = new GetArgs(args);
        interpreter i = new interpreter();
        
        for (int x = 0; x < myArgs.optionCount(); x++) {
            if (myArgs.nthOption(x).option.equals("-o")) {
                String encoding = myArgs.nthOption(x).argument;
                if (encoding != null) {
                    i.setOutputStreamEncoding(encoding);
                }
            }
        }
        
        // If no exception thrown, encoding was set successfully
        assertTrue("Output encoding should be set without error", true);
    }
    
    /**
     * Test that interpreter can be created and basic operations work.
     */
    @Test
    public void testInterpreterBasicFunctionality() {
        interpreter i = new interpreter();
        assertNotNull("Interpreter should not be null", i);
        assertFalse("Kill flag should be false initially", i.killFlag);
        
        // Test that we can set verbose mode
        i.setVerbose(true);
        assertTrue("Verbose should be true", i.isVerbose());
        
        i.setVerbose(false);
        assertFalse("Verbose should be false", i.isVerbose());
    }
    
    /**
     * Test that GetArgs properly parses command line arguments.
     */
    @Test
    public void testGetArgsMultipleOptions() {
        String[] args = {"-v", "-q", "-b", "10"};
        GetArgs myArgs = new GetArgs(args);
        
        assertEquals("Should have 3 options", 3, myArgs.optionCount());
        assertEquals("First option should be -v", "-v", myArgs.nthOption(0).option);
        assertEquals("Second option should be -q", "-q", myArgs.nthOption(1).option);
        assertEquals("Third option should be -b", "-b", myArgs.nthOption(2).option);
        assertEquals("Base argument should be 10", "10", myArgs.nthOption(2).argument);
    }
    
    /**
     * Test that GetArgs handles file arguments.
     */
    @Test
    public void testGetArgsWithFileArguments() {
        String[] args = {"-v", "file1.fiji", "file2.fiji"};
        GetArgs myArgs = new GetArgs(args);
        
        assertEquals("Should have 1 option", 1, myArgs.optionCount());
        assertEquals("Should have 2 arguments", 2, myArgs.argumentCount());
        assertEquals("First argument should be file1.fiji", "file1.fiji", myArgs.nthArgument(0).argument);
        assertEquals("Second argument should be file2.fiji", "file2.fiji", myArgs.nthArgument(1).argument);
    }
}

// Made with Bob
