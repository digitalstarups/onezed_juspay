package com.onezed.GlobalVariable;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

//For Internal Notification
public class AlarmHelper {

    public static void scheduleNotification(Context context, long triggerAtMillis) {
        Intent intent = new Intent(context, NotificationReceiver.class);

        // Use FLAG_IMMUTABLE if you don't need to modify the PendingIntent
        int pendingIntentFlags = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                ? PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, pendingIntentFlags);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }
}
