/*********************************************/
/* SQLSession ... SQL functionality          */
/* jack j. woehr jax@well.com jwoehr@ibm.net */
/* http://www.well.com/user/jax/rcfb         */
/* P.O. Box 51, Golden, Colorado 80402-0051  */
/*********************************************/
/*   Copyright *C* 1998, 2001 Jack J. Woehr  */
/*	      All Rights Reserved	     */
/* PO Box 51 Golden, Colorado 80402-0051 USA */
/*	    http://www.softwoehr.com	     */
/*	  http://fiji.sourceforge.net	     */
/*                                           */
/*       This Program is Free                */
/*            Softwoehr                      */
/*                                           */
/* Permission to distribute this Softwoehr   */
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
/*********************************************/

package com.SoftWoehr.util;

import java.sql.*;
import com.SoftWoehr.*;

/**
 * SQLSession makes connections
 * and issues queries via JDBC
 * using various drivers.
 * @author  $Author: jwoehr $
 * @version $Revision: 1.3 $
 */
public class SQLSession implements SoftWoehr, verbose {
    /** Revision info */
    private final String rcsid = "$Id: SQLSession.java,v 1.3 2001-09-15 07:02:14 jwoehr Exp $";
    /** Implements com.SoftWoehr.SoftWoehr
     * @return the rcsid
     */
    public String rcsId() {return rcsid;}
    
    /** shutdown() here does nothing.
     * @see com.SoftWoehr.SoftWoehr#
     * @return always 0.
     */
    public int shutdown() { return 0; }
    
    /**  Maintains connection to server. */
    private Connection connection = null;
    /**  Server-database name. */
    private String server         = null;
    /**  User id. */
    private String user           = null;
    /**  Password to access server-database. */
    private String password       = null;
    /**  A statement to be passed to server. */
    private Statement statement   = null;
    /**  Result set returned by server. */
    private ResultSet resultSet   = null;
    /**  Meta info on the ResultSet returned. */
    private ResultSetMetaData md  = null;
    /**  Identifies driver */
    private int sessionType;
    /**  Allows debugging info. */
    public boolean isverbose = true;
    /**  Flags whether we're being verbose. */
    
    private static verbosity v =
    new com.SoftWoehr.util.verbosity(new verbose() {
        private boolean verby = false;
        
        /** Returns true if instance is in verbose mode.
         */
        public boolean isVerbose() { return verby; }
        
        /** Sets <code>true</code> or resets <code>false</code> verbose mode.
         */
        public void setVerbose(boolean b) { verby = b; }
        
        /** Say something if the object is in verbose mode, be silent otherwise.
         */
        public void announce(String message) {  System.out.println(message); }
    });
    
    
    /**
     * Identifier for JDBC/ODBC driver.
     */
    public static final int NO_SESSION_TYPE = -1;
    /**
     * Identifier for JDBC/ODBC driver.
     */
    public static final int JDBCODBC = 0;
    
    /**
     * Identifier for JDBC/ODBC driver.
     */
    public static final int JT400    = 1;
    
    /**
     * Identifier for JDBC/ODBC driver.
     */
    public static final int POSTGRES = 2;
    
    /**
     * Identifier for JDBC/ODBC driver.
     */
    public static final int MYSQL    = 3;
    
    /** Create an SQLSession with the default driver.
     * @exception java.sql.SQLException res ipse loq
     * @exception java.lang.ClassNotFoundException res ipse loq
     */
    public SQLSession()
    throws java.sql.SQLException
    , java.lang.ClassNotFoundException {
        this(SQLSession.JDBCODBC);
    }
    
