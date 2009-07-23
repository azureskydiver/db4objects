/* Copyright (C) 2009  Versant Inc.   http://www.db4o.com */
package com.db4o.db4ounit.common.exceptions.propagation;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.exceptions.*;
import com.db4o.io.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class ExceptionDuringTopLevelCallTestSuite extends FixtureBasedTestSuite implements Db4oTestCase, OptOutNetworkingCS {

	public static class ExceptionDuringTopLevelCallTestUnit extends AbstractDb4oTestCase {
	
		private ExceptionSimulatingStorage _storage;
		private Object _unactivated;
		
		public static class Item {
			public String _name;
		}
		
		@Override
		protected void configure(Configuration config) throws Exception {
			final ExceptionPropagationFixture propagationFixture = curPropagationFixture();
			_storage = new ExceptionSimulatingStorage(config.storage(), new ExceptionFactory() {
				private boolean _alreadyCalled = false;
				
				public void throwException() {
					try {
						if(!_alreadyCalled) {
							propagationFixture.throwInitialException();
						}
						else {
							propagationFixture.throwShutdownException();
						}
					}
					finally {
						_alreadyCalled = true;
					}
				}

				public void throwOnClose() {
					propagationFixture.throwCloseException();
				}
			});
			config.storage(_storage);
		}
		
		@Override
		protected void db4oSetupAfterStore() throws Exception {
			store(new Item());
		}
		
		public void testExceptionDuringTopLevelCall() {
			_unactivated = retrieveOnlyInstance(Item.class);
			db().deactivate(_unactivated);
			_storage.triggerException(true);
			curPropagationFixture().assertExecute(new DatabaseContext(db(), _unactivated), curTopLevelFixture());
		}

		private ExceptionPropagationFixture curPropagationFixture() {
			return PROPAGATION_FIXTURE.value();
		}

		private TopLevelOperation curTopLevelFixture() {
			return TOPLEVEL_FIXTURE.value();
		}

		@Override
		protected void db4oTearDownBeforeClean() throws Exception {
			_storage.triggerException(false);
		}
	}

	private static FixtureVariable<ExceptionPropagationFixture> PROPAGATION_FIXTURE = FixtureVariable.<ExceptionPropagationFixture>newInstance("exc");
	private static FixtureVariable<TopLevelOperation> TOPLEVEL_FIXTURE = FixtureVariable.<TopLevelOperation>newInstance("op");
	
	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			new SimpleFixtureProvider(PROPAGATION_FIXTURE,
					new OutOfMemoryErrorPropagationFixture(),
					new OneTimeDb4oExceptionPropagationFixture(),
					new OneTimeRuntimeExceptionPropagationFixture(),
					new RecurringDb4oExceptionPropagationFixture(),
					new RecurringRuntimeExceptionPropagationFixture(),
					new RecoverableExceptionPropagationFixture()
			),
			new SimpleFixtureProvider(TOPLEVEL_FIXTURE,
					new TopLevelOperation("commit") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.commit();
						}
					},
					new TopLevelOperation("store") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.store(new Item());
						}
					},
					new TopLevelOperation("activate") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.activate(context._unactivated, Integer.MAX_VALUE);
						}
					},
					// - no deactivate test, since it doesn't trigger I/O activity
					// - no getByID test, not refactored to asTopLevelCall, since it has custom, more relaxed exception handling
					// FIXME doesn't trigger initial exception - deletes are processed in finally block
//					new TopLevelOperation("delete") {
//						@Override
//						public void apply(DatabaseContext context) {
//							context._db.delete(context._unactivated);
//						}
//					},
					new TopLevelOperation("peek") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.ext().peekPersisted(context._unactivated, 1, true);
						}
					},
					new TopLevelOperation("qbe") {
						@Override
						public void apply(DatabaseContext context) {
							context._db.queryByExample(new Item());
						}
					},
					new TopLevelOperation("query") {
						@Override
						public void apply(DatabaseContext context) {
							ObjectSet<Object> result = context._db.query().execute();
							if(result.hasNext()) {
								result.next();
							}
						}
					}
			),
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
			ExceptionDuringTopLevelCallTestUnit.class,
		};
	}
}
