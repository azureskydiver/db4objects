package com.db4o.ta.tests;import java.util.Date;import com.db4o.ObjectContainer;import com.db4o.ta.Activatable;import com.db4o.ta.internal.Activator;class UnitOfWork /* TA BEGIN */ implements Activatable /* TA END */ {	Date _started;	Date _finished;	String _name;		// TA BEGIN	transient Activator _activator;	// TA END		public UnitOfWork(String name, Date started, Date finished) {		_name = name;		_started = started;		_finished = finished;	}	public String getName() {		// TA BEGIN		activate();		// TA END		return _name;	}		// TA BEGIN	public void bind(ObjectContainer container) {		if (null != _activator) {			_activator.assertCompatible(container);			return;		}		_activator = new Activator(container, this);	}		private void activate() {		if (_activator == null) return;		_activator.activate();	}	// TA END	public long timeSpent() {		// TA BEGIN		activate();		// TA END		return _finished.getTime() - _started.getTime();	}}