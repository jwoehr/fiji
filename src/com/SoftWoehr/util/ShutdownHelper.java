/* ShutdownHelper.java ...  */
/*********************************************/
/*   Copyright *C* 1998, 2001 Jack J. Woehr  */
/*	      All Rights Reserved	     */
/* PO Box 51 Golden, Colorado 80402-0051 USA */
/*	    http://www.softwoehr.com	     */
/*	  http://fiji.sourceforge.net	     */
/*********************************************/
/*                                           */
/*    This Program is Free SoftWoehr.        */
/*                                           */
/* THERE IS NO GUARANTEE, NO WARRANTY AT ALL */
/*********************************************/
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

package com.SoftWoehr.util;

import com.SoftWoehr.SoftWoehr;
import java.util.*;

/** Support class to do shutdown on behalf of other entities.
 * <br>An entity which instantiates <code>ShutdownHelper</code>
 * allows others implementing <code>com.SoftWoehr.SoftWoehr</code>
 * to register with it. The <code>ShutdownHelper</code> method
 * <code>shutdownClients()</code> is then called in the owning
 * entity's <code>shutdown()</code> code.
 * @author $Author: jwoehr $
 * @version $Revision: 1.1 $
 */
public class ShutdownHelper implements SoftWoehr, verbose {
  
  /** Identifies revision level of source for SoftWoehr interface */
  private static final String rcsid = "$Id: ShutdownHelper.java,v 1.1 2016-11-06 21:20:42 jwoehr Exp $";
  
  /** Returns revision info for SoftWoehr interface.
   * @return the rcsid
   */
  public  String              rcsId() { return rcsid; }
  
  /** The ShutdownHelper notifies subcomponents of shutdown then shuts itself down.
   * @see com.SoftWoehr.SoftWoehr#shutdown
   * @return return code
   */
  public int shutdown() { return shutdownClients(); }
  
  /** Is this verbose and announcing? */
  public boolean isverbose = false;
  
  /** Array of clients for shutdown */
  private List<SoftWoehr> clients;
  
  /** Returns <CODE>true</CODE> if entity is set verbose.
   * @see com.SoftWoehr.util.verbose
   * @see com.SoftWoehr.util.verbosity
   * @return verbose indicator
   */
  public boolean isVerbose()              {return isverbose;}
  
  /** Set <CODE>true</CODE> to set entity verbose.
   * @see com.SoftWoehr.util.verbose
   * @see com.SoftWoehr.util.verbosity
   * @param tf verbose setter
   */
  public void    setVerbose  (boolean tf) {isverbose = tf;  }
  
  
  /** Arity/0 ctor */
  public ShutdownHelper() {
    reinit();
  }
  
  /** Call to start anew with empty list of clients */
  public void reinit() {
    clients = new ArrayList<>();
  }
  
  /** Register a client for shutdown
   * @param sw Object to shut down
   */
  public void registerClient(SoftWoehr sw) {
    clients.add(sw);
  }
  
  /** Call all registered clients' shutdown() methods
   */
  public int shutdownClients() {
   int result = 0;
   for (SoftWoehr sw : clients) {
        announce("Shutting down " + sw.toString());
        result = sw.shutdown();
        if (result != 0) {
            break;
        }
   }
   return result;
  }

  /** Add client for later shutdown calls. */
  public void addShutdownClient(SoftWoehr s) {
    clients.add(s);
    }

  /** Remove client for later shutdown calls. */
  public void removeShutdownClient(SoftWoehr s) {
    clients.remove(s);
    }

  /*******************************/
  /*% User methods section ends. */
  /*******************************/

  /**********************************************/
  /*% SoftWoehr default methods section starts. */
  /**********************************************/



   /**
    * @see com.SoftWoehr.util.verbose
    * @see com.SoftWoehr.util.verbosity
    */


  /********************************************/
  /*% SoftWoehr default methods section ends. */
  /********************************************/

  /*********/
  /*% Main */
  /*********/

  /** Demonstrate <code>ShutdownHelper</code>. */
  public static void main (String argv[]) {

    GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */
    ShutdownHelper theShutdownHelper = new ShutdownHelper();         /* Instance of ShutdownHelper we're demoing.  */

    /* GPL'ed SoftWoehr announces itself. */
    System.out.println("ShutdownHelper, Copyright (C) 1988 Jack J. Woehr.");
    System.out.println("ShutdownHelper comes with ABSOLUTELY NO WARRANTY;");
    System.out.println("Please see the file COPYING and/or COPYING.LIB");
    System.out.println("which you should have received with this software.");
    System.out.println("This is free software, and you are welcome to redistribute it");
    System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");

    /* See if user passed in the -v flag to request verbosity. */
    for (int i = 0; i < myArgs.optionCount() ; i++)
      {
      if (myArgs.nthOption(i).getOption().substring(1,2).equals("v"))
        {
        theShutdownHelper.setVerbose(true);
        }                                                         /* End if*/
      }

    // Your code goes here.
    // -------------------

    // -------------------

    return;
    }
}                                                      /* End of ShutdownHelper class*/

/*  End of ShutdownHelper.java */
