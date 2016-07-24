package com.mantkowicz.storage.handler;

import java.util.List;

import com.mantkowicz.storage.callback.Callback;
import com.mantkowicz.storage.operator.Condition;

public abstract class AbstractStorageHandler {
	private String databaseName = "";
	
	public AbstractStorageHandler(String databaseName) {
		this.databaseName = databaseName;
	}
		
	public abstract void connect();
	public abstract <T> void registerEntity(Class<T> type, String tableName);
	public abstract <T> String insert(T object, Callback callback);
	public abstract <T> List<T> select(Class<T> type, Integer maxRecordCount, Condition condition, Callback callback);
	public abstract <T> void update(String objectId, T object, Callback callback);
	public abstract <T> void remove(Class<T> type, String objectId, Callback callback);
	
	public String getDatabaseName() {
		return databaseName;
	}
}
