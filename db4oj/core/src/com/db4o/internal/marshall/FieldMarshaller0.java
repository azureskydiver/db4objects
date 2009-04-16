/* Copyright (C) 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.handlers.*;
import com.db4o.typehandlers.*;

/**
 * @exclude
 */
public class FieldMarshaller0 extends AbstractFieldMarshaller {

    public int marshalledLength(ObjectContainerBase stream, ClassAspect aspect) {
        int len = stream.stringIO().shortLength(aspect.getName());
        if(aspect instanceof FieldMetadata){
            FieldMetadata field = (FieldMetadata) aspect;
            if(field.needsArrayAndPrimitiveInfo()){
                len += 1;
            }
            if(!(field instanceof VirtualFieldMetadata)){
                len += Const4.ID_LENGTH;
            }
        }
        return len;
    }
    
    protected RawFieldSpec readSpec(AspectType aspectType, ObjectContainerBase stream, ByteArrayBuffer reader) {
        String name = StringHandler.readStringNoDebug(stream.transaction().context(), reader);
        if(! aspectType.isFieldMetadata()){
            return new RawFieldSpec(aspectType, name);
        }
        if (name.indexOf(Const4.VIRTUAL_FIELD_PREFIX) == 0) {
        	if(stream._handlers.virtualFieldByName(name)!=null) {
                return new RawFieldSpec(aspectType, name);
        	}
        }
        int fieldTypeID = reader.readInt();
        byte attribs=reader.readByte();
        return new RawFieldSpec(aspectType, name,fieldTypeID,attribs);
    }
    
    public final FieldMetadata read(ObjectContainerBase stream, ClassMetadata containingClass, ByteArrayBuffer reader) {
    	RawFieldSpec spec=readSpec(stream, reader);
    	return fromSpec(spec, stream, containingClass);
    }
    
    protected FieldMetadata fromSpec(RawFieldSpec spec,ObjectContainerBase stream, ClassMetadata containingClass) {
    	if(spec==null) {
    		return null;
    	}
    	
    	final String name=spec.name();
    	if(spec.isVirtualField()) {
    		return stream._handlers.virtualFieldByName(name);
    	}
    	
    	if(spec.isTranslator()){
    		return new TranslatedAspect(containingClass, name);
    	}
    	
    	if (spec.isField()) {
    		return new FieldMetadata(containingClass, name, spec.fieldTypeID(), spec.isPrimitive(), spec.isArray(), spec.isNArray());
    	}
    	
    	return new FieldMetadata(containingClass, name);
    }


    public void write(Transaction trans, ClassMetadata clazz, ClassAspect aspect, ByteArrayBuffer writer) {
        
        writer.writeShortString(trans, aspect.getName());
        
        if(! (aspect instanceof FieldMetadata)){
            return;
        }
        
        FieldMetadata field = (FieldMetadata) aspect;
        
        field.alive();
        
        if(field.isVirtual()){
            return;
        }
        
        TypeHandler4 handler = field.getHandler();
        
        if (handler instanceof StandardReferenceTypeHandler) {
            
            // TODO: ensure there is a test case, to make this happen 
            if ( ((StandardReferenceTypeHandler)handler).classMetadata().getID() == 0) {
                trans.container().needsUpdate(clazz);
            }
        }
        writer.writeInt(field.fieldTypeID());
        BitMap4 bitmap = new BitMap4(3);
        bitmap.set(0, field.isPrimitive());
        bitmap.set(1, Handlers4.handlesArray(handler));
        bitmap.set(2, Handlers4.handlesMultidimensionalArray(handler)); // keep the order
        writer.writeByte(bitmap.getByte(0));
    }


	public void defrag(ClassMetadata classMetadata, ClassAspect aspect, LatinStringIO sio,
        DefragmentContextImpl context) {
        context.incrementStringOffset(sio);
        if (!(aspect instanceof FieldMetadata)) {
            return;
        }
        if (((FieldMetadata) aspect).isVirtual()) {
            return;
        }
        // handler ID
        context.copyID();
        // skip primitive/array/narray attributes
        context.incrementOffset(1);
    }
}
