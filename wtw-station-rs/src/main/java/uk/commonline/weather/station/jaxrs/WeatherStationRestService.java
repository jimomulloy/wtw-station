package uk.commonline.weather.station.jaxrs;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.model.WeatherReport;
import uk.commonline.weather.station.service.WeatherStationManager;

/**
 * Location controller.
 * 
 */
@Path("/station")
@Component
@Transactional
public class WeatherStationRestService /* implements WeatherStationService */{

    @Inject
    WeatherStationManager weatherStationManager;

    protected WeatherStationManager getWeatherStationManager() {
	return weatherStationManager;
    }

    public void setWeatherStationManager(WeatherStationManager weatherStationManager) {
	this.weatherStationManager = weatherStationManager;
    }

    public Class<Weather> getEiClass() {
	return Weather.class;
    }

    @POST
    @Path("report/lat/{lat}/long/{long}")
    @Produces(MediaType.APPLICATION_JSON)
    public WeatherReport getWeatherReport(@PathParam("lat") double latitude, @PathParam("long") double longitude) throws Exception {
	try {
	    WeatherReport w = weatherStationManager.getWeatherReport(latitude, longitude);
	    return w;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    return null;
	}
    }

    @GET
    @Path("current/lat/{lat}/long/{long}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Weather> getCurrentWeather(@PathParam("lat") double latitude, @PathParam("long") double longitude) throws Exception {
	try {
	    List<Weather> w = weatherStationManager.getCurrentWeather(latitude, longitude);
	    return w;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    return null;
	}
    }

    @GET
    @Path("forecast/lat/{lat}/long/{long}/hours/{hours}/count/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WeatherForecast> getForecastWeather(@PathParam("lat") double latitude, @PathParam("long") double longitude,
	    @PathParam("hours") int hours, @PathParam("count") int count) throws Exception {
	try {
	    List<WeatherForecast> w = weatherStationManager.getForecastWeather(latitude, longitude, hours, count);
	    return w;
	} catch (Exception ex) {
	    ex.printStackTrace();
	    return null;
	}
    }
}
