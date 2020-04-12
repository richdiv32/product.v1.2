package com.ng.campusbuddy.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.Notification;
import com.ng.campusbuddy.social.messaging.PhotoActivity;
import com.ng.campusbuddy.social.messaging.chat.ChatActivity;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.utils.Data;
import com.ng.campusbuddy.utils.Sender;
import com.ng.campusbuddy.utils.SharedPref;
import com.ng.campusbuddy.utils.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    Context mContext = UserProfileActivity.this;

    FirebaseUser firebaseUser;
    String profileid;

    CircleImageView image_profile;
    ImageView bg;

    private static final String TAG = "UserProfileActivity";

    TextView fullname, username, profile_status
            , posts ,followers, following;

    Button edit_profile;

    ImageButton my_photos, saved_photos, Info, message;

    TextView Email,Bio, Birthday, Gender, Relationship_status
            , Institution, Faculty, Department,Telephone;

    private LinearLayout profile_info_layout;

    private RecyclerView recyclerView;
    private MyPhotosAdapter myPhotosAdapter;
    private List<Post> postList;

    private RecyclerView recyclerView_saves;
    private MyPhotosAdapter myPhotosAdapter_saves;
    private List<Post> postList_saves;
    private List<String> mySaves;

    //volley request queue for notification
    private RequestQueue requestQueue;
    private boolean notify = false;

    Intent intent;

    DatabaseReference notify_ref;

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = mContext.getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        //check if user is blocked or not
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(profileid).child("blocked_users");

        reference.orderByChild("userid").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.exists()) {
                                Toast.makeText(mContext, "Sorry..., Not permitted.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            //removes status bar with background
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        ImageButton Back = findViewById(R.id.back_btn);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        SharedPreferences prefs = mContext.getSharedPreferences("PREFS", MODE_PRIVATE);


        intent = getIntent();

        if (intent.getStringExtra("profileid") == null){

            profileid = prefs.getString("profileid", "none");

        }
        else {
            profileid = intent.getStringExtra("profileid");
        }

        image_profile = findViewById(R.id.image_profile);
        bg = findViewById(R.id.image_profile_bg);
        fullname = findViewById(R.id.fullname);
        username = findViewById(R.id.username);
        profile_status = findViewById(R.id.profile_status);
        posts = findViewById(R.id.post);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        edit_profile = findViewById(R.id.edit_button);

        my_photos = findViewById(R.id.my_photos);
        saved_photos = findViewById(R.id.saved_photos);
        Info = findViewById(R.id.info);
        message = findViewById(R.id.message);

        Email = findViewById(R.id.email);
        Bio = findViewById(R.id.bio);
        Birthday = findViewById(R.id.birthday);
        Relationship_status = findViewById(R.id.relationship_status);
        Institution = findViewById(R.id.institution);
        Faculty = findViewById(R.id.faculty);
        Department = findViewById(R.id.department);
        Telephone = findViewById(R.id.telephone_number);
        Gender = findViewById(R.id.gender);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView_saves = findViewById(R.id.recycler_view_save);
        profile_info_layout = findViewById(R.id.profile_info);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        myPhotosAdapter = new MyPhotosAdapter(mContext, postList);
        recyclerView.setAdapter(myPhotosAdapter);

        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager mLayoutManagers = new GridLayoutManager(mContext, 3);
        recyclerView_saves.setLayoutManager(mLayoutManagers);
        postList_saves = new ArrayList<>();
        myPhotosAdapter_saves = new MyPhotosAdapter(mContext, postList_saves);
        recyclerView_saves.setAdapter(myPhotosAdapter_saves);


        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.INVISIBLE);
        profile_info_layout.setVisibility(View.GONE);

        //volley request
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        Init();

        userInfo();
        getFollowers();
        getNrPosts();
        myPhotos();
        mySaves();

        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit");
            message.setVisibility(View.GONE);
        } else {
            checkFollow();
            saved_photos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btn = edit_profile.getText().toString();

                if (btn.equals("Edit")){

                    startActivity(new Intent(mContext, EditProfileActivity.class));

                } else if (btn.equals("follow")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).setValue(true);



                    //send notification
                    addNotification();

                } else if (btn.equals("following")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                    //delete notification
                    deleteNotification();

                }
            }
        });


    }

    private void Init() {
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                intent.putExtra("userid", profileid);
                startActivity(intent);
            }
        });


        Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profile_info_layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.GONE);


                DatabaseReference Info_reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
                Info_reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        Bio.setText(user.getBio());
                        Birthday.setText(user.getBirthday());
                        Gender.setText(user.getGender());
                        Institution.setText(user.getInstitution());
                        Faculty.setText(user.getFaculty());
                        Department.setText(user.getDepartment());
                        Telephone.setText(user.getTelephone());
                        Relationship_status.setText(user.getRelationship_status());
                        if (!dataSnapshot.child("email").exists()){
                            Email.setText("No Email to display");
                        }
                        else {
                            Email.setText(user.getEmail());
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_info_layout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });


        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FollowersActivity.class);
                intent.putExtra("id", profileid);
                intent.putExtra("title", "following");
                startActivity(intent);
            }
        });

    }

    private void addNotification(){


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        final String orderBy = "follow"+firebaseUser.getUid();
        reference.orderByChild("orderby").equalTo(orderBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String notification_id = reference.push().getKey();

                                    Log.d(TAG, "In notification");
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("userid", firebaseUser.getUid());
                                    hashMap.put("text", "started following you");
                                    hashMap.put("postid", "");
                                    hashMap.put("type", "follow");
                                    hashMap.put("id", notification_id);
                                    hashMap.put("isseen", false);
                                    hashMap.put("orderby", orderBy);


                                    reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "notification sent");
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            String notification_id = reference.push().getKey();

                            Log.d(TAG, "In notification");
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userid", firebaseUser.getUid());
                            hashMap.put("text", "started following you");
                            hashMap.put("postid", "");
                            hashMap.put("type", "follow");
                            hashMap.put("id", notification_id);
                            hashMap.put("isseen", false);
                            hashMap.put("orderby", orderBy);


                            reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "notification sent");
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void deleteNotification(){


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        String orderBy = "follow"+firebaseUser.getUid();
        reference.orderByChild("orderby").equalTo(orderBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                Glide.with(getApplicationContext())
                        .load(user.getImageurl())
                        .into(image_profile);
                Glide.with(getApplicationContext())
                        .load(user.getImageurl())
                        .into(bg);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                profile_status.setText(user.getProfile_status());


                image_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(UserProfileActivity.this, PhotoActivity.class);
                        intent.putExtra("imageurl", user.getImageurl());
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileid).exists()){
                    edit_profile.setText("following");
                } else{
                    edit_profile.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("followers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Follow").child(profileid).child("following");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNrPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void mySaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);

                    for (String id : mySaves) {
                        if (post.getPostid().equals(id)) {
                            postList_saves.add(post);
                        }
                    }
                }
                myPhotosAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
