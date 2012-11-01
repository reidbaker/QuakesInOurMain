package edu.gatech.earthquakes.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class Importer {
	private final static String dataLocation = "../data/";
	private final static String fileName = "Catalog.csv";

	public static DataSet importData() {
		Set<DataRow> dataRows = new HashSet<DataRow>();
		try {
			File f = new File(dataLocation + fileName);
			System.out.println(f.getCanonicalPath());
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					fileName)));// dataLocation + fileName)));

			while (reader.ready()) {
				String[] data = reader.readLine().split(",");
				dataRows.add(readQuake(data));
			}

			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new DataSet(dataRows);
	}

	private static DataRow readQuake(String[] data) throws IOException {
		Map<String, Object> curQuake = Maps.newHashMap();

		String date = data[0];
		curQuake.put(DataRow.DATE, createDate(date));

		String record = data[1];
		curQuake.put(DataRow.RECORD, record);
		

		Double lat = parseDoubleMissing(data[2]);
		curQuake.put(DataRow.LATTITUDE, lat);

		Double lon = parseDoubleMissing(data[3]);
		curQuake.put(DataRow.LONGITUDE, lon);

		String time = data[4];
		curQuake.put(DataRow.TIME, timeConvert(time));

		String continent = data[5];
		curQuake.put(DataRow.CONTINENT, continentConvert(continent));

		String depth = data[6];
		curQuake.put(DataRow.DEPTH, parseDoubleMissing(depth));

		String momentMagnitude = data[7];
		curQuake.put(DataRow.MOMENT_MAGNITUDE,
				parseDoubleMissing(momentMagnitude));

		String bodyWaveMagnitude = data[8];
		curQuake.put(DataRow.BODY_WAVE_MAGNITUDE,
				parseDoubleMissing(bodyWaveMagnitude));

		String surfaceWaveMagnitude = data[9];
		curQuake.put(DataRow.SURFACE_WAVE_MAGNITUDE,
				parseDoubleMissing(surfaceWaveMagnitude));

		String localWaveMagnitude = data[10];
		curQuake.put(DataRow.LOCAL_WAVE_MAGNITUDE,
				parseDoubleMissing(localWaveMagnitude));

		String eventDep = data[31];
		curQuake.put(DataRow.DEPENDENCY, findDependancy(eventDep));

		String eventType = data[33];
		curQuake.put(DataRow.TYPE, findType(eventType));

		return new DataRow(curQuake);
	}

	private static Date createDate(String yearMonthDay) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH)
					.parse(yearMonthDay);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	private static String timeConvert(String time) {
		// the input --- is what is passed when data is not there
		return time;
	}

	private static DataRow.Continent continentConvert(String continent) {
		// the input --- is what is passed when data is not there
		switch (continent) {
		case "AF":
			return DataRow.Continent.AFRICA;
		case "CH":
			return DataRow.Continent.CHINA;
		case "EU":
			return DataRow.Continent.EURASIA;
		case "IN":
			return DataRow.Continent.INDIA;
		case "NA":
			return DataRow.Continent.NORTH_AMERICA;
		case "SA":
			return DataRow.Continent.SOUTH_AMERICA;
		}
		return null;
	}

	private static DataRow.Dependency findDependancy(String dep) {
		// the input --- is what is passed when data is not there
		switch(dep){
		case "I" : return DataRow.Dependency.INDEPENDENT;
		case "D" : return DataRow.Dependency.DEPENDENT;
		case "P" : return DataRow.Dependency.POSSIBLY;
		}
		return null;
	}

	private static DataRow.Type findType(String type) {
		// the input --- is what is passed when data is not there
		switch (type) {
		case "tect.":
			return DataRow.Type.TECT;
		case "deep min.":
			return DataRow.Type.DEEP_MINING;
		case "mining":
			return DataRow.Type.MINING;
		case "reservoir":
			return DataRow.Type.RESERVOIR;
		case "oil field":
			return DataRow.Type.OIL_FEILD;
		case "-":
			return null;
		}
		return DataRow.Type.TECT;
	}

	private static Double parseDoubleMissing(String num) {
		Double parsedNumber = null;
		try {
			parsedNumber = Double.parseDouble(num);
		} catch (Exception e) {
			parsedNumber = null;
		}
		return parsedNumber;
	}
}
