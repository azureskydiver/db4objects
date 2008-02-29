/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public abstract class SlotFormat {
    
    private static final Hashtable4 _versions = new Hashtable4();
    
    private static final SlotFormat CURRENT_SLOT_FORMAT = MarshallingLogicSimplification.enabled ?  
            new SlotFormatCurrent() : 
                (SlotFormat)new SlotFormat2();
    
    static{
        new SlotFormat0();
        new SlotFormat2();
    }
    
    protected SlotFormat(){
        _versions.put(handlerVersion(), this);
    }
    
    public static final SlotFormat forHandlerVersion(int handlerVersion){
        if(handlerVersion == HandlerRegistry.HANDLER_VERSION){
            return CURRENT_SLOT_FORMAT; 
        }
        if(handlerVersion < 0  || handlerVersion > CURRENT_SLOT_FORMAT.handlerVersion()){
            throw new IllegalArgumentException();
        }
        SlotFormat slotFormat = (SlotFormat) _versions.get(handlerVersion);
        if(slotFormat != null){
            return slotFormat;
        }
        return forHandlerVersion(handlerVersion + 1);
    }
    
    public boolean equals(Object obj) {
        if(! (obj instanceof SlotFormat)){
            return false;
        }
        return handlerVersion() == ((SlotFormat)obj).handlerVersion();
    }
    
    public int hashCode() {
        return handlerVersion();
    }
    
    protected abstract int handlerVersion();

    public abstract int scrollToContent(TypeHandler4 parentHandler, TypeHandler4 arrayElementHandler, ReadBuffer buffer);
    
    public abstract boolean isIndirectedWithinSlot(TypeHandler4 handler);
    
    public static SlotFormat current(){
        return CURRENT_SLOT_FORMAT;
    }
    
    protected boolean isVariableLength(TypeHandler4 handler) {
        return handler instanceof VariableLengthTypeHandler;
    }


}
