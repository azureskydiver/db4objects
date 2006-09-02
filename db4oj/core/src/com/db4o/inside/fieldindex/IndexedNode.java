package com.db4o.inside.fieldindex;

import com.db4o.TreeInt;
import com.db4o.foundation.Iterator4;
import com.db4o.inside.btree.BTree;

public interface IndexedNode {

	boolean isResolved();

	IndexedNode resolve();
	
	BTree getIndex();
	
	int resultSize();

	//FIXME: do we need this?
	TreeInt toTreeInt();

	Iterator4 iterator();
}