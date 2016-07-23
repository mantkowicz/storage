package com.mantkowicz.storage.operator;

public class Condition {
	public String key;
	public Object value;
	public ConditionOperator operator;

	public Condition(String key, ConditionOperator operator, Object value) {
		super();
		this.key = key;
		this.value = value;
		this.operator = operator;
	}	
}
