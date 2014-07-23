package uk.commonline.weather.station.source.google;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.commonline.weather.model.Atmosphere;
import uk.commonline.weather.model.Condition;
import uk.commonline.weather.model.Precipitation;
import uk.commonline.weather.model.Source;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.model.Wind;

public class GoogleWeatherParser {

    private static Logger log = Logger.getLogger(GoogleWeatherParser.class);

    public List<Weather> parseWeatherReport(Source source, String currentData, String forecastData) throws Exception {

	List<Weather> report = new ArrayList<Weather>();
	Weather weather = parseWeatherReport(source, currentData);
	report.add(weather);
	report.addAll(parseForecastWeather(source, forecastData));
	return report;
    }

    public Weather parseWeatherReport(Source source, String data) throws JSONException {

	Weather weather = new Weather();

	weather.setSourceTime(new Date());

	// We create out JSONObject from the data
	JSONObject jObj = new JSONObject(data);

	// We get weather info (This is an array)
	JSONArray jArr = jObj.getJSONArray("weather");

	// We use only the first value
	JSONObject jsonWeather = jArr.getJSONObject(0);
	JSONObject mainObj = getObject("main", jObj);
	JSONObject wObj = getObject("wind", jObj);
	JSONObject cObj = getObject("clouds", jObj);

	Condition condition = new Condition();
	condition.setText(getString("main", jsonWeather));
	condition.setDescription(getString("description", jsonWeather));
	condition.setIcon(getString("icon", jsonWeather));
	condition.setMinTemp(toDouble(getString("temp_min", mainObj)) - 273.00);
	condition.setMaxTemp(toDouble(getString("temp_max", mainObj)) - 273.00);
	condition.setCode(getString("id", jsonWeather));

	long dt = jObj.getLong("dt");

	try {
	    condition.setFromTime(new Date(dt*1000L));
	    condition.setToTime(new Date(dt*1000L));
	} catch (Exception e) {
	    e.printStackTrace();
	    condition.setFromTime(new Date());
	    condition.setToTime(new Date());
	}
	condition.setWeather(weather);
	weather.setCondition(condition);

	Atmosphere atmosphere = new Atmosphere();
	atmosphere.setHumidity(toDouble(getString("humidity", mainObj)));
	atmosphere.setVisibility(toDouble(getString("all", cObj)));
	atmosphere.setPressure(toDouble(getString("pressure", mainObj)));
	atmosphere.setRising("");
	atmosphere.setWeather(weather);
	weather.setAtmosphere(atmosphere);

	Wind wind = new Wind();
	wind.setChill(toDouble("0"));
	wind.setDirection(getString("deg", wObj));
	wind.setSpeed(toDouble(getString("speed", wObj)));
	wind.setWeather(weather);
	weather.setWind(wind);

	Precipitation precipitation = new Precipitation();
	precipitation.setWeather(weather);
	weather.setPrecipitation(precipitation);

	weather.setWriteTime(new Date());
	weather.setSource(source.getName());
	// weather.temperature.setTemp(getFloat("temp", mainObj));

	return weather;
    }

    public List<WeatherForecast> parseForecastWeather(Source source, String data) throws JSONException {

	List<WeatherForecast> forecast = new ArrayList<WeatherForecast>();
	
	// We create out JSONObject from the data
	JSONObject jObj = new JSONObject(data);

	JSONArray jArr = jObj.getJSONArray("list"); // Here we have the forecast
						    // for every day

	// We traverse all the array and parse the data
	for (int i = 0; i < jArr.length(); i++) {
	    JSONObject jDayForecast = jArr.getJSONObject(i);
	    JSONArray jWeatherArr = jDayForecast.getJSONArray("weather");
	    JSONObject jWeatherObj = jWeatherArr.getJSONObject(0);
	    JSONObject jTempObj = jDayForecast.getJSONObject("temp");

	    WeatherForecast weather = new WeatherForecast();
	    weather.setSourceTime(new Date());
	    weather.setPeriodFrom(new Date());
	    weather.setPeriodTo(new Date());

	    long dt = jDayForecast.getLong("dt");

	    Condition condition = new Condition();
	    condition.setText(getString("main", jWeatherObj));
	    condition.setDescription(getString("description", jWeatherObj));
	    condition.setIcon(getString("icon", jWeatherObj));
	    condition.setMinTemp(toDouble(Double.toString(jTempObj.getDouble("min"))) - 273.00 );
	    condition.setMaxTemp(toDouble(Double.toString(jTempObj.getDouble("max"))) - 273.00 );
	    condition.setCode(getString("id", jWeatherObj));
	    String valuee = "";
	    try {
		condition.setFromTime(new Date(dt*1000L));
		condition.setToTime(new Date(dt*1000L));
	    } catch (Exception e) {
		e.printStackTrace();
		condition.setFromTime(new Date());
		condition.setToTime(new Date());
	    }
	    condition.setWeather(weather);
	    weather.setCondition(condition);

	    Atmosphere atmosphere = new Atmosphere();
	    atmosphere.setHumidity(jDayForecast.getDouble("humidity"));
	    // atmosphere.setVisibility(getInt("all", cObj) + "%");
	    atmosphere.setPressure(jDayForecast.getDouble("pressure"));
	    atmosphere.setRising("");
	    atmosphere.setWeather(weather);
	    weather.setAtmosphere(atmosphere);

	    Wind wind = new Wind();
	    wind.setWeather(weather);
	    weather.setWind(wind);

	    Precipitation precipitation = new Precipitation();
	    precipitation.setWeather(weather);
	    weather.setPrecipitation(precipitation);

	    weather.setWriteTime(new Date());
	    weather.setSource(source.getName());
	    forecast.add(weather);
	}

	return forecast;
    }

    private JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
	JSONObject subObj = jObj.getJSONObject(tagName);
	return subObj;
    }

    private String getString(String tagName, JSONObject jObj) throws JSONException {
	return jObj.getString(tagName);
    }

    private float getFloat(String tagName, JSONObject jObj) throws JSONException {
	return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
	return jObj.getInt(tagName);
    }

    private double toDouble(String input) {
	if (input == null || input.isEmpty()) {
	    return 0;
	}
	double value = 0.0;
	try {
	    value = Double.parseDouble(input);
	} catch (Exception ex) {
	    //
	}
	return value;
    }

}
