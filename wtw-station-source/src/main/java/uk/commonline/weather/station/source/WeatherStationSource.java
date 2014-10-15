package uk.commonline.weather.station.source;

import java.util.List;

import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;

/**
 * @author Jim O'Mulloy
 * 
 * WTW Weather station data source API
 *
 */
public interface WeatherStationSource {

    /**
     * Get Current Weather data at given latitude, longitude
     * 
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    Weather getCurrentWeather(double latitude, double longitude) throws Exception;

    /**
     * Get Current Forecast Weather data at given latitude, longitude
     * 
     * @param latitude
     * @param longitude
     * @param hours
     * @param count
     * @return
     * @throws Exception
     */
    List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception;

    /**
     * @return name of source
     */
    String getSourceName();

    /**
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    List<Weather> report(double latitude, double longitude) throws Exception;

}
