package com.ng.campusbuddy.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.post.PostDetailActivity;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyPhotosAdapter extends RecyclerView.Adapter<MyPhotosAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Post> mPosts;

    public MyPhotosAdapter(Context context, List<Post> posts){
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_photos, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder holder, final int position) {

        final Post post = mPosts.get(position);
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


        if (post.getPostimage().equals("")){
//            holder.post_image.setImageResource(R.drawable.placeholder);
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }
        else {

            Glide.with(mContext)
                    .load(post.getPostimage())
                    .placeholder(R.drawable.placeholder)
//                    .override(600, 900)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
//                            holder.Pd.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
//                            holder.Pd.setVisibility(View.GONE);
                            return false;
                        }
                    })

                    .into(holder.post_image);

        }

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                mContext.startActivity(intent);
                Animatoo.animateSlideUp(mContext);
            }
        });

        holder.post_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (fUser.getUid().equals(post.getPublisher())){
                    // show more message confirm dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete this post?");
                    //delete button
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deletePost(position, post.getPostid());
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
                }
                else {

                }


                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView post_image;


        public ImageViewHolder(View itemView) {
            super(itemView);

            post_image = itemView.findViewById(R.id.post_image);

        }
    }

    private void deletePost(int position, String postid) {
//        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        String msgTimeStamp = mPosts.get(position).getTimestamp();
//        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Posts");
//        Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds: dataSnapshot.getChildren()){
//                    if (ds.child("sender").getValue().equals(myUID)){
//
//                        //To remove the post completly
//                        ds.getRef().removeValue();
//
//                        Toast.makeText(mContext, "post deleted.....", Toast.LENGTH_SHORT).show();
//                    }
//                    else {
//
//                    }
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String id = postid;
        FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            deleteNotifications(id, firebaseUser.getUid());
                        }
                    }
                });
    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}