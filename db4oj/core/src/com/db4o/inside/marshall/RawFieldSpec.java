/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */
package com.db4o.inside.marshall;

import com.db4o.*;

public class RawFieldSpec {
	private final String _name;
	private final int _handlerID;
	private final boolean _isPrimitive;
	private final boolean _isArray;
	private final boolean _isNArray;
	private final boolean _isVirtual;
	private int _indexID;

	public RawFieldSpec(final String name, final int handlerID, final byte attribs) {
		_name = name;
		_handlerID = handlerID;
		YapBit yb = new YapBit(attribs);
        _isPrimitive = yb.get();
        _isArray = yb.get();
        _isNArray = yb.get();
        _isVirtual=false;
        _indexID=0;
	}

	public RawFieldSpec(final String name) {
		_name = name;
		_handlerID = 0;
        _isPrimitive = false;
        _isArray = false;
        _isNArray = false;
        _isVirtual=true;
        _indexID=0;
	}

	public String name() {
		return _name;
	}
	
	public int handlerID() {
		return _handlerID;
	}
	
	public boolean isPrimitive() {
		return _isPrimitive;
	}

	public boolean isArray() {
		return _isArray;
	}

	public boolean isNArray() {
		return _isNArray;
	}
	
	public boolean isVirtual() {
		return _isVirtual;
	}
	
	public int indexID() {
		return _indexID;
	}
	
	void indexID(int indexID) {
		_indexID=indexID;
	}
}
