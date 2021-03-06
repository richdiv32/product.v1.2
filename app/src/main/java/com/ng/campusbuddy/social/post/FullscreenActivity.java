package com.ng.campusbuddy.social.post;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LightsLoader;
import com.agrawalsuneet.dotsloader.loaders.TrailingCircularDotsLoader;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jsibbold.zoomage.ZoomageView;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.FollowersActivity;
import com.ng.campusbuddy.social.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ng.campusbuddy.utils.GlideExtentions.isValidContextForGlide;

public class FullscreenActivity extends AppCompatActivity {
    Context mContext = FullscreenActivity.this;


    String postid;
    String publisherid;

    Intent intent;

    EditText addcomment;
    TextView post;

    FirebaseUser firebaseUser;

    ZoomageView mContentView;
    CircleImageView profile_image;
    ImageView PostImage, like, share, save, comment;
    TextView username, post_description, comments, likes;

    LightsLoader Pd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        mContentView = findViewById(R.id.fullscreen_content);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        postdetails();

        PostImage = findViewById(R.id.post_image);
        username = findViewById(R.id.username);
        post_description = findViewById(R.id.description);
        profile_image = findViewById(R.id.image_profile_post);
        comments = findViewById(R.id.comments);
        comment = findViewById(R.id.comment);
        like = findViewById(R.id.like);
        likes = findViewById(R.id.likes);

        Pd = findViewById(R.id.loader);


        share = findViewById(R.id.share);
//        save = findViewById(R.id.save);

        post = findViewById(R.id.post);
        addcomment = findViewById(R.id.add_comment);

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(FullscreenActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                    //plays sound
                    MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.error);
                    mediaPlayer.start();
                } else {
                    addComment();
                }
            }
        });

        Init();
    }

    private void Init() {
        ImageButton Back;
        Back = findViewById(R.id.back_btn);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void postdetails() {

        final DatabaseReference PostRef = FirebaseDatabase.getInstance().getReference()
                .child("Posts").child(postid);

        PostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);

                publisherInfo(profile_image, username, post.getPublisher());
                isLiked(post.getPostid(), like);
//                isSaved(post.getPostid(), save);
                nrLikes(likes, post.getPostid());
                getCommetns(post.getPostid(), comments);

                Glide.with(FullscreenActivity.this)
                        .load(post.getPostimage())
                        .thumbnail(0.1f)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
//                                Pd.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
//                                Pd.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(mContentView);

                post_description.setText(post.getDescription());


                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (like.getTag().equals("like")) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).setValue(true);
                            addNotificationLike(post.getPublisher(), post.getPostid());

                            //plays sound
                            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.pop);
                            mediaPlayer.start();
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).removeValue();
                        }
                    }
                });

                likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, FollowersActivity.class);
                        intent.putExtra("id", post.getPostid());
                        intent.putExtra("title", "likes");
                        mContext.startActivity(intent);
                        Animatoo.animateShrink(mContext);
                    }
                });

                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, PostDetailActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, mContentView, ViewCompat.getTransitionName(mContentView));
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent, optionsCompat.toBundle());
//                        Animatoo.animateShrink(mContext);

                    }
                });

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable)mContentView.getDrawable();
                        if (bitmapDrawable == null){
                            // We work on this later
                        }
                        else {
                            // converting image to bitmap
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            shareImage_Text(post.getDescription(),bitmap);
                        }
                    }
                });

//                save.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (save.getTag().equals("save")){
//                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
//                                    .child(post.getPostid()).setValue(true);
//                        } else {
//                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
//                                    .child(post.getPostid()).removeValue();
//                        }
//                    }
//                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void shareImage_Text(String Description, Bitmap bitmap){
        String shareBody = Description;

        Uri uri = saveImageToShare(bitmap);

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.setType("image/png");
        mContext.startActivity(Intent.createChooser(sIntent, "Share Via"));

    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(mContext.getCacheDir(), "images");

        Uri uri = null;
        try{
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext, "com.ng.campusbuddy.fileprovider", file);
        }
        catch (Exception e){
            Toast.makeText(mContext, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return uri;
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

    private void nrLikes(final TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCommetns(String postId, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.setText(""+dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (isValidContextForGlide(mContext)){
                    Glide.with(FullscreenActivity.this)
                            .load(user.getImageurl())
                            .thumbnail(0.1f)
                            .into(image_profile);
                    username.setText(user.getUsername());
                }
//                if (isValidContextForGlide(mContext)){
//
//                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addNotificationLike(String userid, final String postid){


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        final String orderBy = "like"+firebaseUser.getUid();
        reference.orderByChild("orderby").equalTo(orderBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String notification_id = reference.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("userid", firebaseUser.getUid());
                                    hashMap.put("text", "liked your post");
                                    hashMap.put("postid", postid);
                                    hashMap.put("type", "post");
                                    hashMap.put("id", notification_id);
                                    hashMap.put("isseen", false);
                                    hashMap.put("orderby", orderBy);


                                    reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            String notification_id = reference.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userid", firebaseUser.getUid());
                            hashMap.put("text", "liked your post");
                            hashMap.put("postid", postid);
                            hashMap.put("type", "post");
                            hashMap.put("id", notification_id);
                            hashMap.put("isseen", false);
                            hashMap.put("orderby", orderBy);


                            reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

//    private void deleteNotification(){
//
//
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);
//
//        String orderBy = "follow"+firebaseUser.getUid();
//        reference.orderByChild("orderby").equalTo(orderBy)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()){
//                            dataSnapshot.getRef().removeValue();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//    }

    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//    private void isSaved(final String postid, final ImageView imageView){
//
//        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
//                .child("Saves").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.child(postid).exists()){
//                    imageView.setImageResource(R.drawable.ic_bookmarked);
//                    imageView.setTag("saved");
//                } else{
//                    imageView.setImageResource(R.drawable.ic_bookmark_grey);
//                    imageView.setTag("save");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postid).updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addComment(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("commentid", commentid);

        reference.child(commentid).setValue(hashMap);
        addNotificationComment();
        addcomment.setText("");
        //plays sound
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.send);
        mediaPlayer.start();

    }

    private void addNotificationComment(){


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        final String orderBy = "comment"+firebaseUser.getUid();
        reference.orderByChild("orderby").equalTo(orderBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String notification_id = reference.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("userid", firebaseUser.getUid());
                                    hashMap.put("text", "commented: "+addcomment.getText().toString());
                                    hashMap.put("postid", postid);
                                    hashMap.put("type", "post");
                                    hashMap.put("id", notification_id);
                                    hashMap.put("isseen", false);
                                    hashMap.put("orderby", orderBy);


                                    reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            String notification_id = reference.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userid", firebaseUser.getUid());
                            hashMap.put("text", "commented: "+addcomment.getText().toString());
                            hashMap.put("postid", postid);
                            hashMap.put("type", "post");
                            hashMap.put("id", notification_id);
                            hashMap.put("isseen", false);
                            hashMap.put("orderby", orderBy);


                            reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

}
