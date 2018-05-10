package com.storm.model;

import java.util.List;
import java.util.Map;

public class SourceData {
private Map<String,Object> data;
private List<String> ruleIds;
public Map<String, Object> getData() {
	return data;
}
public void setData(Map<String, Object> data) {
	this.data = data;
}
public List<String> getRuleIds() {
	return ruleIds;
}
public void setRuleIds(List<String> ruleIds) {
	this.ruleIds = ruleIds;
}

}
