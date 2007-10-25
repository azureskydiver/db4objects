/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NTTestCase extends ItemTestCaseBase {
	
	public static void main(String[] args) {
		new NTTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		NTItem item = new NTItem();
		item.item = new TItem();
		item.item.value = 42;
		return item;
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		NTItem item = (NTItem) obj;
		Assert.isNotNull(item.item);
		Assert.areEqual(0, item.item.value);
	}
	
	protected void assertItemValue(Object obj) throws Exception {
		NTItem item = (NTItem) obj;
		Assert.areEqual(42, item.item.value());
	}

	

}
