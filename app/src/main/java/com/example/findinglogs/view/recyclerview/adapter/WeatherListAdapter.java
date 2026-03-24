package com.example.findinglogs.view.recyclerview.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findinglogs.R;
import com.example.findinglogs.model.model.Weather;
import com.example.findinglogs.model.util.Utils;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.ViewHolder> {

    public interface OnWeatherClickListener {
        void onWeatherClick(Weather weather);
    }

    private final Context context;
    private final List<Weather> weathers;
    private final OnWeatherClickListener listener;

    public WeatherListAdapter(Context context, List<Weather> weathers, OnWeatherClickListener listener) {
        this.context = context;
        this.weathers = new ArrayList<>(weathers);
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView name;
        private final TextView temp_current;
        private final TextView temp_max;
        private final TextView temp_min;
        private final TextView pressure;
        private final TextView humidity;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            name = itemView.findViewById(R.id.tv_weather_name);
            temp_current = itemView.findViewById(R.id.temp_current);
            temp_max = itemView.findViewById(R.id.temp_max);
            temp_min = itemView.findViewById(R.id.temp_min);
            pressure = itemView.findViewById(R.id.pressure);
            humidity = itemView.findViewById(R.id.humidity);
            imageView = itemView.findViewById(R.id.img_view_item);
        }

        public void holdWeather(Weather weather, Context context) {
            if (weather.getWeather() != null && !weather.getWeather().isEmpty()) {
                String iconCode = weather.getWeather().get(0).getIcon();
                setCardColor(iconCode, context);

                String iconUrl = "https://openweathermap.org/img/wn/"
                        + iconCode + "@2x.png";
                Glide.with(context)
                        .load(iconUrl)
                        .into(imageView);
            }

            name.setText(weather.getName());
            temp_current.setText(context.getString(R.string.temp_current_label,
                    Utils.getCelsiusTemperatureFromKevin(weather.getMain().getTemp())));
            temp_max.setText(context.getString(R.string.temp_max_label,
                    Utils.getCelsiusTemperatureFromKevin(weather.getMain().getTemp_max())));
            temp_min.setText(context.getString(R.string.temp_min_label,
                    Utils.getCelsiusTemperatureFromKevin(weather.getMain().getTemp_min())));
            pressure.setText(context.getString(R.string.pressure_label,
                    String.valueOf(weather.getMain().getPressure())));
            humidity.setText(context.getString(R.string.humidity_label,
                    String.valueOf(weather.getMain().getHumidity())));
        }

        private void setCardColor(String iconCode, Context context) {
            switch (iconCode) {
                case "02d":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_few_clouds));
                    break;
                case "02n":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_few_clouds_dark));
                    break;
                case "03d":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_cloudy));
                    break;
                case "03n":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_cloudy_dark));
                    break;
                case "04d":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_scattered_clouds));
                    break;
                case "04n":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_scattered_clouds_dark));
                    break;
                case "09d":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_fog));
                    break;
                case "09n":
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_fog_dark));
                    break;
                default:
                    cardView.setCardBackgroundColor(context.getColor(R.color.weather_few_clouds));
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Weather weather = weathers.get(position);
        holder.holdWeather(weather, context);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onWeatherClick(weather);
        });
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }

    public void updateWeathers(List<Weather> weathersValues) {
        weathers.clear();
        weathers.addAll(weathersValues);
        notifyDataSetChanged();
    }
}
