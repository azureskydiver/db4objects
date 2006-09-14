/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;

/**
 * @exclude
 */
public class BTreePointer{
	
	public static BTreePointer max(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return x;
		}
		if (y == null) {
			return y;
		}
		if (x.compareTo(y) > 0) {
			return x;
		}
		return y;
	}

	public static BTreePointer min(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return y;
		}
		if (y == null) {
			return x;
		}
		if (x.compareTo(y) < 0) {
			return x;
		}
		return y;
	}
    
    private final BTreeNode _node;
    
    private final int _index;

	private final Transaction _transaction;

	private final YapReader _nodeReader;
   
    public BTreePointer(Transaction transaction, YapReader nodeReader, BTreeNode node, int index) {
    	if(transaction == null || node == null){
            throw new ArgumentNullException();
        }
        _transaction = transaction;
        _nodeReader = nodeReader;
        _node = node;
        _index = index;
	}

	public final Transaction transaction() {
    	return _transaction;
    }
    
    public final int index(){
        return _index;
    }
    
    public final BTreeNode node() {
        return _node;
    }
    
    public final Object key() {
		return node().key(transaction(), nodeReader(), index());
	}

	public final Object value() {
		return node().value(nodeReader(), index());
	}
	
	private YapReader nodeReader() {
		return _nodeReader;
	}
    
    public BTreePointer next(){
        int indexInMyNode = index() + 1;
        while(indexInMyNode < node().count()){
            if(node().indexIsValid(transaction(), indexInMyNode)){
                return new BTreePointer(transaction(), nodeReader(), node(), indexInMyNode);
            }
            indexInMyNode ++;
        }
        int newIndex = -1;
        BTreeNode nextNode = node();
        YapReader nextReader = null;
        while(newIndex == -1){
            nextNode = nextNode.nextNode();
            if(nextNode == null){
                return null;
            }
            nextReader = nextNode.prepareRead(transaction());
            newIndex = nextNode.firstKeyIndex(transaction());
        }
        return new BTreePointer(transaction(), nextReader, nextNode, newIndex);
    }
    
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(! (obj instanceof BTreePointer)){
            return false;
        }
        BTreePointer other = (BTreePointer) obj;
        
        if(index() != other.index()){
            return false;
        }
        
        return node().equals(other.node());
    }	
    
    public String toString() {
        return "BTreePointer(index=" + index() + ", node=" + node() + ")";      
    }

	public int compareTo(BTreePointer y) {
		if (null == y) {
			throw new ArgumentNullException();
		}
		if (btree() != y.btree()) {
			throw new IllegalArgumentException();
		}		
		return btree().compareKeys(key(), y.key());
	}

	private BTree btree() {
		return node().btree();
	}

	public static boolean lessThan(BTreePointer x, BTreePointer y) {
		return BTreePointer.min(x, y) == x
			&& !equals(x, y);
	}

	public static boolean equals(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return y == null;
		}
		return x.equals(y);
	}

	public boolean isValid() {
		return node().indexIsValid(transaction(), index());
	}    
}
