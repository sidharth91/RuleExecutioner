package com.storm.model;

public class RuleExecutionStep {
private String operator;
private Object loperand;
private Object roperand;
private String ltype;
private String rtype;
private String lstate;
private String rstate;
public Object getLoperand() {
	return loperand;
}
public void setLoperand(Object loperand) {
	this.loperand = loperand;
}
public Object getRoperand() {
	return roperand;
}
public void setRoperand(Object roperand) {
	this.roperand = roperand;
}
public String getOperator() {
	return operator;
}
public void setOperator(String operator) {
	this.operator = operator;
}
public String getLtype() {
	return ltype;
}
public void setLtype(String ltype) {
	this.ltype = ltype;
}
public String getRtype() {
	return rtype;
}
public void setRtype(String rtype) {
	this.rtype = rtype;
}
public String getLstate() {
	return lstate;
}
public void setLstate(String lstate) {
	this.lstate = lstate;
}
public String getRstate() {
	return rstate;
}
public void setRstate(String rstate) {
	this.rstate = rstate;
}



}
