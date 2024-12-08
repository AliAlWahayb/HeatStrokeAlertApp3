package com.example.heatstrokealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay transition to MainActivity for 5 seconds while MainActivity runs in the background
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start MainActivity after 5 seconds
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        }, 3000);
    }
}
