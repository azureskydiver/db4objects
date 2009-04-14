/* Copyright (C) 2009  db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.instrumentation.test.data;

import java.util.*;

public class HashSetClient implements CollectionClient {

	private Set _set;
	
	public HashSetClient() {
		_set = new HashSet();
	}

	public Object collectionInstance() {
		return _set;
	}
}
