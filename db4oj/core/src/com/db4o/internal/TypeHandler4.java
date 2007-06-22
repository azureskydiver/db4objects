/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public interface TypeHandler4 extends Comparable4 
{
	
	boolean canHold(ReflectClass claxx);
	
	void cascadeActivation(Transaction a_trans, Object a_object, int a_depth, boolean a_activate);
	
	ReflectClass classReflector();
    
	void deleteEmbedded(MarshallerFamily mf, StatefulBuffer a_bytes) throws Db4oIOException;
	
	int getID();
	
    boolean hasFixedLength();
    
    int linkLength();
   
    TernaryBool isSecondClass();
    
    /**
     * The length calculation is different, depending from where we 
     * calculate. If we are still in the link area at the beginning of
     * the slot, no data needs to be written to the payload area for
     * primitive types, since they fully fit into the link area. If
     * we are already writing something like an array (or deeper) to
     * the payload area when we come here, a primitive does require
     * space in the payload area.
     * Differentiation is expressed with the 'topLevel' parameter.
     * If 'topLevel==true' we are asking for a size calculation for
     * the link area. If 'topLevel==false' we are asking for a size
     * calculation for the payload area at the end of the slot.
     */
    void calculateLengths(Transaction trans, ObjectHeaderAttributes header, boolean topLevel, Object obj, boolean withIndirection);
    
	ReflectClass primitiveClassReflector();
	
	Object read(MarshallerFamily mf, StatefulBuffer writer, boolean redirect) throws CorruptionException, Db4oIOException;
    
	Object readIndexEntry(MarshallerFamily mf, StatefulBuffer writer) throws CorruptionException, Db4oIOException;
	
	Object readQuery(Transaction trans, MarshallerFamily mf, boolean withRedirection, Buffer reader, boolean toArray) throws CorruptionException, Db4oIOException;
	
    Object writeNew(MarshallerFamily mf, Object a_object, boolean topLevel, StatefulBuffer a_bytes, boolean withIndirection, boolean restoreLinkOffset);
	
	public int getTypeID ();
	
	ClassMetadata getClassMetadata(ObjectContainerBase a_stream);
    
    /**
     * performance optimized read (only used for byte[] so far) 
     */
    boolean readArray(Object array, Buffer reader);
	
	void readCandidates(MarshallerFamily mf, Buffer reader, QCandidates candidates) throws Db4oIOException;
	
	TypeHandler4 readArrayHandler(Transaction a_trans, MarshallerFamily mf, Buffer[] a_bytes);
	
    QCandidate readSubCandidate(MarshallerFamily mf, Buffer reader, QCandidates candidates, boolean withIndirection);

	void defrag(MarshallerFamily mf, ReaderPair readers, boolean redirect);
	
}
