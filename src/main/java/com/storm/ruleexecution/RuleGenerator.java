	package com.storm.ruleexecution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storm.model.Rule;
import com.storm.model.RuleExecutionStep;
import com.storm.model.RuleOperatorStep;

import redis.clients.jedis.Jedis;

public class RuleGenerator {
	
	
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		
		//String tt="( ( 111111 + 222222 > 40 ) & ( 111111 < 333333 ) | ( 111111 in 'trt','per','ser' & 222222 != 23 ) ) & ( 111111 <= 78 )";//space separated string between operand and operator
		
		//String s="((111111 + 222222 >40)&(111111<333333)|(111111in'trt','per','ser'&222222!=23))&(111111<=78)";//space separated string between operand and operator
		
		//String s="((34567:Variable:latest > 30)&(34567:Variable:latest>12345:Variable:avg))|(23456:Variable:avg>5)";//space separated string between operand and operator
		
		String s="((34567:Variable:latest >= 38)&(34567:Variable:latest<=12345:Variable:avg))";//space separated string between operand and operator
		
		Map<String,String> operators=Arrays.asList(OperatorPriority.values()).stream().collect(Collectors.toMap(OperatorPriority::getOperator,OperatorPriority::getRegex));
		
	for(Entry<String, String> map:operators.entrySet()) {
		String name=map.getKey();
		String regex=map.getValue();
		if(!name.equalsIgnoreCase(regex)) {
	    s=s.replaceAll(regex, "[]");
		}else {
		s=s.replace(regex, "[]");
		}
	    s=s.replace("[]", " "+name+" ");
	}
	    s=s.trim();
	    System.out.println(s);
		s=createOperatorExpression(s);
		System.out.println(s);
		ObjectMapper mapper=new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		RuleOperatorStep rule=mapper.readValue(s, RuleOperatorStep.class);
		List<RuleExecutionStep> ruleSteps=new ArrayList<>();
		createRileExecutionSteps(ruleSteps,rule);
		Rule ruledata=new Rule();
		ruledata.setRuleId("Rule1");
		ruledata.setRuleSteps(ruleSteps);
		Jedis jedis=new Jedis();
		Map<String,String> rulemap=new HashMap<String, String>();
		rulemap.put(ruledata.getRuleId(),mapper.writeValueAsString(ruledata));
		jedis.hmset("rule:set1", rulemap);
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
	
	
	private static String createOperatorExpression(String s) throws JsonProcessingException {
		
		 Map<String,Integer> operators=Arrays.asList(OperatorPriority.values()).stream().collect(Collectors.toMap(OperatorPriority::getOperator,OperatorPriority::getPriority));
		 
		List<String> list=Arrays.asList(s.split("\\s{1,}"));
		
		Stack<String> operatorstack=new Stack<>();
		Stack<RuleOperatorStep> operandstack=new Stack<>();
		
		//List<String> operators=Arrays.asList(new String[] {"+",">","&","!="});
				
		for (int i = 0; i < list.size(); i++) {
			String check=list.get(i).trim();

			if (operators.keySet().contains(check)) {
				
					if (operators.get(check) == 0) {
						operatorstack.push(check);
					} else if (operators.get(check) == 1) {
						while (!operatorstack.isEmpty() && operators.get(operatorstack.peek()) != 0) {
							String opr = operatorstack.pop();
							RuleOperatorStep opR = operandstack.pop();
							RuleOperatorStep opL = operandstack.pop();
							operandstack.push(createLeftAndRightOperand(opL, opR, opr));
						}
						operatorstack.pop();

					} else {
						while (!operatorstack.isEmpty()
								&& operators.get(operatorstack.peek()) >= operators.get(check)) {
							String opr = operatorstack.pop();
							RuleOperatorStep opR = operandstack.pop();
							RuleOperatorStep opL = operandstack.pop();
							operandstack.push(createLeftAndRightOperand(opL, opR, opr));
						}
					}

				
				if(operators.get(check)>1 )
				operatorstack.push(check);
			} else {
				operandstack.push(createOperandWithValue(check));
			}
			
			
		}
		
		while(!operatorstack.isEmpty()) {
			String opr=operatorstack.pop();
			RuleOperatorStep opR=operandstack.pop();
			RuleOperatorStep opL=operandstack.pop();
			operandstack.push(createLeftAndRightOperand(opL, opR, opr));
		}
		
		
		//System.out.println(operandstack.pop());
		
		ObjectMapper mapper=new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		String rule=mapper.writeValueAsString(operandstack.pop());
		return rule;
		
	}



	private static RuleOperatorStep createLeftAndRightOperand(RuleOperatorStep opL, RuleOperatorStep opR, String operator) {
		RuleOperatorStep oprr = new RuleOperatorStep();
		oprr.setLoperand(opL);
		oprr.setRoperand(opR);
		oprr.setOperator(operator);
		return oprr;
	}

	private static RuleOperatorStep createOperandWithValue(String value) {
		RuleOperatorStep op = new RuleOperatorStep();
		op.setState(value.split(":").length>=2 && value.split(":")[2]!=null?value.split(":")[2]:"latest");
		op.setType(value.split(":").length>1 && value.split(":")[1]!=null?value.split(":")[1]:"Constant");
		op.setValue(value.split(":")[0]!=null?value.split(":")[0]:value);
		return op;
	}

}
