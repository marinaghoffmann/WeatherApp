package com.example.findinglogs.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findinglogs.R;
import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.view.recyclerview.adapter.WeatherListAdapter;
import com.example.findinglogs.viewmodel.MainViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WeatherListAdapter adapter;
    private final List<Weather> weathers = new ArrayList<>();
    private FloatingActionButton fetchButton;
    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recycler_view_weather);
        fetchButton = findViewById(R.id.fetchButton);

        adapter = new WeatherListAdapter(this, weathers, weather -> {
            Intent intent = new Intent(this, WeatherDetailActivity.class);
            intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_NAME, weather.getName());
            intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_TEMP, weather.getMain().getTemp());
            intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_TEMP_MAX, weather.getMain().getTemp_max());
            intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_TEMP_MIN, weather.getMain().getTemp_min());
            intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_PRESSURE, weather.getMain().getPressure());
            intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_HUMIDITY, weather.getMain().getHumidity());
            intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_FEELS_LIKE, weather.getMain().getFeels_like());
            if (weather.getWeather() != null && !weather.getWeather().isEmpty()) {
                intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_DESCRIPTION,
                        weather.getWeather().get(0).getDescription());
                intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_ICON,
                        weather.getWeather().get(0).getIcon());
            }
            if (weather.getWind() != null) {
                intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_WIND_SPEED, weather.getWind().getSpeed());
                intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_WIND_DEG, weather.getWind().getDeg());
            }
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getWeatherList().observe(this,
                weathers -> adapter.updateWeathers(weathers));

        fetchButton.setOnClickListener(v -> {
            mainViewModel.fetchWeather();
            Toast.makeText(MainActivity.this,
                    "Atualizando dados...",
                    Toast.LENGTH_SHORT).show();
        });
    }
}