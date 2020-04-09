package com.ng.campusbuddy.social;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.ng.campusbuddy.profile.UserProfileActivity;
import com.ng.campusbuddy.social.messaging.chat.ChatActivity;
import com.ng.campusbuddy.utils.Data;
import com.ng.campusbuddy.utils.Sender;
import com.ng.campusbuddy.utils.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ImageViewHolder> {

    private Context mContext;
    private List<User> mUsers;
    private boolean isActivity;
    private boolean notify = false;


    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> users, boolean isActivity){
        mContext = context;
        mUsers = users;
        this.isActivity = isActivity;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final User user = mUsers.get(position);

        holder.btn_message.setVisibility(View.VISIBLE);
        holder.btn_follow.setVisibility(View.VISIBLE);
        isFollowing(user.getId(), holder.btn_follow);
        isBlock(user.getId(), holder.btn_message, holder.btn_follow);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getFullname());
        Glide.with(mContext)
                .load(user.getImageurl())
                .placeholder(R.drawable.profile_bg)
                .thumbnail(0.1f)
                .into(holder.image_profile);

        if (user.getId().equals(firebaseUser.getUid())){
            holder.btn_follow.setVisibility(View.GONE);
            holder.btn_message.setVisibility(View.GONE);
        }


        checkisBlocked(user.getId(),position, holder.btn_follow, holder.btn_message, holder.block_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isBlocked_or_Not(user.getId());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // show delete message confirm dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Block");
                builder.setMessage("Are you sure to block this user?");
                //delete button
                builder.setPositiveButton("Block", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        if (mUsers.get(position).isBlocked()){
//                            unblockUser(user.getId());
//                        }
//                        else {
//                            blockUser(user.getId());
//                        }

                        blockUser(user.getId(), holder.btn_follow, holder.btn_message, holder.block_img);

                    }
                });
                //cancel delete button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });

                //create and show dialog
                builder.create().show();

                return false;
            }
        });

        holder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btn_follow.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());

                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }

        });

        holder.btn_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent message = new Intent(mContext, ChatActivity.class);
                message.putExtra("userid", user.getId());
                mContext.startActivity(message);
                Animatoo.animateSlideUp(mContext);


            }
        });

        holder.block_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unblockUser(user.getId(), holder.btn_follow, holder.btn_message, holder.block_img);
            }
        });
    }


    private void isBlock(final String userid, final Button btn_message, final Button btn_follow){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid).child("blocked_users");

        reference.orderByChild("userid").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.exists()) {
                                btn_message.setVisibility(View.GONE);
                                btn_follow.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void isBlocked_or_Not(final String userid){

        //check if user is blocked or not
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userid).child("blocked_users");

        reference.orderByChild("userid").equalTo(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.exists()) {
                                Toast.makeText(mContext, "Sorry..., Not permitted.", Toast.LENGTH_SHORT).show();
                                return;

                            }
                        }
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", userid);
                        editor.apply();

                        mContext.startActivity(new Intent(mContext, UserProfileActivity.class));
                        Animatoo.animateSplit(mContext);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void checkisBlocked(String id, final int position, final Button btn_follow, final Button btn_message, final ImageView block_img) {

        //check if user is blocked or not
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("blocked_users");

        reference.orderByChild("userid").equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.exists()){
                                mUsers.get(position).setBlocked(true);

                                btn_follow.setVisibility(View.GONE);
                                btn_message.setVisibility(View.GONE);
                                block_img.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void unblockUser(String id, final Button btn_follow, final Button btn_message, final ImageView block_img) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid()).child("blocked_users");

        reference.orderByChild("userid").equalTo(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                            if (ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //unblock successful
                                                Toast.makeText(mContext, "Unblocking successful", Toast.LENGTH_SHORT).show();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //unblock unsuccessful
                                                Toast.makeText(mContext, "Failed "+ e, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void blockUser(String id, final Button btn_follow, final Button btn_message, final ImageView block_img) {
        //block the user by adding id to current user blocked child
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
        .child(firebaseUser.getUid()).child("blocked_users").child(id);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", id);

        reference.setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //blocked successful
                        Toast.makeText(mContext, "User Blocked", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed to block user
                        Toast.makeText(mContext, "Blocking unsuccessful." + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addNotification(String userid){
        Log.d("UserAdapter", "adding notification");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postid", "");
        hashMap.put("type", "follow");

        reference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;
        public Button btn_message;
        public ImageView block_img;

        public ImageViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            fullname = itemView.findViewById(R.id.fullname);
            image_profile = itemView.findViewById(R.id.image_profile);
            btn_follow = itemView.findViewById(R.id.btn_follow);
            btn_message = itemView.findViewById(R.id.btn_message);
            block_img = itemView.findViewById(R.id.blocked_image);
        }
    }

    private void isFollowing(final String userid, final Button button){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()){
                    button.setText("following");
                } else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}