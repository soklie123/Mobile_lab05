package com.example.expense_tracker_app;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class ExpenseTrackerApplication extends Application {

    public static final String BUDGET_WARNING_CHANNEL_ID = "budget_warning_channel";
    public static final String FCM_CHANNEL_ID = "fcm_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Budget Warning Channel
            NotificationChannel budgetChannel = new NotificationChannel(
                    BUDGET_WARNING_CHANNEL_ID,
                    getString(R.string.budget_warning_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            budgetChannel.setDescription(getString(R.string.budget_warning_channel_description));
            budgetChannel.enableVibration(true);

            // FCM Channel
            NotificationChannel fcmChannel = new NotificationChannel(
                    FCM_CHANNEL_ID,
                    getString(R.string.fcm_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            fcmChannel.setDescription(getString(R.string.fcm_channel_description));

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(budgetChannel);
                manager.createNotificationChannel(fcmChannel);
            }
        }
    }
}