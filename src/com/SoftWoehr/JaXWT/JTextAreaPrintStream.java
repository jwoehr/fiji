/*
 * JTextAreaPrintStream.java
 *
 * Created on October 13, 2000, 10:58 PM
 * Copyright (C) 2000, 2016 Jack J. Woehr
 * jwoehr@softwoehr.com PO Box 51, Golden, Colorado 80402 USA
 */
package com.SoftWoehr.JaXWT;

import com.SoftWoehr.util.verbosity;

/**
 * Create an PrintStream that puts its contents to a JTextArea
 *
 * @author jax
 * @version $Id: JTextAreaPrintStream.java,v 1.1 2016/11/06 21:20:40 jwoehr Exp
 * $
 */
public class JTextAreaPrintStream extends java.io.PrintStream implements com.SoftWoehr.util.verbose {

    /**
     * Flags whether we are in verbose mode.
     */
    private boolean is_verbose = true;

    /**
     * Helper for verbose mode.
     */
    private final verbosity v = new verbosity(this);

    /**
     * Creates new JTextAreaPrintStream
     */

    /**
     * Create without instancing text area
     */
    public JTextAreaPrintStream() {
        super((java.io.OutputStream) null);
    }

    /**
     * Create while instancing text area.
     *
     * @param jtaos
     */
    public JTextAreaPrintStream(JTextAreaOutputStream jtaos) {
        super(jtaos);
    }

    /**
     * Returns true if instance is in verbose mode.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return <CODE>true</CODE> .iff this object is in verbose mode.
     */
    @Override
    public boolean isVerbose() {
        return is_verbose;
    }

    /**
     * Sets <code>true</code> or resets <code>false</code> verbose mode.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param b <CODE>true</CODE> sets object verbose.
     */
    @Override
    public void setVerbose(boolean b) {
        is_verbose = b;
    }

    /**
     * Say something if the object is in verbose mode, be silent otherwise.
     *
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param message Message to emit of Java is verbose.
     */
    @Override
    public void announce(String message) {
        v.announce(message);
    }
}
