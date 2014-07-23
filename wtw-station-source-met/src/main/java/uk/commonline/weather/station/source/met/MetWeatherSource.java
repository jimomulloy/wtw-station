package uk.commonline.weather.station.source.met;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import uk.commonline.weather.geo.service.GeoLocationService;
import uk.commonline.weather.model.Source;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.station.source.WeatherStationSource;

public class MetWeatherSource implements WeatherStationSource {

    private static Logger log = Logger.getLogger(MetWeatherSource.class);

    @Inject
    private MetWeatherRetriever metWeatherRetriever;

    @Inject
    private MetWeatherParser metWeatherParser;

    @Inject
    private GeoLocationService geoLocationService;

    @Override
    public List<Weather> report(double latitude, double longitude) {
	String id = geoLocationService.getLocationId(getSourceName(), latitude, longitude);
	//id = "3628";
	List<Weather> report = new ArrayList<Weather>();
	System.out.println("Met report:"+", lat:"+latitude+", lon:"+ longitude + ", id:"+id);
	try {
	    String currentData = getMetWeatherRetriever().getWeatherData(Long.parseLong(id));
	    if (currentData == null) {
		throw new Exception("getMetWeatherRetriever().getWeatherData null stream");
	    }
	    String forecastData = getMetWeatherRetriever().getForecastWeatherData(Long.parseLong(id));
	    if (forecastData == null) {
		throw new Exception("getMetWeatherRetriever().getForecastWeatherData null stream");
	    }
	    Source source = new Source();
	    source.setName(getSourceName());
	    // Parse DataSet
	    report = getMetWeatherParser().parseWeatherReport(source, currentData, forecastData);
	    System.out.println("Met current data:"+currentData);
	    System.out.println("Met forecast data:"+forecastData);
	    for(Weather w: report){
		System.out.println("Met report weather:"+w);
	    }

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return report;
    }

    @Override
    public String getSourceName() {
	return "met";
    }

    public MetWeatherRetriever getMetWeatherRetriever() {
	return metWeatherRetriever;
    }

    public void setMetWeatherRetriever(MetWeatherRetriever metWeatherRetriever) {
	this.metWeatherRetriever = metWeatherRetriever;
    }

    public MetWeatherParser getMetWeatherParser() {
	return metWeatherParser;
    }

    public void setMetWeatherParser(MetWeatherParser metWeatherParser) {
	this.metWeatherParser = metWeatherParser;
    }

    public GeoLocationService getGeoLocationService() {
	return geoLocationService;
    }

    public void setGeoLocationService(GeoLocationService geoLocationService) {
	this.geoLocationService = geoLocationService;
    }

    @Override
    public Weather getCurrentWeather(double latitude, double longitude) throws Exception {
	String id = geoLocationService.getLocationId(getSourceName(), latitude, longitude);
	Weather w = null;

	try {
	    String currentData = getMetWeatherRetriever().getWeatherData(Long.parseLong(id));
	    if (currentData == null) {
		throw new Exception("getMetWeatherRetriever().getWeatherData null stream");
	    }
	    Source source = new Source();
	    source.setName(getSourceName());
	    w = getMetWeatherParser().parseCurrentWeather(source, currentData);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return w;
    }

    @Override
    public List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception {
	List<WeatherForecast> report = new ArrayList<WeatherForecast>();
	String id = geoLocationService.getLocationId(getSourceName(), latitude, longitude);
	id = "3628";

	try {
	    String forecastData = getMetWeatherRetriever().getForecastWeatherData(Long.parseLong(id));
	    if (forecastData == null) {
		throw new Exception("getMetWeatherRetriever().getForecastWeatherData null stream");
	    }
	    Source source = new Source();
	    source.setName(getSourceName());
	    report.addAll(getMetWeatherParser().parseForecastWeather(source, forecastData));

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return report;
    }

}