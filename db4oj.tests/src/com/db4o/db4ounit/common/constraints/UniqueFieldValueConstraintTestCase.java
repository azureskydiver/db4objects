/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.constraints;

import com.db4o.config.*;
import com.db4o.constraints.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class UniqueFieldValueConstraintTestCase
	extends AbstractDb4oTestCase
	implements CustomClientServerConfiguration {
	
	public static class Item {
		
		public String	_str;

		public Item(){
		}
		
		public Item(String str){
			_str = str;
		}
	}
	
	public static class IHaveNothingToDoWithItemInstances {
		public static int _constructorCallsCounter = 0;
		public IHaveNothingToDoWithItemInstances(int value) {
			_constructorCallsCounter = value == 0xdb40 ? 0 : _constructorCallsCounter + 1; 
		}
	}	

	public void configureClient(Configuration config) throws Exception {
		super.configure(config);
    }

	public void configureServer(Configuration config) throws Exception {
		configure(config);
    }	
	
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		indexField(config, Item.class, "_str");
		config.add(new UniqueFieldValueConstraint(Item.class, "_str"));
		config.objectClass(IHaveNothingToDoWithItemInstances.class).callConstructor(true);
	}
	
	protected void store() throws Exception {
		addItem("1");
		addItem("2");
		addItem("3");
	}
	
	public void testNewViolates(){
		addItem("2");
		commitExpectingViolation();
	}	
	
	public void testUpdateViolates(){
		updateItem("2", "3");
		commitExpectingViolation();
	}
	
	public void testUpdateDoesNotViolate(){
		updateItem("2", "4");
		db().commit();
	}

	public void testUpdatingSameObjectDoesNotViolate() {
		updateItem("2", "2");
		db().commit();
	}
	
	public void testNewAfterDeleteDoesNotViolate() {
		deleteItem("2");
		addItem("2");
		db().commit();
	}
	
	public void testDeleteAfterNewDoesNotViolate() {
		Item existing = queryItem("2");
		addItem("2");
		db().delete(existing);
		db().commit();
	}

	private void deleteItem(String value) {
		db().delete(queryItem(value));
	}
	
	private void commitExpectingViolation() {
		Assert.expect(UniqueFieldValueConstraintViolationException.class, new CodeBlock() {
			public void run() throws Throwable {
				db().commit();
			}
		});
		db().rollback();
	}

	private Item queryItem(String str) {
		Query q = newQuery(Item.class);
		q.descend("_str").constrain(str);
		return (Item) q.execute().next();
	}
	
	private void addItem(String value) {
		store(new Item(value));
	}
	
	private void updateItem(String existing, String newValue) {
		Item item = queryItem(existing);
		item._str = newValue;
		store(item);
	}
	public void testObjectsAreNotReadUnnecessarily() {
		addItem("5");
		store(new IHaveNothingToDoWithItemInstances(0xdb40));
		db().commit();
		
		Assert.areEqual(expectedConstructorsCalls(), IHaveNothingToDoWithItemInstances._constructorCallsCounter);
	}
	
	private int expectedConstructorsCalls() {
		return isNetworkClientServer()
									? 2  // Account for constructor validations
									: 1;  
	}

	private boolean isNetworkClientServer() {
		return isClientServer() && !isEmbeddedClientServer();
	}
}
