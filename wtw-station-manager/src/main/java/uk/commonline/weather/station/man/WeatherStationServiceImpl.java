package uk.commonline.weather.station.man;

import static akka.dispatch.Futures.future;
import static akka.dispatch.Futures.sequence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import scala.concurrent.Await;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import uk.commonline.weather.model.Weather;
import uk.commonline.weather.model.WeatherForecast;
import uk.commonline.weather.model.WeatherReport;
import uk.commonline.weather.model.WeatherReport.WeatherSourceData;
import uk.commonline.weather.station.service.WeatherStationService;
import uk.commonline.weather.station.source.WeatherStationSource;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Mapper;

public class WeatherStationServiceImpl implements WeatherStationService {

    public class StationCurrentWeatherCaller implements Callable<Weather> {

        private WeatherStationSource weatherStationSource;
        private double latitude, longitude;

        public StationCurrentWeatherCaller(WeatherStationSource weatherStationSource, double latitude, double longitude) {
            this.weatherStationSource = weatherStationSource;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public Weather call() throws Exception {
            return weatherStationSource.getCurrentWeather(latitude, longitude);
        }
    }

    public final class StationCurrentWeatherMapper extends Mapper<Iterable<Weather>, List<Weather>> {
        List<Weather> report;

        public StationCurrentWeatherMapper(List<Weather> report) {
            this.report = report;
        }

        @Override
        public List<Weather> apply(Iterable<Weather> result) {
            for (Weather w : result) {
                report.add(w);
            }
            return report;
        }
    }

    public class StationForecastWeatherCaller implements Callable<List<WeatherForecast>> {

        private WeatherStationSource weatherStationSource;
        private double latitude, longitude;
        private int hours, count;

        public StationForecastWeatherCaller(WeatherStationSource weatherStationSource, double latitude, double longitude, int hours, int count) {
            this.weatherStationSource = weatherStationSource;
            this.latitude = latitude;
            this.longitude = longitude;
            this.hours = hours;
            this.count = count;
        }

        @Override
        public List<WeatherForecast> call() throws Exception {
            try {
                return weatherStationSource.getForecastWeather(latitude, longitude, hours, count);
            } catch (Exception ex) {
                return new ArrayList<WeatherForecast>();
            }
        }
    }

    public class StationReportCaller implements Callable<List<Weather>> {

        private WeatherStationSource weatherStationSource;
        private double latitude, longitude;

        public StationReportCaller(WeatherStationSource weatherStationSource, double latitude, double longitude) {
            this.weatherStationSource = weatherStationSource;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public List<Weather> call() {
            try {
                return weatherStationSource.report(latitude, longitude);
            } catch (Exception ex) {
                return new ArrayList<Weather>();
            }
        }
    }

    public final class StationWeatherForecastMapper extends Mapper<Iterable<List<WeatherForecast>>, List<WeatherForecast>> {
        List<WeatherForecast> report;

        public StationWeatherForecastMapper(List<WeatherForecast> report) {
            this.report = report;
        }

        @Override
        public List<WeatherForecast> apply(Iterable<List<WeatherForecast>> result) {
            for (List<WeatherForecast> r : result) {
                report.addAll(r);
            }
            return report;
        }
    }

    public final class StationWeatherMapper extends Mapper<Iterable<List<Weather>>, List<Weather>> {
        List<Weather> report;

        public StationWeatherMapper(List<Weather> report) {
            this.report = report;
        }

        @Override
        public List<Weather> apply(Iterable<List<Weather>> result) {
            for (List<Weather> r : result) {
                report.addAll(r);
            }
            return report;
        }
    }

    @Inject
    List<WeatherStationSource> weatherStationSources;

