package com.mantkowicz.storage.record;

public abstract class AbstractRecord {
	private transient boolean isUpdate;

	public AbstractRecord(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	public boolean isUpdate() {
		return isUpdate;
	} 
}
