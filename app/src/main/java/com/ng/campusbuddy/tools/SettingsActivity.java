package com.ng.campusbuddy.tools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
