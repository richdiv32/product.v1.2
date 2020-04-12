package com.ng.campusbuddy.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.model.Notification;
import com.ng.campusbuddy.social.match.Match;
import com.ng.campusbuddy.social.match.MatchesActivity;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.post.PostDetailActivity;
import com.ng.campusbuddy.profile.UserProfileActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Notification> mNotification;

    public NotificationAdapter(Context context, List<Notification> notification){
        mContext = context;
        mNotification = notification;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_notificaion, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        final Notification notification = mNotification.get(position);


        holder.text.setText(notification.getText());

        getUserInfo(holder.image_profile, holder.username, notification.getUserid());
        CheckIsSeen(holder.layout, notification.getId());




        if (notification.getType().equals("follow") || notification.getType().equals("contest")){
            holder.post_image.setVisibility(View.GONE);
        }
        else if (notification.getType().equals("match")){
            holder.post_image.setVisibility(View.VISIBLE);
            getMatchImage(holder.post_image, notification.getUserid());
        }
        else if (notification.getType().equals("post")){
            holder.post_image.setVisibility(View.VISIBLE);
            getPostImage(holder.post_image, notification.getPostid());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (notification.getType().equals("follow") || notification.getType().equals("contest")){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", notification.getUserid());
                    editor.apply();

                    mContext.startActivity(new Intent(mContext, UserProfileActivity.class));
                    Animatoo.animateSplit(mContext);
                    isSeen(notification.getId());
                }
                else if (notification.getType().equals("match")){
                    mContext.startActivity(new Intent(mContext, MatchesActivity.class));
                    Animatoo.animateZoom(mContext);
                    isSeen(notification.getId());

                }
                else if (notification.getType().equals("post")){

                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid());
                    editor.apply();

                    Intent intent = new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("postid", notification.getPostid());
                    intent.putExtra("publisherid", notification.getPublisher());
                    mContext.startActivity(intent);
                    Animatoo.animateZoom(mContext);

                    isSeen(notification.getId());
                }



            }
        });



    }

    private void isSeen(String id){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid())
                .child(id).child("isseen").setValue(true);
    }


    private void CheckIsSeen(final View layout, String id) {

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(fUser.getUid()).child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Notification notification = dataSnapshot.getValue(Notification.class);
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("isseen").exists()){
                        if (notification.isIsseen()){
                            layout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
                        }
                        else {
                            layout.setBackgroundColor(Color.parseColor("#6F8890A6"));
                        }
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //
    @Override
    public int getItemCount() {
        return mNotification.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image;
        public TextView username, text;
        public RelativeLayout layout;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.title);
            text = itemView.findViewById(R.id.description);

            layout = itemView.findViewById(R.id.notification_background);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(publisherid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView post_image, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(mContext).load(post.getPostimage()).into(post_image);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMatchImage(final ImageView post_image, final String userId) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").
                child(fuser.getUid());
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String profile_image = dataSnapshot.child("imageurl").getValue().toString();
                Glide.with(mContext).load(profile_image).into(post_image);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}