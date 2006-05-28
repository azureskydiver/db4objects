/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;


public class PrimitiveMarshaller1 extends PrimitiveMarshaller {
    
    public boolean useNormalClassRead(){
        return false;
    }
    
    public int writeNew(Transaction trans, YapClassPrimitive yapClassPrimitive, Object obj, boolean topLevel, YapWriter writer, boolean withIndirection){
        if(obj != null){
            TypeHandler4 handler = yapClassPrimitive.i_handler;
            handler.writeNew(_family, obj, topLevel, writer, withIndirection);
        }
        return 0;
    }
    
    


}
