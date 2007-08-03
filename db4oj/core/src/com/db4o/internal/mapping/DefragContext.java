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
public interface DefragContext extends IDMapping {
	
	Buffer sourceBufferByAddress(int address,int length) throws IOException;
	Buffer targetBufferByAddress(int address,int length) throws IOException;

	Buffer sourceBufferByID(int sourceID) throws IOException;

	Slot allocateTargetSlot(int targetLength);

	void targetWriteBytes(Buffer targetPointerReader, int targetID);

	Transaction systemTrans();

	void targetWriteBytes(BufferPair readers, int targetAddress);

	void traverseAllIndexSlots(BTree tree, Visitor4 visitor4);	
	
	ClassMetadata yapClass(int id);

	int mappedID(int id,boolean lenient);

	void registerUnindexed(int id);
	
	Iterator4 unindexedIDs();
	
	int sourceAddressByID(int sourceID);
}