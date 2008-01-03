/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.Db4oClientServerTestCase;

public class RollbackUpdateCascadeTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new RollbackUpdateCascadeTestCase().runClientServer();
	}
	
	protected void configure(Configuration config) {
		config.objectClass(Atom.class).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnDelete(true);
	}

	protected void store() {
		Atom atom = new Atom("root");
		atom.child = new Atom("child");
		atom.child.child = new Atom("child.child");
		store(atom);
	}

	public void test() {
		ExtObjectContainer oc1 = openNewClient();
		ExtObjectContainer oc2 = openNewClient();
		ExtObjectContainer oc3 = openNewClient();
		try {			
			Query query1 = oc1.query();
			query1.descend("name").constrain("root");
			ObjectSet os1 = query1.execute();
			Assert.areEqual(1, os1.size());
			Atom o1 = (Atom) os1.next();
			o1.child.child.name = "o1";
			oc1.store(o1);

			Query query2 = oc2.query();
			query2.descend("name").constrain("root");
			ObjectSet os2 = query2.execute();
			Assert.areEqual(1, os2.size());
			Atom o2 = (Atom) os2.next();
			Assert.areEqual("child.child", o2.child.child.name);

			oc1.rollback();
			oc2.purge(o2);
			os2 = query2.execute();
			Assert.areEqual(1, os2.size());
			o2 = (Atom) os2.next();
			Assert.areEqual("child.child", o2.child.child.name);

			oc1.store(o1);
			oc1.commit();
			
			os2 = query2.execute();
			Assert.areEqual(1, os2.size());
			o2 = (Atom) os2.next();
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("o1", o2.child.child.name);
			
			Query query3 = oc3.query();
			query3.descend("name").constrain("root");
			ObjectSet os3 = query1.execute();
			Assert.areEqual(1, os3.size());
			Atom o3 = (Atom) os3.next();
			Assert.areEqual("o1", o3.child.child.name);
			
		} finally {
			oc1.close();
			oc2.close();
			oc3.close(); 
		}
	}
}
