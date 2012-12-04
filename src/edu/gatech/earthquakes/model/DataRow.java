package edu.gatech.earthquakes.model;

import java.util.Map;

import edu.gatech.earthquakes.components.Theme;

public class DataRow implements Comparable<DataRow>{
	
	public static int getColorFor(String enumText){
		int result = -1;
		for(Continent c : DataRow.Continent.values()){
			if(c.toString().equals(enumText)){
				result = c.getColor();
			}
		}
		for(Type t: Type.values()){
			if(t.toString().equals(enumText))
				result = t.getColor();
		}
		if(enumText.equals(DEPTH))
			result = Theme.getPalletteColor(15);
		if(result == -1)
			throw new IllegalArgumentException("Name has no corresponding enum");
		else
			return result;
	}
	
    public final static String DATE = "Date";
    public final static String RECORD = "Record";
    public final static String LATTITUDE = "Lattitude";
    public final static String LONGITUDE = "Longitude";
    public final static String TIME = "Time";
    public final static String CONTINENT = "Continent";
    public enum Continent {
        AFRICA("Africa", Theme.getPalletteColor(0)),
        AUSTRALIA("Australia", Theme.getPalletteColor(1)),
        ASIA("Asia", Theme.getPalletteColor(2)),
        EURASIA("Eurasia", Theme.getPalletteColor(3)),
        INDIA("India", Theme.getPalletteColor(4)),
        NORTH_AMERICA("North America", Theme.getPalletteColor(5)),
        SOUTH_AMERICA("South America", Theme.getPalletteColor(6));

        private String text;
        private int color;

        private Continent(String text, int color){
        	this.text = text;
        	this.color = color;
        }

        public String toString(){
        	return text;
        }
        
        public int getColor(){
        	return color;
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
        INDEPENDENT("Independent", Theme.getPalletteColor(7)),
        DEPENDENT("Dependent", Theme.getPalletteColor(8)),
        POSSIBLY("Possibly", Theme.getPalletteColor(9));

        private String text;
        private int color;

        private Dependency(String text, int color){
        	this.text = text;
        	this.color = color;
        }

        public String toString(){
        	return text;
        }
        
        public int getColor(){
        	return color;
        }
    }
    public final static String TYPE = "Type";
    public enum Type{
        TECT("Tectonic", Theme.getPalletteColor(10)),
        DEEP_MINING("Deep Mining", Theme.getPalletteColor(11)),
        MINING("Mining", Theme.getPalletteColor(12)),
        RESERVOIR("Reservoir", Theme.getPalletteColor(13)),
        OIL_FEILD("Oil Field", Theme.getPalletteColor(14));

        private String text;
        private int color;

        private Type(String text, int color){
        	this.text = text;
        	this.color = color;
        }

        public String toString(){
        	return text;
        }
        
        public int getColor(){
        	return color;
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

	@Override
        public int compareTo(DataRow arg0) {
	    return DataComparator.getDefaultComparator().compare(this, arg0);
        }

}
