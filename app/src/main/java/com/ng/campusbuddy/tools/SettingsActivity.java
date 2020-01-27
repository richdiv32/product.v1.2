package com.ng.campusbuddy.tools;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.utils.SharedPref;


public class SettingsActivity extends AppCompatActivity {
    Switch Mode_switch;
    Switch postSwitch;

    SharedPref sharedPref;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    static final String POST_NOTIFICATION = "POST";
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        AdMod();
        SetUpDarkMode();
        PostNotification();

    }

    private void PostNotification() {
        sp = getSharedPreferences("Notification_SP", MODE_PRIVATE);
        boolean isPostEnabled = sp.getBoolean(""+ POST_NOTIFICATION, false);

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
                editor.putBoolean("" + POST_NOTIFICATION, isChecked);
                editor.apply();

                if (isChecked){

                    //subscribe to POST to enable it's notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(""+POST_NOTIFICATION)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    String msg = "You will receive post notifications";
                                    if (!task.isSuccessful()){
                                        msg = "Subcription failed";

                                    }
                                    Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else {

                    //unsubscribe to POST to enable it's notifications
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(""+POST_NOTIFICATION)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    String msg = "You will not receive post notifications";
                                    if (!task.isSuccessful()){
                                        msg = "UnSubcription failed";

                                    }
                                    Toast.makeText(SettingsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
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
                    Animatoo.animateFade(SettingsActivity.this);
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
}
