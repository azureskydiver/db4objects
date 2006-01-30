/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.db4o;

import com.db4o.test.*;
import com.db4o.inside.replication.*;
import com.db4o.replication.db4o.*;
import com.db4o.test.replication.*;

public class Db4oR0to4Runner extends R0to4Runner {
    
    protected TestableReplicationProvider prepareProviderA() {
        return new Db4oReplicationProvider(Test.objectContainer());
    }

    protected TestableReplicationProvider prepareProviderB() {
        return Db4oReplicationTestUtil.providerB();
    }

    public void test() {
        super.test();
    }
}
