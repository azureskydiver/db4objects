/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.test.replication.db4ounit;

import com.db4o.inside.replication.ReplicationProviderInside;

import db4ounit.db4o.Db4oFixture;

public interface DrsFixture extends Db4oFixture {

	ReplicationProviderInside provider();
}
