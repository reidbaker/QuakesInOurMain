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

import org.jsoup.Jsoup;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class CustomSearch {
    private final static String SEARCH_BASE = "https://www.googleapis.com/customsearch/v1";
    private final static String DATA_LOCATION = /* ".." + File.separator + */"data"
	    + File.separator;
    private final static String PROPERTIES_FILENAME = "config.properties";
    private final static String CACHE_LOCATION = DATA_LOCATION + "cache"
	    + File.separator;
    // custom search api key
    private String key;
    // custom search engine key
    private String cx;

    private static CustomSearch instance;
    static {
	instance = new CustomSearch();
    }

    public static CustomSearch getInstance() {
	return instance;
    }

    // usage is CustomSearch cs = new CustomSearch(); cs.getQuery("some_query");
    private CustomSearch() {
	this(DATA_LOCATION + PROPERTIES_FILENAME);
    }

    public CustomSearch(final String filepath_name) {
	final Properties prop = new Properties();
	try {
	    // load a properties file
	    prop.load(new FileInputStream(filepath_name));
	    key = prop.getProperty("key");
	    cx = prop.getProperty("cx");
	} catch (IOException ex) {
	    ex.printStackTrace();
	}
    }

    private URL getUrl(final String query) throws MalformedURLException {
	String escaped_q = query;
	try {
	    escaped_q = URLEncoder.encode(query, "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	final String attempted_url = SEARCH_BASE + "?" + "key=" + key + "&cx="
	        + cx + "&q=" + escaped_q;
	return new URL(attempted_url);
    }

    private static final String getOnlineContent(final URL url)
	    throws IOException {
	// returns string of html page
	final InputStream in = url.openStream();
	final StringBuffer sb = new StringBuffer();

	final byte[] buffer = new byte[256];

	while (true) {
	    final int byteRead = in.read(buffer);
	    if (byteRead == -1) {
		break;
	    }
	    for (int i = 0; i < byteRead; i++) {
		sb.append((char) buffer[i]);
	    }
	}
	return sb.toString();
    }

    // TODO catch appropriate errors
    public String getQuery(String query) throws NoSuchAlgorithmException,
	    MalformedURLException, IOException {
	// this checks to see if the file is in the cache then returns the
	// results as a string
	String result;
	final byte[] bytesOfMessage = query.getBytes("UTF-8");
	final MessageDigest md = MessageDigest.getInstance("MD5");
	byte[] thedigest = md.digest(bytesOfMessage);
	final String filename = bytesToPrintableString(thedigest);
	File f = new File(CACHE_LOCATION + filename);

	// TODO handle if query happens to be a directory
	if (f.exists() && !f.isDirectory()) {
	    // file is in cache
	    Scanner s = new Scanner(f);
	    // TODO handle no such element exception
	    result = s.useDelimiter("\\Z").next();
	    s.close();
	} else {
	    // get content and write it to a file the return it
	    BufferedWriter out = new BufferedWriter(new FileWriter(f));
	    result = getOnlineContent(getUrl(query));
	    out.write(result);
	    out.close();
	}
	return result;
    }

    private String bytesToPrintableString(final byte[] thedigest) {
	// This is used to allow files to be saved in windows
	// Possible Chars are windows safe chars
	char[] possibleChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
	        '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
	        'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
	        'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
	        'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
	        'V', 'W', 'X', 'Y', 'Z', };
	char[] writeableChars = new char[thedigest.length];
	for (int i = 0; i < thedigest.length; i++) {
	    writeableChars[i] = possibleChars[Math.abs(thedigest[i])
		    % possibleChars.length];
	}
	return new String(writeableChars);
    }

    public static int getTotalCount(final String jsonLine) {
	// jsonline['queries']['request'][0]['totalResults']
	JsonElement jelement = new JsonParser().parse(jsonLine);
	String result = jelement.getAsJsonObject().getAsJsonObject("queries")
	        .getAsJsonArray("request").get(0).getAsJsonObject()
	        .get("totalResults").toString();
	// strips leading and trailing quotes
	result = result.replace('"', ' ').trim();
	return Integer.parseInt(result);
    }

    public static String getTitles(int index, String jsonLine) {
	// jdata['items'][1]['htmlTitle']
	JsonElement jelement = new JsonParser().parse(jsonLine);
	String result = jelement.getAsJsonObject().getAsJsonArray("items")
	        .get(index).getAsJsonObject().get("htmlTitle").toString();
	return Jsoup.parse(result).text();
    }
}
