package com.db4o.nativequery.optimization;

import java.lang.reflect.*;

import com.db4o.*;

public class ReflectUtil {
	public static Method methodFor(Class clazz, String methodName, String[] paramTypeNames) {
		Class[] paramTypes=new Class[paramTypeNames.length];
		for (int paramIdx = 0; paramIdx < paramTypeNames.length; paramIdx++) {
			try {
				paramTypes[paramIdx]=Class.forName(paramTypeNames[paramIdx]);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return methodFor(clazz, methodName, paramTypes);
	}

	public static Method methodFor(Class clazz, String methodName, Class[] paramTypes) {
		Class curclazz=clazz;
		while(curclazz!=null) {
			try {
				Method method=curclazz.getDeclaredMethod(methodName, paramTypes);
				Platform4.setAccessible(method);
				return method;
			} catch (Exception e) {
			}
			curclazz=curclazz.getSuperclass();
		}
		return null;
	}

	public static Field fieldFor(final Class clazz,final String name) {
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

}
