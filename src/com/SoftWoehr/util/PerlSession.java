/** ****************************************** */
/* PerlSession.java                          */
 /* Invoke a Perl macro on a text buffer.     */
 /* jack j. woehr jax@well.com jwoehr@ibm.net */
 /* http://www.well.com/user/jax/rcfb         */
 /* P.O. Box 51, Golden, Colorado 80402-0051  */
/** ****************************************** */
/*   Copyright *C* 1998, 2016 Jack J. Woehr  */
 /*	      All Rights Reserved	     */
 /* PO Box 51 Golden, Colorado 80402-0051 USA */
 /*	    http://www.softwoehr.com	     */
 /*	  http://fiji.sourceforge.net	     */
 /*                                           */
 /*       This Program is Free                */
 /*            SoftWoehr                      */
 /*                                           */
 /* Permission to distribute this SoftWoehr   */
 /* with copyright notice attached is granted.*/
 /*                                           */
 /* Permission to modify for personal use at  */
 /* at home or for your personal use on the   */
 /* job is granted, but you may not publicly  */
 /* make available modified versions of this  */
 /* program without asking and getting the    */
 /* permission of the author, Jack Woehr.     */
 /*                                           */
 /* The permission will usually be granted if */
 /* granted reciprocally by you for the mods. */
 /*                                           */
 /* THERE IS NO GUARANTEE, NO WARRANTY AT ALL */
/** ****************************************** */
/* @author  $Author: jwoehr $                         */
 /* @version $Revision: 1.2 $                       */
package com.SoftWoehr.util;

import com.SoftWoehr.*;
import java.io.*;

public class PerlSession implements SoftWoehr {

    private static final String rcsid = "$Id: PerlSession.java,v 1.2 2016-11-08 01:08:23 jwoehr Exp $";

    /**
     * Returns revision info for SoftWoehr interface.
     */
    @Override
    public String rcsId() {
        return rcsid;
    }

    /**
     * shutdown() here does nothing.
     *
     * @see com.SoftWoehr.SoftWoehr
     */
    public int shutdown() {
        return 0;
    }

    public String result;
    public char outBuffer[];
    /* Resultant text.                  */
    public String invocation = "perl";
    public String options = "";
    public String redirection = "";

    public PerlSession() {
    }

    //* Execute a perl macro specified by a script name and an input file name
    synchronized void perform(String scriptName,
             char inBuf[],
             String inputFileName) {
        redirection = "<" + inputFileName;
        perform(scriptName, inBuf);
    }

    //* Execute a perl macro specified by a script name.
    synchronized void perform(String scriptName, char inBuf[]) {

        result = "";
        /* empty the result string*/
        String commandLine = invocation
                + " "
                + scriptName /* build cmdline*/
                + " "
                + redirection;

        try {
            Process p = Runtime.getRuntime().exec(commandLine);
            /* exec script*/

 /* If redirection, inBuff not meaningful. */
            if (redirection.equals("")) {
                try ( /* inBuff meaningful*/OutputStreamWriter pOut = new OutputStreamWriter(p.getOutputStream())) {
                    pOut.write(inBuf);
                    pOut.flush();
                }
            }

            // Prepare to read in the results.
            InputStreamReader pIn = new InputStreamReader(p.getInputStream());
            outBuffer = new char[1024];
            int numRead;

            // Loop reading.
            while ((numRead = pIn.read(outBuffer, 0, outBuffer.length)) != -1) {
                result = result + new String(outBuffer, 0, numRead);
            }
        } catch (java.io.IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            redirection = "";
        }
        /* End finally*/
    }

    /* void perform (String scriptName)*/

    public static void main(String argv[]) {
        char foo[] = new char[10000];
        if (argv.length != 2) {
            System.out.println("Usage: PerlSession infilename scriptfilename");
            System.exit(0);
        }
        PerlSession p = new PerlSession();
        p.perform(argv[0], null, argv[1]);
        System.out.println(p.result);
    }
}
/* End of PerlSession class*/
