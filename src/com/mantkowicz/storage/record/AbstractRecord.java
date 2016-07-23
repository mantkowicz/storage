package com.mantkowicz.storage.record;

public abstract class AbstractRecord {
	abstract public String getTableName();
	abstract public String toJson();
	abstract public void loadJson(String jsonString);
}
