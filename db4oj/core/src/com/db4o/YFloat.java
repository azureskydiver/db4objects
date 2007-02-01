/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.reflect.ReflectClass;


final class YFloat extends YInt {
    
    private static final Float i_primitive = new Float(0);
    
    public YFloat(YapStream stream) {
        super(stream);
    }
    
    public Object coerce(ReflectClass claxx, Object obj) {
    	return Coercion4.toFloat(obj);
    }

	public Object defaultValue(){
		return i_primitive;
	}
	
	public int getID() {
		return 3;
	}

	protected Class primitiveJavaClass() {
		return float.class;
	}

	Object primitiveNull() {
		return i_primitive;
	}

	Object read1(Buffer a_bytes) {
		return new Float(Float.intBitsToFloat(a_bytes.readInt()));
	}

	public void write(Object a_object, Buffer a_bytes) {
		writeInt(
			Float.floatToIntBits(((Float) a_object).floatValue()),
			a_bytes);
	}

	// Comparison_______________________

	private float i_compareTo;

	private float valu(Object obj) {
		return ((Float) obj).floatValue();
	}

	void prepareComparison1(Object obj) {
		i_compareTo = valu(obj);
	}
    
    public Object current1(){
        return new Float(i_compareTo);
    }

	boolean isEqual1(Object obj) {
		return obj instanceof Float && valu(obj) == i_compareTo;
	}

	boolean isGreater1(Object obj) {
		return obj instanceof Float && valu(obj) > i_compareTo;
	}

	boolean isSmaller1(Object obj) {
		return obj instanceof Float && valu(obj) < i_compareTo;
	}

}
