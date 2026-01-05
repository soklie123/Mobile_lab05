package com.example.expense_tracker_app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.expense_tracker_app.MainActivity;
import com.example.expense_tracker_app.R;

public class NotificationHelper {

    private static final String TAG = "NotificationHelper";
    private static final String BUDGET_WARNING_CHANNEL_ID = "budget_warning_channel";
    private static final String BUDGET_WARNING_CHANNEL_NAME = "Budget Warnings";
    private static final int BUDGET_WARNING_NOTIFICATION_ID = 1001;

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    /**
     * Create notification channel (required for Android 8.0+)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    BUDGET_WARNING_CHANNEL_ID,
                    BUDGET_WARNING_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications when you exceed budget limits");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }

    /**
     * Show budget warning notification
     * @param remark The expense remark/description
     * @param amount The expense amount
     */
    public void showBudgetWarningNotification(String remark, double amount) {
        Log.d(TAG, "showBudgetWarningNotification called with remark: " + remark + ", amount: " + amount);

        // Create intent to open app when notification is tapped
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                flags
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                BUDGET_WARNING_CHANNEL_ID
        )
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Warning icon
                .setContentTitle("Budget Warning!")
                .setContentText("You have exceeded your budget for \"" + remark + "\"")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You have exceeded your budget for \"" + remark + "\"\n\nAmount: $" + String.format("%.2f", amount)))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500})
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        try {
            notificationManager.notify(BUDGET_WARNING_NOTIFICATION_ID, builder.build());
            Log.d(TAG, "Notification displayed successfully");
        } catch (SecurityException e) {
            Log.e(TAG, "Permission denied for notifications", e);
        }
    }

    /**
     * Check if an expense amount exceeds the budget limit
     * @param amount The expense amount to check
     * @return true if budget is exceeded
     */
    public static boolean isBudgetExceeded(double amount) {
        // Check if expense is greater than 100 USD or 400,000 KHR
        return amount > 100 || amount > 400000;
    }
}