/* Copyright (C) 2004 - 2007   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.types.*;

/**
 * @exclude
 * @sharpen.ignore
 */
public class JDK {
	
	Thread addShutdownHook(Runnable runnable){
		return null;
	}
	
	/**
	 * @deprecated
	 **/
	Db4oCollections collections(Transaction transaction){
	    return null;
	}
    
    Class constructorClass(){
        return null;
    }
	
	Object createReferenceQueue() {
		return null;
	}

    public Object createWeakReference(Object obj){
        return obj;
    }
    
	Object createActivateObjectReference(Object queue, ObjectReference ref, Object obj) {
		return null;
	}
	
	Object deserialize(byte[] bytes) {
    	throw new Db4oException(Messages.NOT_IMPLEMENTED);
    }
	
    public void extendConfiguration(Config4Impl config) {
        
    }

    public Config4Class extendConfiguration(ReflectClass clazz, Configuration config, Config4Class classConfig) {
    	return classConfig;
    }

    void forEachCollectionElement(Object obj, Visitor4 visitor) {
        Enumeration e = null;
        if (obj instanceof Hashtable) {
            e = ((Hashtable)obj).elements();
        } else if (obj instanceof Vector) {
            e = ((Vector)obj).elements();
        }
        if (e != null) {
            while (e.hasMoreElements()) {
                visitor.visit(e.nextElement());
            }
        }
	}
	
    String format(Date date, boolean showTime) {
		return date.toString();
	}
	
	Object getContextClassLoader(){
		return null;
	}

	Object getYapRefObject(Object obj) {
		return null;
	}
    
    boolean isCollectionTranslator(Config4Class config) {
        if (config != null) {
            ObjectTranslator ot = config.getTranslator();
            if (ot != null) {
                return ot instanceof THashtable;
            }
        }
        return false;
    }
    
   public boolean isConnected(Socket socket){
       return socket != null;
   }

	public int ver(){
	    return 1;
	}
	
	void killYapRef(Object obj){
		
	}
	
	public Class loadClass(String className, Object classLoader) throws ClassNotFoundException {
	    // We can't use the ClassLoader here since JDK get's converted to .NET
	    // Functionality is overridden in JDKReflect 
		return Class.forName(className);
	}
	
	synchronized void lockFile(String path,Object file){
	}
	
	/**
     * use for system classes only, since not ClassLoader
     * or Reflector-aware
	 */
	boolean methodIsAvailable(String className, String methodName, Class[] params) {
    	return false;
    }

	public long nanoTime() {
		throw new NotImplementedException();
	}
	
	void pollReferenceQueue(ObjectContainerBase session, Object referenceQueue) {
		
	}
	
	public void registerCollections(GenericReflector reflector) {
		
	}
	
	void removeShutdownHook(Thread thread){
		
	}
	
	public Constructor serializableConstructor(Class clazz){
	    return null;
	}
	
	byte[] serialize(Object obj) throws Exception{
    	throw new Db4oException(Messages.NOT_IMPLEMENTED);
    }

	void setAccessible(Object accessibleObject) {
	}
    
    boolean isEnum(Reflector reflector, ReflectClass clazz) {
        return false;
    }
	
    synchronized void unlockFile(String path,Object file) {
	}
    
    public Object weakReferenceTarget(Object weakRef){
        return weakRef;
    }
    
    public Reflector createReflector(Object classLoader) {
    	return null;
    }

    public Reflector reflectorForType(Class clazz) {
		return null;
	}

    public NetTypeHandler[] types(Reflector reflector) {
        return new NetTypeHandler[]{};
    }

    public NetTypeHandler[] netTypes(Reflector reflector) {
        return new NetTypeHandler[]{};
    }

	public boolean useNativeSerialization() {
		return true;
	}

}
