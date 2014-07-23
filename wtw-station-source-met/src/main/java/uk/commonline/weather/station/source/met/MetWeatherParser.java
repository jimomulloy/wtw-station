package uk.commonline.weather.station.source.met;

import java.text.SimpleDateFormat;
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

public class MetWeatherParser {

    private static Logger log = Logger.getLogger(MetWeatherParser.class);

    public List<Weather> parseWeatherReport(Source source, String currentData, String forecastData) throws Exception {

	List<Weather> report = new ArrayList<Weather>();
	Weather weather = parseCurrentWeather(source, currentData);
	report.add(weather);
	report.addAll(parseForecastWeather(source, forecastData));
	return report;
    }

    public Weather parseCurrentWeather(Source source, String data) throws JSONException {

	// We create out JSONObject from the data
	JSONObject jObj = new JSONObject(data);
	JSONObject siterep = getObject("SiteRep", jObj);
	JSONObject dv = getObject("DV", siterep);
	String dateValue = getString("dataDate", dv);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	Date sourceDate = getDate(formatter, dateValue);
	JSONObject location = getObject("Location", dv);
	JSONArray periods = location.getJSONArray("Period");
	JSONObject period;
	JSONObject rep = null;
	for (int i = 0; i < periods.length(); i++) {
	    period = periods.getJSONObject(i);
	    JSONArray reps = period.getJSONArray("Rep");
	    for (int j = 0; j < reps.length(); j++) {
		rep = reps.getJSONObject(j);
	    }
	}

	return parseWeather(source, rep, sourceDate);

    }

    public List<WeatherForecast> parseForecastWeather(Source source, String data) throws JSONException {

	List<WeatherForecast> forecast = new ArrayList<WeatherForecast>();

	JSONObject jObj = new JSONObject(data);
	JSONObject siterep = getObject("SiteRep", jObj);
	JSONObject dv = getObject("DV", siterep);
	JSONObject location = getObject("Location", dv);
	JSONArray periods = location.getJSONArray("Period");
	String dateValue = getString("dataDate", dv);
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	Date sourceDate = getDate(formatter, dateValue);

	JSONObject period;
	JSONObject rep = null;
	for (int i = 0; i < periods.length(); i++) {
	    period = periods.getJSONObject(i);
	    dateValue = getString("value", period);
	    formatter = new SimpleDateFormat("yyyy-MM-dd'Z'");
	    Date forecastDate = getDate(formatter, dateValue);

	    JSONArray reps = period.getJSONArray("Rep");
	    // for (int j = 0; j < reps.length(); j++) {
	    forecast.add(parseForecast(source, reps.getJSONObject(0), reps.getJSONObject(1), sourceDate, forecastDate));
	    // }
	}

	return forecast;
    }

    private Weather parseWeather(Source source, JSONObject rep, Date sourceDate) throws JSONException {

	Weather weather = new Weather();

	weather.setSourceTime(sourceDate);

	Condition condition = new Condition();
	// condition.setText(getString("main", rep));
	condition.setDescription("");
	condition.setMinTemp(toDouble(getString("T", rep)));
	condition.setMaxTemp(toDouble(getString("T", rep)));
	condition.setCode(getString("W", rep));
	condition.setFromTime(sourceDate);
	condition.setToTime(sourceDate);
	condition.setWeather(weather);
	weather.setCondition(condition);

	Atmosphere atmosphere = new Atmosphere();
	atmosphere.setHumidity(toDouble(getString("H", rep)));
	atmosphere.setVisibility(toDouble(getString("V", rep)));
	atmosphere.setPressure(toDouble(getString("P", rep)));
	atmosphere.setRising(getString("Pt", rep));
	atmosphere.setWeather(weather);
	weather.setAtmosphere(atmosphere);

	Wind wind = new Wind();
	wind.setChill(toDouble("0"));
	wind.setDirection(getString("D", rep));
	wind.setSpeed(toDouble(getString("S", rep)));
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

    private WeatherForecast parseForecast(Source source, JSONObject drep, JSONObject nrep, Date sourceDate, Date forecastDate) throws JSONException {

	WeatherForecast weather = new WeatherForecast();

	weather.setPeriodFrom(sourceDate);
	weather.setPeriodTo(sourceDate);

	weather.setSourceTime(sourceDate);

	Condition condition = new Condition();
	// condition.setText(getString("main", rep));
	condition.setDescription("");
	condition.setMinTemp(toDouble(getString("Nm", nrep)));
	condition.setMaxTemp(toDouble(getString("Dm", drep)));
	condition.setCode(getString("W", drep));
	condition.setFromTime(forecastDate);
	condition.setToTime(forecastDate);

	condition.setWeather(weather);
	weather.setCondition(condition);

	Atmosphere atmosphere = new Atmosphere();
	atmosphere.setHumidity(toDouble(getString("Hn", drep)));
	atmosphere.setVisibility(toDouble(getString("V", drep)));
	//atmosphere.setPressure(getString("P", drep));
	// atmosphere.setRising(getString("Pt", rep));
	atmosphere.setWeather(weather);
	weather.setAtmosphere(atmosphere);

	Wind wind = new Wind();
	wind.setChill(toDouble("0"));
	wind.setDirection(getString("D", drep));
	wind.setSpeed(toDouble(getString("S", drep)));
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

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
	JSONObject subObj = jObj.getJSONObject(tagName);
	return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
	return jObj.getString(tagName);
    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
	return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
	return jObj.getInt(tagName);
    }

    private static Date getDate(SimpleDateFormat formatter, String value) {
	try {
	    Date date = formatter.parse(value);
	    return date;
	} catch (Exception ex) {
	    return new Date();
	}
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