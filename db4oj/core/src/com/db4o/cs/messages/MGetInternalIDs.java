/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.cs.messages;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.inside.*;

public final class MGetInternalIDs extends MsgD {
	public final boolean processAtServer(YapServerThread serverThread) {
		Buffer bytes = this.getByteLoad();
		long[] ids;
		synchronized (streamLock()) {
			try {
				ids = stream().getYapClass(bytes.readInt()).getIDs(transaction());
			} catch (Exception e) {
				ids = new long[0];
			}
		}
		int size = ids.length;
		MsgD message = Msg.ID_LIST.getWriterForLength(transaction(), YapConst.ID_LENGTH * (size + 1));
		Buffer writer = message.payLoad();
		writer.writeInt(size);
		for (int i = 0; i < size; i++) {
			writer.writeInt((int) ids[i]);
		}
		serverThread.write(message);
		return true;
	}
}