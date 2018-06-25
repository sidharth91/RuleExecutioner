package com.storm.redisbolt;

import java.io.IOException;
import java.util.ArrayDeque;
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
		rule.setStepNumber(0);
		rule.setData(data); 
		rule.setStack(new ArrayDeque<>()); 
		 
		if(rule.getRuleSteps()!=null && !rule.getRuleSteps().isEmpty()) {
			RuleExecutionStep step=rule.getRuleSteps().get(0);
			String streamName=operatorMap.get(step.getOperator());
			collector.emit(streamName,new Values(rule));
		}
	} catch (Exception e) {
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
			declarer.declareStream(v,new Fields("data")));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
