package com.db4o.drs.test;

import java.util.HashSet;
import java.util.Set;

import com.db4o.drs.test.hibernate.ReplicationConfiguratorTest;
import com.db4o.drs.test.hibernate.TablesCreatorTest;

import db4ounit.TestRunner;

public class Db4oTests extends DrsTestSuite {
	public static void main(String[] args) {
		new Db4oTests().rundb4oCS();
//		new Db4oTests().runCSdb4o();
//		new Db4oTests().runCSCS();
//		new Db4oTests().runDb4oDb4o();
	}

	public void runDb4oDb4o() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oDrsFixture("db4o-b"), getClass())).run();
	}

	public void runCSCS() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 0xdb40), new Db4oClientServerDrsFixture(
				"db4o-cs-b", 4455), getClass())).run();
	}

	public void rundb4oCS() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oDrsFixture("db4o-a"),
				new Db4oClientServerDrsFixture("db4o-cs-b", 4455), getClass()))
				.run();
	}

	public void runCSdb4o() {
		new TestRunner(new DrsTestSuiteBuilder(new Db4oClientServerDrsFixture(
				"db4o-cs-a", 4455), new Db4oDrsFixture("db4o-b"), getClass()))
				.run();
	}
	
	protected Class[] testCases() {
		return all();
	}
	
	private Class[] all() {
		Set<Class> out = new HashSet<Class>();
		
		out.add(EnumTest.class);
		
		for (Class c : shared())
			out.add(c);
		
		return out.toArray(new Class[]{});
	}

	protected Class[] one() {
		return new Class[] { MapTest.class, };
	}
}
