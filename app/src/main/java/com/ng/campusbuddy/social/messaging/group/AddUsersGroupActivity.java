package com.ng.campusbuddy.social.messaging.group;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.UserProfileActivity;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class AddUsersGroupActivity extends AppCompatActivity {
    String id, groupid;
    String title;

    private List<String> idList;

    RecyclerView recyclerView;
    AddUsersGroupAdapter userAdapter;
    List<User> userList;

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
        setContentView(R.layout.activity_new_chat);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        groupid = intent.getStringExtra("groupid");

        assert groupid != null;
        String neq = groupid;


//        AddUsersGroupAdapter addUsersGroupAdapter = new AddUsersGroupAdapter(this, userList, false, true, intent);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title_text = findViewById(R.id.title);
        title_text.setText(title);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        userAdapter = new AddUsersGroupAdapter(this, userList, false, true);
        recyclerView.setAdapter(userAdapter);

        idList = new ArrayList<>();

        getFollowing();

        TextView  textView = findViewById(R.id.group_id);
        textView.setText(neq);

    }


    private void getFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(id).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                idList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    idList.add(snapshot.getKey());
                }
                showUsers();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showUsers() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (String id : idList){
                        if (user.getId().equals(id)){
                            userList.add(user);
                        }
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class AddUsersGroupAdapter extends RecyclerView.Adapter<AddUsersGroupAdapter.ImageViewHolder> {

        private Context mContext;
        private List<User> mUsers;
        private boolean isActivity;
        private boolean ischat;


        private FirebaseUser firebaseUser;

        public AddUsersGroupAdapter(Context mContext, List<User> mUsers, boolean isActivity, boolean ischat) {
            this.mContext = mContext;
            this.mUsers = mUsers;
            this.isActivity = isActivity;
            this.ischat = ischat;
        }


        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_user, parent, false);
            return new ImageViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            final User user = mUsers.get(position);



//            holder.itemView.setBackgroundColor(user.isSelected() ? Color.CYAN : Color.TRANSPARENT);

            holder.username.setText(user.getUsername());


            if (!user.getImageurl().equals(" ")){
                Glide.with(mContext)
                        .load(user.getImageurl())
                        .thumbnail(0.1f)
                        .into(holder.profile_image);

            } else {
                holder.profile_image.setImageResource(R.mipmap.ic_launcher);
            }

            if (ischat){
                if (user.getOnline_status().equals("online")){
                    holder.img_on.setVisibility(View.VISIBLE);
                    holder.img_off.setVisibility(View.GONE);
                } else {
                    holder.img_on.setVisibility(View.GONE);
                    holder.img_off.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.GONE);
            }

            holder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    mContext.startActivity(new Intent(mContext, UserProfileActivity.class));
                    Animatoo.animateSplit(mContext);
                }
            });



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                if (user.isSelected()){
//                    holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
//                    if (mUsers.get(position) != null){
//                        mUsers.set(position, user);
//                    }
//                }
//                else {
//                    holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
//                    if (mUsers.get(position) == user){
//                        mUsers.set(position, user);
//                    }
//                }


//                user.setSelected(!user.isSelected());
//                holder.itemView.setBackgroundColor(user.isSelected()? Color.CYAN : Color.TRANSPARENT);

                    Intent Gintent = new Intent(mContext, GroupChatActivity.class);
                    Gintent.putExtra("groupid",groupid);
                    Gintent.putExtra("userid", user.getId());
                    mContext.startActivity(Gintent);
                    finish();
                    Animatoo.animateZoom(mContext);


                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {



                    return false;
                }
            });


        }


        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {

            public TextView username;
            public ImageView profile_image;
            private ImageView img_on;
            private ImageView img_off;
            public RelativeLayout UserchatLayout; //for click listner

            public ImageViewHolder(View itemView) {
                super(itemView);

                username = itemView.findViewById(R.id.username);
                profile_image = itemView.findViewById(R.id.profile_image);
                img_on = itemView.findViewById(R.id.img_on);
                img_off = itemView.findViewById(R.id.img_off);
                UserchatLayout = itemView.findViewById(R.id.chatUserLayout);
            }
        }


    }


}
