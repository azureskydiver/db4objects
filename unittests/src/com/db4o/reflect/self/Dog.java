package com.db4o.reflect.self;

public class Dog {
	private String _name;

	public Dog() {
	}
	
	public Dog(String _name) {
		this._name = _name;
	}
	
	public String toString() {
		return "DOG: "+_name;
	}
}
