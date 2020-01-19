package com.ng.campusbuddy.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ng.campusbuddy.R;
import com.ng.campusbuddy.utils.SharedPref;

public class SettingsActivity extends AppCompatActivity {
    Switch Mode_switch;

    SharedPref sharedPref;
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

        SetUpDarkMode();

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
                    sharedPref.setNightModeState(true);
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    finish();
                }
                else {
                    sharedPref.setNightModeState(false);
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }
        });
    }
}
