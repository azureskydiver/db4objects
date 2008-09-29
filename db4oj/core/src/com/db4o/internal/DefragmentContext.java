/* Copyright (C) 2004 - 2007  db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.internal.mapping.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

public interface DefragmentContext extends BufferContext, MarshallingInfo, HandlerVersionContext{
	
	public TypeHandler4 typeHandlerForId(int id);

	public int copyID();

	public int copyIDReturnOriginalID();
	
	public int copySlotlessID();

	public int copyUnindexedID();
	
	public void defragment(TypeHandler4 handler);
	
	public int handlerVersion();

	public void incrementOffset(int length);

	boolean isLegacyHandlerVersion();
	
	public int mappedID(int origID);
	
	public ByteArrayBuffer sourceBuffer();
	
	public ByteArrayBuffer targetBuffer();

	public Slot allocateTargetSlot(int length);

	public Slot allocateMappedTargetSlot(int sourceAddress, int length);

	public int copySlotToNewMapped(int sourceAddress, int length) throws IOException;

	public ByteArrayBuffer sourceBufferByAddress(int sourceAddress, int length) throws IOException;
	
	public ByteArrayBuffer sourceBufferById(int sourceId) throws IOException;
	
	public void targetWriteBytes(int address, ByteArrayBuffer buffer);
	
	public DefragmentServices services();
}
