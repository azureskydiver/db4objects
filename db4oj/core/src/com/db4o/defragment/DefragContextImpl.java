/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.defragment;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class DefragContextImpl implements DefragContext {	

	public static abstract class DbSelector {
		DbSelector() {
		}
		
		abstract LocalObjectContainer db(DefragContextImpl context);

		Transaction transaction(DefragContextImpl context) {
			return db(context).systemTransaction();
		}
	}
	
	public final static DbSelector SOURCEDB=new DbSelector() {
		LocalObjectContainer db(DefragContextImpl context) {
			return context._sourceDb;
		}
	};

	public final static DbSelector TARGETDB=new DbSelector() {
		LocalObjectContainer db(DefragContextImpl context) {
			return context._targetDb;
		}
	};

	private static final long CLASSCOLLECTION_POINTER_ADDRESS = 2+2*Const4.INT_LENGTH;
	
	public final LocalObjectContainer _sourceDb;
	final LocalObjectContainer _targetDb;
	private final ContextIDMapping _mapping;
	private DefragmentListener _listener;
	private Queue4 _unindexed=new NonblockingQueue();
	

	public DefragContextImpl(DefragmentConfig defragConfig,DefragmentListener listener) {
		_listener=listener;
		Config4Impl originalConfig =  (Config4Impl) defragConfig.db4oConfig();
		Configuration sourceConfig=(Configuration) originalConfig.deepClone(null);
		sourceConfig.weakReferences(false);
		sourceConfig.flushFileBuffers(false);
		sourceConfig.readOnly(true);
		_sourceDb=(LocalObjectContainer)Db4o.openFile(sourceConfig,defragConfig.tempPath()).ext();
		_targetDb = freshYapFile(defragConfig.origPath(),defragConfig.blockSize());
		_mapping=defragConfig.mapping();
		_mapping.open();
	}
	
	static LocalObjectContainer freshYapFile(String fileName,int blockSize) {
		new File(fileName).delete();
		return (LocalObjectContainer)Db4o.openFile(DefragmentConfig.vanillaDb4oConfig(blockSize),fileName).ext();
	}
	
	public int mappedID(int oldID,int defaultID) {
		int mapped=internalMappedID(oldID,false);
		return (mapped!=0 ? mapped : defaultID);
	}

	public int mappedID(int oldID) throws MappingNotFoundException {
		int mapped=internalMappedID(oldID,false);
		if(mapped==0) {
			throw new MappingNotFoundException(oldID);
		}
		return mapped;
	}

	public int mappedID(int id,boolean lenient) throws MappingNotFoundException {
		if(id == 0){
			return 0;
		}
		int mapped = internalMappedID(id,lenient);
		if(mapped==0) {
			_listener.notifyDefragmentInfo(new DefragmentInfo("No mapping found for ID "+id));
			return 0;
		}
		return mapped;
	}

	private int internalMappedID(int oldID,boolean lenient) throws MappingNotFoundException {
		if(oldID==0) {
			return 0;
		}
		if(_sourceDb.handlers().isSystemHandler(oldID)) {
			return oldID;
		}
		return _mapping.mappedID(oldID,lenient);
	}

	public void mapIDs(int oldID,int newID, boolean isClassID) {
		_mapping.mapIDs(oldID,newID, isClassID);
	}

	public void close() {
		_sourceDb.close();
		_targetDb.close();
		_mapping.close();
	}
	
	public Buffer readerByID(DbSelector selector,int id) {
		Slot slot=readPointer(selector, id);
		return readerByAddress(selector,slot.address(),slot.length());
	}

	public StatefulBuffer sourceWriterByID(int id) throws IOException {
		Slot slot=readPointer(SOURCEDB, id);
		return _sourceDb.readWriterByAddress(SOURCEDB.transaction(this),slot.address(),slot.length());
	}

	public Buffer sourceReaderByAddress(int address,int length) throws IOException {
		return readerByAddress(SOURCEDB, address, length);
	}

	public Buffer targetReaderByAddress(int address,int length) throws IOException {
		return readerByAddress(TARGETDB, address, length);
	}

	public Buffer readerByAddress(DbSelector selector,int address,int length) {
		return selector.db(this).bufferByAddress(address,length);
	}

	public StatefulBuffer targetWriterByAddress(int address,int length) throws IllegalArgumentException {
		return _targetDb.readWriterByAddress(TARGETDB.transaction(this),address,length);
	}
	
	public Slot allocateTargetSlot(int length) {
		return _targetDb.getSlot(length);
	}

	public void targetWriteBytes(ReaderPair readers,int address) {
		readers.write(_targetDb,address);
	}

	public void targetWriteBytes(Buffer reader,int address) {
		_targetDb.writeBytes(reader,address,0);
	}

	public StoredClass[] storedClasses(DbSelector selector) {
		LocalObjectContainer db = selector.db(this);
		db.showInternalClasses(true);
		try {
			return db.classCollection().storedClasses();
		} finally {
			db.showInternalClasses(false);
		}
	}
	
	public LatinStringIO stringIO() {
		return _sourceDb.stringIO();
	}
	
	public void targetCommit() {
		_targetDb.commit();
	}
	
	public TypeHandler4 sourceHandler(int id) {
		return _sourceDb.handlerByID(id);
	}
	
	public int sourceClassCollectionID() {
		return _sourceDb.classCollection().getID();
	}

	public static void targetClassCollectionID(String file,int id) throws IOException {
		RandomAccessFile raf=new RandomAccessFile(file,"rw");
		try {
			Buffer reader=new Buffer(Const4.INT_LENGTH);

			raf.seek(CLASSCOLLECTION_POINTER_ADDRESS);			
			reader._offset=0;
			reader.writeInt(id);
			raf.write(reader._buffer);
		}
		finally {
			raf.close();
		}
	}

	private Hashtable4 _classIndices=new Hashtable4(16);

	public int classIndexID(ClassMetadata yapClass) {
		return classIndex(yapClass).id();
	}

	public void traverseAll(ClassMetadata yapClass,Visitor4 command) {
		if(!yapClass.hasClassIndex()) {
			return;
		}
		yapClass.index().traverseAll(SOURCEDB.transaction(this), command);
	}
	
	public void traverseAllIndexSlots(ClassMetadata yapClass,Visitor4 command) {
		Iterator4 slotIDIter=yapClass.index().allSlotIDs(SOURCEDB.transaction(this));
		while(slotIDIter.moveNext()) {
			command.visit(slotIDIter.current());
		}
	}

	public void traverseAllIndexSlots(BTree btree,Visitor4 command) {
		Iterator4 slotIDIter=btree.allNodeIds(SOURCEDB.transaction(this));
		while(slotIDIter.moveNext()) {
			command.visit(slotIDIter.current());
		}
	}

	public int databaseIdentityID(DbSelector selector) {
		LocalObjectContainer db = selector.db(this);
		Db4oDatabase identity = db.identity();
		if(identity==null) {
			return 0;
		}
		return identity.getID(selector.transaction(this));
	}
	
	private ClassIndexStrategy classIndex(ClassMetadata yapClass) {
		ClassIndexStrategy classIndex=(ClassIndexStrategy)_classIndices.get(yapClass);
		if(classIndex==null) {
			classIndex=new BTreeClassIndexStrategy(yapClass);
			_classIndices.put(yapClass,classIndex);
			classIndex.initialize(_targetDb);
		}
		return classIndex;
	}

	public Transaction systemTrans() {
		return SOURCEDB.transaction(this);
	}

	public void copyIdentity() {
		_targetDb.setIdentity(_sourceDb.identity());
	}

	public void targetClassCollectionID(int newClassCollectionID) {
		_targetDb.systemData().classCollectionID(newClassCollectionID);
	}

	public Buffer sourceReaderByID(int sourceID) throws IOException {
		return readerByID(SOURCEDB,sourceID);
	}
	
	public BTree sourceUuidIndex() {
		if(sourceUuidIndexID()==0) {
			return null;
		}
		return _sourceDb.getUUIDIndex().getIndex(systemTrans());
	}
	
	public void targetUuidIndexID(int id) {
		_targetDb.systemData().uuidIndexId(id);
	}

	public int sourceUuidIndexID() {
		return _sourceDb.systemData().uuidIndexId();
	}
	
	public ClassMetadata yapClass(int id) {
		return _sourceDb.classMetadataForId(id);
	}
	
	public void registerUnindexed(int id) {
		_unindexed.add(new Integer(id));
	}

	public Iterator4 unindexedIDs() {
		return _unindexed.iterator();
	}

	private Slot readPointer(DbSelector selector,int id) {
		Buffer reader=readerByAddress(selector, id, Const4.POINTER_LENGTH);
        if(Deploy.debug){
            reader.readBegin(Const4.YAPPOINTER);    
        }
		int address=reader.readInt();
		int length=reader.readInt();
        if(Deploy.debug){
            reader.readEnd();
        }
		return new Slot(address,length);
	}

	public int blockSize() {
		return _sourceDb.config().blockSize();
	}
	
}