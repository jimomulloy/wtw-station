package uk.commonline.weather.station.source.met;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import uk.commonline.weather.station.source.ConditionInfo;

public class MetWeatherParser {

    private static Logger log = Logger.getLogger(MetWeatherParser.class);

    private static final Map<Integer, ConditionInfo> conditionInfoMap = prepareMap();

    private static Date getDate(SimpleDateFormat formatter, String value) {
        try {
            Date date = formatter.parse(value);
            return date;
        } catch (Exception ex) {
            return new Date();
        }
    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        try {
            return (float) jObj.getDouble(tagName);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        try {
            return jObj.getInt(tagName);
        } catch (Exception ex) {
            return 0;
        }
    }

    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        try {
            return jObj.getString(tagName);
        } catch (Exception ex) {
            return "";
        }
    }

    private static Map<Integer, ConditionInfo> prepareMap() {
        Map<Integer, ConditionInfo> hashMap = new HashMap<>();
        hashMap.put(0, new ConditionInfo(0, "http://www.metoffice.gov.uk/media/image/t/c/w0.png", "Clear night"));
        hashMap.put(1, new ConditionInfo(1, "http://www.metoffice.gov.uk/media/image/t/s/w1.png", "Sunny day"));
        hashMap.put(2, new ConditionInfo(2, "http://www.metoffice.gov.uk/media/image/0/n/w2.png", "Partly cloudy (night)"));
        hashMap.put(3, new ConditionInfo(3, "http://www.metoffice.gov.uk/media/image/s/s/w3.png", "Partly cloudy (day)"));
        hashMap.put(4, new ConditionInfo(4, "http://www.metoffice.gov.uk/media/image/s/9/w4.png", "Dust"));
        hashMap.put(5, new ConditionInfo(5, "http://www.metoffice.gov.uk/media/image/0/3/w5.png", "Mist"));
        hashMap.put(6, new ConditionInfo(6, "http://www.metoffice.gov.uk/media/image/0/2/w6.png", "Fog"));
        hashMap.put(7, new ConditionInfo(7, "http://www.metoffice.gov.uk/media/image/2/2/w7.png", "Cloudy"));
        hashMap.put(8, new ConditionInfo(8, "http://www.metoffice.gov.uk/media/image/2/i/w8.png", "Overcast"));
        hashMap.put(9, new ConditionInfo(9, "http://www.metoffice.gov.uk/media/image/3/9/w9.png", "Light rain shower (night)"));
        hashMap.put(10, new ConditionInfo(10, "http://www.metoffice.gov.uk/media/image/a/d/w10_1.png", "Light rain shower (day)"));
        hashMap.put(11, new ConditionInfo(11, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "Drizzle"));
        hashMap.put(12, new ConditionInfo(12, "http://www.metoffice.gov.uk/media/image/b/r/w12.png", "Light rain"));
        hashMap.put(13, new ConditionInfo(13, "http://www.metoffice.gov.uk/media/image/b/8/w13.png", "Heavy rain shower (night)"));
        hashMap.put(14, new ConditionInfo(14, "http://www.metoffice.gov.uk/media/image/c/b/w14.png", "Heavy rain shower (day)"));
        hashMap.put(15, new ConditionInfo(15, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "Heavy rain"));
        hashMap.put(16, new ConditionInfo(16, "http://www.metoffice.gov.uk/media/image/3/k/w16.png", "Sleet shower (night)"));
        hashMap.put(17, new ConditionInfo(17, "http://www.metoffice.gov.uk/media/image/3/5/w17.png", "Sleet shower (day)"));
        hashMap.put(18, new ConditionInfo(18, "http://www.metoffice.gov.uk/media/image/3/6/w18.png", "Sleet"));
        hashMap.put(19, new ConditionInfo(19, "http://www.metoffice.gov.uk/media/image/4/6/w19.png", "Hail shower (night)"));
        hashMap.put(20, new ConditionInfo(20, "http://www.metoffice.gov.uk/media/image/4/o/w20.png", "Hail shower (day)"));
        hashMap.put(21, new ConditionInfo(21, "http://www.metoffice.gov.uk/media/image/4/f/w21.png", "Hail"));
        hashMap.put(22, new ConditionInfo(22, "http://www.metoffice.gov.uk/media/image/5/0/w22.png", "Light snow shower (night)"));
        hashMap.put(23, new ConditionInfo(23, "http://www.metoffice.gov.uk/media/image/5/q/w23.png", "Light snow shower (day)"));
        hashMap.put(24, new ConditionInfo(24, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "Light snow"));
        hashMap.put(25, new ConditionInfo(25, "http://www.metoffice.gov.uk/media/image/5/d/w25.png", "Heavy snow shower (night)"));
        hashMap.put(26, new ConditionInfo(26, "http://www.metoffice.gov.uk/media/image/6/l/w26.png", "Heavy snow shower (day)"));
        hashMap.put(27, new ConditionInfo(27, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "Heavy snow"));
        hashMap.put(28, new ConditionInfo(28, "http://www.metoffice.gov.uk/media/image/6/t/w28.png", "Thunder shower (night)"));
        hashMap.put(29, new ConditionInfo(29, "http://www.metoffice.gov.uk/media/image/6/5/w29.png", "Thunder shower (day)"));
        hashMap.put(30, new ConditionInfo(30, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "Thunder"));
        hashMap.put(31, new ConditionInfo(31, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "Tropical Storm"));
        hashMap.put(32, new ConditionInfo(33, "http://www.metoffice.gov.uk/media/image/7/q/w33.png", "Haze"));
        hashMap.put(99, new ConditionInfo(99, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "No Data"));
        return hashMap;
    }

