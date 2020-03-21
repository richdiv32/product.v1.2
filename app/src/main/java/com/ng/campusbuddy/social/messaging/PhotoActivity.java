package com.ng.campusbuddy.social.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ng.campusbuddy.R;

public class PhotoActivity extends AppCompatActivity {

    Intent intent;

    ImageView image;
   String imageurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        intent = getIntent();
        imageurl = intent.getStringExtra("imageurl");

        image = findViewById(R.id.imageView);

        Glide.with(PhotoActivity.this)
                .load(imageurl)
                .placeholder(R.drawable.placeholder)
                .into(image);

    }
}
