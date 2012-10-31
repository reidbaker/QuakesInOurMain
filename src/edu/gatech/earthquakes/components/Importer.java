package edu.gatech.earthquakes.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import edu.gatech.earthquakes.model.DataSet;

public class Importer {
    private final static String dataLocation = "../data/";
    private final static String fileName = "Catalog.txt";

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

    private static void readQuake(BufferedReader reader) throws IOException{
        Map<String, Object> = new M
        String yearRecord = reader.readLine();
        createDate(yearRecord.split("\\s")[0]);

        System.out.println(reader.readLine());
    }

    private static String createDate(String yearMonthDay){
        return yearMonthDay;
    }

}
