package com.ng.campusbuddy.social.messaging.room;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
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
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.messaging.MessageAdapter;
import com.ng.campusbuddy.social.messaging.chat.Chat;
import com.ng.campusbuddy.social.messaging.group.GroupChatActivity;
import com.ng.campusbuddy.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomActivity extends AppCompatActivity {
    CircleImageView image;
    TextView title, count;

    FirebaseUser fuser;
    DatabaseReference reference;
    ValueEventListener seenListener;

    ImageButton btn_send, back_btn, attach_btn;
    EmoticonEditText text_send;
    EmoticonButton emoji_btn;

    RecyclerView message_recycler;
    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerView;

    Intent intent;

    String room_id,roomlist_id, userid;

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
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        image = findViewById(R.id.profile_image);
        title = findViewById(R.id.title);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);
        count = findViewById(R.id.description);
        attach_btn = findViewById(R.id.btn_attachment);
        emoji_btn = findViewById(R.id.emoji_btn);

        intent = getIntent();

        roomlist_id = intent.getStringExtra("roomlist_id");
        room_id = intent.getStringExtra("room_id");
        fuser = FirebaseAuth.getInstance().getCurrentUser();



        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = text_send.getText().toString().trim();
                if (!msg.equals("")){
                    sendMessage(fuser.getUid(), msg);
                } else {
                    Toast.makeText(RoomActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
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

        InitRoomInfo();
        SetupNavigationDrawer();
        readMesagges();
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
//        if (!mEmoticonKeyboardFragment.handleBackPressed())
        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        frameLayout.setVisibility(View.GONE);

        super.onBackPressed();
    }

    private void InitRoomInfo() {
        reference = FirebaseDatabase.getInstance().getReference().child("Rooms")
                .child(roomlist_id);

        reference.child(room_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);

                title.setText(room.getTitle_chatroom());

                if (room.getImage_chatroom().equals("")){
                    image.setImageResource(R.drawable.placeholder);
                }
                else {

                    Picasso.get()
                            .load(room.getImage_chatroom())
                            .placeholder(R.drawable.placeholder)
                            .into(image);
                }


//                readMesagges(fuser.getUid(), group.getGroup_userid(), user.getImageurl(),);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String message){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "text");

        reference.child("Rooms")
                .child(roomlist_id)
                .child(room_id)
                .child("messages").push().setValue(hashMap);

    }

    private void readMesagges(){
        message_recycler = findViewById(R.id.recycler_view);
        message_recycler.setHasFixedSize(true);
        message_recycler.setLayoutManager(new LinearLayoutManager(this));
        mchat = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Rooms").child(roomlist_id)
                .child(room_id).child("messages");
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

                    messageAdapter = new MessageAdapter(RoomActivity.this, mchat, user.getImageurl(), user.getUsername(), user.getId());
                    message_recycler.setAdapter(messageAdapter);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

        final DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.nav_drawer);
        View headerview=navigationView.getHeaderView(0);
        RelativeLayout navigationHeader = headerview.findViewById(R.id.nav_header_container);
        ImageButton EditButton = headerview.findViewById(R.id.edit_button);
        ImageButton AddButton = headerview.findViewById(R.id.add_user);

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



        EditButton.setVisibility(View.GONE);
        AddButton.setVisibility(View.GONE);
        Creator.setVisibility(View.GONE);

        reference = FirebaseDatabase.getInstance().getReference().child("Rooms")
                .child(roomlist_id);

        reference.child(room_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);

                Title.setText(room.getTitle_chatroom());

                if (room.getImage_chatroom().equals("")){
                    Group_image.setImageResource(R.drawable.chat_bg);
                }
                else {

                    Picasso.get()
                            .load(room.getImage_chatroom())
                            .placeholder(R.drawable.placeholder)
                            .into(Group_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                return new MessageAdapter.ViewHolder(view);
            } else {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_left, parent, false);
                return new MessageAdapter.ViewHolder(view);
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
            holder.username.setText(username);




            if (id.equals(fuser.getUid())){
                holder.username.setVisibility(View.GONE);
            }
            else {
                holder.username.setVisibility(View.VISIBLE);
            }

            holder.username.setVisibility(View.GONE);
            holder.profile_image.setVisibility(View.GONE);

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

                Picasso.get()
                        .load(chat.getMessage())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.show_image);
            }

            if (imageurl.equals("")){
                holder.profile_image.setImageResource(R.drawable.profile_bg);
            } else {
                Glide.with(mContext).load(imageurl).into(holder.profile_image);
            }


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
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Rooms").child(roomlist_id)
                    .child(room_id).child("messages");
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
            public TextView txt_date;
            public RelativeLayout messageLayout; //for click listner to show delete

            public ViewHolder(View itemView) {
                super(itemView);

                username = itemView.findViewById(R.id.username);
                show_message = itemView.findViewById(R.id.show_message);
                show_image = itemView.findViewById(R.id.show_image);
                profile_image = itemView.findViewById(R.id.profile_image);
                txt_date = itemView.findViewById(R.id.txt_date);
                messageLayout = itemView.findViewById(R.id.messageLayout);
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
    }


}

