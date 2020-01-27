package com.ng.campusbuddy.education;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.start.WelcomeActivity;
import com.ng.campusbuddy.tools.NotificationsActivity;
import com.ng.campusbuddy.tools.SettingsActivity;
import com.ng.campusbuddy.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.long1.spacetablayout.SpaceTabLayout;

public class EducationActivity extends AppCompatActivity {
    Context mcontext = EducationActivity.this;

    SpaceNavigationView spaceNavigationView;
    Toolbar toolbar;

//    FirebaseUser firebaseUser;
    String profileid;


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
        setContentView(R.layout.activity_education);


        profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();



        SetupNavigationDrawer();

        /*Bottom Navigation*/
        //add the fragments you want to display in a List
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TimeTableFragment());
        fragmentList.add(new ShelfFragment());
        fragmentList.add(new BrowseFragment());
        fragmentList.add(new BookmarkFragment());
        fragmentList.add(new QuestionAnswerFragment());

        ViewPager viewPager = findViewById(R.id.viewPager);
        SpaceTabLayout tabLayout = findViewById(R.id.spaceTabLayout);

        tabLayout.initialize(viewPager, getSupportFragmentManager(),
                fragmentList, savedInstanceState);
        tabLayout.setTabOneIcon(R.drawable.ic_timetable);
        tabLayout.setTabTwoIcon(R.drawable.ic_shelf);
        tabLayout.setTabThreeIcon(R.drawable.ic_browse);
        tabLayout.setTabFourIcon(R.drawable.ic_bookmark);
        tabLayout.setTabFiveIcon(R.drawable.ic_qa_red);
        /*---------------------------------------------*/
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        spaceNavigationView.onSaveInstanceState(outState);
//    }


    /*Navigation Drawer*/
    private void SetupNavigationDrawer() {

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
//                drawerLayout, R.string.drawer_open, R.string.drawer_close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Objects.requireNonNull(getSupportActionBar()).setDefaultDisplayHomeAsUpEnabled(true);
//        }
        View headerview = navigationView.getHeaderView(0);
        RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);

        // name, prfoile status
        final TextView Username = headerview.findViewById(R.id.nav_username);
        final TextView Profile_status = headerview.findViewById(R.id.nav_status);
        final TextView Followers = headerview.findViewById(R.id.followers);
        final TextView Following = headerview.findViewById(R.id.following);
        final CircleImageView Profile_image = headerview.findViewById(R.id.image_profile);



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
                        startActivity(new Intent(mcontext, HomeActivity.class));
                        Animatoo.animateSlideLeft(mcontext);
                        finish();
                        break;
                    case R.id.nav_education:
                        Toast.makeText(mcontext, "Education", Toast.LENGTH_SHORT).show();
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
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();Animatoo.animateSlideLeft(mcontext);
                        mAuth.signOut();
                        startActivity(new Intent(mcontext, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        Animatoo.animateShrink(mcontext);
                        break;
                }

                return false;
            }
        });

//        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//        };

//         showing dot next to notifications label
//        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);

//        drawerLayout.setDrawerListener(actionBarDrawerToggle);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
    }



}
