package com.ng.campusbuddy.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.auth.LoginActivity;
import com.ng.campusbuddy.social.messaging.PhotoActivity;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.utils.SharedPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    Context mContext = ProfileActivity.this;

    FirebaseUser firebaseUser;
    String profileid;

    CircleImageView image_profile;
    ImageView bg;

    TextView Fullname, Username, Profile_status
            , posts ,followers, following;

    Button edit_profile;

    ImageButton my_photos, saved_photos, Info;

    TextView Email, Bio, Birthday, Gender, Relationship_status
            , Institution, Faculty, Department,Telephone;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            //removes status bar with background
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton Back = findViewById(R.id.back_btn);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileid = firebaseUser.getUid();

        image_profile = findViewById(R.id.image_profile);
        bg = findViewById(R.id.image_profile_bg);
        Fullname = findViewById(R.id.fullname);
        Username = findViewById(R.id.username);
        Profile_status = findViewById(R.id.profile_status);
        posts = findViewById(R.id.post);
        followers = findViewById(R.id.followers);
        following = findViewById(R.id.following);
        edit_profile = findViewById(R.id.edit_button);

        my_photos = findViewById(R.id.my_photos);
        saved_photos = findViewById(R.id.saved_photos);
        Info = findViewById(R.id.info);

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
        recyclerView_saves.setVisibility(View.GONE);
        profile_info_layout.setVisibility(View.GONE);

        if (profileid.equals(profileid)){
            edit_profile.setText("Edit");
        }
        else {
            checkFollow();
        }

        Init();

        userInfo();
        getFollowers();
        getNrPosts();
         myPhotos();
        mySaves();


    }


    private void Init() {
        Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                profile_info_layout.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.GONE);


                final DatabaseReference Info_reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
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
                            Info_reference.child("email").setValue(firebaseUser.getEmail());
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

        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profile_info_layout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] options = {"Edit Info","Change Email", "Change Password"};

                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Choose image from");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {
                            startActivity(new Intent(mContext, EditProfileActivity.class));
                        }
                        if (which == 1){
                            showUpdateEmail();
                        }
                        if (which == 2){
                            showUpdatePassword();
                        }
                    }
                });
                builder.create().show();

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

    private void showUpdateEmail() {
        //inflate layout for dialog
        View view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_update_password, null);
        final EditText Current_email = view.findViewById(R.id.current_email);
        final EditText New_email = view.findViewById(R.id.new_email);
        final EditText Password = view.findViewById(R.id.password);
        Button Update_Email = view.findViewById(R.id.change_email_btn);
        LinearLayout Password_Layout = view.findViewById(R.id.password_layout);
        LinearLayout Email_Layout = view.findViewById(R.id.email_layout);

        Email_Layout.setVisibility(View.VISIBLE);
        Password_Layout.setVisibility(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Update_Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_current_email = Current_email.getText().toString();
                final String str_new_email = New_email.getText().toString();
                String str_password = Password.getText().toString();

                if (TextUtils.isEmpty(str_current_email)){
                    Toast.makeText(ProfileActivity.this, "Input your current email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(str_new_email)){
                    Toast.makeText(ProfileActivity.this, "Input your new email", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(str_password)){
                    Toast.makeText(ProfileActivity.this, "Input your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                    //before changing password re-authenticate the user
                    AuthCredential authCredential = EmailAuthProvider.getCredential(fUser.getEmail(), str_password);
                    fUser.reauthenticate(authCredential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //successfully  authenticated, begin update
                                    fUser.updateEmail(str_new_email)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //email changed
                                                    DatabaseReference Info_reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                                                    Info_reference.child("email").setValue(str_new_email);
                                                    Toast.makeText(ProfileActivity.this, "Email changed successfully...", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //failed updating email
                                                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //authentication failed
                                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                dialog.dismiss();
            }
        });
    }

    private void showUpdatePassword() {
        //inflate layout for dialog
        View view = LayoutInflater.from(ProfileActivity.this).inflate(R.layout.dialog_update_password, null);
        final EditText Current_Password = view.findViewById(R.id.current_password);
        final EditText New_Password = view.findViewById(R.id.new_password);
        Button Update_Password = view.findViewById(R.id.update_btn);
        LinearLayout Password_Layout = view.findViewById(R.id.password_layout);
        LinearLayout Email_Layout = view.findViewById(R.id.email_layout);

        Email_Layout.setVisibility(View.GONE);
        Password_Layout.setVisibility(View.VISIBLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.show();

        Update_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_current_password = Current_Password.getText().toString();
                final String str_new_password = New_Password.getText().toString();

                if (TextUtils.isEmpty(str_current_password)){
                    Toast.makeText(ProfileActivity.this, "Input your current password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(str_new_password)){
                    Toast.makeText(ProfileActivity.this, "Input your new password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (str_new_password.length()<6){
                    Toast.makeText(ProfileActivity.this, "Password must be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

                    //before changing password re-authenticate the user
                    AuthCredential authCredential = EmailAuthProvider.getCredential(fUser.getEmail(), str_current_password);
                    fUser.reauthenticate(authCredential)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //successfully  authenticated, begin update
                                    fUser.updatePassword(str_new_password)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //password changed
                                                    Toast.makeText(ProfileActivity.this, "Password changed successfully...", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    //failed updating password
                                                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //authentication failed
                                    Toast.makeText(mContext, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }

                dialog.dismiss();
            }
        });
    }

    private void userInfo(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    final User user = dataSnapshot.getValue(User.class);

                    Glide.with(getApplicationContext())
                            .load(user.getImageurl())
                            .thumbnail(0.1f)
                            .into(image_profile);

                    Glide.with(getApplicationContext())
                            .load(user.getImageurl())
                            .thumbnail(0.1f)
                            .into(bg);
                    Username.setText(user.getUsername());
                    Fullname.setText(user.getFullname());
                    Profile_status.setText(user.getProfile_status());

                    image_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ProfileActivity.this, PhotoActivity.class);
                            intent.putExtra("imageurl", user.getImageurl());
                            startActivity(intent);
                        }
                    });
                }
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
