/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.ext.*;

/**
 * no reading
 * no writing
 * no updates
 * no weak references
 * navigation by ID only both sides need synchronised ClassCollections and
 * MetaInformationCaches
 */
class YapObjectCarrier extends YapMemoryFile {
	
	YapObjectCarrier (
		YapStream a_callingStream,
		MemoryFile memoryFile
		) {
			super(a_callingStream, memoryFile);
	}
	
	void initialize0b(){
		// do nothing
	}
	
	void initialize1(){
	    i_handlers = i_parent.i_handlers;
		i_classCollection = i_parent.i_classCollection;
		i_config = i_parent.i_config;
		i_stringIo = i_parent.i_stringIo;
		i_references = new YapReferences(this);
		initialize2();
	}
	
	void initialize2b(){
		// do nothing
	}
	
	void initializeEssentialClasses(){
	    // do nothing
	}
	
	void initialize4NObjectCarrier(){
		// do nothing
	}
	
	void initNewClassCollection(){
	    // do nothing
	}
	
    protected int blockSize(){
        return 1;
    }
	
    boolean canUpdate(){
        return false;
    }
    
	void configureNewFile() {
		  // do nothing
		}
		
    public boolean close() {
        
        // TODO: An object carrier can simply be gc'd.
        // It does not need to be cleaned up.
        
        synchronized (i_lock) {
            boolean ret = close1();
            if (ret) {
				i_config = null;
            }
            return ret;
        }
    }
	
	void createTransaction() {
		i_trans = new TransactionObjectCarrier(this, null);
		i_systemTrans = i_trans;
	}
	
	long currentVersion(){
	    return 0;
	}
	
    public boolean dispatchsEvents() {
        return false;
    }
	
    protected void finalize() {
        // do nothing
    }
	
	
	final void free(int a_address, int a_length){
		// do nothing
	}
	
	int getSlot(int a_length){
		int address = i_writeAt;
		i_writeAt += a_length;
		return address;
	}
	
	public Db4oDatabase identity() {
	    return i_parent.identity();
	}
	
	boolean maintainsIndices(){
		return false;
	}
	
	void message(String msg){
		// do nothing
	}
	
	void raiseVersion(long a_minimumVersion){
	    // do nothing
	}
	
	void readThis(){
		// do nothing
	}
	
	boolean stateMessages(){
		return false; // overridden to do nothing in YapObjectCarrier
	}
	
	void write(boolean shuttingDown) {
		// System.out.println(memoryFile.getBytes().length);
		checkNeededUpdates();
		writeDirty();
		getTransaction().commit();
	}
	
	final void writeHeader(boolean shuttingDown) {
	    // do nothing
	}
	
    void writeBootRecord() {
        // do nothing
    }

}