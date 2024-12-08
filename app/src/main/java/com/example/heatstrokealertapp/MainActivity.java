package com.example.heatstrokealertapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements LocationUtils.OnLocationRetrievedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_SEARCH = 1;

    private TextView cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView, HumidityTextView, VisibilityTextView,
            PressureTextTextView, SunriseTextView, SunSetTextView, WindDegTextView, WindSpeedTextView, UvIndexTextTextView, DewPointTextView;
    private RecyclerView recyclerView, hourlyWeatherRecyclerView;
    private DrawerLayout drawerLayout;
    private Button notificationsBtn, searchBtn, precautionBtn, explanationBtn;

    private WeatherUtils weatherUtils;
    private LocationUtils locationUtils;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        cityNameTextView = findViewById(R.id.CityName);
        tMinMaxTextView = findViewById(R.id.TMinMax);
        FeelsLikeTempTextView = findViewById(R.id.FeelsLikeTemp);
        HumidityTextView = findViewById(R.id.HumidityText);
        DewPointTextView = findViewById(R.id.DewPointText);
        VisibilityTextView = findViewById(R.id.VisibiltyText);
        PressureTextTextView = findViewById(R.id.PressureText);
        WindDegTextView = findViewById(R.id.WindDegText);
        WindSpeedTextView = findViewById(R.id.WindSpeedText);
        UvIndexTextTextView = findViewById(R.id.UvIndexText);
        SunriseTextView = findViewById(R.id.SunriseText);
        SunSetTextView = findViewById(R.id.SunSetText);

        // RecyclerView setup
        recyclerView = findViewById(R.id.recyclerViewDays);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        hourlyWeatherRecyclerView = findViewById(R.id.recyclerViewHours);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        hourlyWeatherRecyclerView.setLayoutManager(layoutManager);

        // Initialize LocationUtils
        locationUtils = new LocationUtils(this, this);
        locationUtils.checkLocationPermission();

        // Initialize WeatherUtils
        weatherUtils = new WeatherUtils(this, cityNameTextView, tMinMaxTextView, FeelsLikeTempTextView,
                HumidityTextView, DewPointTextView, VisibilityTextView, PressureTextTextView, WindDegTextView,
                WindSpeedTextView, UvIndexTextTextView, SunriseTextView, SunSetTextView, hourlyWeatherRecyclerView,
                recyclerView);

        // Load the last saved weather data
        weatherUtils.loadLastSavedWeatherData();
        weatherUtils.loadHourlyWeatherData();
        weatherUtils.loadDailyWeatherData();

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);

        // Menu Buttons
        notificationsBtn = findViewById(R.id.NotificationsBtn);
        searchBtn = findViewById(R.id.SearchBtn);
        precautionBtn = findViewById(R.id.PrecautionBtn);
        explanationBtn = findViewById(R.id.ExplanationBtn);

        // Open menu when button clicked
        LinearLayout openMenuButton = findViewById(R.id.MenuBtn);
        openMenuButton.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);  // Opens the menu from the right
        });

        // Handle Notifications Button click
        notificationsBtn.setOnClickListener(v -> {
            Intent NotificationsIntent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(NotificationsIntent);
        });

        // Handle Search Button click
        searchBtn.setOnClickListener(v -> {
            // Open the search activity
            Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
            startActivityForResult(searchIntent, REQUEST_CODE_SEARCH);  // Start SearchActivity for result
        });

        // Handle Precaution Button click
        precautionBtn.setOnClickListener(v -> {
            // Start PrecautionActivity
            Intent precautionIntent = new Intent(MainActivity.this, PrecautionActivity.class);
            startActivity(precautionIntent);
        });

        // Handle Explanation Button click
        explanationBtn.setOnClickListener(v -> {
            // Start ExplanationActivity
            Intent explanationIntent = new Intent(MainActivity.this, ExplanationActivity.class);
            startActivity(explanationIntent);
        });
    }

    @Override
    public void onBackPressed() {
        // Close the menu when back button is pressed if the menu is visible
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationRetrieved(double latitude, double longitude, String cityName) {
        // When location is retrieved, fetch weather data
        cityNameTextView.setText(cityName);
        weatherUtils.fetchWeatherData(cityName);
    }

    // Handle the result from SearchActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SEARCH && resultCode == RESULT_OK) {
            String selectedCity = data.getStringExtra("selected_city");
            if (selectedCity != null) {
                // Update the city name and fetch weather data
                cityNameTextView.setText(selectedCity);
                weatherUtils.fetchWeatherData(selectedCity);  // Fetch weather data for the selected city
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestLocationPermission() {
        new AlertDialog.Builder(this)
                .setMessage("We need location permission to show weather data for your area.")
                .setPositiveButton("Grant", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE))
                .setNegativeButton("Cancel", null)
                .show();
    }
}
