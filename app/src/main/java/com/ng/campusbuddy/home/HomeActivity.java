
package com.ng.campusbuddy.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.ng.campusbuddy.education.EducationActivity;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.tools.SliderAdapterADs;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


public class HomeActivity extends AppCompatActivity {

    Context mcontext = HomeActivity.this;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SetupNavigationDrawer();
        ADimageslider();

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
                        Toast.makeText(mcontext, "Home", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_education:
                        Intent education = new Intent(mcontext, EducationActivity.class);
                        startActivity(education);
                        finish();
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
