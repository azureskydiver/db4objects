/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.foundation.*;


/**
 * @exclude
 */
public class EmptyBTreeRange implements BTreeRange{
	
	public static final BTreeRange INSTANCE = new EmptyBTreeRange();
	
	private EmptyBTreeRange() {
	}
    
    public void traverseKeys(Visitor4 visitor){
        // do nothing
    }

    public BTreePointer first() {
        return null;
    }

	public KeyValueIterator iterator() {
		return new KeyValueIterator() {
			public Object value() {
				return null;
			}
		
			public boolean moveNext() {
				return false;
			}
		
			public Object key() {
				return null;
			}
		};
	}
}
