package com.example.findinglogs.model.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastItem {
    private WeatherInfo main;
    private List<WeatherDetail> weather;
    private Wind wind;

    @SerializedName("dt_txt")
    private String dtTxt;

    private long dt;

    public WeatherInfo getMain() {
        return main;
    }

    public List<WeatherDetail> getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }

    public String getDtTxt() {
        return dtTxt;
    }

    public long getDt() {
        return dt;
    }
}
