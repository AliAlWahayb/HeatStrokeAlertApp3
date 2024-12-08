package com.example.heatstrokealertapp;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private List<WeatherItem> forecastList;

    public WeatherAdapter(List<WeatherItem> forecastList) {
        this.forecastList = forecastList;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item_daily, parent, false);
        return new WeatherViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        WeatherItem weatherItem = forecastList.get(position);

        // Set the data for each item in the list
        holder.dateText.setText(weatherItem.getDate());
        holder.MaxTemperatureText.setText(weatherItem.getTempMax() + "°C");
        holder.MinTemperatureText.setText(weatherItem.getTempMin() + "°C");

        String iconPath = weatherItem.getIcon();

        // Get the resource ID based on the icon name
        int iconResId = holder.itemView.getContext().getResources().getIdentifier(iconPath, "drawable", holder.itemView.getContext().getPackageName());

        // Check if the resource ID is valid
        if (iconResId == 0) {
            // If the icon is not found, load a default icon
            iconResId = R.drawable.safe;
        }

        // Load the icon dynamically using Glide
        Glide.with(holder.weatherIcon.getContext())
                .load(iconResId)
                .into(holder.weatherIcon);
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, MaxTemperatureText, MinTemperatureText;
        ImageView weatherIcon;

        public WeatherViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.ForecastDateText);
            MaxTemperatureText = itemView.findViewById(R.id.MaxTemperatureText);
            MinTemperatureText = itemView.findViewById(R.id.MinTemperatureText);
            weatherIcon = itemView.findViewById(R.id.DailyWeatherIcon);
        }
    }
}
