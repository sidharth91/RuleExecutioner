package com.storm.ruleexecution;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.kafka.spout.KafkaSpout;
import org.apache.storm.kafka.spout.KafkaSpoutConfig;
import org.apache.storm.redis.common.config.JedisPoolConfig;
import org.apache.storm.topology.TopologyBuilder;

import com.storm.bolt.ADDOperatorBolt;
import com.storm.bolt.ANDOperatorBolt;
import com.storm.bolt.CollectDataBolt;
import com.storm.bolt.GTOperatorBolt;
import com.storm.bolt.INOperatorBolt;
import com.storm.bolt.LTOperatorBolt;
import com.storm.bolt.MULOperatorBolt;
import com.storm.bolt.OROperatorBolt;
import com.storm.bolt.ResultBolt;
import com.storm.bolt.SUBOperatorBolt;
import com.storm.redisbolt.RedisRuleLookupBolt;
import com.storm.redisbolt.RedisValueLookupBolt;
import com.storm.redisbolt.RuleExecutionStartBolt;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	final TopologyBuilder tp = new TopologyBuilder();
    	tp.setSpout("KafkaSpout", new KafkaSpout<>(KafkaSpoutConfig.builder("localhost:" + 9092, "data").build()), 1);
    	
    	tp.setBolt("CollectDataBolt",new CollectDataBolt()).shuffleGrouping("KafkaSpout");
    	
    	JedisPoolConfig poolConfig = new JedisPoolConfig.Builder().setHost("localhost").setPort(6379).build();
    	
    	tp.setBolt("RedisRuleLookupBolt",new RedisRuleLookupBolt(poolConfig)).shuffleGrouping("CollectDataBolt","rulelookupboltstream");
    	
    	tp.setBolt("RuleExecutionStartBolt",new RuleExecutionStartBolt()).shuffleGrouping("RedisRuleLookupBolt","rulestartstream");
    	
    	tp.setBolt("GTOperatorBolt",new GTOperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","gtstream").shuffleGrouping("RedisValueLookupBolt", "gtstream").shuffleGrouping("GTOperatorBolt","gtstream")
    	.shuffleGrouping("LTOperatorBolt","gtstream").shuffleGrouping("ANDOperatorBolt","gtstream").shuffleGrouping("OROperatorBolt","gtstream")
    	.shuffleGrouping("ADDOperatorBolt","gtstream").shuffleGrouping("MULOperatorBolt","gtstream").shuffleGrouping("SUBOperatorBolt","gtstream").shuffleGrouping("INOperatorBolt","gtstream");
    	
    	tp.setBolt("LTOperatorBolt",new LTOperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","ltstream").shuffleGrouping("RedisValueLookupBolt", "ltstream").shuffleGrouping("LTOperatorBolt","ltstream")
    	.shuffleGrouping("GTOperatorBolt","ltstream").shuffleGrouping("ANDOperatorBolt","ltstream").shuffleGrouping("OROperatorBolt","ltstream")
    	.shuffleGrouping("ADDOperatorBolt","ltstream").shuffleGrouping("MULOperatorBolt","ltstream").shuffleGrouping("SUBOperatorBolt","ltstream").shuffleGrouping("INOperatorBolt","ltstream");
    	
    	tp.setBolt("ANDOperatorBolt",new ANDOperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","andstream").shuffleGrouping("RedisValueLookupBolt", "andstream")
    	.shuffleGrouping("GTOperatorBolt","andstream").shuffleGrouping("LTOperatorBolt","andstream").shuffleGrouping("ANDOperatorBolt","andstream").shuffleGrouping("OROperatorBolt","andstream")
    	.shuffleGrouping("ADDOperatorBolt","andstream").shuffleGrouping("MULOperatorBolt","andstream").shuffleGrouping("SUBOperatorBolt","andstream").shuffleGrouping("INOperatorBolt","andstream");
    	
    	tp.setBolt("OROperatorBolt",new OROperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","orstream").shuffleGrouping("RedisValueLookupBolt", "orstream")
    	.shuffleGrouping("GTOperatorBolt","orstream").shuffleGrouping("LTOperatorBolt","orstream").shuffleGrouping("ANDOperatorBolt","orstream").shuffleGrouping("OROperatorBolt","orstream")
    	.shuffleGrouping("ADDOperatorBolt","orstream").shuffleGrouping("MULOperatorBolt","orstream").shuffleGrouping("SUBOperatorBolt","orstream").shuffleGrouping("INOperatorBolt","orstream");
    	
    	tp.setBolt("RedisValueLookupBolt",new RedisValueLookupBolt(poolConfig)).shuffleGrouping("GTOperatorBolt","redisvaluefinderstream").shuffleGrouping("LTOperatorBolt","redisvaluefinderstream")
    	.shuffleGrouping("OROperatorBolt","redisvaluefinderstream").shuffleGrouping("ANDOperatorBolt","redisvaluefinderstream").shuffleGrouping("ADDOperatorBolt","redisvaluefinderstream")
    	.shuffleGrouping("MULOperatorBolt","redisvaluefinderstream").shuffleGrouping("SUBOperatorBolt","redisvaluefinderstream").shuffleGrouping("INOperatorBolt","redisvaluefinderstream");
    	
    	tp.setBolt("ResultBolt",new ResultBolt()).shuffleGrouping("GTOperatorBolt","resultstream").shuffleGrouping("LTOperatorBolt","resultstream")
    	.shuffleGrouping("OROperatorBolt","resultstream").shuffleGrouping("ANDOperatorBolt","resultstream").shuffleGrouping("ADDOperatorBolt","resultstream").shuffleGrouping("SUBOperatorBolt","resultstream")
    	.shuffleGrouping("MULOperatorBolt","resultstream").shuffleGrouping("INOperatorBolt","resultstream");
    	
    	
    	tp.setBolt("ADDOperatorBolt",new ADDOperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","addstream").shuffleGrouping("RedisValueLookupBolt", "addstream").shuffleGrouping("GTOperatorBolt","addstream")
    	.shuffleGrouping("LTOperatorBolt","addstream").shuffleGrouping("ANDOperatorBolt","addstream").shuffleGrouping("OROperatorBolt","addstream")
    	.shuffleGrouping("ADDOperatorBolt","addstream").shuffleGrouping("MULOperatorBolt","addstream").shuffleGrouping("SUBOperatorBolt","addstream").shuffleGrouping("INOperatorBolt","addstream");
    	
    	tp.setBolt("MULOperatorBolt",new MULOperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","mulstream").shuffleGrouping("RedisValueLookupBolt", "mulstream").shuffleGrouping("GTOperatorBolt","mulstream")
    	.shuffleGrouping("LTOperatorBolt","mulstream").shuffleGrouping("ANDOperatorBolt","mulstream").shuffleGrouping("OROperatorBolt","mulstream")
    	.shuffleGrouping("ADDOperatorBolt","mulstream").shuffleGrouping("SUBOperatorBolt","mulstream").shuffleGrouping("MULOperatorBolt","mulstream").shuffleGrouping("INOperatorBolt","mulstream");
    	
    	tp.setBolt("SUBOperatorBolt",new SUBOperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","substream").shuffleGrouping("RedisValueLookupBolt", "substream").shuffleGrouping("GTOperatorBolt","substream")
    	.shuffleGrouping("LTOperatorBolt","substream").shuffleGrouping("ANDOperatorBolt","substream").shuffleGrouping("OROperatorBolt","substream")
    	.shuffleGrouping("ADDOperatorBolt","substream").shuffleGrouping("MULOperatorBolt","substream").shuffleGrouping("SUBOperatorBolt","substream").shuffleGrouping("INOperatorBolt","substream");
    	
    	tp.setBolt("INOperatorBolt",new INOperatorBolt()).shuffleGrouping("RuleExecutionStartBolt","instream").shuffleGrouping("RedisValueLookupBolt", "instream").shuffleGrouping("GTOperatorBolt","instream")
    	.shuffleGrouping("LTOperatorBolt","instream").shuffleGrouping("ANDOperatorBolt","instream").shuffleGrouping("OROperatorBolt","instream")
    	.shuffleGrouping("ADDOperatorBolt","instream").shuffleGrouping("MULOperatorBolt","instream").shuffleGrouping("SUBOperatorBolt","instream").shuffleGrouping("INOperatorBolt","instream");
    	
    	Config conf = new Config();
        conf.setMaxSpoutPending(5);
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("wordCounter", conf, tp.createTopology());
    }
}
