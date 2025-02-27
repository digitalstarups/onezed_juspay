package com.onezed.GlobalVariable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.onezed.Model.UserProfileModel;
import com.onezed.R;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "my_channel_id";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.onezed_app_logo) // Set your notification icon
                .setContentTitle("OneZed")
                .setContentText(GlobalVariable.LocalNotificationText+" Done. Thank you for using OneZed "+ UserProfileModel.getInstance().getName())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.v("notification","notification");
            //notificationManager.notify(1, builder.build());
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
