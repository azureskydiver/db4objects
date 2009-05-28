/* Copyright (C) 2004   Versant Inc.   http://www.db4o.com */

package com.db4o.cs.internal.messages;

import com.db4o.internal.*;

public final class MReleaseSemaphore extends MsgD implements ServerSideMessage {
	public final boolean processAtServer() {
		String name = readString();
		((LocalObjectContainer)stream()).releaseSemaphore(transaction(),name);
		return true;
	}
}