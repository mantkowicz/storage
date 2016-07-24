package com.mantkowicz.storage.operator;

public class ConditionAND extends Condition {
	public Condition conditionA;
	public Condition conditionB;
	
	public ConditionAND(Condition conditionA, Condition conditionB) {
		super();
		this.conditionA = conditionA;
		this.conditionB = conditionB;
	}
}
