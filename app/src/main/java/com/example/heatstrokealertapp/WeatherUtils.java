package com.example.heatstrokealertapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.widget.Toast;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;

public class WeatherUtils {

    private Context context;
    private TextView cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView, HumidityTextView, DewPointTextView,
            VisibilityTextView, PressureTextTextView, WindDegTextView, WindSpeedTextView, UvIndexTextTextView,
            SunriseTextView, SunSetTextView;

    private RecyclerView hourlyWeatherRecyclerView, recyclerView;

    public WeatherUtils(Context context, TextView cityNameTextView, TextView tMinMaxTextView,
                        TextView FeelsLikeTempTextView, TextView HumidityTextView, TextView DewPointTextView,
                        TextView VisibilityTextView, TextView PressureTextTextView, TextView WindDegTextView,
                        TextView WindSpeedTextView, TextView UvIndexTextTextView, TextView SunriseTextView,
                        TextView SunSetTextView, RecyclerView hourlyWeatherRecyclerView, RecyclerView recyclerView) {
        this.context = context;
        this.cityNameTextView = cityNameTextView;
        this.tMinMaxTextView = tMinMaxTextView;
        this.FeelsLikeTempTextView = FeelsLikeTempTextView;
        this.HumidityTextView = HumidityTextView;
        this.DewPointTextView = DewPointTextView;
        this.VisibilityTextView = VisibilityTextView;
        this.PressureTextTextView = PressureTextTextView;
        this.WindDegTextView = WindDegTextView;
        this.WindSpeedTextView = WindSpeedTextView;
        this.UvIndexTextTextView = UvIndexTextTextView;
        this.SunriseTextView = SunriseTextView;
        this.SunSetTextView = SunSetTextView;
        this.hourlyWeatherRecyclerView = hourlyWeatherRecyclerView;
        this.recyclerView = recyclerView;
    }

