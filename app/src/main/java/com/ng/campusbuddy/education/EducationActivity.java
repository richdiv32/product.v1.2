package com.ng.campusbuddy.education;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.MatchUpFragment;
import com.ng.campusbuddy.social.SocialActivity;

public class EducationActivity extends AppCompatActivity {
    Context mcontext = EducationActivity.this;

    SpaceNavigationView spaceNavigationView;

    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
//        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_nav_draw);
//        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
//                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                }
//                else {
//                    drawerLayout.openDrawer(GravityCompat.START);
//                }
//            }
//        });


        SetupNavigationDrawer();
//        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);


        /*Bottom Navigation*/
        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_timetable));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_myshelf));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_bookmark));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_qa));


        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(mcontext, "Browse", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(EducationActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(EducationActivity.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });
      /*---------------------------------------------*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    private void SetupNavigationDrawer() {

        NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview = navigationView.getHeaderView(0);
        RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);

        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profile = new Intent(mcontext, ProfileActivity.class);
                startActivity(profile);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.nav_home:
                        startActivity(new Intent(mcontext, HomeActivity.class));
                        finish();
                        break;
                    case R.id.nav_education:
                        Toast.makeText(mcontext, "Education", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_social:
                        Intent social = new Intent(mcontext, SocialActivity.class);
                        startActivity(social);
                        finish();
                        break;
                    case R.id.nav_notifications:
                        Toast.makeText(mcontext, "Notifications", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_settings:
                        Toast.makeText(mcontext, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_log_out:
                        Toast.makeText(mcontext, "Log Out", Toast.LENGTH_SHORT).show();
                        break;
                }

                return false;
            }
        });
    }

}
