/* Desktop.java ...  */
/** ****************************************** */
/* Copyright *C* 1999, 2001, 2016            */
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
package com.SoftWoehr.FIJI.base.desktop;

import java.awt.*;
import java.awt.event.*;

import com.SoftWoehr.SoftWoehr;
import com.SoftWoehr.util.*;

/**
 *
 * @author $Author: jwoehr $
 * @version $Revision: 1.2 $
 */
public class Desktop extends Window implements ActionListener,
        Runnable,
        SoftWoehr,
        verbose {

    /**
     * **************************************
     */
    /*% SoftWoehr default variables section. */
    /**
     * **************************************
     */
    /**
     * Revision level
     */
    private static final String rcsid = "$Id: Desktop.java,v 1.2 2016-11-10 02:07:44 jwoehr Exp $";

    /**
     * Implements com.SoftWoehr.SoftWoehr
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
    private final verbosity v = new verbosity(this);

    /**
     * *******************************************
     */
    /*% SoftWoehr default variables section ends. */
    /**
     * *******************************************
     */
    /**
     * ********************************
     */
    /*% User variables section starts. */
    /**
     * ********************************
     */
    Frame myFrame;
    PopupMenu myPopup;
    ShutdownHelper myShutdownHelper;
    ThreadGroup myThreadGroup;

    /**
     * ******************************
     */
    /*% User variables section ends. */
    /**
     * ******************************
     */
    /**
     * ******************************
     */
    /*% User methods section starts. */
    /**
     * ******************************
     */
    /**
     * Arity/0 ctor.
     *
     * @param f
     */
    public Desktop(Frame f) {
        super(f);
        myFrame = f;
        reinit();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Shutdown clients and then do any necessary shutdown to self.
     *
     * @see com.SoftWoehr.SoftWoehr
     */
    @Override
    public int shutdown() {
        return myShutdownHelper.shutdownClients();
    }

    /**
     * Reinitialize object, discarding previous state.
     */
    public final void reinit() {
        myShutdownHelper = new ShutdownHelper();
        myThreadGroup = new ThreadGroup("MyDesktop");
        setSize(getMaximumSize());
        setBackground(Color.pink);
        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (fe.getID() == FocusEvent.FOCUS_GAINED) {
                    toBack();
                    myFrame.toBack();
                }
                /* End if*/
            }

            @Override
            public void focusLost(FocusEvent fe) {
            }
        });

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent we) {
                toBack();
                myFrame.toBack();
            }

            @Override
            public void windowClosing(WindowEvent we) {
            }

            @Override
            public void windowClosed(WindowEvent we) {
            }

            @Override
            public void windowIconified(WindowEvent we) {
            }

            @Override
            public void windowDeiconified(WindowEvent we) {
                toBack();
                myFrame.toBack();
            }

            @Override
            public void windowActivated(WindowEvent we) {
            }

            @Override
            public void windowDeactivated(WindowEvent we) {
            }
        });

        myPopup = new PopupMenu();
        /* Instance our popup menu.*/
        MenuItem tempItem;
        /* Menu loader.                     */

        tempItem = new MenuItem("Shell" /* Create item.*/,
                new MenuShortcut(KeyEvent.VK_S));
        tempItem.addActionListener(this);
        myPopup.add(tempItem);
        tempItem = new MenuItem("OldShell" /* Create item.*/,
                new MenuShortcut(KeyEvent.VK_0));
        tempItem.addActionListener(this);
        myPopup.add(tempItem);
        tempItem = new MenuItem("Edit" /* Create item.*/,
                new MenuShortcut(KeyEvent.VK_E));
        tempItem.addActionListener(this);
        myPopup.add(tempItem);
        tempItem = new MenuItem("Quit" /* Create item.*/,
                new MenuShortcut(KeyEvent.VK_Q));
        tempItem.addActionListener(this);
        myPopup.add(tempItem);

        /**
         * Add popup to the desktop.
         */
        add(myPopup);

        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger() || (0 != (MouseEvent.BUTTON3_MASK & e.getModifiers()))) {
                    Point p = e.getPoint();
                    myPopup.show(e.getComponent(), e.getX(), e.getY());
                } else {
                }
                /* End if*/
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

    }

    /**
     * Run the Desktop.
     */
    @Override
    public void run() {
        setVisible(true);
    }

    /**
     * Handle popup menu actions.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String a = e.getActionCommand();
        switch (a) {
            case "Shell":
                /*! TODO: Reference shell in a controller, e.g., the
                *  Shutdown array, or whatever subsumes that.
                */
                com.SoftWoehr.FIJI.FIJIGui fg = new com.SoftWoehr.FIJI.FIJIGui();
                // fg.setVerbose(isVerbose());
                /*! Yes, we're definitely going to have to be more sophisticated. */
                new Thread(myThreadGroup, fg).start();
                break;
        /* End if*/
            case "OldShell":
                /*! TODO: Reference shell in a controller, e.g., the
                *  Shutdown array, or whatever subsumes that.
                */
                ShellFrame sf = new ShellFrame(this);
                sf.setVerbose(isVerbose());
                /*! Yes, we're definitely going to have to be more sophisticated. */
                new Thread(myThreadGroup, sf).start();
                break;
            case "Edit":
                break;
            case "Quit":
                setVisible(false);
                dispose();
                System.exit(0);
            default:
                break;
        }
    }

    /**
     * ****************************
     */
    /*% User methods section ends. */
    /**
     * ****************************
     */
    /**
     * *******************************************
     */
    /*% SoftWoehr default methods section starts. */
    /**
     * *******************************************
     */
    /**
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     */
    public boolean isVerbose() {
        return isverbose;
    }

    /**
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     */
    public void setVerbose(boolean tf) {
        isverbose = tf;
    }

    /**
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     */
    public void announce(String s) {
        v.announce(s);
    }

    /**
     * *****************************************
     */
    /*% SoftWoehr default methods section ends. */
    /**
     * *****************************************
     */
    /**
     * ******
     */
    /*% Main */
    /**
     * ******
     */
    /**
     * Demonstrate <code>Desktop</code>.
     */
    public static void main(String argv[]) {

        GetArgs myArgs = new GetArgs(argv);/* Assimilate the command line.     */

 /* GPL'ed SoftWoehr announces itself. */
        System.out.println("Desktop, Copyright (C) 1988 Jack J. Woehr.");
        System.out.println("Desktop comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("Please see the file COPYING and/or COPYING.LIB");
        System.out.println("which you should have received with this software.");
        System.out.println("This is free software, and you are welcome to redistribute it");
        System.out.println("under certain conditions enumerated in COPYING and/or COPYING.LIB.");

        // Test code goes here.
        // -------------------
        // GetArgs.main(argv);       /* Delete this stub when you write some code.*/
        Frame f = new Frame();
        Desktop desktop = new Desktop(f);
        for (int x = 0; x < myArgs.optionCount(); x++) {
            Argument a = myArgs.nthOption(x);
            if (a.option.equals("-v")) {
                desktop.setVerbose(true);
                desktop.announce("Verbose mode set.\n");
            }
            /* End if*/
        }
        /* End for*/
        desktop.show();
        // new Thread(desktop).start();
        // -------------------
    }
}
/* End of Desktop class*/

 /*  End of Desktop.java */
