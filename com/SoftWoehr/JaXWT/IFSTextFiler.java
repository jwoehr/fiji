/*
 * IFSTextFiler.java
 *
 * Copyright *C* 2001 Jack J. Woehr
 * All Rights Reserved
 * PO Box 51, Golden, Colorado 80402-0051 USA
 * http://www.softwoehr.com
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * Free Software NO WARRANTY NO GUARANTEE
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * Created on November 6, 2000, 8:29 PM
 */

package com.SoftWoehr.JaXWT;

import java.io.*;
import javax.swing.JTextArea;
import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400SecurityException;
import com.ibm.as400.access.IFSJavaFile;
import com.ibm.as400.access.IFSTextFileInputStream;
import com.ibm.as400.access.IFSTextFileOutputStream;

/** A class to file to IFS from a text editor.
 * @deprecated Currently doesn't work!!! Not finished.
 * @author jax
 * @version $Id: IFSTextFiler.java,v 1.3 2001-09-30 08:24:22 jwoehr Exp $
 */
public class IFSTextFiler extends Object implements TextFiler {
    
    private EditPanePanel my_edit_panel;
    private AS400 my_as400;
    
    /** Creates new IFSTextFiler
     * @param as400 associated as400 instance
     * @param ep associated EditPanePanel
     */
    public IFSTextFiler(AS400 as400, EditPanePanel ep) {
        my_as400 = as400;
        my_edit_panel = ep;
    }
    
    // Member Access
    ////////////////
    
    /** Set the AS400 to which we refer
     * @param as400 associated as400 instance
     */
    protected void set_as400(AS400 as400) {
        my_as400 = as400;
    }
    
    /** Set the AS400 to which we refer
     * @return associated as400 instance
     */
    protected AS400 get_as400() {
        return my_as400;
    }
    
    /** Set the edit text area to which we refer
     * @param ep associated EditPanePanel
     */
    protected void set_edit_panel(EditPanePanel ep) {
        my_edit_panel = ep;
    }
    
    /** Set the edit text area to which we refer
     * @return associated EditPanePanel
     */
    protected EditPanePanel get_edit_panel() {
        return my_edit_panel;
    }
    
    /**
     * @return  */
    protected javax.swing.JFrame get_panel_frame() {
        return get_edit_panel().get_frame();
    }
    
    /** Get the edit text area to which we refer
     * @return  */
    protected EditTextPane get_edit_text_pane() {
        return my_edit_panel.get_text_pane();
    }
    
    // Required interfaces
    //////////////////////
    
    /** Save some predefined text to a file.
     * True iff saved.
     * @param f
     * @param encoding
     * @return  */
    public boolean save(File f,int encoding) {
        boolean result = save_to_file(f, encoding, get_edit_text_pane().getText());
        if (result) {
            get_edit_text_pane().set_changed(false);
        }
        
        return result;
    }
    
    /** Open a file in some predefined area
     * @param f The file
     * @param encoding The encoding to be used
     * @return  */
    public boolean open(File f, int encoding) {
        boolean result = read_text_pane_from_file(f, encoding);
        if (result) {
            get_edit_text_pane().set_changed(false);
        }
        return result;
    }
    
    /** Insert a file in some area at some offset
     * @param f The file
     * @param encoding The encoding to be used
     * @param position position to insert at
     * @return <code>true</code> .iff success
     */
    public boolean insert(File f, int encoding, int position) {
        boolean result= insert_text_pane_from_file(f, encoding, position);
        return result;
    }
    
    // Implementation details
    /////////////////////////
    
    /** If file is readable, read text into area from it
     * @param f The file
     * @param encoding The encoding to be used
     * @return <code>true</code> .iff success
     */
    protected boolean read_text_pane_from_file(File f, int encoding) {
        boolean result = false;
        String text = read_text_from_file(f, encoding);
        if (null != text) {
            get_edit_text_pane().setText(text);
            result = true;
        }
        return result;
    }
    
