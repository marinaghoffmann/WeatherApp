package com.example.findinglogs.model.repo.remote.api;

import com.example.findinglogs.model.model.ForecastResponse;

public interface ForecastCallback {
    void onSuccess(ForecastResponse response);
    void onFailure(String msg);
}
