package com.ng.campusbuddy.utils;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.ng.campusbuddy.R;

public class OreoNotification extends ContextWrapper {

    private static final String ID = "com.ng.campusbuddy";
    private static final String NAME = "CampusBuddy";

    public static final String CHANNEL_MATCH_ID = "match";
    public static final String CHANNEL_LIKE_ID = "like";
    public static final String CHANNEL_FOLLOW_ID = "follow";
    public static final String CHANNEL_COMMENT_ID = "comment";

    private NotificationManager notificationManager;

    public OreoNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {

        NotificationChannel channel = new NotificationChannel(ID, NAME,
                NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(channel);

        NotificationChannel match = new NotificationChannel(
                CHANNEL_MATCH_ID,
                "Matches",
                NotificationManager.IMPORTANCE_HIGH);
        match.enableLights(true);
        match.enableVibration(true);
        match.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationChannel follow = new NotificationChannel(
                CHANNEL_FOLLOW_ID,
                "Follows",
                NotificationManager.IMPORTANCE_HIGH
        );
        follow.enableLights(true);
        follow.enableVibration(true);
        follow.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationChannel like = new NotificationChannel(
                CHANNEL_LIKE_ID,
                "Like",
                NotificationManager.IMPORTANCE_HIGH
        );
        like.enableLights(true);
        like.enableVibration(true);
        like.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationChannel comment = new NotificationChannel(
                CHANNEL_COMMENT_ID,
                "Like",
                NotificationManager.IMPORTANCE_HIGH
        );
        comment.enableLights(true);
        comment.enableVibration(true);
        comment.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(match);
        manager.createNotificationChannel(follow);
        manager.createNotificationChannel(like);
    }

    public NotificationManager getManager(){
        if (notificationManager == null){
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return  notificationManager;
    }



    @TargetApi(Build.VERSION_CODES.O)
    public  Notification.Builder getONotifications(String title, final String body,
                                                   PendingIntent pendingIntent, Uri soundUri,
                                                   String icon){
        //Large Icon
//        Bitmap  = BitmapFactory.decodeResource(getResources(), R.drawable.logo);


        return new Notification.Builder(getApplicationContext(), ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setColor(Color.RED)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setAutoCancel(true);
    }
}
