package com.ng.campusbuddy.utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();
    public static final String CHANNEL_MATCH_ID = "match";
    public static final String CHANNEL_POST_ID = "post";
    public static final String CHANNEL_FOLLOW_ID = "match";

    DatabaseReference PrescenseRef;
    FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mAuth = FirebaseAuth.getInstance();
        try{
            PrescenseRef = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(mAuth.getCurrentUser().getUid());

            PrescenseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null){
                        String timestamp = String.valueOf(System.currentTimeMillis());
                        PrescenseRef.child("online_status").onDisconnect().setValue(timestamp);

                        PrescenseRef.child("online_status").setValue("online");

                        //tokens
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference()
                                .child("Tokens");
                        Token mtoken = new Token(deviceToken);
                        tokenRef.child(mAuth.getCurrentUser().getUid()).setValue(mtoken);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, "Not Login Yet", Toast.LENGTH_SHORT).show();
        }


        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, true);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.2");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://campusbuddy.com.ng");

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(30) // fetch every half minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });


        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel match = new NotificationChannel(
                    CHANNEL_MATCH_ID,
                    "Match Up",
                    NotificationManager.IMPORTANCE_HIGH
            );
            match.setDescription("matching channel");
            match.enableLights(true);
            match.enableVibration(true);
            match.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            NotificationChannel follow = new NotificationChannel(
                    CHANNEL_FOLLOW_ID,
                    "Following",
                    NotificationManager.IMPORTANCE_HIGH
            );
            follow.setDescription("following channel");
            follow.setShowBadge(true);

            NotificationChannel post = new NotificationChannel(
                    CHANNEL_POST_ID,
                    "Like and Comment",
                    NotificationManager.IMPORTANCE_HIGH
            );
            post.setDescription("Like and Comment channel");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(match);
            manager.createNotificationChannel(follow);
            manager.createNotificationChannel(post);
        }


    }
}
