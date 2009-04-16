/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.freespace;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;

import db4ounit.*;


/**
 * @exclude
 */
public class FreespaceManagerMigrationTestCase extends FormatMigrationTestCaseBase {
    
    int [][] INT_ARRAY_DATA = { {1,2}, {3,4}};
    
    String [][] STRING_ARRAY_DATA = { {"a", "b"}, {"c","d"}};
    
    public static class StClass {
        
        public int id;

        public Vector vect;

        public Vector getVect() {
            return vect;
        }

        public void setVect(Vector vect) {
            this.vect = vect;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
    
    protected void configureForStore(Configuration config) {
        commonConfigure(config);
        config.freespace().useIndexSystem();
    }

    @Override
    protected boolean isApplicableForDb4oVersion() {
        return db4oMajorVersion() >= 5;
    }
    
    protected void configureForTest(Configuration config) {
        commonConfigure(config);
        config.freespace().useBTreeSystem();
    }
    
    @Override
    protected void deconfigureForStore(Configuration config) {
        if(!isApplicableForDb4oVersion()){
            return;
        }
    	config.freespace().useRamSystem();
    }

    @Override
    protected void deconfigureForTest(Configuration config) {
        if(!isApplicableForDb4oVersion()){
            return;
        }
    	config.freespace().useRamSystem();
    }

    private void commonConfigure(Configuration config) {
        // config.blockSize(8);
        config.objectClass(StClass.class).cascadeOnActivate(true);
        config.objectClass(StClass.class).cascadeOnUpdate(true);
        config.objectClass(StClass.class).cascadeOnDelete(true);
        config.objectClass(StClass.class).minimumActivationDepth(5);
        config.objectClass(StClass.class).updateDepth(10);
    }

    protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
        ObjectSet objectSet = objectContainer.query(StClass.class);
        for (int i = 0; i < 2; i++) {
            StClass cls = (StClass) objectSet.next();
            Vector v = cls.getVect();
            int[][] intArray = (int[][]) v.get(0);
            ArrayAssert.areEqual(INT_ARRAY_DATA[0], intArray[0]);
            ArrayAssert.areEqual(INT_ARRAY_DATA[1], intArray[1]);
            String [][] stringArray = (String[][]) v.get(1);
            ArrayAssert.areEqual(STRING_ARRAY_DATA[0], stringArray[0]);
            ArrayAssert.areEqual(STRING_ARRAY_DATA[1], stringArray[1]);
            objectContainer.delete(cls);
        }
    }

    protected String fileNamePrefix() {
        return "freespace";
    }

    protected void store(ExtObjectContainer objectContainer) {
       for( int i=0; i< 10; i++){
            StClass cls = new StClass();
            Vector v = new Vector(10);
            v.add(INT_ARRAY_DATA);
            v.add(STRING_ARRAY_DATA);
            cls.setId(i);
            cls.setVect(v);
            objectContainer.set(cls);
        }
    }


}
