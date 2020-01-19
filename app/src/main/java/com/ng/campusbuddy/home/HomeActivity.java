
package com.ng.campusbuddy.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.auth.SetUpProfileActivity;
import com.ng.campusbuddy.education.EducationActivity;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.adapter.SliderAdapterADs;
import com.ng.campusbuddy.start.WelcomeActivity;
import com.ng.campusbuddy.tools.NotificationsActivity;
import com.ng.campusbuddy.tools.SettingsActivity;
import com.ng.campusbuddy.utils.SharedPref;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeActivity extends AppCompatActivity {
    Context mcontext = HomeActivity.this;

    String profileid;

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference user_Ref;



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
        setContentView(R.layout.activity_home);

        AdMod();

        mAuth = FirebaseAuth.getInstance();
        profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_Ref = FirebaseDatabase.getInstance().getReference().child("Users");

        SetupNavigationDrawer();
        ADimageslider();


        LoadImage();

    }


    @Override
    protected void onStart() {
        super.onStart();


        firebaseUser = mAuth.getCurrentUser();

        //check if user is null
        if (firebaseUser == null){
            startActivity(new Intent(mcontext, WelcomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
//        else {
//            final String current_uid = mAuth.getCurrentUser().getUid();
//
//            user_Ref.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(!dataSnapshot.hasChild(current_uid)){
//                        startActivity(new Intent(mcontext, SetUpProfileActivity.class)
//                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                        finish();
//                    }
//                    else {
//                        Toast.makeText(mcontext, "Welcome to Campus Buddy", Toast.LENGTH_SHORT).show();
//                    }
//                }

//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        }
    }

    private void AdMod() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
    }

    private void ADimageslider() {

        SliderView sliderView = findViewById(R.id.ADsSlider);
        SliderAdapterADs adapter = new SliderAdapterADs(this);
        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.RED);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    private void LoadImage() {

        final CircleImageView Profile_image = findViewById(R.id.image_profile);
        //        Loading profile image
        String profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String profile_image = dataSnapshot.child("imageurl").getValue().toString();

                    Glide.with(getApplicationContext())
                            .load(profile_image)
                            .into(Profile_image);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SetupNavigationDrawer() {

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview=navigationView.getHeaderView(0);
        RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);

        // name, prfoile status
        final TextView Username = headerview.findViewById(R.id.nav_username);
        final TextView Profile_status = headerview.findViewById(R.id.nav_status);
        final CircleImageView Profile_image = headerview.findViewById(R.id.image_profile);
        final TextView Followers = headerview.findViewById(R.id.followers);
        final TextView Following = headerview.findViewById(R.id.following);

        //        Loading profile image
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String profile_image = dataSnapshot.child("imageurl").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String profile_status = dataSnapshot.child("profile_status").getValue().toString();

                    Glide.with(getApplicationContext())
                            .load(profile_image)
                            .into(Profile_image);

//                    Picasso.get(HomeActivity.this)
//                            .load(profile_image)
//                            .centerCrop()
//                            .into(Profile_image);


                    Username.setText(username);
                    Profile_status.setText(profile_status);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference Followers_reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        Followers_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(mcontext, ProfileActivity.class);
                startActivity(profile);
                Animatoo.animateSplit(mcontext);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        Toast.makeText(mcontext, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_education:
                        Intent education = new Intent(mcontext, EducationActivity.class);
                        startActivity(education);
                        Animatoo.animateSlideLeft(mcontext);
                        finish();
                        break;
                    case R.id.nav_social:
                        Intent social = new Intent(mcontext, SocialActivity.class);
                        startActivity(social);
                        Animatoo.animateSlideLeft(mcontext);
                        finish();
                        break;
                    case R.id.nav_notifications:
                        startActivity(new Intent(mcontext, NotificationsActivity.class));
                        Animatoo.animateSlideLeft(mcontext);
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(mcontext, SettingsActivity.class));
                        Animatoo .animateSlideLeft(mcontext);
                        break;
                    case R.id.nav_log_out:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        startActivity(new Intent(mcontext, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        Animatoo.animateShrink(mcontext);
                        break;
                }

                return false;
            }
        });
    }

}
