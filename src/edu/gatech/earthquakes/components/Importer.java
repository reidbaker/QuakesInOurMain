package edu.gatech.earthquakes.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.gatech.earthquakes.model.DataRow;
import edu.gatech.earthquakes.model.DataSet;

public class Importer {
    private final static String dataLocation = "../data/";
    private final static String fileName = "Catalog.txt";

    public final static String DATE = "Date";
    public final static String RECORD = "Record";
    public static DataSet importData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(dataLocation + fileName)));
            readQuake(reader);
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

    private static DataRow readQuake(BufferedReader reader) throws IOException{
        Map<String, Object> curQuake = Maps.newHashMap();

        String[] yearRecord = reader.readLine().split("\\s");

        String date = yearRecord[0];
        curQuake.put(DATE, createDate(date));

        String record = yearRecord[1];
        curQuake.put(RECORD, record);

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

}
