/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;


import db4ounit.*;
import java.util.*;

import com.db4o.*;
import com.db4o.db4ounit.util.*;


/**
 * @sharpen.ignore
 */
public class VectorUpdateTestCase extends HandlerUpdateTestCaseBase{
    
    private static final Object[] DATA = new Object[] { 
        "one",
        "aAzZ|!§$%&/()=?ßöäüÄÖÜYZ;:-_+*~#^°'@",
        "",
        createNestedVector(10),
        null,
    };
    
    private static Vector createNestedVector(int depth){
        Vector vector = new Vector();
        vector.add("nested1");
        vector.add("nested2");
        if(depth > 0){
            vector.add(createNestedVector(depth - 1));
        }
        return vector;
    }

    protected String typeName() {
        return "Vector";
    }
    
    public static class Item {
        
        public Vector _typed;
        
        public Object _untyped;
        
        public Vector _emptyTyped;
        
        public Object _emptyUntyped;
    }
    
    /** Todo: add as type to Item **/
    public static class VectorExtensionWithField extends Vector{
        
        public static final String STORED_NAME = "outVectorsName";
        
        public String name;
        
        public boolean equals(Object obj) {
            if(! super.equals(obj)){
                return false;
            }
            VectorExtensionWithField other = (VectorExtensionWithField) obj;
            if(name == null){
                return other.name == null;
            }
            return name.equals(other.name);
        }
    }
    
    /** Todo: add as type to Item **/
    public static class VectorExtensionWithoutField extends Vector{
        
    }
    
    
    protected Object[] createValues() {
        if(testNotCompatibleToOldVersion()){
            return new Item[0];
        }
        
        Item[] values = new Item[3];
        values[0] = createItem(Vector.class);
        values[1] = createItem(VectorExtensionWithField.class);
        values[2] = createItem(VectorExtensionWithoutField.class);
        return values;
    }
    
    private Item createItem(Class clazz){
        Item item = new Item();
        createVectors(item, clazz);
        return item;
    }

    private void createVectors(Item item, Class clazz) {
        item._typed = createFilledVector(clazz);
        item._untyped = createFilledVector(clazz);
        item._emptyTyped = createVector(clazz);
        item._emptyUntyped = createVector(clazz);
    }
    
    private Vector createFilledVector(Class clazz){
        Vector vector = createVector(clazz); 
        fillVector(vector);
        if( vector instanceof VectorExtensionWithField){
            VectorExtensionWithField typedVector = (VectorExtensionWithField) vector;
            typedVector.name = VectorExtensionWithField.STORED_NAME;
        }
        return vector;
    }
        
    private Vector createVector(Class clazz){
        Vector vector = null;
        try {
            vector = (Vector) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vector;
    }
    
    private void fillVector(Object vector){
        for (int i = 0; i < DATA.length; i++) {
            ((Vector)vector).addElement(DATA[i]);
        }
    }
    
    protected Object createArrays() {
        return null;
    }
    
    protected void assertValues(Object[] values) {
        if(testNotCompatibleToOldVersion()){
            return;
        }
        assertItem(values[0], Vector.class);
        assertItem(values[1], VectorExtensionWithField.class);
        assertItem(values[2], VectorExtensionWithoutField.class);
    }

    private boolean testNotCompatibleToOldVersion() {
        // This test fails for 3.0 and 4.0 versions, probably
        // because translators are incompatible.
        
        if(db4oMajorVersion() < 5) {
            return true;
        }
        return db4oHeaderVersion() == VersionServices.HEADER_30_40;
    }

    private void assertItem(Object obj, Class clazz) {
        Item item = (Item) obj;
        assertVector(item._typed, clazz);
        assertVector(item._untyped, clazz);
        assertEmptyVector(item._emptyTyped);
        assertEmptyVector(item._emptyUntyped);
    }
    
    private void assertEmptyVector(Object obj) {
        Vector vector = (Vector) obj;
        Assert.areEqual(0, vector.size());
    }

    private void assertVector(Object obj, Class clazz) {
        Vector vector = (Vector) obj;
        Object[] array = new Object[vector.size()];
        int idx = 0;
        Enumeration enumer = vector.elements();
        while(enumer.hasMoreElements()){
            array[idx++] = enumer.nextElement();
        }
        ArrayAssert.areEqual(DATA, array);
        Assert.isInstanceOf(clazz, vector);
        if( vector instanceof VectorExtensionWithField){
            VectorExtensionWithField typedVector = (VectorExtensionWithField) vector;
            Assert.areEqual(VectorExtensionWithField.STORED_NAME, typedVector.name);
        }

    }

    protected void assertArrays(Object obj) {
        // do nothing
    }    
    
}
