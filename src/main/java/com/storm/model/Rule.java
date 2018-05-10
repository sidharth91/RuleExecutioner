package com.storm.model;

import java.util.List;

public class Rule {
private String ruleId;
private List<RuleExecutionStep> ruleSteps;
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

}
