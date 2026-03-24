package com.example.findinglogs.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.findinglogs.R;
import com.example.findinglogs.model.model.ForecastItem;
import com.example.findinglogs.model.model.ForecastResponse;
import com.example.findinglogs.model.repo.Repository;
import com.example.findinglogs.model.repo.remote.api.ForecastCallback;
import com.example.findinglogs.model.repo.remote.WeatherManager;
import com.example.findinglogs.model.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    public static final String EXTRA_WEATHER_LAT = "extra_weather_lat";
    public static final String EXTRA_WEATHER_LON = "extra_weather_lon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

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
        float lat = getIntent().getFloatExtra(EXTRA_WEATHER_LAT, 0f);
        float lon = getIntent().getFloatExtra(EXTRA_WEATHER_LON, 0f);

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

        tvName.setText(name);
        tvDescription.setText(description != null
                ? capitalizeFirst(description)
                : getString(R.string.no_description));
        tvTemp.setText(Utils.getCelsiusTemperatureFromKevin(temp));
        tvFeelsLike.setText(Utils.getCelsiusTemperatureFromKevin(feelsLike));
        tvTempMax.setText(Utils.getCelsiusTemperatureFromKevin(tempMax));
        tvTempMin.setText(Utils.getCelsiusTemperatureFromKevin(tempMin));
        tvPressure.setText(pressure + " hPa");
        tvHumidity.setText((int) humidity + "%");
        tvWindSpeed.setText(String.format(Locale.getDefault(), "%.1f km/h", windSpeed * 3.6f));
        tvWindDir.setText(getWindDirection(windDeg));

        if (icon != null && !icon.isEmpty()) {
            String iconUrl = "https://openweathermap.org/img/wn/" + icon + "@2x.png";
            Glide.with(this).load(iconUrl).into(imgIcon);
        }

        if (lat != 0f || lon != 0f) {
            loadForecast(String.valueOf(lat), String.valueOf(lon));
        }
    }

    private void loadForecast(String lat, String lon) {
        View forecastCard = findViewById(R.id.forecast_card);
        ProgressBar forecastProgress = findViewById(R.id.forecast_progress);
        LinearLayout forecastContainer = findViewById(R.id.forecast_container);

        forecastCard.setVisibility(View.VISIBLE);
        forecastProgress.setVisibility(View.VISIBLE);

        WeatherManager weatherManager = new WeatherManager();
        weatherManager.retrieveFiveDayForecast(lat, lon, new ForecastCallback() {
            @Override
            public void onSuccess(ForecastResponse response) {
                forecastProgress.setVisibility(View.GONE);
                List<ForecastItem> items = response.getList();
                if (items == null || items.isEmpty()) return;

                SimpleDateFormat dayFmt = new SimpleDateFormat("EEE", new Locale("pt", "BR"));
                SimpleDateFormat hourFmt = new SimpleDateFormat("HH:mm", Locale.getDefault());

                for (ForecastItem item : items) {
                    View itemView = LayoutInflater.from(WeatherDetailActivity.this)
                            .inflate(R.layout.forecast_item, forecastContainer, false);

                    TextView tvDay = itemView.findViewById(R.id.tv_forecast_day);
                    TextView tvHour = itemView.findViewById(R.id.tv_forecast_hour);
                    ImageView imgIcon = itemView.findViewById(R.id.img_forecast_icon);
                    TextView tvTemp = itemView.findViewById(R.id.tv_forecast_temp);
                    TextView tvDesc = itemView.findViewById(R.id.tv_forecast_desc);

                    Date date = new Date(item.getDt() * 1000);
                    tvDay.setText(capitalizeFirst(dayFmt.format(date)));
                    tvHour.setText(hourFmt.format(date));
                    tvTemp.setText(Utils.getCelsiusTemperatureFromKevin(item.getMain().getTemp()));

                    if (item.getWeather() != null && !item.getWeather().isEmpty()) {
                        String iconCode = item.getWeather().get(0).getIcon();
                        String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                        Glide.with(WeatherDetailActivity.this).load(iconUrl).into(imgIcon);
                        tvDesc.setText(capitalizeFirst(item.getWeather().get(0).getDescription()));
                    }

                    forecastContainer.addView(itemView);
                }
            }

            @Override
            public void onFailure(String msg) {
                forecastProgress.setVisibility(View.GONE);
                forecastCard.setVisibility(View.GONE);
            }
        });
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
