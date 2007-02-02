/* Copyright (C) 2006   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.fieldindex;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;

public interface IndexedNode extends Iterable4 {

	boolean isResolved();

	IndexedNode resolve();

	BTree getIndex();
	
	int resultSize();

	//FIXME: do we need this?
	TreeInt toTreeInt();
}