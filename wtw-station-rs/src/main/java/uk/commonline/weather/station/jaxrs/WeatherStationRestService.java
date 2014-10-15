package uk.commonline.weather.station.jaxrs;

import java.util.ArrayList;
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
import uk.commonline.weather.station.service.WeatherStationService;

/**
 * @author Jim O'Mulloy
 * 
 *         WTW Station JAXRS Service
 *
 */
@Path("/station")
@Component
public class WeatherStationRestService {

    @Inject
    WeatherStationService weatherStationService;

    @GET
    @Path("current/lat/{lat}/long/{long}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Weather> getCurrentWeather(@PathParam("lat") double latitude, @PathParam("long") double longitude) throws Exception {
        List<Weather> w = new ArrayList<Weather>();
        try {
            w = weatherStationService.getCurrentWeather(latitude, longitude);
            return w;
        } catch (Exception ex) {
            ex.printStackTrace();
            return w;
        }
    }

    public Class<Weather> getEiClass() {
        return Weather.class;
    }

    @GET
    @Path("forecast/lat/{lat}/long/{long}/hours/{hours}/count/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<WeatherForecast> getForecastWeather(@PathParam("lat") double latitude, @PathParam("long") double longitude,
            @PathParam("hours") int hours, @PathParam("count") int count) throws Exception {
        List<WeatherForecast> w = new ArrayList<WeatherForecast>();
        try {
            w = weatherStationService.getForecastWeather(latitude, longitude, hours, count);
            return w;
        } catch (Exception ex) {
            ex.printStackTrace();
            return w;
        }
    }

    @POST
    @Path("report/lat/{lat}/long/{long}")
    @Produces(MediaType.APPLICATION_JSON)
    public WeatherReport getWeatherReport(@PathParam("lat") double latitude, @PathParam("long") double longitude) throws Exception {
        WeatherReport w = new WeatherReport();
        try {
            w = weatherStationService.getWeatherReport(latitude, longitude);
            return w;
        } catch (Exception ex) {
            ex.printStackTrace();
            return w;
        }
    }

    protected WeatherStationService getWeatherStationService() {
        return weatherStationService;
    }

    public void setWeatherStationService(WeatherStationService weatherStationService) {
        this.weatherStationService = weatherStationService;
    }
}
