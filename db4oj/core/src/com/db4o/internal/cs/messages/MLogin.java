/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;

import com.db4o.*;
import com.db4o.internal.cs.*;

/**
 * @exclude
 */
public class MLogin extends MsgD implements ServerSideMessage {

	public boolean processAtServer() {
		String userName = readString();
		String password = readString();
		ObjectServerImpl server = serverMessageDispatcher().server();
		User found = server.getUser(userName);
		if (found != null) {
			if (found.password.equals(password)) {
				serverMessageDispatcher().setDispatcherName(userName);
				serverMessageDispatcher().login();
				logMsg(32, userName);
				int blockSize = stream().blockSize();
				int encrypt = stream()._handlers.i_encrypt ? 1 : 0;
				write(Msg.LOGIN_OK.getWriterForInts(transaction(), new int[] { blockSize, encrypt}));
				return true;
			}
		}
		write(Msg.FAILED);
		return true;
	}

}
