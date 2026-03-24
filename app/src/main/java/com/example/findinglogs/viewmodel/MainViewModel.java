package com.example.findinglogs.viewmodel;


import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.model.repo.Repository;
import com.example.findinglogs.model.repo.remote.api.WeatherCallback;
import com.example.findinglogs.model.util.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public enum UiState { LOADING, SUCCESS, ERROR, EMPTY }

    private static final String TAG = MainViewModel.class.getSimpleName();
    private static final int FETCH_INTERVAL = 120_000;
    private final Repository mRepository;
    private final MutableLiveData<List<Weather>> _weatherList = new MutableLiveData<>(new ArrayList<>());
    private final LiveData<List<Weather>> weatherList = _weatherList;

    private final MutableLiveData<UiState> _uiState = new MutableLiveData<>(UiState.LOADING);
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<String> _searchResult = new MutableLiveData<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable fetchRunnable = this::fetchAllForecasts;

    private final List<String> userCities = new ArrayList<>();

    public MainViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
        startFetching();
    }

    public LiveData<List<Weather>> getWeatherList() {
        return weatherList;
    }

    public LiveData<UiState> getUiState() {
        return _uiState;
    }

    public LiveData<String> getErrorMessage() {
        return _errorMessage;
    }

    public LiveData<Boolean> getIsRefreshing() {
        return _isRefreshing;
    }

    public LiveData<String> getSearchResult() {
        return _searchResult;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void startFetching() {
        fetchAllForecasts();
        handler.postDelayed(fetchRunnable, FETCH_INTERVAL);
    }

    public void fetchAllForecasts() {
        handler.removeCallbacks(fetchRunnable);

        if (!isNetworkAvailable()) {
            _uiState.setValue(UiState.ERROR);
            _errorMessage.setValue("Sem conexao com a internet");
            _isRefreshing.setValue(false);
            handler.postDelayed(fetchRunnable, FETCH_INTERVAL);
            return;
        }

        if (Logger.ISLOGABLE) Logger.d(TAG, "fetchAllForecasts()");

        List<Weather> currentList = _weatherList.getValue();
        if (currentList == null || currentList.isEmpty()) {
            _uiState.setValue(UiState.LOADING);
        }

        HashMap<String, String> localizations = mRepository.getLocalizations();
        int totalRequests = localizations.size() + userCities.size();

        if (totalRequests == 0) {
            _uiState.setValue(UiState.EMPTY);
            _isRefreshing.setValue(false);
            return;
        }

        HashMap<String, Weather> uniqueWeathers = new HashMap<>();
        int[] responseCount = {0};
        int[] errorCount = {0};

        WeatherCallback aggregateCallback = new WeatherCallback() {
            @Override
            public void onSuccess(Weather result) {
                uniqueWeathers.put(result.getName(), result);
                responseCount[0]++;
                checkComplete(responseCount[0], totalRequests, errorCount[0], uniqueWeathers);
            }

            @Override
            public void onFailure(String error) {
                responseCount[0]++;
                errorCount[0]++;
                checkComplete(responseCount[0], totalRequests, errorCount[0], uniqueWeathers);
            }
        };

        for (String latlon : localizations.values()) {
            mRepository.retrieveForecast(latlon, aggregateCallback);
        }

        for (String cityName : userCities) {
            mRepository.searchCity(cityName, aggregateCallback);
        }
    }

    private void checkComplete(int responses, int total, int errors,
                               HashMap<String, Weather> uniqueWeathers) {
        if (responses == total) {
            List<Weather> sortedList = new ArrayList<>(uniqueWeathers.values());
            sortedList.sort((w1, w2) ->
                    w1.getName().compareToIgnoreCase(w2.getName()));
            _weatherList.setValue(sortedList);
            _isRefreshing.setValue(false);

            if (sortedList.isEmpty()) {
                _uiState.setValue(UiState.ERROR);
                _errorMessage.setValue("Erro ao carregar dados");
            } else {
                _uiState.setValue(UiState.SUCCESS);
            }

            handler.postDelayed(fetchRunnable, FETCH_INTERVAL);
        }
    }

    public void searchAndAddCity(String cityName) {
        if (!isNetworkAvailable()) {
            _searchResult.setValue("Sem conexao com a internet");
            return;
        }

        mRepository.searchCity(cityName, new WeatherCallback() {
            @Override
            public void onSuccess(Weather result) {
                List<Weather> current = _weatherList.getValue();
                if (current != null) {
                    for (Weather w : current) {
                        if (w.getName().equalsIgnoreCase(result.getName())) {
                            _searchResult.setValue("ALREADY_ADDED");
                            return;
                        }
                    }
                }

                userCities.add(result.getName());

                List<Weather> updated = new ArrayList<>(current != null ? current : new ArrayList<>());
                updated.add(result);
                updated.sort((w1, w2) ->
                        w1.getName().compareToIgnoreCase(w2.getName()));
                _weatherList.setValue(updated);
                _uiState.setValue(UiState.SUCCESS);
                _searchResult.setValue("SUCCESS:" + result.getName());
            }

            @Override
            public void onFailure(String msg) {
                _searchResult.setValue("NOT_FOUND");
            }
        });
    }

    public void fetchWeather() {
        _isRefreshing.setValue(true);
        handler.removeCallbacks(fetchRunnable);
        fetchAllForecasts();
    }

    public Repository getRepository() {
        return mRepository;
    }

    @Override
    protected void onCleared() {
        handler.removeCallbacks(fetchRunnable);
        super.onCleared();
    }
}
