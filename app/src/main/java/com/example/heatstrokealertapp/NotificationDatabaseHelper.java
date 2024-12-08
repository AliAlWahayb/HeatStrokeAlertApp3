package com.example.heatstrokealertapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class NotificationDatabaseHelper extends SQLiteOpenHelper {

    // Database configuration
    private static final String DATABASE_NAME = "notifications.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_ICON = "icon";

    // Constructor
    public NotificationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create notifications table
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_MESSAGE + " TEXT, "
                + COLUMN_ICON + " INTEGER)";
        db.execSQL(CREATE_TABLE);
        Log.d("NotificationDatabase", "Table created with query: " + CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade database
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        onCreate(db);
        Log.d("NotificationDatabase", "Database upgraded to version " + newVersion);
    }

    // Add a single notification
    public void addNotification(NotificationItem notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TIME, notification.getTime());
        values.put(COLUMN_MESSAGE, notification.getMessage());
        values.put(COLUMN_ICON, notification.getIconResId());

        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        if (result != -1) {
            Log.d("NotificationDatabase", "Notification added successfully: " + notification.getMessage());
        } else {
            Log.e("NotificationDatabase", "Failed to add notification: " + notification.getMessage());
        }
        db.close();
    }

    // Retrieve all notifications
    public List<NotificationItem> getAllNotifications() {
        List<NotificationItem> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NOTIFICATIONS,
                null, // Select all columns
                null, // No WHERE clause
                null, // No selection arguments
                null, // No groupBy
                null, // No having
                COLUMN_ID + " ASC" // Order by ID Asscending
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Fetch data from the cursor
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                int icon = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ICON));

                // Add the notification to the list
                notifications.add(new NotificationItem(time, message, icon));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            Log.w("NotificationDatabase", "No notifications found in the database.");
        }

        db.close();
        return notifications;
    }

    // Delete all notifications
    public void deleteAllNotifications() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NOTIFICATIONS, null, null);
        Log.d("NotificationDatabase", "Deleted " + rowsDeleted + " notifications.");
        db.close();
    }

    // Delete a single notification by ID
    public void deleteNotificationById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NOTIFICATIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        if (rowsDeleted > 0) {
            Log.d("NotificationDatabase", "Deleted notification with ID: " + id);
        } else {
            Log.w("NotificationDatabase", "No notification found with ID: " + id);
        }
        db.close();
    }

    // Count the number of notifications
    public int getNotificationCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        Log.d("NotificationDatabase", "Total notifications: " + count);
        return count;
    }
}