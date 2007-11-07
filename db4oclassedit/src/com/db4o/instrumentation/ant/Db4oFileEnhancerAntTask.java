/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.ant;

import java.io.*;
import java.util.*;
import java.util.jar.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.types.resources.*;

import com.db4o.instrumentation.classfilter.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.main.*;

/**
 * Ant task for build time class file instrumentation.
 * 
 * @see BloatClassEdit
 */
public class Db4oFileEnhancerAntTask extends Task {
	private final List _sources = new ArrayList();
	private String _targetDir;
	private final List _classPath = new ArrayList();
	private final List _editFactories = new ArrayList();
	private final List _jars = new ArrayList();
	private String _jarTargetDir;

	public void add(AntClassEditFactory editFactory) {
		_editFactories.add(editFactory);
	}
	
	public void addSources(FileSet fileSet) {
		_sources.add(fileSet);
	}

	public void addJars(FileSet fileSet) {
		_jars.add(fileSet);
	}

	public void setTargetdir(String targetDir) {
		_targetDir=targetDir;
	}

	public void setJarTargetdir(String targetDir) {
		_jarTargetDir=targetDir;
	}

	public void addClasspath(Path path) {
		_classPath.add(path);
	}

	private static interface FileResourceBlock {
		void process(FileResource resource) throws Exception;
	}
	
	public void execute() {
		try {
			FileSet[] sourceArr = prepareFileSets(_sources);
			AntFileSetPathRoot root = new AntFileSetPathRoot(sourceArr);
			ClassFilter filter = collectClassFilters(root);
			BloatClassEdit clazzEdit = collectClassEdits(filter);
			final String[] classPath = collectClassPathArray();

			enhanceClassFiles(root, clazzEdit, classPath);
			enhanceJars(clazzEdit, classPath);
		} catch (Exception exc) {
			throw new BuildException(exc);
		}
	}

	private String[] collectClassPathArray() {
		List paths=new ArrayList();
		for (Iterator pathIter = _classPath.iterator(); pathIter.hasNext();) {
			Path path = (Path) pathIter.next();
			String[] curPaths=path.list();
			for (int curPathIdx = 0; curPathIdx < curPaths.length; curPathIdx++) {
				paths.add(curPaths[curPathIdx]);
			}
		}
		return (String[]) paths.toArray(new String[paths.size()]);
	}

	private void enhanceClassFiles(AntFileSetPathRoot root,
			BloatClassEdit clazzEdit, final String[] classPath)
			throws Exception {
		new Db4oFileInstrumentor(clazzEdit).enhance(root, _targetDir, classPath);
	}

	private void enhanceJars(BloatClassEdit clazzEdit, final String[] classPath)
			throws Exception {
		final Db4oJarEnhancer jarEnhancer = new Db4oJarEnhancer(clazzEdit);
		forEachJar(new FileResourceBlock() {
			public void process(FileResource resource) throws Exception {
				File targetJarFile = new File(_jarTargetDir, resource.getFile().getName());
				jarEnhancer.enhance(resource.getFile(), targetJarFile, classPath);
			}
		});
	}

	private ClassFilter collectClassFilters(AntFileSetPathRoot root) throws Exception {
		final List filters = new ArrayList();
		filters.add(root);
		forEachJar(new FileResourceBlock() {
			public void process(FileResource resource) throws IOException {
				JarFile jarFile = new JarFile(resource.getFile());
				filters.add(new JarFileClassFilter(jarFile));
			}
			
		});

		ClassFilter filter = new CompositeOrClassFilter((ClassFilter[]) filters.toArray(new ClassFilter[filters.size()]));
		return filter;
	}

	private void forEachJar(FileResourceBlock collectFiltersBlock) throws Exception {
		for (Iterator jarSetIter = _jars.iterator(); jarSetIter.hasNext();) {
			FileSet jarSet = (FileSet) jarSetIter.next();
			for (Iterator jarIter = jarSet.iterator(); jarIter.hasNext();) {
				FileResource jarResource = (FileResource) jarIter.next();
				collectFiltersBlock.process(jarResource);
				
			}
		}
	}

	private BloatClassEdit collectClassEdits(ClassFilter classFilter) {
		BloatClassEdit clazzEdit = null;
		switch(_editFactories.size()) {
			case 0:
				clazzEdit = new NullClassEdit();
				break;
			case 1:
				clazzEdit = ((AntClassEditFactory)_editFactories.get(0)).createEdit(classFilter);
				break;
			default:
				List classEdits = new ArrayList(_editFactories.size());
				for (Iterator factoryIter = _editFactories.iterator(); factoryIter.hasNext(); ) {
					AntClassEditFactory curFactory = (AntClassEditFactory) factoryIter.next();
					classEdits.add(curFactory.createEdit(classFilter));
				}
				clazzEdit = new CompositeBloatClassEdit((BloatClassEdit[])classEdits.toArray(new BloatClassEdit[classEdits.size()]), true);
				
		}
		return clazzEdit;
	}

	private FileSet[] prepareFileSets(List fileSets) {
		for (Iterator sourceIter = fileSets.iterator(); sourceIter.hasNext();) {
			FileSet fileSet = (FileSet) sourceIter.next();
			fileSet.appendIncludes(new String[]{"**/*.class"});
		}
		return (FileSet[]) fileSets.toArray(new FileSet[fileSets.size()]);
	}
}
