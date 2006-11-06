/* Copyright (C) 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * @exclude
 */
public abstract class MappingIterator implements Iterator4 {

	private final Iterator4 _iterator;	

	private Object _current;
	
	public static final Object SKIP = new Object(); 

	public MappingIterator(Iterator4 iterator) {
		if (null == iterator) {
			throw new ArgumentNullException();
		}
		_iterator = iterator;
		_current = Iterators.NO_ELEMENT;
	}
	
	protected abstract Object map(final Object current);

	public boolean moveNext() {
		do {
			if (!_iterator.moveNext()) {
				_current = Iterators.NO_ELEMENT;
				return false;
			}
			_current = map(_iterator.current());
		} while(_current == SKIP);
		return true;
	}
	
	public void reset() {
		_current = Iterators.NO_ELEMENT;
		_iterator.reset();
	}

	public Object current() {
		if (Iterators.NO_ELEMENT == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}
}