package uk.commonline.weather.station.source.yahoo;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class YahooWeatherRetriever {

	private static Logger log = Logger.getLogger(YahooWeatherRetriever.class);
	
	private String yahooAppid = "nHC8SgfV34FhrOczpZWEwOtNUU44RVqanKQjsAb2soBuM2LhXEt.gJVIkDzvU4sXvQ--";

	public InputStream retrieveByZip(String zipcode) throws Exception {
		log.info( "Retrieving Weather Data" );
		String url = "http://where.yahooapis.com/v1/places$and(.q('"+zipcode+"'),.type(7));count=1?appid="+yahooAppid;
		URLConnection conn = new URL(url).openConnection();
		return conn.getInputStream();
	}
	
	public void setUrl(String url){
	    
	}

}
