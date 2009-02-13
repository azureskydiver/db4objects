package com.db4o.db4ounit.jre12;

import com.db4o.db4ounit.jre12.foundation.*;
import com.db4o.db4ounit.jre12.reflect.*;

import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class AllTestsJdk1_2 extends Db4oTestSuite {

	public static void main(String[] args) {
		System.exit(new AllTestsJdk1_2().runAll());
    }

	protected Class[] testCases() {
		return new Class[] {
		    
			// FIXME: solve the workspacePath issue and uncomment migration.AllCommonTests.class below
//			com.db4o.db4ounit.common.migration.AllCommonTests.class,
		    
			com.db4o.db4ounit.jre12.defragment.DefragUnknownClassTestCase.class,
			com.db4o.db4ounit.common.defragment.LegacyDatabaseDefragTestCase.class,
			com.db4o.db4ounit.common.freespace.FreespaceManagerTypeChangeSlotCountTestCase.class,
			com.db4o.db4ounit.common.ta.AllTests.class,
			com.db4o.db4ounit.jre11.AllTests.class,
			com.db4o.db4ounit.jre12.assorted.AllTests.class,
			com.db4o.db4ounit.jre12.blobs.AllTests.class,
			com.db4o.db4ounit.jre12.defragment.AllTests.class,
			com.db4o.db4ounit.jre12.fieldindex.AllTests.class,
			com.db4o.db4ounit.jre12.handlers.AllTests.class,
			com.db4o.db4ounit.jre12.soda.AllTests.class,
			com.db4o.db4ounit.jre12.collections.AllTests.class,
			com.db4o.db4ounit.jre12.collections.facades.AllTests.class,
			com.db4o.db4ounit.jre12.collections.map.AllTests.class,
			com.db4o.db4ounit.jre12.collections.transparent.AllTests.class,
			com.db4o.db4ounit.jre12.querying.AllTests.class,
			com.db4o.db4ounit.jre12.regression.AllTests.class,
			com.db4o.db4ounit.jre12.ta.AllTests.class,
			com.db4o.db4ounit.jre12.ta.collections.AllTests.class,
			com.db4o.db4ounit.jre12.types.AllTests.class,
			StandaloneNativeReflectorTestCase.class,
			IterableBaseTestCase.class,
		};
	}
}
