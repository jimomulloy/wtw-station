package uk.commonline.weather.station.source.xively;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class XivelyWeatherRetriever {

    private static Logger log = Logger.getLogger(XivelyWeatherRetriever.class);

    private String yahooAppid = "nHC8SgfV34FhrOczpZWEwOtNUU44RVqanKQjsAb2soBuM2LhXEt.gJVIkDzvU4sXvQ--";

    public InputStream retrieveById(long id) throws Exception {
        String url = "http://weather.yahooapis.com/forecastrss?w=" + id;
        log.info("Retrieving Weather Data for url:" + url);
        URLConnection conn = new URL(url).openConnection();
        return conn.getInputStream();
    }

    public InputStream retrieveHistoricWeatherById(long id, long periodFrom, long periodTo) throws Exception {
        return null;
    }

    public void setUrl(String url) {

    }

}
