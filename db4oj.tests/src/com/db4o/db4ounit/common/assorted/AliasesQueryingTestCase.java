/* Copyright (C) 2004 - 2011  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;

public class AliasesQueryingTestCase extends TestWithTempFile implements OptOutSilverlight{
	
	public static void main(String[] args) {
		new ConsoleTestRunner(AliasesQueryingTestCase.class).run();
	}
	
        public void testQuery_B_Before_A_AfterAliasing(){
            createData();
            querydb(aliasConfig(), new int[] { 1, 1 }, B.class, A.class);
        }

        public void testQuery_A_Before_B_AfterAliasing(){
            createData();
            querydb(aliasConfig(), new int[] { 1, 1 }, A.class, B.class);
        }

        public void testQuery(){
        	createData();
        	EmbeddedObjectContainer database = Db4oEmbedded.openFile(aliasConfig(), tempFile());
        	database.query(B.class);
        	database.query(A.class);
            database.close();
            querydb(Db4oEmbedded.newConfiguration(), new int[] { 1, 0 }, A.class, B.class);
        }

        private void createData() {
        	EmbeddedObjectContainer database = Db4oEmbedded.openFile(tempFile());
            database.store(new A("Item1"));
            database.commit();
            database.close();
        }

        public <T, T1> void querydb(EmbeddedConfiguration config, int[] count, Class<T> class1, Class<T1> class2) {
        	EmbeddedObjectContainer database = Db4oEmbedded.openFile(config, tempFile());
        	try{
	        	List<T> list = database.query(class1);
	            Assert.areEqual(count[0], list.size(), "Unexpected result querying for " + class1.getSimpleName());
	            if(count[0] > 0){
	            	// System.out.println("Querying for " + class1.getSimpleName() + " getting " + list.get(0).getClass().getSimpleName());
	            }
	
	            List<T1> list1 = database.query(class2);
	            Assert.areEqual(count[1], list1.size(), "Unexpected result querying for " + class2.getSimpleName());
	            if(count[1] > 0){
	            	// System.out.println("Querying for " + class2.getSimpleName() + " getting " + list1.get(0).getClass().getSimpleName());
	            }

        	} finally {
        		database.close();
        	}
        }

        private EmbeddedConfiguration aliasConfig(){
            EmbeddedConfiguration configuration = Db4oEmbedded.newConfiguration();
            configuration.common().addAlias(new TypeAlias(CrossPlatformServices.fullyQualifiedName(A.class), CrossPlatformServices.fullyQualifiedName(B.class)));
            return configuration;
        }
        
        public static class A
        {
            private String _name;

            public A(String name)
            {
                _name = name;
            }

            public String getName()
            {
                return _name;
            }
            
            public void setName(String name){
            	_name = name;
            }

            public String toString()
            {
                return "Name: " + _name + " Type: " + getClass().getName();
            }
        }

        public static class B
        {
            private String _name;

            public String getName(){
                return _name;
            }
            
            public void setName(String name){
            	_name = name;
            }

            public String toString()
            {
                return "Name: " + _name + " Type: " + getClass().getName();
            }
        }


    }
