/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;


/**
 * @exclude
 */
public class IDHandler extends PrimitiveIntHandler {

	public IDHandler(ObjectContainerBase stream) {
		super(stream);
	}

    public void defragIndexEntry(DefragmentContextImpl context) {
    	context.copyID(true,false);
    }

}
