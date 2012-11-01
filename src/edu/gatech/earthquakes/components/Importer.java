package edu.gatech.earthquakes.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class Importer {
    private final static String dataLocation = "../data/";
    private final static String fileName = "Catalog.txt";

    public static DataSet importData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(dataLocation + fileName)));
            String[] yearRecord = reader.readLine().split("\\s");
            // Most of the data
            String [] data = reader.readLine().split("\\s");

            while(yearRecord != null){
                readQuake(yearRecord, data);
                yearRecord = reader.readLine().split("\\s");
                // Most of the data
                data = reader.readLine().split("\\s");
            }
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static DataRow readQuake(String [] yearRecord, String [] data) throws IOException{
        Map<String, Object> curQuake = Maps.newHashMap();


        String date = yearRecord[0];
        curQuake.put(DataRow.DATE, createDate(date));

        String record = yearRecord[1];
        curQuake.put(DataRow.RECORD, record);

        double lat = parseDoubleMissing(data[1]);
        curQuake.put(DataRow.LATTITUDE, lat);

        double lon = parseDoubleMissing(data[2]);
        curQuake.put(DataRow.LONGITUDE, lon);

        String time = data[3];
        curQuake.put(DataRow.TIME, timeConvert(time));

        String continent = data[4];
        curQuake.put(DataRow.DEPTH, continentConvert(time));

        String depth = data[5];
        curQuake.put(DataRow.MOMENT_MAGNITUDE, parseDoubleMissing(depth));

        String momentMagnitude = data[6];
        curQuake.put(DataRow.MOMENT_MAGNITUDE_UNCERTAINTY, parseDoubleMissing(momentMagnitude));

        String bodyWaveMagnitude = data[7];
        curQuake.put(DataRow.BODY_WAVE_MAGNITUDE, parseDoubleMissing(bodyWaveMagnitude));

        String surfaceWaveMagnitude = data[8];
        curQuake.put(DataRow.SURFACE_WAVE_MAGNITUDE, parseDoubleMissing(surfaceWaveMagnitude));

        String localWaveMagnitude = data[9];
        curQuake.put(DataRow.LOCAL_WAVE_MAGNITUDE, parseDoubleMissing(localWaveMagnitude));

        String eventDep = data[29];
        curQuake.put(DataRow.DEPENDENCY, findDependancy(eventDep));

        String eventType = data[30];
        curQuake.put(DataRow.TYPE, findType(eventType));

        return new DataRow(curQuake);
    }

    private static Date createDate(String yearMonthDay){
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).parse(yearMonthDay);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    private static String timeConvert(String time){
        //the input --- is what is passed when data is not there
        return time;
    }

    private static String continentConvert(String continent){
        //the input --- is what is passed when data is not there
        return continent;
    }

    private static String findDependancy(String dep){
        //the input --- is what is passed when data is not there
        return dep;
    }

    private static DataRow.type findType(String type){
        //the input --- is what is passed when data is not there
/*(        switch (type){
        case "deep min": return DataRow.type.DEEP_MINING;
        case "-": return null;
        }
*/        return DataRow.type.TECT;
    }

    private static Double parseDoubleMissing(String num){
        Double parsedNumber = null;
        try{
            parsedNumber = Double.parseDouble(num);
        }catch(Exception e){
           parsedNumber = null;
        }
        return parsedNumber;
    }
}
