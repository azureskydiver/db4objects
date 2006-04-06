/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.ext.Db4oUUID;
import com.db4o.inside.replication.GenericReplicationSession;
import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.ObjectState;
import com.db4o.replication.ReplicationEvent;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.replication.ReplicationProvider;
import com.db4o.replication.ReplicationSession;
import com.db4o.test.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ReplicationFeaturesMain extends ReplicationTestCase {

	private final Set _setA = new HashSet(1);
	private final Set _setB = new HashSet(1);
	private final Set _setBoth = new HashSet(2);
	private final Set _NONE = Collections.EMPTY_SET;

	private Set _direction;
	private Set _containersToQueryFrom;
	private Set _containersWithNewObjects;
	private Set _containersWithChangedObjects;
	private Set _containersWithDeletedObjects;
	private Set _objectsToPrevailInConflicts;

	private String _intermittentErrors = "";
	private int _testCombination;
	private static final String A = "A";
	private static final String B = "B";

	private int round = 0;

	public void configure() {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
	}

	protected void actualTest() {
		_setA.add(A);
		_setB.add(B);

		_setBoth.addAll(_setA);
		_setBoth.addAll(_setB);

        _testCombination = 0;
        
		tstWithDeletedObjectsIn(_NONE);
		tstWithDeletedObjectsIn(_setA);
		tstWithDeletedObjectsIn(_setB);
		tstWithDeletedObjectsIn(_setBoth);

		if (_intermittentErrors.length() > 0) {
			System.err.println("Intermittent errors found in test combinations:" + _intermittentErrors);
			Test.ensure(false);
		}

		System.out.println("=========================== TODO:");
		System.out.println("Peek for conflict");
		System.out.println("Run test on JDK1.");
	}

	protected void clean() {
		delete(new Class[]{Replicated.class});
	}

	private void tstWithDeletedObjectsIn(Set containers) {
		_containersWithDeletedObjects = containers;

		tstDirection(_setA);
		tstDirection(_setB);
		tstDirection(_setBoth);
	}

	private void tstDirection(Set direction) {
		_direction = direction;

		tstQueryingFrom(_setA);
		tstQueryingFrom(_setB);
		tstQueryingFrom(_setBoth);
	}

	private void tstQueryingFrom(Set containersToQueryFrom) {
		_containersToQueryFrom = containersToQueryFrom;

		tstWithNewObjectsIn(_NONE);
		tstWithNewObjectsIn(_setA);
		tstWithNewObjectsIn(_setB);
		tstWithNewObjectsIn(_setBoth);
	}

	private void tstWithNewObjectsIn(Set containersWithNewObjects) {
		_containersWithNewObjects = containersWithNewObjects;

		tstWithChangedObjectsIn(_NONE);
		tstWithChangedObjectsIn(_setA);
		tstWithChangedObjectsIn(_setB);
		tstWithChangedObjectsIn(_setBoth);
	}

	private void tstWithChangedObjectsIn(Set containers) {
		_containersWithChangedObjects = containers;

		tstWithObjectsPrevailingConflicts(_NONE);
		tstWithObjectsPrevailingConflicts(_setA);
		tstWithObjectsPrevailingConflicts(_setB);
	}


	private void tstWithObjectsPrevailingConflicts(Set containers) {
		_objectsToPrevailInConflicts = containers;

		runCurrentCombination();
	}


	private void runCurrentCombination() {
		_testCombination++;
		System.out.println("" + _testCombination + " =================================");
		//printCombination();

		if (_testCombination < 537)  //Use this to skip some combinations and avoid waiting.
			return;

		int _errors = 0;
		while (true) {
			try {
				doIt();
				break;
			} catch (RuntimeException rx) {
				_errors++;
//                if (_errors == 10) {
				if (_errors == 1) {
					sleep(100);
					printCombination();
					throw rx;
				}
			}
		}
		if (_errors > 0)
			_intermittentErrors += "\n\t Combination: " + _testCombination + " (" + _errors + " errors)";
	}

	static private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void printCombination() {
		System.out.println("" + _testCombination + " =================================");
		System.out.println("New Objects In: " + print(_containersWithNewObjects));
		System.out.println("Changed Objects In: " + print(_containersWithChangedObjects));
		System.out.println("Deleted Objects In: " + print(_containersWithDeletedObjects));
		System.out.println("Querying From: " + print(_containersToQueryFrom));
		System.out.println("Direction: To " + print(_direction));
		System.out.println("Prevailing Conflicts: " + print(_objectsToPrevailInConflicts));
	}

	private void doIt() {
		initState();
		//reopenContainers();

		performChanges();

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB, new ReplicationEventListener() {

			public void onReplicate(ReplicationEvent event) {
				if (!event.isConflict()) return;

				if (_objectsToPrevailInConflicts.isEmpty()) {
					event.overrideWith(null);
					return;
				}

				ObjectState override = _objectsToPrevailInConflicts.contains(A)
						? event.stateInProviderA()
						: event.stateInProviderB();
				event.overrideWith(override);
			}

		});

		if (_direction.size() == 1) {
			if (_direction.contains(A)) {
				replication.setDirection(_providerB, _providerA);
			}
			if (_direction.contains(B)) {
				replication.setDirection(_providerA, _providerB);
			}
		}


		if (_containersToQueryFrom.contains(A)) {
			replicateQueryingFrom(replication, _providerA, _providerB);
		}
		if (_containersToQueryFrom.contains(B)) {
			replicateQueryingFrom(replication, _providerB, _providerA);
		}

		replication.commit();

		checkNames();

		clean();
	}


	private void checkNames() {
		checkNames(A, A);
		checkNames(A, B);
		checkNames(B, A);
		checkNames(B, B);
	}

	private void checkNames(String origin, String inspected) {
		checkName(container(inspected), "oldFrom" + origin, isOldNameExpected(inspected));
		checkName(container(inspected), "newFrom" + origin, isNewNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromAChangedIn" + origin, isChangedNameExpected(origin, inspected));
		checkName(container(inspected), "oldFromBChangedIn" + origin, isChangedNameExpected(origin, inspected));
	}

	private TestableReplicationProviderInside container(String aOrB) {
		return aOrB.equals(A) ? _providerA : _providerB;
	}

	private boolean isOldNameExpected(String inspectedContainer) {
		if (isDeletionExpected(inspectedContainer)) return false;
		if (isChangedNameExpected(A, inspectedContainer)) return false;
		if (isChangedNameExpected(B, inspectedContainer)) return false;
		return true;
	}

	private boolean isDeletionExpected(String inspectedContainer) {
		String other = other(inspectedContainer);
		if (prevailedInReplication(other))
			return isDeletionExpected(other);

		return (hasDeletions(inspectedContainer));
	}


	private boolean isNewNameExpected(String origin, String inspected) {
		if (!_containersWithNewObjects.contains(origin)) return false;
		if (origin.equals(inspected)) return true;
		if (!_containersToQueryFrom.contains(origin)) return false;
		if (!_direction.contains(inspected)) return false;
		return true;
	}

	private boolean isChangedNameExpected(String changedContainer, String inspectedContainer) {
		if (isDeletionExpected(inspectedContainer)) return false;

		String other = other(inspectedContainer);
		if (prevailedInReplication(other))
			return isChangedNameExpected(changedContainer, other);

		if (!inspectedContainer.equals(changedContainer)) return false;
		if (!hasChanges(inspectedContainer)) return false;

		return true;
	}

	private boolean prevailedInReplication(String container) {
		if (!wasReplicationTriggered()) return false;
		if (!_direction.contains(other(container))) return false;

		if (wasConflict())
			return _objectsToPrevailInConflicts.contains(container);

		return modifiedContainers().contains(container);
	}

	private boolean wasConflict() {
		if (!wasReplicationTriggered()) return false;
		return modifiedContainers().containsAll(_direction);
	}

	private Set modifiedContainers() {
		Set result = new HashSet();
		result.addAll(_containersWithChangedObjects);
		result.addAll(_containersWithDeletedObjects);
		return result;
	}

	private boolean wasReplicationTriggered() {
		Set triggers = modifiedContainers();
		triggers.retainAll(_containersToQueryFrom);
		return !triggers.isEmpty();
	}

	private boolean hasChanges(String container) {
		return _containersWithChangedObjects.contains(container);
	}

	private boolean hasDeletions(String container) {
		return _containersWithDeletedObjects.contains(container);
	}

	private String other(String aOrB) {
		return aOrB.equals(A) ? B : A;
	}

	private void performChanges() {

		if (_containersWithNewObjects.contains(A)) {
			_providerA.storeNew(new Replicated("newFromA"));
		}
		if (_containersWithNewObjects.contains(B)) {
			_providerB.storeNew(new Replicated("newFromB"));
		}

		if (hasDeletions(A)) {
			deleteObject(_providerA, "oldFromA");
			deleteObject(_providerA, "oldFromB");
		}
		if (hasDeletions(B)) {
			deleteObject(_providerB, "oldFromA");
			deleteObject(_providerB, "oldFromB");
		}

		if (hasChanges(A)) {
			changeObject(_providerA, "oldFromA", "oldFromAChangedInA");
			changeObject(_providerA, "oldFromB", "oldFromBChangedInA");
		}
		if (hasChanges(B)) {
			changeObject(_providerB, "oldFromA", "oldFromAChangedInB");
			changeObject(_providerB, "oldFromB", "oldFromBChangedInB");
		}

		_providerA.commit();
		_providerB.commit();
	}

	private void changeObject(TestableReplicationProviderInside container, String name, String newName) {
		Replicated obj = find(container, name);
		if (obj == null) return;
		obj.setName(newName);
		container.update(obj);
	}

	private void deleteObject(TestableReplicationProviderInside container, String name) {
		Replicated obj = find(container, name);
		container.delete(obj);
	}

	private void checkName(TestableReplicationProviderInside container, String name, boolean isExpected) {
		//System.out.println("");
		//System.out.println(name + (isExpected ? " " : " NOT") + " expected in container " + containerName(container));
		Replicated obj = find(container, name);
		if (isExpected) {
			ensure(obj != null);
		} else {
			ensure(obj == null);
		}
	}


	private String containerName(ReplicationProvider container) {
		if (container == _providerA) return A;
		if (container == _providerB) return B;
		throw new IllegalStateException();
	}

	private Replicated find(TestableReplicationProviderInside container, String name) {
		//System.out.println("container = " + container);
		//System.out.println("name = " + name);

		ObjectSet storedObjects = container.getStoredObjects(Replicated.class);

		int resultCount = 0;
		Replicated result = null;
		while (storedObjects.hasNext()) {
			Replicated replicated = (Replicated) storedObjects.next();
//			System.out.println("replicated = " + replicated);
			if (replicated == null)
				System.out.println("??????????????????????????????");
			if (name.equals(replicated.getName())) {
				result = replicated;
				resultCount++;
			}
		}

		if (resultCount > 1)
			fail("At most one object with name " + name + " was expected.");
		return result;

//		Query q = container.query();
//		q.constrain(Replicated.class);
//		q.descend("_name").constrain(name);
//		ObjectSet set = q.execute();
//		if (set.size() > 1) fail("At most one object with name " + name + " was expected.");
//		return (Replicated) set.next();
	}

	private static void fail(String string) {
		System.err.println(string);
		throw new RuntimeException(string);
	}

	private void initState() {
		checkEmpty(_providerA);
		checkEmpty(_providerB);

		_providerA.storeNew(new Replicated("oldFromA"));
		_providerB.storeNew(new Replicated("oldFromB"));

		_providerA.commit();
		_providerB.commit();

		final ReplicationSession replication = new GenericReplicationSession(_providerA, _providerB);

		replicateQueryingFrom(replication, _providerA, _providerB);
		replicateQueryingFrom(replication, _providerB, _providerA);

		replication.commit();
	}

	private void checkEmpty(TestableReplicationProviderInside provider) {
		if (provider.getStoredObjects(Replicated.class).hasNext())
			throw new RuntimeException(provider.getName() + " is not empty");
	}

	private static void replicateQueryingFrom(ReplicationSession replication, ReplicationProvider origin, ReplicationProvider other) {
		ObjectSet changes = origin.objectsChangedSinceLastReplication();
		while (changes.hasNext()) {
			Object changed = changes.next();
			replication.replicate(changed);
		}

		ObjectSet deletions = origin.uuidsDeletedSinceLastReplication();
		while (deletions.hasNext()) {
			Db4oUUID uuid = (Db4oUUID) deletions.next();
			Object obj = other.getObject(uuid);
			if (obj == null) continue;
			replication.replicate(obj);
		}
	}

	private static void ensure(boolean condition) {
		if (!condition) throw new RuntimeException();
	}

	private String print(Set containerSet) {
		if (containerSet.isEmpty()) return "NONE";
		if (containerSet.size() == 2) return "BOTH";
		return (String) containerSet.iterator().next();
	}

	public void test() {
		super.test();
	}


}
