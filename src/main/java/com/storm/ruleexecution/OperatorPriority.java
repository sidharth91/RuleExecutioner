package com.storm.ruleexecution;

public enum OperatorPriority {
		and(2,"and","&","&"),or(2,"or","|","|"),
		eq(3,"eq","==","=="),neq(3,"neq","!=","!="),gt(3,"gt",">",">(?!=)"),lt(3,"lt","<","<(?!=)"),lteq(3,"lteq","<=","<="),gteq(3,"gteq",">=",">="),eqs(3,"eqs","eq","eq"),in(3,"in","in","in"),
		add(4,"add","+","+"),sub(4,"sub","-","-"),mul(5,"mul","*","*"),div(5,"div","/","/"),
		leftbracket(0,"leftbracket","(","("),rightbracket(1,"rightbracket",")",")");
	
	   private final int priority;
	   private final String key;
	   private final String operator;
	   private final String regex;

	   OperatorPriority(int priority,String key,String operator,String regex) {
	        this.priority = priority;
	        this.key = key;
	        this.operator = operator;
	        this.regex = regex;
	    }
	    
	 
		public String getKey() {
			return key;
		}

		public String getOperator() {
			return operator;
		}

		public int getPriority() {
			return priority;
		}

		public String getRegex() {
			return regex;
		}
	    
		
		
	    
	
}
