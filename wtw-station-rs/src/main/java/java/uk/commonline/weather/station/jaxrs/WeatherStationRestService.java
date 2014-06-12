package java.uk.commonline.weather.station.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import uk.commonline.weather.model.Location;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.station.service.WeatherStationManager;

/**
 * Location controller.
 * 
 */
@Path("/location")
@Component
@Transactional
public class WeatherStationRestService implements WeatherStationManager {

    @Autowired
    WeatherStationManager weatherStationManager;

    protected WeatherStationManager getService() {
	return weatherStationManager;
    }

    public void setLocationService(WeatherStationManager weatherStationManager) {
	this.weatherStationManager = weatherStationManager;
    }

    public Class<Weather> getEiClass() {
	return Weather.class;
    }

    @GET
    @Path("zip/{zip}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Weather retrieveForecast(@PathParam("zip") String zip) {
	try {
	    return weatherStationManager.retrieveForecast(zip);
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return null;
	}
    }
}
