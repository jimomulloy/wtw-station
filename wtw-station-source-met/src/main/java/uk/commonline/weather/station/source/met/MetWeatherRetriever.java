package uk.commonline.weather.station.source.met;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class MetWeatherRetriever {

    private static Logger log = Logger.getLogger(MetWeatherRetriever.class);

    private static String BASE_FORECAST_URL = "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/{id}?res=daily&key=";

    private static String BASE_URL = "http://datapoint.metoffice.gov.uk/public/data/val/wxobs/all/json/{id}?res=hourly&key=";

    private String metAppid = "b6bb5fb9-3846-46c1-8f3c-2e2ecd9c790e";

    public String getForecastWeatherData(long id) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {

            // Forecast
            String url = BASE_FORECAST_URL.replaceAll("\\{id\\}", Long.toString(id)) + metAppid;
            ;
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuffer buffer1 = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(is));
            String line1 = null;
            while ((line1 = br1.readLine()) != null)
                buffer1.append(line1 + "\r\n");

            is.close();
            con.disconnect();
            return buffer1.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

    public String getHistoricWeatherData(long id, long periodFrom, long periodTo) {

        return null;

    }

    public String getWeatherData(long id) {
        HttpURLConnection con = null;
        InputStream is = null;

        try {
            String url = BASE_URL.replaceAll("\\{id\\}", Long.toString(id)) + metAppid;
            ;

            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("GET");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.connect();

            // Let's read the response
            StringBuffer buffer = new StringBuffer();
            is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null)
                buffer.append(line + "\r\n");

            is.close();
            con.disconnect();

            return buffer.toString();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }

        return null;

    }

}
