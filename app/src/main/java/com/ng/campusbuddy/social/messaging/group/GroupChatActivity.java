package com.ng.campusbuddy.social.messaging.group;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.kevalpatel2106.emoticompack.samsung.SamsungEmoticonProvider;
import com.kevalpatel2106.emoticongifkeyboard.EmoticonGIFKeyboardFragment;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.Emoticon;
import com.kevalpatel2106.emoticongifkeyboard.emoticons.EmoticonSelectListener;
import com.kevalpatel2106.emoticongifkeyboard.gifs.Gif;
import com.kevalpatel2106.emoticongifkeyboard.gifs.GifSelectListener;
import com.kevalpatel2106.emoticongifkeyboard.widget.EmoticonButton;
import com.kevalpatel2106.emoticongifkeyboard.widget.EmoticonEditText;
import com.kevalpatel2106.emoticongifkeyboard.widget.EmoticonTextView;
import com.kevalpatel2106.gifpack.giphy.GiphyGifProvider;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.UserProfileActivity;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.messaging.chat.Chat;
import com.ng.campusbuddy.utils.SharedPref;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {
    CircleImageView group_image;
    TextView title, description;

    FirebaseUser fuser;
    DatabaseReference reference;
    ValueEventListener seenListener;

    ImageButton btn_send, back_btn, attach_btn;
    EmoticonEditText text_send;
    EmoticonButton emoji_btn;

    RecyclerView message_recycler;
    MessageAdapter messageAdapter;
    List<Chat> mchat;

    Intent intent;

    String groupid, userid;
    String CreatorUID;

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
        //targets the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
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

        //init permissions arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        //volley request
        requestQueue = Volley.newRequestQueue(getApplicationContext());



        group_image = findViewById(R.id.profile_image);
        title = findViewById(R.id.title);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        description = findViewById(R.id.description);
        attach_btn = findViewById(R.id.btn_attachment);
        emoji_btn = findViewById(R.id.emoji_btn);

        intent = getIntent();
        userid = intent.getStringExtra("userid");
        groupid = intent.getStringExtra("groupid");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (userid != null){
            AddUserToGroup();
        }


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString().trim();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(), msg);
                } else {
                    Toast.makeText(GroupChatActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }

                //reset edittext after sending message
                text_send.setText("");
            }
        });

        emoji_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Emoticon();

                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(text_send.getWindowToken(), 0);
            }
        });

        attach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });


        reference = FirebaseDatabase.getInstance().getReference().child("Groups");

        reference.child(groupid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);

                title.setText(group.getGroup_title());

                if (group.getGroup_image().equals("")){
                    group_image.setImageResource(R.drawable.placeholder);
                }
                else {

                    Glide.with(getApplicationContext())
                            .load(group.getGroup_image())
                            .placeholder(R.drawable.placeholder)
                            .into(group_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        SetupNavigationDrawer();

        readMesagges();
    }

    private void AddUserToGroup() {
        final DatabaseReference RefReceiver = FirebaseDatabase.getInstance().getReference("Grouplist")
                .child(userid)
                .child(groupid);
        RefReceiver.child("groupid").setValue(groupid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups")
                                .child(groupid).child("group_users").child(userid);
                        ref.child("group_userid").setValue(userid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(GroupChatActivity.this, "User added to group", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

    }



    private void readMesagges(){
        message_recycler = findViewById(R.id.recycler_view);
        message_recycler.setHasFixedSize(true);
        message_recycler.setLayoutManager(new LinearLayoutManager(this));
        mchat = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupid).child("messages");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    mchat.add(chat);

                }
                LoadUsersData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadUsersData() {


        final List<User> mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chat chat : mchat){
                        if (user.getId().equals(chat.getSender())){
                            mUsers.add(user);
                        }
                    }

                    messageAdapter = new MessageAdapter(GroupChatActivity.this, mchat, user.getImageurl(), user.getUsername(), user.getId());
                    message_recycler.setAdapter(messageAdapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void Emoticon() {

        EmoticonGIFKeyboardFragment.EmoticonConfig emoticonConfig = new EmoticonGIFKeyboardFragment.EmoticonConfig()
                .setEmoticonProvider(SamsungEmoticonProvider.create())
                .setEmoticonSelectListener(new EmoticonSelectListener() {
                    @Override
                    public void emoticonSelected(Emoticon emoticon) {
//                        text_send.getText().insert(emoticon.getIcon());
                        text_send.getText().append(emoticon.getUnicode());
//                        text_send.setText(emoticon + "\u263A");
                    }

                    @Override
                    public void onBackSpace() {


                    }
                });

        //GIF config
        EmoticonGIFKeyboardFragment.GIFConfig gifConfig = new EmoticonGIFKeyboardFragment
                .GIFConfig(GiphyGifProvider.create(this, "0OADsQMbBfnSjyQudSukHfvyi9Tj0BTX"))
                .setGifSelectListener(new GifSelectListener() {
                    @Override
                    public void onGifSelected(@NonNull Gif gif) {

                    }
                });

        EmoticonGIFKeyboardFragment emoticonGIFKeyboardFragment = EmoticonGIFKeyboardFragment
                .getNewInstance(findViewById(R.id.fragment_container), emoticonConfig, gifConfig);

        //Adding the keyboard fragment to keyboard container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, emoticonGIFKeyboardFragment)
                .commit();

        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        frameLayout.setVisibility(View.VISIBLE);


    }

    @Override
    public void onBackPressed() {
        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        frameLayout.setVisibility(View.GONE);

        super.onBackPressed();
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

    private void sendMessage(String sender, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");

        reference.child("Groups")
                .child(groupid)
                .child("messages").push().setValue(hashMap);

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
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //image picked from Gallery, get uri of image
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                image_rui = result.getUri();

                ChangeGroupImage(image_rui);

            }
            else if (requestCode == IMAGE_PICK_GALLERY_CODE){
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

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview=navigationView.getHeaderView(0);
        final RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);
        final ImageButton EditButton = headerview.findViewById(R.id.edit_button);
        final ImageButton AddButton = headerview.findViewById(R.id.add_user);

//        String CreatorUID;


        RelativeLayout Nav_button = findViewById(R.id.profile_layout);
        Nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.END);

            }
        });

        // name, prfoile status
        final TextView Title = headerview.findViewById(R.id.group_title);
        final TextView Creator = headerview.findViewById(R.id.creator);
        final ImageView Group_image = headerview.findViewById(R.id.group_image);


        reference = FirebaseDatabase.getInstance().getReference().child("Groups");

        reference.child(groupid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);

                Title.setText(group.getGroup_title());

                if (group.getGroup_image().equals("")){
                    Group_image.setImageResource(R.drawable.chat_bg);
                }
                else {

                    Glide.with(GroupChatActivity.this)
                            .load(group.getGroup_image())
                            .placeholder(R.drawable.placeholder)
                            .into(Group_image);
                }

                CreatorUID = group.getGroup_creator();
                    DatabaseReference creatorRef = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(CreatorUID);

                    creatorRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                User user = dataSnapshot.getValue(User.class);
                                Creator.setText("Creator: "+ user.getUsername());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                if (group.getGroup_creator().equals(fuser.getUid())){

                    EditButton.setVisibility(View.VISIBLE);
                    AddButton.setVisibility(View.VISIBLE);

                    navigationHeader.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(GroupChatActivity.this, "Edit Group Image", Toast.LENGTH_SHORT).show();

                            CropImage.activity()
                                    .setAspectRatio(1,1)
                                    .start(GroupChatActivity.this);
                        }
                    });

                    EditButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(GroupChatActivity.this, "Edit Group Name", Toast.LENGTH_SHORT).show();
                            EditGroup();
                        }
                    });

                    AddButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseUser ID = FirebaseAuth.getInstance().getCurrentUser();

                            Intent intent = new Intent(GroupChatActivity.this, AddUsersGroupActivity.class);
                            intent.putExtra("id", ID.getUid());
                            intent.putExtra("groupid", groupid);
                            startActivity(intent);

                            Toast.makeText(GroupChatActivity.this, "Start new chat", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                else {
                    EditButton.setVisibility(View.GONE);
                    AddButton.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        LoadGroupUsers(navigationView);
    }

    private void LoadGroupUsers(NavigationView navigationView) {
        final RecyclerView Group_user_recycler = navigationView.findViewById(R.id.group_users_recycler);
        Group_user_recycler.setHasFixedSize(true);
        Group_user_recycler.setLayoutManager(new LinearLayoutManager(this));
        final List<Group> usersList = new ArrayList<>();
        final GroupUsersAdapter  groupUsersAdapter = new GroupUsersAdapter(this, usersList);
        Group_user_recycler.setAdapter(groupUsersAdapter);


        final TextView GroupUserCount = navigationView.findViewById(R.id.users_count);


        reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupid)
                .child("group_users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Group user = snapshot.getValue(Group.class);
                    usersList.add(user);

                }

                GroupUserCount.setText(dataSnapshot.getChildrenCount()+" users");
                groupUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void EditGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Group Name");

        final EditText groupName = new EditText(getApplicationContext());
        groupName.setHint("Type group name... ");
        builder.setView(groupName);


        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName_str = groupName.getText().toString();

                if (TextUtils.isEmpty(groupName_str)){
                    Toast.makeText(getApplicationContext(), "You can't have a group without a name", Toast.LENGTH_SHORT).show();
                }
                else {
                    EditGroupName(groupName_str);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.cancel();
            }
        });

        //create and show dialog
        builder.create().show();
    }

    private void EditGroupName(final String groupName_str) {

        final DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("group_title", groupName_str);

        groupRef.updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Toast.makeText(getApplicationContext(), "Group name changed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void ChangeGroupImage(Uri image_rui) {
        //progressDialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Sending image.....");
        progressDialog.setMessage("few seconds left");

        final String timeStamp = "" + System.currentTimeMillis();
        String fileNameAndPath = "group_profile_images/"+"groupImage_"+timeStamp;

        //get bitmap from image uri
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_rui);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();//this will convert image to bytes
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        //image uploaded
                        progressDialog.dismiss();

                        //get url of uploaded image
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUri = uriTask.getResult().toString();

                        if (uriTask.isSuccessful()){
                            //add image uri and other info to database
                            DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference().child("Groups")
                                    .child(groupid);

                            //setup required data
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("group_image", downloadUri);

                            databaseReference.updateChildren(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(GroupChatActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });

    }

    DatabaseReference ref;
    private void online_status(String online_status){
        ref = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("online_status", online_status);

        ref.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        online_status("online");
    }

    @Override
    protected void onResume() {
        super.onResume();
        online_status("online");
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        String timestamp = String.valueOf(System.currentTimeMillis());
//
//        ref.removeEventListener(seenListener);
//        online_status(timestamp);
//    }


    private class GroupUsersAdapter extends RecyclerView.Adapter<GroupUsersAdapter.ViewHolder> {

        private Context mContext;
        private List<Group> mUsers;

        public GroupUsersAdapter(Context mContext, List<Group> mUsers){
            this.mUsers = mUsers;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {


            final Group group = mUsers.get(position);

            getUserInfo(holder.profile_image, holder.username, group.getGroup_userid());

            holder.profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", group.getGroup_userid());
                    editor.apply();

                    ((FragmentActivity)mContext).startActivity(new Intent(mContext, UserProfileActivity.class));
                    Animatoo.animateZoom(mContext);
                }
            });

            holder.UserchatLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // show remove message confirm dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Remove User");
                    builder.setMessage("Are you sure to remove user from group?");
                    //remove button
                    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        RemoveUser(position);

                        }
                    });
                    //cancel remove button
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
        }

    private void RemoveUser(int position) {
        String userint = mUsers.get(position).getGroup_userid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupid).child("group_users");
        Query query = dbRef.orderByChild("group_userid").equalTo(userint);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //To remove the message completly from chat
                    ds.getRef().removeValue();

                    Toast.makeText(mContext, "User removed.....", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Grouplist")
                .child(userint);
        Query Gquery = ref.orderByChild("groupid").equalTo(groupid);
        Gquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //To remove the message completly from chat
                    ds.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        @Override
        public int getItemCount() {
            return mUsers.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{

            public TextView username;
            public ImageView profile_image;
            public RelativeLayout UserchatLayout; //for click listner to show delete

            public ViewHolder(View itemView) {
                super(itemView);

                username = itemView.findViewById(R.id.username);
                profile_image = itemView.findViewById(R.id.image_profile);
                UserchatLayout = itemView.findViewById(R.id.comment_Layout);
            }
        }

        private void getUserInfo(final ImageView Profile_image, final TextView username, String group_userid){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(group_userid);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(getApplicationContext())
                            .load(user.getImageurl())
                            .thumbnail(0.1f)
                            .into(Profile_image);
                    username.setText(user.getUsername());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        public static  final int MSG_TYPE_LEFT = 0;
        public static  final int MSG_TYPE_RIGHT = 1;

        private Context mContext;
        private List<Chat> mChat;
        private String imageurl;
        private String username;
        private String id;

        FirebaseUser fuser;

        public MessageAdapter(Context mContext, List<Chat> mChat, String imageurl, String username, String id){
            this.mChat = mChat;
            this.mContext = mContext;
            this.imageurl = imageurl;
            this.username = username;
            this.id = id;

        }

        @NonNull
        @Override
        public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == MSG_TYPE_RIGHT) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_right, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, final int position) {

            Chat chat = mChat.get(position);

            String timeStamp = mChat.get(position).getTimestamp();
            String type = mChat.get(position).getType();

            //converting time stamp to dd/mm/yyyy hh:mm am/pm
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(Long.parseLong(timeStamp));
            String dateTime = DateFormat.format("hh:mm aa, dd/MM", cal).toString();

            holder.txt_date.setText(dateTime);



            holder.txt_seen.setVisibility(View.GONE);

            if (chat.getSender().equals(fuser.getUid())){
                holder.username.setVisibility(View.GONE);
            }
            else {
                holder.username.setVisibility(View.VISIBLE);
            }


            if (type.equals("text")){
                //text message
                holder.show_message.setVisibility(View.VISIBLE);
                holder.show_image.setVisibility(View.GONE);

                holder.show_message.setText(chat.getMessage());
            }
            else {
                //image
                holder.show_message.setVisibility(View.GONE);
                holder.show_image.setVisibility(View.VISIBLE);

                Glide.with(mContext)
                        .load(chat.getMessage())
                        .placeholder(R.drawable.placeholder)
                        .thumbnail(0.1f)
                        .into(holder.show_image);
            }

            if (imageurl.equals("")){
                holder.profile_image.setImageResource(R.drawable.profile_bg);
            } else {
                Glide.with(mContext)
                        .load(imageurl)
                        .placeholder(R.drawable.profile_bg)
                        .thumbnail(0.1f)
                        .into(holder.profile_image);
            }

            publisherInfo(holder.profile_image, holder.username,chat.getSender());

            holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // show delete message confirm dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Delete");
                    builder.setMessage("Are you sure to delete this message?");
                    //delete button
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            deleteMessage(position);

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

        }

        private void deleteMessage(int position) {
            final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String msgTimeStamp = mChat.get(position).getTimestamp();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Groups")
                    .child(groupid).child("messages");
            Query query = dbRef.orderByChild("timestamp").equalTo(msgTimeStamp);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        if (ds.child("sender").getValue().equals(myUID)){

                            //To remove the message completly from chat
                            ds.getRef().removeValue();

                            Toast.makeText(mContext, "message deleted.....", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            Toast.makeText(mContext, "You can delete only your messages....", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mChat.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{
            public TextView username;
            public EmoticonTextView show_message;
            public ImageView profile_image, show_image;
            public TextView txt_date, txt_seen;
            public RelativeLayout messageLayout; //for click listner to show delete

            public ViewHolder(View itemView) {
                super(itemView);

                username = itemView.findViewById(R.id.username);
                show_message = itemView.findViewById(R.id.show_message);
                show_image = itemView.findViewById(R.id.show_image);
                profile_image = itemView.findViewById(R.id.profile_image);
                txt_date = itemView.findViewById(R.id.txt_date);
                messageLayout = itemView.findViewById(R.id.messageLayout);
                txt_seen = itemView.findViewById(R.id.txt_seen);
            }
        }

        @Override
        public int getItemViewType(int position) {
            fuser = FirebaseAuth.getInstance().getCurrentUser();
            if (mChat.get(position).getSender().equals(fuser.getUid())){
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }

        private void publisherInfo(final ImageView image_profile, final TextView username, final String userid){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userid);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(mContext)
                            .load(user.getImageurl())
                            .placeholder(R.drawable.profile_bg)
                            .thumbnail(0.1f)
                            .into(image_profile);
                    username.setText(user.getUsername());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}

