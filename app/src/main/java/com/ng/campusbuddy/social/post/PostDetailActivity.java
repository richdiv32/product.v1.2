package com.ng.campusbuddy.social.post;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrawalsuneet.dotsloader.loaders.LightsLoader;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
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
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.FollowersActivity;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.messaging.PhotoActivity;
import com.ng.campusbuddy.utils.SharedPref;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class
PostDetailActivity extends AppCompatActivity {
    Context mContext = PostDetailActivity.this;

    RecyclerView recyclerView_comments, recyclerView_post;

    CommentAdapter commentAdapter;
    PostAdapter postAdapter;

    List<Comment> commentList;
    List<Post> postList;

    LightsLoader PD;


    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid;
    String publisherid;

    Intent intent;

    FirebaseUser firebaseUser;

    CircleImageView profile_image;
    ImageView more,PostImage, like, share, save, comment;
    TextView username, time_date, post_description, comments, likes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
//        //full scree width
//        Window window = getWindow();
//        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        PD = findViewById(R.id.loader);

        PostImage = findViewById(R.id.post_image);
        username = findViewById(R.id.username);
        post_description = findViewById(R.id.description);
        profile_image = findViewById(R.id.image_profile_post);
        comments = findViewById(R.id.comments);
        comment = findViewById(R.id.comment);
        like = findViewById(R.id.like);
        likes = findViewById(R.id.likes);
        time_date = findViewById(R.id.time_date);


        share = findViewById(R.id.share);
        save = findViewById(R.id.save);
        more = findViewById(R.id.more);

        /*---------------------------Post----------------------------*/
        SharedPreferences prefs = mContext.getSharedPreferences("PREFS", MODE_PRIVATE);
        postid = prefs.getString("postid", "none");

        recyclerView_post = findViewById(R.id.recycler_view_post);
        recyclerView_post.setHasFixedSize(true);
        LinearLayoutManager pLayoutManager = new LinearLayoutManager(mContext);
        recyclerView_post.setLayoutManager(pLayoutManager);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(mContext, postList);
        recyclerView_post.setAdapter(postAdapter);

        readPost();
        /*-------------------------------------------*/

        /*--------------------Comment------------------------*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisherid = intent.getStringExtra("publisherid");

        recyclerView_comments = findViewById(R.id.recycler_view_comments);
        recyclerView_comments.setHasFixedSize(true);
        LinearLayoutManager cLayoutManager = new LinearLayoutManager(this);
        recyclerView_comments.setLayoutManager(cLayoutManager);
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, postid);
        recyclerView_comments.setAdapter(commentAdapter);

        post = findViewById(R.id.post);
        addcomment = findViewById(R.id.add_comment);
        image_profile = findViewById(R.id.image_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addcomment.getText().toString().equals("")){
                    Toast.makeText(PostDetailActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();
        /*------------------------------------------------*/

        postdetails();



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
                isSaved(post.getPostid(), save);
                nrLikes(likes, post.getPostid());
                getCommetns(post.getPostid(), comments);

                Glide.with(PostDetailActivity.this)
                        .load(post.getPostimage())
                        .thumbnail(0.1f)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                PD.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                PD.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(PostImage);

                post_description.setText(post.getDescription());


                like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (like.getTag().equals("like")) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).setValue(true);
                            addNotificationLike(post.getPublisher(), post.getPostid());
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

                share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable)PostImage.getDrawable();
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

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (save.getTag().equals("save")){
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                                    .child(post.getPostid()).setValue(true);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                                    .child(post.getPostid()).removeValue();
                        }
                    }
                });

                PostImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PhotoActivity.class);
                        intent.putExtra("imageurl", post.getPostimage());
                        startActivity(intent);
                        Animatoo.animateShrink(mContext);
                    }
                });

                more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        OptionsMore(view, post.getPostid(), post.getPostimage(), post.getPublisher());
                    }
                });

                String timeStamp = post.getTimestamp();

                //converting time stamp to dd/mm/yyyy hh:mm am/pm
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(timeStamp));
                String dateTime = DateFormat.format("hh:mm aa, dd/MM", cal).toString();

                time_date.setText(dateTime);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void OptionsMore(View view, final String postid, final String postimage, final String publisherid) {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.edit:
                        editPost(postid);
                        return true;
                    case R.id.delete:
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
                        return true;
                    case R.id.report:
                        Toast.makeText(mContext, "Post Reported!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.save:
                        Toast.makeText(mContext, "Downloaded to gallery", Toast.LENGTH_SHORT).show();

                        downloadImage(postimage, postid);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.inflate(R.menu.post_menu);
        if (!publisherid.equals(firebaseUser.getUid())){
            popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
            popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
        }
        popupMenu.show();
    }

    private long downloadImage(String url, String extention) {

        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(mContext, Environment.getExternalStorageDirectory().toString(), "CB_post"+ extention + ".jpg");

        return downloadManager.enqueue(request);
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
                Glide.with(PostDetailActivity.this)
                        .load(user.getImageurl())
                        .thumbnail(0.1f)
                        .into(image_profile);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addNotificationLike(String userid, String postid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "liked your post");
        hashMap.put("postid", postid);
        hashMap.put("type", "post");

        reference.push().setValue(hashMap);
    }

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

    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmarked);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_bookmark_grey);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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





    private void readPost(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                Post post = dataSnapshot.getValue(Post.class);
                postList.add(post);

                postAdapter.notifyDataSetChanged();
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

    }

    private void addNotificationComment(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "commented: "+addcomment.getText().toString());
        hashMap.put("postid", postid);
        hashMap.put("type", "post");

        reference.push().setValue(hashMap);
    }

    private void getImage(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext())
                        .load(user.getImageurl())
                        .thumbnail(0.1f)
                        .into(image_profile);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readComments(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
