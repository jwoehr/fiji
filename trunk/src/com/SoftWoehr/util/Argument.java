/* Argument.java ... an option parsed.	      */
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

import  com.SoftWoehr.*;

/** Holds an argument parsed from a command line.
 * If it's a plain argument, records the argument
 * string and position. If it is a dash-option,
 * records the option string (-a -b etc.) and
 * the argument to the option, if any. In any
 * case, records the position in the command
 * line that the arg or opt-arg pair came in.
 * @author $Author: jwoehr $
 * @version $Revision: 1.1 $
 * @see com.SoftWoehr.util.GetArgs
 */
public class Argument implements SoftWoehr {

    /** The rcs id */
    public static final String rcsid = "$Id: Argument.java,v 1.1 2016-11-06 21:20:42 jwoehr Exp $";

    /** Implement com.SoftWoehr.SoftWoehr ...
     * return the RCS id for the class.
     * @return  the rcs id
     */
    public String rcsId() { return rcsid; }

    /** shutdown() here does nothing.
     * @see com.SoftWoehr.SoftWoehr#shutdown
     * @return alway zero (0).
     */
    public int shutdown() { return 0; }

    /** The "option", that is, dash-letter, e.g., -a -b etc. */
    public String option;

    /** The argument to the option, e.g., "-o full" where
     * "full" is the argument to the option "-o".
     */
    public String argument;

    /** The position among the options-and-arguments in which
     * this option-and-argument appears.
     */
    public int position;

    /** Create an Argument from an option, argument and position.
     * @param option    The command-line option
     * @param argument  The command line arg
     * @param position  The nth-ity of the entity.
     */
    public Argument(String option, String argument, int position) {
	this.option   = option;
	this.argument = argument;
	this.position = position;
    }

    /** Return the option and argument as a String.
     * @return The string representation of the option and argument
     */
    public String toString()
    {return option + " " + argument;}

    /** Return the option portion (if any) of the Argument.
     * @return  The option itself.
     */
    public String getOption() {return option;}

    /** Return the argument portion (if any) of the Argument.
     * @return  The argument itself.
     */
    public String getArgument() {return argument;}
}						   /* End of Argument class*/

/* End of Argument.java */
