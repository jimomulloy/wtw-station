package uk.commonline.weather.station.man;

import java.util.List;

import uk.commonline.weather.model.Weather;
import uk.commonline.weather.station.service.WeatherStationManager;
import uk.commonline.weather.station.source.WeatherStationSource;

public class WeatherStationManangerImpl implements WeatherStationManager {

    // @Autowired
    List<WeatherStationSource> weatherStationSources;

    protected List<WeatherStationSource> getWeatherStationSources() {
	return weatherStationSources;
    }

    public void setWeatherStationSources(List<WeatherStationSource> geoLocationSources) {
	this.weatherStationSources = geoLocationSources;
    }

    @Override
    public Weather retrieveForecast(String zip) throws Exception {
	Weather l = weatherStationSources.get(0).getWeatherStationService().retrieveForecast(zip);
	if (l == null) {
	    l = new Weather();
	    // l.setCity("London");
	}
	return l;
    }
}