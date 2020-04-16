package com.ng.campusbuddy.social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
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

import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.Notification;
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.fragments.ChatRoomFragment;
import com.ng.campusbuddy.social.fragments.FeedsFragment;
import com.ng.campusbuddy.social.fragments.FindFriendFragment;
import com.ng.campusbuddy.social.fragments.MatchUpFragment;
import com.ng.campusbuddy.social.fragments.MessagesFragment;
import com.ng.campusbuddy.social.messaging.chat.Chat;
import com.ng.campusbuddy.start.WelcomeActivity;
import com.ng.campusbuddy.tools.NotificationsActivity;
import com.ng.campusbuddy.tools.SettingsActivity;
import com.ng.campusbuddy.tools.WebViewActivity;
import com.ng.campusbuddy.utils.ForceUpdateChecker;
import com.ng.campusbuddy.utils.SharedPref;


import de.hdodenhof.circleimageview.CircleImageView;

public class SocialActivity extends AppCompatActivity implements
        ForceUpdateChecker.OnUpdateNeededListener{
    Context mcontext = SocialActivity.this;

    Fragment selectedfragment = null;

    String profileid;


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if (firebaseUser != null) {
            final String current_uid = firebaseUser.getUid();

            DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

            UserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(current_uid)) {
                        startActivity(new Intent(mcontext, SetUpProfileActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currentuser", firebaseUser.getUid());
            editor.apply();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        //targets the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, getString(R.string.App_ID));


        profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        SetupNavigationDrawer();



        /*Bottom Navigation*/
//        //add the fragments you want to display in a List
//        List<Fragment> fragmentList = new ArrayList<>();
//        fragmentList.add(new FeedsFragment());
//        fragmentList.add(new MessagesFragment());
//        fragmentList.add(new MatchUpFragment());
//        fragmentList.add(new ChatRoomFragment());
//        fragmentList.add(new FindFriendFragment());
//
//        ViewPager viewPager = findViewById(R.id.viewPager);
//        SpaceTabLayout tabLayout = findViewById(R.id.spaceTabLayout);
//
//        tabLayout.initialize(viewPager, getSupportFragmentManager(),
//                fragmentList, savedInstanceState);
//        tabLayout.setTabOneIcon(R.drawable.ic_feeds);
//        tabLayout.setTabTwoIcon(R.drawable.ic_chat);
//        tabLayout.setTabThreeIcon(R.drawable.ic_match_up);
//        tabLayout.setTabFourIcon(R.drawable.ic_chat_room);
//        tabLayout.setTabFiveIcon(R.drawable.ic_search);



        final BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bubbleNavigation);


//        bubbleNavigationLinearView.setBadgeValue(0, "10");
//        bubbleNavigationLinearView.setBadgeValue(1, "200");
//        bubbleNavigationLinearView.setBadgeValue(2, "6");

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new FeedsFragment()).commit();

        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position){
                    case 0:
                        selectedfragment= new FeedsFragment();
                        break;
                    case 1:
                        selectedfragment= new MessagesFragment();
                        break;
                    case 2:
                        selectedfragment= new MatchUpFragment();
                        break;
                    case 3:
                        selectedfragment= new ChatRoomFragment();
                        break;
                    case 4:
                        selectedfragment= new FindFriendFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedfragment).commit();
            }
        });


        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if (unread == 0){

                }
                else {
                    //TODO: Attend to this
//                    bubbleNavigationLinearView.setBadgeValue(1, String.valueOf(unread));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*---------------------------------------------*/

        TapTarget();

    }


    private void SetupNavigationDrawer() {

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        final NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview=navigationView.getHeaderView(0);
        RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);

        // name, prfoile status
        final TextView Username = headerview.findViewById(R.id.nav_username);
        final TextView Profile_status = headerview.findViewById(R.id.nav_status);
        final CircleImageView Profile_image = headerview.findViewById(R.id.image_profile);
        final ImageView Profile_image_bg = headerview.findViewById(R.id.image_profile_bg);
        final TextView Followers = headerview.findViewById(R.id.followers);
        final TextView Following = headerview.findViewById(R.id.following);

        //badge
        //for nav button
        final TextView counter = findViewById(R.id.counter);
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Notification notification = snapshot.getValue(Notification.class);
                    if (!notification.isIsseen()){
                        unread++;
                    }
                }
                if (unread == 0){
                    counter.setVisibility(View.GONE);
                    navigationView.getMenu().getItem(1).setActionView(R.layout.menu_dot_gone);
                }
                else if (unread > 99){
                    counter.setText("+99");
                    // showing dot next to notifications label
                    navigationView.getMenu().getItem(1).setActionView(R.layout.menu_dot);
                }
                else {
                    counter.setText(String.valueOf(unread));
                    // showing dot next to notifications label
                    navigationView.getMenu().getItem(1).setActionView(R.layout.menu_dot);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        ImageButton Nav = findViewById(R.id.nav_button);
        Nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START, true);
            }
        });

        //        Loading profile image
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);

                    Glide.with(getApplicationContext())
                            .load(user.getImageurl())
                            .into(Profile_image);

                    Glide.with(getApplicationContext())
                            .load(user.getImageurl())
                            .into(Profile_image_bg);
                    Username.setText(user.getUsername());
                    Profile_status.setText(user.getProfile_status());
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
//                    case R.id.nav_home:
////                        drawerLayout.closeDrawer(GravityCompat.START, true);
//                        startActivity(new Intent(mcontext, HomeActivity.class));
//                        Animatoo.animateSlideLeft(mcontext);
//                        finish();
//                        break;
//                    case R.id.nav_education:
//                        Intent education = new Intent(mcontext, EducationActivity.class);
//                        startActivity(education);
//                        Animatoo.animateSlideLeft(mcontext);
//                        finish();
//                        break;
                    case R.id.nav_social:
                        Toast.makeText(mcontext, "Social", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_notifications:
                        startActivity(new Intent(mcontext, NotificationsActivity.class));
                        Animatoo.animateSlideLeft(mcontext);
                        break;
                    case R.id.nav_settings:
                        startActivity(new Intent(mcontext, SettingsActivity.class));
                        Animatoo .animateSlideLeft(mcontext);
                        break;
                    case R.id.nav_about_us:
                        String url = "https://campusbuddy.xyz/company";
                        Intent intent = new Intent(SocialActivity.this, WebViewActivity.class);
                        intent.putExtra("Url", url);
                        startActivity(intent);
                        break;
                    case R.id.nav_faq:
                        Toast.makeText(mcontext, "FAQ in progress..", Toast.LENGTH_SHORT).show();
//                        String url2 = "https://campusbuddy.xyz/Team";
//                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
//                        startActivity(Intent.createChooser(intent2, "Browse with"));
                        break;
                    case R.id.nav_log_out:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        startActivity(new Intent(mcontext, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        Animatoo.animateShrink(mcontext);
                        break;
                    case R.id.nav_invite:
                        String shareBody = "Check out Campus Buddy App, its the best college student platform. " +
                                "Inquire, Learn, Connect and Grow your campus experience just like me." +
                                "Get it for free at https://campusbuddy.xyz/Download " +
                                "or on Play Store.";
                        Intent sIntent = new Intent(Intent.ACTION_SEND);
                        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
                        sIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sIntent, "Invite a friend via..."));
                        break;
                    case R.id.nav_blog:
                        String url2 = "https://www.campusbuddy.com.ng";
                        Intent intent2 = new Intent(SocialActivity.this, WebViewActivity.class);
                        intent2.putExtra("Url", url2);
                        startActivity(intent2);
                        Animatoo .animateSlideLeft(mcontext);
                        break;
                }

                return false;
            }
        });
    }

    static final int TIME_INTERVAL = 2000;
    long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()){
            super.onBackPressed();
            return;
        }
        else {
            Toast.makeText(mcontext, "Tap again to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onUpdateNeeded(final String updateUrl) {
//        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//        final  View popup_view = inflater.inflate(R.layout.dialog_update_app, null);
//        final PopupWindow popupWindow = new PopupWindow(popup_view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        popupWindow.setOutsideTouchable(false);
//        popupWindow.setFocusable(false);
//        popupWindow.showAtLocation(popup_view, Gravity.CENTER, 0, 0);

        //inflate layout for dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_app, null);

        Button Update_App = view.findViewById(R.id.update_btn);
        Button Cancel = view.findViewById(R.id.nope_btn);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog)
                .setCancelable(false);
        builder.setView(view);


        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.show();

        Update_App.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectStore(updateUrl);
                dialog.show();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setTitle("New version available")
//                .setMessage("Please, update app to new version.")
//                .setCancelable(false)
//                .setPositiveButton("Update",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                redirectStore(updateUrl);
//                            }
//                        }).setNegativeButton("No, thanks",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                finish();
//                            }
//                        }).create();
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();

    }

    private void redirectStore(String updateUrl) {

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void TapTarget() {
//        TapTargetView.showFor(this,
//                TapTarget.forView(findViewById(R.id.nav_button), "Menu", "click here to view various options")
//                        .tintTarget(false)
//                        .outerCircleColor(R.color.colorPrimary));
    }


}
