package com.example.findinglogs.model.model;

import androidx.annotation.NonNull;

public class Wind {
    private float speed;
    private int deg;

    public Wind() {
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    @NonNull
    @Override
    public String toString() {
        return "Wind{" +
                "speed=" + speed +
                ", deg=" + deg +
                '}';
    }
}
