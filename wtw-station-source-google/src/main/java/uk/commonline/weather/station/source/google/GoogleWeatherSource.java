package uk.commonline.weather.station.source.google;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import uk.commonline.weather.model.Source;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.station.source.WeatherStationSource;

public class GoogleWeatherSource implements WeatherStationSource {

    private static Logger log = Logger.getLogger(GoogleWeatherSource.class);

    @Inject
    private GoogleWeatherRetriever googleWeatherRetriever;

    @Inject
    private GoogleWeatherParser googleWeatherParser;

    @Override
    public Weather getCurrentWeather(double latitude, double longitude) throws Exception {
        Weather w = null;

        try {
            String currentData = getGoogleWeatherRetriever().getWeatherData(latitude, longitude);

            // Parse DataSet
            Source source = new Source();
            source.setName(getSourceName());
            // report =
            // getGoogleWeatherParser().parseCurrentWeatherReport(source,
            // currentData);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return w;
    }

    @Override
    public List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception {
        List<WeatherForecast> report = new ArrayList<WeatherForecast>();

        try {
            String currentData = getGoogleWeatherRetriever().getWeatherData(latitude, longitude);

            // Parse DataSet
            Source source = new Source();
            source.setName(getSourceName());
            // report =
            // getGoogleWeatherParser().parseForecastWeatherReport(source,
            // currentData);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return report;
    }

    public GoogleWeatherParser getGoogleWeatherParser() {
        return googleWeatherParser;
    }

    public GoogleWeatherRetriever getGoogleWeatherRetriever() {
        return googleWeatherRetriever;
    }

    @Override
    public String getSourceName() {
        return "google";
    }

    @Override
    public List<Weather> report(double latitude, double longitude) {

        List<Weather> report = new ArrayList<Weather>();
        try {
            String currentData = getGoogleWeatherRetriever().getWeatherData(latitude, longitude);
            String forecastData = getGoogleWeatherRetriever().getForecastWeatherData(latitude, longitude, 5);

            // Parse DataSet
            Source source = new Source();
            source.setName(getSourceName());
            report = getGoogleWeatherParser().parseWeatherReport(source, currentData, forecastData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return report;
    }

    public void setGoogleWeatherParser(GoogleWeatherParser googleWeatherParser) {
        this.googleWeatherParser = googleWeatherParser;
    }

    public void setGoogleWeatherRetriever(GoogleWeatherRetriever googleWeatherRetriever) {
        this.googleWeatherRetriever = googleWeatherRetriever;
    }

}