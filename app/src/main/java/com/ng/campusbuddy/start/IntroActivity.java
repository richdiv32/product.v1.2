package com.ng.campusbuddy.start;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.ng.campusbuddy.R;
import com.ng.campusbuddy.adapter.SliderAdapter;
import com.ng.campusbuddy.model.ScreenItem;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;


public class IntroActivity extends AppCompatActivity  {


    Button btnNext;
    int position = 0 ;
    Button btnGetStarted;
    Animation btnAnim ;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            //removes status bar with background
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        //This method is used so that your splash activity
//        //can cover the entire screen.


        // when this activity is about to be launch we need to check if its openened before or not
        if (restorePrefData()) {

            Intent welcome = new Intent(getApplicationContext(), WelcomeActivity.class );
            startActivity(welcome);
            finish();

        }
        setContentView(R.layout.activity_intro);



        // fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("CONNECT WITH STUDENTS","Stay in touch with friends and colleagues across campuses in the country. The fun never ends.",R.drawable.splash1));
        mList.add(new ScreenItem("MATCH UP WITH SOMEONE SPECIAL","Find your match on campus, say  hi to your crushes.",R.drawable.splash4));
        mList.add(new ScreenItem("POST YOUR FAVOURITE MOMENTS","Build your social life by posting and engaging in our social platform, acquire more followers and likes to your post.",R.drawable.splash2));
        mList.add(new ScreenItem("CHAT WITH NEW AND OLD FRIENDS","Send messages to friends through Direct Messaging, Groups and Chat Rooms.",R.drawable.splash3));

        final SliderView sliderView = findViewById(R.id.Intor_Slider);
        final SliderAdapter adapter = new SliderAdapter(this, mList);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setIndicatorSelectedColor(Color.RED);
        sliderView.setIndicatorUnselectedColor(Color.WHITE);

        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        // next button click Listner
        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position = sliderView.getCurrentPagePosition();
                if (position < mList.size()) {

                    position++;
                    sliderView.setCurrentPagePosition(position);

                }

                if (position == mList.size()-1) { // when we rech to the last screen

                    loaddLastScreen();

                }

            }
        });


        // Get Started button click listener
        btnGetStarted = findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open main activity
                Intent welcome = new Intent(getApplicationContext(), WelcomeActivity.class);
                startActivity(welcome);
                // also we need to save a boolean value to storage so next time when the user run the app
                // we could know that he is already checked the intro screen activity
                // i'm going to use shared preferences to that process
                savePrefsData();
                finish();

            }
        });

        // skip button click listener
        tvSkip = findViewById(R.id.tv_skip);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loaddLastScreen();
            }
        });
    }


    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;

    }

    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.apply();

    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {

//        btnNext.setVisibility(View.INVISIBLE);
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        // setup animation
        btnGetStarted.setAnimation(btnAnim);

    }

//    @AfterPermissionGranted(123)
//    private void Permissions() {
//        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
//        if (EasyPermissions.hasPermissions(this, perms)) {
//            // Already have permission, do the thing
//            // ...
//
//        } else {
//            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, "We need all permission",
//                    123, perms);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        // Forward results to EasyPermissions
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
//
//    @Override
//    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//
//    }
//
//    @Override
//    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
//        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
//        // This will display a dialog directing them to enable the permission in app settings.
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            // Do something after user returned from app settings screen, like showing a Toast.
////            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
////                    .show();
//        }
//    }
}
