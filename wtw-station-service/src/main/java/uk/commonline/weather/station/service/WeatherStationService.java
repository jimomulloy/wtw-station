package uk.commonline.weather.station.service;

import java.util.List;

import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.model.WeatherReport;

/**
 * @author Jim O'Mulloy
 *
 *         WTW Weather Station service API
 * 
 */
public interface WeatherStationService {

    /**
     * Get current Weather data at given latitude, longitude from registered
     * weather stations
     * 
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    List<Weather> getCurrentWeather(double latitude, double longitude) throws Exception;

    /**
     * Get current Forecast Weather data at given latitude, longitude from
     * registered weather stations
     * 
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception;

    /**
     * Get Weather report for current Weather data at given latitude, longitude
     * from registered weather stations
     * 
     * @param latitude
     * @param longitude
     * @return
     * @throws Exception
     */
    WeatherReport getWeatherReport(double latitude, double longitude) throws Exception;
}
