/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.types.*;

/**
 * @exclude
 * @persistent
 */
public class P2HashMap extends P1Collection implements Db4oMap, TransactionListener {

    private static final float FILL = 0.6F;

    private transient int i_changes;
    private transient boolean i_dontStoreOnDeactivate;

    public P1HashElement[] i_entries;
    public int i_mask;
    public int i_maximumSize;
    public int i_size;
    
    public int i_type;  // 0 == default hash, 1 == ID hash
    

    transient P1HashElement[] i_table;

    public int i_tableSize;

    P2HashMap() {
    }

    P2HashMap(int a_size) {
        a_size = (int) (a_size / FILL);
        i_tableSize = 1;
        while (i_tableSize < a_size) {
            i_tableSize = i_tableSize << 1;
        }
        i_mask = i_tableSize - 1;
        i_maximumSize = (int) (i_tableSize * FILL);
        i_table = new P1HashElement[i_tableSize];
    }

    public int activationDepth() {
        return 2;
    }

    public int adjustReadDepth(int a_depth) {
        return 2;
    }
    
    void checkActive() {
        super.checkActive();
        if (i_table == null) {
            i_table = new P1HashElement[i_tableSize];
            if (i_entries != null) {
                for (int i = 0; i < i_entries.length; i++) {
                    if(i_entries[i] != null){
                        i_entries[i].checkActive();
                        i_table[i_entries[i].i_position] = i_entries[i];
                    }
                }
            }
            i_changes = 0;
            
            // FIXME: reducing the table in size can be a problem during defragment in 
            //        C/S mode on P2HashMaps that were partially stored uncommitted.
            
//            if ((i_size + 1) * 10 < i_tableSize) {
//                i_tableSize = i_size + 5;
//                increaseSize();
//                modified();
//            }
            
        }
    }

    public void clear() {
        synchronized (streamLock()) {
            checkActive();
            if (i_size != 0) {
                for (int i = 0; i < i_table.length; i++) {
                    deleteAllElements(i_table[i]);
                    i_table[i] = null;
                }
                if(i_entries != null){
	                for (int i = 0; i < i_entries.length; i++) {
	                    i_entries[i] = null;
	                }
                }
                i_size = 0;
                modified();
            }
        }
    }

    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    public Object createDefault(Transaction a_trans) {
        checkActive();
        P2HashMap m4 = new P2HashMap(i_size);
        m4.i_type = i_type;
        m4.setTrans(a_trans);
        P2HashMapIterator i = new P2HashMapIterator(this);
        while (i.hasNext()) {
            Object key = i.next();
            if(key != null){
                m4.put4(key, get4(key));
            }
        }
        return m4;
    }

    private void deleteAllElements(P1HashElement a_entry) {
        if (a_entry != null) {
            a_entry.checkActive();
            deleteAllElements((P1HashElement)a_entry.i_next);
            a_entry.delete(i_deleteRemoved);
        }
    }

    public Set entrySet() {
        throw new UnsupportedOperationException();
    }

    private boolean equals(P1HashElement phe, int hashCode, Object key) {
        return phe.i_hashCode == hashCode && phe.activatedKey(elementActivationDepth()).equals(key);
    }

    public Object get(Object key) {
        synchronized (streamLock()) {
            checkActive();
            return get4(key);
        }
    }

    Object get4(Object key) {
        if(key == null){
            return null;
        }
        int hash = hashOf(key);
        P1HashElement phe = i_table[hash & i_mask];
        while (phe != null) {
            phe.checkActive();
            if (equals(phe, hash, key)) {
                return phe.activatedObject(elementActivationDepth());
            }
            phe = (P1HashElement)phe.i_next;
        }
        return null;
    }
    
    private int hashOf(Object key) {
        if(i_type == 1) {
            int id = (int)getIDOf(key);
            if(id == 0) {
                store(key);
            }
            id = (int)getIDOf(key);
            if(id == 0) {
                Exceptions4.throwRuntimeException(62);
            }
            return id;
        }
        return key.hashCode();
    }
    

