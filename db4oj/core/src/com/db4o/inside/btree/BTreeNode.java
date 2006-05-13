/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.btree;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.inside.ix.*;

/**
 * We work with BTreeNode in two states:
 * 
 * - deactivated: never read, no valid members, ID correct or 0 if new
 * - write: real representation of keys, values and children in arrays
 * The write state can be detected with canWrite(). States can be changed
 * as needed with prepareRead() and prepareWrite().
 * 
 * @exclude
 */
public class BTreeNode extends YapMeta{
    
    private static final int MAX_ENTRIES = 4;

    private static final int HALF_ENTRIES = MAX_ENTRIES / 2;
    
    private static final int COUNT_LEAF_AND_3_LINK_LENGTH = (YapConst.YAPINT_LENGTH * 4) + 1; 
 
    private static final int SLOT_LEADING_LENGTH = YapConst.LEADING_LENGTH  + COUNT_LEAF_AND_3_LINK_LENGTH;
    
   
    final BTree _btree;
    
    
    private int _count;
    
    private boolean _isLeaf;
    
    
    private Object[] _keys;
    
    /**
     * Can contain BTreeNode or Integer for ID of BTreeNode 
     */
    private Object[] _children;  
    
    /**
     * Only used for leafs
     */
    private Object[] _values;
    
    
    private int _parentID;
    
    private int _previousID;
    
    private int _nextID;
    
    private boolean _dead;
    
    
    
    /* Constructor for new nodes */
    public BTreeNode(BTree btree, 
                     int count, 
                     boolean isLeaf,
                     int parentID, 
                     int previousID, 
                     int nextID){
        _btree = btree;
        _parentID = parentID;
        _previousID = previousID;
        _nextID = nextID;
        _count = count;
        _isLeaf = isLeaf;
        prepareArrays();
        setStateDirty();
    }
    
    /* Constructor for existing nodes, requires valid ID */
    public BTreeNode(int id, BTree btree){
        _btree = btree;
        setID(id);
        setStateDeactivated();
    }
    
    /* Constructor to create a new root from two nodes */
    public BTreeNode(Transaction trans, BTreeNode firstChild, BTreeNode secondChild){
        this(firstChild._btree, 2, false, 0, 0, 0);
        _keys[0] = firstChild._keys[0];
        _children[0] = firstChild;
        _keys[1] = secondChild._keys[0];
        _children[1] = secondChild;
        
        write(trans.systemTransaction());
        
        firstChild._parentID = getID();
        secondChild._parentID = getID();
    }

    
    
    /**
     * @return the split node if this node is split
     * or this if the first key has changed
     */
    public BTreeNode add(Transaction trans){
        
        YapReader reader = prepareRead(trans);
        
        Searcher s = search(trans, reader);
        
        if(_isLeaf){
            
            prepareWrite(trans);
            

            // TODO: Anything special on exact match?  Possibly compare value part also?
            
//            if(s._cmp == 0){
//                
//            }
            
            // Leaf only: Check last comparison result and position
            //            beyond last if added is greater.
            if(s._cmp < 0){
                s._cursor ++;
            }
            
            insert(s._cursor);
            _keys[s._cursor] = new BTreeAdd(trans, keyHandler().current());
            if(handlesValues()){
                _values[s._cursor] = valueHandler().current();
            }
            
        }else{
            
            BTreeNode childNode = child(reader, s._cursor);
            BTreeNode childNodeOrSplit = childNode.add(trans);
            if(childNodeOrSplit == null){
                return null;
            }
            prepareWrite(trans);
            _keys[s._cursor] = childNode._keys[0];
            if(childNode != childNodeOrSplit){
                int splitCursor = s._cursor + 1;
                insert(splitCursor);
                _keys[splitCursor] = childNodeOrSplit._keys[0];
                _children[splitCursor] = childNodeOrSplit;
            }
        }
        
        if(_count == MAX_ENTRIES){
            return split(trans);
        }
        
        if(s._cursor == 0){
            return this;  
        }
        
        return null;
    }
    
    private boolean canWrite(){
        return _keys != null;
    }
    
    private BTreeNode child(int index){
        if (_children[index] instanceof BTreeNode){
            return (BTreeNode)_children[index];
        }
        return _btree.produceNode(((Integer)_children[index]).intValue());
    }
    
