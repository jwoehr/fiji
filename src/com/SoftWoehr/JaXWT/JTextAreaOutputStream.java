/*
 * JTextAreaOutputStream.java
 *
 * Created on October 13, 2000, 10:58 PM
 * Copyright (C) 2000, 2016 Jack J. Woehr
 * jwoehr@softwoehr.com PO Box 51, Golden, Colorado 80402 USA
 */
package com.SoftWoehr.JaXWT;

import com.SoftWoehr.util.verbosity;
import javax.swing.JTextArea;

/**
 * Create an OutputStream that puts its contents to a JTextArea
 *
 * @author jax
 * @version $Id: JTextAreaOutputStream.java,v 1.1 2016/11/06 21:20:40 jwoehr Exp
 * $
 */
public class JTextAreaOutputStream extends java.io.OutputStream implements com.SoftWoehr.util.verbose {

    /**
     * Flags whether we are in verbose mode.
     */
    private boolean is_verbose = true;
    private JTextArea my_text_area;

    /**
     * Helper for verbose mode.
     */
    private final verbosity v = new verbosity(this);

    /**
     * Creates new JTextAreaOutputStream
     */

    /**
     * Create without instancing text area
     */
    public JTextAreaOutputStream() {
    }

    /**
     * Create while instancing text area.
     *
     * @param jta The JTextArea for this stream to use.
     */
    public JTextAreaOutputStream(JTextArea jta) {
        set_text_area(jta);
    }

    /**
     * Set the text area for this output.
     *
     * @param jta The JTextArea for this stream to use.
     */
    public final void set_text_area(JTextArea jta) {
        my_text_area = jta;
    }

    /**
     * Get the text area for this output.
     *
     * @return The JTextArea used for output from this stream.
     */
    public JTextArea get_text_area() {
        return my_text_area;
    }

    /**
     * Error message used by write()
     */
    private String no_text_area_message() {
        return this + " doesn't have an associated JTextArea.";
    }

    class NullTextAreaException extends Exception {

        NullTextAreaException(String s) {
            super(s);
        }
    }

    /**
     * Write a byte to the output stream.
     *
     * @param b Byte to write.
     */
    @Override
    public void write(int b) {
        byte b1[] = new byte[1];
        b1[0] = (byte) b;
        String s = new String(b1);

        /**
         * Append if text area, otherwise print error.
         */
        if (null != get_text_area()) {
            my_text_area.append(s);
        } else {
            System.err.println(no_text_area_message());
        }
    }

    /**
     * Write a byte array to the output stream.
     *
     * @param b Byte array to write.
     */
    @Override
    public void write(byte b[]) {
        String s = new String(b);

        /**
         * Append if text area, otherwise print error.
         */
        if (null != get_text_area()) {
            my_text_area.append(s);
        } else {
            System.err.println(no_text_area_message());
        }
    }

    /**
     * Write a byte subarray to the output stream.
     *
     * @param b The byte array.
     * @param offset Offset to start at.
     * @param length Length to write.
     */
    @Override
    public void write(byte b[], int offset, int length) {
        String s = new String(b, offset, length);

        /**
         * Append if text area, otherwise print error.
         */
        if (null != get_text_area()) {
            my_text_area.append(s);
        } else {
            System.err.println(no_text_area_message());
        }
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
