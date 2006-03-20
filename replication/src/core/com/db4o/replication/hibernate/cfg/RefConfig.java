package com.db4o.replication.hibernate.cfg;

import com.db4o.replication.hibernate.impl.Util;
import com.db4o.replication.hibernate.metadata.DeletedObject;
import com.db4o.replication.hibernate.metadata.ReplicationComponentField;
import com.db4o.replication.hibernate.metadata.ReplicationComponentIdentity;
import com.db4o.replication.hibernate.metadata.ReplicationProviderSignature;
import com.db4o.replication.hibernate.metadata.ReplicationRecord;
import com.db4o.replication.hibernate.metadata.UuidLongPartSequence;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;

public class RefConfig {
// ------------------------------ FIELDS ------------------------------

	protected Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	protected Dialect dialect;

	public Dialect getDialect() {
		return dialect;
	}

	protected void addClasses() {
		Util.addClass(configuration, ReplicationProviderSignature.class);
		Util.addClass(configuration, ReplicationRecord.class);
		Util.addClass(configuration, ReplicationComponentIdentity.class);
		Util.addClass(configuration, ReplicationComponentField.class);
		Util.addClass(configuration, UuidLongPartSequence.class);
		Util.addClass(configuration, DeletedObject.class);
	}

	protected void init() {
		configuration.setProperty("hibernate.format_sql", "true");
		configuration.setProperty("hibernate.use_sql_comments", "true");
		configuration.setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
		configuration.setProperty("hibernate.cache.use_query_cache", "false");
		configuration.setProperty("hibernate.cache.use_second_level_cache", "false");
		configuration.setProperty("hibernate.cglib.use_reflection_optimizer", "true");
		configuration.setProperty("hibernate.connection.release_mode", "after_transaction");

		addClasses();
		dialect = Dialect.getDialect(configuration.getProperties());
	}

	public String getType(int sqlType) {
		return dialect.getTypeName(sqlType);
	}
}
