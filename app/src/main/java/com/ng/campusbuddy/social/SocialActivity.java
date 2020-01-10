package com.ng.campusbuddy.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.ng.campusbuddy.education.EducationActivity;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import eu.long1.spacetablayout.SpaceTabLayout;

public class SocialActivity extends AppCompatActivity {
    Context mcontext = SocialActivity.this;

    SpaceTabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        SetupNavigationDrawer();

        /*Bottom Navigation*/
        //add the fragments you want to display in a List
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new FeedsFragment());
        fragmentList.add(new MessagesFragment());
        fragmentList.add(new MatchUpFragment());
        fragmentList.add(new ChatRoomFragment());
        fragmentList.add(new FindFriendFragment());


        ViewPager viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.spaceTabLayout);

//        tabLayout.setTabFiveIcon(R.drawable.ic_search);
//        tabLayout.setTabFourIcon(R.drawable.ic_chat_room);

        //we need the savedInstanceState to get the position
        tabLayout.initialize(viewPager, getSupportFragmentManager(),
                fragmentList, savedInstanceState);

        /*---------------------------------------------*/
    }


    private void SetupNavigationDrawer() {

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview=navigationView.getHeaderView(0);
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
                        Intent education = new Intent(mcontext, EducationActivity.class);
                        startActivity(education);
                        finish();
                        break;
                    case R.id.nav_social:
                        Toast.makeText(mcontext, "Social", Toast.LENGTH_SHORT).show();
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
