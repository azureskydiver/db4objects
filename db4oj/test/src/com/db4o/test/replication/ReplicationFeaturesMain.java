/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.replication;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.replication.*;
import com.db4o.test.*;

public class ReplicationFeaturesMain {

	
	
	// THIS IS WORK IN PROGRESS BY KLAUS.
	
	
	
	
	private static final Set A = new HashSet(1); 
	private static final Set B = new HashSet(1); 
	private static final Set BOTH = new HashSet(2); 
	private static final Set NONE = Collections.EMPTY_SET; 
	
	{
		A.add("A");
		B.add("B");
		BOTH.add("A");
		BOTH.add("B");
	}
	
    private static ObjectContainer _a;
    private static ObjectContainer _b;
	
	private Set _direction;
	private Set _containersToQueryFrom;
	private Set _containersWithNewObjects;
	private Set _containersWithChangedObjects;
	private Set _objectsToPrevailInConflicts;

	private Set _possibleNamesInA;
	private Set _possibleNamesInB;
	private Set _impossibleNamesInA;
	private Set _impossibleNamesInB;
	
	private static final String[] INITIAL_NAMES = new String[]{"a1", "a2", "b1", "b2"};
	private static final String[] ALL_POSSIBLE_NAMES = new String[]{"a1", "a2", "b1", "b2", "newInA", "newInB", "a1ChangedInA", "a1ChangedInB", "b1ChangedInA", "b1ChangedInB"};
	private static final String[] NAME_CHANGES = new String[]{"newInA", "newInB", "a1ChangedInA", "a1ChangedInB", "b1ChangedInA", "b1ChangedInB"};
	
    
    public void configure(){
        Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
        Db4o.configure().generateVersionNumbers(Integer.MAX_VALUE);
    }
	
    public void test() {
		_a = Test.objectContainer();
		_b = Test.replica();
		deleteAll(_a);
		deleteAll(_b);

		clearNameExpectations();
		addNames(_possibleNamesInA, ALL_POSSIBLE_NAMES);
		addNames(_possibleNamesInB, INITIAL_NAMES);
		addNames(_impossibleNamesInB, NAME_CHANGES);
		tstDirection(A);

		clearNameExpectations();
		addNames(_possibleNamesInA, INITIAL_NAMES);
		addNames(_impossibleNamesInA, NAME_CHANGES);
		addNames(_possibleNamesInB, ALL_POSSIBLE_NAMES);
		tstDirection(B);
		
		clearNameExpectations();
		addNames(_possibleNamesInA, ALL_POSSIBLE_NAMES);
		addNames(_possibleNamesInB, ALL_POSSIBLE_NAMES);
		tstDirection(BOTH);
		
//      TODO: replication.checkConflict(obj); //(peek)
		
    }

	private void tstDirection(Set direction) {
		_direction = direction;
		
		tstQueryingFrom(NONE);
		tstQueryingFrom(A);
		tstQueryingFrom(B);
		tstQueryingFrom(BOTH);
	}

	private void tstQueryingFrom(Set containers) {
		_containersToQueryFrom = containers;
		
		tstWithNewObjectsIn(NONE);
		tstWithNewObjectsIn(A);
		tstWithNewObjectsIn(B);
		tstWithNewObjectsIn(BOTH);
	}

	private void tstWithNewObjectsIn(Set containers) {
		_containersWithNewObjects = containers;
		
		tstWithChangedObjectsIn(NONE);
		tstWithChangedObjectsIn(A);
		tstWithChangedObjectsIn(B);
		tstWithChangedObjectsIn(BOTH);
	}

	private void tstWithChangedObjectsIn(Set containers) {
		_containersWithChangedObjects = containers;
		
		tstWithObjectsPrevailingConflicts(NONE);
		tstWithObjectsPrevailingConflicts(A);
		tstWithObjectsPrevailingConflicts(B);
	}


	private void tstWithObjectsPrevailingConflicts(Set containers) {
		_objectsToPrevailInConflicts = containers;
		
		doIt();
	}

	private void doIt() {
		initState();
		checkNames(_a, _possibleNamesInA);
		checkNames(_b, _possibleNamesInB);
		checkClean();
		
	}

	private void clearNameExpectations() {
		_possibleNamesInA = new HashSet();
		_possibleNamesInB = new HashSet();
		_impossibleNamesInA = new HashSet();
		_impossibleNamesInB = new HashSet();
	}

	private void addNames(Set names, String[] newNames) {
		for (int i = 0; i < newNames.length; i++) {
			names.add(newNames[i]);
		}
	}


	private void checkNames(ObjectContainer container, Set names) {
		Iterator iter = names.iterator();
		while (iter.hasNext()) {
			String name = (String)iter.next();
			check(container, name);
		}
	}

	private void checkClean() {
		checkClean(_a);
		checkClean(_b);
	}

	private void checkClean(ObjectContainer container) {
		Test.ensure(container.get(null).size() == 0);		
	}

	private void check(ObjectContainer container, String name) {
		Query q = container.query();
		q.constrain(Replicated.class);
		q.descend("_name").constrain(name);
		Object o = q.execute().next();
		container.delete(o);
		Test.ensure(!q.execute().hasNext());
	}

	private void initState() {
		_a.set(new Replicated("a1"));
		_a.set(new Replicated("a2"));

		_b.set(new Replicated("b1"));
		_b.set(new Replicated("b2"));
		
		_a.commit();
		_b.commit();

		final ReplicationProcess replication = _a.ext().replicationBegin(_b, new ReplicationConflictHandler() {
            public Object resolveConflict(ReplicationProcess process, Object a, Object b) {
            	return null;
            }
        });

        replicateFrom(replication, _a);
		replicateFrom(replication, _b);
		replication.commit();

		Test.ensure(objectsToReplicate(replication, _a).size() == 0);
		Test.ensure(objectsToReplicate(replication, _b).size() == 0);
	}

	private void deleteAll(ObjectContainer container) {
		ObjectSet all = container.get(null);
		while (all.hasNext()) {
			container.delete(all.next());
		}
	}

    private static void replicateFrom(ReplicationProcess replication, ObjectContainer origin) {
        ObjectSet set = objectsToReplicate(replication, origin);
        while(set.hasNext()){
            replication.replicate(set.next());
        }
    }

	private static ObjectSet objectsToReplicate(ReplicationProcess replication, ObjectContainer origin) {
		Query q = origin.query();
		q.constrain(Replicated.class);
		replication.whereModified(q);
		return q.execute();
	}
    
    private static void checkAllEqual(ExtObjectContainer con1, ExtObjectContainer con2){
        DeepCompare comparator = new DeepCompare();
        
		Query q = con1.query();
		q.constrain(Replicated.class);
        ObjectSet all = q.execute();
        while(all.hasNext()){
            Object obj1 = all.next();
            con1.activate(obj1, Integer.MAX_VALUE);
            
            Db4oUUID uuid = con1.getObjectInfo(obj1).getUUID();
            Object obj2 = con2.getByUUID(uuid);
            con2.activate(obj2, Integer.MAX_VALUE);

            Test.ensure(comparator.isEqual(obj1, obj2));
        }
    }
    
}
