/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public final class ObjectHeader {
    
    private final ClassMetadata _classMetadata;
    
    public final MarshallerFamily _marshallerFamily;
    
    public final ObjectHeaderAttributes _headerAttributes;
    
    private int _handlerVersion;
    
    public ObjectHeader(ObjectContainerBase stream, Buffer reader){
    	this(stream,null,reader);
    }

    public ObjectHeader(ClassMetadata yapClass, Buffer reader){
    	this(null,yapClass,reader);
    }

    public ObjectHeader(StatefulBuffer writer){
        this(writer.getStream(), writer);
    }
    
    public ObjectHeader(ObjectContainerBase stream, ClassMetadata yc, Buffer reader){
        if (Deploy.debug) {
            reader.readBegin(Const4.YAPOBJECT);
        }
        int classID = reader.readInt();
        _marshallerFamily = readMarshallerFamily(reader, classID);
        
        classID=normalizeID(classID);

        _classMetadata=(yc!=null ? yc : stream.classMetadataForId(classID));

        if (Deploy.debug) {
        	// This check has been added to cope with defragment in debug mode: SlotDefragment#setIdentity()
        	// will trigger calling this constructor with a source db yap class and a target db stream,
        	// thus _yapClass==null. There may be a better solution, since this call is just meant to
        	// skip the object header.
        	if(_classMetadata!=null) {
	        	int ycID = _classMetadata.getID();
		        if (classID != ycID) {
		        	System.out.println("ObjectHeader::init YapClass does not match. Expected ID: " + ycID + " Read ID: " + classID);
		        }
        	}
        }
        _headerAttributes = readAttributes(_marshallerFamily,reader);
    }

    public static ObjectHeader defrag(DefragmentContextImpl context) {
    	BufferImpl source = context.source();
    	BufferImpl target = context.target();
		ObjectHeader header=new ObjectHeader(context.services().systemTrans().container(),null,source);
    	int newID =context.mapping().mappedID(header.classMetadata().getID());
        if (Deploy.debug) {
            target.readBegin(Const4.YAPOBJECT);
        }
		header._marshallerFamily._object.writeObjectClassID(target,newID);		
		header._marshallerFamily._object.skipMarshallerInfo(target);
		readAttributes(header._marshallerFamily, target);
    	return header;
    }		
    		
    public ObjectMarshaller objectMarshaller() {
        return _marshallerFamily._object;
    }

	private MarshallerFamily readMarshallerFamily(Buffer reader, int classID) {
		boolean marshallerAware=marshallerAware(classID);
		_handlerVersion = 0;
        if(marshallerAware) {
            _handlerVersion = reader.readByte();
        }
        MarshallerFamily marshallerFamily=MarshallerFamily.version(_handlerVersion);
		return marshallerFamily;
	}
    
    private static ObjectHeaderAttributes readAttributes(MarshallerFamily marshallerFamily,Buffer reader) {
    	return marshallerFamily._object.readHeaderAttributes((BufferImpl)reader);
    }

    private boolean marshallerAware(int id) {
    	return id<0;
    }
    
    private int normalizeID(int id) {
    	return (id<0 ? -id : id);
    }

    public ClassMetadata classMetadata() {
        return _classMetadata;
    }
    
    public int handlerVersion() {
        return _handlerVersion;
    }
    
}
