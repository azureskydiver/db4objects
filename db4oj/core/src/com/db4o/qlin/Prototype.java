/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.qlin;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

/**
 * Internal implementation for QLinSupport mapping
 * of expressions to fields.
 * @exclude
 */
public class Prototype <T> {
	
	private final IdentityHashtable4 _fieldsByIdentity = new IdentityHashtable4();
	
	private final Hashtable4 _fieldsByIntId = new Hashtable4();
	
	private final T _object;
	
	private int intIdGenerator;
	
	private Prototype(T obj){
		_object = obj;
	}
	
	public T object(){
		return _object;
	}

	public static <T> Prototype<T> forContext(QLinContext context){
		ReflectClass claxx = context.classReflector();
		T obj = (T) claxx.newInstance();
		final Prototype<T> prototype = new Prototype<T>(obj);
		prototype.init(context, claxx);
		return prototype;
	}

	private void init(final QLinContext context, final ReflectClass claxx) {
		Reflections.forEachField(claxx, new Procedure4<ReflectField>() {
			public void apply(ReflectField field) {
				if(field.isStatic()){
					return;
				}
				ReflectClass fieldType = field.getFieldType();
				
				IntegerConverter converter = integerConverterforClassName(context, fieldType.getName());
				if(converter != null){
					int id = ++intIdGenerator;
					Object integerRepresentation = converter.fromInteger(id);
					field.set(_object, integerRepresentation);
					_fieldsByIntId.put(id, Pair.of(integerRepresentation, field.getName()));
					return;
				}
				if(! fieldType.isPrimitive()){
					Object identityInstance = fieldType.newInstance();
					if(identityInstance == null){
						return;
					}
					field.set(_object, identityInstance);
					_fieldsByIdentity.put(identityInstance, field.getName());
					return;
				}
			}
		});
	}
	
	public String matchToFieldName(QLinContext context, Object expression) {
		if(expression == null){
			return null;
		}
		ReflectClass claxx = context.reflector.forObject(expression);
		if(claxx == null){
			return null;
		}
		
		IntegerConverter converter = integerConverterforClassName(context, claxx.getName());
		if(converter != null){
			final Pair<?, String> mappedPair = (Pair)_fieldsByIntId.get(converter.toInteger(expression));
			if(mappedPair == null){
				return null;
			}
			Object mappedRepresenation = mappedPair.first;
			if(! mappedRepresenation.equals(expression)){
				return null;
			}
			return mappedPair.second;
		}
		if(claxx.isPrimitive()){
			return null;
		}
		return (String)_fieldsByIdentity.get(expression);
	}
	
	private static IntegerConverter integerConverterforClassName(QLinContext context, String className){
		if(_converters == null){
			_converters = new Hashtable4();
			IntegerConverter[] converters = new IntegerConverter[]{
				new IntegerConverter(){
					public String primitiveName() {return int.class.getName();}
					public Object fromInteger(int i) {return new Integer(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return long.class.getName();}
					public Object fromInteger(int i) {return new Long(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return double.class.getName();}
					public Object fromInteger(int i) {return new Double(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return float.class.getName();}
					public Object fromInteger(int i) {return new Float(i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return byte.class.getName();}
					public Object fromInteger(int i) {return new Byte((byte)i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return char.class.getName();}
					public Object fromInteger(int i) {return new Character((char)i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return short.class.getName();}
					public Object fromInteger(int i) {return new Short((short)i);}
				},
				new IntegerConverter(){
					public String primitiveName() {return String.class.getName();}
					public Object fromInteger(int i) {return STRING_IDENTIFIER + i;}
					@Override
					public int toInteger(Object obj) {
						if(! (obj instanceof String)){
							return -1;
						}
						String str = (String)obj;
						if(str.length() < STRING_IDENTIFIER.length()){
							return -1;
						}
						return Integer.parseInt(str.substring(STRING_IDENTIFIER.length()));
					}
				},
			};
			for (IntegerConverter converter : converters) {
				_converters.put(converter.primitiveName(), converter);
				if(! converter.primitiveName().equals(converter.wrapperName(context))){
					_converters.put(converter.wrapperName(context), converter);
				}
			}
		}
		return (IntegerConverter) _converters.get(className);
	}
	
	private static Hashtable4 _converters;
	
	private static abstract class IntegerConverter <T> {
		
		public String wrapperName(QLinContext context){
			return context.reflector.forObject(fromInteger(1)).getName();
		}
		
		public abstract String primitiveName();
		
		public abstract T fromInteger(int i);

		public int toInteger(T obj){
			return Integer.parseInt(obj.toString());
		}
		
	}
	
	private static final String STRING_IDENTIFIER = "QLinIdentity"; 

}
