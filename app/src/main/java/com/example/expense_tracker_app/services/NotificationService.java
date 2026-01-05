package com.example.expense_tracker_app.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.expense_tracker_app.MainActivity;
import com.example.expense_tracker_app.R;
import com.example.expense_tracker_app.ExpenseTrackerApplication;

public class NotificationService {

    private static final int BUDGET_WARNING_NOTIFICATION_ID = 1001;

    /**
     * Show budget warning notification when expense exceeds limit
     */
    public static void showBudgetWarning(Context context, String remark, double amount) {
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

        // Build notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                ExpenseTrackerApplication.BUDGET_WARNING_CHANNEL_ID
        )
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Warning icon
                .setContentTitle("Budget Warning!")
                .setContentText("You have exceeded your budget for \"" + remark + "\"")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You have exceeded your budget for \"" + remark + "\"\nAmount: $" + amount))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 200, 500});

        // Show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // Check for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(BUDGET_WARNING_NOTIFICATION_ID, builder.build());
            }
        } else {
            notificationManager.notify(BUDGET_WARNING_NOTIFICATION_ID, builder.build());
        }
    }

    /**
     * Check if expense exceeds budget limit
     */
    public static boolean isBudgetExceeded(double amount) {
        // Check if expense is greater than 100 USD or 400,000 KHR
        return amount > 100 || amount > 400000;
    }
}