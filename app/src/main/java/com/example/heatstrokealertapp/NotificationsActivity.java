package com.example.heatstrokealertapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";
    private NotificationDatabaseHelper databaseHelper;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);



        // Initialize the database helper
        databaseHelper = new NotificationDatabaseHelper(this);



        // Set up the RecyclerView
        recyclerView = findViewById(R.id.recycler_view_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        databaseHelper.deleteAllNotifications();
        databaseHelper.addNotification(new NotificationItem("10 minutes ago", "A sunny day in your location, consider wearing UV protection", R.drawable.caution));
        databaseHelper.addNotification(new NotificationItem("1 day ago", "A cloudy day will occur all day long, don't worry about the heat of the sun", R.drawable.safe));
        databaseHelper.addNotification(new NotificationItem("2 days ago", "Potential for rain today is 84%, don't forget your umbrella.", R.drawable.safe));

        // Fetch all notifications from the database
        List<NotificationItem> notifications = databaseHelper.getAllNotifications();

        // Set the adapter with the notifications data
        notificationAdapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(notificationAdapter);



        // Handle opening the notification activity
        findViewById(R.id.CloseBtn).setOnClickListener(v -> {
            finish();
        });
    }
}
