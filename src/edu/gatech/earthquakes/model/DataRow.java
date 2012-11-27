package edu.gatech.earthquakes.model;

import java.util.Map;

public class DataRow {
    public final static String DATE = "Date";
    public final static String RECORD = "Record";
    public final static String LATTITUDE = "Lattitude";
    public final static String LONGITUDE = "Longitude";
    public final static String TIME = "Time";
    public final static String CONTINENT = "Continent";
    public enum Continent {
        AFRICA("Africa"),
        AUSTRALIA("Australia"),
        ASIA("Asia"),
        EURASIA("Eurasia"),
        INDIA("India"),
        NORTH_AMERICA("North America"),
        SOUTH_AMERICA("South America");
        
        private String text;
        
        private Continent(String text){
        	this.text = text;
        }
        
        public String toString(){
        	return text;
        }
    }
    public final static String DEPTH = "Depth";
    public final static String MOMENT_MAGNITUDE = "Magnitude";
    public final static String MOMENT_MAGNITUDE_UNCERTAINTY = "Magnitude_Uncertainty";
    public final static String BODY_WAVE_MAGNITUDE = "Body Wave Magnitude";
    public final static String SURFACE_WAVE_MAGNITUDE = "Surface Wave Magnitude";
    public final static String LOCAL_WAVE_MAGNITUDE = "Local Wave Magnitude";
    public final static String DEPENDENCY = "Dependency";
    public final static String MAIN_DATE = "Main Date";
    public enum Dependency{
        INDEPENDENT("Independent"),
        DEPENDENT("Dependent"),
        POSSIBLY("Possibly");
        
        private String text;
        
        private Dependency(String text){
        	this.text = text;
        }
        
        public String toString(){
        	return text;
        }
    }
    public final static String TYPE = "Type";
    public enum Type{
        TECT("Tectonic"),
        DEEP_MINING("Deep Mining"),
        MINING("Mining"),
        RESERVOIR("Reservoir"),
        OIL_FEILD("Oil Field");
        
        private String text;
        
        private Type(String text){
        	this.text = text;
        }
        
        public String toString(){
        	return text;
        }
    }

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
	
	public Object getValue(String dataType){
		return variables.get(dataType);
	}

}
