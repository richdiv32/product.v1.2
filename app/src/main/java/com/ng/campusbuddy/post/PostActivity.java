package com.ng.campusbuddy.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.Post;
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.utils.SharedPref;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    private Uri mImageUri;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;

    EditText description;

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
        setContentView(R.layout.activity_post);

        description = findViewById(R.id.description);

        storageRef = FirebaseStorage.getInstance().getReference("posts");



        Init();
//        LoadUserInfo();
        LoadImage();
    }

    private void Init() {
        ImageButton Back = findViewById(R.id.nav_button);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage_10();
            }
        });

        ImageView image_added = findViewById(R.id.image_added);
        image_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1,1)
                        .start(PostActivity.this);
            }
        });


        final RelativeLayout ImageLayout = findViewById(R.id.post_imageLayout);
        final CardView TextLayout = findViewById(R.id.post_textLayout);

        ImageLayout.setVisibility(View.VISIBLE);
        TextLayout.setVisibility(View.GONE);

        ImageButton Button_post_text = findViewById(R.id.btn_post_text);
        Button_post_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLayout.setVisibility(View.GONE);
                TextLayout.setVisibility(View.VISIBLE);
            }
        });

        ImageButton Button_post_image = findViewById(R.id.btn_post_image);
        Button_post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLayout.setVisibility(View.VISIBLE);
                TextLayout.setVisibility(View.GONE);
            }
        });




    }

//    private void LoadUserInfo() {
//
//        final CircleImageView Profile_image = findViewById(R.id.image_profile);
//        final TextView Username = findViewById(R.id.nav_username);
//
//        Profile_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(PostActivity.this, ProfileActivity.class));
//                Animatoo.animateSplit(PostActivity.this);
//            }
//        });
//
//        //        Loading profile image
//        String profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
//        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()){
//                    String profile_image = dataSnapshot.child("imageurl").getValue().toString();
//                    String username = dataSnapshot.child("username").getValue().toString();
//
//
//                    Picasso.get()
//                            .load(profile_image)
//                            .centerCrop()
//                            .into(Profile_image);
//
//
////                    Username.setText(username);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void LoadImage() {

        final CircleImageView Profile_image = findViewById(R.id.image_profile);
        final TextView Username = findViewById(R.id.username);

        Profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, ProfileActivity.class));
                Animatoo.animateSplit(PostActivity.this);
            }
        });

        //        Loading profile image
        String profileid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Users");
        Nav_reference.child(profileid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String profile_image = dataSnapshot.child("imageurl").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();

                    Glide.with(getApplicationContext())
                            .load(profile_image)
                            .into(Profile_image);

                    Username.setText(username);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage_10(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();
        if (mImageUri != null){
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", miUrlOk);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);

                        pd.dismiss();

                        startActivity(new Intent(PostActivity.this, SocialActivity.class));
                        finish();

                    } else {
                        Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(PostActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();

            ImageView image_added = findViewById(R.id.image_added);
            image_added.setImageURI(mImageUri);
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, PostActivity.class));
            finish();
        }
    }

}
