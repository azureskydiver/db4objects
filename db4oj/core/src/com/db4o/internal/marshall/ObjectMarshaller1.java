/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class ObjectMarshaller1 extends ObjectMarshaller{

    public void addFieldIndices(final ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final Slot oldSlot) {
		TraverseFieldCommand command = new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if (isNull) {
					field.addIndexEntry(writer.getTransaction(), writer.getID(), null);
				} 
				else {
					field.addFieldIndex(_family, yc, writer, oldSlot);
				}
			}
		};
		traverseFields(yc, writer, attributes, command);
	}
    
    public TreeInt collectFieldIDs(TreeInt tree, ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final String name) {
        final TreeInt[] ret={tree};
		TraverseFieldCommand command = new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if(isNull) {
					return;
				}
		        if (name.equals(field.getName())) {
		            ret[0] = field.collectIDs(_family, ret[0], writer);
		        } 
		        else {
		        	field.incrementOffset(writer);
		        }
			}
		};
		traverseFields(yc, writer, attributes, command);
		return ret[0];
    }

    public void deleteMembers(ClassMetadata yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, int type, final boolean isUpdate){
        TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
		        if(isNull){
		            field.removeIndexEntry(writer.getTransaction(), writer.getID(), null);
		        }else{
		            field.delete(_family, writer, isUpdate);
		        }
			}
		};
		traverseFields(yc, writer, attributes, command);
    }

    public boolean findOffset(ClassMetadata yc, FieldListInfo fieldListInfo, final BufferImpl reader, final FieldMetadata field) {
        final boolean[] ret={false};
		TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(FieldMetadata curField, boolean isNull, ClassMetadata containingClass) {
		        if (curField == field) {
		        	ret[0]=!isNull;
		        	cancel();
		        	return;
		        }
		        if(!isNull){
		            curField.incrementOffset(reader);
		        }
			}
		};
		traverseFields(yc, reader, fieldListInfo, command);
		return ret[0];
    }
    
    public ObjectHeaderAttributes readHeaderAttributes(BufferImpl reader) {
        return new ObjectHeaderAttributes(reader);
    }
    
    public Object readIndexEntry(ClassMetadata clazz, ObjectHeaderAttributes attributes, FieldMetadata field, StatefulBuffer reader) throws FieldIndexException {
        if(clazz == null){
            return null;
        }
        
        if(! findOffset(clazz, attributes, reader, field)){
            return null;
        }
        
        try {
			return field.readIndexEntry(_family, reader);
		} catch (CorruptionException exc) {
			throw new FieldIndexException(exc,field);
		} 
    }
    
    public void readVirtualAttributes(final Transaction trans, ClassMetadata yc, final ObjectReference yo, ObjectHeaderAttributes attributes, final BufferImpl reader) {
		TraverseFieldCommand command = new TraverseFieldCommand() {
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if (!isNull) {
					field.readVirtualAttribute(trans, reader, yo);
				}
			}
		};
		traverseFields(yc, reader, attributes, command);
	}

    protected boolean isNull(FieldListInfo fieldList,int fieldIndex) {
    	return fieldList.isNull(fieldIndex);
    }

	public void defragFields(ClassMetadata clazz,ObjectHeader header, final DefragmentContextImpl context) {
        TraverseFieldCommand command = new TraverseFieldCommand() {
        	
        	public int fieldCount(ClassMetadata yapClass, BufferImpl reader) {
        		return context.readInt();
        	}
        	
			public void processField(FieldMetadata field, boolean isNull, ClassMetadata containingClass) {
				if (!isNull) {
					field.defragField(_family,context);
				} 
			}
		};
		traverseFields(clazz, null, header._headerAttributes, command);
	}

	public void writeObjectClassID(BufferImpl reader, int id) {
		reader.writeInt(-id);
	}

	public void skipMarshallerInfo(BufferImpl reader) {
		reader.incrementOffset(1);
	}

	
}