    private BTreeNode child(YapReader reader, int index){
        if( childLoaded(index) ){
            return (BTreeNode)_children[index];
        }
        BTreeNode child = _btree.produceNode(childID(reader, index));
        
        // TODO: Check exactly, when we want to keep a reference
        // to children. This should probably happen from the child.
        
//        if(_children != null){
//            _children[index] = child; 
//        }
        return child;
    }
    
    private int childID(YapReader reader, int index){
        if(_children == null){
            seekChild(reader, index);
            return reader.readInt();
        }
        return childID(index);
    }
    
    private int childID(int index){
        if(childLoaded(index)){
            return ((BTreeNode)_children[index]).getID();
        }
        return ((Integer)_children[index]).intValue();
    }
    
    private boolean childLoaded(int index){
        if(_children == null){
            return false;
        }
        return _children[index] instanceof BTreeNode;
    }
    
    private boolean childCanSupplyFirstKey(int index){
        if(! childLoaded(index)){
            return false;
        }
        return ((BTreeNode)_children[index])._keys != null;
    }
    
    void commit(Transaction trans){
        
        if(_dead){
            return;
        }

        if(! canWrite()){
            return;
        }
        
        if(! isDirty(trans)){
            return;
        }
        
        Object keyZero = _keys[0];
        
        if(_isLeaf){
            
            boolean vals = handlesValues();
            
            Object[] tempKeys = new Object[MAX_ENTRIES];
            Object[] tempValues = vals ? new Object[MAX_ENTRIES] : null; 
            
            int count = 0;
        
            for (int i = 0; i < _count; i++) {
                Object key = _keys[i];
                BTreePatch patch = keyPatch(i);
                if(patch != null){
                    key = patch.commit(trans, _btree);
                }
                if(key != No4.INSTANCE){
                    tempKeys[count] = key;
                    if(vals){
                        tempValues[count] = _values[i];
                    }
                    count ++;
                }
            }
            
            _keys = tempKeys;
            _values = tempValues;
            
            _count = count;
            
            if(_count == 0){
                if(_parentID != 0){
                    free(trans);
                    return;
                }
            }
            
            
            // TODO: Merge nodes here on low _count value.
            
        }else{
        
            for (int i = 0; i < _count; i++) {
                BTreePatch patch = keyPatch(i);
                if(patch != null){
                    _keys[i] = child(i).firstKey(trans);
                }
            }
        }
        
        if(_keys[0] != keyZero){
            tellParentAboutChangedKey(trans);
        }
        
    }
    
    private void free(Transaction trans){
        
        _dead = true;
        
        if(_parentID != 0){
            BTreeNode parent = _btree.produceNode(_parentID);
            parent.removeChild(trans, this);
        }
        
        pointPreviousTo(trans, _nextID);
        
        pointNextTo(trans, _previousID);
        
        trans.systemTransaction().slotFreePointerOnCommit(getID());
        
        _btree.removeNode(this);
        
    }
    
    private void removeChild(Transaction trans, BTreeNode child) {
        prepareWrite(trans);
        int id = child.getID();
        for (int i = 0; i < _count; i++) {
            if(childID(i) == id){
                
                if(_count == 1){
                    if(_parentID != 0){
                        free(trans);
                        return;
                    }
                }
                remove(i);
                
                if(i <= 1){
                    if(_parentID != 0){
                        BTreeNode parent = _btree.produceNode(_parentID);
                        parent.keyChanged(trans, this);
                        return;
                    }
                }
                
                if(_count == 0){
                    _isLeaf = true;
                }
            }
        }
    }
    
    private void keyChanged(Transaction trans, BTreeNode child) {
        prepareWrite(trans);
        int id = child.getID();
        for (int i = 0; i < _count; i++) {
            if(childID(i) == id){
                _keys[i] = child._keys[0];
                if(i == 0){
                    tellParentAboutChangedKey(trans);
                }
                return;
            }
        }
    }
    
    private void tellParentAboutChangedKey(Transaction trans){
        if(_parentID != 0){
            BTreeNode parent = _btree.produceNode(_parentID);
            parent.keyChanged(trans, this);
        }
    }

