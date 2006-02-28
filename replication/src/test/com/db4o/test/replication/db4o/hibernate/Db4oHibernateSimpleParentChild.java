/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o.hibernate;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.HibernateReplicationProviderImpl;
import com.db4o.test.Test;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import com.db4o.test.replication.SimpleParentChild;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import org.hibernate.cfg.Configuration;


public class Db4oHibernateSimpleParentChild extends SimpleParentChild {

	protected TestableReplicationProviderInside prepareProviderA() {
		Configuration configuration = HibernateConfigurationFactory.createNewDbConfig();
		configuration.addClass(SPCParent.class);
		configuration.addClass(SPCChild.class);
		return new HibernateReplicationProviderImpl(configuration, "A");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new Db4oReplicationProvider(Test.objectContainer());
	}

	public void test() {
		super.test();
	}

}
