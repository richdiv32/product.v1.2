package com.ng.campusbuddy.tools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.utils.ForceUpdateChecker;
import com.ng.campusbuddy.utils.SharedPref;

import java.util.HashMap;
import java.util.Map;

import static com.ng.campusbuddy.utils.ForceUpdateChecker.KEY_CURRENT_VERSION;
import static com.ng.campusbuddy.utils.ForceUpdateChecker.KEY_UPDATE_REQUIRED;
import static com.ng.campusbuddy.utils.ForceUpdateChecker.KEY_UPDATE_URL;


public class SettingsActivity extends AppCompatActivity {

    Switch Mode_switch;
    Switch postSwitch;

    SharedPref sharedPref;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    static final String TOPIC_POST_NOTIFICATION = "POST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AdMod();
        SetUpDarkMode();
        PostNotification();
        Invite();
        Update_App();
        Themes();
        Rate_Us();

    }

    private void PostNotification() {
        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean(""+ TOPIC_POST_NOTIFICATION, false);

        postSwitch = findViewById(R.id.notification_switch);

        //if enabled check swicth, otherwise unchect switch - by default unchecked/false
        if (isPostEnabled){
            postSwitch.setChecked(true);
        }
        else {
            postSwitch.setChecked(false);
        }


        postSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //edit swity state
                editor = sp.edit();
                editor.putBoolean("" + TOPIC_POST_NOTIFICATION, isChecked);
                editor.apply();

                if (isChecked){

                    subscribePostNotificaiton();

                }
                else {

                    unSubcribePostNotification();

                }
            }
        });
    }

    private void unSubcribePostNotification() {
        //unsubscribe to POST to enable it's notifications
        FirebaseMessaging.getInstance().unsubscribeFromTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "You will not receive post notifications";
                        if (!task.isSuccessful()){
                            msg = "UnSubscription failed";

                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subscribePostNotificaiton() {
        //subscribe to POST to enable it's notifications
        FirebaseMessaging.getInstance().subscribeToTopic(""+TOPIC_POST_NOTIFICATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String msg = "You will receive post notifications";
                        if (!task.isSuccessful()){
                            msg = "Subscription failed";

                        }
                        Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AdMod() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
    }

    private void SetUpDarkMode() {

        Mode_switch = findViewById(R.id.mode_switch);
        if (sharedPref.loadNightModeState() == true){
            Mode_switch.setChecked(true);
        }
        Mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(SettingsActivity.this, "Dark Age", Toast.LENGTH_SHORT).show();
                    sharedPref.setNightModeState(true);
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                    Animatoo.animateFade(SettingsActivity.this);
                    finish();
                }
                else {
                    Toast.makeText(SettingsActivity.this, "Day Light", Toast.LENGTH_SHORT).show();
                    sharedPref.setNightModeState(false);
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    Animatoo.animateFade(SettingsActivity.this);
                    finish();
                }
            }
        });
    }

    private void Invite(){
        LinearLayout Invite = findViewById(R.id.set_invite);

        Invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = "Check out Campus Buddy App, its the best college student platform. " +
                        "Inquire, Learn, Connect and Grow your campus experience just like me." +
                        "Get it for free at https://campusbuddy.xyz/Download " +
                        "or on Play Store.";
                Intent sIntent = new Intent(Intent.ACTION_SEND);
                sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                sIntent.setType("text/plain");
                startActivity(Intent.createChooser(sIntent, "Invite a friend via..."));
            }
        });
    }

    private void Update_App(){


        LinearLayout Update = findViewById(R.id.set_update);

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Web = "https://campusbuddy.xyz/Download";
                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Web));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });


    }

    private void Themes(){
        LinearLayout Themes = findViewById(R.id.set_theme);
        Themes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Get awesome themes in the next app update.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void Rate_Us(){
        LinearLayout Rating = findViewById(R.id.set_rating);

        Rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                catch (ActivityNotFoundException e){
            String Web = "https://campusbuddy.xyz/Download";
            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Web));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
