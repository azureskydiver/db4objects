/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import java.util.Date;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;


public class PrimitiveMarshaller0 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return true;
    }
    
    public int writeNew(Transaction trans, PrimitiveFieldHandler yapClassPrimitive, Object obj, boolean topLevel, StatefulBuffer parentWriter, boolean withIndirection, boolean restoreLinkOffset){
        
        int id = 0;
        
        if(obj != null){
            
            TypeHandler4 handler = yapClassPrimitive.i_handler;
        
            ObjectContainerBase stream = trans.stream();
            id = stream.newUserObject();
            int address = -1;
            int length = objectLength(handler);
            if(! stream.isClient()){
                address = ((LocalTransaction)trans).file().getSlot(length); 
            }
            trans.setPointer(id, address, length);
            
            StatefulBuffer writer = new StatefulBuffer(trans, length);
            writer.useSlot(id, address, length);
            if (Deploy.debug) {
                writer.writeBegin(Const4.YAPOBJECT);
            }
            writer.writeInt(yapClassPrimitive.getID());
            
            handler.writeNew(_family, obj, false, writer, true, false);
            
            writer.writeEnd();
            stream.writeNew(yapClassPrimitive, writer);
        }
        
        if(parentWriter != null){
            parentWriter.writeInt(id);
        }
        
        return id;
    }
    
    public Date readDate(Buffer bytes) {
		final long value = LongHandler.readLong(bytes);
		if (value == Long.MAX_VALUE) {
			return null;
		}
		return new Date(value);
	}
    
    public Integer readInteger(Buffer bytes) {
		final int value = bytes.readInt();
		if (value == Integer.MAX_VALUE) {
			return null;
		}
		return new Integer(value);
	}


}
