package uk.commonline.weather.station.source;

import java.util.List;

import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;

public interface WeatherStationSource {

    Weather getCurrentWeather(double latitude, double longitude) throws Exception;

    List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception;

    String getSourceName();

    List<Weather> report(double latitude, double longitude) throws Exception;

}
