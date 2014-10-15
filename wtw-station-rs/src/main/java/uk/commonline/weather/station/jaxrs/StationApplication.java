package uk.commonline.weather.station.jaxrs;

import java.util.logging.Logger;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author Jim O'Mulloy
 * 
 * WTW Station Service JAXRS Application configuration.
 *
 */
@ApplicationPath("webresources")
public class StationApplication extends ResourceConfig {
    public StationApplication() {
        packages("uk.commonline.weather.station.jaxrs;uk.commonline.weather.model;org.codehaus.jackson.jaxrs");

        // Enable LoggingFilter & output entity.
        //registerInstances(new LoggingFilter(Logger.getLogger(StationApplication.class.getName()), true));

    }
}