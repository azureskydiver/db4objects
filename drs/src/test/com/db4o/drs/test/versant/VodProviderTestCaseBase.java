/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.test.versant;

import java.util.*;

import javax.jdo.*;

import com.db4o.drs.versant.*;

public class VodProviderTestCaseBase extends VodDatabaseTestCaseBase {
	
	protected static final String DATABASE_NAME = "VodDatabaseTestCaseBase";
	
	protected VodReplicationProvider _provider;
	
	// This is a direct PersistenceManager that works around the _provider
	// so we can see what's committed, using a second reference system.
	protected PersistenceManager _pm;

	protected VodDatabase _vod;
	
	public void setUp() throws Exception {
		_vod = new VodDatabase(DATABASE_NAME);
		registerMetadataFiles(_vod);
		_pm = _vod.createPersistenceManager();
		cleanDb();
		_provider = new VodReplicationProvider(_vod);
	}

	public void tearDown() throws Exception {
		_pm.close();
		_provider.commit();
		_provider.destroy();
	}
	
	private void cleanDb(){
		_pm.currentTransaction().begin();
		Collection allObjects = (Collection) _pm.newQuery(Object.class).execute();
		for (Object object : allObjects) {
			_pm.deletePersistent(object);
		}
		_pm.currentTransaction().commit();
	}
	
	public static void classSetUp() throws Exception {
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.createDb();
		vod.amendPropertyIfNotExists("versant.metadata.0", "drs.jdo");
		vod.enhance("bin");
	}

	public static void classTearDown() {
		VodDatabase vod = new VodDatabase(DATABASE_NAME);
		vod.removeDb();
	}

}