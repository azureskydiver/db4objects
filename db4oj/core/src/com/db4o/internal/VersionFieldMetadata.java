/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import com.db4o.ext.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public class VersionFieldMetadata extends VirtualFieldMetadata {

    VersionFieldMetadata(ObjectContainerBase stream) {
        super();
        setName(VirtualField.VERSION);
        i_handler = new LongHandler(stream);
    }
    
    public void addFieldIndex(MarshallerFamily mf, ClassMetadata yapClass, StatefulBuffer writer, Slot oldSlot) {
        writer.writeLong(writer.getStream().generateTimeStampId());
    }
    
    public void delete(MarshallerFamily mf, StatefulBuffer a_bytes, boolean isUpdate) {
        a_bytes.incrementOffset(linkLength());
    }

    void instantiate1(Transaction a_trans, ObjectReference a_yapObject, Buffer a_bytes) {
        a_yapObject.virtualAttributes().i_version = a_bytes.readLong();
    }

    void marshall1(ObjectReference a_yapObject, StatefulBuffer a_bytes, boolean a_migrating, boolean a_new) {
        ObjectContainerBase stream = a_bytes.getStream()._parent;
        VirtualAttributes va = a_yapObject.virtualAttributes();
        if (! a_migrating) {
            va.i_version = stream.generateTimeStampId();
        }
        if(va == null){
            a_bytes.writeLong(0);
        }else{
            a_bytes.writeLong(va.i_version);
        }
    }

    public int linkLength() {
        return Const4.LONG_LENGTH;
    }
    
    void marshallIgnore(Buffer writer) {
        writer.writeLong(0);
    }


}