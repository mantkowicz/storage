package com.mantkowicz.storage.operator;

public class ConditionOR extends Condition {
	public Condition conditionA;
	public Condition conditionB;
	
	public ConditionOR(Condition conditionA, Condition conditionB) {
		super();
		this.conditionA = conditionA;
		this.conditionB = conditionB;
	}	
}
