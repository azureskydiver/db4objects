/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class UTF8StringIO extends LatinStringIO{
	
	private static final UTF8Encoder ENCODER = new UTF8Encoder();
	
 	private String decode(byte[] bytes, int start ,int length){
 		try {
			return ENCODER.decode(bytes, start, length);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
 	}
 	
 	private byte[] encode(String str){
 		try {
			return ENCODER.encode(str);
		} catch (IOException e) {
			throw new Db4oIOException(e);
		}
 	}
 	
    public byte encodingByte(){
		return Const4.UTF8;
	}
    
 	public int length(String str){
 		return encode(str).length + Const4.OBJECT_LENGTH + Const4.INT_LENGTH;
 	}
 	
 	public String read(ReadBuffer buffer, int length){
 		byte[] bytes = new byte[length];
 		buffer.readBytes(bytes);
 		return decode(bytes, 0, bytes.length);
 	}
 	
 	public String read(byte[] bytes){
 		return decode(bytes, 0, bytes.length);
 	}
 	
 	public int shortLength(String str){
 		return encode(str).length + Const4.INT_LENGTH;
 	}
 	
 	public void write(WriteBuffer buffer, String str) {
 		buffer.writeBytes(encode(str));
 	}
 	
 	public byte[] write(String str){
 		return encode(str);
 	}
 	
	/**
	 * Note the different implementation when compared to LatinStringIO and UnicodeStringIO:
	 * Instead of writing the length of the string, UTF8StringIO writes the length of the 
	 * byte array.
	 */
 	public void writeLengthAndString(WriteBuffer buffer, String str){
	    if (str == null) {
	        buffer.writeInt(0);
	        return;
	    }
	    byte[] bytes = encode(str);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
	}

}
