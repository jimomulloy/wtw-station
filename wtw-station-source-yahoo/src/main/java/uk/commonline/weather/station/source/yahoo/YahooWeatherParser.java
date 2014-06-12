package uk.commonline.weather.station.source.yahoo;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import uk.commonline.weather.model.Weather;

public class YahooWeatherParser {

    private static Logger log = Logger.getLogger(YahooWeatherParser.class);

    public Weather parseWeather(InputStream inputStream) throws Exception {

	Weather weather = null;

	// Process response
	Document response = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);

	XPathFactory factory = XPathFactory.newInstance();
	XPath xPath = factory.newXPath();

	// Get all search Result nodes
	NodeList nodes = (NodeList) xPath.evaluate("/place", response, XPathConstants.NODESET);
	int nodeCount = nodes.getLength();

	// iterate over search Result nodes
	for (int i = 0; i < nodeCount; i++) {
	    // Get each xpath expression as a string
	    String woeid = (String) xPath.evaluate("woeid", nodes.item(i), XPathConstants.STRING);
	    String country = (String) xPath.evaluate("country", nodes.item(i), XPathConstants.STRING);
	    String city = (String) xPath.evaluate("locality1", nodes.item(i), XPathConstants.STRING);
	    String postal = (String) xPath.evaluate("postal", nodes.item(i), XPathConstants.STRING);
	    // print out the Title, Summary, and URL for each search result
	    weather = new Weather();
	    // weather.setCity(city);
	    System.out.println(weather);
	}
	return weather;
    }

}
