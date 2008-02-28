/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.internal.marshall;

import com.db4o.internal.convert.conversions.*;

/**
 * Represents a db4o file format version, assembles all the marshallers
 * needed to read/write this specific version.
 * 
 * A marshaller knows how to read/write certain types of values from/to its
 * representation on disk for a given db4o file format version.
 * 
 * Responsibilities are somewhat overlapping with TypeHandler's.
 * 
 * @exclude
 */
public class MarshallerFamily {
    
    
    public static class FamilyVersion{
        
        public static final int PRE_MARSHALLER = 0;
        
        public static final int MARSHALLER = 1;
        
        public static final int BTREE_FIELD_INDEXES = 2; 
        
    }
    
    private static int CURRENT_VERSION = FamilyVersion.BTREE_FIELD_INDEXES;
    
    public final ArrayMarshaller _array;
    
    public final ClassMarshaller _class;
    
    public final FieldMarshaller _field;
    
    public final ObjectMarshaller _object;

    public final PrimitiveMarshaller _primitive;

    public final StringMarshaller _string;
    
    public final UntypedMarshaller _untyped;

    private final int _converterVersion;
    
    private final int _handlerVersion;

    private final static MarshallerFamily[] allVersions = new MarshallerFamily[] {
        
        // LEGACY => before 5.4
        
        new MarshallerFamily(
            0,
            0,
            new ArrayMarshaller0(),
            new ClassMarshaller0(),
            new FieldMarshaller0(),
            new ObjectMarshaller0(), 
            new PrimitiveMarshaller0(),
            new StringMarshaller0(),
            new UntypedMarshaller0()),
        
        new MarshallerFamily(
            ClassIndexesToBTrees_5_5.VERSION,
            1,
            new ArrayMarshaller1(),
            new ClassMarshaller1(),
            new FieldMarshaller0(),
            new ObjectMarshaller1(), 
            new PrimitiveMarshaller1(),
            new StringMarshaller1(),
            new UntypedMarshaller1()),
    
        new MarshallerFamily(
            FieldIndexesToBTrees_5_7.VERSION,
            2,
            new ArrayMarshaller1(),
            new ClassMarshaller2(),
            new FieldMarshaller1(),
            new ObjectMarshaller1(), 
            new PrimitiveMarshaller1(),
            new StringMarshaller1(),
            new UntypedMarshaller1()),
        
        };

    public MarshallerFamily(
            int converterVersion,
            int handlerVersion,
            ArrayMarshaller arrayMarshaller,
            ClassMarshaller classMarshaller,
            FieldMarshaller fieldMarshaller,
            ObjectMarshaller objectMarshaller,
            PrimitiveMarshaller primitiveMarshaller, 
            StringMarshaller stringMarshaller,
            UntypedMarshaller untypedMarshaller) {
        _converterVersion = converterVersion;
        _handlerVersion = handlerVersion;
        _array = arrayMarshaller;
        _array._family = this;
        _class = classMarshaller;
        _class._family = this;
        _field = fieldMarshaller;
        _object = objectMarshaller;
        _object._family = this;
        _primitive = primitiveMarshaller;
        _primitive._family = this;
        _string = stringMarshaller;
        _untyped = untypedMarshaller;
        _untyped._family = this;
    }

    public static MarshallerFamily version(int n) {
        n = noVersionGreaterThan2(n);
        return allVersions[n];
    }

    private static int noVersionGreaterThan2(int n) {
        if(n > 2){
            n = 2;
        }
        return n;
    }

    public static MarshallerFamily current() {
        if(CURRENT_VERSION < FamilyVersion.BTREE_FIELD_INDEXES){
            throw new IllegalStateException("Using old marshaller versions to write database files is not supported, source code has been removed.");
        }
        return version(CURRENT_VERSION);
    }
    
    public static MarshallerFamily forConverterVersion(int n){
        MarshallerFamily result = allVersions[0];
        for (int i = 1; i < allVersions.length; i++) {
            if(allVersions[i]._converterVersion > n){
                return result;
            }
            result = allVersions[i]; 
        }
        return result;
    }
    
    public int handlerVersion(){
    	return _handlerVersion;
    }
    
}
