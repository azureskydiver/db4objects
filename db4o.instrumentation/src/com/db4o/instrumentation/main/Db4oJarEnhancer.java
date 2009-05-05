/* Copyright (C) 2004 - 2006 Versant Inc. http://www.db4o.com */

package com.db4o.instrumentation.main;

import java.io.*;

import com.db4o.foundation.io.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;

/**
 * Enhances classes stored in jar files keeping the jar structure
 * untouched.
 */
public class Db4oJarEnhancer {
	
	private final Db4oFileInstrumentor _fileEnhancer;

	public Db4oJarEnhancer(BloatClassEdit classEdit) {
		_fileEnhancer = new Db4oFileInstrumentor(classEdit);
	}

	public void enhance(File inputJar, File outputJar, String[] classPath) throws Exception {
		final String workingDir = tempDir(inputJar.getName());
		try {
			extractJarTo(inputJar, workingDir);
			enhance(workingDir, classPath);
			makeJarFromDirectory(workingDir, outputJar);
		} finally {
			deleteDirectory(workingDir);
		}
	}

	private void deleteDirectory(String workingDir) {
		Directory4.delete(workingDir, true);
	}

	private void enhance(String workingDir, String[] classPath) throws Exception  {
		_fileEnhancer.enhance(workingDir, workingDir, classPath);
	}

	private String tempDir(String name) {
		return Path4.combine(Path4.getTempPath(), name + "-working");
	}

	private void extractJarTo(File inputJar, String workingDir) throws IOException {
		new ZipFileExtraction(inputJar, workingDir);
	}
	
	private void makeJarFromDirectory(String workingDir, File outputJar) throws IOException {
		new ZipFileCreation(workingDir, outputJar);
	}
}
