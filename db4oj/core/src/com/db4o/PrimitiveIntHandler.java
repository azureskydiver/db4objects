/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o;


/**
 * improves YInt performance slightly by not doing checks
 * and by doing the comparison with a substraction
 * @exclude
 */
public final class PrimitiveIntHandler extends YInt{
	
	public PrimitiveIntHandler(YapStream stream) {
		super(stream);
	}
	
	public YapComparable prepareComparison(Object obj) {
		_currentInteger = ((Integer)obj);
		_currentInt = _currentInteger.intValue();
		return this;
	}
	
	private Integer _currentInteger;
	
	private int _currentInt;

	public int compareTo(Object obj) {
		return val(obj) - _currentInt;
	}

	public Object current() {
		return _currentInteger;
	}

	public boolean isEqual(Object obj) {
		return val(obj) == _currentInt;
	}

	public boolean isGreater(Object obj) {
		return val(obj) > _currentInt;
	}

	public boolean isSmaller(Object obj) {
		return val(obj) < _currentInt;
	}
	
}
