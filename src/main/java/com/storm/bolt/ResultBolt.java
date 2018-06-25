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

public class ResultBolt implements IRichBolt {

	private OutputCollector collector;

	
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		this.collector=collector;
		
		
	}

	@Override
	public void execute(Tuple tuple) {
		Rule rule=(Rule) tuple.getValueByField("data");
		System.out.println(rule.getStack().peekFirst().toString());	 
         
		collector.ack(tuple);
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
