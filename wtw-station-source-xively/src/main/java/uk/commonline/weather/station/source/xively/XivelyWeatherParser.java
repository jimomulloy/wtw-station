package uk.commonline.weather.station.source.xively;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
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

public class XivelyWeatherParser implements ErrorHandler {

    private static Logger log = Logger.getLogger(XivelyWeatherParser.class);

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
	//TODO
	return report;
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
	condition.setMinTemp(toDouble(xPath.compile("/rss/channel/item/condition/@temp").evaluate(response)));
	condition.setMaxTemp(toDouble(xPath.compile("/rss/channel/item/condition/@temp").evaluate(response)));
	condition.setCode(xPath.compile("/rss/channel/item/condition/@code").evaluate(response));
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
	atmosphere.setPressure(toDouble(xPath.compile("/rss/channel/atmosphere/@pressure").evaluate(response)));
	atmosphere.setRising(xPath.compile("/rss/channel/atmosphere/@rising").evaluate(response));
	atmosphere.setWeather(weather);
	weather.setAtmosphere(atmosphere);

	Wind wind = new Wind();
	wind.setChill(toDouble(xPath.compile("/rss/channel/wind/@chill").evaluate(response)));
	wind.setDirection(xPath.compile("/rss/channel/wind/@direction").evaluate(response));
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
	NodeList nodes = (NodeList) xPath.evaluate("/rss/channel/forecast", response, XPathConstants.NODESET);
	int nodeCount = nodes.getLength();

	// iterate over search Result nodes
	for (int i = 0; i < nodeCount; i++) {
	    // Get each xpath expression as a string
	    String date = (String) xPath.evaluate("date", nodes.item(i), XPathConstants.STRING);
	    String minTemp = (String) xPath.evaluate("low", nodes.item(i), XPathConstants.STRING);
	    String maxTemp = (String) xPath.evaluate("high", nodes.item(i), XPathConstants.STRING);
	    String code = (String) xPath.evaluate("code", nodes.item(i), XPathConstants.STRING);
	    String text = (String) xPath.evaluate("text", nodes.item(i), XPathConstants.STRING);
	    weather = new WeatherForecast();
	    weather.setPeriodFrom(new Date());
	    weather.setPeriodTo(new Date());
	    // weather.setRegion(region);
	    Condition condition = new Condition();
	    condition.setText(text);
	    condition.setMinTemp(toDouble(minTemp));
	    condition.setMaxTemp(toDouble(maxTemp));
	    condition.setCode(code);
	    String valuee = "";
	    try {
		Date condDate = new SimpleDateFormat("dd MMM yyyy").parse(valuee);
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

    public void fatalError(SAXParseException spe) throws SAXException {
	System.out.println("Fatal error at line " + spe.getLineNumber());
	System.out.println(spe.getMessage());
	throw spe;
    }

    public void warning(SAXParseException spe) {
	System.out.println("Warning at line " + spe.getLineNumber());
	System.out.println(spe.getMessage());
    }

    public void error(SAXParseException spe) {
	System.out.println("Error at line " + spe.getLineNumber());
	System.out.println(spe.getMessage());
    }
    
    private double toDouble(String input){
	if(input == null || input.isEmpty()){
	    return 0; 
	}
	double value = 0.0;
	try{
	    value = Double.parseDouble(input);
	}
	catch(Exception ex){
	    //
	}
	return value;
    }

}
