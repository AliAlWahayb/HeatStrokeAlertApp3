package com.example.heatstrokealertapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

public class PrecautionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_precaution);

        // Find the CloseBtn ImageButton by ID
        ImageButton closeBtn = findViewById(R.id.CloseBtn);

        // Set an OnClickListener for the CloseBtn
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
