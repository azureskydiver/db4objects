/* Copyright (C) 2005   db4objects Inc.   http://www.db4o.com */

package com.db4o.reflect.jdk;

import java.lang.reflect.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;
import com.db4o.reflect.core.*;

/**
 * Reflection implementation for Class to map to JDK reflection.
 * 
 * @sharpen.ignore
 */
public class JdkClass implements JavaReflectClass{
	
	protected final Reflector _reflector;
	private final JdkReflector _jdkReflector;
	private final Class<?> _clazz;
    private ReflectConstructorSpec _constructorSpec;
    
	public JdkClass(Reflector reflector, JdkReflector jdkReflector, Class<?> clazz) {		
        if(jdkReflector == null){
        	throw new ArgumentNullException();
        }
        if(reflector == null){
            throw new ArgumentNullException();
        }
		_reflector = reflector;
		_clazz = clazz;
		_jdkReflector = jdkReflector;
		_constructorSpec = ReflectConstructorSpec.UNSPECIFIED_CONSTRUCTOR;
	}
    
	public ReflectClass getComponentType() {
		return _reflector.forClass(_clazz.getComponentType());
	}

	private ReflectConstructor[] getDeclaredConstructors(){
		if(!_jdkReflector.configuration().testConstructors()) {
			return null;
		}
		try {
			Constructor<?>[] constructors = _clazz.getDeclaredConstructors();
			ReflectConstructor[] reflectors = new ReflectConstructor[constructors.length];
			for (int i = 0; i < constructors.length; i++) {
				reflectors[i] = new JdkConstructor(_reflector, constructors[i]);
			}
			return reflectors;
		}
		catch(NoClassDefFoundError exc) {
			return new ReflectConstructor[0];
		}
	}
	
	public ReflectField getDeclaredField(String name){
		try {
			return createField(_clazz.getDeclaredField(name));
		} 
		catch (Exception e) {
			return null;
		}
		catch (NoClassDefFoundError e) {
			return null;
		}
	}

	protected JdkField createField(Field field) {
		return new JdkField(_reflector, field);
	}
	
	public ReflectField[] getDeclaredFields(){
		try {
			Field[] fields = _clazz.getDeclaredFields();
			ReflectField[] reflectors = new ReflectField[fields.length];
			for (int i = 0; i < reflectors.length; i++) {
				reflectors[i] = createField(fields[i]);
			}
			return reflectors;
		}
		catch(NoClassDefFoundError exc) {
			return new ReflectField[0];
		}
	}
    
    public ReflectClass getDelegate(){
        return this;
    }
	
	public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses){
		Class<?>[] nativeParamClasses = JdkReflector.toNative(paramClasses);
		try {
			Method method = getNativeMethod(methodName, nativeParamClasses);
			return ((method == null) ? null : new JdkMethod(method, reflector()));
		} catch (Exception e) {
			return null;
		}
	}

	private Method getNativeMethod(String methodName, Class<?>[] paramClasses) {
		Class<?> clazz = _clazz;
		while(clazz != null) {
			try {
				return clazz.getDeclaredMethod(methodName, paramClasses);
			}
			catch(NoSuchMethodException exc) {
				clazz = clazz.getSuperclass();
			}
		}
		return null;
	}
	
	public String getName(){
		return _clazz.getName();
	}
	
	public ReflectClass getSuperclass() {
		return _reflector.forClass(_clazz.getSuperclass());
	}
	
	public boolean isAbstract(){
		return Modifier.isAbstract(_clazz.getModifiers());
	}
	
	public boolean isArray() {
		return _clazz.isArray();
	}

	public boolean isAssignableFrom(ReflectClass type) {
		if(!(type instanceof JavaReflectClass)) {
			return false;
		}
		return _clazz.isAssignableFrom(JdkReflector.toNative(type));
	}
	
	public boolean isCollection() {
		return _reflector.isCollection(this);
	}
	
	public boolean isInstance(Object obj) {
		return _clazz.isInstance(obj);
	}
	
	public boolean isInterface(){
		return _clazz.isInterface();
	}
	
	public boolean isPrimitive() {
		return _clazz.isPrimitive();
	}
    
    public Object newInstance() {
		return constructorSpec().newInstance();
	}
	
	public Class<?> getJavaClass(){
		return _clazz;
	}
    
    public Reflector reflector() {
        return _reflector;
    }
    
    public ReflectConstructor getSerializableConstructor() {
    	Constructor<?> serializableConstructor = Platform4.jdk().serializableConstructor(_clazz);
		if (null == serializableConstructor) {
			return null;
		}
		return new JdkConstructor(_reflector, serializableConstructor);
    }

	public Object nullValue() {
		return _jdkReflector.nullValue(this);
	}
	
	private ReflectConstructorSpec constructorSpec() {
		if(_constructorSpec.canBeInstantiated().isUnspecified()) {
			_constructorSpec = ConstructorSupport.createConstructor(this, _clazz, _jdkReflector.configuration(), getDeclaredConstructors());
		}
		return _constructorSpec;
	}

	public boolean ensureCanBeInstantiated() {
		return constructorSpec().canBeInstantiated().definiteYes();
	}
}
