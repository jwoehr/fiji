/*
 * FIJI.java
 * Copyright *C* 2001,2015 Jack J. Woehr
 * All Rights Reserved
 * PO Box 51, Golden, Colorado 80402-0051 USA
 * http://www.softwoehr.com
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * Free Software NO WARRANTY NO GUARANTEE
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * Created on October 15, 2000, 7:48 PM
 */
package com.SoftWoehr.FIJI;

/**
 * This class merely serves as a launcher for the FIJI interpreter running at
 * the command line.
 *
 * @author jax
 * @version $Id: FIJI.java,v 1.1 2016-11-06 21:20:42 jwoehr Exp $
 */
public class FIJI extends Object {

    /**
     * Creates new FIJI
     */
    public FIJI() {
    }

    /**
     * Run FIJI at the command line level.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        boolean gui = false;
        String[] newArgs = null;
        if (args.length > 0) {
            newArgs = new String[args.length - 1];
            if (args[0].equals("--gui") || args[0].equals("-g")) {
                gui = true;
                for (int i = 0; i < newArgs.length; i++) {
                    newArgs[i] = args[args.length - newArgs.length + i];
                }
            }
        }
        if (gui) {
            com.SoftWoehr.FIJI.FIJIGui.main(newArgs);
        } else {
            com.SoftWoehr.FIJI.base.desktop.shell.interpreter.main(args);
        }
    }
}
