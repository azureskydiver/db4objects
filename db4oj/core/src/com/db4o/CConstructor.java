/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.lang.reflect.*;
import com.db4o.reflect.*;

/**
 * Reflection implementation for Constructor to map to JDK reflection.
 */
class CConstructor implements IConstructor{
	
	private final Constructor constructor;
	
	public CConstructor(Constructor constructor){
		this.constructor = constructor;
	}
	
	public Class[] getParameterTypes(){
		return constructor.getParameterTypes();
	}
	
	public void setAccessible(){
		Platform.setAccessible(constructor);
	}
	
	public Object newInstance(Object[] parameters){
		try {
			return constructor.newInstance(parameters);
		} catch (Exception e) {
			return null;
		} 
	}
}
