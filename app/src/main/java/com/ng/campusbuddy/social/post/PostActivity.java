package com.ng.campusbuddy.social.post;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.AllianceLoader;
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
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

   Uri mImageUri = null;
    String miUrlOk = "";
    private StorageTask uploadTask;
    StorageReference storageRef;

    EditText description;
    EditText note;

    Intent intent;

    CircleImageView Profile_image;
    TextView Username;
    ImageView Add;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //for image
    Uri image_rui = null;

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
        note = findViewById(R.id.description_note);

        storageRef = FirebaseStorage.getInstance().getReference("posts");

        //get data through intent from previous activities adapter
        intent = getIntent();

        //detect if note fab was clicked
        String notes = intent.getStringExtra("Notes");
        if (notes == null){

            final RelativeLayout ImageLayout = findViewById(R.id.post_imageLayout);
            final LinearLayout TextLayout = findViewById(R.id.post_textLayout);

            ImageLayout.setVisibility(View.GONE);
            TextLayout.setVisibility(View.VISIBLE);
        }
        else {
            final RelativeLayout ImageLayout = findViewById(R.id.post_imageLayout);
            final LinearLayout TextLayout = findViewById(R.id.post_textLayout);

            ImageLayout.setVisibility(View.VISIBLE);
            TextLayout.setVisibility(View.GONE);
        }

        //get image from camera
        String image_path = intent.getStringExtra("imagePath");
        if (image_path != null){
            Uri fileUri = Uri.parse(image_path);
            mImageUri = fileUri;
            //set to imageview
            ImageView image_added = findViewById(R.id.image_added);
            image_added.setImageURI(mImageUri);

            final RelativeLayout ImageLayout = findViewById(R.id.post_imageLayout);
            final LinearLayout TextLayout = findViewById(R.id.post_textLayout);

            ImageLayout.setVisibility(View.VISIBLE);
            TextLayout.setVisibility(View.GONE);
        }



        //get data and its type from intent
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null){

            if ("text/plain".equals(type)){
                //text type data
                handleSendText(intent);
            }
            else if (type.startsWith("image")){
                //image type data
                handleSendImage(intent);
            }

            final RelativeLayout ImageLayout = findViewById(R.id.post_imageLayout);
            final LinearLayout TextLayout = findViewById(R.id.post_textLayout);

            ImageLayout.setVisibility(View.VISIBLE);
            TextLayout.setVisibility(View.GONE);
        }

        Init();
        LoadImage();
    }

    private void handleSendText(Intent intent) {
        //handle received text
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null){
            //set to description edit text
            description.setText(sharedText);
        }

    }

    private void handleSendImage(Intent intent) {
        //handle received image(url)
        Uri imageURI = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageURI != null){
            mImageUri = imageURI;
            //set to imageview
            ImageView image_added = findViewById(R.id.image_added);
            image_added.setImageURI(mImageUri);

        }

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
                checkLayout();

            }
        });

        ImageView Add = findViewById(R.id.add_btn);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });




        final RelativeLayout ImageLayout = findViewById(R.id.post_imageLayout);
        final LinearLayout TextLayout = findViewById(R.id.post_textLayout);

