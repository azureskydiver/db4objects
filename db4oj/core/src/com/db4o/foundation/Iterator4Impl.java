/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package  com.db4o.foundation;


/**
 * @exclude
 */
public class Iterator4Impl implements Iterator4 {
	
    public static final Iterator4 EMPTY = new Iterator4Impl(null);
    
    private List4 _next;
	
	private Object _current;

	public Iterator4Impl(List4 first){
		_next = first;
		_current = Iterators.NO_ELEMENT;
	}

	public boolean moveNext() {
		if (_next == null) {
			_current = Iterators.NO_ELEMENT;
			return false;
		}
		_current = _next._element;
		_next = _next._next;
		return true;
	}

	public Object current(){
		if (Iterators.NO_ELEMENT == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}
}
