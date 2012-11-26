package edu.gatech.earthquakes.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Scanner;


public class CustomSearch {
    private final static String SEARCH_BASE = "https://www.googleapis.com/customsearch/v1";
    private final static String dataLocation = ".." + File.separator + "data" + File.separator;
    private final static String fileName = "config.properties";
    private final static String cacheFolder = dataLocation + "cache" + File.separator;
    //custom search api key
    private String key;
    //custom search engine key
    private String cx;

    public CustomSearch(){
        this(dataLocation + fileName);
    }

    public CustomSearch(String filepath_name) {
        Properties prop = new Properties();
        try {
            //load a properties file
            prop.load(new FileInputStream(filepath_name));
            key = prop.getProperty("key");
            cx = prop.getProperty("cx");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private URL getUrl(String query) throws MalformedURLException{
        String escaped_q = query;
        try {
            escaped_q = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String attempted_url = SEARCH_BASE + "?" + "key=" + key + "&cx=" + cx + "&q=" + escaped_q;
        //System.out.println(attempted_url);
        return new URL(attempted_url);
    }

    private static final String getOnlineContent(final URL url) throws IOException{
        //returns string of html page
        final InputStream in = url.openStream();
        final StringBuffer sb = new StringBuffer();

        final byte [] buffer = new byte[256];

        while(true){
            int byteRead = in.read(buffer);
            if(byteRead == -1){
                break;
            }
            for(int i = 0; i < byteRead; i++){
                sb.append((char)buffer[i]);
            }
        }
        return sb.toString();
    }

    public String getQuery(String query) throws NoSuchAlgorithmException, MalformedURLException, IOException{
        String result;
        byte[] bytesOfMessage = query.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        String filename = new String(thedigest);
        File f = new File(cacheFolder + filename);

        if(f.exists() && !f.isDirectory()){
            //file is in cache
            Scanner s = new Scanner(f);
            result = s.useDelimiter("\\Z").next();
            s.close();
            System.out.println("From cache: " + query);
        }
        else{
            BufferedWriter out = new BufferedWriter(new FileWriter(f));
            result = getOnlineContent(getUrl(query));
            out.write(result);
            out.close();
            System.out.println("From web: " + query);
        }
        return result;
    }

}
