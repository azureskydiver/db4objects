/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

class YapReferenceQueue extends java.lang.ref.ReferenceQueue
{
	YapRef yapPoll(){
		return (YapRef)super.poll();
	}
	
}
