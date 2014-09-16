package uk.commonline.weather.station.source;

public class ConditionInfo {
    public int code;
    public String url;
    public String text;

    public ConditionInfo(int code, String url, String text) {
        this.code = code;
        this.url = url;
        this.text = text;
    }
}