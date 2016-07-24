package com.mantkowicz.storage.operator;

public class Condition {
	public String key;
	public Object value;
	public Operators operator;
	
	public Condition(String key, Operators operator, Object value) {
		this.key = key;
		this.value = value;
		this.operator = operator;
	}
	
	public Condition() {
	}
}
