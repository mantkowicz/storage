package com.mantkowicz.storage.operator;

public class Condition {
	public String key;
	public Object value;
	public ConditionOperator operator;

	public Condition(String key, Object value, ConditionOperator operator) {
		super();
		this.key = key;
		this.value = value;
		this.operator = operator;
	}	
}
