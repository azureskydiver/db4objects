/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.replication;


public class SPCChild {

	private String name;

	public SPCChild() {
	}

	public SPCChild(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public String toString() {
		return "SPCChild{" +
				"name='" + name + '\'' +
				'}';
	}
}
