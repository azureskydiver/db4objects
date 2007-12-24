/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */
package com.db4odoc.ssl;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;

public class SSLSocketsExample {

	private static String HOST = "localhost";
	private static String FILE = "reference.db4o";
	private static int PORT = 0xdb40;
	private static String USER = "db4o";
	private static String PASSWORD = "db4o";
	
	private static final String KEYSTORE_ID = "jks";
	private static final String KEYSTORE_PATH = "SSLCert";
	private static final String KEYSTORE_PASSWORD = "password";

	private static SecureSocketFactory socketFactory;

	
	public static void main(String[] args) throws Exception {
		
	    // Create a SecureSocketFactory for the SSL context
		socketFactory = createSecureSocketFactory();
		
		Configuration config = Db4o.newConfiguration();
		ObjectServer db4oServer = Db4o.openServer(config, FILE, PORT,
				socketFactory);
		db4oServer.grantAccess(USER, PASSWORD);
		try {
			storeObjectsRemotely(HOST, PORT, USER, PASSWORD);
			queryRemoteServer(HOST, PORT, USER, PASSWORD);
		} finally {
			db4oServer.close();
		}
	}
	// end main
	
	private static SecureSocketFactory createSecureSocketFactory() throws Exception{
		SSLContext sc;
		
		//Create a trust manager that does not validate certificate chains
	    TrustManager[] trustAllCerts = createTrustManager();
	    
	    // Install the all-trusting trust manager
		sc = SSLContext.getInstance("SSLv3");
		KeyStore ks = KeyStore.getInstance(KEYSTORE_ID);
	    ks.load(new FileInputStream(KEYSTORE_PATH), null);
	    KeyManagerFactory kmf = KeyManagerFactory.getInstance( KeyManagerFactory.getDefaultAlgorithm() );
	    kmf.init( ks, KEYSTORE_PASSWORD.toCharArray());
	    
	    sc.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
	    return new SecureSocketFactory(sc);
	}
	// end createSecureSocketFactory
	
	private static TrustManager[] createTrustManager(){
		return new TrustManager[]{
		        new X509TrustManager() {
		            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		                return null;
		            }
		            public void checkClientTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		            public void checkServerTrusted(
		                java.security.cert.X509Certificate[] certs, String authType) {
		            }
		        }
		    };
	}
	// end createTrustManager
	
	
	private static void storeObjectsRemotely(String host, int port,String user,String password) throws IOException {
		Configuration config = Db4o.newConfiguration();
        ObjectContainer client=Db4o.openClient(config, "localhost",port,user,password, socketFactory);
        Pilot pilot = new Pilot("Fernando Alonso", 89);
        client.set(pilot);
        client.close();
    }
    // end storeObjectsRemotely
	
	private static void queryRemoteServer(String host, int port,String user,String password) throws IOException {
		Configuration config = Db4o.newConfiguration();
        ObjectContainer client=Db4o.openClient(config, "localhost",port,user,password, socketFactory);
        listResult(client.get(new Pilot(null, 0)));
        client.close();
    }
    // end queryRemoteServer

	
    
    private static void listResult(ObjectSet result) {
        System.out.println(result.size());
        while(result.hasNext()) {
            System.out.println(result.next());
        }
    }
    // end listResult
    
    
}
