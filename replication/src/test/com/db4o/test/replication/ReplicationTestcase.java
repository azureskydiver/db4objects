/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.ObjectSet;
import com.db4o.foundation.Collection4;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.Replication;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;


public abstract class ReplicationTestcase {

	private long _timer;

	protected void printCombination(ProviderPair p) {
		String claxx = this.getClass().getName();
		String pa = p._providerA.getName();
		String pb = p._providerB.getName();

		String out = "Test = " + claxx + ", provider A = " + pa + ", provider B = " + pb;

		System.out.println(out);
	}

	protected TestableReplicationProviderInside _providerA;
	protected TestableReplicationProviderInside _providerB;

	protected Collection4 providerPairs = new Collection4();

	protected ReplicationTestcase() {
		initproviderPairs();
	}

	protected void addProviderPairs(TestableReplicationProviderInside a, TestableReplicationProviderInside b) {
		providerPairs.add(new ProviderPair(a, b));
	}

	//TODO make abstract
	protected void initproviderPairs() {

	}

	protected TestableReplicationProviderInside prepareProviderA() {
		throw new UnsupportedOperationException("unreachable");
	}

	protected TestableReplicationProviderInside prepareProviderB() {
		throw new UnsupportedOperationException("unreachable me");
	}

	protected void init() {
		_providerA = prepareProviderA();
		_providerB = prepareProviderB();
	}

	protected void destroy() {
		_providerA.closeIfOpened();
		_providerB.closeIfOpened();
	}

	protected void delete(Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			_providerA.delete(classes[i]);
			_providerB.delete(classes[i]);
		}
		_providerA.commit();
		_providerB.commit();
	}

	protected void replicateAll(TestableReplicationProviderInside providerFrom, TestableReplicationProviderInside providerTo) {
		//System.out.println("from = " + providerFrom + ", to = " + providerTo);
		ReplicationSession replication = Replication.begin(providerFrom, providerTo);
		ObjectSet allObjects = providerFrom.objectsChangedSinceLastReplication();

		if (!allObjects.hasNext())
			throw new RuntimeException("Can't find any objects to replicate");

		while (allObjects.hasNext()) {
			Object changed = allObjects.next();
			//System.out.println("changed = " + changed);
			replication.replicate(changed);
		}
		replication.commit();
	}

	protected void ensureOneInstance(TestableReplicationProviderInside provider, Class clazz) {
		ensureInstanceCount(provider, clazz, 1);
	}

	protected void ensureInstanceCount(TestableReplicationProviderInside provider, Class clazz, int count) {
		ObjectSet objectSet = provider.getStoredObjects(clazz);
		while (objectSet.hasNext()) {
			count --;
			objectSet.next();
		}
		if (count != 0) {
			int xxx = 1;
		}
		Test.ensure(count == 0);
	}

	protected void replicateClass(TestableReplicationProviderInside providerA, TestableReplicationProviderInside providerB, Class clazz) {
		//System.out.println("ReplicationTestcase.replicateClass");
		ReplicationSession replication = Replication.begin(providerA, providerB);
		ObjectSet allObjects = providerA.objectsChangedSinceLastReplication(clazz);
		while (allObjects.hasNext()) {
			final Object obj = allObjects.next();
			//System.out.println("obj = " + obj);
			replication.replicate(obj);
		}
		replication.commit();
	}

	protected Object getOneInstance(TestableReplicationProviderInside provider, Class clazz) {
		ObjectSet objectSet = provider.getStoredObjects(clazz);

		if (!objectSet.hasNext())
			throw new RuntimeException("object not found");

		return objectSet.next();
	}

	protected void startTimer() {
		_timer = System.currentTimeMillis();
	}

	protected void logTime(String msg) {
		long time = System.currentTimeMillis();
		long duration = time - _timer;
		System.out.println(msg + " " + duration + "ms");
		_timer = System.currentTimeMillis();
	}


}