    private void increaseSize() {
        i_tableSize = i_tableSize << 1;
        i_maximumSize = (int) (i_tableSize * FILL);
        i_mask = i_tableSize - 1;
        P1HashElement[] temp = i_table;
        i_table = new P1HashElement[i_tableSize];
        for (int i = 0; i < temp.length; i++) {
            reposition(temp[i]);
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set keySet() {
        return new P2HashMapKeySet(this);
    }

    void modified() {
        if (getTrans() != null) {
            if (i_changes == 0) {
                getTrans().addTransactionListener(this);
            }
            i_changes++;
        }
    }

    public void postRollback() {
        i_dontStoreOnDeactivate = true;
        deactivate();
        i_dontStoreOnDeactivate = false;
    }

    public void preCommit() {
        if (i_changes > 0) {
            Collection4 col = new Collection4();
            for (int i = 0; i < i_table.length; i++) {
                if (i_table[i] != null) {
                    i_table[i].checkActive();
                    if (i_table[i].i_position != i) {
                        i_table[i].i_position = i;
                        i_table[i].update();
                    }
                    col.add(i_table[i]);
                }
            }
            if (i_entries == null || i_entries.length != col.size()) {
                i_entries = new P1HashElement[col.size()];
            }
            int i = 0;
            Iterator4 it = col.iterator();
            while (it.hasNext()) {
                i_entries[i++] = (P1HashElement)it.next();
            }
            store(2);
        }
        i_changes = 0;
    }

    public void preDeactivate() {
        if (!i_dontStoreOnDeactivate) {
            preCommit();
        }
        i_table = null;
    }

    public Object put(Object key, Object value) {
        synchronized (streamLock()) {
            checkActive();
            return put4(key, value);
        }
    }

    private Object put4(Object key, Object value) {
        int hash = hashOf(key);
        P1HashElement entry = new P1HashElement(getTrans(), null, key, hash, value);
        i_size++;
        if (i_size > i_maximumSize) {
            increaseSize();
        }
        modified();
        int index = entry.i_hashCode & i_mask;
        P1HashElement phe = i_table[index];
        P1HashElement last = null;
        while (phe != null) {
            phe.checkActive();
            if (equals(phe, entry.i_hashCode, key)) {
                i_size--;
                Object ret = phe.activatedObject(elementActivationDepth());
                entry.i_next = phe.i_next;
                store(entry);
                if (last != null) {
                    last.i_next = entry;
                    last.update();
                } else {
                    i_table[index] = entry;
                }
                phe.delete(i_deleteRemoved);
                return ret;
            }
            last = phe;
            phe = (P1HashElement)phe.i_next;
        }
        entry.i_next = i_table[index];
        i_table[index] = entry;
        store(entry);
        return null;
    }

    public void putAll(Map t) {
        synchronized (streamLock()) {
            checkActive();
            Iterator i = t.keySet().iterator();
            while (i.hasNext()) {
                Object key = i.next();
                if (key != null) {
                    put4(key, t.get(key));
                }
            }
        }
    }

    public Object remove(Object key) {
        synchronized (streamLock()) {
            checkActive();
            return remove4(key);
        }
    }

    Object remove4(Object key) {
        int hash = hashOf(key);
        P1HashElement phe = i_table[hash & i_mask];
        P1HashElement last = null;
        while (phe != null) {
            phe.checkActive();
            if (equals(phe, hash, key)) {
                if (last != null) {
                    last.i_next = phe.i_next;
                    last.update();
                } else {
                    i_table[hash & i_mask] = (P1HashElement)phe.i_next;
                }
                modified();
                i_size--;
                Object obj = phe.activatedObject(elementActivationDepth());
                phe.delete(i_deleteRemoved);
                return obj;
            }
            last = phe;
            phe = (P1HashElement)phe.i_next;
        }
        return null;
    }
    
    public void replicateFrom(Object obj) {
        checkActive();
        if(i_entries != null){
            for (int i = 0; i < i_entries.length; i++) {
                if(i_entries[i] != null){
                    i_entries[i].delete(false);
                }
                i_entries[i] = null;
            }
        }
        if(i_table != null){
            for (int i = 0; i < i_table.length; i++) {
                i_table[i] = null;
            }
        }
        i_size = 0;
        
        P2HashMap m4 = (P2HashMap)obj;
        m4.checkActive();
        P2HashMapIterator i = new P2HashMapIterator(m4);
        while (i.hasNext()) {
            Object key = i.next();
            put4(key, m4.get4(key));
        }
        
        modified();
    }


    private void reposition(P1HashElement a_entry) {
        if (a_entry != null) {
            reposition((P1HashElement)a_entry.i_next);
            a_entry.checkActive();
            Object oldNext = a_entry.i_next;
            a_entry.i_next = i_table[a_entry.i_hashCode & i_mask];
            if (a_entry.i_next != oldNext) {
                a_entry.update();
            }
            i_table[a_entry.i_hashCode & i_mask] = a_entry;
        }
    }
    
    public int size() {
        synchronized (streamLock()) {
            checkActive();
            return i_size;
        }
    }

    public Object storedTo(Transaction a_trans) {
        if (getTrans() == null) {
            setTrans(a_trans);
            modified();
        } else {
            if (a_trans != getTrans()) {
                return replicate(getTrans(), a_trans);
            }
        }
        return this;
    }

    public Collection values() {
        throw new UnsupportedOperationException();
    }

}
