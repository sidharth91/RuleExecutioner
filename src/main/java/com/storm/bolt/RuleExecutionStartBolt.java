package com.storm.bolt;

import java.io.IOException;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storm.model.Constants;
import com.storm.model.Rule;
import com.storm.model.RuleExecutionStep;

public class RuleExecutionStartBolt implements IRichBolt {

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
	ObjectMapper mapper=new ObjectMapper();
	try {
		Rule rule=mapper.readValue(tuple.getStringByField("rule"),Rule.class) ;
		 Map<String,Object> data=(Map<String, Object>) tuple.getValueByField("data");
		 
		if(rule.getRuleSteps()!=null && !rule.getRuleSteps().isEmpty()) {
			RuleExecutionStep step=rule.getRuleSteps().get(0);
			String streamName=operatorMap.get(step.getOperator());
			collector.emit(streamName,new Values(data,rule,0,new Stack<Object>()));
		}
	} catch (IOException e) {
		e.printStackTrace();
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
			declarer.declareStream(v,new Fields("data","rule","stepnumber","resultstack")));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
