package uk.commonline.weather.station.source.yahoo;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import uk.commonline.weather.geo.service.GeoLocationService;
import uk.commonline.weather.model.Source;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.station.source.WeatherStationSource;

public class YahooWeatherSource implements WeatherStationSource {

    private static Logger log = Logger.getLogger(YahooWeatherSource.class);

    @Inject
    private YahooWeatherRetriever yahooWeatherRetriever;

    @Inject
    private YahooWeatherParser yahooWeatherParser;

    @Inject
    private GeoLocationService geoLocationService;

    @Override
    public Weather getCurrentWeather(double latitude, double longitude) throws Exception {
        String id = geoLocationService.getLocationId(getSourceName(), latitude, longitude);
        Weather w = null;
        // location.setCity("Unknown");

        try {
            InputStream dataIn = yahooWeatherRetriever.retrieveById(Long.parseLong(id));
            if (dataIn == null) {
                throw new Exception("yahooWeatherRetriever.retrieveById null stream");
            }
            // Parse DataSet
            Source source = new Source();
            source.setName(getSourceName());
            w = yahooWeatherParser.parseWeather(source, dataIn);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return w;
    }

    @Override
    public List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception {
        String id = geoLocationService.getLocationId(getSourceName(), latitude, longitude);
        List<WeatherForecast> report = new ArrayList<WeatherForecast>();
        // location.setCity("Unknown");

        try {
            InputStream dataIn = yahooWeatherRetriever.retrieveById(Long.parseLong(id));
            if (dataIn == null) {
                throw new Exception("yahooWeatherRetriever.retrieveById null stream");
            }
            // Parse DataSet
            Source source = new Source();
            source.setName(getSourceName());
            report = yahooWeatherParser.parseForecastWeather(source, dataIn, hours, count);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return report;
    }

    public GeoLocationService getGeoLocationService() {
        return geoLocationService;
    }

    @Override
    public String getSourceName() {
        return "yahoo";
    }

    public YahooWeatherParser getYahooWeatherParser() {
        return yahooWeatherParser;
    }

    public YahooWeatherRetriever getYahooWeatherRetriever() {
        return yahooWeatherRetriever;
    }

    @Override
    public List<Weather> report(double latitude, double longitude) {
        String id = geoLocationService.getLocationId(getSourceName(), latitude, longitude);
        List<Weather> report = new ArrayList<Weather>();
        // location.setCity("Unknown");
        System.out.println("Yahoo report:" + ", lat:" + latitude + ", lon:" + longitude);
        try {
            InputStream dataIn = yahooWeatherRetriever.retrieveById(Long.parseLong(id));
            if (dataIn == null) {
                throw new Exception("yahooWeatherRetriever.retrieveByIdnull stream");
            }
            // Parse DataSet
            Source source = new Source();
            source.setName(getSourceName());
            report = yahooWeatherParser.parseWeatherReport(source, dataIn);

            System.out.println("Yahoo data:" + yahooWeatherRetriever.retrieveById(Long.parseLong(id)));
            for (Weather w : report) {
                System.out.println("Yahoo report weather:" + w);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return report;
    }

    public void setGeoLocationService(GeoLocationService geoLocationService) {
        this.geoLocationService = geoLocationService;
    }

    public void setYahooWeatherParser(YahooWeatherParser yahooWeatherParser) {
        this.yahooWeatherParser = yahooWeatherParser;
    }

    public void setYahooWeatherRetriever(YahooWeatherRetriever yahooWeatherRetriever) {
        this.yahooWeatherRetriever = yahooWeatherRetriever;
    }

}