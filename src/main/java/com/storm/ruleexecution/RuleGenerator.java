	package com.storm.ruleexecution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storm.model.Rule;
import com.storm.model.RuleExecutionStep;
import com.storm.model.RuleOperatorStep;

import redis.clients.jedis.Jedis;

public class RuleGenerator {
	
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {
		//String s="{ \"operator\":\"and\", \"loperand\":{ \"operator\":\"or\", \"loperand\":{ \"operator\":\"gt\", \"loperand\":{ \"value\":\"R13\", \"state\":\"avg\", \"type\":\"Variable\" }, \"roperand\":{ \"value\":\"R14\", \"state\":\"avg\", \"type\":\"Variable\" } }, \"roperand\":{ \"operator\":\"lt\", \"loperand\":{ \"value\":\"R13\", \"state\":\"latest\", \"type\":\"Variable\" }, \"roperand\":{ \"value\":\"R14\", \"state\":\"max\", \"type\":\"Variable\" } } }, \"roperand\":{ \"operator\":\"or\", \"loperand\":{ \"operator\":\"gt\", \"loperand\":{ \"value\":\"R13\", \"state\":\"latest\", \"type\":\"Variable\" }, \"roperand\":{ \"value\":\"R14\", \"state\":\"avg\", \"type\":\"Variable\" } }, \"roperand\":{ \"operator\":\"lt\", \"loperand\":{ \"value\":\"R13\", \"state\":\"latest\", \"type\":\"Variable\" }, \"roperand\":{ \"value\":\"R13\", \"state\":\"max\", \"type\":\"Variable\" } } } }";
		String s="{ \"operator\":\"and\", \"loperand\":{ \"operator\":\"gt\", \"loperand\":{ \"operator\":\"add\", \"loperand\":{ \"value\":\"R13\", \"state\":\"avg\", \"type\":\"Variable\" }, \"roperand\":{ \"value\":\"R14\", \"state\":\"avg\", \"type\":\"Variable\" } }, \"roperand\":{ \"operator\":\"mul\", \"loperand\":{ \"value\":15, \"state\":\"\", \"type\":\"Constant\" }, \"roperand\":{ \"value\":9, \"state\":\"\", \"type\":\"Constant\" } } }, \"roperand\":{ \"operator\":\"or\", \"loperand\":{ \"operator\":\"gt\", \"loperand\":{ \"value\":\"R13\", \"state\":\"latest\", \"type\":\"Variable\" }, \"roperand\":{ \"value\":\"R14\", \"state\":\"avg\", \"type\":\"Variable\" } }, \"roperand\":{ \"operator\":\"lt\", \"loperand\":{ \"value\":\"R13\", \"state\":\"latest\", \"type\":\"Variable\" }, \"roperand\":{ \"value\":\"R13\", \"state\":\"max\", \"type\":\"Variable\" } } } }";
		ObjectMapper mapper=new ObjectMapper();
		RuleOperatorStep rule=mapper.readValue(s, RuleOperatorStep.class);
		List<RuleExecutionStep> ruleSteps=new ArrayList<>();
		createRileExecutionSteps(ruleSteps,rule);
		Rule ruledata=new Rule();
		ruledata.setRuleId("Rule1");
		ruledata.setRuleSteps(ruleSteps);
		Jedis jedis=new Jedis();
		jedis.lpush("rule:set1", mapper.writeValueAsString(ruledata));
		System.out.println("done");
	}

	private static void createRileExecutionSteps(List<RuleExecutionStep> ruleSteps, RuleOperatorStep rule) {
		
		if(rule.getOperator()==null) {
			return;
		}
		createRileExecutionSteps(ruleSteps,rule.getLoperand());
		createRileExecutionSteps(ruleSteps,rule.getRoperand());
		
		RuleExecutionStep step=new RuleExecutionStep();
		step.setOperator(rule.getOperator());
		step.setLoperand(rule.getLoperand().getValue());
		step.setLstate(rule.getLoperand().getState());
		step.setLtype(rule.getLoperand().getType());
		step.setRoperand(rule.getRoperand().getValue());
		step.setRstate(rule.getRoperand().getState());
		step.setRtype(rule.getRoperand().getType());
		ruleSteps.add(step);
		
	}
	

}
