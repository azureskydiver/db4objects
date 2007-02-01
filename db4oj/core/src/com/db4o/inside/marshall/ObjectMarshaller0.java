/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.inside.marshall;

import com.db4o.*;
import com.db4o.inside.*;
import com.db4o.inside.slots.*;

/**
 * @exclude
 */
class ObjectMarshaller0 extends ObjectMarshaller {
    
    public void addFieldIndices(final YapClass yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final Slot oldSlot) {
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(YapField field, boolean isNull, YapClass containingClass) {
	            field.addFieldIndex(_family, yc, writer, oldSlot);
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    }
    
    public TreeInt collectFieldIDs(TreeInt tree, YapClass yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, final String name) {
    	final TreeInt[] ret={tree};
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(YapField field, boolean isNull, YapClass containingClass) {
	            if (name.equals(field.getName())) {
	                ret[0] = field.collectIDs(_family, ret[0], writer);
	            } else {
	                field.incrementOffset(writer);
	            }
			}
    	};
    	traverseFields(yc, writer, attributes, command);
        return ret[0];
    }
    
    public void deleteMembers(YapClass yc, ObjectHeaderAttributes attributes, final StatefulBuffer writer, int type, final boolean isUpdate){
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(YapField field, boolean isNull, YapClass containingClass) {
	            field.delete(_family, writer, isUpdate);
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    }
    
    public boolean findOffset(YapClass yc, ObjectHeaderAttributes attributes, final Buffer writer, final YapField field) {
    	final boolean[] ret={false};
    	TraverseFieldCommand command=new TraverseFieldCommand() {
    		public void processField(YapField curField, boolean isNull, YapClass containingClass) {
	            if (curField == field) {
	                ret[0]=true;
	                cancel();
	                return;
	            }
	            writer.incrementOffset(curField.linkLength());
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    	return ret[0];
    }
    
    protected final int headerLength(){
        return YapConst.OBJECT_LENGTH + YapConst.ID_LENGTH;
    }
    
    public void instantiateFields(YapClass yc, ObjectHeaderAttributes attributes, final YapObject yapObject, final Object onObject, final StatefulBuffer writer) {
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(YapField field, boolean isNull, YapClass containingClass) {
                try {
					field.instantiate(_family, yapObject, onObject, writer);
				} catch (CorruptionException e) {
					cancel();
				}
			}
    	};
    	traverseFields(yc, writer, attributes, command);
    }
    
    private int linkLength(YapClass yc, YapObject yo) {
        int length = YapConst.INT_LENGTH;
        if (yc.i_fields != null) {
            for (int i = 0; i < yc.i_fields.length; i++) {
                length += linkLength(yc.i_fields[i], yo);
            }
        }
        if (yc.i_ancestor != null) {
            length += linkLength(yc.i_ancestor, yo);
        }
        return length;
    }
    
    protected int linkLength(YapField yf, YapObject yo){
        return yf.linkLength();
    }
    
    private void marshall(YapClass yapClass, YapObject a_yapObject, Object a_object, StatefulBuffer writer, boolean a_new) {
        marshallDeclaredFields(yapClass, a_yapObject, a_object, writer, a_new);
        if (Deploy.debug) {
            writer.writeEnd();
            writer.debugCheckBytes();
        }
    }
    
    private void marshallDeclaredFields(YapClass yapClass, final YapObject yapObject, final Object object, final StatefulBuffer writer, final boolean isNew) {
        final Config4Class config = yapClass.configOrAncestorConfig();
        final Transaction trans=writer.getTransaction();
    	TraverseFieldCommand command=new TraverseFieldCommand() {
    		public int fieldCount(YapClass yc, Buffer reader) {
    	        writer.writeInt(yc.i_fields.length);
    	        return yc.i_fields.length;
    		}
    		
			public void processField(YapField field, boolean isNull, YapClass containingClass) {
	            Object obj = field.getOrCreate(trans, object);
	            if (obj instanceof Db4oTypeImpl) {
	                obj = ((Db4oTypeImpl)obj).storedTo(trans);
	            }
	            field.marshall(yapObject, obj, _family, writer, config, isNew);
			}
    	};
    	traverseFields(yapClass, writer, readHeaderAttributes(writer), command);
    }
    
    protected int marshalledLength(YapField yf, YapObject yo){
        return 0;
    }

    public StatefulBuffer marshallNew(Transaction a_trans, YapObject yo, int a_updateDepth){
        
        StatefulBuffer writer = createWriterForNew(a_trans, yo, a_updateDepth, objectLength(yo));
        
        YapClass yc = yo.getYapClass();
        Object obj = yo.getObject();
        
        if(yc.isPrimitive()){
            ((YapClassPrimitive)yc).i_handler.writeNew(MarshallerFamily.current(), obj, false, writer, true, false);
            if (Deploy.debug) {
                writer.writeEnd();
                writer.debugCheckBytes();
            }
        }else{
            writeObjectClassID(writer,yc.getID());
            yc.checkUpdateDepth(writer);
            marshall(yc, yo, obj, writer, true);
        }
        return writer;
    }

    public void marshallUpdate(
        Transaction trans,
        int updateDepth,
        YapObject yapObject,
        Object obj
        ) {
        
        StatefulBuffer writer = createWriterForUpdate(trans,updateDepth, yapObject.getID(), 0, objectLength(yapObject));
        
        YapClass yapClass = yapObject.getYapClass();
        
        yapClass.checkUpdateDepth(writer);
        
        writer.writeInt(yapClass.getID());
        marshall(yapClass, yapObject, obj, writer, false);
        
        marshallUpdateWrite(trans, yapObject, obj, writer);
    }

    private int objectLength(YapObject yo) {
        return headerLength() + linkLength(yo.getYapClass(), yo);
    }

    public ObjectHeaderAttributes readHeaderAttributes(Buffer reader) {
        return null;
    }

    public Object readIndexEntry(YapClass yc, ObjectHeaderAttributes attributes, YapField yf, StatefulBuffer reader) {
        if(yc == null){
            return null;
        }
        
        if(! findOffset(yc, attributes, reader, yf)){
            return null;
        }
        
        return yf.readIndexEntry(_family, reader);
    }
    
    public void readVirtualAttributes(final Transaction trans,  YapClass yc, final YapObject yo, ObjectHeaderAttributes attributes, final Buffer reader){
    	TraverseFieldCommand command=new TraverseFieldCommand() {
			public void processField(YapField field, boolean isNull, YapClass containingClass) {
	            field.readVirtualAttribute(trans, reader, yo);
			}
    	};
    	traverseFields(yc, reader, attributes, command);
    }

    protected boolean isNull(ObjectHeaderAttributes attributes,int fieldIndex) {
    	return false;
    }

	public void defragFields(YapClass yapClass,ObjectHeader header, ReaderPair readers) {
	}

	public void writeObjectClassID(Buffer reader, int id) {
		reader.writeInt(id);
	}

	public void skipMarshallerInfo(Buffer reader) {
	}
}
