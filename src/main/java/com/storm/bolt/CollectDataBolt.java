package com.storm.bolt;

import java.io.IOException;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storm.model.SourceData;
/*This class Initiate the rule consumed data from kafka topic*/
public class CollectDataBolt implements IRichBolt {

	private OutputCollector collector;
	
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
	}

	@Override
	public void execute(Tuple tuple) {//kafkaTuple contain  fields "topic", "partition", "offset", "key", "value".
		ObjectMapper mapper=new ObjectMapper();
		SourceData sourceData=null;
		try {
			sourceData=mapper.readValue(tuple.getStringByField("value"), SourceData.class);
			if(sourceData!=null) {
				for(String rulesetId:sourceData.getRuleIds()) {
					collector.emit("rulelookupboltstream",new Values(sourceData.getData(),rulesetId));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			collector.ack(tuple);
		}
	    finally {
	    	collector.ack(tuple);	
		}
		
	}

	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
        this.collector=collector;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declare) {
		declare.declareStream("rulelookupboltstream",new Fields("data","rulesetId"));
		
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
