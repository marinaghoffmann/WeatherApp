package com.example.findinglogs.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.findinglogs.R;
import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.model.util.Utils;

import java.util.Locale;

public class WeatherDetailActivity extends AppCompatActivity {

    public static final String EXTRA_WEATHER_NAME = "extra_weather_name";
    public static final String EXTRA_WEATHER_TEMP = "extra_weather_temp";
    public static final String EXTRA_WEATHER_TEMP_MAX = "extra_weather_temp_max";
    public static final String EXTRA_WEATHER_TEMP_MIN = "extra_weather_temp_min";
    public static final String EXTRA_WEATHER_PRESSURE = "extra_weather_pressure";
    public static final String EXTRA_WEATHER_HUMIDITY = "extra_weather_humidity";
    public static final String EXTRA_WEATHER_FEELS_LIKE = "extra_weather_feels_like";
    public static final String EXTRA_WEATHER_DESCRIPTION = "extra_weather_description";
    public static final String EXTRA_WEATHER_ICON = "extra_weather_icon";
    public static final String EXTRA_WEATHER_WIND_SPEED = "extra_weather_wind_speed";
    public static final String EXTRA_WEATHER_WIND_DEG = "extra_weather_wind_deg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        // Enable back button on action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        // Get data from intent
        String name = getIntent().getStringExtra(EXTRA_WEATHER_NAME);
        float temp = getIntent().getFloatExtra(EXTRA_WEATHER_TEMP, 0f);
        float tempMax = getIntent().getFloatExtra(EXTRA_WEATHER_TEMP_MAX, 0f);
        float tempMin = getIntent().getFloatExtra(EXTRA_WEATHER_TEMP_MIN, 0f);
        float pressure = getIntent().getFloatExtra(EXTRA_WEATHER_PRESSURE, 0f);
        float humidity = getIntent().getFloatExtra(EXTRA_WEATHER_HUMIDITY, 0f);
        float feelsLike = getIntent().getFloatExtra(EXTRA_WEATHER_FEELS_LIKE, 0f);
        String description = getIntent().getStringExtra(EXTRA_WEATHER_DESCRIPTION);
        String icon = getIntent().getStringExtra(EXTRA_WEATHER_ICON);
        float windSpeed = getIntent().getFloatExtra(EXTRA_WEATHER_WIND_SPEED, 0f);
        int windDeg = getIntent().getIntExtra(EXTRA_WEATHER_WIND_DEG, 0);

        // Bind views
        TextView tvName = findViewById(R.id.detail_tv_name);
        TextView tvDescription = findViewById(R.id.detail_tv_description);
        TextView tvTemp = findViewById(R.id.detail_tv_temp);
        TextView tvFeelsLike = findViewById(R.id.detail_tv_feels_like);
        TextView tvTempMax = findViewById(R.id.detail_tv_temp_max);
        TextView tvTempMin = findViewById(R.id.detail_tv_temp_min);
        TextView tvPressure = findViewById(R.id.detail_tv_pressure);
        TextView tvHumidity = findViewById(R.id.detail_tv_humidity);
        TextView tvWindSpeed = findViewById(R.id.detail_tv_wind_speed);
        TextView tvWindDir = findViewById(R.id.detail_tv_wind_direction);
        ImageView imgIcon = findViewById(R.id.detail_img_icon);

        // Populate views
        tvName.setText(name);
        tvDescription.setText(description != null
                ? capitalizeFirst(description)
                : "—");
        tvTemp.setText(Utils.getCelsiusTemperatureFromKevin(temp));
        tvFeelsLike.setText(Utils.getCelsiusTemperatureFromKevin(feelsLike));
        tvTempMax.setText(Utils.getCelsiusTemperatureFromKevin(tempMax));
        tvTempMin.setText(Utils.getCelsiusTemperatureFromKevin(tempMin));
        tvPressure.setText(pressure + " hPa");
        tvHumidity.setText((int) humidity + "%");
        tvWindSpeed.setText(String.format(Locale.getDefault(), "%.1f km/h", windSpeed * 3.6f));
        tvWindDir.setText(getWindDirection(windDeg));

        // Load icon
        if (icon != null && !icon.isEmpty()) {
            String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
            Glide.with(this).load(iconUrl).into(imgIcon);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return Character.toUpperCase(text.charAt(0)) + text.substring(1);
    }

    private String getWindDirection(int deg) {
        String[] directions = {"N", "NE", "L", "SE", "S", "SO", "O", "NO"};
        int index = (int) Math.round(deg / 45.0) % 8;
        return directions[index];
    }
}
