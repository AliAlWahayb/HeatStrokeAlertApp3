package com.example.heatstrokealertapp;

public class HourlyWeather {
    private String time;
    private double tempC;
    private String iconPath;

    public HourlyWeather(String time, double tempC, String iconPath) {
        this.time = time;
        this.tempC = tempC;
        this.iconPath = iconPath;
    }

    // Getter methods
    public String getTime() {
        return time;
    }

    public double getTempC() {
        return tempC;
    }


    public String getIconPath() {
        return iconPath;
    }
}
