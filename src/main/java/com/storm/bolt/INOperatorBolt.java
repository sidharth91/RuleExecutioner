package com.storm.bolt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.storm.model.Constants;
import com.storm.model.Rule;
import com.storm.model.RuleExecutionStep;

public class INOperatorBolt implements IRichBolt {

	private OutputCollector collector;
	private Map<String,String> operatorMap;
	
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		this.collector=collector;
		this.operatorMap=Arrays.asList(Constants.OperatorConstant.values()).stream().collect(
                Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
		
	}

	@Override
	public void execute(Tuple tuple) {
		
		try {
		Rule rule=(Rule) tuple.getValueByField("data");
		 
		 String lvalue=new String();
		 List<String> rvalue=new ArrayList<>();
		

         if(rule.getRuleSteps().get(rule.getStepNumber()).getLoperand()!=null) {  
        	 if(Constants.ValueType.Constant.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getLtype())) {
        		 lvalue=rule.getRuleSteps().get(rule.getStepNumber()).getLoperand().toString();
        	 }if(Constants.ValueType.Variable.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getLtype())) {
        		 if(Constants.State.latest.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getLstate()) &&  !rule.getData().isEmpty() && rule.getData().containsKey(rule.getRuleSteps().get(rule.getStepNumber()).getLoperand())) {
        			 lvalue=rule.getData().get(rule.getRuleSteps().get(rule.getStepNumber()).getLoperand()).toString();
        		 }else {
        			 String streamName=operatorMap.get(rule.getRuleSteps().get(rule.getStepNumber()).getOperator());
          			collector.emit("redisvaluefinderstream",new Values(rule,streamName,Constants.Side.Left.name()));
          			return;
        		 }
        	 }
        
         }
         if(rule.getRuleSteps().get(rule.getStepNumber()).getRoperand()!=null) {  
        	 if(Constants.ValueType.Constant.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getRtype())) {
        		 rvalue=Arrays.asList(rule.getRuleSteps().get(rule.getStepNumber()).getRoperand().toString().split(","));
        	 }if(Constants.ValueType.Variable.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getRtype())) {
        		 if(Constants.State.latest.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getRstate()) &&  !rule.getData().isEmpty() && rule.getData().containsKey(rule.getRuleSteps().get(rule.getStepNumber()).getRoperand())) {
        			 rvalue=Arrays.asList(rule.getData().get(rule.getRuleSteps().get(rule.getStepNumber()).getRoperand()).toString().split(","));
        		 }else {
        			 String streamName=operatorMap.get(rule.getRuleSteps().get(rule.getStepNumber()).getOperator());
          			collector.emit("redisvaluefinderstream",new Values(rule,streamName,Constants.Side.Right.name()));
          			return;
        		 }
        	 }
        
         }
       
         if(rvalue.isEmpty())
        	 rvalue=Arrays.asList(rule.getStack().removeFirst().toString().split(","));
         if(lvalue.isEmpty())
        	 lvalue=rule.getStack().removeFirst().toString();
         
         
         rule.getStack().addFirst(rvalue.contains(lvalue));
         
          rule.setStepNumber(rule.getStepNumber()+1);
         if(rule.getStepNumber()<rule.getRuleSteps().size())
         collector.emit(operatorMap.get(rule.getRuleSteps().get(rule.getStepNumber()).getOperator()),new Values(rule));
         else
        collector.emit("resultstream",new Values(rule));	 
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}finally {
			collector.ack(tuple);
		}
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		Map<String,String> streammap=Arrays.asList(Constants.OperatorConstant.values()).stream().collect(
                Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
		streammap.forEach((k,v)->
			declarer.declareStream(v,new Fields("data")));
		
		declarer.declareStream("resultstream",new Fields("data"));
		declarer.declareStream("redisvaluefinderstream",new Fields("data","streamname","side"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}