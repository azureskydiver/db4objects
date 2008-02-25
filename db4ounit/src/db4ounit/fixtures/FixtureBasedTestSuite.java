/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package db4ounit.fixtures;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * TODO: FixtureDecorator.getLabel should be something like (subject[0]) - (<fixture label>[index])
 * 
 * TODO: FixtureProviders must accept the index of a specific fixture to run with (to make it easy to reproduce a failure)
 * 
 * TODO: Iterator framework cleanup (composition should be based on Iterable4)
 * 
 * TODO: experiment with ParallelTestRunner that uses a thread pool to run tests in parallel
 * 
 * TODO: single sharpen run during the build (so sharpen.property annotation are always available)
 * 
 * TODO: ensure concurrency framework still working
 *
 */
public abstract class FixtureBasedTestSuite implements TestSuiteBuilder {

	public abstract Class[] testUnits();

	public abstract FixtureProvider[] fixtureProviders();

	public Iterator4 iterator() {
		final FixtureProvider[] providers = fixtureProviders();
		
		final Iterable4 decorators = Iterators.map(Iterators.iterable(providers), new Function4() {
			public Object apply(final Object arg) {
				final FixtureProvider provider = (FixtureProvider)arg;
				final ContextVariable variable = provider.variable();
				return Iterators.map(provider, new Function4() {
					public Object apply(final Object fixture) {
						return new FixtureDecorator(variable, fixture);
					}
				});
			}
		});
		final Iterable4 testsXdecorators = Iterators.crossProduct(new Iterable4[] {
			tests(),
			Iterators.crossProduct(decorators)
		});
		return Iterators.map(testsXdecorators, new Function4() {
			public Object apply(Object arg) {
				Iterator4 tuple = ((Iterable4)arg).iterator();
				Test test = (Test)Iterators.next(tuple);
				Iterable4 decorators = (Iterable4)Iterators.next(tuple);
				return decorate(test, decorators.iterator());
			}
		}).iterator();
	}

	private Iterable4 tests() {
		return new ReflectionTestSuiteBuilder(testUnits());
	}
	
	private Test decorate(Test test, Iterator4 decorators) {
		while (decorators.moveNext()) {
			test = ((TestDecorator)decorators.current()).decorate(test);
		}
		return test;
	}

}