    /** If file is readable, insert into area from it
     * @param f The file
     * @param encoding The encoding to be used
     * @param offset  position to insert at
     * @return <code>true</code> .iff success
     */
    protected boolean insert_text_pane_from_file(File f, int encoding, int offset) {
        boolean result = false;
        String text = read_text_from_file(f, encoding);
        if (null != text) {
            //     get_edit_text_pane().insert(text, offset);
            // Has to be implemented in terms of replaceSelection
            // which means we have to be careful where cursor is
            // and what is selected.
            result = true;
        }
        return result;
    }
    
    /** If file is readable, read text from it
     * @param f  The file
     * @param encoding The encoding to be used
     * @return <code>true</code> .iff success
     */
    protected String read_text_from_file(File f, int encoding) {
        IFSTextFileInputStream fis = null;
        String result = null;
        
        try {    // Read all in as chars in a buffered fashion.
            fis = new IFSTextFileInputStream(my_as400, (IFSJavaFile) f, IFSTextFileInputStream.SHARE_ALL);
            result = fis.read((int) f.length());
        }
        catch (AS400SecurityException ex) {
            StockDialog.showExceptionDialog(get_panel_frame(), "Access denied reading" + f.getAbsolutePath() + ".", ex, "Exception reading file");
        }
        catch (FileNotFoundException ex) {
            StockDialog.showExceptionDialog(get_panel_frame(), "Couldn't find " + f.getAbsolutePath() + ".", ex, "Exception reading file");
        }
        catch (UnsupportedEncodingException ex) {
            StockDialog.showExceptionDialog(get_panel_frame(), "Unsupported encoding.", ex, "Exception reading file");
        }
        catch (IOException ex) {
            StockDialog.showExceptionDialog(get_panel_frame(), "I/O Exception reading " + f.getAbsolutePath() + ".", ex, "Exception reading file");
        }
        if (null != fis) {
            try {
                fis.close();
            }
            catch (IOException ex) {
                StockDialog.showExceptionDialog(get_panel_frame(), "I/O Exception closing FileInputStream.", ex, "Exception reading file");
            }
        }
        return result;
    }
    
    /** Save string if possible. True if success.
     * @param f  The file
     * @param encoding The encoding to be used
     * @param s String to write.
     * @return <code>true</code> .iff success
     */
    protected boolean save_to_file(File f, int encoding, String s) {
        boolean result = false;
        if (!f.isDirectory()) {
            try {
                f.createNewFile(); // Simply returns false if extant
                result = write_to_file(f, encoding, s);
            }
            catch (IOException ex) {
                StockDialog.fileCreateException(get_panel_frame(), f.getAbsolutePath(), ex);
                result = false;
            }
        }
        else {
            StockDialog.showErrorDialog
            (get_panel_frame(), "The path " + f.getAbsolutePath() + " is a directory, not a file.", "Can't Overwrite Directory");
        }
        return result;
    }
    
    /** If file is writable, write text area to it
     * @param f The file
     * @param encoding The encoding to be used
     * @param s String to write.
     * @return <code>true</code> .iff success
     */
    protected boolean write_to_file(File f, int encoding, String s) {
        boolean result = false;
        IFSTextFileOutputStream fos = null;
        String filename = f.getAbsolutePath();
        if (null == s) {
            s = "";
        }
        if (f.canWrite()) {
            try {
                fos = new IFSTextFileOutputStream(my_as400, (IFSJavaFile) f, IFSTextFileOutputStream.SHARE_ALL, false);
                fos.setCCSID(encoding);
                fos.write(s);
                result = true;
            }
            catch (AS400SecurityException ex) {
                StockDialog.showExceptionDialog(get_panel_frame(), "Access denied writing" + f.getAbsolutePath() + ".", ex, "Exception writing file");
            }
            catch (IOException ex) {
                StockDialog.fileWriteException(get_panel_frame(), filename, ex);
            }
            catch (java.beans.PropertyVetoException ex) {
                StockDialog.showExceptionDialog(get_panel_frame(), filename, ex, "Couldn't set write encoding");
            }
            
            if (null != fos) {
                try {
                    fos.close();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else { // File wasn't writable
            StockDialog.showErrorDialog(get_panel_frame(), "The file " + filename + " is not writable.", "Can't Write File");
        }
        return result;
    }
}