package com.storm.bolt;

import java.util.List;

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
		 Object lookupValue = command.lrange(tuple.getStringByField("rulesetId"),0,-1);
		 List<String> rules=(List<String>) lookupValue;
		 for(String rule:rules) {
			 collector.emit("rulestartstream",new Values(tuple.getValueByField("data"),rule));
		 }
		}
		 catch (Exception e) {
	            this.collector.reportError(e);
	            this.collector.ack(tuple);
	        } finally {
	            returnInstance(command);
	            this.collector.ack(tuple);
	        }
	}


}
