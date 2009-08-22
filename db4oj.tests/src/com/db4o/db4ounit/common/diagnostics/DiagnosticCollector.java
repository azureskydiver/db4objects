/* Copyright (C) 2009 Versant Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.diagnostics;

import java.util.ArrayList;
import java.util.List;

import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;

public class DiagnosticCollector implements DiagnosticListener {

	private List<Diagnostic> _diagnostics = new ArrayList<Diagnostic>();

	public void onDiagnostic(Diagnostic d) {
		_diagnostics.add(d);
	}

	public List<Diagnostic> diagnostics() {
		return _diagnostics;
	}

}
