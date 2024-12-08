package com.example.heatstrokealertapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyWeatherViewHolder> {

    private List<HourlyWeather> hourlyWeatherList;

    public HourlyWeatherAdapter(List<HourlyWeather> hourlyWeatherList) {
        this.hourlyWeatherList = hourlyWeatherList;
    }

    @NonNull
    @Override
    public HourlyWeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each item in the list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_item_hourly, parent, false);
        return new HourlyWeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyWeatherViewHolder holder, int position) {
        HourlyWeather hourlyWeather = hourlyWeatherList.get(position);

        // Set the data for each item in the list
        holder.timeText.setText(hourlyWeather.getTime());
        holder.tempText.setText(hourlyWeather.getTempC() + "°C");

        // Load the icon based on the iconPath
        String iconPath = hourlyWeather.getIconPath();
        int iconResId = holder.itemView.getContext().getResources().getIdentifier(iconPath, "drawable", holder.itemView.getContext().getPackageName());

        // Load the icon using Glide or Picasso
        Glide.with(holder.weatherIcon.getContext())
                .load(iconResId)
                .into(holder.weatherIcon);
    }

    @Override
    public int getItemCount() {
        return hourlyWeatherList.size();
    }

    public static class HourlyWeatherViewHolder extends RecyclerView.ViewHolder {
        TextView timeText, tempText;
        ImageView weatherIcon;

        public HourlyWeatherViewHolder(View itemView) {
            super(itemView);
            timeText = itemView.findViewById(R.id.HourlyTimeText);
            tempText = itemView.findViewById(R.id.HourlyTempText);
            weatherIcon = itemView.findViewById(R.id.HourlyWeatherIcon);
        }
    }
}
