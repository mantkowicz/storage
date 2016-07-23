package com.mantkowicz.storage.handler;

import java.util.List;

import com.mantkowicz.storage.callback.Callback;
import com.mantkowicz.storage.operator.Condition;
import com.mantkowicz.storage.record.AbstractRecord;

public abstract class AbstractStorageHandler {
	private String databaseName = "";
	
	public AbstractStorageHandler(String databaseName) {
		this.databaseName = databaseName;
	}
		
	public abstract void connect();
	public abstract <T extends AbstractRecord> String insert(T object, Callback callback);
	public abstract <T extends AbstractRecord> List<T> select(List<T> objects, List<Condition> conditions, Callback callback);
	public abstract <T extends AbstractRecord> void update(Long objectId, T object, Callback callback);
	
	public String getDatabaseName() {
		return databaseName;
	}
}
