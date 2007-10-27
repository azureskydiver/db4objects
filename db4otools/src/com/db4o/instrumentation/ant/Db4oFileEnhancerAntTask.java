/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.instrumentation.ant;

import java.util.*;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

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
	private String _packagePredicate;
	private final List _editFactories = new ArrayList();

	public void add(ClassEditFactory editFactory) {
		_editFactories.add(editFactory);
	}
	
	public void addSources(FileSet fileSet) {
		_sources.add(fileSet);
	}
	
	public void setTargetdir(String targetDir) {
		_targetDir=targetDir;
	}

	public void addClasspath(Path path) {
		_classPath.add(path);
	}

	public void setPackagefilter(String packagePredicate) {
		_packagePredicate=packagePredicate;
	}

	public void execute() {
		List paths=new ArrayList();
		for (Iterator pathIter = _classPath.iterator(); pathIter.hasNext();) {
			Path path = (Path) pathIter.next();
			String[] curPaths=path.list();
			for (int curPathIdx = 0; curPathIdx < curPaths.length; curPathIdx++) {
				paths.add(curPaths[curPathIdx]);
			}
		}
		BloatClassEdit clazzEdit = null;
		switch(_editFactories.size()) {
			case 0:
				clazzEdit = new NullClassEdit();
				break;
			case 1:
				clazzEdit = ((ClassEditFactory)_editFactories.get(0)).createEdit();
				break;
			default:
				List classEdits = new ArrayList(_editFactories.size());
				for (Iterator factoryIter = _editFactories.iterator(); factoryIter.hasNext(); ) {
					ClassEditFactory curFactory = (ClassEditFactory) factoryIter.next();
					classEdits.add(curFactory.createEdit());
				}
				clazzEdit = new CompositeBloatClassEdit((BloatClassEdit[])classEdits.toArray(new BloatClassEdit[classEdits.size()]), true);
				
		}
		AntFileSetPathRoot root = new AntFileSetPathRoot((FileSet[])_sources.toArray(new FileSet[_sources.size()]));
		try {
			new Db4oFileEnhancer(clazzEdit).enhance(root,_targetDir,(String[])paths.toArray(new String[paths.size()]),(_packagePredicate==null ? "" : _packagePredicate));
		} catch (Exception exc) {
			throw new BuildException(exc);
		}
	}
}
