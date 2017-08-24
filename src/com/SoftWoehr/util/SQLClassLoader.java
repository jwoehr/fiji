/*
 * SQLClassLoader.java
 *
 * Copyright *C* 2001, 2017 Jack J. Woehr
 * All Rights Reserved
 * PO Box 51, Golden, Colorado 80402-0051 USA
 * http://www.softwoehr.com
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * Free Software NO WARRANTY NO GUARANTEE
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * Open Source SoftWoehr
 * Created on May 5, 2001, 12:31 AM
 */
package com.SoftWoehr.util;

import java.io.*;
import java.sql.*;
import com.ibm.as400.access.*;
import java.lang.reflect.Method;

/**
 * Store and load Java classes to and from a database.
 *
 * The class database can be created with SQL something like this:
 *
 * <code>CREATE TABLE MYCOLLECT.MYTABLE(CLASSNAME VARCHAR(256), CLASSFILE BLOB)</code>
 *
 * @author jax
 * @version $Id: SQLClassLoader.java,v 1.5 2017-08-24 01:20:36 jwoehr Exp $
 */
public class SQLClassLoader extends java.lang.ClassLoader {

    private String my_server_url = null;
    private String my_sql_collection_name = null;
    private String my_sql_table_name = null;
    private String my_userid = null;
    private int my_session_type = SQLSession.NO_SESSION_TYPE;

    private AS400JDBCRowSet my_rowset = null;

