
package com.ng.campusbuddy.home;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideExtension;
import com.bumptech.glide.annotation.GlideOption;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.auth.SetUpProfileActivity;
import com.ng.campusbuddy.model.AD;
import com.ng.campusbuddy.social.match.MatchesActivity;
import com.ng.campusbuddy.social.messaging.chat.ChatActivity;
import com.ng.campusbuddy.tools.WebViewActivity;
import com.ng.campusbuddy.utils.Token;
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.start.WelcomeActivity;
import com.ng.campusbuddy.tools.AdInfoActivity;
import com.ng.campusbuddy.tools.NotificationsActivity;
import com.ng.campusbuddy.tools.SettingsActivity;
import com.ng.campusbuddy.utils.SharedPref;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.Url;

import static com.ng.campusbuddy.utils.App.CHANNEL_FOLLOW_ID;
import static com.ng.campusbuddy.utils.App.CHANNEL_MATCH_ID;


public class HomeActivity extends AppCompatActivity {
    Context mcontext = HomeActivity.this;

    String profileid;

    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    DatabaseReference user_Ref;

    private NotificationManagerCompat notificationManager;
    private EditText Title_txt, Message_txt;
    ImageView Image;
    Uri image_rui = null;


    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = mAuth.getCurrentUser();

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
                    } else {

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
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        //targets the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        super.onCreate(savedInstanceState);
//        this will prevent screenshot in an activity
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_home);

        AdMod();

        mAuth = FirebaseAuth.getInstance();
        profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        user_Ref = FirebaseDatabase.getInstance().getReference().child("Users");

        SetupNavigationDrawer();
        ADimageslider();

        LoadImage();

        TextView ADD = findViewById(R.id.ad);
        ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                builder.setTitle("Create New AD");

                LinearLayout linearLayout = new LinearLayout(mcontext);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                final EditText groupName = new EditText(mcontext);
                final EditText web = new EditText(mcontext);
                final EditText tel = new EditText(mcontext);
                groupName.setHint("Type Ad title... ");
                web.setHint("Website");
                tel.setHint("Tel");
                builder.setView(linearLayout);


                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String groupName_str = groupName.getText().toString();

                        if (TextUtils.isEmpty(groupName_str)) {
                            Toast.makeText(mcontext, "You can't create a room without a name", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference().child("ADs").child("Social")
                                    .child("Slides");
                            String room_id = roomRef.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", room_id);
                            hashMap.put("full_image", "");
                            hashMap.put("image", "");
                            hashMap.put("title", groupName_str);
                            hashMap.put("description", "new ad added to campus buddy");
                            //TODO: work on the next two lines
                            hashMap.put("web", web.getText().toString());
                            hashMap.put("call", tel.getText().toString());

                            roomRef.child(room_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        Toast.makeText(mcontext, groupName_str + " has been created", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.cancel();
                    }
                });

                //create and show dialog
                builder.create().show();
            }
        });

        RecommendedUsers();

//        UpdateToken();

        channels();

    }

    private void channels() {
        notificationManager = NotificationManagerCompat.from(this);

        Title_txt = findViewById(R.id.title);
        Message_txt = findViewById(R.id.message);

        Button matchup_btn = findViewById(R.id.match_channel);
        Button follow_btn = findViewById(R.id.follow_channel);
        Button like_btn = findViewById(R.id.like_channel);
        Button comment_btn = findViewById(R.id.comment_channel);



        matchup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                matchChannel();
                imageChannel();
            }
        });

        follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followChannel();
            }
        });

        like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postChannel();
            }
        });

        comment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postChannel();
            }
        });
    }

    public void imageChannel() {
        String title = Title_txt.getText().toString();
        String body = Message_txt.getText().toString();

        //Init for the custom notification layout
        final RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.item_notification_bar);
        notificationLayout.setTextViewText(R.id.title, title);
        notificationLayout.setTextViewText(R.id.description, body);
        notificationLayout.setImageViewResource(R.id.imageView, R.drawable.logo);


        //get bitmap from image uri
//        image_url = image_rui.to

        //Large Icon
//        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.chat_bg);


        final Intent intent = new Intent(this, MatchesActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_MATCH_ID)
                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(title)
