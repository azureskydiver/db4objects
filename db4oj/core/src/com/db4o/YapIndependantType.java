/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;


/**
 * Common base class for YapString and YapArray:
 * There is one indirection in the database file to this.
 */
abstract class YapIndependantType implements YapDataType
{
    final YapStream _stream;
    
    public YapIndependantType(YapStream stream) {
        _stream = stream;
    }
    
    YapWriter i_lastIo;
	
	public final void copyValue(Object a_from, Object a_to){
		// do nothing
	}
	
	/** overriden in YapArray */
	public void deleteEmbedded(YapWriter a_bytes){
		int address = a_bytes.readInt();
		int length = a_bytes.readInt();
		if(address > 0){
			a_bytes.getTransaction().freeOnCommit(address, address, length);
		}
	}
	
	public final IClass primitiveClassReflector(){
		return null;
	}
	
    public Object readIndexObject(YapWriter a_writer) throws CorruptionException{
        return read(a_writer);
    }
	
	public Object indexEntry(Object a_object){
	    if(a_object == null){
	        return null;
	    }
	    return new int[] {i_lastIo.getAddress(),i_lastIo.getLength()};
	}
	
	public final int linkLength(){
		return YapConst.YAPINT_LENGTH + YapConst.YAPID_LENGTH;
	}
	
}