    /**
     * Free the RowSet connection resource
     */
    public void close_rowset() {
        if (my_rowset != null) {
            try {
                my_rowset.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * Creates new SQLClassLoader
     *
     * @param user_id
     * @param password
     * @param server_name
     * @param sql_collection_name
     * @param sql_table_name
     * @param session_type
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public SQLClassLoader(String server_name, String user_id, String password, String sql_collection_name, String sql_table_name, int session_type)
            throws java.sql.SQLException, ClassNotFoundException {
        my_session_type = session_type;
        my_server_url = SQLSession.calcURL(server_name, session_type);
        my_sql_collection_name = sql_collection_name;
        my_sql_table_name = sql_table_name;
        my_userid = user_id;
        // /* Debug */ System.out.println("server URL " + my_server_url + " collection name " + my_sql_collection_name + " table name " + my_sql_table_name + " type " + session_type);
        SQLSession.registerDriver(my_session_type);

        switch (session_type) {
            case SQLSession.JT400:
                my_rowset = new AS400JDBCRowSet(my_server_url, my_userid, password);
                break;

            case SQLSession.JDBCODBC:
                break;

            case SQLSession.POSTGRES:
                break;

            case SQLSession.MYSQL:
                break;

            default:
        }
    }

    /**
     * An exception when no result set comes back
     */
    public static class NoRowSetException extends Exception {
    }

    /**
     * Check validity of object state before running.
     *
     * @throws NoRowSetException
     */
    public void validate_setup()
            throws NoRowSetException {
        switch (my_session_type) {

            case SQLSession.JT400:
                if (null == my_rowset) {
                    throw new NoRowSetException();
                }
                break;

            case SQLSession.JDBCODBC:
                break;

            case SQLSession.POSTGRES:
                break;

            case SQLSession.MYSQL:
                break;

            default:
        }
    }

    /**
     * Read in a file to a byte array. The file is the class file for the
     * fully-qualified class name passed in.
     *
     * @param class_name
     * @return
     */
    public byte[] read_class_file(String class_name) {
        byte[] input_bytes = null;
        int bytes_read;
        File class_file = null;
        FileInputStream file_stream;
        String file_name;
        String path_name;

        // Support package names by converting the class name to
        //   a file name by replacing the "." in the package name with a
        //   path separator. This only applies because the class file is being
        //  loaded from a file.
        file_name = class_name.replace('.', File.separatorChar);
        path_name = file_name + ".class";

        // Read the class file into a buffer in one shot
        try {
            class_file = new File(path_name);
            file_stream = new FileInputStream(class_file);

            // Create a buffer big enough to hold the entire class file.
            bytes_read = (int) class_file.length();
            input_bytes = new byte[bytes_read];

            // Read the class file into the buffer
            try {
                /* bytes_read = */
                file_stream.read(input_bytes);
            } catch (IOException e) {
                System.err.println("Exception trying to read the class file: " + e);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File " + (path_name == null ? "??" : path_name) + " doesn't exist: " + e);
        }
        return input_bytes;
    }

    /**
     * Store a class object to the AS400
     *
     * @param class_name
     * @param class_object class as byte array
     * @throws NoRowSetException
     * @throws SQLException
     */
    public void insertAS400class(String class_name, byte[] class_object)
            throws NoRowSetException, SQLException {
        // Throws if object not set up correctly.
        validate_setup();

        // Set the prepared statement and initialize the parameters.
        my_rowset.setCommand("INSERT INTO " + my_sql_collection_name + "." + my_sql_table_name + " VALUES (?, ?)");
        my_rowset.setString(1, class_name);
        my_rowset.setObject(2, class_object);

        // Populate the rowset.
        my_rowset.execute();
    }

    /**
     * @param class_name
     * @throws NoRowSetException
     * @throws ClassNotFoundException
     * @throws SQLException
     * @return
     */
    protected byte[] selectAS400class(String class_name)
            throws NoRowSetException, ClassNotFoundException, SQLException {
        // Throws if object not set up correctly.
        validate_setup();

        my_rowset.setCommand(
                "SELECT CLASSFILE FROM "
                + my_sql_collection_name
                + "."
                + my_sql_table_name
                + " WHERE CLASSNAME = '"
                + class_name
                + "'"
        );
        my_rowset.execute();
        my_rowset.next();
        Blob blob = my_rowset.getBlob("CLASSFILE");
        return blob.getBytes(1, new Long(blob.length()).intValue());
    }

    /**
     * Insert a class object into the database.
     *
     * @param class_name
     * @param class_object
     * @throws NoRowSetException
     * @throws SQLException
     */
    protected void addClassToDataSource(String class_name, byte[] class_object)
            throws NoRowSetException, SQLException {
        switch (my_session_type) {

            case SQLSession.JT400:
                insertAS400class(class_name, class_object);
                break;

            case SQLSession.JDBCODBC:
                break;

            case SQLSession.POSTGRES:
                break;

            case SQLSession.MYSQL:
                break;

            default:
        }
    }

    /**
     * Read a class file and insert it into the database under the given name
     *
     * @param class_name
     * @param file_name
     * @throws NoRowSetException
     * @throws SQLException
     */
    public void insertClassFile(String class_name, String file_name)
            throws NoRowSetException, SQLException {
        byte[] b = read_class_file(file_name);
        if (b != null) {
            addClassToDataSource(class_name, b);
        } else {
            System.err.println("No class file data to insert.");
        }
    }

    /**
     * Get a class from SQLClassLoader's assigned data source.
     *
     * @return The not-yet-resolved Class object
     * @param class_name The sought class
     * @throws ClassNotFoundException If class is not found.
     */
    @Override
    protected Class findClass(String class_name) throws ClassNotFoundException {
        byte[] b = null;
        Class c = null;

        switch (my_session_type) {

            case SQLSession.JT400:
                try {
                    b = selectAS400class(class_name);
                } catch (NoRowSetException | SQLException e) {
                    System.out.println(e);
                }
                break;

            case SQLSession.JDBCODBC:
                break;

            case SQLSession.POSTGRES:
                break;

            case SQLSession.MYSQL:
                break;

            default:
        }

        if (null != b) {
            c = defineClass(class_name, b, 0, b.length);
        }

        return c;
    }

    /**
     * Test self
     *
     * @return
     * @param user_id
     * @param password
     * @param call_main
     * @param server_name
     * @param collection_name
     * @param table_name
     * @param class_name
     * @param file_name
     * @param session_type
     * @param command
     */
    public static Class test_me(
            String server_name,
            String user_id,
            String password,
            String collection_name,
            String table_name,
            String class_name,
            String file_name,
            int session_type,
            String command,
            boolean call_main
    ) {
        SQLClassLoader sql = null;
        Class c = null;
        char cmd = '!';

        // Parse command
        if (command != null) {
            cmd = command.trim().charAt(0);
        } else {
            usage();
        }

        // Get connection
        if (server_name != null && table_name != null) {
            try {
                sql = new SQLClassLoader(server_name, user_id, password, collection_name, table_name, session_type);
            } catch (java.sql.SQLException | ClassNotFoundException e) {
                System.err.println(e);
            }
        } else {
            usage();
        }

        // Try it
        if (sql != null && class_name != null) {
            java.lang.reflect.Method[] methods;
            switch (cmd) {

                case 'r':
                    try {
                        c = sql.loadClass(class_name, true);
                        System.out.println("Resolved class is: " + c.toString());
                        System.out.println("Methods are: ");
                        methods = c.getDeclaredMethods();
                        for (Method m : methods) {
                            System.out.println(m.toString());
                            if (call_main && m.getName().equals("main")) {
                                Object[] a = new Object[1];
                                a[0] = new String[0];
                                try {
                                    System.out.println("Calling " + c + " method " + m);
                                    m.invoke(c, a);
                                } catch (java.lang.reflect.InvocationTargetException | java.lang.IllegalAccessException e) {
                                    System.err.println(e);
                                }
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println(e);
                    }
                    break;

                case 'w':
                    if (class_name == null) {
                        System.err.println("No class name given.");
                    } else {
                        if (file_name != null) {
                            try {
                                sql.insertClassFile(class_name, file_name);
                            } catch (SQLClassLoader.NoRowSetException | SQLException e) {
                                System.err.println(e);
                            }
                        } else {
                            System.err.println("No filename given for write.");
                        }
                    }
                    break;

                default:
                    System.out.println("Unknown command '" + command + "'.");
            }
        } else {
            usage();
        }
        return c;
    }

    /**
     * Print usage message
     */
    public static void usage() {
        System.out.println("Usage:");
        System.out.println("SQLClassLoader -s server_name -u userid -p password -c collection_name -t table_name -n class_name -f file_name -y session_type -x cmd [ -m (calls main) ]");
    }

    /**
     * Test storing and retrieving classes
     *
     * @param argv Arguments to the test.
     */
    public static void main(String[] argv) {
        GetArgs args = new GetArgs(argv);
        Argument a;

        int my_session_type = SQLSession.NO_SESSION_TYPE;
        String my_server_name = null;
        String my_sql_collection_name = null;
        String my_sql_table_name = null;
        String my_class_name = null;
        String my_file_name = null;
        String my_command = null;
        String my_userid = null;
        String my_password = null;
        boolean call_main = false;

        // Get our arguments */
        System.err.println("Option count is " + args.optionCount() + "\n");

        // Loop through options */
        for (int i = 0; i < args.optionCount(); i++) {
            System.err.println("Evaluting option number " + i);
            a = args.nthOption(i);
            System.err.println("Evaluating option " + a.option + " " + a.argument);
            switch (a.option) {
                case "-s":
                    my_server_name = a.argument.trim();
                    System.out.println("Server is " + my_server_name);
                    break;
                case "-u":
                    my_userid = a.argument.trim();
                    System.out.println("User id is " + my_userid);
                    break;
                case "-p":
                    my_password = a.argument.trim();
                    System.out.println("Password is " + my_password);
                    break;
                case "-c":
                    my_sql_collection_name = a.argument.trim();
                    System.out.println("Collection is " + my_sql_collection_name);
                    break;
                case "-t":
                    my_sql_table_name = a.argument.trim();
                    System.out.println("Table is " + my_sql_table_name);
                    break;
                case "-n":
                    my_class_name = a.argument.trim();
                    System.out.println("Class is " + my_class_name);
                    break;
                case "-f":
                    my_file_name = a.argument.trim();
                    System.out.println("File is " + my_file_name);
                    break;
                case "-m":
                    call_main = true;
                    break;
                case "-y":
                    try {
                        my_session_type = Integer.parseInt(a.argument.trim());
                        System.out.println("Session type is " + my_session_type);
                    } catch (NumberFormatException e) {
                        System.err.println(e);
                    }
                    break;
                case "-x":
                    my_command = a.argument.trim();
                    break;
                default:
                    System.err.println("Bad option " + a.option + " " + a.argument);
                    usage();
                    System.exit(-1);
            }
        }
        System.out.println("Arguments all evaluted. Ready to call:");
        System.out.println(
                "test_me (" + my_server_name
                + ", " + my_sql_collection_name
                + ", " + my_sql_table_name
                + ", " + my_class_name
                + ", " + my_file_name
                + ", " + my_session_type
                + ", " + my_command
                + ", " + call_main
                + ")"
        );

        try {
            test_me // Execute self test
                    (
                            my_server_name,
                            my_userid,
                            my_password,
                            my_sql_collection_name,
                            my_sql_table_name,
                            my_class_name,
                            my_file_name,
                            my_session_type,
                            my_command,
                            call_main
                    );
        } finally {
            System.gc();
        }

        System.exit(0);
    }
}
