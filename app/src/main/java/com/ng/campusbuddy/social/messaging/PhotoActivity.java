package com.ng.campusbuddy.social.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;
import com.ng.campusbuddy.R;


public class PhotoActivity extends AppCompatActivity {

    Intent intent;

    ZoomageView image;
   String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        intent = getIntent();
        imageurl = intent.getStringExtra("imageurl");

        image = findViewById(R.id.image);

        Glide.with(PhotoActivity.this)
                .load(imageurl)
                .thumbnail(0.1f)
                .into(image);

    }
}
