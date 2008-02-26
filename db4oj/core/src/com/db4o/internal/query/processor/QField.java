/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.types.*;


/**
 * @exclude
 */
public class QField implements Visitor4, Unversioned{
	
	transient Transaction i_trans;
	public String i_name;
	transient FieldMetadata i_yapField;
	public int i_yapClassID;
	public int i_index;
	
	public QField(){
		// C/S only	
	}
	
	public QField(Transaction a_trans, String name, FieldMetadata a_yapField, int a_yapClassID, int a_index){
		i_trans = a_trans;
		i_name = name;
		i_yapField = a_yapField;
		i_yapClassID = a_yapClassID;
		i_index = a_index;
		if(i_yapField != null){
		    if(! i_yapField.alive()){
		        i_yapField = null;
		    }
		}
	}
    
    boolean canHold(ReflectClass claxx){
        return i_yapField == null || i_yapField.canHold(claxx);
    }
	
	Object coerce(Object a_object){
	    ReflectClass claxx = null;
	    Reflector reflector = i_trans.reflector();
	    if(a_object != null){
	        if(a_object instanceof ReflectClass){
	            claxx = (ReflectClass)a_object;
	        }else{
	            claxx = reflector.forObject(a_object);
	        }
	    }else{
	        
	        // TODO: Review this line for NullableArrayHandling 
	        
			if(Deploy.csharp){
				return a_object;
			}
			
	    }
        if(i_yapField == null){
            return a_object;
        }
        return i_yapField.coerce(claxx, a_object);
	}
    
	
	ClassMetadata getYapClass(){
		if(i_yapField != null){
			return i_yapField.handlerClassMetadata(i_trans.container());
		}
		return null;
	}
	
	FieldMetadata getYapField(ClassMetadata yc){
		if(i_yapField != null){
			return i_yapField;
		}
		FieldMetadata yf = yc.fieldMetadataForName(i_name);
		if(yf != null){
		    yf.alive();
		}
		return yf;
	}
	
	public FieldMetadata getYapField() {
		return i_yapField;
	}
	
	boolean isArray(){
		return i_yapField != null && i_yapField.getHandler() instanceof ArrayHandler;
	}
	
	boolean isClass(){
		return i_yapField == null ||  Handlers4.handlesClass(i_yapField.getHandler());
	}
	
	boolean isSimple(){
		return i_yapField != null &&  Handlers4.handlesSimple(i_yapField.getHandler());
	}
	
	PreparedComparison prepareComparison(Context context, Object obj){
		if(i_yapField != null){
			return i_yapField.prepareComparison(context, obj);
		}
		if(obj == null){
			return Null.INSTANCE;
		}
		ClassMetadata yc = i_trans.container().produceClassMetadata(i_trans.reflector().forObject(obj));
		FieldMetadata yf = yc.fieldMetadataForName(i_name);
		if(yf != null){
			return yf.prepareComparison(context, obj);
		}
		return null;
	}

	
	void unmarshall(Transaction a_trans){
		if(i_yapClassID != 0){
			ClassMetadata yc = a_trans.container().classMetadataForId(i_yapClassID);
			i_yapField = yc.i_fields[i_index];
		}
	}
	
	public void visit(Object obj) {
		((QCandidate) obj).useField(this);
	}
	
	public String toString() {
		if(i_yapField != null){
			return "QField " + i_yapField.toString();
		}
		return super.toString();
	}
}