    private boolean isDirty(Transaction trans){
        if(! canWrite()){
            return false;
        }
        
        for (int i = 0; i < _count; i++) {
            BTreePatch patch = keyPatch(i);
            if(patch != null){
                if(patch.forTransaction(trans) != null){
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void compare(Searcher s, YapReader reader){
        Indexable4 handler = keyHandler();
        if(_keys != null){
            s.resultIs(handler.compareTo(key(s._cursor)));
        }else{
            seekKey(reader, s._cursor);
            s.resultIs(handler.compareTo(handler.readIndexEntry(reader)));
        }
    }
    
    private int entryLength(){
        int len = keyHandler().linkLength();
        if(_isLeaf){
            if(handlesValues()){
                len += valueHandler().linkLength();
            }
        }else{
            len += YapConst.YAPID_LENGTH;
        }
        return len;
    }
    
    private Object firstKey(Transaction trans){
        for (int ix = 0; ix < _count; ix++) {
            BTreePatch patch = keyPatch(ix);
            if(patch == null){
                return _keys[ix];
            }
            Object obj = patch.getObject(trans);
            if(obj != No4.INSTANCE){
                return obj;
            }
        }
        return No4.INSTANCE;
    }
    
    public byte getIdentifier() {
        return YapConst.BTREE_NODE;
    }
    
    private boolean handlesValues(){
        return _btree._valueHandler != Null.INSTANCE; 
    }
    
    private void insert(int pos){
        if(pos < 0){
            pos = 0;
        }
        if(pos > _count -1){
            _count ++;
            return;
        }
        int len = _count - pos;
        System.arraycopy(_keys, pos, _keys, pos + 1, len);
        if(_values != null){
            System.arraycopy(_values, pos, _values, pos + 1, len);
        }
        if(_children != null){
            System.arraycopy(_children, pos, _children, pos + 1, len);
        }
        _count++;
    }
    
    private void remove(int pos){
        int len = _count - pos;
        _count--;
        System.arraycopy(_keys, pos + 1, _keys, pos, len);
        _keys[_count] = null;
        if(_values != null){
            System.arraycopy(_values, pos + 1, _values, pos, len);
            _values[_count] = null;
        }
        if(_children != null){
            System.arraycopy(_children, pos + 1, _children, pos, len);
            _children[_count] = null;
        }
    }
    
    private Object key(int index){
        BTreePatch patch = keyPatch(index);
        if(patch == null){
            return _keys[index];
        }
        return patch._object;
    }
    
    private Object key(Transaction trans, YapReader reader, int index){
        if( _keys != null ){
            return key(trans, index);
        }
        seekKey(reader, index);
        return keyHandler().readIndexEntry(reader);
    }
    
    private Object key(Transaction trans, int index){
        BTreePatch patch = keyPatch(index);
        if(patch == null){
            return _keys[index];
        }
        return patch.getObject(trans);
    }
    
    private BTreePatch keyPatch(int index){
        if( _keys[index] instanceof BTreePatch){
            return (BTreePatch)_keys[index];
        }
        return null;
    }
    
    private Indexable4 keyHandler(){
        return _btree._keyHandler;
    }
    
    public int ownLength() {
        return SLOT_LEADING_LENGTH
          + (_count * entryLength())
          + YapConst.BRACKETS_BYTES;
    }
    
    private YapReader prepareRead(Transaction trans){
        if(canWrite()){
            return null;
        }
        if(isNew()){
            return null;
        }
        
        YapReader reader = trans.i_file.readReaderByID(trans.systemTransaction(), getID());
        
        if (Deploy.debug) {
            reader.readBegin(getIdentifier());
        }
        
        readSlotHeader(reader);
        
        return reader;
    }

    void prepareWrite(Transaction trans){
        
        if(_dead){
            return;
        }
        
        if(canWrite()){
            setStateDirty();
            return;
        }
        
        if(_keys == null){
        
        // if(! isActive()){
            read(trans.systemTransaction());
            setStateDirty();
            _btree.addToProcessing(this);
        }
    }
    
    void prepareArrays(){
        if(_keys != null){
            return;
        }
        _keys = new Object[MAX_ENTRIES];
        if(_isLeaf){
            if(handlesValues()){
                _values = new Object[MAX_ENTRIES];
            }
        }else{
            _children = new Object[MAX_ENTRIES];
        }
    }
    
    private void readSlotHeader(YapReader reader){
        _count = reader.readInt();
        byte leafByte = reader.readByte();
        _isLeaf = (leafByte == 1);
        _parentID = reader.readInt();
        _previousID = reader.readInt();
        _nextID = reader.readInt();
    }
    
    public void readThis(Transaction trans, YapReader reader) {
        readSlotHeader(reader);

        prepareArrays();

        boolean isInner = ! _isLeaf;
        boolean vals = handlesValues() && _isLeaf;
        for (int i = 0; i < _count; i++) {
            _keys[i] = keyHandler().readIndexEntry(reader);
            if(vals){
                _values[i] = valueHandler().readIndexEntry(reader);
            }else{
                if(isInner){
                    _children[i] = new Integer(reader.readInt());
                }
            }
        }
    }
    
    public void remove(Transaction trans){
        YapReader reader = prepareRead(trans);
        
        Searcher s = search(trans, reader);
        if(s._cursor < 0){
            return;
        }
        
        if(_isLeaf){
            
            if(s._cmp != 0){
                return;
            }
            
            prepareWrite(trans);
            
            BTreeRemove btr = new BTreeRemove(trans, keyHandler().current());
            
            BTreePatch patch = keyPatch(s._cursor);
            if(patch != null){
                _keys[s._cursor] = patch.append(btr);
            }else{
                _keys[s._cursor] = btr;    
            }
            
            return;
        }
            
        child(reader, s._cursor).remove(trans);
    }
    
    void rollback(Transaction trans){
        
        if(! canWrite()){
            return;
        }
        
        if(_isLeaf){
            
            boolean vals = handlesValues();
            
            Object[] tempKeys = new Object[MAX_ENTRIES];
            Object[] tempValues = vals ? new Object[MAX_ENTRIES] : null; 
            
            int count = 0;
        
            for (int i = 0; i < _count; i++) {
                Object key = _keys[i];
                BTreePatch patch = keyPatch(i);
                if(patch != null){
                    key = patch.rollback(trans, _btree);
                }
                if(key != No4.INSTANCE){
                    tempKeys[count] = key;
                    if(vals){
                        tempValues[count] = _values[i];
                    }
                    count ++;
                }
            }
            
            _keys = tempKeys;
            _values = tempValues;
            
            _count = count;
            
            // TODO: Merge nodes here on low _count value.
        }
    }
    
    private Searcher search(Transaction trans, YapReader reader){
        Searcher s = new Searcher(_count);
        while(s.incomplete()){
            compare(s, reader);
        }
        if(s._cursor < 0){
            s._cursor = 0;
        }else{
            if(! _isLeaf){
            
                // Check last comparison result and step back one if added
                // is smaller than last comparison.
                
                if(s._cmp > 0 && s._cursor > 0){
                    s._cursor --;
                }
            }
        }
        return s;
    }
    
    private void seekAfterKey(YapReader reader, int ix){
        seekKey(reader, ix);
        reader._offset += keyHandler().linkLength();
    }
    
    private void seekChild(YapReader reader, int ix){
        seekAfterKey(reader, ix);
    }
    
    private void seekKey(YapReader reader, int ix){
        reader._offset = SLOT_LEADING_LENGTH + (entryLength() * ix);
    }
    
    private void seekValue(YapReader reader, int ix){
        if(handlesValues()){
            seekAfterKey(reader, ix);
        }else{
            seekKey(reader, ix);
        }
    }
    
    private BTreeNode split(Transaction trans){
        
        BTreeNode res = new BTreeNode(_btree, HALF_ENTRIES, _isLeaf,_parentID, getID(), _nextID);
        
        System.arraycopy(_keys, HALF_ENTRIES, res._keys, 0, HALF_ENTRIES);
        for (int i = HALF_ENTRIES; i < _keys.length; i++) {
            _keys[i] = null;
        }
        if(_values != null){
            res._values = new Object[MAX_ENTRIES];
            System.arraycopy(_values, HALF_ENTRIES, res._values, 0, HALF_ENTRIES);
            for (int i = HALF_ENTRIES; i < _values.length; i++) {
                _values[i] = null;
            }
        }
        if(_children != null){
            res._children = new Object[MAX_ENTRIES];
            System.arraycopy(_children, HALF_ENTRIES, res._children, 0, HALF_ENTRIES);
            for (int i = HALF_ENTRIES; i < _children.length; i++) {
                _children[i] = null;
            }
        }
        
        _count = HALF_ENTRIES;
        
        res.write(trans.systemTransaction());
        _btree.addNode(res);
        
        int splitID = res.getID();
        
        pointNextTo(trans, splitID);
        
        setNextID(trans, splitID);

        if(_children != null){
            for (int i = 0; i < HALF_ENTRIES; i++) {
                if(res._children[i] == null){
                    break;
                }
                res.child(i).setParentID(trans, splitID );
            }
        }
        return res;
    }
    
    private void pointNextTo(Transaction trans, int id){
        if(_nextID > 0){
            _btree.produceNode(_nextID).setPreviousID(trans, id);
        }
    }
    
    private void pointPreviousTo(Transaction trans, int id){
        if(_previousID != 0){
            _btree.produceNode(_previousID).setNextID(trans, id);
        }
    }
    
    private void setParentID(Transaction trans, int id){
        prepareWrite(trans);
        _parentID = id;
    }
    
    private void setPreviousID(Transaction trans, int id){
        prepareWrite(trans);
        _previousID = id;
    }
    
    private void setNextID(Transaction trans, int id){
        prepareWrite(trans);
        _nextID = id;
    }
    
    public void traverseKeys(Transaction trans, Visitor4 visitor){
        YapReader reader = prepareRead(trans);
        if(_isLeaf){
            for (int i = 0; i < _count; i++) {
                Object obj = key(trans,reader, i);
                if(obj != No4.INSTANCE){
                    visitor.visit(obj);
                }
            }
        }else{
            for (int i = 0; i < _count; i++) {
                child(reader,i).traverseKeys(trans, visitor);
            }
        }
    }
    
    public void traverseValues(Transaction trans, Visitor4 visitor){
        if(! handlesValues()){
            traverseKeys(trans, visitor);
            return;
        }
        YapReader reader = prepareRead(trans);
        if(_isLeaf){
            for (int i = 0; i < _count; i++) {
                if(key(trans,reader, i) != No4.INSTANCE){
                    visitor.visit(value(reader, i));
                }
            }
        }else{
            for (int i = 0; i < _count; i++) {
                child(reader,i).traverseValues(trans, visitor);
            }
        }
    }
    
    private Object value(YapReader reader, int index){
        if( _values != null ){
            return _values[index];
        }
        seekValue(reader, index);
        return valueHandler().readIndexEntry(reader);
    }

    
    private Indexable4 valueHandler(){
        return _btree._valueHandler;
    }
    
    public boolean writeObjectBegin() {
        if(_dead){
            return false;
        }
        if(_keys == null){
            return false;
        }
        return super.writeObjectBegin();
    }
    
    
    public void writeThis(Transaction trans, YapReader a_writer) {
        
        int count = 0;
        int startOffset = a_writer._offset;
        
        a_writer.incrementOffset(COUNT_LEAF_AND_3_LINK_LENGTH);

        if(_isLeaf){
            boolean vals = handlesValues();
            for (int i = 0; i < _count; i++) {
                Object obj = key(trans, i);
                if(obj != No4.INSTANCE){
                    count ++;
                    keyHandler().writeIndexEntry(a_writer, obj);
                    if(vals){
                        valueHandler().writeIndexEntry(a_writer, _values[i]);
                    }
                }
            }
        }else{
            for (int i = 0; i < _count; i++) {
                if(childCanSupplyFirstKey(i)){
                    BTreeNode child = (BTreeNode)_children[i];
                    Object childKey = child.firstKey(trans);
                    if(childKey != No4.INSTANCE){
                        count ++;
                        keyHandler().writeIndexEntry(a_writer, childKey);
                        a_writer.writeIDOf(trans, child);
                    }
                }else{
                    count ++;
                    keyHandler().writeIndexEntry(a_writer, key(i));
                    a_writer.writeIDOf(trans, _children[i]);
                }
            }
        }
        
        int endOffset = a_writer._offset;
        a_writer._offset = startOffset;
        a_writer.writeInt(count);
        a_writer.append( _isLeaf ? (byte) 1 : (byte) 0);
        a_writer.writeInt(_parentID);
        a_writer.writeInt(_previousID);
        a_writer.writeInt(_nextID);
        a_writer._offset = endOffset;

    }
    
    public String toString() {
        if(_count == 0){
            return "Node not loaded";
        }
        String str = "\nBTreeNode";
        str += "\nid: " + getID();
        str += "\nparent: " + _parentID;
        str += "\nprevious: " + _previousID;
        str += "\nnext: " + _nextID;
        str += "\ncount:" + _count;
        str += "\nleaf:" + _isLeaf + "\n";
        
        if(_keys != null){
            
            str += " { ";
            
            boolean first = true;
            
            for (int i = 0; i < _count; i++) {
                if(_keys[i] != null){
                    if(! first){
                        str += ", ";
                    }
                    str += _keys[i].toString();
                    first = false;
                }
            }
            
            str += " }";
        }
        return str;
    }
    

}
