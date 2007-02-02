/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.internal.*;
import com.db4o.internal.cs.*;

public final class MSetSemaphore extends MsgD {
	
	public final boolean processAtServer(ServerMessageDispatcher serverThread) {
		int timeout = readInt();
		String name = readString();
		LocalObjectContainer stream = (LocalObjectContainer)stream();
		boolean res = stream.setSemaphore(transaction(), name, timeout);
		if(res){
			serverThread.write(Msg.SUCCESS);
		}else{
			serverThread.write(Msg.FAILED);
		}
		return true;
	}
}