//        ImageLayout.setVisibility(View.VISIBLE);
//        TextLayout.setVisibility(View.GONE);



    }

    private void checkLayout() {

        if (mImageUri == null && description.getText().toString().equals("")){
            uploadNote();
        }
        else {
            uploadImage_10();
        }

    }

    private void uploadNote() {
        final AllianceLoader Pd = findViewById(R.id.loader);
        Pd.setVisibility(View.VISIBLE);

        if (note.getText().toString() != null){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

            String timestamp = String.valueOf(System.currentTimeMillis());
            String postid = reference.push().getKey();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("postid", postid);
            hashMap.put("postimage", "");
            hashMap.put("description", note.getText().toString());
            hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("timestamp", timestamp);


            reference.child(postid).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            pd.dismiss();
                            Pd.setVisibility(View.GONE);

                            prepareNotification(
                                    "",
                                    "" + Username + " added new post",
                                    ""+ description,
                                    "PostNotification",
                                    "POST" );

                            startActivity(new Intent(PostActivity.this, SocialActivity.class));
                            finish();
                        }
                    });
        }
    }

    private void  prepareNotification(String pID, String title, String description, String notificationType, String notifiactionTopic){

        //prepare dat for notification
        String NOTIFICATION_TOPIC = "/topics/" + notifiactionTopic; //topic must match with what the receiver subscribe to
        String NOTIFICATION_TITLE = title; //e.g CAMPUS BUDDY added a new post
        String NOTIFICATION_DESCRIPTION = description; //content of post
        String NOTIFICATION_TYPE = notificationType; //This indicate what type on notification this is <Now there are two notifications(Chat and Post)>

        //prepare json what to send, and where to send
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

        sendPostNotificaiton(notificationJS);
    }

    private void sendPostNotificaiton(JSONObject notificationJS) {
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
                //put params
                Map<String , String > headers = new HashMap<>();
                headers.put("Content-Type","application/json");
                headers.put("Authorization", "key= AAAAwTB5AhQ:APA91bEu8Ma29SIWNCWcdcuI93O6cgybCefaEa-m97bHCHkATKekLfV1OHcSA8YnqD74tp9Bvua_31AAZ9vrSwii-dDmCt00IA2UPn1IrGzW9Yi0Yo0GmlrCRbfVUiizVCFxK9qQqaSS");

                return headers;
            }
        };

        //engage the volley request
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void LoadImage() {

        Profile_image = findViewById(R.id.image_profile);
        Username = findViewById(R.id.username);

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
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Posting");
//        pd.show();

        final AllianceLoader Pd = findViewById(R.id.loader);
        Pd.setVisibility(View.VISIBLE);

        if (mImageUri != null){
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));


//            uploadTask = fileReference.putFile(mImageUri);
            uploadTask = fileReference.putBytes(data);
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
                        final String timestamp = String.valueOf(System.currentTimeMillis());

                        String postid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", miUrlOk);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("timestamp", timestamp);

                        reference.child(postid).setValue(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        pd.dismiss();
                                        Pd.setVisibility(View.GONE);

                                        prepareNotification(
                                                ""+timestamp,
                                                "" + Username + " added new post",
                                                ""+ description,
                                                "PostNotification",
                                                "POST" );

                                        startActivity(new Intent(PostActivity.this, SocialActivity.class));
                                        finish();
                                    }
                                });
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

        }
        else if (description.getText().toString() != null && mImageUri == null){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

            String timestamp = String.valueOf(System.currentTimeMillis());
            String postid = reference.push().getKey();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("postid", postid);
            hashMap.put("postimage", "");
            hashMap.put("description", description.getText().toString());
            hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("timestamp", timestamp);


            reference.child(postid).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            pd.dismiss();
//                            Pd.setVisibility(View.VISIBLE);

                            prepareNotification(
                                    "",
                                    "" + Username + " added new post",
                                    ""+ description,
                                    "PostNotification",
                                    "POST" );

                            startActivity(new Intent(PostActivity.this, SocialActivity.class));
                            finish();
                        }
                    });
        }
        else {
            Toast.makeText(PostActivity.this, "Post an image or type a text", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK){
//            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
//                //image picked from Gallery, get uri of image
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            mImageUri = result.getUri();
//            }

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image is picked from gallery, get uri of image
                mImageUri = data.getData();

                ImageView image_added = findViewById(R.id.image_added);
                image_added.setImageURI(mImageUri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image
                ImageView image_added = findViewById(R.id.image_added);
                image_added.setImageURI(mImageUri);
            }
            else {
                Toast.makeText(this, "Image sending unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showImagePickDialog() {

        String[] options = {"Camera","Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose image from");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    //Carmera Clicked
                    pickFromCamera();
                }
                if (which == 1){
                    //Gallery Clicked
                    pickFromGallery();
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery(){
//        Intent to pick image from gallery
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*")
                , IMAGE_PICK_GALLERY_CODE);

//        CropImage.activity()
//                .setAspectRatio(1,1)
//                .start(PostActivity.this);
    }

    private void pickFromCamera(){
        //Intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, mImageUri)
                , IMAGE_PICK_CAMERA_CODE);
    }

}
