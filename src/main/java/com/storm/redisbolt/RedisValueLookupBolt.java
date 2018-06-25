package com.storm.redisbolt;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.storm.redis.bolt.AbstractRedisBolt;
import org.apache.storm.redis.common.config.JedisClusterConfig;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import com.storm.model.Constants;
import com.storm.model.Rule;

import redis.clients.jedis.JedisCommands;

public class RedisValueLookupBolt extends AbstractRedisBolt {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public RedisValueLookupBolt(JedisPoolConfig config) {
        super(config);

	}
    public RedisValueLookupBolt(JedisClusterConfig config) {
        super(config);
    }

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		Map<String,String> streammap=Arrays.asList(Constants.OperatorConstant.values()).stream().collect(
                Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
		streammap.forEach((k,v)->
			declarer.declareStream(v,new Fields("data")));
	}
	
	
	@Override
	protected void process(Tuple tuple) {
		JedisCommands command=null;
		String hashkey=null;
		String key=null;
		Rule rule=(Rule) tuple.getValueByField("data");
		try {
		command=getInstance();
		if(Constants.Side.Left.name().equalsIgnoreCase(tuple.getStringByField("side"))) {
			hashkey=(String) rule.getRuleSteps().get(rule.getStepNumber()).getLoperand();
			key=rule.getRuleSteps().get(rule.getStepNumber()).getLstate();
		}
		if(Constants.Side.Right.name().equalsIgnoreCase(tuple.getStringByField("side"))) {
			hashkey=(String) rule.getRuleSteps().get(rule.getStepNumber()).getRoperand();
			key=rule.getRuleSteps().get(rule.getStepNumber()).getRstate();
		}
		
		 Object lookupValue = command.hget(hashkey, key);
		
		if(Constants.Side.Left.name().equalsIgnoreCase(tuple.getStringByField("side"))) {
			rule.getRuleSteps().get(rule.getStepNumber()).setLoperand(lookupValue);
			rule.getRuleSteps().get(rule.getStepNumber()).setLtype(Constants.ValueType.Constant.name());
		}
		if(Constants.Side.Right.name().equalsIgnoreCase(tuple.getStringByField("side"))) {
			rule.getRuleSteps().get(rule.getStepNumber()).setRoperand(lookupValue);
			rule.getRuleSteps().get(rule.getStepNumber()).setRtype(Constants.ValueType.Constant.name());
		}		
		
		collector.emit(tuple.getStringByField("streamname"),new Values(rule));
		}
		 catch (Exception e) {
	            this.collector.reportError(e);
	        } finally {
	            returnInstance(command);
	            this.collector.ack(tuple);
	        }
	}


}
