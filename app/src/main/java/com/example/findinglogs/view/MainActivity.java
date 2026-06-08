package com.example.findinglogs.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.findinglogs.R;
import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.view.recyclerview.adapter.WeatherListAdapter;
import com.example.findinglogs.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG_LIFECYCLE = "Lifecycle_Main";
    private static final String TAG_BROADCAST = "Broadcast_Main";

    private WeatherListAdapter adapter;
    private final List<Weather> weathers = new ArrayList<>();
    private MainViewModel mainViewModel;

    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout loadingView;
    private LinearLayout errorView;
    private LinearLayout emptyView;
    private TextView tvErrorMessage;
    private TextView tvOfflineBanner;
    private final BroadcastReceiver connectivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean conectado = isNetworkAvailable();
            if (conectado) {
                Log.d(TAG_BROADCAST, "onReceive: internet voltou, atualizando dados...");
                Toast.makeText(context, "Internet voltou! Atualizando...", Toast.LENGTH_SHORT).show();
                tvOfflineBanner.setVisibility(View.GONE);
                mainViewModel.fetchWeather();
            } else {
                Log.d(TAG_BROADCAST, "onReceive: internet caiu, mostrando aviso");
                Toast.makeText(context, "Sem internet!", Toast.LENGTH_SHORT).show();
                tvOfflineBanner.setVisibility(View.VISIBLE);
            }
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG_LIFECYCLE, "onCreate: Activity criada");
        Toast.makeText(this, "Main → onCreate", Toast.LENGTH_SHORT).show();

        RecyclerView recyclerView = findViewById(R.id.recycler_view_weather);
        FloatingActionButton addCityButton = findViewById(R.id.addCityButton);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        loadingView = findViewById(R.id.loading_view);
        errorView = findViewById(R.id.error_view);
        emptyView = findViewById(R.id.empty_view);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        tvOfflineBanner = findViewById(R.id.tv_offline_banner);
        MaterialButton btnRetry = findViewById(R.id.btn_retry);

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
            if (weather.getCoord() != null) {
                intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_LAT, weather.getCoord().getLat());
                intent.putExtra(WeatherDetailActivity.EXTRA_WEATHER_LON, weather.getCoord().getLon());
            }
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getWeatherList().observe(this,
                weathers -> adapter.updateWeathers(weathers));

        mainViewModel.getUiState().observe(this, state -> {
            loadingView.setVisibility(state == MainViewModel.UiState.LOADING ? View.VISIBLE : View.GONE);
            errorView.setVisibility(state == MainViewModel.UiState.ERROR ? View.VISIBLE : View.GONE);
            emptyView.setVisibility(state == MainViewModel.UiState.EMPTY ? View.VISIBLE : View.GONE);
            swipeRefresh.setVisibility(state == MainViewModel.UiState.SUCCESS ? View.VISIBLE : View.GONE);
        });

        mainViewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null) tvErrorMessage.setText(msg);
        });

        mainViewModel.getIsRefreshing().observe(this, refreshing ->
                swipeRefresh.setRefreshing(Boolean.TRUE.equals(refreshing)));

        mainViewModel.getSearchResult().observe(this, result -> {
            if (result == null) return;
            if (result.equals("ALREADY_ADDED")) {
                Toast.makeText(this, R.string.city_already_added, Toast.LENGTH_SHORT).show();
            } else if (result.equals("NOT_FOUND")) {
                Toast.makeText(this, R.string.error_city_not_found, Toast.LENGTH_SHORT).show();
            } else if (result.startsWith("SUCCESS:")) {
                String cityName = result.substring(8);
                Toast.makeText(this, getString(R.string.city_added, cityName), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefresh.setOnRefreshListener(() -> mainViewModel.fetchWeather());
        btnRetry.setOnClickListener(v -> mainViewModel.fetchWeather());
        addCityButton.setOnClickListener(v -> showSearchDialog());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG_LIFECYCLE, "onStart: Activity visível");
        Toast.makeText(this, "Main → onStart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_LIFECYCLE, "onResume: Activity em foco (interativa)");
        Toast.makeText(this, "Main → onResume", Toast.LENGTH_SHORT).show();

        IntentFilter filtro = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityReceiver, filtro);
        Log.d(TAG_BROADCAST, "onResume: receiver de conectividade registrado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG_LIFECYCLE, "onPause: Activity perdeu o foco");
        Toast.makeText(this, "Main → onPause", Toast.LENGTH_SHORT).show();

        unregisterReceiver(connectivityReceiver);
        Log.d(TAG_BROADCAST, "onPause: receiver de conectividade desregistrado");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG_LIFECYCLE, "onStop: Activity não está mais visível");
        Toast.makeText(this, "Main → onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG_LIFECYCLE, "onRestart: Activity voltando do onStop");
        Toast.makeText(this, "Main → onRestart", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG_LIFECYCLE, "onDestroy: Activity destruída");
        Toast.makeText(this, "Main → onDestroy", Toast.LENGTH_SHORT).show();
    }

    private void showSearchDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_search_city, null);
        EditText etCityName = dialogView.findViewById(R.id.et_city_name);

        new AlertDialog.Builder(this)
                .setTitle(R.string.search_city_title)
                .setView(dialogView)
                .setPositiveButton(R.string.add, (dialog, which) -> {
                    String cityName = etCityName.getText().toString().trim();
                    if (!cityName.isEmpty()) {
                        mainViewModel.searchAndAddCity(cityName);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}