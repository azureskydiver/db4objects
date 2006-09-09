/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.types;

public class RecursiveUnTypedPrivate extends RTest
{
	private Object recurse;
	private String depth;
	
	public void set(int ver){
		set(ver, 10);
	}
	
	private void set(int ver, int a_depth){
		depth = "s" + ver + ":" +  a_depth;	
		if(a_depth > 0){
			recurse = new RecursiveUnTypedPrivate();
			((RecursiveUnTypedPrivate)recurse).set(ver, a_depth - 1);
		}
	}
	
	public boolean jdk2(){
		return true;
	}

}
