package uk.commonline.weather.station.service;

import java.util.List;

import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.model.WeatherReport;

public interface WeatherStationService {

    List<Weather> getCurrentWeather(double latitude, double longitude) throws Exception;

    List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception;

    WeatherReport getWeatherReport(double latitude, double longitude) throws Exception;
}
