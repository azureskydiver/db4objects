/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.io.*;

import com.db4o.*;
import com.db4o.internal.*;


/**
 * @exclude
 */
public class FieldMarshaller2 extends FieldMarshaller1 {
    
    private static final int ASPECT_TYPE_TAG_LENGTH = 1;
    
    public int marshalledLength(ObjectContainerBase stream, ClassAspect aspect) {
        return super.marshalledLength(stream, aspect) + ASPECT_TYPE_TAG_LENGTH;
    }
    
    protected RawFieldSpec readSpec(AspectType aspectType, ObjectContainerBase stream, ByteArrayBuffer reader) {
        return super.readSpec(AspectType.forByte(reader.readByte()), stream, reader);
    }
    
    public void write(Transaction trans, ClassMetadata clazz, ClassAspect aspect, ByteArrayBuffer writer) {
        writer.writeByte(aspect.aspectType()._id);
        super.write(trans, clazz, aspect, writer);
    }
    
    public void defrag(ClassMetadata classMetadata, ClassAspect aspect, LatinStringIO sio,
        final DefragmentContextImpl context)
        throws CorruptionException, IOException {
        context.readByte();
        super.defrag(classMetadata, aspect, sio, context);
    }

}
