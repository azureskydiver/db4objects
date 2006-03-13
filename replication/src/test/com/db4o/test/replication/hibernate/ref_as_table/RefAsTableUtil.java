package com.db4o.test.replication.hibernate.ref_as_table;

import com.db4o.replication.hibernate.HibernateReplicationProvider;
import com.db4o.replication.hibernate.impl.ref_as_table.RefAsTableReplicationProvider;
import org.hibernate.cfg.Configuration;

public class RefAsTableUtil {
	public static HibernateReplicationProvider newProvider(Configuration cfg, String name) {
		return new RefAsTableReplicationProvider(cfg, name);
	}
}