    /** Create an SQLSession with a specific driver.
     * Session type JT400 loads IBM JDBC bridge.
     * Session type JDBCODBC loads JDBC/Odbc  driver.
     * Session type POSTGRES loads Postgresql JDBC driver.
     * @param sess The session type constant
     * @throws ClassNotFoundException res ipse loq
     * @exception java.sql.SQLException res ipse loq
     */
    public SQLSession(int sess)
    throws java.sql.SQLException
    , java.lang.ClassNotFoundException {
        sessionType = sess;
        registerDriver(sess);
    }
    
    
    /** Register one of the supported drivers by id.
     * @param session_type sess const int
     * @throws SQLException res ipse loq
     * @throws ClassNotFoundException res ipse loq
     */
    public static void registerDriver(int session_type)
    throws java.sql.SQLException
    , java.lang.ClassNotFoundException {
        switch (session_type) {
            case JT400   :
                java.sql.DriverManager.registerDriver
                (new com.ibm.as400.access.AS400JDBCDriver());
                sync_announce("SQLSession using Jt400 Jdbc.");
                break;
                
            case JDBCODBC:
                Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
                sync_announce("SQLSession using JdbcOdbc bridge.");
                break;
                
            case POSTGRES:
                Class.forName("postgresql.Driver");
                sync_announce("SQLSession using Postgresql.");
                break;
                
            case MYSQL:
                Class.forName("mysql.Driver");
                sync_announce("SQLSession using MySQL.");
                break;
                
            default:
                sync_announce("No driver type selection.");
                throw new java.lang.ClassNotFoundException();
        }                                                     /* End switch*/
    }
    
    /** Formulate the URL representing this type of JDBC connection.
     * @param server_name Server
     * @param session_type Session type const int
     * @return The URL representing this type of JDBC connection.
     */
    public static String calcURL(String server_name, int session_type) {
        String url = null;
        
        switch (session_type) {
            case JT400:
                url = "jdbc:as400://" + server_name;
                break;
                
            case JDBCODBC:
                url = "jdbc:odbc:" + server_name;
                break;
                
            case POSTGRES:
                url = "jdbc:postgresql:" + server_name;
                /** The postgres driver recognises JDBC URL's of the form:
                 * jdbc:postgresql:database
                 * jdbc:postgresql://host/database
                 * jdbc:postgresql://host:port/database
                 *
                 *   Also, you can supply both username and passwords as arguments, by appending
                 *   them to the URL. eg:
                 *
                 *   jdbc:postgresql:database?user=me
                 *   jdbc:postgresql:database?user=me&password=mypass
                 */
                break;
                
            case MYSQL:
                url = "jdbc:mysql:" + server_name;
                break;
        }
        return url;
    }
    
    /** Establish connection to the database using name, userid, password.
     * Session type JT400 uses IP "url" for IBM JDBC.
     * Session type JDBCODBC uses SNA "url" for CA ODBC.
     * Session type POSTGRES uses Postgresql JDBC "url".
     * n   * @exception java.sql.SQLException
     * @param s Server
     * @param u User id
     * @param p Password
     * @throws SQLException res ipse loq
     */
    public void connect(String s, String u, String p)
    throws java.sql.SQLException {
        server = s; user = u; password = p;
        String url = calcURL(server, sessionType);
        
        if (null == url) {
            announce("No driver selected.");
            throw new java.sql.SQLException();
        }
        
        announce("JDBC URL composed is " + url);
        connection = DriverManager.getConnection(url, user, password);
        statement = connection.createStatement();
    }
    
    /** Connect to the server with just a url.
     * Session type JT400 uses IP "url" for IBM JDBC.
     * Session type JDBCODBC uses SNA "url" for CA ODBC.
     * Session type POSTGRES uses Postgresql JDBC "url".
     * @param server_name Server to connect to.
     * @exception java.sql.SQLException res ipse loq
     */
    public void connect(String server_name)
    throws java.sql.SQLException {
        String url = calcURL(server_name, sessionType);
        if (null == url) {
            announce("No driver selected.");
            throw new java.sql.SQLException();
        }                                                     /* End switch*/
        
        announce("JDBC URL composed is " + url);
        connection = DriverManager.getConnection(url);
        statement = connection.createStatement();
    }
    
    /** Close the connection to the current server.
     * @exception java.sql.SQLException res ipse loq
     */
    public void disconnect() throws java.sql.SQLException {
        
        if (statement != null) {
            statement.close();
            statement = null;
        }
        
        // Throw away most of our state objects.
        server    = null; user = null; password = null;
        resultSet = null; md   = null;
        
        if (!(connection == null)) {
            if (!connection.isClosed()) {
                announce("SQLSession disconnect.");
                connection.close();
            }
        }
        connection = null;
    }
    
