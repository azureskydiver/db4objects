package com.db4o.test.replication;

import com.db4o.test.*;

public class ReplicationTestSuite extends TestSuite{

	public Class[] tests() {
		return new Class[] {
// crashes            // ReplicateDb4oList.class,
			ReplicationFeaturesMain.class,
            R0to4Runner.class,
			ReplicationFeatures.class
		};
	}
	
	

}
