package com.db4o.test.replication.hibernate.oracle;

import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.replication.Replicated;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.hibernate.HibernateReplicationFeaturesMain;

public class OracleReplicationFeaturesMain extends HibernateReplicationFeaturesMain {
	public OracleReplicationFeaturesMain() {

	}

	public void test() {
		cfgA = HibernateConfigurationFactory.produceOracleConfigA();
		cfgA.addClass(Replicated.class);
		pA = new RefAsColumnsReplicationProvider(cfgA, "A");

		cfgB = HibernateConfigurationFactory.produceOracleConfigB();
		cfgB.addClass(Replicated.class);
		pB = new RefAsColumnsReplicationProvider(cfgB, "B");
		super.test();
	}
}
