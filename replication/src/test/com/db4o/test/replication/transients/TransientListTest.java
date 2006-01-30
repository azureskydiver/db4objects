/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication.transients;

import com.db4o.inside.replication.*;
import com.db4o.test.replication.collections.*;


public class TransientListTest extends ListTest{
    
    protected TestableReplicationProvider prepareProviderA() {
        return new TransientReplicationProvider(new byte[]{0});
    }

    protected TestableReplicationProvider prepareProviderB() {
        return new TransientReplicationProvider(new byte[]{1});
    }

    public void test() {
        super.test();
    }
    
}
