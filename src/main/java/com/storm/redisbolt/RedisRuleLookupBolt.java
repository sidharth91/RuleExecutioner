package com.storm.redisbolt;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.storm.redis.bolt.AbstractRedisBolt;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import redis.clients.jedis.JedisCommands;

public class RedisRuleLookupBolt extends AbstractRedisBolt {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public RedisRuleLookupBolt(JedisPoolConfig config) {
        super(config);

	}
    public RedisRuleLookupBolt(JedisClusterConfig config) {
        super(config);
    }

	@Override
	public void declareOutputFields(OutputFieldsDeclarer decler) {
		decler.declareStream("rulestartstream",new Fields("data","rule"));
	}
	
	
	@Override
	protected void process(Tuple tuple) {
		JedisCommands command=null;
		try {
		command=getInstance();
		 Map<String,String> rules = command.hgetAll(tuple.getStringByField("rulesetId"));
		
		 for(Entry<String, String> rule:rules.entrySet()) {
			 collector.emit("rulestartstream",new Values(tuple.getValueByField("data"),rule.getValue()));
		 }
		}
		 catch (Exception e) {
	            this.collector.reportError(e);
	        } finally {
	            returnInstance(command);
	            this.collector.ack(tuple);
	        }
	}


}
