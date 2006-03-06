package com.db4o.test.replication.transients;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.test.replication.SingleTypeCollectionReplicationTest;

public class TransientSingleTypeCollectionReplicationTest extends SingleTypeCollectionReplicationTest {
	protected TestableReplicationProviderInside prepareProviderA() {
		return new TransientReplicationProvider(new byte[]{0});
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		return new TransientReplicationProvider(new byte[]{1});
	}

	public void testCollectionReplication() {
		super.testCollectionReplication();
	}
}
