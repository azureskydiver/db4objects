/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect;

/** 
 * representation for java.lang.Class.
 * <br><br>See the respective documentation in the JDK API.
 * @see <a href="IReflect.html"><code>IReflect</code></a>
 */
public interface IClass {
	
	public IConstructor[] getDeclaredConstructors();
	
	public IField[] getDeclaredFields();
	
	public IField getDeclaredField(String name);
	
	public IMethod getMethod(String methodName, Class[] paramClasses);
	
	public boolean isAbstract();
	
	public boolean isInterface();
	
	public Object newInstance();
	
}

