/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.replication.db4o;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.inside.replication.*;

/**
 * using YapObject's hc_tree functionality, only exposing the methods
 * that are to be used in this class. Do not use superclass methods.
 * 
 * Implementation details that are difficult to read: 
 * The hc_xxx variables are used for the sorted tree.
 * The virtualAttributes is used to 
 */
public class Db4oReplicationReferenceImpl extends YapObject implements ReplicationReference, Db4oReplicationReference{
    
    private Object _counterPart;
    
    private boolean _markedForReplicating;
    
    Db4oReplicationReferenceImpl(ObjectInfo objectInfo){
        YapObject yo = (YapObject)objectInfo;
        Transaction trans = yo.getTrans();
        VirtualAttributes va = yo.virtualAttributes(trans);
        if(va != null){
            setVirtualAttributes(va.shallowClone());
        }else{
            // No virtu
            setVirtualAttributes(new VirtualAttributes());
        }
        Object obj = yo.getObject();
        setObject(obj);
        hc_init(obj);
    }
    
    public Db4oReplicationReferenceImpl(Object myObject, Db4oDatabase db, long longPart, long version) {
        setObject(myObject);
        hc_init(myObject);
        VirtualAttributes va = new VirtualAttributes();
        va.i_database = db;
        va.i_uuid = longPart;
        va.i_version = version;
        setVirtualAttributes(va);
    }

    public Db4oReplicationReferenceImpl add(Db4oReplicationReferenceImpl newNode){
        return (Db4oReplicationReferenceImpl)hc_add(newNode);
    }
    
    public Db4oReplicationReferenceImpl find(Object obj){
        return (Db4oReplicationReferenceImpl)hc_find(obj);
    }
    
    public void traverse(Visitor4 visitor){
        hc_traverse(visitor);
    }

    public Db4oUUID uuid() {
        Db4oDatabase db = signaturePart();
        if(db == null){
            return null;
        }
        return new Db4oUUID(longPart(), db.getSignature());
    }

    public long version() {
        return virtualAttributes().i_version;
    }

    public Object object() {
        return getObject();
    }

    public Object counterpart() {
        return _counterPart;
    }

    public void setCounterpart(Object obj) {
        _counterPart = obj;
    }

    public void markForReplicating() {
        _markedForReplicating = true;
    }

    public boolean isMarkedForReplicating() {
        return _markedForReplicating;
    }

    public Db4oDatabase signaturePart() {
        return virtualAttributes().i_database;
    }

    public long longPart() {
        return virtualAttributes().i_uuid;
    }
    
    private VirtualAttributes virtualAttributes(){
        return virtualAttributes(null);
    }

}
