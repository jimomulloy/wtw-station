package uk.commonline.weather.station.client.jaxrs;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import uk.commonline.data.client.jaxrs.AbstractCrudClient;
import uk.commonline.data.client.jaxrs.RestClient;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.model.WeatherListMessenger;
import uk.commonline.weather.model.WeatherMessenger;
import uk.commonline.weather.model.WeatherReport;
import uk.commonline.weather.model.WeatherReportMessenger;
import uk.commonline.weather.station.service.WeatherStationService;

/**
 * @author Jim O'Mulloy
 * 
 *         JAXRS Client for WTW Station Service.
 *
 */
public class WeatherStationClient extends AbstractCrudClient<Weather> implements WeatherStationService {

    private static final String SERVICE_PATH = "wtwstation/webresources/station";

    @Override
    public List<Weather> getCurrentWeather(double latitude, double longitude) throws Exception {
        GenericType<List<Weather>> list = new GenericType<List<Weather>>() {
        };
        List<Weather> report = getRestClient().getClient().register(WeatherListMessenger.class).target(getRestClient().createUrl(getPath()))
                .path("current/lat/{lat}/long/{long}").resolveTemplate("lat", latitude).resolveTemplate("long", longitude)
                .request(MediaType.APPLICATION_JSON).get(list);
        return report;
    }

    @Override
    public List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception {
        GenericType<List<WeatherForecast>> list = new GenericType<List<WeatherForecast>>() {
        };
        List<WeatherForecast> report = getRestClient().getClient().register(WeatherListMessenger.class).target(getRestClient().createUrl(getPath()))
                .path("forecast/lat/{lat}/long/{long}/hours/{hours}/count/{count}").resolveTemplate("lat", latitude)
                .resolveTemplate("long", longitude).resolveTemplate("hours", hours).resolveTemplate("count", count)
                .request(MediaType.APPLICATION_JSON).get(list);
        return report;
    }

    @Override
    protected String getPath() {
        return SERVICE_PATH;
    }

    @Override
    public WeatherReport getWeatherReport(double latitude, double longitude) throws Exception {
        WeatherReport report = getRestClient().getClient().register(WeatherReportMessenger.class).target(getRestClient().createUrl(getPath()))
                .path("report/lat/{lat}/long/{long}").resolveTemplate("lat", latitude).resolveTemplate("long", longitude).request()
                .post(Entity.entity(null, MediaType.APPLICATION_JSON), WeatherReport.class);
        return report;
    }

    @Override
    public void setRestClient(RestClient restClient) {
        super.setRestClient(restClient);
        restClient.registerProvider(WeatherListMessenger.class);
        restClient.registerProvider(WeatherReportMessenger.class);
        restClient.registerProvider(WeatherMessenger.class);
        restClient.resetClient();
    }
}
