package uk.commonline.weather.station.service;

import uk.commonline.weather.model.Weather;

public interface WeatherStationService {

    Weather retrieveForecast(String zip) throws Exception;
}
