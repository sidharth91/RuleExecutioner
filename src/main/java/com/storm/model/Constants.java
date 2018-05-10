package com.storm.model;

public class Constants {
	public enum OperatorConstant {
		and("and", "andstream"), or("or", "orstream"), gt("gt", "gtstream"), lt("lt", "ltstream");

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
}
