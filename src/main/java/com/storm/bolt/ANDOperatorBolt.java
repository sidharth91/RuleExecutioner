package com.storm.bolt;

import java.util.Arrays;
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

public class ANDOperatorBolt implements IRichBolt {

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
		 
		 Boolean lvalue=null;
		 Boolean rvalue=null;
		

         if(rule.getRuleSteps().get(rule.getStepNumber()).getLoperand()!=null) {  
        	 if(Constants.ValueType.Constant.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getLtype())) {
        		 lvalue=(Boolean)rule.getRuleSteps().get(rule.getStepNumber()).getLoperand();
        	 }if(Constants.ValueType.Variable.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getLtype())) {
        		 if(Constants.State.latest.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getLstate()) &&  !rule.getData().isEmpty() && rule.getData().containsKey(rule.getRuleSteps().get(rule.getStepNumber()).getLoperand())) {
        			 lvalue=(Boolean)rule.getData().get(rule.getRuleSteps().get(rule.getStepNumber()).getLoperand());
        		 }else {
        			 String streamName=operatorMap.get(rule.getRuleSteps().get(rule.getStepNumber()).getOperator());
          			collector.emit("redisvaluefinderstream",new Values(rule,streamName,Constants.Side.Left.name()));
          			return;
        		 }
        	 }
        
         }
         if(rule.getRuleSteps().get(rule.getStepNumber()).getRoperand()!=null) {  
        	 if(Constants.ValueType.Constant.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getRtype())) {
        		 rvalue=(Boolean)rule.getRuleSteps().get(rule.getStepNumber()).getRoperand();
        	 }if(Constants.ValueType.Variable.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getRtype())) {
        		 if(Constants.State.latest.name().equalsIgnoreCase(rule.getRuleSteps().get(rule.getStepNumber()).getRstate()) &&  !rule.getData().isEmpty() && rule.getData().containsKey(rule.getRuleSteps().get(rule.getStepNumber()).getRoperand())) {
        			 rvalue=(Boolean)rule.getData().get(rule.getRuleSteps().get(rule.getStepNumber()).getRoperand());
        		 }else {
        			 String streamName=operatorMap.get(rule.getRuleSteps().get(rule.getStepNumber()).getOperator());
          			collector.emit("redisvaluefinderstream",new Values(rule,streamName,Constants.Side.Right.name()));
          			return;
        		 }
        	 }
        
         }
       
         if(rvalue==null)
        	 rvalue=(Boolean)rule.getStack().removeFirst();
         if(lvalue==null)
        	 lvalue=(Boolean) rule.getStack().removeFirst();
         
         rule.getStack().addFirst(lvalue && rvalue);
         
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