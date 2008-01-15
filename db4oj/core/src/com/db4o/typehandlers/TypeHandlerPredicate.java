/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.typehandlers;

import com.db4o.reflect.*;


/**
 * Predicate to be able to select if a specific TypeHandler is
 * applicable for a specific Type and version.
 */
public interface TypeHandlerPredicate {
    
    /**
     * return true if a TypeHandler is to be used for a specific
     * Type and version 
     * @param classReflector the Type passed by db4o that is to
     * be tested by this predicate.
     * @param version the version
     * @return true if the TypeHandler is to be used for a specific
     * Type and version.
     */
    public boolean match(ReflectClass classReflector, int version);

}
