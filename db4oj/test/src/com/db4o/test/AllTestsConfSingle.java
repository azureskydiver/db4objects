/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import com.db4o.reflect.generic.*;
import com.db4o.test.reflect.*;
import com.db4o.test.replication.*;

public class AllTestsConfSingle extends TestSuite{

	static protected final String TEST_CONFIGURATION = "AllTestsConfSingle";
	
    static TestSuite[] TEST_SUITES = new TestSuite[]{
        new ReplicationTestSuite(),
        // new AllTestsConfSingle(),
    };

    
    public Class[] tests(){
        return new Class[] {
            
//            QueryForList.class
            
//            SimplestPossible.class,
//            KnownClasses.class
            
        
            ReplicationFeatures.class,
            
            // Soda.class
           
            // CallConstructors.class
        
    	};
    }

    /**
      * the number of test runs 
      */
    public int RUNS = 1;

	/**
	 * delete the database files
	 */
	public boolean DELETE_FILE = true;

    /**
      * run the tests stand-alone 
      */
    public boolean SOLO = true;
    
    /**
      * run the tests in client/server mode 
      */
    public boolean CLIENT_SERVER = true;

    /**
     * run the test against a memory file instead of disc file
     */
    public static boolean MEMORY_FILE = false;

    /**
      * run the client/server test against a remote server. 
      * This requires AllTestsServer to be started on the other machine and 
      * SERVER_HOSTNAME to be set correctly.
      */
    final boolean REMOTE_SERVER = false;

    /**
     * the database file to be used for the server.
     */
    public static String FILE_SERVER = "xt_serv.yap";

    /**
     * the database file to be used stand-alone.
     */
    public static String FILE_SOLO = "xt_solo.yap";
    
    
    /**
     * the database file to be used for replication testing
     */
    public static String FILE_REPLICA = "xt_replica.yap";
    
    /**
     * the server host name.
     */
    public static String SERVER_HOSTNAME = "localhost";

    /**
     * the server port.
     */
    public static int SERVER_PORT = 4448;

    /**
     * the db4o user.
     */
    public static String DB4O_USER = "db4o";

    /**
     * the db4o password.
     */
    public static String DB4O_PASSWORD = "db4o";
    
	/**
	 * path to blobs held externally
	 */
    public static String BLOB_PATH = "TEMP/db4oTestBlobs";

}
