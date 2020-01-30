package com.ng.campusbuddy.social.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.quicksettings.Tile;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.utils.Data;
import com.ng.campusbuddy.utils.Sender;
import com.ng.campusbuddy.utils.SharedPref;
import com.ng.campusbuddy.utils.Token;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    CircleImageView group_image;
    TextView title, description;

    FirebaseUser fuser;
    DatabaseReference reference;
    ValueEventListener seenListener;

    ImageButton btn_send, back_btn, attach_btn;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    String groupid;

    //volley request queue for notification
    private RequestQueue requestQueue;
    private boolean notify = false;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

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
        setContentView(R.layout.activity_group_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //volley request
        requestQueue = Volley.newRequestQueue(getApplicationContext());


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        group_image = findViewById(R.id.profile_image);
        title = findViewById(R.id.title);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        description = findViewById(R.id.description);
        attach_btn = findViewById(R.id.btn_attachment);

        intent = getIntent();
        groupid = intent.getStringExtra("groupid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString().trim();
                if (!msg.equals("")){
//                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(GroupChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

                //reset edittext after sending message
                text_send.setText("");
            }
        });

        attach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("Grouplist")
                .child(fuser.getUid());


        reference.child(groupid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Grouplist grouplist = dataSnapshot.getValue(Grouplist.class);

                title.setText(grouplist.getTitle());

                if (grouplist.getGroupimage().equals("")){
                    group_image.setImageResource(R.mipmap.ic_launcher);
                }
                else {

                    Picasso.get()
                            .load(grouplist.getGroupimage())
                            .placeholder(R.drawable.placeholder)
                            .into(group_image);
                }


//                readMesagges(fuser.getUid(), userid, user.getImageurl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        seenMessage(userid);
        SetupNavigationDrawer();
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
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                }
                if (which == 1){
                    //Gallery Clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    private void pickFromGallery(){
        //Intent to pick image from gallery
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*")
                , IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        //Intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, image_rui)
                , IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

//    private void seenMessage(final String userid){
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        seenListener = reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)){
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("isseen", true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void sendMessage(String sender, final String receiver, String message){
//
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        String timestamp = String.valueOf(System.currentTimeMillis());
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("sender", sender);
//        hashMap.put("receiver", receiver);
//        hashMap.put("message", message);
//        hashMap.put("isseen", false);
//        hashMap.put("timestamp", timestamp);
//        hashMap.put("type", "text");
//
//        reference.child("Chats").push().setValue(hashMap);
//
//
//        // add user to chat fragment
//        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                .child(fuser.getUid())
//                .child(userid);
//
//        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()){
//                    chatRef.child("id").setValue(userid);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
//                .child(userid)
//                .child(fuser.getUid());
//        chatRefReceiver.child("id").setValue(fuser.getUid());
//
//        final String msg = message;
//
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                if (notify) {
//                    sendNotifiaction(receiver, user.getUsername(), msg);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void sendImageMessage(Uri image_rui) {
//        notify = true;
//        //progressDialog
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Sending image.....");
//        progressDialog.setMessage("few seconds left");
//        progressDialog.dismiss();
//
//        final String timeStamp = "" + System.currentTimeMillis();
//        String fileNameAndPath = "ChatImages/"+"post_"+timeStamp;
//
//        //Chats node will be created that will contain all images sent via chat
//
//        //get bitmap from image uri
//        Bitmap bitmap = null;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_rui);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] data = baos.toByteArray();//this will convert image to bytes
//        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
//        ref.putBytes(data)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        //image uploaded
//                        progressDialog.dismiss();
//
//                        //get url of uploaded image
//                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
//                        while (!uriTask.isSuccessful());
//                        String downloadUri = uriTask.getResult().toString();
//
//                        if (uriTask.isSuccessful()){
//                            //add image uri and other info to database
//                            DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference();
//
//                            //setup required data
//                            HashMap<String, Object> hashMap = new HashMap<>();
//                            hashMap.put("sender", fuser.getUid());
//                            hashMap.put("receiver", userid);
//                            hashMap.put("message", downloadUri);
//                            hashMap.put("timestamp", timeStamp);
//                            hashMap.put("type", "image");
//                            hashMap.put("isSeen", false);
//
//                            databaseReference.child("Chats").push().setValue(hashMap);
//
//                            //send notification
//                            DatabaseReference database = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                            database.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    User user = dataSnapshot.getValue(User.class);
//
//                                    if (notify){
//                                        sendNotifiaction(userid, user.getUsername(), "Sent you a photo...");
//                                    }
//                                    notify = false;
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            // add user to chat fragment
//                            final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                                    .child(fuser.getUid())
//                                    .child(userid);
//
//                            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    if (!dataSnapshot.exists()){
//                                        chatRef.child("id").setValue(userid);
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//
//                            final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("Chatlist")
//                                    .child(userid)
//                                    .child(fuser.getUid());
//                            chatRefReceiver.child("id").setValue(fuser.getUid());
//
//                            final String msg = downloadUri;
//
//                            reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//                            reference.addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    User user = dataSnapshot.getValue(User.class);
//                                    if (notify) {
//                                        sendNotifiaction(userid, user.getUsername(), msg);
//                                    }
//                                    notify = false;
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                }
//                            });
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        //failed
//                        Toast.makeText(GroupChatActivity.this, "Error Occured", Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                });
//    }

//    private void sendNotifiaction(String receiver, final String username, final String message){
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(receiver);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Token token = snapshot.getValue(Token.class);
//                    Data data = new Data(""+fuser.getUid(),
//                            R.mipmap.ic_launcher_foreground,
//                            ""+username+": "+message,
//                            "New Message",
//                            ""+userid,
//                            "ChatNotification");
//
//                    Sender sender = new Sender(data, token.getToken());
//
//                    //fcm json object request
//                    try{
//                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
//                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
//                                new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        //response of the request
//                                        Log.d("JSON_RESPONSE", "onResponse: "+response.toString());
//                                    }
//                                }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.d("JSON_RESPONSE", "onResponse: "+error.toString());
//                            }
//                        }){
//                            @Override
//                            public Map<String, String> getHeaders() throws AuthFailureError {
//                                //put params
//                                Map<String, String> headers = new HashMap<>();
//                                headers.put("Content-Type", "application/json");
//                                headers.put("Authorization", "key= AAAAwTB5AhQ:APA91bEu8Ma29SIWNCWcdcuI93O6cgybCefaEa-m97bHCHkATKekLfV1OHcSA8YnqD74tp9Bvua_31AAZ9vrSwii-dDmCt00IA2UPn1IrGzW9Yi0Yo0GmlrCRbfVUiizVCFxK9qQqaSS");
//
//                                //TODO : Ckeck Google server key
//                                // AIzaSyCu-GjMjBy2C_8jgeOGCI9YNFrprRQkqew
//
//                                return headers;
//                            }
//                        };
//
//                        //add this request to queue
//                        requestQueue.add(jsonObjectRequest);
//                    }
//                    catch (JSONException e){
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

//    private void readMesagges(final String myid, final String userid, final String imageurl){
//        mchat = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mchat.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
//                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
//                        mchat.add(chat);
//                    }
//
//                    messageAdapter = new MessageAdapter(GroupChatActivity.this, mchat, imageurl);
//                    recyclerView.setAdapter(messageAdapter);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void currentUser(String userid){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this, "Camera & Storage permission required", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image picked from Gallery, get uri of image
                image_rui = data.getData();

                //send uri to firebase storage
//                sendImageMessage(image_rui);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image
//                sendImageMessage(image_rui);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void SetupNavigationDrawer() {

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview=navigationView.getHeaderView(0);
        RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);
        ImageButton EditButton = headerview.findViewById(R.id.edit_button);
        ImageButton AddButton = headerview.findViewById(R.id.add_user);

        // name, prfoile status
        final TextView Title = headerview.findViewById(R.id.group_title);
        final TextView Creator = headerview.findViewById(R.id.creator);
        final ImageView Group_image = headerview.findViewById(R.id.group_image);

//        //        Loading profile image
//        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Grouplist")
//                .child(fuser.getUid());
//
//        Nav_reference.child(groupid).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
////                if (dataSnapshot.exists()){
////                    String group_image = dataSnapshot.child("groupimage").getValue().toString();
////                    String title = dataSnapshot.child("title").getValue().toString();
//////                    String creator = dataSnapshot.child("profile_status").getValue().toString();
////
////
////                    Picasso.get().load(group_image).into(Group_image);
////                    Title.setText(title);
//////                    Profile_status.setText(profile_status);
////                }
////                else{
////
////                    Picasso.get().load(R.drawable.chat_bg).into(Group_image);
//////                    Username.setText("Username");
//////                    Profile_status.setText("Hey there, am on Campus Buddy");
////                }
//
//                Grouplist grouplist = dataSnapshot.getValue(Grouplist.class);
//
//                Title.setText(grouplist.getTitle());
//
//                if (grouplist.getGroupimage().equals("")){
//                    Group_image.setImageResource(R.drawable.chat_bg);
//                }
//                else {
//
//                    Picasso.get()
//                            .load(grouplist.getGroupimage())
//                            .placeholder(R.drawable.placeholder)
//                            .into(Group_image);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("Grouplist")
                .child(fuser.getUid());


        Nav_reference.child(groupid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Grouplist grouplist = dataSnapshot.getValue(Grouplist.class);

                if (dataSnapshot.exists()){
                    Title.setText(grouplist.getTitle());

                    if (grouplist.getGroupimage().equals("")){
                        Group_image.setImageResource(R.drawable.chat_bg);
                    }
                    else {

                        Picasso.get()
                                .load(grouplist.getGroupimage())
                                .placeholder(R.drawable.placeholder)
                                .into(Group_image);
                    }
                }
//                else {
//
//                    Picasso.get().load(R.drawable.auth_bg).into(Group_image);
//                    Title.setText("Title");
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        navigationHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupChatActivity.this, "Edit Group Image", Toast.LENGTH_SHORT).show();
            }
        });

        EditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupChatActivity.this, "Edit Group Name", Toast.LENGTH_SHORT).show();
            }
        });

        AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(GroupChatActivity.this, "Add User", Toast.LENGTH_SHORT).show();
            }
        });

