package com.ng.campusbuddy.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.FollowersActivity;
import com.ng.campusbuddy.profile.UserProfileActivity;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.messaging.chat.ChatActivity;
import com.ng.campusbuddy.social.post.PostDetailActivity;

import java.util.Random;

import static com.ng.campusbuddy.utils.App.CHANNEL_FOLLOW_ID;
import static com.ng.campusbuddy.utils.App.CHANNEL_MATCH_ID;

public class FirebaseMessaging extends FirebaseMessagingService {


    private static final String ADMIN_CHANNEL_ID = "admin_channel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);



        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        /*Here we identify that there are two types of notifications
        *       >notificationType = "PostNotification"
        *       >notificationType = "ChatNotification"
        *       >notificationType = "MatchNotification"
        *       >notificationType = "FollowNotification"*/

        String notificationType = remoteMessage.getData().get("notificationType");
        if (notificationType.equals("ChatNotification")){
            //chat notifications
            String sent = remoteMessage.getData().get("sent");//my ID
            String user = remoteMessage.getData().get("user");//user ID


            if (firebaseUser != null && sent.equals(firebaseUser.getUid())){
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNotification(remoteMessage);
                    } else {
                        sendNotification(remoteMessage);
                    }
                }
            }
        }
        else if (notificationType.equals("PostNotification")){
            //post notifications
            String sender = remoteMessage.getData().get("sender");
            String pId = remoteMessage.getData().get("postID");
            String title = remoteMessage.getData().get("postTitle");
            String description = remoteMessage.getData().get("postDescription");

            //if user is same that has posted don't show notificaiton
            if (!sender.equals(currentUser)){
                showPostNotification(""+pId, ""+title, ""+description);
            }
        }
        else if (notificationType.equals("MatchNotification")){
            //match notifications
            String sent = remoteMessage.getData().get("sent");//user ID
            String user = remoteMessage.getData().get("user");//my ID


            if (firebaseUser != null && sent.equals(firebaseUser.getUid())){
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        sendOreoNotification(remoteMessage);
                    } else {
                        sendNotification(remoteMessage);
                    }
                }
            }
        }
        else if (notificationType.equals("FollowNotification")){

            //chat notifications
            String sent = remoteMessage.getData().get("sent");// my ID
            String user = remoteMessage.getData().get("user");// user ID
            String title = remoteMessage.getData().get("title");
            String description = remoteMessage.getData().get("body");
            String image = remoteMessage.getData().get("imageURL");


            if (firebaseUser != null && sent.equals(firebaseUser.getUid())){
                if (!currentUser.equals(user)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        showFollowNotification(""+user, ""+title, ""+description, image);
                    } else {
                        sendNotification(remoteMessage);
                    }
                }
            }
        }


    }

    private void showPostNotification(String pId, String title, String description) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "New Notification";
            String channelDescription = "Device to device post notification";

            NotificationChannel adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
            adminChannel.setDescription(channelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(adminChannel);
            }
        }

        //show post detail activity using post id when notification clicked
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra("postid", pId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);

        //souond for notification
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ""+ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(description)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        //show notification
        notificationManager.notify(notificationID, builder.build());
    }

    private void showFollowNotification(String pId, String title, String description, String profile_image) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "Follow Notification";
            String channelDescription = "Device to device follow notification";

            NotificationChannel followChannel = new NotificationChannel(
                    ADMIN_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            followChannel.setDescription(channelDescription);
            followChannel.enableLights(true);
            followChannel.setLightColor(Color.RED);
            followChannel.enableVibration(true);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(followChannel);
            }
        }

        //show post detail activity using post id when notification clicked
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("profileid", pId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Init for the custom notification layout
        final RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.item_notification_bar);
        notificationLayout.setTextViewText(R.id.title, title);
        notificationLayout.setTextViewText(R.id.description, description);
        notificationLayout.setImageViewResource(R.id.imageView, R.drawable.logo);


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_FOLLOW_ID)
                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setLargeIcon(largeIcon)
                .setColor(Color.RED)
                .setContent(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);


        final Notification notification = builder.build();


        final NotificationTarget notificationTarget = new NotificationTarget(getApplicationContext(),
                R.id.image_profile,
                notificationLayout,notification,
                notificationID);

//        final NotificationTarget notificationTarget2 = new NotificationTarget(getApplicationContext(),
//                R.id.post_image,
//                notificationLayout,notification,
//                2);

        Glide.with(getApplicationContext())
                .asBitmap()
                .load(profile_image)
                .into(notificationTarget);




        //show notification
        notificationManager.notify(notificationID, notification);
    }

    private void showMatchNotification(String pId, String title, String description) {
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = new Random().nextInt(3000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "Follow Notification";
            String channelDescription = "Device to device follow notification";

            NotificationChannel followChannel = new NotificationChannel(
                    ADMIN_CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_HIGH);
            followChannel.setDescription(channelDescription);
            followChannel.enableLights(true);
            followChannel.setLightColor(Color.RED);
            followChannel.enableVibration(true);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(followChannel);
            }
        }

        //show post detail activity using post id when notification clicked
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("profileid", pId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Init for the custom notification layout
        final RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.item_notification_bar);
        notificationLayout.setTextViewText(R.id.title, title);
        notificationLayout.setTextViewText(R.id.description, description);
        notificationLayout.setImageViewResource(R.id.imageView, R.drawable.logo);


        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_FOLLOW_ID)
                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setLargeIcon(largeIcon)
                .setColor(Color.RED)
                .setContent(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);


        final Notification notification = builder.build();


        final NotificationTarget notificationTarget = new NotificationTarget(getApplicationContext(),
                R.id.image_profile,
                notificationLayout,notification,
                notificationID);

//        final NotificationTarget notificationTarget2 = new NotificationTarget(getApplicationContext(),
//                R.id.post_image,
//                notificationLayout,notification,
//                2);

        //        Loading profile image
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(pId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);


                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(user.getImageurl())
                            .into(notificationTarget);

//                    Glide.with(getApplicationContext())
//                            .asBitmap()
//                            .load(user.getImageurl())
//                            .into(notificationTarget2);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //show notification
        notificationManager.notify(notificationID, notification);
    }

    private void sendOreoNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String image = remoteMessage.getData().get("imageURL");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        final Notification.Builder builder = oreoNotification.getONotifications(
                title,
                body,
                pendingIntent,
                defaultSound,
                icon);
//        Glide.with(getApplicationContext())
//                .asBitmap()
//                .load(image)
//                .into(new SimpleTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        builder.setLargeIcon(resource);
//                    }
//                });

        int j = 0;
        if (i > 0){
            j = i;
        }

        oreoNotification.getManager().notify(j, builder.build());

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String image = remoteMessage.getData().get("imageURL");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));


        Intent intent = new Intent(this, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(image)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        builder.setLargeIcon(resource);
                    }
                });
        NotificationManager noti = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int j = 0;
        if (i > 0){
            j = i;
        }

        noti.notify(j, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token = new Token(tokenRefresh);
        ref.child(user.getUid()).setValue(token);
    }
}
