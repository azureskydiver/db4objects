/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.reflect.*;

/**
 * @exclude
 */
public interface YapDataType extends YapComparable
{
	
	void appendEmbedded3(YapWriter a_bytes);
		
	boolean canHold(ReflectClass claxx);
	
	void cascadeActivation(Transaction a_trans, Object a_object, int a_depth, boolean a_activate);
	
	ReflectClass classReflector();
	
	// special construct for deriving from simple types
	void copyValue(Object a_from, Object a_to);
	
	void deleteEmbedded(YapWriter a_bytes);
	
	int getID();
	
	boolean equals(YapDataType a_dataType); // needed for YapField.equals
	
	Object indexEntry(Object a_object);
	
	Object comparableObject(Transaction trans, Object indexEntry);
	
	int linkLength();
	
	void prepareLastIoComparison(Transaction a_trans, Object obj);
	
	ReflectClass primitiveClassReflector();
	
	Object read(YapWriter writer) throws CorruptionException;
    
	Object readIndexValueOrID(YapWriter writer) throws CorruptionException;
	
	Object readQuery(Transaction trans, YapReader reader, boolean toArray) throws CorruptionException;
	
	boolean supportsIndex();
	
    // returns the ID for first class objects,
    // 0 for null in first class object fields
    // -1 for primitives
	int writeNew(Object a_object, YapWriter a_bytes);
	
	public int getType ();
	
	YapClass getYapClass(YapStream a_stream);
    
    /**
     * performance optimized read (only used for byte[] so far) 
     */
    boolean readArray(Object array, YapWriter reader);
	
	void readCandidates(YapReader a_bytes, QCandidates a_candidates);
	
	Object readIndexEntry(YapReader a_reader) ;
	
	YapDataType readArrayWrapper(Transaction a_trans, YapReader[] a_bytes);
	
    /**
     * performance optimized write (only used for byte[] so far) 
     */
    boolean writeArray(Object array, YapWriter reader);
    
	void writeIndexEntry(YapWriter a_writer, Object a_object);

	
}
