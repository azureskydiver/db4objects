/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.mapping;

import java.io.IOException;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;

/**
 * Encapsulates services involving source and target database files during defragmenting.
 * 
 * @exclude
 */
public interface DefragmentServices extends IDMapping {
	
	BufferImpl sourceBufferByAddress(int address,int length) throws IOException;
	BufferImpl targetBufferByAddress(int address,int length) throws IOException;

	BufferImpl sourceBufferByID(int sourceID) throws IOException;

	Slot allocateTargetSlot(int targetLength);

	void targetWriteBytes(BufferImpl targetPointerReader, int targetID);

	Transaction systemTrans();

	void targetWriteBytes(DefragmentContextImpl context, int targetAddress);

	void traverseAllIndexSlots(BTree tree, Visitor4 visitor4);	
	
	ClassMetadata classMetadataForId(int id);

	int mappedID(int id,boolean lenient);

	void registerUnindexed(int id);
	
	Iterator4 unindexedIDs();
	
	int sourceAddressByID(int sourceID);
}