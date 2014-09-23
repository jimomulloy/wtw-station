package uk.commonline.weather.station.source.google;

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

public class GoogleWeatherParser {

    private static Logger log = Logger.getLogger(GoogleWeatherParser.class);

    private static final Map<Integer, ConditionInfo> conditionInfoMap = prepareMap();

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

    private static Map<Integer, ConditionInfo> prepareMap() {
        Map<Integer, ConditionInfo> hashMap = new HashMap<>();
        hashMap.put(200, new ConditionInfo(200, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorm with light rain"));
        hashMap.put(201, new ConditionInfo(201, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorm with rain"));
        hashMap.put(202, new ConditionInfo(202, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorm with heavy rain"));
        hashMap.put(210, new ConditionInfo(210, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "light thunderstorm"));
        hashMap.put(211, new ConditionInfo(211, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorm"));
        hashMap.put(212, new ConditionInfo(212, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "heavy thunderstorm "));
        hashMap.put(221, new ConditionInfo(221, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "ragged thunderstorm "));
        hashMap.put(230, new ConditionInfo(230, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorm with light drizzle "));
        hashMap.put(231, new ConditionInfo(231, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorm with drizzle "));
        hashMap.put(232, new ConditionInfo(232, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorm with heavy drizzle"));

        hashMap.put(300, new ConditionInfo(300, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "light intensity drizzle"));
        hashMap.put(301, new ConditionInfo(301, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "drizzle"));
        hashMap.put(302, new ConditionInfo(302, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "heavy intensity drizzle"));
        hashMap.put(310, new ConditionInfo(310, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "light intensity drizzle rain"));
        hashMap.put(311, new ConditionInfo(311, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "drizzle rain"));
        hashMap.put(312, new ConditionInfo(312, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "heavy intensity drizzle rain"));
        hashMap.put(313, new ConditionInfo(313, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "shower rain and drizzle"));
        hashMap.put(314, new ConditionInfo(314, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "heavy shower rain and drizzle"));
        hashMap.put(321, new ConditionInfo(321, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "shower drizzle"));

        hashMap.put(500, new ConditionInfo(500, "http://www.metoffice.gov.uk/media/image/b/r/w12.png", "light rain"));
        hashMap.put(501, new ConditionInfo(501, "http://www.metoffice.gov.uk/media/image/b/r/w12.png", "moderate rain"));
        hashMap.put(502, new ConditionInfo(502, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "heavy intensity rain"));
        hashMap.put(503, new ConditionInfo(503, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "very heavy rain"));
        hashMap.put(504, new ConditionInfo(504, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "extreme rain"));
        hashMap.put(511, new ConditionInfo(511, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "freezing rain "));
        hashMap.put(520, new ConditionInfo(520, "http://www.metoffice.gov.uk/media/image/b/r/w12.png", "light intensity shower rain"));
        hashMap.put(521, new ConditionInfo(521, "http://www.metoffice.gov.uk/media/image/b/r/w12.png", "shower rain"));
        hashMap.put(522, new ConditionInfo(522, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "heavy intensity shower rain"));
        hashMap.put(531, new ConditionInfo(531, "http://www.metoffice.gov.uk/media/image/b/r/w12.png", "ragged shower rain"));

        hashMap.put(600, new ConditionInfo(600, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "light snow"));
        hashMap.put(601, new ConditionInfo(601, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "snow"));
        hashMap.put(602, new ConditionInfo(602, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "heavy snow"));
        hashMap.put(611, new ConditionInfo(611, "http://www.metoffice.gov.uk/media/image/3/6/w18.png", "sleet"));
        hashMap.put(612, new ConditionInfo(612, "http://www.metoffice.gov.uk/media/image/3/6/w18.png", "shower sleet"));
        hashMap.put(615, new ConditionInfo(615, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "light rain and snow"));
        hashMap.put(616, new ConditionInfo(616, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "rain and snow"));
        hashMap.put(620, new ConditionInfo(620, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "light shower snow"));
        hashMap.put(621, new ConditionInfo(621, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "shower snow"));
        hashMap.put(622, new ConditionInfo(622, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "heavy shower snow"));

        hashMap.put(701, new ConditionInfo(701, "http://www.metoffice.gov.uk/media/image/0/3/w5.png", "mist"));
        hashMap.put(711, new ConditionInfo(711, "http://www.metoffice.gov.uk/media/image/7/q/w33.png", "smoke"));
        hashMap.put(721, new ConditionInfo(721, "http://www.metoffice.gov.uk/media/image/7/q/w33.png", "haze"));
        hashMap.put(731, new ConditionInfo(731, "http://www.metoffice.gov.uk/media/image/s/9/w4.png", "sand, dust whirls"));
        hashMap.put(741, new ConditionInfo(741, "http://www.metoffice.gov.uk/media/image/0/2/w6.png", "fog"));
        hashMap.put(751, new ConditionInfo(751, "http://www.metoffice.gov.uk/media/image/s/9/w4.png", "sand"));
        hashMap.put(761, new ConditionInfo(761, "http://www.metoffice.gov.uk/media/image/s/9/w4.png", "dust"));
        hashMap.put(762, new ConditionInfo(762, "http://www.metoffice.gov.uk/media/image/s/9/w4.png", "volcanic ash"));
        hashMap.put(771, new ConditionInfo(771, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "squalls"));
        hashMap.put(781, new ConditionInfo(781, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "tornado"));

        hashMap.put(800, new ConditionInfo(800, "http://www.metoffice.gov.uk/media/image/t/s/w1.png", "clear sky"));
        hashMap.put(801, new ConditionInfo(801, "http://www.metoffice.gov.uk/media/image/s/s/w3.png", "few clouds"));
        hashMap.put(802, new ConditionInfo(802, "http://www.metoffice.gov.uk/media/image/s/s/w3.png", "scattered clouds"));
        hashMap.put(803, new ConditionInfo(803, "http://www.metoffice.gov.uk/media/image/2/i/w8.png", "broken clouds"));
        hashMap.put(804, new ConditionInfo(804, "http://www.metoffice.gov.uk/media/image/2/2/w7.png", "overcast clouds"));

        hashMap.put(900, new ConditionInfo(900, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "tornado"));
        hashMap.put(901, new ConditionInfo(901, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "tropical storm"));
        hashMap.put(902, new ConditionInfo(902, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "hurricane"));
        hashMap.put(903, new ConditionInfo(903, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "cold"));
        hashMap.put(904, new ConditionInfo(904, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "hot"));
        hashMap.put(905, new ConditionInfo(905, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "windy"));
        hashMap.put(906, new ConditionInfo(906, "http://www.metoffice.gov.uk/media/image/4/f/w21.png", "hail"));

        hashMap.put(950, new ConditionInfo(950, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "setting"));
        hashMap.put(951, new ConditionInfo(951, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "calm"));
        hashMap.put(952, new ConditionInfo(952, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "light breeze"));
        hashMap.put(953, new ConditionInfo(953, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "gentle breeze"));
        hashMap.put(954, new ConditionInfo(954, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "moderate breeze"));
        hashMap.put(955, new ConditionInfo(955, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "fresh breeze"));
        hashMap.put(956, new ConditionInfo(956, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "strong breeze"));
        hashMap.put(957, new ConditionInfo(957, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "high wind, near gale"));
        hashMap.put(958, new ConditionInfo(958, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "gale"));
        hashMap.put(959, new ConditionInfo(959, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "severe gale"));
        hashMap.put(960, new ConditionInfo(960, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "storm"));
        hashMap.put(961, new ConditionInfo(961, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "violent storm"));
        hashMap.put(962, new ConditionInfo(962, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "hurricane"));

        return hashMap;
    }

    String[] compass = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N" };

    public ConditionInfo getConditonInfo(int code) {
        if (conditionInfoMap.containsKey(code)) {
            return conditionInfoMap.get(code);
        }
        return new ConditionInfo(code, "", "Not Used");
    }

    private float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    public List<WeatherForecast> parseForecastWeather(Source source, String data) throws JSONException {
        log.debug("!!Google parseForecastWeather:\n"+data);
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

            long dt = jDayForecast.getLong("dt");

            Condition condition = new Condition();
            condition.setText(getString("main", jWeatherObj));
            condition.setDescription(getString("description", jWeatherObj));
            condition.setIcon(getString("icon", jWeatherObj));
            condition.setMinTemp(toDouble(Double.toString(jTempObj.getDouble("min"))) - 273.00);
            condition.setMaxTemp(toDouble(Double.toString(jTempObj.getDouble("max"))) - 273.00);
            String code = getString("id", jWeatherObj);
            condition.setCode(code);
            try {
                ConditionInfo ci = getConditonInfo(Integer.parseInt(code));
                condition.setIcon(ci.url);
                // condition.setText(ci.url);
            } catch (Exception ex) {
                //
            }
            try {
                Date condDate = new Date(dt * 1000L);
                condition.setFromTime(new Date(condDate.getTime() - 12 * 60 * 60 * 1000));
                condition.setToTime(new Date(condDate.getTime() + 12 * 59 * 60 * 1000));
                weather.setPeriodFrom(new Date(condDate.getTime() - 12 * 60 * 60 * 1000));
                weather.setPeriodTo(new Date(condDate.getTime() + 12 * 59 * 60 * 1000));
            } catch (Exception e) {
                e.printStackTrace();
                condition.setFromTime(new Date());
                condition.setToTime(new Date());
                weather.setPeriodFrom(new Date());
                weather.setPeriodTo(new Date(new Date().getTime() + 24 * 59 * 60 * 1000));
            }
            condition.setWeather(weather);
            weather.setCondition(condition);

            Atmosphere atmosphere = new Atmosphere();
            atmosphere.setHumidity(jDayForecast.getDouble("humidity"));
            atmosphere.setPressure(jDayForecast.getDouble("pressure"));
            atmosphere.setRising("");
            atmosphere.setWeather(weather);
            weather.setAtmosphere(atmosphere);

            Wind wind = new Wind();
            wind.setChill(toDouble("0"));
            wind.setDirection(compass[(int) Math.round(jDayForecast.getDouble("deg") / 22.5)]);
            wind.setSpeed(jDayForecast.getDouble("speed"));
            wind.setWeather(weather);
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

    public Weather parseWeatherReport(Source source, String data) throws JSONException {
        log.debug("!!Google parseWeather:\n"+data);
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
        String code = getString("id", jsonWeather);
        condition.setCode(code);
        try {
            ConditionInfo ci = getConditonInfo(Integer.parseInt(code));
            condition.setIcon(ci.url);
            // condition.setText(ci.url);
        } catch (Exception ex) {
            //
        }
        long dt = jObj.getLong("dt");

        try {
            condition.setFromTime(new Date(dt * 1000L));
            condition.setToTime(new Date(dt * 1000L));
        } catch (Exception e) {
            e.printStackTrace();
            condition.setFromTime(new Date());
            condition.setToTime(new Date());
        }
        condition.setWeather(weather);
        weather.setCondition(condition);

        Atmosphere atmosphere = new Atmosphere();
        atmosphere.setHumidity(toDouble(getString("humidity", mainObj)));
        // atmosphere.setVisibility(toDouble(getString("all", cObj))*1000.0);
        atmosphere.setPressure(toDouble(getString("pressure", mainObj)));
        atmosphere.setRising("");
        atmosphere.setWeather(weather);
        weather.setAtmosphere(atmosphere);

        Wind wind = new Wind();
        wind.setChill(toDouble("0"));
        wind.setDirection(compass[(int) Math.round(toDouble(getString("deg", wObj)) / 22.5)]);
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

    public List<Weather> parseWeatherReport(Source source, String currentData, String forecastData) throws Exception {

        List<Weather> report = new ArrayList<Weather>();
        Weather weather = parseWeatherReport(source, currentData);
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
