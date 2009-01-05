/* Copyright (C) 2006 - 2007 db4objects Inc.  http://www.db4o.com */

package db4ounit.extensions.fixtures;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;
import db4ounit.extensions.util.*;


public class Db4oClientServer extends
		AbstractDb4oFixture implements Db4oClientServerFixture {
    
	protected static final String FILE = "Db4oClientServer.yap";
    
    public static final String HOST = "127.0.0.1";

    public static final String USERNAME = "db4o";

    public static final String PASSWORD = USERNAME;

    private ObjectServer _server;

    private final File _file;

	private boolean _embeddedClient;

	private ExtObjectContainer _objectContainer;
	
	private String _label;
    
    private int _port;

	private Configuration _serverConfig;
    
    
    public Db4oClientServer(ConfigurationSource configSource,String fileName, boolean embeddedClient, String label) {
    	super(configSource);
        _file = new File(fileName);
        _embeddedClient = embeddedClient;
        _label = label;
    }
    
    public Db4oClientServer(ConfigurationSource configSource, boolean embeddedClient, String label){
        this(configSource,filePath(), embeddedClient, label);
    }
    
    public void open(Class testCaseClass) throws Exception {
		openServer();
		final Configuration config = cloneConfiguration();
		applyFixtureConfiguration(testCaseClass, config);
		_objectContainer = _embeddedClient ? openEmbeddedClient().ext() : Db4o
				.openClient(config, HOST, _port, USERNAME, PASSWORD).ext();
	}

	public ExtObjectContainer openNewClient() {
		return _embeddedClient ? openEmbeddedClient().ext() : Db4o.openClient(
				cloneConfiguration(), HOST, _port,
				USERNAME, PASSWORD).ext();
	}

	private Config4Impl cloneConfiguration() {
	    return cloneDb4oConfiguration((Config4Impl) config());
    }

    private void openServer() throws Exception {
        _serverConfig = cloneConfiguration();
		_server = Db4o.openServer(_serverConfig,_file.getAbsolutePath(), -1);
        _port = _server.ext().port();
        _server.grantAccess(USERNAME, PASSWORD);
    }
    
    public void close() throws Exception {
		if (null != _objectContainer) {
			_objectContainer.close();
			_objectContainer = null;
		}
		closeServer();
	}

    private void closeServer() throws Exception {
    	if (null != _server) {
	        _server.close();
	        _server = null;
    	}
    }	

	public ExtObjectContainer db() {
		return _objectContainer;
	}
    
    protected void doClean() {
        _file.delete();
    }
    
    public ObjectServer server() {
    	return _server;
    }
    
    public boolean embeddedClients() {
    	return _embeddedClient;
    }
    
    /**
	 * Does not accept a clazz which is assignable from OptOutCS, or not
	 * assignable from Db4oTestCase.
	 * 
	 * @return returns false if the clazz is assignable from OptOutCS, or not
	 *         assignable from Db4oTestCase. Otherwise, returns true.
	 */
	public boolean accept(Class clazz) {
		if (!Db4oTestCase.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (OptOutCS.class.isAssignableFrom(clazz)) {
			return false;
		}
		if (!_embeddedClient && (OptOutNetworkingCS.class.isAssignableFrom(clazz))) {
			return false;
		}
		if (_embeddedClient && (OptOutAllButNetworkingCS.class.isAssignableFrom(clazz))) {
			return false;
		}
		return true;
	}
    
	public LocalObjectContainer fileSession() {
		return (LocalObjectContainer)_server.ext().objectContainer();
	}
	
	public void defragment() throws Exception {
		defragment(filePath());
	}
	
	private ObjectContainer openEmbeddedClient() {
		return _server.openClient(config());
	}
	
	private Config4Impl cloneDb4oConfiguration(Configuration config) {
		return (Config4Impl) ((Config4Impl)config).deepClone(this);
	}

	public String label() {
		return buildLabel(_label);
	}

	public int serverPort() {
		return _port;
	}

	private static String filePath() {
		return CrossPlatformServices.databasePath(FILE);
	}

	public void configureAtRuntime(RuntimeConfigureAction action) {
		action.apply(config());
		action.apply(_serverConfig);
	}
}