    public ConditionInfo getConditonInfo(int code) {
        if (conditionInfoMap.containsKey(code)) {
            return conditionInfoMap.get(code);
        }
        return new ConditionInfo(code, "", "Not Used");
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

    private WeatherForecast parseForecast(Source source, JSONObject drep, JSONObject nrep, Date sourceDate, Date forecastDate) throws JSONException {

        WeatherForecast weather = new WeatherForecast();

        weather.setSourceTime(sourceDate);

        Condition condition = new Condition();
        condition.setMinTemp(toDouble(getString("Nm", nrep)));
        condition.setMaxTemp(toDouble(getString("Dm", drep)));
        condition.setFromTime(forecastDate);
        condition.setToTime(new Date(forecastDate.getTime() + (1000 * 60 * 60 * 24)));
        weather.setPeriodFrom(forecastDate);
        weather.setPeriodTo(new Date(forecastDate.getTime() + (1000 * 60 * 60 * 24)));

        String code = getString("W", drep);

        if(code == null || code.isEmpty()){
            code = "99";
        }
        condition.setCode(code);
        try {
            ConditionInfo ci = getConditonInfo(Integer.parseInt(code));
            condition.setIcon(ci.url);
            condition.setText(ci.text);
            condition.setDescription(ci.text);
        } catch (Exception ex) {
            //
        }
        condition.setWeather(weather);
        weather.setCondition(condition);

        Atmosphere atmosphere = new Atmosphere();
        atmosphere.setHumidity(toDouble(getString("Hn", drep)));
        atmosphere.setVisibility(toDouble(getString("V", drep)));
        atmosphere.setWeather(weather);
        weather.setAtmosphere(atmosphere);

        Wind wind = new Wind();
        wind.setChill(toDouble("0"));
        wind.setDirection(getString("D", drep));
        wind.setSpeed(toDouble(getString("S", drep)));
        wind.setWeather(weather);
        weather.setWind(wind);

        Precipitation precipitation = new Precipitation();
        double pn = toDouble(getString("PPn", drep));
        double pd = toDouble(getString("PPd", drep));
        precipitation.setRate(new Double((pd + pn) / 2.0));
        precipitation.setWeather(weather);
        weather.setPrecipitation(precipitation);

        weather.setWriteTime(new Date());

        weather.setSource(source.getName());
        // weather.temperature.setTemp(getFloat("temp", mainObj));

        return weather;
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
            forecast.add(parseForecast(source, reps.getJSONObject(0), reps.getJSONObject(1), sourceDate, forecastDate));
        }

        return forecast;
    }

    private Weather parseWeather(Source source, JSONObject rep, Date sourceDate) throws JSONException {

        Weather weather = new Weather();

        weather.setSourceTime(sourceDate);

        Condition condition = new Condition();
        condition.setMinTemp(toDouble(getString("T", rep)));
        condition.setMaxTemp(toDouble(getString("T", rep)));
        condition.setFromTime(sourceDate);
        condition.setToTime(sourceDate);
        condition.setWeather(weather);
        String code = getString("W", rep);

        if(code == null || code.isEmpty()){
            code = "99";
        }
        condition.setCode(code);
        try {
            ConditionInfo ci = getConditonInfo(Integer.parseInt(code));
            condition.setIcon(ci.url);
            condition.setText(ci.text);
            condition.setDescription(ci.text);
        } catch (Exception ex) {
            //
        }
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

        return weather;
    }

    public List<Weather> parseWeatherReport(Source source, String currentData, String forecastData) throws Exception {

        List<Weather> report = new ArrayList<Weather>();
        Weather weather = parseCurrentWeather(source, currentData);
        report.add(weather);
        report.addAll(parseForecastWeather(source, forecastData));
        return report;
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