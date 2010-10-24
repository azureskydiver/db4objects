/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */

package com.db4o.drs.versant.metadata;

public class ObjectInfo extends VodLoidAwareObject {
	
	private long signatureLoid;
	
	private long classMetadataLoid;
	
	private long objectLoid;
	
	private int operation;
	
	private long uuidLongPart; 
	
	private long modificationVersion;
	
	public ObjectInfo(long signatureLoid, long classMetadataLoid, long objectLoid, int operation, long version) {
		this.signatureLoid = signatureLoid; 
		this.classMetadataLoid = classMetadataLoid;
		this.objectLoid = objectLoid;
		this.operation = operation;
		uuidLongPart = version;
		modificationVersion = version;
	}

	public ObjectInfo(){
		
	}
	
	public void activate(){
		// just access one field
		classMetadataLoid();
	}
	
	public long classMetadataLoid() {
		return classMetadataLoid;
	}

	public long objectLoid() {
		return objectLoid;
	}

	public int operation() {
		return operation;
	}
	
	public long uuidLongPart(){
		return uuidLongPart;
	}
	
	public void uuidLongPart(long newValue){
		uuidLongPart = newValue;
	}
	
	public long signatureLoid(){
		return signatureLoid;
	}
	
	public void signatureLoid(long loid) {
		this.signatureLoid = loid;
	}
	
	public long modificationVersion() {
		return modificationVersion;
	}

	
	public void modificationVersion(long version) {
		modificationVersion = version;
	}


	@Override
	public String toString() {
		return "(obj:" + objectLoid + ", " + Operations.forValue(operation) + " ,creation:" + uuidLongPart + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(! (obj instanceof ObjectInfo) ){
			return false;
		}
		ObjectInfo other = (ObjectInfo) obj;
		return objectLoid == other.objectLoid;
	}

	public void copyStateFrom(ObjectInfo other) {
		operation = other.operation;
		modificationVersion = other.modificationVersion;
	}
	
	public static class Operations {
		
		public final int value;

		private final String _description;

		private Operations(int value, String description) {
			this.value = value;
			_description = description;
		}

		public static final Operations CREATE = new Operations(1, "create");
		
		public static final Operations UPDATE = new Operations(2, "update");
		
		public static final Operations DELETE = new Operations(3, "delete");
		
		public static Operations forValue(int value){
			switch(value){
				case 1:
					return CREATE;
				case 2:
					return UPDATE;
				case 3:
					return DELETE;
				default:
					throw new IllegalArgumentException();
			}
		}
		
		@Override
		public String toString() {
			return _description;
		}
		
	}



}
		