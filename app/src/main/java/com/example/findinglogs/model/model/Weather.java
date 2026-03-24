package com.example.findinglogs.model.model;


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Weather {
    private WeatherInfo main;
    private String name;
    private List<WeatherDetail> weather = new ArrayList<>();
    private Wind wind;
    private Coord coord;

    public Weather() {
    }

    public WeatherInfo getMain() {
        return main;
    }

    public void setMain(WeatherInfo main) {
        this.main = main;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WeatherDetail> getWeather() {
        return weather;
    }

    public void setWeather(List<WeatherDetail> weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    @NonNull
    @Override
    public String toString() {
        return "Weather{" +
                "main=" + main +
                ", name='" + name + '\'' +
                ", weather=" + weather +
                ", wind=" + wind +
                '}';
    }

    public static class Coord {
        private float lon;
        private float lat;

        public float getLon() { return lon; }
        public float getLat() { return lat; }
    }
}
