package com.example.heatstrokealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private ListView cityListView;
    private EditText searchEditText;
    private ArrayAdapter<String> cityAdapter;
    private ArrayList<String> currentCities;
    private GeonamesService geonamesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        cityListView = findViewById(R.id.city_list);
        searchEditText = findViewById(R.id.search_bar);

        currentCities = new ArrayList<>();
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentCities);
        cityListView.setAdapter(cityAdapter);

        // Set up Retrofit to call the Geonames API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://secure.geonames.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        geonamesService = retrofit.create(GeonamesService.class);

        // Adding a TextWatcher to handle real-time search
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String query = charSequence.toString();
                if (!query.isEmpty()) {
                    fetchCities(query);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Handle item click
        cityListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = currentCities.get(position);

            // Returning selected city to MainActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selected_city", selectedCity);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    // Fetch cities based on search query
    private void fetchCities(String query) {
        // Make the API call to fetch cities from GeoNames
        Call<GeonamesResponse> call = geonamesService.searchCities(query, 10, "alisaw11");

        call.enqueue(new Callback<GeonamesResponse>() {
            @Override
            public void onResponse(Call<GeonamesResponse> call, Response<GeonamesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GeonamesResponse.City> cities = response.body().getCities();
                    currentCities.clear();
                    for (GeonamesResponse.City city : cities) {
                        currentCities.add(city.getName());
                    }
                    cityAdapter.notifyDataSetChanged();
                } else {
                    Log.e("SearchActivity", "Response failed: " + response.code() + " - " + response.message());
                    Toast.makeText(SearchActivity.this, "No cities found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeonamesResponse> call, Throwable t) {
                Log.e("SearchActivity", "Error fetching cities", t);
                Toast.makeText(SearchActivity.this, "Failed to fetch cities", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
