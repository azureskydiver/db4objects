/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.query.result;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.QueryComparator;

/**
 * @exclude 
 */
public class StatefulQueryResult {
    
    private final QueryResult _delegate;
    private final Iterable4Adaptor _iterable;
    
    public StatefulQueryResult(QueryResult queryResult){
        _delegate = queryResult;
        _iterable = new Iterable4Adaptor(queryResult);
    }

    public Object get(int index) {
    	synchronized(streamLock()){
    		return _delegate.get(index);
    	}
    }
    
    public long[] getIDs() {
    	synchronized(streamLock()){
	    	long[] ids = new long[size()];
	        int i = 0;
	        final IntIterator4 iterator = _delegate.iterateIDs();
	        while (iterator.moveNext()) {
	        	ids[i++] = iterator.currentInt();
	        }
	        return ids;
    	}
    }

    public boolean hasNext() {
    	synchronized(streamLock()){
    		return _iterable.hasNext();
    	}
    }

    public Object next() {
    	synchronized(streamLock()){
    		return _iterable.next();
    	}
    }

    public void reset() {
    	synchronized(streamLock()){
    		_iterable.reset();
    	}
    }

    public int size() {
    	synchronized(streamLock()){
    		return _delegate.size();
    	}
    }

	public void sort(QueryComparator cmp) {
		synchronized(streamLock()){
			_delegate.sort(cmp);
		}
	}	
		
	Object streamLock() {
		return _delegate.lock();
	}
	
	ExtObjectContainer objectContainer() {
		return _delegate.objectContainer();
	}
	
	public int indexOf(Object a_object) {	
		synchronized(streamLock()){
	        int id = (int)objectContainer().getID(a_object);
	        if(id <= 0){
	            return -1;
	        }
	        return _delegate.indexOf(id);
	    }
	}

	public Iterator4 iterateIDs() {
		synchronized(streamLock()){
			return _delegate.iterateIDs();
		}
	}

	public Iterator4 iterator() {
		synchronized(streamLock()){
			return _delegate.iterator();
		}
	}
}
