/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.events;

import com.db4o.internal.*;

public class TransactionalEventArgs extends EventArgs {
	
	private final Transaction _transaction;

	public TransactionalEventArgs(Transaction transaction) {
		_transaction = transaction;
	}
	
	public Object transaction() {
		return _transaction;
	}
}
