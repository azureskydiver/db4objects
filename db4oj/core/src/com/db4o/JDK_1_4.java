/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import java.io.*;
import java.util.*;

import com.db4o.ext.*;

class JDK_1_4 extends JDK_1_3 {
	
	private Hashtable fileLocks;
	
	synchronized void lock(RandomAccessFile file) {
		Object channel = Reflection4.invoke(file, "getChannel", null, null);
		Object fl = Reflection4.invoke(channel, "tryLock", null, null); 
		if(fl == null){
			throw new DatabaseFileLockedException();
		}
		if(fileLocks == null){
			fileLocks = new Hashtable();
		}
		fileLocks.put(file, fl);
	}
	
	synchronized void unlock(RandomAccessFile file) {
		if(fileLocks != null){
			Object fl = fileLocks.get(file);
			if(fl != null){
			    Reflection4.invoke(fl, "release", null, null); 
				fileLocks.remove(file);
			}
		}
	}
	
	public int ver(){
	    return 4;
	}
	
}
