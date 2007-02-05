package com.db4o.objectmanager.api.helpers;

import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.Reflector;
import com.db4o.reflect.generic.GenericObject;
import com.db4o.ext.StoredClass;
import com.db4o.ext.StoredField;
import com.db4o.ObjectContainer;

import java.util.*;
import java.text.Collator;

/**
 * <p/>
 * Named ReflectHelper2 so it doesn't conflict with the one in db4o-sql
 * </p>
 * <p/>
 * User: treeder
 * Date: Aug 21, 2006
 * Time: 7:02:15 PMd
 */
public class ReflectHelper2 {

	/**
	 * <p/>
	 * Returns all the classes that the user would have stored.  Ignores core java classes and db4o classes.
	 * </p>
	 *
	 * @param container
	 * @return List<ReflectClass>
	 */
	public static List getUserStoredClasses(ObjectContainer container) {
		String[] ignore = new String[]{
				"java.lang.",
				"java.util.",
				"java.math.",
				"System.",
				"j4o.lang.AssemblyNameHint",
				"com.db4o.",
				/*"com.db4o.ext",
				"com.db4o.config",
				"com.db4o.StaticClass",
				"com.db4o.StaticField",*/
				"Db4objects.Db4o",
		};

		// Get the known classes
		StoredClass[] knownClasses = container.ext().storedClasses();
		Reflector reflector = container.ext().reflector();

		// Filter them
		List filteredList = new ArrayList();
		for (int i = 0; i < knownClasses.length; i++) {
			StoredClass sc = knownClasses[i];
			ReflectClass knownClass = reflector.forName(sc.getName());
			/*
			this is null for someone in the forums.
			if(knownClass == null){
				// STRANGE???
				System.out.println("knownClass: " + knownClass + " for " + sc.getName());
				continue;
			}*/
			if (knownClass.isArray() || knownClass.isPrimitive()) {
				continue;
			}
			boolean take = true;
			for (int j = 0; j < ignore.length; j++) {
				if (knownClass.getName().startsWith(ignore[j])) {
					take = false;
					break;
				}
			}
			if (!take) {
				continue;
			}
			filteredList.add(knownClass);
		}

		// Sort them
		Collections.sort(filteredList, new Comparator() {
			private Collator comparator = Collator.getInstance();

			public int compare(Object arg0, Object arg1) {
				ReflectClass class0 = (ReflectClass) arg0;
				ReflectClass class1 = (ReflectClass) arg1;

				return comparator.compare(class0.getName(), class1.getName());
			}
		});

		return filteredList;
	}

	/**
	 * <p/>
	 * Equivalent to isLeaf, isEditable, isSortaPrimitive... ;)
	 * </p>
	 *
	 * @param c
	 * @return
	 */
	public static boolean isEditable(Class c) {
		return c.isPrimitive()
				|| String.class.isAssignableFrom(c)
				|| Number.class.isAssignableFrom(c)
				|| Date.class.isAssignableFrom(c)
				|| Boolean.class.isAssignableFrom(c)
				|| Character.class.isAssignableFrom(c);
	}

	public static boolean isPrimitiveArray(Object ob) {
		if(!ob.getClass().isArray()) return false;
		Class objectsType = ob.getClass().getComponentType();
		return (objectsType == Boolean.TYPE
				|| objectsType == Byte.TYPE
				|| objectsType == Character.TYPE
				|| objectsType == Short.TYPE
				|| objectsType == Integer.TYPE
				|| objectsType == Long.TYPE
				|| objectsType == Float.TYPE
				|| objectsType == Double.TYPE);
	}

	public static boolean isIndexable(StoredField storedField) {
		ReflectClass storedType = storedField.getStoredType();
		if(storedType != null) { // primitive arrays return null
			//System.out.println("storedType: " + storedType + ", isprim: " + storedType.isPrimitive() + ", " + storedType.isSecondClass());
			if(storedType.isPrimitive() || storedType.isSecondClass()){
				// this appears to include the ones you'd expect: Date, String, primitives, and primitive wrappers.
				return true;
			}
		}
		return false;
	}
}