    /** Get a Prepared Statement to use in a query
     * @param sql_text Text of query/update
     * @throws SQLException res ipse loq
     * @return the prepared statement
     */
    public PreparedStatement prepareStatement(String sql_text)
    throws java.sql.SQLException {
        return connection.prepareStatement(sql_text);
    }
    
    /** Execute an SQL statement and grab the result set, if any.
     * @param sqlText Text of query/update
     * @exception java.sql.SQLException res ipse loq
     * @return true iff success
     */
    public boolean execute(String sqlText) throws java.sql.SQLException {
        
        boolean anyMoreResults = statement.execute(sqlText);
        grabResultSet();
        return anyMoreResults;                  /* are more results waiting*/
    }
    
    /**  Grab (possibly null) latest result set and save it. */
    private void grabResultSet() throws java.sql.SQLException {
        resultSet = statement.getResultSet();/* possibly null result set of execute()*/
        if (!(resultSet == null)) // Instance ResultSetMetaData
        {
            md = resultSet.getMetaData();
        }
        else {
            md = null;
        }                                                         /* End if*/
    }
    
    /** Fetch to the caller the last result set.
     * @return the result set
     */
    public ResultSet getResultSet() {
        return resultSet;
    }
    
    /** Fetch to the caller metadata for the last result set.
     * @return the metadata
     */
    public ResultSetMetaData getResultSetMetaData() {
        return md;
    }
    
    /** Shlep in the next result set, possibly null.
     * @throws SQLException res ipse loq
     * @return next result set.
     */
    public boolean getMoreResults() throws java.sql.SQLException {
        boolean isResultSet = statement.getMoreResults();
        if (isResultSet) {
            grabResultSet();
        }
        else {
            resultSet = null;
            md = null;
        }                                                         /* End if*/
        return isResultSet;
    }
    
    /** Get our connection.
     * @return the Connection or <CODE>null</CODE>.
     */
    public Connection getConnection() {
        return connection;
    }
    
    /** Return <CODE>true</CODE> if connected.
     * @return <CODE>true</CODE> if connected.
     */
    public boolean isConnected() {
        boolean result = false;
        if (connection != null) {
            try {
                if (!connection.isClosed()){
                    result = true;
                }
            }
            catch (java.sql.SQLException ex) {
                result = false; // redundant
            }
        }
        return result;
    }
    
    /** Return <CODE>true</CODE> if verbose.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @return <CODE>true</CODE> if verbose
     */
    public boolean isVerbose() {
        return isverbose;
    }
    
    /** Announce a string if verbose.
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.verbosity
     * @param s String to announce.
     */
    public void announce(String s) {
        sync_announce(s);
    }
    
    /** Announce synchronized if verbose */
    private static synchronized void sync_announce(String s) {
        v.announce(s);
    }
    
    /** Set verbosity
     * @see com.SoftWoehr.util.verbose
     * @see com.SoftWoehr.util.verbosity
     * @param b <CODE>true</CODE> if verbose
     */
    public void setVerbose(boolean b) {
        isverbose = b;
    }
    
    /** Display usage message for main(). */
    static void usage() {
        System.err.println("Usage: SQLSession -s server [-t sessionType] [-u userId] [-p password] [-l separator-string] -q \"SQL Text\"");
        System.err.println("       where sessionType is jdbcOdbc jt400 or postgresql");
        System.err.println("       where optional separator is one or more chars");
    }
    
