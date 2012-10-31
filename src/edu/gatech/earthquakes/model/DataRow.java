package edu.gatech.earthquakes.model;

import java.util.Map;

public class DataRow {

	private Map<String, Object> variables;
	
	public DataRow(Map<String, Object> variables){
		this.variables = variables;
	}

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}
	
}
