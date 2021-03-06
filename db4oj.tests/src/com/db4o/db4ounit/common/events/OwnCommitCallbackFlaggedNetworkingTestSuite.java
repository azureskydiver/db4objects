/* Copyright (C) 2010  Versant Inc.   http://www.db4o.com */
/**
 * @sharpen.if !SILVERLIGHT
 */
package com.db4o.db4ounit.common.events;

import db4ounit.fixtures.*;

public class OwnCommitCallbackFlaggedNetworkingTestSuite extends FixtureBasedTestSuite {

	public static class Item {
		public int _id;
		
		public Item(int id) {
			_id = id;
		}
	}

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new SimpleFixtureProvider<OwnCommittedCallbacksFixture.ContainerFactory>(OwnCommittedCallbacksFixture.FACTORY, 
					new OwnCommittedCallbacksFixture.NetworkingCSContainerFactory()
			),
			new SimpleFixtureProvider<OwnCommittedCallbacksFixture.CommitAction>(OwnCommittedCallbacksFixture.ACTION,
					new OwnCommittedCallbacksFixture.ClientACommitAction(),
					new OwnCommittedCallbacksFixture.ClientBCommitAction()
			),
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
				OwnCommittedCallbacksFixture.OwnCommitCallbackFlaggedTestUnit.class,
		};
	}

}