    /** Demonstrate the class by taking one SQL query.
     * @param argv args to main
     * @see #usage
     */
    public static void main(String argv[]) {
        String server = null;
        String sessionType = "";
        String sql = null;
        String userId = null;
        String password = null;
        String separator = ",";
        SQLSession session = null;
        ResultSet r = null;
        ResultSetMetaData rmd = null;
        boolean isVerbose = false;
        
        int sess = JDBCODBC;
        int i;
        GetArgs args = new GetArgs(argv);
        Argument a = null;
        
    /* Get our arguments */
        System.err.println("Option count is " + args.optionCount() + "\n");
    /* Loop through options */
        for (i = 0; i < args.optionCount(); i++) {
            System.err.println("Evaluting option number " + i);
            a = args.nthOption(i);
            System.err.println("Evaluating option " + a.option + " " + a.argument);
            if (a.option.equals("-s")) {
                server = a.argument;
                System.err.println("Server is " + server);
            }
            else if (a.option.equals("-t")) {
                sessionType = a.argument;
            }
            else if (a.option.equals("-u")) {
                userId = a.argument;
            }
            else if (a.option.equals("-p")) {
                password = a.argument;
            }
            else if (a.option.equals("-l")) {
                separator = a.argument;
            }
            else if (a.option.equals("-q")) {
                sql = a.argument;
            }
            else if (a.option.equals("-v")) {
                if (!(a.argument == null)) {
                    if (a.argument.equals("quiet")) {
                        isVerbose = false;
                    }
                }
                else {
                    isVerbose = true;
                }
            }
            else {
                System.err.println("Bad option " + a.option + " " + a.argument);
                usage();
                System.exit(-1);
            }
        }                                                        /* End for*/
        System.err.println("Done evaluating options.");
        System.err.println("Argument count is " + args.argumentCount());
        System.err.flush();
        
        //  if (args.argumentCount() > 0)
        //    {
        //    sql = args.nthArgument(0).argument;
        //    }
        
        System.err.println("SQL is " + sql);
        
    /* Validate our arguments */
        if ((null == server) || (null == sql)) {
            usage();
            System.exit(-1);
        }
        
        if (sessionType.equals("jt400"))
            sess = SQLSession.JT400;
        else if (sessionType.equals("jdbcOdbc"))
            sess = SQLSession.JDBCODBC;
        else if (sessionType.equals("postgres"))
            sess = SQLSession.POSTGRES;
        else {
            System.err.println("Unknown session type, defaulting to jdbcOdbc.");
            sess =  SQLSession.JDBCODBC;
        }
        
        try {
            System.err.println("Trying to create session.");
            session = new SQLSession(sess);
        }                                                        /* End try*/
        
        catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }                                                      /* End catch*/
        
        try {
            if (session != null) {
                session.setVerbose(isVerbose);
                if ((userId != null) && (password != null)) {
                    session.connect(server, userId, password);
                }
                else {
                    session.connect(server);
                }                                                 /* End if*/
                session.execute(sql);
                r  = session.getResultSet();
                if (r == null) {
                    System.err.println("The query \"" + sql + "\" did not return a result set.");
                }
                else {
                    rmd = session.getResultSetMetaData();
                    int columnCount = rmd.getColumnCount();
                    if (columnCount > 0) {
                        for (i = 1; i <= columnCount ; i++) {
                            String s = rmd.getColumnName(i);
                            String t = rmd.getTableName(i);
                            /* ResultSetMetaData.getColumnLabel() bombs on JdbcOdbc bridge via PComm*/
                            System.out.print(t + "." + s);
                            if (i < columnCount) {
                                System.out.print(separator);/* Don't print comma on last entry*/
                            }
                        }
                        System.out.println();                /* Finish line*/
                        
                        /* Now get the rows */
                        while (r.next()) {
                            for (i = 1; i <= columnCount; i++) {
                                String s = r.getString(i);
                                System.out.print(s);
                                if (i < columnCount) {
                                    System.out.print(separator);/* Don't print comma on last entry*/
                                }
                            }                                    /* End for*/
                            System.out.println();            /* Finish line*/
                        }
                    }                                             /* End if*/
                }
                session.disconnect();
            }
        }
        catch (Exception e) {
            try {
                if (session != null) {
                    session.disconnect();
                }                                                 /* End if*/
            }                                                    /* End try*/
            
            catch (Exception f) {
                f.printStackTrace(System.err);
            }                                                  /* End catch*/
            e.printStackTrace(System.err);
            System.exit(-1);
        }                                                      /* End catch*/
        System.out.println();
        System.out.println("---");
        System.out.println("Program exited normally.");
        System.exit(0);
    }
}                                                /* End of SQLSession class*/

