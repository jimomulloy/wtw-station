package uk.commonline.weather.station.source;

import uk.commonline.weather.station.service.WeatherStationService;

public interface WeatherStationSource {

    WeatherStationService getWeatherStationService();

    String getSourceName();
}
