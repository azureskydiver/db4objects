/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.ext;

import com.db4o.*;


/**
 * this Exception is thrown, if objects can not be stored and if
 * db4o is configured to throw Exceptions on storage failures.
 * @see <a href="../config/Configuration.html#exceptionsOnNotStorable(boolean)">
 * <code>Configuration#exceptionsOnNotStorable()</code></a>.
 */
public class ObjectNotStorableException extends RuntimeException{
	
	public ObjectNotStorableException(Class a_class){
	    super(Messages.get(a_class.isPrimitive() ? 59: 45, a_class.getName()));
	}
}
