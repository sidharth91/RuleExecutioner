package com.storm.model;

public class RuleOperatorStep {

private RuleOperatorStep loperand;
private RuleOperatorStep roperand;
private String operator;
private Object value;
private String state;
private String type;
public RuleOperatorStep getLoperand() {
	return loperand;
}
public void setLoperand(RuleOperatorStep loperand) {
	this.loperand = loperand;
}
public RuleOperatorStep getRoperand() {
	return roperand;
}
public void setRoperand(RuleOperatorStep roperand) {
	this.roperand = roperand;
}
public String getOperator() {
	return operator;
}
public void setOperator(String operator) {
	this.operator = operator;
}
public Object getValue() {
	return value;
}
public void setValue(Object value) {
	this.value = value;
}
public String getState() {
	return state;
}
public void setState(String state) {
	this.state = state;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}


}
