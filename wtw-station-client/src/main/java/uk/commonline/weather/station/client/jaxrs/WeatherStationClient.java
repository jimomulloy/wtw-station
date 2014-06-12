package uk.commonline.weather.station.client.jaxrs;

import javax.ws.rs.core.MediaType;

import uk.commonline.data.client.jaxrs.AbstractCrudClient;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.station.service.WeatherStationService;

/**
 */
public class WeatherStationClient extends AbstractCrudClient<Weather> implements WeatherStationService {

    protected String getPath() {
	return "station";
    }

    @Override
    public Weather retrieveForecast(String zip) {
	Weather weather = getRestClient().getClient().target(getRestClient().createUrl("http://localhost:8080/wtwbase/webresources/"))
		.path(getPath()).path("zip/{zip}").resolveTemplate("zip", zip).request(MediaType.APPLICATION_JSON).get(Weather.class);
	if (weather == null) {
	    weather = newCiInstance();
	}
	return weather;
    }
}
