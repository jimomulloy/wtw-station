package uk.commonline.weather.station.source.xively;

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

public class XivelyWeatherSource implements WeatherStationSource {

    private static Logger log = Logger.getLogger(XivelyWeatherSource.class);

    @Inject
    private XivelyWeatherRetriever xivelyWeatherRetriever;

    @Inject
    private XivelyWeatherParser xivelyWeatherParser;

    @Inject
    private GeoLocationService geoLocationService;

    @Override
    public List<Weather> report(double latitude, double longitude) {
	String id = geoLocationService.getLocationId(getSourceName(), latitude, longitude);
	List<Weather> report = new ArrayList<Weather>();
	// location.setCity("Unknown");

	try {
	    InputStream dataIn = xivelyWeatherRetriever.retrieveById(Long.parseLong(id));
	    if (dataIn == null) {
		throw new Exception("xivelyWeatherRetriever.retrieveByIdnull stream");
	    }
	    // Parse DataSet
	    Source source = new Source();
	    source.setName(getSourceName());
	    report = xivelyWeatherParser.parseWeatherReport(source, dataIn);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return report;
    }

    @Override
    public String getSourceName() {
	return "xively";
    }

    public XivelyWeatherParser getXivelyWeatherParser() {
	return xivelyWeatherParser;
    }

    public XivelyWeatherRetriever getXivelyWeatherRetriever() {
	return xivelyWeatherRetriever;
    }

    public void setXivelyWeatherParser(XivelyWeatherParser xivelyWeatherParser) {
	this.xivelyWeatherParser = xivelyWeatherParser;
    }

    public void setXivelyWeatherRetriever(XivelyWeatherRetriever xivelyWeatherRetriever) {
	this.xivelyWeatherRetriever = xivelyWeatherRetriever;
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
	// location.setCity("Unknown");

	try {
	    InputStream dataIn = xivelyWeatherRetriever.retrieveById(Long.parseLong(id));
	    if (dataIn == null) {
		throw new Exception("xivelyWeatherRetriever.retrieveById null stream");
	    }
	    // Parse DataSet
	    Source source = new Source();
	    source.setName(getSourceName());
	    w = xivelyWeatherParser.parseWeather(source, dataIn);

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
	    InputStream dataIn = xivelyWeatherRetriever.retrieveById(Long.parseLong(id));
	    if (dataIn == null) {
		throw new Exception("xivelyWeatherRetriever.retrieveById null stream");
	    }
	    // Parse DataSet
	    Source source = new Source();
	    source.setName(getSourceName());
	    report = xivelyWeatherParser.parseForecastWeather(source, dataIn, hours, count);

	} catch (Exception ex) {
	    ex.printStackTrace();
	}

	return report;
    }

}