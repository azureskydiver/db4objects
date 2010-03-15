/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.ids;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class SequentialIdGenerator {
	
	private final int _minValidId;
	
	private final int _maxValidId;
	
	private int _idGenerator;
	
	private boolean _overflow;
	
	private final Function4<Integer, Integer> _findFreeId;
	
	public SequentialIdGenerator(Function4<Integer, Integer> findFreeId, int initialValue, int minValidId, int maxValidId) {
		_findFreeId = findFreeId;
		 _minValidId = minValidId;
		_maxValidId = maxValidId;
		initializeGenerator(initialValue);
	}
	
	public SequentialIdGenerator(Function4<Integer, Integer> findFreeId, int minValidId, int maxValidId) {
		this(findFreeId, minValidId - 1, minValidId, maxValidId);
	}

	public void read(ByteArrayBuffer buffer) {
		initializeGenerator(buffer.readInt());
	}
	
	private void initializeGenerator(int val){
		if(val < 0){
			_overflow = true;
			_idGenerator = - val;
			return;
		}
		_idGenerator = val;
	}
	
	
	public void write(ByteArrayBuffer buffer) {
		buffer.writeInt(persistentGeneratorValue()); 
	}

	public int persistentGeneratorValue() {
		return _overflow ?  -_idGenerator : _idGenerator;
	}
	
	public int newId() {
		adjustIdGenerator(_idGenerator);
		if(! _overflow){
			return _idGenerator;
		}
		int id = _findFreeId.apply(_idGenerator);
		if(id > 0){
			adjustIdGenerator(id - 1);
			return id;
		}
		id = _findFreeId.apply(_minValidId);
		if(id > 0){
			adjustIdGenerator(id - 1);
			return id;
		}
		throw new Db4oFatalException("Out of IDs");
	}
	
	private void adjustIdGenerator(int id) {
		if(id == _maxValidId){
			_idGenerator = _minValidId;
			_overflow = true;
			return;
		}
		_idGenerator = id + 1;
	}

}