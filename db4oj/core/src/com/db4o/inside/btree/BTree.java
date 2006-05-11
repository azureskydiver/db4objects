/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;

/**
 * @exclude
 */
public class BTree extends YapMeta{
    
    private static final byte BTREE_VERSION = (byte)1;
    
    final Indexable4 _keyHandler;
    
    final Indexable4 _valueHandler;
    
    private BTreeNode _root;
   
    /**
     * All instantiated nodes are held in this tree. From here the nodes
     * are only referred to by weak references, so they can be garbage
     * collected automatically, as soon as they are no longer referenced
     * from the hard references in the BTreeNode#_children array.
     */
    private TreeIntWeakObject _nodes;
    
    private int _size;
    
    private Visitor4 _removeListener;
    
    private Hashtable4 _sizesByTransaction;
    
    
    //  just for debugging purposes for now
    public String _name;
    
    
    public BTree(String name, Transaction trans, int id, Indexable4 keyHandler, Indexable4 valueHandler){
        _name = name;
        _keyHandler = keyHandler;
        _valueHandler = (valueHandler == null) ? Null.INSTANCE : valueHandler;
        _sizesByTransaction = new Hashtable4(1);
        if(id == 0){
            setStateDirty();
            _root = new BTreeNode(this, 0, true, 0, 0, 0);
            _root.write(trans.systemTransaction());
            addNode(_root);
            write(trans.systemTransaction());
        }else{
            setID(id);
            setStateDeactivated();
        }
    }
    
    public BTree(Transaction trans, int id, Indexable4 keyHandler, Indexable4 valueHandler){
        this(null, trans, id, keyHandler, valueHandler);
    }
    
    public void add(Transaction trans, Object value){
        ensureDirty(trans);
        _keyHandler.prepareComparison(value);
        BTreeNode rootOrSplit = _root.add(trans);
        if(rootOrSplit != null && rootOrSplit != _root){
            _root = new BTreeNode(trans, _root, rootOrSplit);
            _root.write(trans.systemTransaction());
            addNode(_root);
        }
        setStateDirty();
        sizeChanged(trans, 1);
    }
    
    public void remove(Transaction trans, Object value){
        ensureDirty(trans);
        _keyHandler.prepareComparison(value);
        _root.remove(trans);
        sizeChanged(trans, -1);
    }

    
    public void commit(final Transaction trans){
        
        Object sizeDiff = _sizesByTransaction.get(trans);
        if(sizeDiff != null){
            _size += ((Integer) sizeDiff).intValue();
        }
        _sizesByTransaction.remove(trans);
        
        // TODO: Here we are doing writes twice.
        // New nodes will already have been written above.
        // Currently they are rewritten on commit.
        
        if(_nodes != null){
            _nodes = _nodes.traverseRemoveEmpty(new Visitor4() {
                public void visit(Object obj) {
                    ((BTreeNode)obj).commit(trans);
                }
            });
        }
        write(trans.systemTransaction());
    }
    
    public void rollback(final Transaction trans){
        
        _sizesByTransaction.remove(trans);
        
        if(_nodes == null){
            return;
        }
        _nodes = _nodes.traverseRemoveEmpty(new Visitor4() {
            public void visit(Object obj) {
                ((BTreeNode)obj).rollback(trans);
            }
        });
    }
    
    private void ensureActive(Transaction trans){
        if(! isActive()){
            read(trans.systemTransaction());
        }
    }
    
    private void ensureDirty(Transaction trans){
        ensureActive(trans);
        trans.dirtyBTree(this);
        setStateDirty();
    }
    
    public byte getIdentifier() {
        return YapConst.BTREE;
    }
    
    public void setRemoveListener(Visitor4 vis){
        _removeListener = vis;
    }
    
    public int ownLength() {
        return 1 + YapConst.OBJECT_LENGTH + YapConst.YAPINT_LENGTH + YapConst.YAPID_LENGTH;
    }
    
    BTreeNode produceNode(int id){
        TreeIntWeakObject tio = new TreeIntWeakObject(id);
        _nodes = (TreeIntWeakObject)Tree.add(_nodes, tio);
        tio = (TreeIntWeakObject)tio.duplicateOrThis();
        BTreeNode node = (BTreeNode)tio.getObject();
        if(node == null){
            node = new BTreeNode(id, this);
            tio.setObject(node);
        }
        return node;
    }
    
    void addNode(BTreeNode node){
        _nodes = (TreeIntWeakObject)Tree.add(_nodes, new TreeIntWeakObject(node.getID(), node));
    }
    
    void notifyRemoveListener(Object obj){
        if(_removeListener != null){
            _removeListener.visit(obj);
        }
    }

    public void readThis(Transaction a_trans, YapReader a_reader) {
        a_reader.incrementOffset(1);  // first byte is version, for possible future format changes
        _size = a_reader.readInt();
        _root = produceNode(a_reader.readInt());
    }
    
    public void writeThis(Transaction trans, YapReader a_writer) {
        a_writer.append(BTREE_VERSION);
        a_writer.writeInt(_size);
        a_writer.writeIDOf(trans, _root);
    }
    
    public int size(Transaction trans){
        ensureActive(trans);
        Object sizeDiff = _sizesByTransaction.get(trans);
        if(sizeDiff != null){
            return _size + ((Integer) sizeDiff).intValue();
        }
        return _size;
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        ensureActive(trans);
        if(_root == null){
            return;
        }
        _root.traverseKeys(trans, visitor);
    }
    
    public String toString() {
        if(_name == null){
            return super.toString();
        }
        return "BTree for " + _name;
    }
    
    private void sizeChanged(Transaction trans, int changeBy){
        Object sizeDiff = _sizesByTransaction.get(trans);
        if(sizeDiff == null){
            _sizesByTransaction.put(trans, new Integer(changeBy));
            return;
        }
        _sizesByTransaction.put(trans, new Integer(((Integer) sizeDiff).intValue() + changeBy));
    }


}

