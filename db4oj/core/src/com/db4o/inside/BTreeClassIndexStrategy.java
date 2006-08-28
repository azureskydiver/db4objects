/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.btree.BTree;
import com.db4o.inside.convert.conversions.ClassIndexesToBTrees;

/**
 * @exclude
 */
public class BTreeClassIndexStrategy extends AbstractClassIndexStrategy {
	
	private BTree _btreeIndex;
	
	public BTreeClassIndexStrategy(YapClass yapClass) {
		super(yapClass);
	}	

	public int entryCount(Transaction ta) {
		return _btreeIndex != null
			? _btreeIndex.size(ta)
			: 0;
	}

	public void initialize(YapStream stream) {
		createBTreeIndex(stream, 0);
	}

	public void purge() {
	}

	public void read(YapReader reader, YapStream stream) {
		readBTreeIndex(reader, stream);
	}

	public void writeId(YapReader writer, Transaction trans) {
		if (_btreeIndex == null){
            writer.writeInt(0);
        } else {
            _btreeIndex.write(trans);
            writer.writeInt(- _btreeIndex.getID());
        }
	}
	
	public void traverseAll(Transaction ta,Visitor4 command) {
		// better alternatives for this null check? (has been moved as is from YapFile)
		if(_btreeIndex!=null) {
			_btreeIndex.traverseKeys(ta,command);
		}
	}

	private void createBTreeIndex(final YapStream stream, int btreeID){
        if (stream.isClient()) {
        	return;
        }
        _btreeIndex = ((YapFile)stream).createBTreeClassIndex(btreeID);
        _btreeIndex.setRemoveListener(new Visitor4() {
            public void visit(Object obj) {
                int id = ((Integer)obj).intValue();
                YapObject yo = stream.getYapObject(id);
                if (yo != null) {
                    stream.yapObjectGCd(yo);
                }
            }
        });
    }
	
	private void readBTreeIndex(YapReader reader, YapStream stream) {
		int indexId = reader.readInt();
		if(! stream.isClient() && _btreeIndex == null){
		    YapFile yf = (YapFile)stream;
		    if(indexId < 0){
		        createBTreeIndex(stream, - indexId);
		    }else{
		        createBTreeIndex(stream, 0);
		        new ClassIndexesToBTrees().convert(yf, indexId, _btreeIndex);
		        yf.setDirtyInSystemTransaction(_yapClass);
		    }
		}
	}

	protected void internalAdd(Transaction trans, int id) {
		_btreeIndex.add(trans, new Integer(id));
	}

	protected void internalRemove(Transaction ta, int id) {
		_btreeIndex.remove(ta, new Integer(id));
	}

	public void dontDelete(Transaction transaction, int id) {
	}
	
	public void defragReference(YapClass yapClass, YapReader source, YapReader target, IDMapping mapping,int classIndexID) {
		int oldID=source.readInt();
		int newID = -classIndexID;
		target.writeInt(newID);
		PMFDDebug.logModify("CLASS INDEX",oldID,newID,source,target);
	}
	
	public int id() {
		return _btreeIndex.getID();
	}
}
