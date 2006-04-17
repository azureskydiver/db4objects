package com.db4o.test.replication;

import com.db4o.inside.replication.TestableReplicationProviderInside;
import com.db4o.replication.ObjectState;
import com.db4o.replication.ReplicationEvent;
import com.db4o.replication.ReplicationEventListener;
import com.db4o.test.Test;

public class ReplicationEventTest extends ReplicationTestCase {
// ------------------------------ FIELDS ------------------------------

	private static final String IN_A = "in A";
	private static final String MODIFIED_IN_A = "modified in A";
	private static final String MODIFIED_IN_B = "modified in B";

	protected void actualTest() {
		tstNoAction();
		clean();

		tstNewObject();
		clean();

		//tstDeletionPrevail();
		clean();

		//tstDeletionNotPrevail();
		clean();

		tstOverrideWhenNoConflicts();
		clean();

		tstOverrideWhenConflicts();
		clean();

		tstStopTraversal();
		clean();
	}

	private void tstDeletionPrevail() {
		throw new UnsupportedOperationException("fs");
	}

	private void tstDeletionNotPrevail() {
		throw new UnsupportedOperationException("fs");
	}

	class BooleanClosure {
		private boolean value;

		public BooleanClosure(boolean value) {
			this.value = value;
		}

		void setValue(boolean v) {
			value = v;
		}

		public boolean getValue() {
			return value;
		}
	}

	private void tstNewObject() {
		storeParentAndChildToProviderA();

		final BooleanClosure invoked = new BooleanClosure(false);

		ReplicationEventListener listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				invoked.setValue(true);

				final ObjectState stateA = event.stateInProviderA();
				final ObjectState stateB = event.stateInProviderB();

				Test.ensure(stateA.isNew());
				Test.ensure(!stateB.isNew());

				Test.ensure(stateA.getObject() != null);
				Test.ensure(stateB.getObject() == null);

				event.stopTraversal();
			}
		};

		replicateAll(_providerA, _providerB, listener);

		Test.ensure(invoked.getValue());

		ensureNames(_providerA, IN_A, IN_A);
		ensureNotExist(_providerB, SPCParent.class);
		ensureNotExist(_providerB, SPCChild.class);
	}

	private void ensureNotExist(TestableReplicationProviderInside provider, Class type) {
		Test.ensure(! provider.getStoredObjects(type).hasNext());
	}

	private void ensureNames(TestableReplicationProviderInside provider, String parentName, String childName) {
		ensureOneInstanceOfParentAndChild(provider);
		SPCParent parent = (SPCParent) getOneInstance(provider, SPCParent.class);

		if (! parent.getName().equals(parentName)) {
			System.out.println("expected = " + parentName);
			System.out.println("actual = " + parent.getName());
		}

		Test.ensure(parent.getName().equals(parentName));
		Test.ensureEquals(childName, parent.getChild().getName());
	}

	private void ensureOneInstanceOfParentAndChild(TestableReplicationProviderInside provider) {
		ensureOneInstance(provider, SPCParent.class);
		ensureOneInstance(provider, SPCChild.class);
	}

	private void modifyInProviderA() {
		SPCParent parent = (SPCParent) getOneInstance(_providerA, SPCParent.class);
		parent.setName(MODIFIED_IN_A);
		SPCChild child = parent.getChild();
		child.setName(MODIFIED_IN_A);
		_providerA.update(parent);
		_providerA.update(child);
		_providerA.commit();

		ensureNames(_providerA, MODIFIED_IN_A, MODIFIED_IN_A);
	}

	private void modifyInProviderB() {
		SPCParent parent = (SPCParent) getOneInstance(_providerB, SPCParent.class);
		parent.setName(MODIFIED_IN_B);
		SPCChild child = parent.getChild();
		child.setName(MODIFIED_IN_B);
		_providerB.update(parent);
		_providerB.update(child);
		_providerB.commit();

		ensureNames(_providerB, MODIFIED_IN_B, MODIFIED_IN_B);
	}

	private void replicateAllToProviderBFirstTime() {
		replicateAll(_providerA, _providerB);

		ensureNames(_providerA, IN_A, IN_A);
		ensureNames(_providerB, IN_A, IN_A);
	}

	private void storeParentAndChildToProviderA() {
		SPCChild child = new SPCChild(IN_A);
		SPCParent parent = new SPCParent(child, IN_A);
		_providerA.storeNew(parent);
		_providerA.commit();

		ensureNames(_providerA, IN_A, IN_A);
	}

	public void test() {
		super.test();
	}

	private void tstNoAction() {
		storeParentAndChildToProviderA();
		replicateAllToProviderBFirstTime();
		modifyInProviderB();

		ReplicationEventListener listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				//do nothing
			}
		};
		replicateAll(_providerB, _providerA, listener);

		ensureNames(_providerA, MODIFIED_IN_B, MODIFIED_IN_B);
		ensureNames(_providerB, MODIFIED_IN_B, MODIFIED_IN_B);
	}

	private void tstOverrideWhenNoConflicts() {
		storeParentAndChildToProviderA();
		replicateAllToProviderBFirstTime();
		modifyInProviderB();

		ReplicationEventListener listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				Test.ensure(!event.isConflict());
				event.overrideWith(event.stateInProviderB());
			}
		};

		replicateAll(_providerB, _providerA, listener);

		ensureNames(_providerA, IN_A, IN_A);
		ensureNames(_providerB, IN_A, IN_A);
	}

	private void tstOverrideWhenConflicts() {
		storeParentAndChildToProviderA();
		replicateAllToProviderBFirstTime();

		//introduce conflicts
		modifyInProviderA();
		modifyInProviderB();

		ReplicationEventListener listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				Test.ensure(event.isConflict());

				if (event.isConflict())
					event.overrideWith(event.stateInProviderB());
			}
		};

		replicateAll(_providerA, _providerB, listener);

		ensureNames(_providerA, MODIFIED_IN_B, MODIFIED_IN_B);
		ensureNames(_providerB, MODIFIED_IN_B, MODIFIED_IN_B);
	}

	private void tstStopTraversal() {
		storeParentAndChildToProviderA();
		replicateAllToProviderBFirstTime();

		//introduce conflicts
		modifyInProviderA();
		modifyInProviderB();

		ReplicationEventListener listener = new ReplicationEventListener() {
			public void onReplicate(ReplicationEvent event) {
				Test.ensure(event.isConflict());

				event.stopTraversal();
			}
		};

		replicateAll(_providerA, _providerB, listener);

		ensureNames(_providerA, MODIFIED_IN_A, MODIFIED_IN_A);
		ensureNames(_providerB, MODIFIED_IN_B, MODIFIED_IN_B);
	}
}
