/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.jdk14;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.replication.hibernate.ref_as_columns.RefAsColumnsReplicationProvider;
import com.db4o.test.Test;
import com.db4o.test.replication.SPCChild;
import com.db4o.test.replication.SPCParent;
import com.db4o.test.replication.hibernate.HibernateConfigurationFactory;
import com.db4o.test.replication.template.SimpleParentChild;
import org.hibernate.cfg.Configuration;

public class SimpleParentChildCombinations extends SimpleParentChild {
	public void test() {
		super.test();
	}

	protected void initproviderPairs() {
		TestableReplicationProviderInside a;
		TestableReplicationProviderInside b;

		a = new RefAsColumnsReplicationProvider(newCfg(), "HSQL RefAsColumns");
		b = new RefAsColumnsReplicationProvider(newCfg(), "HSQL RefAsColumns");
		addProviderPairs(a, b);

		final Db4oReplicationProvider db4o = new Db4oReplicationProvider(Test.objectContainer(), "db4o");
		addProviderPairs(a, db4o);
		addProviderPairs(db4o, a);
	}

	protected Configuration newCfg() {
		Configuration cfg;
		cfg = HibernateConfigurationFactory.createNewDbConfig();
		cfg.addClass(SPCParent.class);
		cfg.addClass(SPCChild.class);
		return cfg;
	}

}
