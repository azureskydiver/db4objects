package com.db4o.test.replication.collections.map;

import com.db4o.replication.db4o.Db4oReplicationProvider;
import com.db4o.types.Db4oMap;

public class Db4oMapTest extends MapTest {

	protected void actualTest() {
		if (!(_providerA instanceof Db4oReplicationProvider))
			return;

		super.actualTest();
	}

	public void test() {
		super.test();
	}

	protected MapHolder createMapHolder() {
		MapHolder mh = new MapHolder("h1");
		final Db4oMap db4oMap = ((Db4oReplicationProvider) _providerA).getObjectContainer().collections().newHashMap(1);

		mh.setMap(db4oMap);
		return mh;
	}
}
