/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class Handlers4 {

    public static final int INT_ID = 1;
    
    public static final int LONG_ID = 2;
    
    public static final int FLOAT_ID = 3;
    
    public static final int BOOLEAN_ID = 4;
    
    public static final int DOUBLE_ID = 5;
    
    public static final int BYTE_ID = 6;
    
    public static final int CHAR_ID = 7;
    
    public static final int SHORT_ID = 8;
    
    public static final int STRING_ID = 9;
    
    public static final int DATE_ID = 10;
    
    public static final int UNTYPED_ID = 11;
    
    public static final int ANY_ARRAY_ID = 12;
    
    public static final int ANY_ARRAY_N_ID = 13;
    
    public static boolean handlerCanHold(TypeHandler4 handler, ReflectClass claxx){
    	return handler.canHold(claxx);
    }
    
    public static boolean handlesSimple(TypeHandler4 handler){
        TypeHandler4 baseTypeHandler = baseTypeHandler(handler); 
        if (!(baseTypeHandler instanceof QueryableTypeHandler)) {
        	return false;
        }
        return ((QueryableTypeHandler)baseTypeHandler).isSimple();
    }
    
    public static boolean handlesArray(TypeHandler4 handler){
        return handler instanceof ArrayHandler;
    }
    
    public static boolean handlesMultidimensionalArray(TypeHandler4 handler){
        return handler instanceof MultidimensionalArrayHandler;
    }
    
    public static boolean handlesClass(TypeHandler4 handler){
        return baseTypeHandler(handler) instanceof CascadingTypeHandler;
    }
    
    public static ReflectClass primitiveClassReflector(TypeHandler4 handler, Reflector reflector){
        TypeHandler4 baseTypeHandler = baseTypeHandler(handler);
        if(baseTypeHandler instanceof PrimitiveHandler){
            return ((PrimitiveHandler)baseTypeHandler).primitiveClassReflector();
        }
        return null;
    }
    
    public static TypeHandler4 baseTypeHandler(TypeHandler4 handler){
        if(handler instanceof ArrayHandler){
            return ((ArrayHandler)handler).delegateTypeHandler();
        }
        if(handler instanceof PrimitiveTypeMetadata){
            return ((PrimitiveTypeMetadata)handler).typeHandler();
        }
        return handler;
    }
    
    public static ReflectClass baseType(ReflectClass clazz){
        if(clazz == null){
            return null;
        }
        if(clazz.isArray()){
            return baseType(clazz.getComponentType());
        }
        return clazz;
    }

	public static boolean isClassAware(TypeHandler4 typeHandler){
		return 	typeHandler instanceof BuiltinTypeHandler || 
				typeHandler instanceof StandardReferenceTypeHandler;
	}

	public static int calculateLinkLength(TypeHandler4 handler){
	    if (handler == null) {
	        // must be ClassMetadata
	        return Const4.ID_LENGTH;
	    }
	    if(handler instanceof TypeFamilyTypeHandler){
	        return ((TypeFamilyTypeHandler) handler).linkLength();
	    }
	    if(handler instanceof PersistentBase){
	        return ((PersistentBase)handler).linkLength();
	    }
	    if(handler instanceof PrimitiveHandler){
	        return ((PrimitiveHandler)handler).linkLength();
	    }
	    if(handler instanceof VariableLengthTypeHandler){
	        if(isValueType(handler)){
	            return Const4.INDIRECTION_LENGTH;    
	        }
	        return Const4.ID_LENGTH;
	    }
	    if (isUntyped(handler)) {
	    	return Const4.ID_LENGTH;
	    }
	    if (handler instanceof StandardReferenceTypeHandler) {
	    	return Const4.ID_LENGTH;
	    }
	    
	    // TODO: For custom handlers there will have to be a way 
	    //       to calculate the length in the slot.
	    
	    //        Options:
	    
	    //        (1) Remember when the first object is marshalled.
	    //        (2) Add a #defaultValue() method to TypeHandler4,
	    //            marshall the default value and check.
	    //        (3) Add a way to test the custom handler when it
	    //            is installed and remember the length there. 
	    
	    throw new NotImplementedException("Unknown type handler type: " + handler);
	}

	public static boolean holdsValueType(TypeHandler4 handler) {
		return isValueType(baseTypeHandler(handler));
	}
	
	public static boolean isValueType(TypeHandler4 handler) {
	    return !(handler instanceof ReferenceTypeHandler);
	}
	
	public static boolean isCascading(TypeHandler4 handler) {
	    return handler instanceof CascadingTypeHandler;
	}
	
	public static boolean isPrimitive(TypeHandler4 handler) {
		return handler instanceof PrimitiveHandler;
	}
	
	public static boolean isUntyped(TypeHandler4 handler) {
		return handler instanceof OpenTypeHandler;
	}
	
	public static boolean isVariableLength(TypeHandler4 handler) {
	    return handler instanceof VariableLengthTypeHandler;
	}

	public static FieldAwareTypeHandler fieldAwareTypeHandler(TypeHandler4 typeHandler) {
		if(typeHandler instanceof FieldAwareTypeHandler){
			return (FieldAwareTypeHandler) typeHandler;
		}
		return NullFieldAwareTypeHandler.INSTANCE;
	}

	public static void collectIDs(final QueryingReadContext context,
			TypeHandler4 typeHandler) {
		if(typeHandler instanceof CascadingTypeHandler){
	    	((CascadingTypeHandler)typeHandler).collectIDs(context);	
	    }
	}

	public static boolean useDedicatedSlot(Context context, TypeHandler4 handler) {
	    if (isValueType(handler)) {
	        return false;
	    }
	    if (isUntyped(handler)) {
	        return false;
	    }
	    return true;
	}

	public static TypeHandler4 arrayElementHandler(TypeHandler4 handler, QueryingReadContext queryingReadContext) {
		if(! (handler instanceof CascadingTypeHandler)){
			return null;
		}
	    CascadingTypeHandler cascadingHandler = (CascadingTypeHandler) HandlerRegistry.correctHandlerVersion(queryingReadContext, handler); 
	    return HandlerRegistry.correctHandlerVersion(queryingReadContext, cascadingHandler.readCandidateHandler(queryingReadContext));
	}
	
	public static Object nullRepresentationInUntypedArrays(TypeHandler4 handler){
        if (handler instanceof PrimitiveHandler){
            return ((PrimitiveHandler) handler).nullRepresentationInUntypedArrays();
        }
        return null;
	}

	public static boolean handleAsObject(TypeHandler4 typeHandler){
	    if(isValueType(typeHandler)){
	        return false;
	    }
	    if(isUntyped(typeHandler)){
	        return false;
	    }
	    return true;
	}

	public static void cascadeActivation(ActivationContext context, TypeHandler4 handler) {
    	if(! (handler instanceof CascadingTypeHandler)){
    		return;
    	}
    	((CascadingTypeHandler)handler).cascadeActivation(context);
	}

	public static boolean handlesPrimitiveArray(TypeHandler4 typeHandler) {
	    return typeHandler instanceof ArrayHandler;
//	    	&& isPrimitive(((ArrayHandler)typeHandler).delegateTypeHandler());
	}

	public static boolean hasClassIndex(TypeHandler4 typeHandler) {
	    if(typeHandler instanceof StandardReferenceTypeHandler){
	        return ((StandardReferenceTypeHandler)typeHandler).classMetadata().hasClassIndex();
	    }
	    return false;
	}

	public static boolean canLoadFieldByIndex(TypeHandler4 handler) {
		if (handler instanceof QueryableTypeHandler) {
	        return !((QueryableTypeHandler)handler).descendsIntoMembers();
	    }
	    return true;
	}

	public static Object wrapWithTransactionContext(Transaction transaction,
			Object value, TypeHandler4 handler) {
		if(isValueType(handler)) {
			return value;
		}
		return transaction.wrap(value);
	}

	public static void collectIdsInternal(CollectIdContext context, final TypeHandler4 handler, int linkLength) {
        if(! (isCascading(handler))){
        	ReadBuffer buffer = context.buffer();
			buffer.seek(buffer.offset() + linkLength);
            return;
        }

        if (handler instanceof StandardReferenceTypeHandler) {
            context.addId();
            return;
        } 
        
        LocalObjectContainer container = (LocalObjectContainer) context.container();
        final SlotFormat slotFormat = context.slotFormat();
        
        if(handleAsObject(handler)){
            // TODO: Code is similar to QCandidate.readArrayCandidates. Try to refactor to one place.
            int collectionID = context.readInt();
            ByteArrayBuffer collectionBuffer = container.readReaderByID(context.transaction(), collectionID);
            ObjectHeader objectHeader = new ObjectHeader(container, collectionBuffer);
            QueryingReadContext subContext = new QueryingReadContext(context.transaction(), context.handlerVersion(), collectionBuffer, collectionID, context.collector());
            objectHeader.classMetadata().collectIDs(subContext);
            return;
        }
        
        final QueryingReadContext queryingReadContext = new QueryingReadContext(context.transaction(), context.handlerVersion(), context.buffer(), 0, context.collector());
        slotFormat.doWithSlotIndirection(queryingReadContext, handler, new Closure4() {
            public Object run() {
                ((CascadingTypeHandler) handler).collectIDs(queryingReadContext);
                return null;
            }
        });
    }

	public static boolean isIndirectedIndexed(TypeHandler4 handler) {
		return isValueType(handler)
			&& (handler instanceof VariableLengthTypeHandler)
			&& (handler instanceof IndexableTypeHandler);
	}

	public static PreparedComparison prepareComparisonFor(TypeHandler4 handler,
			Context context, Object obj) {
		if (!(handler instanceof Comparable4)) {
	    	return null;
	    }
	    return ((Comparable4)handler).prepareComparison(context, obj);
	}

	public static ReflectClass primitiveClassReflector(ClassMetadata classMetadata, Reflector reflector) {
		
		if(classMetadata instanceof PrimitiveTypeMetadata){
            return primitiveClassReflector(((PrimitiveTypeMetadata)classMetadata).typeHandler(), reflector);
        }
		return null;
	}

	public static void activate(UnmarshallingContext context, TypeHandler4 handler) {
		if (handler instanceof ReferenceTypeHandler) {
	    	((ReferenceTypeHandler)handler).activate(context);
	    }
	}

	public static void write(TypeHandler4 handler, WriteContext context, Object obj) {
		handler.write(context, obj);
	}

	public static Object readValueType(ReadContext context, TypeHandler4 handler) {
		return ((ValueTypeHandler)handler).read(context);
	}

	public static boolean isStandaloneTypeHandler(TypeHandler4 customTypeHandler) {
		return isValueType(customTypeHandler) || customTypeHandler instanceof OpenTypeHandler;
	}

	public static ClassMetadata erasedFieldType(final ObjectContainerBase container,
			final ReflectClass fieldType) {
		return fieldType.isInterface()
			? container.classMetadataForID(UNTYPED_ID)
			: container.produceClassMetadata(baseType(fieldType));
	}
}
