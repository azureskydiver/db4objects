package db4ounit;

import java.lang.reflect.*;
import java.util.Vector;

public class ReflectionTestSuiteBuilder implements TestSuiteBuilder {
	
	private Class[] _classes;

	public ReflectionTestSuiteBuilder(Class clazz) {
		if (null == clazz) throw new IllegalArgumentException("clazz");
		_classes = new Class[] { clazz };
	}
	
	public ReflectionTestSuiteBuilder(Class[] classes) {
		if (null == classes) throw new IllegalArgumentException("classes");
		_classes = classes;
	}
	
	public TestSuite build() {
		return (1 == _classes.length)
			? fromClass(_classes[0])
			: fromClasses(_classes);
	}
	
	protected TestSuite fromClasses(Class[] classes) {		
		TestSuite[] suites = new TestSuite[classes.length];
		for (int i = 0; i < classes.length; i++) {
			suites[i] = fromClass(classes[i]);
		}
		return new TestSuite(suites);
	}
	
	protected TestSuite fromClass(Class clazz) {		
		Object instance = newInstance(clazz);
		return fromInstance(instance);
	}

	private TestSuite fromInstance(Object instance) {
		if (instance instanceof TestSuiteBuilder) {
			return ((TestSuiteBuilder)instance).build();
		}
		final Class clazz = instance.getClass();
		
		if (instance instanceof Test) {
			return new TestSuite(clazz.getName(), new Test[] { (Test)instance });
		}
		if (!(instance instanceof TestCase)) {
			throw new IllegalArgumentException("" + clazz + " is not marked as " + TestCase.class);
		}
		Vector tests = new Vector();
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (!isTestMethod(method)) {
				emitWarningOnIgnoredTestMethod(instance, method);
				continue;			
			}
			tests.addElement(createTest(instance, method));
		}		
		return new TestSuite(clazz.getName(), toArray(tests));
	}
	
	private void emitWarningOnIgnoredTestMethod(Object subject, Method method) {
		if (!method.getName().startsWith("_test")) {
			return;
		}
		System.err.println("IGNORED: " + TestMethod.createLabel(subject, method));
	}

	protected boolean isTestMethod(Method method) {
		return method.getName().startsWith("test")			
			&& TestPlatform.isPublic(method)
			&& !TestPlatform.isStatic(method)
			&& !TestPlatform.hasParameters(method);
	}
	
	private static Test[] toArray(Vector tests) {
		Test[] array = new Test[tests.size()];
		tests.copyInto(array);
		return array;
	}

	protected Object newInstance(Class clazz) {		
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new TestException(e);
		}
	}
	
	protected Test createTest(Object instance, Method method) {
		return new TestMethod(instance, method);
	}	
}
