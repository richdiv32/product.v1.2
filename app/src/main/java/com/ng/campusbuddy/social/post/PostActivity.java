package com.ng.campusbuddy.social.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.ng.campusbuddy.profile.ProfileActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.utils.SharedPref;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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


    private void  prepareNotification(String pID, String title, String description, String notificationType, String notifiactionTopic){

        //prepare dat for notification
        String NOTIFICATION_TOPIC = "/topics/" + notifiactionTopic; //topic must match with what the receiver subscribeb to
        String NOTIFICATION_TITLE = title; //e.g CAMPUS BUDDY added a new post
        String NOTIFICATION_DESCRIPTION = description; //content of post
        String NOTIFICATION_TYPE = notificationType; //This indicate what type on notification this is <Now there are two notifications(Chat and Post)>

        //prepart json what to send, and where to send
        JSONObject notificationJS = new JSONObject();
        JSONObject notificationBodyJS = new JSONObject();

        try {
            //what to send
            notificationBodyJS.put("notificationType", NOTIFICATION_TYPE);
            notificationBodyJS.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
            notificationBodyJS.put("postID", pID);
            notificationBodyJS.put("postTitle", NOTIFICATION_TITLE);
            notificationBodyJS.put("postDescription", NOTIFICATION_DESCRIPTION);
            //where to send
            notificationBodyJS.put("to", NOTIFICATION_TOPIC);

            notificationBodyJS.put("data", notificationBodyJS);// combine data to be sent
        } catch (JSONException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //send volley object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJS,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PostActivity.this, ""+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //put required headers
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "key = ");
                //TODO: FCM required
                return headers;
            }
        };

        //engage the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }


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

                        //send Notification

//                        prepareNotification(
//                                ""+ timestamp,
//                                ""+ username + "added new post",
//                                ""+ description,
//                                "PostNotification",
//                                "POST" );

                        prepareNotification(
                                "",
                                "" +"Your Friend"+ "added new post",
                                ""+ description,
                                "PostNotification",
                                "POST" );

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
