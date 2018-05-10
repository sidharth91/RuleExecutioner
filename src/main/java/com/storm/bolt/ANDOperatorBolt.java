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
		Rule rule=(Rule) tuple.getValueByField("rule");
		Integer stepnumber=tuple.getIntegerByField("stepnumber");
		Stack<Object> resultstack=(Stack<Object>) tuple.getValueByField("resultstack");
		 Map<String,Object> data=(Map<String, Object>) tuple.getValueByField("data");
		 
		 Boolean lvalue=null;
		 Boolean rvalue=null;
		

		
         RuleExecutionStep operand=(RuleExecutionStep) rule.getRuleSteps().get(tuple.getIntegerByField("stepnumber"));
         if(operand.getLoperand()!=null) {  
        	 if(Constants.ValueType.Constant.name().equalsIgnoreCase(operand.getLtype())) {
        		 lvalue=(Boolean)operand.getLoperand();
        	 }if(Constants.ValueType.Variable.name().equalsIgnoreCase(operand.getLtype())) {
        		 if("Cur".equalsIgnoreCase(operand.getLstate()) &&  !data.isEmpty() && data.containsKey(operand.getLoperand())) {
        			 lvalue=(Boolean)data.get(operand.getLoperand());
        		 }else {
        			 String streamName=operatorMap.get(operand.getOperator());
          			collector.emit("redisvaluefinderstream",new Values(data,rule,stepnumber,resultstack,streamName,Constants.Side.Left.name()));
          			return;
        		 }
        	 }
        
         }
         if(operand.getRoperand()!=null) {  
        	 if(Constants.ValueType.Constant.name().equalsIgnoreCase(operand.getRtype())) {
        		 rvalue=(Boolean)operand.getRoperand();
        	 }if(Constants.ValueType.Variable.name().equalsIgnoreCase(operand.getRtype())) {
        		 if("Cur".equalsIgnoreCase(operand.getRstate()) &&  !data.isEmpty() && data.containsKey(operand.getRoperand())) {
        			 rvalue=(Boolean)data.get(operand.getRoperand());
        		 }else {
        			 String streamName=operatorMap.get(operand.getOperator());
          			collector.emit("redisvaluefinderstream",new Values(data,rule,stepnumber,resultstack,streamName,Constants.Side.Right.name()));
          			return;
        		 }
        		 
        	 }
         }
       
         if(rvalue==null)
        	 rvalue=(Boolean)resultstack.pop();
         if(lvalue==null)
        	 lvalue=(Boolean) resultstack.pop();
         
         resultstack.push(lvalue && rvalue);
         stepnumber++;
         if(stepnumber<rule.getRuleSteps().size())
         collector.emit(operatorMap.get(rule.getRuleSteps().get(stepnumber).getOperator()),new Values(data,rule,stepnumber,resultstack));
         else
        collector.emit("resultstream",new Values(data,rule,stepnumber,resultstack));	 
         
		collector.ack(tuple);
		
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
			declarer.declareStream(v,new Fields("data","rule","stepnumber","resultstack")));
		
		declarer.declareStream("resultstream",new Fields("data","rule","stepnumber","resultstack"));
		declarer.declareStream("redisvaluefinderstream",new Fields("data","rule","stepnumber","resultstack","streamname","side"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
