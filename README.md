# RuleExecutioner
Rule Execution Using Apache storm ,Apache kafka, Redis
1.Run the RuleGenerator by giving a rule json string like:
	
	{
	"operator": "and",
	"loperand": {
		"operator": "gt",
		"loperand": {
			"operator": "add",
			"loperand": {
				"value": "R13",
				"state": "avg",
				"type": "Variable"
			},
			"roperand": {
				"value": "R14",
				"state": "avg",
				"type": "Variable"
			}
		},
		"roperand": {
			"operator": "mul",
			"loperand": {
				"value": 15,
				"state": "",
				"type": "Constant"
			},
			"roperand": {
				"value": 9,
				"state": "",
				"type": "Constant"
			}
		}
	},
	"roperand": {
		"operator": "or",
		"loperand": {
			"operator": "gt",
			"loperand": {
				"value": "R13",
				"state": "latest",
				"type": "Variable"
			},
			"roperand": {
				"value": "R14",
				"state": "avg",
				"type": "Variable"
			}
		},
		"roperand": {
			"operator": "lt",
			"loperand": {
				"value": "R13",
				"state": "latest",
				"type": "Variable"
			},
			"roperand": {
				"value": "R13",
				"state": "max",
				"type": "Variable"
			}
		}
	}
}

json contain : {"operator": "and","loperand": {},"roperand": {}}

operand have four value 
				value->it can be a constant or a object id like (5 or R13(ruleID))
				(create a hash with object id hashkey and (avg,latest,max,min) as key and set some value to each key)
				state->if value is a ruleid what is the state (avg,min,max,latest)
				type->value is constant the set it to constant if value is a variable set it to variable
				operator->it contain the operator as string it may not present if this is the end of tree operand
				
this operation will convert the json string and store in redisdb its like creating a rule in a ruleset and it create a rule in redisdb
{
	"ruleId": "Rule1",
	"ruleSteps": [{
		"operator": "add",
		"loperand": "R13",
		"roperand": "R14",
		"ltype": "Variable",
		"rtype": "Variable",
		"lstate": "avg",
		"rstate": "avg"
	}, {
		"operator": "mul",
		"loperand": 15,
		"roperand": 9,
		"ltype": "Constant",
		"rtype": "Constant",
		"lstate": "",
		"rstate": ""
	}, {
		"operator": "gt",
		"loperand": null,
		"roperand": null,
		"ltype": null,
		"rtype": null,
		"lstate": null,
		"rstate": null
	}, {
		"operator": "gt",
		"loperand": "R13",
		"roperand": "R14",
		"ltype": "Variable",
		"rtype": "Variable",
		"lstate": "latest",
		"rstate": "avg"
	}, {
		"operator": "lt",
		"loperand": "R13",
		"roperand": "R13",
		"ltype": "Variable",
		"rtype": "Variable",
		"lstate": "latest",
		"rstate": "max"
	}, {
		"operator": "or",
		"loperand": null,
		"roperand": null,
		"ltype": null,
		"rtype": null,
		"lstate": null,
		"rstate": null
	}, {
		"operator": "and",
		"loperand": null,
		"roperand": null,
		"ltype": null,
		"rtype": null,
		"lstate": null,
		"rstate": null
	}]
}	

when ever data comes from kafka this {"data":{"R13":40,"R14":13},ruleIds:["rule:set1"]}	
application will execute the rule set with multiple rules in it and show true or false value for that each rule		