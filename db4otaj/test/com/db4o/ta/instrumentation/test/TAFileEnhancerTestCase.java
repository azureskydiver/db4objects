package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.net.MalformedURLException;

import EDU.purdue.cs.bloat.editor.*;

import com.db4o.db4ounit.common.ta.MockActivator;
import com.db4o.foundation.io.*;
import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;
import com.db4o.internal.Reflection4;
import com.db4o.ta.*;
import com.db4o.ta.instrumentation.*;

import db4ounit.*;

public class TAFileEnhancerTestCase implements TestCase, TestLifeCycle {
    
	private final static Class INSTRUMENTED_CLAZZ = ToBeInstrumentedWithFieldAccess.class;
	
	private final static Class NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;

	private final static Class EXTERNAL_INSTRUMENTED_CLAZZ = ToBeInstrumentedWithExternalFieldAccess.class;

	private String srcDir;
	
	private String targetDir;	
	
	public void setUp() throws Exception {
		srcDir = IO.mkTempDir("tafileinstr/source");
		targetDir = IO.mkTempDir("tafileinstr/target");
		copyClassFilesTo(
			new Class[] { INSTRUMENTED_CLAZZ, NOT_INSTRUMENTED_CLAZZ, EXTERNAL_INSTRUMENTED_CLAZZ },
			srcDir);
	}
	
	public void test() throws Exception {
		
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		loader.assertAssignableFrom(Activatable.class, INSTRUMENTED_CLAZZ);
		loader.assertNotAssignableFrom(Activatable.class, NOT_INSTRUMENTED_CLAZZ);
	}

	private AssertingClassLoader newAssertingClassLoader()
			throws MalformedURLException {
		return new AssertingClassLoader(new File(targetDir), new Class[] { INSTRUMENTED_CLAZZ, NOT_INSTRUMENTED_CLAZZ, EXTERNAL_INSTRUMENTED_CLAZZ });
	}

	private void enhance() throws Exception {
		ClassFilter filter = new ByNameClassFilter(new String[]{ INSTRUMENTED_CLAZZ.getName(), EXTERNAL_INSTRUMENTED_CLAZZ.getName() });
		Db4oFileInstrumentor enhancer = new Db4oFileInstrumentor(new InjectTransparentActivationEdit(filter));
		enhancer.enhance(srcDir, targetDir, new String[]{});
	}
	
	public void testMethodInstrumentation() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		
		Activatable instrumented = (Activatable) loader.newInstance(INSTRUMENTED_CLAZZ);
		MockActivator activator = MockActivator.activatorFor(instrumented);
		Reflection4.invoke(instrumented, "setInt", Integer.TYPE, new Integer(42));
		Assert.areEqual(1, activator.writeCount());
		Assert.areEqual(0, activator.readCount());
	}
	
	public void testExternalFieldAccessInstrumentation() throws Exception {
		enhance();
		
		AssertingClassLoader loader = newAssertingClassLoader();
		
		Activatable server = (Activatable) loader.newInstance(INSTRUMENTED_CLAZZ);
		Object client = loader.newInstance(EXTERNAL_INSTRUMENTED_CLAZZ);
		MockActivator activator = MockActivator.activatorFor(server);
		Reflection4.invoke(client, "accessExternalField", server.getClass(), server);
		Assert.areEqual(1, activator.writeCount());
		Assert.areEqual(0, activator.readCount());
	}
	
	
	public void testExceptionsAreBubbledUp() throws Exception {
		
		final RuntimeException exception = new RuntimeException();
		
		final Throwable thrown = Assert.expect(RuntimeException.class, new CodeBlock() {
			public void run() throws Exception {
				new Db4oFileInstrumentor(new BloatClassEdit() {
					public InstrumentationStatus enhance(
							ClassEditor ce,
							ClassLoader origLoader,
							BloatLoaderContext loaderContext) {
						
						throw exception;
						
					}
				}).enhance(srcDir, targetDir, new String[] {});
			}
		});
		
		Assert.areSame(exception, thrown);
			
	}
	
	public void tearDown() throws Exception {
		Directory4.delete(srcDir, true);
		Directory4.delete(targetDir, true);
	}

	private void copyClassFilesTo(final Class[] classes, final String toDir)
			throws IOException {
		for (int i = 0; i < classes.length; i++) {
			copyClassFile(classes[i], toDir);
		}
	}	

	private void copyClassFile(Class clazz, String toDir) throws IOException {
		File file = ClassFiles.fileForClass(clazz);
		String targetPath = Path4.combine(toDir, ClassFiles.classNameAsPath(clazz));
		File4.delete(targetPath);
		File4.copy(file.getCanonicalPath(), targetPath);
	}
}
