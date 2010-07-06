package com.db4o.db4ounit.common;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class SampleClassLevelTestSuite extends FixtureBasedTestSuite implements ClassLevelFixtureTest, Db4oTestCase {

	public static class SampleClassLevelTestUnit extends AbstractDb4oTestCase implements ClassLevelFixtureTest {
		public static void classSetUp() {
			System.out.println("unit setup");
		}
	
		public static void classTearDown() {
			System.out.println("unit teardown");
		}
		
		public void test() {
			System.out.println("unit test: " + db()+ ", " + FIXTURE.value());
		}
	}

	public static void classSetUp() {
		System.out.println("suite setup");
	}

	public static void classTearDown() {
		System.out.println("suite teardown");
	}

	private static FixtureVariable<Name> FIXTURE = FixtureVariable.newInstance("name");
	
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			new SimpleFixtureProvider<Name>(FIXTURE, new Name("foo"), new Name("bar")),	
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
			SampleClassLevelTestUnit.class,	
		};
	}
	
	public static class Name implements Labeled {
		private String _name;

		public Name(String name) {
			_name = name;
		}
		
		public String label() {
			return _name;
		}
		
		@Override
		public String toString() {
			return _name;
		}
	}
}
