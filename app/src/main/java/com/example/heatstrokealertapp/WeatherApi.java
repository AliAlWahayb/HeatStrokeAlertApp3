package com.example.heatstrokealertapp;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherApi {
    private static final String API_KEY = "d61006078fa84f45bf3224454240412";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/forecast.json";

    public static void getWeatherData(String cityName, final WeatherDataCallback callback) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String city = params[0];
                String urlString = BASE_URL + "?key=" + API_KEY + "&q=" + city + "&days=7&hourly=yes&aqi=no&alerts=no";

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    InputStreamReader reader = new InputStreamReader(urlConnection.getInputStream());
                    StringBuilder result = new StringBuilder();
                    int data = reader.read();
                    while (data != -1) {
                        result.append((char) data);
                        data = reader.read();
                    }
                    return result.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result != null) {
                    try {
                        JSONObject jsonResponse = new JSONObject(result);
                        JSONObject currentWeather = jsonResponse.getJSONObject("current");


                        // Extract the required data
                        String weatherMain = currentWeather.getJSONObject("condition").getString("text");
                        double temp = currentWeather.getDouble("temp_c");
                        double feelsLike = currentWeather.getDouble("feelslike_c");
                        int humidity = currentWeather.getInt("humidity");
                        double pressure = currentWeather.getDouble("pressure_mb");
                        double windSpeed = currentWeather.getDouble("wind_kph");
                        int windDeg = currentWeather.getInt("wind_degree");
                        double dewPoint = currentWeather.getDouble("dewpoint_c");
                        double visibility = currentWeather.getDouble("vis_km");
                        double Uv = currentWeather.getDouble("uv");

                        JSONObject forecast = jsonResponse.getJSONObject("forecast");
                        JSONArray forecastday = forecast.getJSONArray("forecastday");
                        JSONObject today = forecastday.getJSONObject(0).getJSONObject("astro");
                        String sunrise = today.getString("sunrise");
                        String sunset = today.getString("sunset");

                        JSONObject dayWeather = forecastday.getJSONObject(0).getJSONObject("day");
                        double minTempC = dayWeather.getDouble("mintemp_c");
                        double maxTempC = dayWeather.getDouble("maxtemp_c");
                        double avgTempC = dayWeather.getDouble("avgtemp_c");
                        int avgHumidity = dayWeather.getInt("avghumidity");


                        // ArrayList to store the hourly weather data as ArrayList<String>
                        ArrayList<ArrayList<String>> hourlyWeatherDataList = new ArrayList<>();

                        // Navigate to the "hour" array of the first forecast day
                        JSONObject Hourforecast = jsonResponse.getJSONObject("forecast");
                        JSONArray Hourforecastday = Hourforecast.getJSONArray("forecastday");
                        JSONObject firstDay = Hourforecastday.getJSONObject(0); // Get the first day

                        JSONArray hours = firstDay.getJSONArray("hour");

                        for (int i = 0; i < hours.length(); i++) {
                            JSONObject hourData = hours.getJSONObject(i);

                            // Extract time, temperature, and humidity for each hour
                            String hourtime = hourData.getString("time");
                            // Define the original datetime format (24-hour format)
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

                            // Define the desired time format (12-hour format)
                            SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
                            // Parse the original time string into a Date object
                            Date date = inputFormat.parse(hourtime);

                            // Format the Date object into the desired 12-hour format
                            String formattedTime = outputFormat.format(date);
                            double hourtempC = hourData.getDouble("temp_c");
                            int hourheatindex = hourData.getInt("heatindex_c");

                            int isDay = hourData.getInt("is_day");

                            // Create a new ArrayList to hold the data for this hour
                            ArrayList<String> hourDataList = new ArrayList<>();

                            // Add the data (time, temperature, humidity) to the hourDataList
                            hourDataList.add(formattedTime);
                            hourDataList.add(String.valueOf(hourtempC)); // Convert double to String
                            hourDataList.add(String.valueOf(hourheatindex)); // Convert int to String
                            hourDataList.add(String.valueOf(isDay)); // Convert int to String

                            // Add the hour's data to the main hourlyWeatherDataList
                            hourlyWeatherDataList.add(hourDataList);
                        }

                        // ArrayList to store all the dates
                        ArrayList<ArrayList<String>> weatherDataList = new ArrayList<>();

                        // Iterate through each day in the forecastday array
                        for (int i = 0; i < forecastday.length(); i++) {
                            JSONObject day = forecastday.getJSONObject(i);

                            String forecastdate = day.getString("date");
                            double forecastavgTempf = day.getJSONObject("day").getDouble("mintemp_f");
                            double forecastminTempC = day.getJSONObject("day").getDouble("mintemp_c");
                            double forecastmaxTempC = day.getJSONObject("day").getDouble("maxtemp_c");
                            int forecastavgHumidity = day.getJSONObject("day").getInt("avghumidity");

                            // Create a new ArrayList for the current day's data
                            ArrayList<String> dayData = new ArrayList<>();
                            dayData.add(forecastdate);
                            dayData.add(String.valueOf(forecastavgTempf));
                            dayData.add(String.valueOf(forecastminTempC));
                            dayData.add(String.valueOf(forecastmaxTempC));
                            dayData.add(String.valueOf(forecastavgHumidity));

                            // Add the day's data to the main list
                            weatherDataList.add(dayData);
                        }


                        // Pass the extracted data to the callback
                        callback.onSuccess(hourlyWeatherDataList, weatherDataList, weatherMain, temp, feelsLike, dewPoint, pressure, humidity, visibility, Uv, windSpeed, windDeg, sunrise, sunset, minTempC, maxTempC, avgTempC, avgHumidity);

                    } catch (Exception e) {
                        callback.onError("Failed to parse data");
                    }
                } else {
                    callback.onError("Failed to fetch data");
                }
            }
        }.execute(cityName);
    }

    // Interface for callback
    public interface WeatherDataCallback {
        void onSuccess(ArrayList<ArrayList<String>> hourlyWeatherDataList, ArrayList<ArrayList<String>> weatherDataList, String weatherMain, double temp, double feelsLike, double dewPoint, double pressure, int humidity, double Uv, double visibility, double windSpeed, int windDeg, String sunrise, String sunset, double minTempC, double maxTempC , double avgTempC  , int avgHumidity);

        void onError(String error);
    }
}
