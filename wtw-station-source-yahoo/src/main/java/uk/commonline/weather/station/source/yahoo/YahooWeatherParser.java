package uk.commonline.weather.station.source.yahoo;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import uk.commonline.weather.model.Atmosphere;
import uk.commonline.weather.model.Condition;
import uk.commonline.weather.model.Location;
import uk.commonline.weather.model.Precipitation;
import uk.commonline.weather.model.Source;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.model.Wind;
import uk.commonline.weather.station.source.ConditionInfo;

public class YahooWeatherParser implements ErrorHandler {

    private static Logger log = Logger.getLogger(YahooWeatherParser.class);

    private static final Map<Integer, ConditionInfo> conditionInfoMap = prepareMap();

    static void listNodes(Node node, String indent) {
        String nodeName = node.getNodeName();
        System.out.println(indent + nodeName + " Node, type is " + node.getClass().getName() + ":");
        System.out.println(indent + " " + node);

        NodeList list = node.getChildNodes();
        if (list.getLength() > 0) {
            System.out.println(indent + "Child Nodes of " + nodeName + " are:");
            for (int i = 0; i < list.getLength(); i++)
                listNodes(list.item(i), indent + " ");
        }
    }

    private static Map<Integer, ConditionInfo> prepareMap() {
        Map<Integer, ConditionInfo> hashMap = new HashMap<>();
        hashMap.put(0, new ConditionInfo(0, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "tornado"));
        hashMap.put(1, new ConditionInfo(1, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "tropical storm"));
        hashMap.put(2, new ConditionInfo(2, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "hurricane"));
        hashMap.put(3, new ConditionInfo(3, "http://www.metoffice.gov.uk/media/image/7/3/w31.png", "severe thunderstorms"));
        hashMap.put(4, new ConditionInfo(4, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thunderstorms"));
        hashMap.put(5, new ConditionInfo(5, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "mixed rain and snow"));
        hashMap.put(6, new ConditionInfo(6, "http://www.metoffice.gov.uk/media/image/3/6/w18.png", "mixed rain and sleet"));
        hashMap.put(7, new ConditionInfo(7, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "mixed snow and sleet"));
        hashMap.put(8, new ConditionInfo(8, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "freezing drizzle"));
        hashMap.put(9, new ConditionInfo(9, "http://www.metoffice.gov.uk/media/image/a/k/w11.png", "drizzle"));
        hashMap.put(10, new ConditionInfo(10, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "freezing rain"));
        hashMap.put(11, new ConditionInfo(11, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "showers"));
        hashMap.put(12, new ConditionInfo(12, "http://www.metoffice.gov.uk/media/image/3/n/w15.png", "showers"));
        hashMap.put(13, new ConditionInfo(13, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "snow flurries"));
        hashMap.put(14, new ConditionInfo(14, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "light snow showers"));
        hashMap.put(15, new ConditionInfo(15, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "blowing snow"));
        hashMap.put(16, new ConditionInfo(16, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "snow"));
        hashMap.put(17, new ConditionInfo(17, "http://www.metoffice.gov.uk/media/image/4/f/w21.png", "hail"));
        hashMap.put(18, new ConditionInfo(18, "http://www.metoffice.gov.uk/media/image/3/6/w18.png", "sleet"));
        hashMap.put(19, new ConditionInfo(19, "http://www.metoffice.gov.uk/media/image/s/9/w4.png", "dust"));
        hashMap.put(20, new ConditionInfo(20, "http://www.metoffice.gov.uk/media/image/0/2/w6.png", "foggy"));
        hashMap.put(21, new ConditionInfo(21, "http://www.metoffice.gov.uk/media/image/7/q/w33.png", "haze"));
        hashMap.put(22, new ConditionInfo(22, "http://www.metoffice.gov.uk/media/image/s/9/w4.png", "smoky"));
        hashMap.put(23, new ConditionInfo(23, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "blustery"));
        hashMap.put(24, new ConditionInfo(24, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "windy"));
        hashMap.put(25, new ConditionInfo(25, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "cold"));
        hashMap.put(26, new ConditionInfo(26, "http://www.metoffice.gov.uk/media/image/2/2/w7.png", "cloudy"));
        hashMap.put(27, new ConditionInfo(27, "http://www.metoffice.gov.uk/media/image/0/n/w2.png", "mostly cloudy (night)"));
        hashMap.put(28, new ConditionInfo(28, "http://www.metoffice.gov.uk/media/image/s/s/w3.png", "mostly cloudy (day)"));
        hashMap.put(29, new ConditionInfo(29, "http://www.metoffice.gov.uk/media/image/0/n/w2.png", "partly cloudy (night)"));
        hashMap.put(30, new ConditionInfo(30, "http://www.metoffice.gov.uk/media/image/s/s/w3.png", "partly cloudy (day)"));
        hashMap.put(31, new ConditionInfo(31, "http://www.metoffice.gov.uk/media/image/t/c/w0.png", "clear (night)"));
        hashMap.put(32, new ConditionInfo(32, "http://www.metoffice.gov.uk/media/image/t/s/w1.png", "sunny"));
        hashMap.put(33, new ConditionInfo(33, "http://www.metoffice.gov.uk/media/image/t/c/w0.png", "fair (night)"));
        hashMap.put(34, new ConditionInfo(34, "http://www.metoffice.gov.uk/media/image/t/s/w1.png", "fair (day)"));
        hashMap.put(35, new ConditionInfo(35, "http://www.metoffice.gov.uk/media/image/4/f/w21.png", "mixed rain and hail"));
        hashMap.put(36, new ConditionInfo(36, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "hot"));
        hashMap.put(37, new ConditionInfo(37, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "isolated thunderstorms"));
        hashMap.put(38, new ConditionInfo(38, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "scattered thunderstorms"));
        hashMap.put(39, new ConditionInfo(39, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "scattered thunderstorms"));
        hashMap.put(40, new ConditionInfo(40, "http://www.metoffice.gov.uk/media/image/b/r/w12.png", "scattered showers"));
        hashMap.put(41, new ConditionInfo(41, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "heavy snow"));
        hashMap.put(42, new ConditionInfo(42, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "scattered snow showers"));
        hashMap.put(43, new ConditionInfo(43, "http://www.metoffice.gov.uk/media/image/6/l/w27.png", "heavy snow"));
        hashMap.put(44, new ConditionInfo(44, "http://www.metoffice.gov.uk/media/image/s/s/w3.png", "partly cloudy"));
        hashMap.put(45, new ConditionInfo(45, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "thundershowers"));
        hashMap.put(46, new ConditionInfo(46, "http://www.metoffice.gov.uk/media/image/5/2/w24.png", "snow showers"));
        hashMap.put(47, new ConditionInfo(47, "http://www.metoffice.gov.uk/media/image/7/e/w30.png", "isolated thundershowers"));
        hashMap.put(3200, new ConditionInfo(3200, "http://www.metoffice.gov.uk/media/image/f/p/w99.png", "not available"));
        return hashMap;
    }

    String[] compass = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW", "N" };

    @Override
    public void error(SAXParseException spe) {
        System.out.println("Error at line " + spe.getLineNumber());
        System.out.println(spe.getMessage());
    }

    @Override
    public void fatalError(SAXParseException spe) throws SAXException {
        System.out.println("Fatal error at line " + spe.getLineNumber());
        System.out.println(spe.getMessage());
        throw spe;
    }

    public ConditionInfo getConditonInfo(int code) {
        if (conditionInfoMap.containsKey(code)) {
            return conditionInfoMap.get(code);
        }
        return new ConditionInfo(code, "", "Not Used");
    }

    public Weather parseCurrentWeather(Source source, Document response, XPath xPath) throws Exception {

        Weather weather = new Weather();

        Location location = new Location();
        location.setName(xPath.compile("/rss/channel/location/@city").evaluate(response));
        location.setType("city");

        location.setRegion(xPath.compile("/rss/channel/location/@region").evaluate(response));
        location.setCountry(xPath.compile("/rss/channel/location/@country").evaluate(response));
        // location.setZip(zip);
        // weather.setRegion(region);

        Condition condition = new Condition();
        condition.setText(xPath.compile("/rss/channel/item/condition/@text").evaluate(response));
        condition.setMinTemp((toDouble(xPath.compile("/rss/channel/item/condition/@temp").evaluate(response)) - 32.0) * 5.0 / 9.0);
        condition.setMaxTemp((toDouble(xPath.compile("/rss/channel/item/condition/@temp").evaluate(response)) - 32.0) * 5.0 / 9.0);

        String code = xPath.compile("/rss/channel/item/condition/@code").evaluate(response);
        condition.setCode(code);
        try {
            ConditionInfo ci = getConditonInfo(Integer.parseInt(code));
            condition.setIcon(ci.url);
            // condition.setText(ci.url);
            String desc = xPath.compile("/rss/channel/item/description/text()").evaluate(response);
            int i1 = desc.indexOf("<img src=\"");
            String d2 = desc.substring(i1 + 10);
            i1 = d2.indexOf(".gif");
            String d3 = d2.substring(0, i1 + 4);
            if (d3.startsWith("http")) {
                // condition.setIcon(d3);
            }
        } catch (Exception ex) {
            //
        }

        String valuee = "";
        try {
            valuee = xPath.compile("/rss/channel/item/condition/@date").evaluate(response);
            Date condDate = new SimpleDateFormat("E, dd MMM yyyy HH:mm a z").parse(valuee);
            System.out.println("Date is ::" + condDate);
            condition.setFromTime(condDate);
            condition.setToTime(condDate);
        } catch (Exception e) {
            System.out.println("Date :" + valuee + ", Error::" + e);
            e.printStackTrace();
            condition.setFromTime(new Date());
            condition.setToTime(new Date());
        }
        condition.setWeather(weather);
        weather.setCondition(condition);

        Atmosphere atmosphere = new Atmosphere();
        atmosphere.setHumidity(toDouble(xPath.compile("/rss/channel/atmosphere/@humidity").evaluate(response)));
        atmosphere.setVisibility(toDouble(xPath.compile("/rss/channel/atmosphere/@visibility").evaluate(response)));
        atmosphere.setPressure(toDouble(xPath.compile("/rss/channel/atmosphere/@pressure").evaluate(response)) * 6894.75729 / 100);
        String r = xPath.compile("/rss/channel/atmosphere/@rising").evaluate(response);
        if (r.equals("1")) {
            r = "R";
        } else if (r.equals("2")) {
            r = "F";
        } else {
            r = "";
        }
        atmosphere.setRising(r);
        atmosphere.setWeather(weather);
        weather.setAtmosphere(atmosphere);

        Wind wind = new Wind();
        wind.setChill(toDouble(xPath.compile("/rss/channel/wind/@chill").evaluate(response)));
        String d = xPath.compile("/rss/channel/wind/@direction").evaluate(response);
        wind.setDirection(compass[(int) Math.round(toDouble(d) / 22.5)]);
        wind.setSpeed(toDouble(xPath.compile("/rss/channel/wind/@speed").evaluate(response)));
        wind.setWeather(weather);
        weather.setWind(wind);

        Precipitation precipitation = new Precipitation();
        precipitation.setWeather(weather);
        weather.setPrecipitation(precipitation);

        weather.setWriteTime(new Date());
        weather.setSource(source.getName());
        return weather;
    }

    public List<WeatherForecast> parseForecastWeather(Source source, Document response, XPath xPath) throws Exception {

        List<WeatherForecast> forecasts = new ArrayList<WeatherForecast>();

        WeatherForecast weather = new WeatherForecast();

        // Location location = new Location();
        // location.setName(xPath.compile("/rss/channel/location/@city").evaluate(response));
        // location.setType("city");

        // location.setRegion(xPath.compile("/rss/channel/location/@region").evaluate(response));
        // location.setCountry(xPath.compile("/rss/channel/location/@country").evaluate(response));
        // location.setZip(zip);

        // Get all search Result nodes
        NodeList nodes = (NodeList) xPath.evaluate("/rss/channel/item/forecast", response, XPathConstants.NODESET);
        int nodeCount = nodes.getLength();
        // iterate over search Result nodes
        for (int i = 0; i < nodeCount; i++) {
            Element forecast = (Element) nodes.item(i);
            // Get each xpath expression as a string
            String date = xPath.evaluate("@date", forecast);
            String minTemp = xPath.evaluate("@low", forecast);
            String maxTemp = xPath.evaluate("@high", forecast);
            String code = xPath.evaluate("@code", forecast);
            String text = xPath.evaluate("@text", forecast);
            System.out.println("Yahoo parseForecastWeather node :" + i + ", text:" + text);
            weather = new WeatherForecast();
            // weather.setRegion(region);
            Condition condition = new Condition();
            condition.setText(text);
            condition.setMinTemp((toDouble(minTemp) - 32.0) * 5.0 / 9.0);
            condition.setMaxTemp((toDouble(maxTemp) - 32.0) * 5.0 / 9.0);
            condition.setCode(code);
            try {
                ConditionInfo ci = getConditonInfo(Integer.parseInt(code));
                condition.setIcon(ci.url);
            } catch (Exception ex) {
                //
            }

            String valuee = date;
            try {
                Date condDate = new SimpleDateFormat("dd MMM yyyy").parse(valuee);
                System.out.println("Date is ::" + condDate);
                condition.setFromTime(condDate);
                condition.setToTime(condDate);
                weather.setPeriodFrom(condDate);
                Date dateNextDay = new Date(condDate.getTime() + 24 * 60 * 60 * 1000);
                weather.setPeriodTo(dateNextDay);
            } catch (Exception e) {
                System.out.println("Date :" + valuee + ", Error::" + e);
                // e.printStackTrace();
                condition.setFromTime(new Date());
                condition.setToTime(new Date());
                weather.setPeriodFrom(new Date());
                weather.setPeriodTo(new Date());
            }
            condition.setWeather(weather);
            weather.setCondition(condition);

            Atmosphere atmosphere = new Atmosphere();
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
            forecasts.add(weather);
        }
        return forecasts;

    }

    public List<WeatherForecast> parseForecastWeather(Source source, InputStream inputStream, int hours, int count) throws Exception {

        List<WeatherForecast> report = new ArrayList<WeatherForecast>();

        // Process response
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        // factory.setValidating(true);
        DocumentBuilder builder;
        Document response = null;
        XPathExpression expr = null;
        builder = factory.newDocumentBuilder();
        builder.setErrorHandler(this);
        response = builder.parse(inputStream);

        DocumentType doctype = response.getDoctype();

        // create an XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance();

        // create an XPath object
        XPath xPath = xFactory.newXPath();

        report.addAll(parseForecastWeather(source, response, xPath));

        return report;
    }

    public List<Weather> parseHistoricWeather(Source source, InputStream inputStream, long periodFrom, long periodTo) throws Exception {

        List<Weather> report = new ArrayList<Weather>();
        // TODO
        return report;
    }

    public Weather parseWeather(Source source, InputStream inputStream) throws Exception {

        Weather weather = new Weather();

        // Process response
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        // factory.setValidating(true);
        DocumentBuilder builder;
        Document response = null;
        builder = factory.newDocumentBuilder();
        builder.setErrorHandler(this);
        response = builder.parse(inputStream);

        // create an XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance();

        // create an XPath object
        XPath xPath = xFactory.newXPath();

        weather = parseCurrentWeather(source, response, xPath);

        return weather;
    }

    public List<Weather> parseWeatherReport(Source source, InputStream inputStream) throws Exception {

        List<Weather> report = new ArrayList<Weather>();
        Weather weather = new Weather();

        // Process response
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        // factory.setValidating(true);
        DocumentBuilder builder;
        Document response = null;
        XPathExpression expr = null;
        builder = factory.newDocumentBuilder();
        builder.setErrorHandler(this);
        response = builder.parse(inputStream);

        DocumentType doctype = response.getDoctype();

        // create an XPathFactory
        XPathFactory xFactory = XPathFactory.newInstance();

        // create an XPath object
        XPath xPath = xFactory.newXPath();

        weather = parseCurrentWeather(source, response, xPath);

        report.add(weather);

        report.addAll(parseForecastWeather(source, response, xPath));

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

    @Override
    public void warning(SAXParseException spe) {
        System.out.println("Warning at line " + spe.getLineNumber());
        System.out.println(spe.getMessage());
    }
}
