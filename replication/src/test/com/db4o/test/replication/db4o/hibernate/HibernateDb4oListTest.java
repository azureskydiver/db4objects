package com.db4o.test.replication.db4o.hibernate;

import com.db4o.inside.replication.TestableReplicationProvider;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.Test;
import com.db4o.test.replication.collections.ListContent;
import com.db4o.test.replication.collections.ListHolder;
import com.db4o.test.replication.collections.ListTest;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;

public class HibernateDb4oListTest extends ListTest {

	protected TestableReplicationProvider prepareProviderA() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	protected TestableReplicationProvider prepareProviderB() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(ListHolder.class);
		configuration.addClass(ListContent.class);
		return new HibernateReplicationProviderImpl(configuration, "A", new byte[]{1});
	}

	public void test() {
		super.test();
	}

}
