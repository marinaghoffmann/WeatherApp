package com.example.findinglogs.model.repo.remote;


import androidx.annotation.NonNull;

import com.example.findinglogs.BuildConfig;
import com.example.findinglogs.model.model.ForecastResponse;
import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.model.repo.remote.api.ForecastCallback;
import com.example.findinglogs.model.repo.remote.api.ServicesInterfaceWrapper;
import com.example.findinglogs.model.repo.remote.api.WeatherCallback;
import com.example.findinglogs.model.util.Logger;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherManager {
    private static final String TAG = WeatherManager.class.getSimpleName();

    public WeatherManager() {
        if (Logger.ISLOGABLE) Logger.d(TAG, "WeatherManager()");
    }

    public void retrieveForecast(String localization, WeatherCallback callback) {
        if (Logger.ISLOGABLE) Logger.d(TAG, "retrieveForecast()");
        String apiKey = BuildConfig.WEATHER_API_KEY;
        String[] split = localization.split(",");
        String lat = split[0];
        String lon = split[1];

        ConnectionManager.getInstance()
                .getWeatherConnection()
                .create(ServicesInterfaceWrapper.WeatherService.class)
                .getWeather(lat, lon, apiKey).enqueue(new Callback<>() {

                    @Override
                    public void onResponse(@NonNull Call<Weather> call,
                                           @NonNull Response<Weather> resp) {
                        if (resp.isSuccessful() && resp.code() == HttpsURLConnection.HTTP_OK) {
                            assert resp.body() != null;
                            callback.onSuccess(resp.body());
                        } else {
                            if (Logger.ISLOGABLE)
                                Logger.w(TAG, "getForecast: status:" + resp.code());
                            callback.onFailure(String.valueOf(resp.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Weather> call,
                                          @NonNull Throwable throwable) {
                        callback.onFailure(String.valueOf(throwable.getMessage()));
                    }
                });
    }

    public void searchCity(String cityName, WeatherCallback callback) {
        if (Logger.ISLOGABLE) Logger.d(TAG, "searchCity: " + cityName);
        String apiKey = BuildConfig.WEATHER_API_KEY;

        ConnectionManager.getInstance()
                .getWeatherConnection()
                .create(ServicesInterfaceWrapper.WeatherService.class)
                .getWeatherByCity(cityName, apiKey).enqueue(new Callback<>() {

                    @Override
                    public void onResponse(@NonNull Call<Weather> call,
                                           @NonNull Response<Weather> resp) {
                        if (resp.isSuccessful() && resp.code() == HttpsURLConnection.HTTP_OK && resp.body() != null) {
                            callback.onSuccess(resp.body());
                        } else {
                            callback.onFailure(String.valueOf(resp.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Weather> call,
                                          @NonNull Throwable throwable) {
                        callback.onFailure(String.valueOf(throwable.getMessage()));
                    }
                });
    }

    public void retrieveFiveDayForecast(String lat, String lon, ForecastCallback callback) {
        if (Logger.ISLOGABLE) Logger.d(TAG, "retrieveFiveDayForecast()");
        String apiKey = BuildConfig.WEATHER_API_KEY;

        ConnectionManager.getInstance()
                .getWeatherConnection()
                .create(ServicesInterfaceWrapper.WeatherService.class)
                .getForecast(lat, lon, apiKey, 40).enqueue(new Callback<>() {

                    @Override
                    public void onResponse(@NonNull Call<ForecastResponse> call,
                                           @NonNull Response<ForecastResponse> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            callback.onSuccess(resp.body());
                        } else {
                            callback.onFailure(String.valueOf(resp.code()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ForecastResponse> call,
                                          @NonNull Throwable throwable) {
                        callback.onFailure(String.valueOf(throwable.getMessage()));
                    }
                });
    }
}
