package com.db4o.test.replication.hibernate.mysql;

import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;

public class MySQLReplicationFeaturesMain extends HibernateReplicationFeaturesMain {
	public MySQLReplicationFeaturesMain() {

	}

	public void test() {
		cfgA = HibernateConfigurationFactory.produceMySQLConfigA();
		cfgA.addClass(Replicated.class);
		pA = new HibernateReplicationProviderImpl(cfgA, "A");

		cfgB = HibernateConfigurationFactory.produceMySQLConfigB();
		cfgB.addClass(Replicated.class);
		pB = new HibernateReplicationProviderImpl(cfgB, "B");
		super.test();
	}
}
