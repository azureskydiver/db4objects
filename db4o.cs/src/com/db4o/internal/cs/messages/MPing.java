/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.cs.messages;



/**
 * @exclude
 */
public class MPing extends Msg implements ServerSideMessage {

	public boolean processAtServer() {
	    write(Msg.PONG);
		return true;
	}
	
}