    private WeatherReport buildReport(List<Weather> weathers, double latitude, double longitude) {
        WeatherReport report = new WeatherReport();
        report.setLatitude(latitude);
        report.setLongitude(longitude);
        Map<String, WeatherSourceData> sourceMap = report.getSourceMap();
        for (Weather weather : weathers) {
            WeatherSourceData wsd;
            if (!sourceMap.containsKey(weather.getSource())) {
                wsd = report.new WeatherSourceData();
                sourceMap.put(weather.getSource(), wsd);
                wsd.setStatus(0);
            }
            wsd = sourceMap.get(weather.getSource());
            if (weather instanceof WeatherForecast) {
                wsd.getForecasts().add((WeatherForecast) weather);
            } else {
                wsd.getRecordings().add(weather);
            }
        }
        for (WeatherStationSource weatherStationSource : weatherStationSources) {
            if (!sourceMap.containsKey(weatherStationSource.getSourceName())) {
                WeatherSourceData wsd = report.new WeatherSourceData();
                sourceMap.put(weatherStationSource.getSourceName(), wsd);
                wsd.setStatus(1);
            }
        }
        report.setDate(new Date());
        return report;
    }

    @Override
    public List<Weather> getCurrentWeather(double latitude, double longitude) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        ExecutionContext ec = ExecutionContexts.fromExecutorService(executor);

        List<Future<Weather>> futures = new ArrayList<Future<Weather>>();

        List<Weather> report = new ArrayList<Weather>();
        for (WeatherStationSource weatherStationSource : weatherStationSources) {
            futures.add(future(new StationCurrentWeatherCaller(weatherStationSource, latitude, longitude), ec));
        }

        // compose a sequence of the futures
        Future<Iterable<Weather>> futuresSequence = sequence(futures, ec);

        Future<List<Weather>> futureSum = futuresSequence.map(new StationCurrentWeatherMapper(report), ec);

        try {
            report = Await.result(futureSum, Duration.apply(120, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();

        return report;
    }

    @Override
    public List<WeatherForecast> getForecastWeather(double latitude, double longitude, int hours, int count) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        ExecutionContext ec = ExecutionContexts.fromExecutorService(executor);

        List<Future<List<WeatherForecast>>> futures = new ArrayList<Future<List<WeatherForecast>>>();

        List<WeatherForecast> report = new ArrayList<WeatherForecast>();
        for (WeatherStationSource weatherStationSource : weatherStationSources) {
            futures.add(future(new StationForecastWeatherCaller(weatherStationSource, latitude, longitude, hours, count), ec));
        }

        // compose a sequence of the futures
        Future<Iterable<List<WeatherForecast>>> futuresSequence = sequence(futures, ec);

        Future<List<WeatherForecast>> futureSum = futuresSequence.map(new StationWeatherForecastMapper(report), ec);

        try {
            report = Await.result(futureSum, Duration.apply(120, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();

        return report;
    }

    @Override
    public WeatherReport getWeatherReport(double latitude, double longitude) throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(3);
        ExecutionContext ec = ExecutionContexts.fromExecutorService(executor);

        List<Future<List<Weather>>> futures = new ArrayList<Future<List<Weather>>>();

        List<Weather> report = new ArrayList<Weather>();
        for (WeatherStationSource weatherStationSource : weatherStationSources) {
            futures.add(future(new StationReportCaller(weatherStationSource, latitude, longitude), ec));
        }

        // compose a sequence of the futures
        Future<Iterable<List<Weather>>> futuresSequence = sequence(futures, ec);

        Future<List<Weather>> futureSum = futuresSequence.map(new StationWeatherMapper(report), ec);

        try {
            report = Await.result(futureSum, Duration.apply(120, TimeUnit.SECONDS));
        } catch (Exception e) {
            e.printStackTrace();
        }

        executor.shutdown();

        return buildReport(report, latitude, longitude);
    }

    protected List<WeatherStationSource> getWeatherStationSources() {
        return weatherStationSources;
    }

    public void setWeatherStationSources(List<WeatherStationSource> geoLocationSources) {
        this.weatherStationSources = geoLocationSources;
    }
}