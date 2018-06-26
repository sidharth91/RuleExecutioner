package com.storm.model;

public class Constants {
	public enum OperatorConstant {
		and("&", "andstream"), or("|", "orstream"), gt(">", "gtstream"), lt("<", "ltstream"),add("+","addstream"),sub("-","substream"),mul("*","mulstream"),in("in","instream"),div("/","divstream")
		,gteq(">=","gteqstream"),lteq("<=","lteqstream");

		private String key;
		private String value;

		private OperatorConstant(String key, String value) {
			this.value = value;
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public String getKey() {
			return key;
		}
	}

	
	public enum ValueType{
		Constant,Variable;
	}
	
	public enum Side{
		Left,Right;
	}
	
	public enum State{
		latest,avg,max,min;
	}
}
