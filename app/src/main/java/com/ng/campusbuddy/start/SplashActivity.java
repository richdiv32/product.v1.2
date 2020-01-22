package com.ng.campusbuddy.start;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.auth.LoginActivity;
import com.ng.campusbuddy.auth.SetUpProfileActivity;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.utils.SharedPref;

public class SplashActivity extends AppCompatActivity {

    TextView tv;
    ImageView iv;

    private static int SPLASH_SCREEN_TIME_OUT=1500;
//    After completion of 1500 ms (1.5s), the next activity will get started.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //This method is used so that your splash activity
        //can cover the entire screen.


        setContentView(R.layout.activity_splash);

        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.splash_anim);
        tv.startAnimation(myanim);
        iv.startAnimation(myanim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                Intent splash = new Intent(SplashActivity.this, IntroActivity.class);
                Intent splash = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(splash);
                Animatoo.animateZoom(SplashActivity.this);
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);
    }
}
