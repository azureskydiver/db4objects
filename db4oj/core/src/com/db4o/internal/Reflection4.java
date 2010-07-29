/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.lang.reflect.*;

/**
 * @exclude
 * 
 * Use the methods in this class for system classes only, since they 
 * are not ClassLoader or Reflector-aware.
 * 
 * TODO: this class should go to foundation.reflect, along with ReflectException and ReflectPlatform
 */
public class Reflection4 {
	
	public static Object invokeStatic(Class clazz, String methodName) {
		return invoke(clazz, methodName, null, null, null);
	}
    
    public static Object invoke (Object obj, String methodName) throws ReflectException {
        return invoke(obj.getClass(), methodName, null, null, obj );
    }
    
    public static Object invoke (Object obj, String methodName, Object[] params) throws ReflectException {
        Class[] paramClasses = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            paramClasses[i] = params[i].getClass();
        }
        return invoke(obj.getClass(), methodName, paramClasses, params, obj );
    }

    public static Object invoke (Object obj, String methodName, Class[] paramClasses, Object[] params) throws ReflectException {
        return invoke(obj.getClass(), methodName, paramClasses, params, obj );
    }
    
    public static Object invoke (Class clazz, String methodName, Class[] paramClasses, Object[] params) throws ReflectException {
        return invoke(clazz, methodName, paramClasses, params, null);
    }
    
    private static Object invoke(Class clazz, String methodName, Class[] paramClasses, Object[] params, Object onObject) {
    	return invoke(params, onObject, getMethod(clazz, methodName, paramClasses));
	}

	public static Object invoke(String className, String methodName,
            Class[] paramClasses, Object[] params, Object onObject) throws ReflectException {
        Method method = getMethod(className, methodName, paramClasses);
        return invoke(params, onObject, method);
    }
    
    public static Object invoke(Object[] params, Object onObject, Method method) throws ReflectException {
        if(method == null) {
            return null;
        }
        Platform4.setAccessible(method);
        try {
            return method.invoke(onObject, params);
        } catch (InvocationTargetException e) {
            throw new ReflectException(e.getTargetException());
        } catch (IllegalArgumentException e) {
            throw new ReflectException(e);
        } catch (IllegalAccessException e) {
            throw new ReflectException(e);          
        } 
    }

    /**
     * calling this method "method" will break C# conversion with the old converter
     */
    public static Method getMethod(String className, String methodName,
            Class[] paramClasses) {
        Class clazz = ReflectPlatform.forName(className);
        if (clazz == null) {
            return null;
        }
        return getMethod(clazz, methodName, paramClasses);
    }

	public static Method getMethod(Class clazz, String methodName,
			Class[] paramClasses) {
		Class curclazz=clazz;
		while(curclazz!=null) {
			try {
				return curclazz.getDeclaredMethod(methodName, paramClasses);
			} catch (Exception e) {
			}
			curclazz=curclazz.getSuperclass();
		}
		return null;
	}

	public static Object invoke(final Object obj, String methodName,
			Class signature, Object value) throws ReflectException {
		return invoke(obj, methodName, new Class[] { signature }, new Object[] { value });
	}

	public static Field getField(final Class clazz,final String name) {
		Class curclazz=clazz;
		while(curclazz!=null) {
			try {
				Field field=curclazz.getDeclaredField(name);
				Platform4.setAccessible(field);
				return field;
			} catch (Exception e) {
				
			}
			curclazz=curclazz.getSuperclass();
		}
		return null;
	}

	public static Object getFieldValue(final Object obj, final String fieldName)
		throws ReflectException {
		try {
			return getField(obj.getClass(), fieldName).get(obj);
		} catch (Exception e) {
			throw new ReflectException(e);
		}
	}

    public static Object newInstance(Object template) {
        try {
            return template.getClass().newInstance();
        } catch (Exception e) {
            throw new ReflectException(e);
        }
    }
    
    public static String dump(Object obj){
    	if(obj == null){
    		return "null";
    	}
    	Class clazz = obj.getClass();
    	StringBuffer sb = new StringBuffer();
    	sb.append(clazz.getName());
    	Field[] fields = clazz.getDeclaredFields();
    	for (Field field : fields) {
    		sb.append("\n");
    		Platform4.setAccessible(field);
    		sb.append("\t");
			sb.append(field.getName());
			sb.append(": ");
			try {
				sb.append(field.get(obj));
			} catch (Exception e) {
				sb.append("Exception caught: ");
				sb.append(e);
			} 
		}
    	return sb.toString();
    }
    
}