//                .setContentText(body)
//                .setLargeIcon(largeIcon)
                .setColor(Color.RED)
                .setContent(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);


        final Notification notification = builder.build();


        final NotificationTarget notificationTarget = new NotificationTarget(getApplicationContext(),
                R.id.image_profile,
                notificationLayout,notification,
                1);

        final NotificationTarget notificationTarget2 = new NotificationTarget(getApplicationContext(),
                R.id.post_image,
                notificationLayout,notification,
                2);



        //        Loading profile image
        String profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);


                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(R.drawable.profile_bg)
                            .into(notificationTarget);

                    Glide.with(getApplicationContext())
                            .asBitmap()
                            .load(user.getImageurl())
                            .into(notificationTarget2);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        noti.notify(1, notification);
    }


    @GlideExtension
    public static class MyGlideExtension{
        private MyGlideExtension(){}

        @NonNull
        @GlideOption
        public static RequestOptions roundedCorners(RequestOptions options, @NonNull Context context, int cornerRadius){
            int px = Math.round(cornerRadius * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return options.transform(new RoundedCorners(px));
        }
    }

    public void matchChannel() {
        //show post detail activity using post id when notification clicked
        Intent intent = new Intent(this, MatchesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);


        //Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_MATCH_ID)
                .setSmallIcon(R.drawable.logo)
//                .setLargeIcon(largeIcon)
                .setContentTitle(Title_txt.getText().toString())
                .setContentText(Message_txt.getText().toString())
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(Color.RED)
                .build();
        notificationManager.notify(1, notification);
    }

    public void followChannel() {
        //show post detail activity using post id when notification clicked
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, intent, PendingIntent.FLAG_ONE_SHOT);


        //Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_FOLLOW_ID)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setNumber(3)
                .setContentTitle(Title_txt.getText().toString())
                .setContentText(Message_txt.getText().toString())
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(Color.RED)
                .build();

        notificationManager.notify(2, notification);
    }

    public void postChannel() {
        //show post detail activity using post id when notification clicked
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 3, intent, PendingIntent.FLAG_ONE_SHOT);

        //Large Icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        //sound
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_FOLLOW_ID)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(Title_txt.getText().toString())
                .setContentText(Message_txt.getText().toString())
                .setSound(defaultSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setColor(Color.RED)
                .build();


        notificationManager.notify(3, notification);
    }


    private void RecommendedUsers() {
        List<User> followingList = new ArrayList<>();


        RecyclerView User_recycler = findViewById(R.id.rec_users);
        final List<User> mUserList = new ArrayList<>();
        User_recycler.hasFixedSize();
        LinearLayoutManager mLayoutManger = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        User_recycler.setLayoutManager(mLayoutManger);
        final UserAdapter userAdapter = new UserAdapter(this, mUserList);
        User_recycler.setAdapter(userAdapter);

        DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    User user = ds.getValue(User.class);
//                    if (user.getId().equals(firebaseUser.getUid())){
//
//                    }

                    mUserList.add(user);
                    Collections.shuffle(mUserList);
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void AdMod() {
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        AdView mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);
    }

    private void ADimageslider() {
        final ArrayList<AD> sliderList = new ArrayList<>();
        final SliderView sliderView = findViewById(R.id.ADsSlider);
        final SliderAdapterADs adapter = new SliderAdapterADs(this, sliderList);
        sliderView.setSliderAdapter(adapter);

        DatabaseReference Adref = FirebaseDatabase.getInstance().getReference().child("ADs").child("Browse")
                .child("Slides");

        Adref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sliderList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    AD ad = ds.getValue(AD.class);
                    sliderList.add(ad);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        Profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mcontext, ProfileActivity.class));
                Animatoo.animateSplit(mcontext);
            }
        });

        //        Loading profile image
        String profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    Glide.with(getApplicationContext())
                            .load(user.getImageurl())
                            .into(Profile_image);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void SetupNavigationDrawer() {


        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview = navigationView.getHeaderView(0);
        RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);

        LinearLayout Nav_button = findViewById(R.id.nav);
        Nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mcontext, "drawer", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(GravityCompat.START, true);

            }
        });

        // name, prfoile status
        final TextView Username = headerview.findViewById(R.id.nav_username);
        final TextView Profile_status = headerview.findViewById(R.id.nav_status);
        final CircleImageView Profile_image = headerview.findViewById(R.id.image_profile);
        final ImageView Profile_image_bg = headerview.findViewById(R.id.image_profile_bg);
        final TextView Followers = headerview.findViewById(R.id.followers);
        final TextView Following = headerview.findViewById(R.id.following);

        //        Loading profile image
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    User user = dataSnapshot.getValue(User.class);
//
//                    Picasso.get().load(user.getImageurl()).into(Profile_image);
//                    Glide.with(getApplicationContext())
//                            .load(user.getImageurl())
//                            .into(Profile_image_bg);
//                    Username.setText(user.getUsername());
//                    Profile_status.setText(user.getProfile_status());
//                }
//                else{
//
//                    Picasso.get().load(R.drawable.profile_bg).into(Profile_image);
//                    Username.setText("Username");
//                    Profile_status.setText("Hey there, am on Campus Buddy");
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference Followers_reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        Followers_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Followers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mcontext, ProfileActivity.class));
                Animatoo.animateSplit(mcontext);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_social:
                        drawerLayout.closeDrawer(GravityCompat.START, true);
                        Intent social = new Intent(mcontext, SocialActivity.class);
                        startActivity(social);
                        Animatoo.animateSlideLeft(mcontext);
                        finish();
                        break;
                    case R.id.nav_notifications:
                        drawerLayout.closeDrawer(GravityCompat.START, true);
                        startActivity(new Intent(mcontext, NotificationsActivity.class));
                        Animatoo.animateSlideLeft(mcontext);
                        break;
                    case R.id.nav_settings:
                        drawerLayout.closeDrawer(GravityCompat.START, true);
                        startActivity(new Intent(mcontext, SettingsActivity.class));
                        Animatoo.animateSlideLeft(mcontext);
                        break;
                    case R.id.nav_about_us:
                        String url = "https://campusbuddy.xyz/Organisation";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(Intent.createChooser(intent, "Browse with"));
                        break;
                    case R.id.nav_faq:
                        String url2 = "https://campusbuddy.xyz/Team";
                        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse(url2));
                        startActivity(Intent.createChooser(intent2, "Browse with"));
                        break;
                    case R.id.nav_log_out:
                        drawerLayout.closeDrawer(GravityCompat.START, true);
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        mAuth.signOut();
                        startActivity(new Intent(mcontext, WelcomeActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        Animatoo.animateShrink(mcontext);
                        break;
                    case R.id.nav_invite:
                        drawerLayout.closeDrawer(GravityCompat.START, true);
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
                }

                return false;
            }
        });
    }

    static final int TIME_INTERVAL = 2000;
    long mBackPressed;

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(mcontext, "Tap again to exit", Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }


    private class SliderAdapterADs extends SliderViewAdapter<SliderAdapterADs.SliderAdapterVH> {

        private Context context;
        private ArrayList<AD> sliderlist;

        public SliderAdapterADs(Context context, ArrayList<AD> sliderlist) {
            this.context = context;
            this.sliderlist = sliderlist;
        }

        @Override
        public SliderAdapterADs.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider_layout, null);
            return new SliderAdapterADs.SliderAdapterVH(inflate);
        }


        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

            final AD ad = sliderlist.get(position);


            viewHolder.textViewDescription.setText(ad.getTitle());


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AdInfoActivity.class);
                    intent.putExtra("Ad_id", ad.getId());
                    intent.putExtra("context", "Home");
                    context.startActivity(intent);
                }
            });


            Glide.with(context)
                    .load(ad.getImage())
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.imageViewBackground);

        }


        @Override
        public int getCount() {
            return sliderlist.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            public View itemView;
            public ImageView imageViewBackground;
            public TextView textViewDescription;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
                textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
                this.itemView = itemView;
            }
        }


    }

    public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

        private Context mContext;
        private List<User> mUsers;


        private FirebaseUser firebaseUser;

        public UserAdapter(Context context, List<User> users) {
            mContext = context;
            mUsers = users;
        }


        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_story, parent, false);
            return new ImageViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            final User user = mUsers.get(position);

            isFollowing(user.getId(), holder.layout, holder.getAdapterPosition());

            holder.username.setText(user.getUsername());
            Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);


        }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            public TextView username;
            public ImageView image_profile;
            public CardView layout;

            public ImageViewHolder(View itemView) {
                super(itemView);

                username = itemView.findViewById(R.id.story_username);
                image_profile = itemView.findViewById(R.id.story_photo);
                layout = itemView.findViewById(R.id.story_layout);
            }
        }

        private void isFollowing(final String userid, final CardView layout, final int position) {

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Follow").child(firebaseUser.getUid()).child("following");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
//                    if (dataSnapshot.child(userid).exists() || firebaseUser.getUid().equals(userid)){
////                        removeItem(position);
////                        layout.setVisibility(View.GONE);
////
//                    }
//                    else{
//                        layout.setVisibility(View.VISIBLE);
//                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void removeItem(int position) {
            mUsers.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mUsers.size());
        }

    }

}
