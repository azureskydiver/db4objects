/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.foundation;

/**
 * A context variable is a value associated to a specific thread and scope.
 * 
 * The value is brought into scope with the {@link #with} method.
 * 
 */
public class ContextVariable {
	
	private static class ThreadSlot {
		public final Thread thread;
		public final Object value;
		
		public ThreadSlot(Object value_) {
			thread = Thread.currentThread();
			value = value_;
		}
	}
	
	private final Class _expectedType;
	private final Collection4 _values = new Collection4();
	
	public ContextVariable() {
		this(null);
	}
	
	public ContextVariable(Class expectedType) {
		_expectedType = expectedType;
	}
	
	/**
	 * @sharpen.property
	 */
	public Object value() {
		final Thread current = Thread.currentThread();
		synchronized (this) {
			final Iterator4 iterator = _values.iterator();
			while (iterator.moveNext()) {
				ThreadSlot slot = (ThreadSlot)iterator.current();
				if (slot.thread == current) {
					return slot.value;
				}
			}
		}
		return null;
	}
	
	public void with(Object value, Runnable block) {
		validate(value);
		
		ThreadSlot slot = pushValue(value);
		try {
			block.run();
		} finally {
			popValue(slot);
		}
	}

	private void validate(Object value) {
		if (value == null || _expectedType == null) {
			return;
		}
		if (_expectedType.isInstance(value)) {
			return;
		}
		throw new IllegalArgumentException("Expecting instance of '" + _expectedType + "' but got '" + value + "'");
	}

	private synchronized void popValue(ThreadSlot slot) {
		_values.remove(slot);
	}

	private ThreadSlot pushValue(Object value) {
		ThreadSlot slot = new ThreadSlot(value);
		synchronized (this) {
			_values.prepend(slot);
		}
		return slot;
	}
}
