package com.ng.campusbuddy.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.adapter.MyPhotosAdapter;
import com.ng.campusbuddy.model.Post;
import com.ng.campusbuddy.model.User;
import com.ng.campusbuddy.utils.SharedPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {
    Context mContext = UserProfileActivity.this;

    FirebaseUser firebaseUser;
    String profileid;

    CircleImageView image_profile;

    TextView fullname, username, profile_status
            , posts ,followers, following;

    Button edit_profile;

    ImageButton my_photos, saved_photos, Info;

    TextView Bio, Birthday, Gender, Relationship_status
            , Institution, Faculty, Department, Email, Telephone;

    private LinearLayout profile_info_layout;

    private RecyclerView recyclerView;
    private MyPhotosAdapter myPhotosAdapter;
    private List<Post> postList;

    private RecyclerView recyclerView_saves;
    private MyPhotosAdapter myPhotosAdapter_saves;
    private List<Post> postList_saves;
    private List<String> mySaves;

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
        setContentView(R.layout.activity_profile_user);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = mContext.getSharedPreferences("PREFS", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");

        image_profile = findViewById(R.id.image_profile);
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

        Bio = findViewById(R.id.bio);
        Birthday = findViewById(R.id.birthday);
        Email = findViewById(R.id.email);
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

        Init();

        userInfo();
        getFollowers();
        getNrPosts();
        myPhotos();
        mySaves();

        if (profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit");
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
                    addNotification();
                } else if (btn.equals("following")){

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }
            }
        });


    }

    private void Init() {
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
                        Email.setText(user.getEmail());
                        Gender.setText(user.getGender());
                        Institution.setText(user.getInstitution());
                        Faculty.setText(user.getFaculty());
                        Department.setText(user.getDepartment());
                        Telephone.setText(user.getTelephone());
                        Relationship_status.setText(user.getRelationship_status());

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

//        saved_photos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                profile_info_layout.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.GONE);
//                recyclerView_saves.setVisibility(View.VISIBLE);
//            }
//        });


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
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("ispost", false);

        reference.push().setValue(hashMap);
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mContext == null){
                    return;
                }
                User user = dataSnapshot.getValue(User.class);

                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                profile_status.setText(user.getBio());

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