//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//
//                switch (menuItem.getItemId()){
//                    case R.id.nav_home:
//                        Toast.makeText(mcontext, "Home", Toast.LENGTH_SHORT).show();
//                        break;
//                    case R.id.nav_education:
//                        Intent education = new Intent(mcontext, EducationActivity.class);
//                        startActivity(education);
//                        Animatoo.animateSlideLeft(mcontext);
//                        finish();
//                        break;
//                    case R.id.nav_social:
//                        Intent social = new Intent(mcontext, SocialActivity.class);
//                        startActivity(social);
//                        Animatoo.animateSlideLeft(mcontext);
//                        finish();
//                        break;
//                    case R.id.nav_notifications:
//                        startActivity(new Intent(mcontext, NotificationsActivity.class));
//                        Animatoo.animateSlideLeft(mcontext);
//                        break;
//                    case R.id.nav_settings:
//                        startActivity(new Intent(mcontext, SettingsActivity.class));
//                        Animatoo .animateSlideLeft(mcontext);
//                        break;
//                    case R.id.nav_log_out:
//                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                        mAuth.signOut();
//                        startActivity(new Intent(mcontext, WelcomeActivity.class)
//                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                        Animatoo.animateShrink(mcontext);
//                        break;
//                }
//
//                return false;
//            }
//        });
    }

}

