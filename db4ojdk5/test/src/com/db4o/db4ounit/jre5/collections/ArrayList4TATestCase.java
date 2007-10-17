/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;


/**
 * @exclude
 */
public class ArrayList4TATestCase extends ArrayList4TATestCaseBase {

	public static void main(String[] args) {
		new ArrayList4TATestCase().runSolo();
	}


	public void testAdd() throws Exception {
		ArrayList4Asserter.assertAdd(retrieveAndAssertNullArrayList4());
	}
	
	public void testAdd_LObject() throws Exception {
		ArrayList4Asserter.assertAdd_LObject(retrieveAndAssertNullArrayList4());
	}

	public void testAddAll_LCollection() throws Exception {
		ArrayList4Asserter.assertAddAll_LCollection(retrieveAndAssertNullArrayList4());
	}

	public void testClear() throws Exception {
		ArrayList4Asserter.assertClear(retrieveAndAssertNullArrayList4());
	}

	public void testContains() throws Exception {
		ArrayList4Asserter.assertContains(retrieveAndAssertNullArrayList4());
	}

	public void testContainsAll() throws Exception {
		ArrayList4Asserter.assertContainsAll(retrieveAndAssertNullArrayList4());
	}

	public void testIndexOf() throws Exception {
		ArrayList4Asserter.assertIndexOf(retrieveAndAssertNullArrayList4());
	}

	public void testIsEmpty() throws Exception {
		ArrayList4Asserter.assertIsEmpty(retrieveAndAssertNullArrayList4());
	}

	public void testIterator() throws Exception {
		ArrayList4Asserter.assertIterator(retrieveAndAssertNullArrayList4());
	}

	public void testLastIndexOf() throws Exception {
		ArrayList4Asserter.assertLastIndexOf(retrieveAndAssertNullArrayList4());
	}

	public void testRemove_LObject() throws Exception {
		ArrayList4Asserter.assertRemove_LObject(retrieveAndAssertNullArrayList4());
	}

	public void testRemoveAll() throws Exception {
		ArrayList4Asserter.assertRemoveAll(retrieveAndAssertNullArrayList4());
	}

	public void testSet() throws Exception {
		ArrayList4Asserter.assertSet(retrieveAndAssertNullArrayList4());
	}

	public void testSize() throws Exception {
		ArrayList4Asserter.assertSize(retrieveAndAssertNullArrayList4());
	}
	
	public void testToArray() throws Exception {
		ArrayList4Asserter.assertToArray(retrieveAndAssertNullArrayList4());
	}
	
	public void testToArray_LObject() throws Exception {
		ArrayList4Asserter.assertToArray_LObject(retrieveAndAssertNullArrayList4());
	}
	
	public void testToString() throws Exception {
		ArrayList4Asserter.assertToString(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_EnsureCapacity() throws Exception {
		ArrayList4Asserter.assertTrimToSize_EnsureCapacity(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_Remove() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Remove(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_Iterator() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testEnsureCapacity_Iterator() throws Exception {
		ArrayList4Asserter.assertEnsureCapacity_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testClear_Iterator() throws Exception {
		ArrayList4Asserter.assertClear_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testClone() throws Exception {
		ArrayList4Asserter.assertClone(retrieveAndAssertNullArrayList4());
	}
	
	public void testEquals() throws Exception {
		ArrayList4Asserter.assertEquals(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_NoSuchElementException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_ConcurrentModificationException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext() throws Exception {
		ArrayList4Asserter.assertIteratorNext(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorRemove() throws Exception {
		ArrayList4Asserter.assertIteratorRemove(retrieveAndAssertNullArrayList4());
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		ArrayList4Asserter.assertRemove_IllegalStateException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorRemove_ConcurrentModificationException(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList() throws Exception {
		ArrayList4Asserter.assertSubList(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList_ConcurrentModification() throws Exception {
		ArrayList4Asserter.assertSubList_ConcurrentModification(retrieveAndAssertNullArrayList4());
	}
	public void testSubList_Clear() throws Exception {
		ArrayList4Asserter.assertSubList_Clear(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList_Clear2() throws Exception {
		ArrayList4Asserter.assertSubList_Clear2(retrieveAndAssertNullArrayList4());
	}

}
