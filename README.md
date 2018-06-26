# RuleExecutioner
Rule Execution Using Apache storm ,Apache kafka, Redis
1.Run the RuleGenerator by giving a rule  string like:
	((34567:Variable:latest > 30)&(34567:Variable:latest>12345:Variable:avg))
	
2.redis db push some data if we are going to use avg max and min for checking
	redis 127.0.0.1:6379> hgetall 12345
							1) "avg"
							2) "38"
                            3) "max"
							4) "43"
							5) "min"
							6) "41"
							redis 127.0.0.1:6379> hgetall 23456
							1) "avg"
							2) "4"
							3) "min"
							4) "2"
							5) "max"
							6) "6"
json contain : {"operator": "and","loperand": {},"roperand": {}}

operand have four value 
				value->it can be a constant or a object id like (5 or R13(ruleID))
				(create a hash with object id hashkey and (avg,latest,max,min) as key and set some value to each key)
				state->if value is a ruleid what is the state (avg,min,max,latest)
				type->value is constant the set it to constant if value is a variable set it to variable
				operator->it contain the operator as string it may not present if this is the end of tree operand
				
this operation will convert the json string and store in redisdb its like creating a rule in a ruleset and it create a rule in redisdb
{{
  "ruleId": "Rule1",
  "ruleSteps": [
    {
      "operator": ">=",
      "loperand": "34567",
      "roperand": "38",
      "ltype": "Variable",
      "rtype": "Constant",
      "lstate": "latest",
      "rstate": "latest"
    },
    {
      "operator": "<=",
      "loperand": "34567",
      "roperand": "12345",
      "ltype": "Variable",
      "rtype": "Variable",
      "lstate": "latest",
      "rstate": "avg"
    },
    {
      "operator": "&"
    }
  ]
}

when ever data comes from kafka this {"data":{"34567":40,"56789":"test"},"ruleIds":["rule:set1"]}
application will execute the rule set with multiple rules in it and show true or false value for that each rule		