package com.onezed.GlobalVariable;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onezed.Activity.MainActivity;
import com.onezed.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FirebaseMessageReceiver
        extends FirebaseMessagingService {

    // Override onNewToken to get new token
    @Override
    public void onNewToken(@NonNull String token)
    {
        Log.v("FirebaseToken",token);
    }

    // body from the message passed in FCM
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String imageUrl = remoteMessage.getData().get("image_url");
        String webUrl = remoteMessage.getData().get("web_url");
        String activityRedirect = remoteMessage.getData().get("activity_redirect");

        String title = remoteMessage.getNotification().getTitle();
        String message = remoteMessage.getNotification().getBody();

        Log.v("Firebase",title+" "+message+" "+imageUrl+" "+webUrl+" "+activityRedirect);

        showNotification(title, message, imageUrl, webUrl, activityRedirect);
        // Play custom audio for 3 seconds
       // playCustomSound();
    }

    public void showNotification(String title, String message, String imageUrl, String webUrl, String activityRedirect) {
        Log.v("Notification",title+" "+message+" "+imageUrl+" "+webUrl+" "+activityRedirect);
        Intent intent;

        if (webUrl != null && !webUrl.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
            Log.v("Notification","urlRedirect");
        } else if (activityRedirect != null && !activityRedirect.isEmpty()) {
            try {
                Log.v("Notification","ActivityRedirect");
                Class<?> targetActivity = Class.forName("com.onezed.Activity." + activityRedirect);
                intent = new Intent(this, targetActivity);
            } catch (ClassNotFoundException e) {
                Log.v("Notification","ActivityRedirect Failed");
                intent = new Intent(this, MainActivity.class);
            }
        } else {
            Log.v("Notification","Not Url Not Activity");
            intent = new Intent(this, MainActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = "notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);

        // Detect Dark Mode
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            // Dark Mode: Set text color to white
            remoteViews.setTextColor(R.id.title, getResources().getColor(android.R.color.white));
            remoteViews.setTextColor(R.id.message, getResources().getColor(android.R.color.white));
        } else {
            // Light Mode: Set text color to black
            remoteViews.setTextColor(R.id.title, getResources().getColor(android.R.color.black));
            remoteViews.setTextColor(R.id.message, getResources().getColor(android.R.color.black));
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.onezed_app_logo)
                .setAutoCancel(true)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();

        if (imageUrl != null && !imageUrl.isEmpty()) {
            NotificationTarget notificationTarget = new NotificationTarget(
                    this, R.id.icon, remoteViews, notification, 0);

            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(notificationTarget);
        }

        notificationManager.notify(0, notification);
    }
    // Helper method to download the image
    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//    private void playCustomSound() {
//        // Reference the audio file in the raw folder
//        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.custom_bell_notification);
//
//        if (mediaPlayer != null) {
//            mediaPlayer.start(); // Play the sound
//
//            // Stop the sound after 3 seconds
//            new Handler().postDelayed(() -> {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.release();
//                }
//            }, 3000);
//        } else {
//            Log.e("AudioError", "Audio file not found in raw directory.");
//        }
//    }
}
