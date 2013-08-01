package com.simbiosys.chapooapp;

public class GenericObject {
	public int objectType;
	public String objectID;
	public String objectName;
	public String parentID;
	
	public GenericObject() {}
	
	public GenericObject(String id, String name, int type) {
		this.objectType = type;
		this.objectID = id;
		this.objectName = name;
	}
	
	public int getObjectType() {
		return objectType;
	}
	
	public String getObjectID() {
		return objectID;
	}
	
	public String getObjectName() {
		return objectName;
	}
	
}
