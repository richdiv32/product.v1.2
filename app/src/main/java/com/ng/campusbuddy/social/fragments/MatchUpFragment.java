package com.ng.campusbuddy.social.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.adapter.MatchArrayAdapter;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.match.Match;
import com.ng.campusbuddy.social.match.MatchesActivity;
import com.ng.campusbuddy.social.messaging.chat.ChatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;


public class MatchUpFragment extends Fragment {
    View view;

    private Match cards_data[];
    private MatchArrayAdapter arrayAdapter;
    private int i;

    private FirebaseAuth mAuth;

    private String currentUId;

    private DatabaseReference usersDb;


    ListView listView;
    List<Match> rowItems;

    ImageButton Love_btn;

    Match obj;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_match_up, container, false);
        if (restorePrefData()){

        }
        else {
            TapTarget();
        }


        Love_btn = view.findViewById(R.id.love_btn);
        Love_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MatchesActivity.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });


        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        currentUId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<>();

        arrayAdapter = new MatchArrayAdapter(getActivity(), R.layout.item_match_up, rowItems );

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) view.findViewById(R.id.match_card);

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

                obj = (Match) dataObject;
                userId= obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
//                Toast.makeText(getActivity(), "NOPE", Toast.LENGTH_SHORT).show();

                //inflate layout for dialog
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                ImageView Image = view.findViewById(R.id.toast_img);
                TextView Text = view.findViewById(R.id.toast_txt);

                Text.setText("Nope");
                Image.setImageResource(R.drawable.emoji_samsung_1f60f);

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.BOTTOM, 0 , 120);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Match obj = (Match) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yes").child(currentUId).setValue(true);
                isConnectionMatch(userId);


                //inflate layout for dialog
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                ImageView Image = view.findViewById(R.id.toast_img);
                TextView Text = view.findViewById(R.id.toast_txt);

                Text.setText("Yes");
                Image.setImageResource(R.drawable.emoji_samsung_1f60d);

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.BOTTOM, 0 , 120);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

//                rowItems.toArray();
//                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                //inflate layout for dialog
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                ImageView Image = view.findViewById(R.id.toast_img);
                TextView Text = view.findViewById(R.id.toast_txt);

                Text.setText("I like you too");
                Image.setImageResource(R.drawable.emoji_samsung_1f496);

                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.BOTTOM, 0 , 120);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(view);
                toast.show();
            }
        });

        return view;
    }

    private void TapTarget() {
        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(view.findViewById(R.id.love_btn), "Matches", "See your matches and start the conversation with your pair")
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorPrimary),
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        savePrefsData();

                        view.dismiss(true);
                    }
                });
    }


    private void addNotification(String userId){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userid", firebaseUser.getUid());
        hashMap.put("text", "is matched with you");
        hashMap.put("postid", "");
        hashMap.put("type", "match");

        reference.push().setValue(hashMap);
    }

    private void isConnectionMatch(final String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    showPopup(userId);


//                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(userId).child("connections").child("matches").child(currentUId).child("userId").setValue(currentUId);
                    usersDb.child(currentUId).child("connections").child("matches").child(userId).child("userId").setValue(userId);
                    addNotification(userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String userSex;
    private String oppositeUserSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDb = usersDb.child(user.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("gender").getValue() != null){
                        userSex = dataSnapshot.child("gender").getValue().toString();
                        switch (userSex){
                            case "male":
                                oppositeUserSex = "female";
                                break;
                            case "female":
                                oppositeUserSex = "male";
                                break;
                        }
                        getOppositeSexUsers();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeSexUsers(){
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.child("gender").getValue() != null) {
//                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId)
//                            && !dataSnapshot.child("connections").child("yes").hasChild(currentUId) && dataSnapshot.child("gender")
//                            .getValue().toString().equals(oppositeUserSex)) {
//                        String profileImageUrl = dataSnapshot.child("imageurl").getValue().toString();
//                        Match item = new Match(dataSnapshot.getKey(), dataSnapshot.child("username").getValue().toString(), profileImageUrl);
//                        rowItems.add(item);
//                        arrayAdapter.notifyDataSetChanged();
//                    }
                    if (dataSnapshot.exists() && dataSnapshot.child("gender").getValue().toString().equals(oppositeUserSex)) {
                        String profileImageUrl = dataSnapshot.child("imageurl").getValue().toString();
                        Match item = new Match(dataSnapshot.getKey(), dataSnapshot.child("username").getValue().toString(), profileImageUrl);
                        rowItems.add(item);
                        Collections.shuffle(rowItems);//randomize the array list
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void showPopup(final String userId) {
        final  View popup_view = LayoutInflater.from(getContext()).inflate(R.layout.item_matched, null);
        final PopupWindow popupWindow = new PopupWindow(popup_view, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
//        popupWindow.dismiss();
        popupWindow.showAtLocation(popup_view, Gravity.CENTER, 0, 0);


        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        final CircleImageView myImage = popup_view.findViewById(R.id.image_profile);
        final CircleImageView userImage = popup_view.findViewById(R.id.image_profile2);
        Button Continue = popup_view.findViewById(R.id.continue_btn);
        Button Chat = popup_view.findViewById(R.id.chat_btn);
        final TextView Paired = popup_view.findViewById(R.id.paired_text);

        DatabaseReference myInfo = FirebaseDatabase.getInstance().getReference();
        myInfo.child("Users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);

                    Glide.with(getActivity())
                            .load(user.getImageurl())
                            .thumbnail(0.1f)
                            .into(myImage);

//                    Username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference UserInfo = FirebaseDatabase.getInstance().getReference();
        UserInfo.child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);

                    Glide.with(getActivity())
                            .load(user.getImageurl())
                            .thumbnail(0.1f)
                            .into(userImage);

                    Paired.setText("You and " + user.getUsername() + " like each other");

//                    Username.setText(user.getUsername());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent message = new Intent(getContext(), ChatActivity.class);
                message.putExtra("userid", userId);
                getContext().startActivity(message);
                Animatoo.animateSlideUp(getContext());
                popupWindow.dismiss();
            }
        });

        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });

    }

    private boolean restorePrefData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isTapTargetClickedBefore = pref.getBoolean("isTapOpnend",false);
        return  isTapTargetClickedBefore;

    }
    private void savePrefsData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isTapOpnend",true);
        editor.apply();

    }



}
