/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.marshall;

import com.db4o.*;

/**
 * @exclude
 */
public abstract class ArrayMarshaller {
    
    public MarshallerFamily _family;
    
    public abstract void deleteEmbedded(YapArray arrayHandler, YapWriter reader);
    
    public abstract int marshalledLength(YapArray handler, Object obj);
    
    public abstract Object read(YapArray arrayHandler,  YapWriter reader) throws CorruptionException;
    
    public abstract Object readQuery(YapArray arrayHandler, Transaction trans, YapReader reader) throws CorruptionException;
    
    public abstract Object writeNew(YapArray arrayHandler, Object obj, YapWriter writer);

}
