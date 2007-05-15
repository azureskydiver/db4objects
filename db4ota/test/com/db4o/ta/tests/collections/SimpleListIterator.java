/* Copyright (C) 2007   db4objects Inc.   http://www.db4o.com */
package com.db4o.ta.tests.collections;

import java.util.*;

/**
 * @sharpen.ignore
 */
public class SimpleListIterator implements Iterator {
	
	private List _list;
	private int _index;

	public SimpleListIterator(List list) {
		_list = list;
	}

	public boolean hasNext() {
		return _index < _list.size();
	}

	public Object next() {
		return _list.get(_index++);
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
