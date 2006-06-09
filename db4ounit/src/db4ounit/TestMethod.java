package db4ounit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Reflection based db4ounit.Test implementation.
 */
public class TestMethod implements Test {
	
	private Object _subject;
	private Method _method;

	public TestMethod(Object instance, Method method) {
		if (null == instance) throw new IllegalArgumentException("instance");
		if (null == method) throw new IllegalArgumentException("method");		
		_subject = instance;
		_method = method;
	}
	
	public Object getSubject() {
		return _subject;
	}

	public String getLabel() {
		return _subject.getClass().getName() + "." + _method.getName();
	}

	public void run(TestResult result) {
		try {
			setUp();
			_method.invoke(_subject, new Object[0]);
		} catch (IllegalArgumentException e) {
			throw new TestException(e);
		} catch (IllegalAccessException e) {
			throw new TestException(e);
		} catch (InvocationTargetException e) {
			result.testFailed(this, e.getTargetException());
		} finally {
			tearDown();
		}
	}

	private void tearDown() {
		if (_subject instanceof TestLifeCycle) {
			try {
				((TestLifeCycle)_subject).tearDown();
			} catch (Exception e) {
				throw new TearDownFailureException(e);
			}
		}
	}

	private void setUp() {
		if (_subject instanceof TestLifeCycle) {
			try {
				((TestLifeCycle)_subject).setUp();
			} catch (Exception e) {
				throw new SetupFailureException(e);
			}
		}
	}
}
