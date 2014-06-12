package uk.commonline.weather.station.source.yahoo;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;

import uk.commonline.weather.model.Weather;
import uk.commonline.weather.station.service.WeatherStationService;
import uk.commonline.weather.station.source.WeatherStationSource;

public class YahooWeatherSource implements WeatherStationSource, WeatherStationService {

    @Autowired
    private YahooWeatherRetriever yahooWeatherRetriever;

    @Autowired
    private YahooWeatherParser yahooWeatherParser;

    @Override
    public Weather retrieveForecast(String zip) {

	Weather weather = null;
	weather = new Weather();
	// location.setCity("Unknown");

	try {
	    // Retrieve Data
	    InputStream dataIn = yahooWeatherRetriever.retrieveByZip(zip);

	    // Parse DataSet
	    weather = yahooWeatherParser.parseWeather(dataIn);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return weather;
    }

    @Override
    public WeatherStationService getWeatherStationService() {
	return this;
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

    public void setYahooWeatherParser(YahooWeatherParser yahooGeoLocationParser) {
	this.yahooWeatherParser = yahooGeoLocationParser;
    }

    public void setYahooWeatherRetriever(YahooWeatherRetriever yahooGeoLocationRetriever) {
	this.yahooWeatherRetriever = yahooGeoLocationRetriever;
    }

}