    public void fetchCityName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        new Thread(() -> {
            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    String cityName = addresses.get(0).getLocality();
                    updateCityNameOnUI(cityName);
                    fetchWeatherData(cityName);
                } else {
                    String cityName = "City not found";
                    updateCityNameOnUI(cityName);
                }
            } catch (IOException e) {
                e.printStackTrace();
                String cityName = "Geocoder error";
                updateCityNameOnUI(cityName);
            }
        }).start();
    }

    private void updateCityNameOnUI(String cityName) {
        new Handler(Looper.getMainLooper()).post(() -> cityNameTextView.setText(cityName));
    }

    public  void fetchWeatherData(String cityName) {
        WeatherApi.getWeatherData(cityName, new WeatherApi.WeatherDataCallback() {
            @Override
            public void onSuccess(ArrayList<ArrayList<String>> hourlyWeatherDataList,
                                  ArrayList<ArrayList<String>> weatherDataList,
                                  String weatherMain, double temp, double feelsLike, double dewPoint,
                                  double pressure, int humidity, double Uv, double visibility,
                                  double windSpeed, int windDeg, String sunrise, String sunset,
                                  double minTempC, double maxTempC, double avgTempC, int avgHumidity) {

                // Save the weather data to SharedPreferences
                saveWeatherDataToPreferences(cityName, weatherMain, maxTempC, minTempC, feelsLike, humidity,
                        dewPoint, visibility, pressure, windDeg, windSpeed, sunrise, sunset);

                updateWeatherUI(weatherMain, maxTempC, minTempC, weatherMain, feelsLike, humidity,
                        dewPoint, visibility, pressure, windDeg, windSpeed, sunrise, sunset);

                processHourlyWeatherData(hourlyWeatherDataList);
                processDailyWeatherData(weatherDataList);
            }

            @Override
            public void onError(String error) {
                showToast("Weather API Error: " + error);
            }
        });
    }

    private void updateWeatherUI(String weatherMain, double maxTempC, double minTempC,
                                 String weatherMainDesc, double feelsLike, int humidity,
                                 double dewPoint, double visibility, double pressure, int windDeg,
                                 double windSpeed, String sunrise, String sunset) {
        new Handler(Looper.getMainLooper()).post(() -> {
            tMinMaxTextView.setText(String.format(Locale.US, "H:%.1f° L:%.1f° %s", maxTempC, minTempC, weatherMainDesc));
            FeelsLikeTempTextView.setText(feelsLike + "°C");
            HumidityTextView.setText(humidity + "%");
            DewPointTextView.setText(String.format(Locale.US, "The dew point is %.1f°C", dewPoint));
            VisibilityTextView.setText(visibility + "KM");
            PressureTextTextView.setText(pressure + "\n   hPa");
            WindDegTextView.setText(windDeg + "°");
            WindSpeedTextView.setText(windSpeed + " m/s");
            SunriseTextView.setText(sunrise);
            SunSetTextView.setText(sunset);
        });
    }

    private void processHourlyWeatherData(ArrayList<ArrayList<String>> hourlyWeatherDataList) {
        List<HourlyWeather> hourlyWeatherList = new ArrayList<>();
        for (ArrayList<String> hourData : hourlyWeatherDataList) {
            if (hourData.size() >= 3) {
                String hourtime = hourData.get(0);
                double hourtempC = Double.parseDouble(hourData.get(1));
                int hourheatindex = Integer.parseInt(hourData.get(2));
                int isDay = Integer.parseInt(hourData.get(3));
                String iconPath = classifyHeatIndex(hourheatindex);
                if (isDay == 1){
                    hourlyWeatherList.add(new HourlyWeather(hourtime, hourtempC, iconPath));
                }

            }
        }

        // Save the hourly weather data to preferences
        saveHourlyWeatherDataToPreferences(hourlyWeatherList);

        HourlyWeatherAdapter hourlyWeatherAdapter = new HourlyWeatherAdapter(hourlyWeatherList);
        hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);
    }

    private void processDailyWeatherData(ArrayList<ArrayList<String>> weatherDataList) {
        List<WeatherItem> weatherItems = new ArrayList<>();
        for (ArrayList<String> dayData : weatherDataList) {
            String date = dayData.get(0);
            String avgTemp = dayData.get(1);
            double minTemp = Double.parseDouble(dayData.get(2));
            double maxTemp = Double.parseDouble(dayData.get(3));
            int forecastavgHumidity = Integer.parseInt(dayData.get(4));
            double heatIndex = calculateHeatIndexFahrenheit(Double.parseDouble(avgTemp), forecastavgHumidity);
            String iconPath = classifyHeatIndexFahrenheit(heatIndex);
            weatherItems.add(new WeatherItem(date, maxTemp, minTemp, forecastavgHumidity, iconPath));
        }

        // Save the daily weather data to preferences
        saveDailyWeatherDataToPreferences(weatherItems);


        WeatherAdapter weatherAdapter = new WeatherAdapter(weatherItems);
        recyclerView.setAdapter(weatherAdapter);
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }

    private String classifyHeatIndex(double heatIndexCelsius) {
        if (heatIndexCelsius <= 25.0) {
            return "safe";
        } else if (heatIndexCelsius > 25.0 && heatIndexCelsius <= 30.0) {
            return "caution";
        } else if (heatIndexCelsius > 30.0 && heatIndexCelsius <= 35.0) {
            return "ext_caution";
        } else if (heatIndexCelsius > 35.0 && heatIndexCelsius <= 40.0) {
            return "danger";
        } else {
            return "extreme_danger";
        }
    }


    private String classifyHeatIndexFahrenheit(double heatIndexFahrenheit) {
        if (heatIndexFahrenheit <= 77.0) {
            return "safe";
        } else if (heatIndexFahrenheit > 77.0 && heatIndexFahrenheit <= 86.0) {
            return "caution";
        } else if (heatIndexFahrenheit > 86.0 && heatIndexFahrenheit <= 95.0) {
            return "ext_caution";
        } else if (heatIndexFahrenheit > 95.0 && heatIndexFahrenheit <= 104.0) {
            return "danger";
        } else {
            return "extreme_danger";
        }
    }



    private double calculateHeatIndexFahrenheit(double temperatureFahrenheit, double humidity) {
        return -42.379 + 2.04901523 * temperatureFahrenheit + 10.14333127 * humidity -
                0.22475541 * temperatureFahrenheit * humidity - 6.83783e-03 * Math.pow(temperatureFahrenheit, 2) -
                5.481717e-02 * Math.pow(humidity, 2) + 1.22874e-03 * Math.pow(temperatureFahrenheit, 2) * humidity +
                8.5282e-04 * temperatureFahrenheit * Math.pow(humidity, 2) - 1.99e-06 * Math.pow(temperatureFahrenheit, 2) * Math.pow(humidity, 2);
    }

    private void saveWeatherDataToPreferences(String cityName, String weatherMain, double maxTempC, double minTempC,
                                              double feelsLike, int humidity, double dewPoint, double visibility,
                                              double pressure, int windDeg, double windSpeed, String sunrise, String sunset) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("cityName", cityName);
        editor.putString("weatherMain", weatherMain);
        editor.putFloat("maxTempC", (float) maxTempC);
        editor.putFloat("minTempC", (float) minTempC);
        editor.putFloat("feelsLike", (float) feelsLike);
        editor.putInt("humidity", humidity);
        editor.putFloat("dewPoint", (float) dewPoint);
        editor.putFloat("visibility", (float) visibility);
        editor.putFloat("pressure", (float) pressure);
        editor.putInt("windDeg", windDeg);
        editor.putFloat("windSpeed", (float) windSpeed);
        editor.putString("sunrise", sunrise);
        editor.putString("sunset", sunset);
        editor.apply();  // Save asynchronously
    }

    private void saveHourlyWeatherDataToPreferences(List<HourlyWeather> hourlyWeatherList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear the previous hourly data
        editor.putInt("hourlyWeatherSize", hourlyWeatherList.size());

        for (int i = 0; i < hourlyWeatherList.size(); i++) {
            HourlyWeather hourData = hourlyWeatherList.get(i);
            editor.putString("hour_" + i + "_time", hourData.getTime());
            editor.putFloat("hour_" + i + "_temp", (float) hourData.getTempC());
            editor.putString("hour_" + i + "_icon", hourData.getIconPath());
        }

        editor.apply();  // Save asynchronously
    }

    private void saveDailyWeatherDataToPreferences(List<WeatherItem> weatherItems) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear the previous daily data
        editor.putInt("dailyWeatherSize", weatherItems.size());

        for (int i = 0; i < weatherItems.size(); i++) {
            WeatherItem dayData = weatherItems.get(i);
            editor.putString("day_" + i + "_date", dayData.getDate());
            editor.putFloat("day_" + i + "_maxTemp", (float) dayData.getTempMax());
            editor.putFloat("day_" + i + "_minTemp", (float) dayData.getTempMin());
            editor.putString("day_" + i + "_icon", dayData.getIcon());
        }

        editor.apply();  // Save asynchronously
    }



    public void loadLastSavedWeatherData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);

        String cityName = sharedPreferences.getString("cityName", "Unknown City");
        String weatherMain = sharedPreferences.getString("weatherMain", "N/A");
        int maxTempC = (int) sharedPreferences.getFloat("maxTempC", 0);
        int minTempC = (int) (sharedPreferences.getFloat("minTempC", 0));
        int feelsLike = (int) (sharedPreferences.getFloat("feelsLike", 0));
        int humidity = sharedPreferences.getInt("humidity", 0);
        int dewPoint = (int) (sharedPreferences.getFloat("dewPoint", 0));
        int visibility = (int) (sharedPreferences.getFloat("visibility", 0));
        int pressure = (int) (sharedPreferences.getFloat("pressure", 0));
        int windDeg = sharedPreferences.getInt("windDeg", 0);
        int windSpeed = (int) (sharedPreferences.getFloat("windSpeed", 0));
        String sunrise = sharedPreferences.getString("sunrise", "N/A");
        String sunset = sharedPreferences.getString("sunset", "N/A");

        // Update the UI with saved data
        updateWeatherUI(weatherMain, maxTempC, minTempC, weatherMain, feelsLike, humidity,
                dewPoint, visibility, pressure, windDeg, windSpeed, sunrise, sunset);
    }

    public void loadHourlyWeatherData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);
        int size = sharedPreferences.getInt("hourlyWeatherSize", 0);
        List<HourlyWeather> hourlyWeatherList = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String time = sharedPreferences.getString("hour_" + i + "_time", "");
            int temp = (int) (sharedPreferences.getFloat("hour_" + i + "_temp", 0));
            String icon = sharedPreferences.getString("hour_" + i + "_icon", "");

            hourlyWeatherList.add(new HourlyWeather(time, temp, icon));
        }

        // Now, update the RecyclerView with the loaded data
        HourlyWeatherAdapter hourlyWeatherAdapter = new HourlyWeatherAdapter(hourlyWeatherList);
        hourlyWeatherRecyclerView.setAdapter(hourlyWeatherAdapter);
    }

    public void loadDailyWeatherData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("WeatherPrefs", Context.MODE_PRIVATE);
        int size = sharedPreferences.getInt("dailyWeatherSize", 0);
        List<WeatherItem> weatherItems = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String date = sharedPreferences.getString("day_" + i + "_date", "");
            int maxTemp = (int) (sharedPreferences.getFloat("day_" + i + "_maxTemp", 0));
            int minTemp = (int) (sharedPreferences.getFloat("day_" + i + "_minTemp", 0));
            int avgHumidity = sharedPreferences.getInt("day_" + i + "_avgHumidity", 0);
            String icon = sharedPreferences.getString("day_" + i + "_icon", "");

            weatherItems.add(new WeatherItem(date, maxTemp, minTemp, avgHumidity, icon));
        }

        // Now, update the RecyclerView with the loaded data
        WeatherAdapter weatherAdapter = new WeatherAdapter(weatherItems);
        recyclerView.setAdapter(weatherAdapter);
    }





}
