package com.storm.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class Rule {
private String ruleId;
private List<RuleExecutionStep> ruleSteps;
private Integer stepNumber;
private Deque<Object> stack;
 private Map<String,Object> data;

public String getRuleId() {
	return ruleId;
}
public void setRuleId(String ruleId) {
	this.ruleId = ruleId;
}
public List<RuleExecutionStep> getRuleSteps() {
	return ruleSteps;
}
public void setRuleSteps(List<RuleExecutionStep> ruleSteps) { 
	this.ruleSteps = ruleSteps;
}
public Integer getStepNumber() {
	return stepNumber;
}
public void setStepNumber(Integer stepNumber) {
	this.stepNumber = stepNumber;
}

public Deque<Object> getStack() {
	return stack;
}
public void setStack(Deque<Object> stack) {
	this.stack = stack;
}
public Map<String, Object> getData() {
	return data;
}
public void setData(Map<String, Object> data) {
	this.data = data;
}



}
