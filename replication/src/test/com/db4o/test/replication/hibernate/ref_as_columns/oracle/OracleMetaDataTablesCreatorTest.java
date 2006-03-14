package com.db4o.test.replication.hibernate.ref_as_columns.oracle;

import com.db4o.test.replication.hibernate.HibernateUtil;
import com.db4o.test.replication.hibernate.ref_as_columns.RefAsColumnsMetaDataTablesCreatorTest;
import org.hibernate.cfg.Configuration;

public class OracleMetaDataTablesCreatorTest extends RefAsColumnsMetaDataTablesCreatorTest {
	public void test() {
		super.test();
	}

	protected Configuration createCfg() {
		return HibernateUtil.produceOracleConfigA();
	}

	protected Configuration validateCfg() {
		return new Configuration().configure("com/db4o/test/replication/hibernate/oracle/hibernate-Oracle-validate.cfg.xml");
	}
}
