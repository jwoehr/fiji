/**********************************************/
/*    UnsignedByte.java ... unsigned type.    */
/*					      */
/* Copyright *C* 1998, 2001 Jack J. Woehr     */
/*	      All Rights Reserved	      */
/* PO Box 51, Golden, Colorado 80402-0051 USA */
/*	    http://www.softwoehr.com	      */
/*	  http://fiji.sourceforge.net	      */
/*					      */
/*	      This Program is Free	      */
/*		   Softwoehr		      */
/*					      */
/*  Permission to distribute this Softwoehr   */
/* with copyright notice attached is granted. */
/*					      */
/*  Permission to modify for personal use at  */
/*  at home or for your personal use on the   */
/*  job is granted, but you may not publicly  */
/*  make available modified versions of this  */
/*   program without asking and getting the   */
/*   permission of the author, Jack Woehr.    */
/*					      */
/* The permission will usually be granted if  */
/* granted reciprocally by you for the mods.  */
/*					      */
/* THERE IS NO GUARANTEE, NO WARRANTY AT ALL  */
/**********************************************/
package com.SoftWoehr.util;

import com.SoftWoehr.*;
import java.io.*;

/**
 * UnsignedByte is for unsigned byte math.
 * @author  $Author: jwoehr $
 * @version $Revision: 1.2 $
 */
class UnsignedByte extends Number implements SoftWoehr {
    /** Identifies revision level of source. */
    private static final String rcsid = "$Id: UnsignedByte.java,v 1.2 2001-09-04 21:49:40 jwoehr Exp $";
    
    /** Implements com.SoftWoehr.SoftWoehr
     * @return The rcs id.
     */
    public String rcsId() {return rcsid;}
    
    /** shutdown() here does nothing.
     * @see com.SoftWoehr.SoftWoehr#
     * @return Always 0.
     */
    public int shutdown() { return 0; }
    
    /** The maximum value an UnsignedByte can have. */
    public static final int MAX_VALUE = 0xff;
    
    /** The minimum value an UnsignedByte can have. */
    public static final int MIN_VALUE = 0;
    
    // /** The Class object representing the primitive type int. */
    // Have to examine what Byte returns for this.
    //  public static final class TYPE = ;
    
    /** The repository of value. */
    private int i;
    
    /** Construct an UnsignedByte from an integer.
     * @param i Integer source of byte.
     */
    public UnsignedByte(int i) {
        this.i = i & 0xff;
    }
    
    /** Construct an UnsignedByte from a string.
     * Uses <code>Integer.parseInt()</code> to conform
     * to the behaviour of the <code>Integer(String)</code>
     * and <code>Byte(String)</code>. The other
     * possibility was <code>Integer.decode(String)</code>.
     * @param s String source of byte.
     */
    public UnsignedByte(String s) {
        this(Integer.parseInt(s));
    }
    
    /** Not implemented */
    private UnsignedByte() {}
    
    /** Return a new String object representing the value in base 10.
     * @return String representation of the byte value.
     */
    public String toString() {
        return Integer.toString(i);
    }
    
    /** Return a new String object
     * representing the object's value
     * in radix 10 as an unsigned byte.
     * @param i The int to string-ize.
     * @return String representation.
     */
    public static String toString(int i) {
        return new UnsignedByte(i).toString();
    }
    
    /** Return a new String object
     * representing the object's value
     * in radix <I>radix</I>.
     * @param i The int to string-ize.
     * @param radix The radix of conversion.
     * @return String representation.
     */
    public static String toString(int i, int radix) {
        return Integer.toString(new UnsignedByte(i).intValue(), radix);
    }
    
    /** Returns the value of the specified number as a byte.
     * @return Byte value of this ubyte.
     */
    public byte byteValue() {
        return (byte) i;
    }
    
    /** Returns the value of the specified number as a double.
     * @return Double representation.
     */
    public double doubleValue() {
        return i;
    }
    
    /** Return the value of the specified number as a float.
     * @return Float representation.
     */
    public float floatValue() {
        return i;
    }
    
    /** Returns the value of the specified number as an int.
     * @return Int value.
     */
    public int intValue() {
        return i;
    }
    
    /** Returns the value of the specified number as a long.
     * @return Long value.
     */
    public long longValue() {
        return i;
    }
    
    /** Returns the value of the specified number as a short.
     * @return Short value.
     */
    public short shortValue() {
        return (short) i;
    }
    
    /**
     * Decodes a String into an UnsignedByte. The String may
     * represent decimal, hexadecimal, and octal numbers.
     * @param s The String input.
     * @return The ubyte result.
     */
    public static UnsignedByte decode(String s) {
        return new UnsignedByte(Integer.decode(s).intValue());
    }
    
    /** Assuming the specified String represents an UnsignedByte,,
     * returns a new UnsignedByte object initialized to that value.
     * Throws an exception if the String cannot be parsed as an
     * UnsignedByte.
     * @param s The string representation
     * @param radix radix of conversion
     * @return ubyte result.
     */
    public static UnsignedByte valueOf(String s,
    int radix) // throws NumberFormatException
    {
        return new UnsignedByte(Integer.valueOf(s).intValue());
    }
    
    /** Demo and test UnsignedByte
     * @param argv Unused args to main().
     */
    public static void main(String argv[]) {
        if (argv.length < 1) {
            System.out.println("Args should be: uByte [display-radix]");
        }
        else {
            UnsignedByte ub = new UnsignedByte(argv[0]);
            System.out.println("In decimal, that unsigned byte is " + ub.toString());
            System.out.println("In hex, that unsigned byte is "
            + UnsignedByte.toString(ub.intValue(), 16));
            if (argv.length > 1) {
                int r = Integer.decode(argv[1]).intValue();
                System.out.println("In the radix " + r
                + " which you suggested, that unsigned byte is "
                + UnsignedByte.toString(ub.intValue(), r));
            }							  /* End if*/
        }								  /* End if*/
    }
}					       /* End of UnsignedByte class*/

/*  End of UnsignedByte.java */

