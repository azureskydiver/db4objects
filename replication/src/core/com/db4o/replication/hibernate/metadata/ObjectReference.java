package com.db4o.replication.hibernate.metadata;

public class ObjectReference {
	public static final String TABLE_NAME = "ObjectReference";
	public static final String CLASS_NAME = "className";
	public static final String OBJECT_ID = "objectId";
	public static final String UUID = "uuid";
	public static final String VERSION = "version";

	private String className;

	private long objectId;

	private Uuid uuid;

	private long version;
	public ObjectReference() {}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public long getObjectId() {
		return objectId;
	}

	public void setObjectId(long objectId) {
		this.objectId = objectId;
	}

	public Uuid getUuid() {
		return uuid;
	}

	public void setUuid(Uuid uuid) {
		this.uuid = uuid;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

	public String toString() {
		return "ObjectReference{" +
				"className='" + className + '\'' +
				", objectId=" + objectId +
				", uuid=" + uuid +
				", version=" + version +
				'}';
	}
}
