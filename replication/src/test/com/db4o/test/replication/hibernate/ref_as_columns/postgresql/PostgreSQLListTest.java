package com.db4o.test.replication.hibernate.ref_as_columns.postgresql;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.hibernate.impl.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.ref_as_columns.hsql.ListTestHsqlRefAsColumns;
import org.hibernate.cfg.Configuration;

public class PostgreSQLListTest extends ListTestHsqlRefAsColumns {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.producePostgreSQLConfigA();
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.producePostgreSQLConfigB();
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return new RefAsColumnsReplicationProvider(configuration, "B");
	}

	public void test() {
		super.test();
	}

}
