/* Copyright (C) 2004 - 2007 db4objects Inc. http://www.db4o.com */

package com.db4odoc.constructors;

class C3 {
	  private String s;
	  private int i;

	  private C3(String s) {
	    this.s=s;
	    this.i=s.length();
	  }

	  public String toString() {
	    return s+i;
	  }
	}