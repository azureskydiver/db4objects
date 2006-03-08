package com.db4o.replication.hibernate.ref_as_columns;

import com.db4o.replication.hibernate.ObjectConfig;
import com.db4o.replication.hibernate.UpdateEventListener;
import com.db4o.replication.hibernate.common.Common;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.event.EventListeners;
import org.hibernate.event.PostUpdateEvent;
import org.hibernate.event.PostUpdateEventListener;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class RefAsColumnsUpdateEventListener extends EmptyInterceptor
		implements Interceptor, UpdateEventListener {
	private static final UpdateEventListener instance = new RefAsColumnsUpdateEventListener();
	private static final Map threadSessionMap = new HashMap();
	private static final Map sessionConfigurationMap = new HashMap();

	RefAsColumnsUpdateEventListener() {
		//empty
	}

	public static void configure(Configuration cfg) {
		new RefAsColumnsTablesCreator(RefAsColumnsConfiguration.produce(cfg)).execute();
		cfg.setInterceptor(instance);
		EventListeners eventListeners = cfg.getEventListeners();
		eventListeners.setPostUpdateEventListeners(new PostUpdateEventListener[]{instance});
	}

	public static void install(Session session, Configuration cfg) {
		threadSessionMap.put(Thread.currentThread(), session);
		sessionConfigurationMap.put(session, cfg);
	}

	protected static void collectionUpdated(Object collection) {
		if (!(collection instanceof PersistentCollection))
			throw new RuntimeException(collection + " should always be PersistentCollection");

		PersistentCollection persistentCollection = ((PersistentCollection) collection);
		Object owner = persistentCollection.getOwner();

		if (Common.skip(owner)) return;

		Serializable id = getId(owner);
		ObjectUpdated(owner, id);
	}

	protected static Serializable getId(Object obj) {
		Session session = getSession();
		return session.getIdentifier(obj);
	}

	protected static Configuration getConfiguration() {
		return (Configuration) sessionConfigurationMap.get(getSession());
	}

	protected static Session getSession() {
		return (Session) threadSessionMap.get(Thread.currentThread());
	}

	protected static void ObjectUpdated(Object obj, Serializable id) {
		final Session session = getSession();
		if (session == null)
			throw new RuntimeException("Unable to update the version number of an object. Did you forget to call ReplicationConfigurator.install(session, cfg) after opening a session?");

		long newVersion = Common.getMaxVersion(session.connection()) + 1;
		Configuration cfg = getConfiguration();

		ObjectConfig objectConfig = new ObjectConfig(cfg);

		String tableName = objectConfig.getTableName(obj.getClass());
		String primaryKeyColumnName = objectConfig.getPrimaryKeyColumnName(obj);
		Connection connection = session.connection();
		Shared.incrementObjectVersion(connection, id, newVersion, tableName, primaryKeyColumnName);
	}

	public void onPostUpdate(PostUpdateEvent event) {
		Object object = event.getEntity();

		if (Common.skip(object)) return;

		ObjectUpdated(object, event.getId());
	}

	public void onCollectionRemove(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}

	public void onCollectionUpdate(Object collection, Serializable key) throws CallbackException {
		collectionUpdated(collection);
	}
}