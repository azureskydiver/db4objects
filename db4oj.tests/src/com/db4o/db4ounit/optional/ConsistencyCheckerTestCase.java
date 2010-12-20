/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.db4ounit.optional;

import com.db4o.*;
import com.db4o.consistency.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

public class ConsistencyCheckerTestCase extends TestWithTempFile {

	public static class Item {
	}
	
	public void testFreeUsedSlot() {
		assertInconsistencyDetected(new Procedure4<Pair<LocalObjectContainer,Item>>() {
			public void apply(Pair<LocalObjectContainer, Item> state) {
				Item item = state.second;
				LocalObjectContainer db = state.first;
				int id = (int) db.getID(item);
				Slot slot = db.idSystem().committedSlot(id);
				db.freespaceManager().free(slot);
			}
		});
	}

	public void testFreeShiftedUsedSlot() {
		assertInconsistencyDetected(new Procedure4<Pair<LocalObjectContainer,Item>>() {
			public void apply(Pair<LocalObjectContainer, Item> state) {
				Item item = state.second;
				LocalObjectContainer db = state.first;
				int id = (int) db.getID(item);
				Slot slot = db.idSystem().committedSlot(id);
				db.freespaceManager().free(new Slot(slot.address() + 1, slot.length()));
			}
		});
	}

	private void assertInconsistencyDetected(Procedure4<Pair<LocalObjectContainer, Item>> proc) {
		LocalObjectContainer db = (LocalObjectContainer) Db4oEmbedded.openFile(tempFile());
		try {
			Item item = new Item();
			db.store(item);
			db.commit();
			Assert.isTrue(new ConsistencyChecker(db).checkSlotConsistency().consistent());
			proc.apply(new Pair<LocalObjectContainer, Item>(db, item));
			db.commit();
			Assert.isFalse(new ConsistencyChecker(db).checkSlotConsistency().consistent());
		}
		finally {
			db.close();			
		}
	}

}