package com.ng.campusbuddy.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.Query;
import com.ng.campusbuddy.model.AD;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.utils.SharedPref;


public class AdInfoActivity extends AppCompatActivity {


    Intent intent;
    String ad_id, context_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            //removes status bar with background
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_info);


        intent = getIntent();

        ad_id = intent.getStringExtra("Ad_id");
        context_name = intent.getStringExtra("context");





        final ImageView ADimage = findViewById(R.id.image);
        final TextView ADtitle = findViewById(R.id.title);
        final TextView ADdescription = findViewById(R.id.description);

        final DatabaseReference AD = FirebaseDatabase.getInstance().getReference().child("ADs")
                .child(context_name).child(ad_id);

        AD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AD ad = dataSnapshot.getValue(AD.class);

                Glide.with(AdInfoActivity.this)
                        .load(ad.getFull_image())
                        .placeholder(R.drawable.placeholder)
                        .into(ADimage);

                ADtitle.setText(ad.getTitle());
                ADdescription.setText(ad.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AdMod();

    }

    private void AdMod() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
    }

}
