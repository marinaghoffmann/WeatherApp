package com.example.findinglogs;

import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.model.model.WeatherDetail;
import com.example.findinglogs.model.model.WeatherInfo;
import com.example.findinglogs.model.model.Wind;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WeatherModelTest {

    @Test
    public void weather_settersAndGetters_workCorrectly() {
        Weather weather = new Weather();
        weather.setName("Recife");

        WeatherInfo info = new WeatherInfo(300f, 301f, 298f, 303f, 1013f, 80f);
        weather.setMain(info);

        assertEquals("Recife", weather.getName());
        assertEquals(300f, weather.getMain().getTemp(), 0.01f);
        assertEquals(301f, weather.getMain().getFeels_like(), 0.01f);
        assertEquals(298f, weather.getMain().getTemp_min(), 0.01f);
        assertEquals(303f, weather.getMain().getTemp_max(), 0.01f);
        assertEquals(1013f, weather.getMain().getPressure(), 0.01f);
        assertEquals(80f, weather.getMain().getHumidity(), 0.01f);
    }

    @Test
    public void weatherDetail_settersAndGetters_workCorrectly() {
        WeatherDetail detail = new WeatherDetail();
        detail.setMain("Clouds");
        detail.setDescription("nuvens dispersas");
        detail.setIcon("03d");

        assertEquals("Clouds", detail.getMain());
        assertEquals("nuvens dispersas", detail.getDescription());
        assertEquals("03d", detail.getIcon());
    }

    @Test
    public void wind_settersAndGetters_workCorrectly() {
        Wind wind = new Wind();
        wind.setSpeed(5.5f);
        wind.setDeg(180);

        assertEquals(5.5f, wind.getSpeed(), 0.01f);
        assertEquals(180, wind.getDeg());
    }

    @Test
    public void weather_defaultWeatherList_isNotNull() {
        Weather weather = new Weather();
        assertNotNull(weather.getWeather());
        assertTrue(weather.getWeather().isEmpty());
    }

    @Test
    public void weather_toString_containsName() {
        Weather weather = new Weather();
        weather.setName("Caruaru");
        assertTrue(weather.toString().contains("Caruaru"));
    }

    @Test
    public void weatherList_sorting_worksAlphabetically() {
        Weather w1 = new Weather();
        w1.setName("Recife");
        Weather w2 = new Weather();
        w2.setName("Caruaru");
        Weather w3 = new Weather();
        w3.setName("Petrolina");

        List<Weather> list = new ArrayList<>();
        list.add(w1);
        list.add(w2);
        list.add(w3);
        list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));

        assertEquals("Caruaru", list.get(0).getName());
        assertEquals("Petrolina", list.get(1).getName());
        assertEquals("Recife", list.get(2).getName());
    }

    @Test
    public void weatherInfo_constructor_setsAllFields() {
        WeatherInfo info = new WeatherInfo(300f, 301f, 298f, 303f, 1013f, 80f);
        assertEquals(300f, info.getTemp(), 0.01f);
        assertEquals(301f, info.getFeels_like(), 0.01f);
        assertEquals(298f, info.getTemp_min(), 0.01f);
        assertEquals(303f, info.getTemp_max(), 0.01f);
        assertEquals(1013f, info.getPressure(), 0.01f);
        assertEquals(80f, info.getHumidity(), 0.01f);
    }
}
