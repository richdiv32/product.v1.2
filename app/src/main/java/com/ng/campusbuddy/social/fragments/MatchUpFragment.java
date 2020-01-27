package com.ng.campusbuddy.social.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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
import com.ng.campusbuddy.education.EducationActivity;
import com.ng.campusbuddy.model.Match;
import com.ng.campusbuddy.social.matches.MatchesActivity;

import java.util.ArrayList;
import java.util.List;


public class MatchUpFragment extends Fragment {

    private Match cards_data[];
    private MatchArrayAdapter arrayAdapter;
    private int i;

    private FirebaseAuth mAuth;

    private String currentUId;

    private DatabaseReference usersDb;


    ListView listView;
    List<Match> rowItems;

    ImageButton Love_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_match_up, container, false);


        Love_btn = view.findViewById(R.id.love_btn);

        Love_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMatches(view);
            }
        });


        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        checkUserSex();

        rowItems = new ArrayList<Match>();

        arrayAdapter = new MatchArrayAdapter(getActivity(), R.layout.match_up_item, rowItems );

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

                Match obj = (Match) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("nope").child(currentUId).setValue(true);
                Toast.makeText(getActivity(), "NOPE", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Match obj = (Match) dataObject;
                String userId = obj.getUserId();
                usersDb.child(userId).child("connections").child("yes").child(currentUId).setValue(true);
                isConnectionMatch(userId);
                Toast.makeText(getActivity(), "YES", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Item Clicked", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = usersDb.child(currentUId).child("connections").child("yes").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Toast.makeText(getActivity(), "Matched", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();

                    usersDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUId).child("ChatId").setValue(key);
                    usersDb.child(currentUId).child("connections").child("matches").child(dataSnapshot.getKey()).child("ChatId").setValue(key);
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
                    if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUId)
                            && !dataSnapshot.child("connections").child("yes").hasChild(currentUId) && dataSnapshot.child("gender")
                            .getValue().toString().equals(oppositeUserSex)) {
                        String profileImageUrl = dataSnapshot.child("imageurl").getValue().toString();
//                        if (!dataSnapshot.child("imageurl").getValue().equals("default")) {
//                            profileImageUrl = dataSnapshot.child("imageurl").getValue().toString();
//                        }
                        Match item = new Match(dataSnapshot.getKey(), dataSnapshot.child("username").getValue().toString(), profileImageUrl);
                        rowItems.add(item);
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


    public void goToMatches(View view) {
        Intent intent = new Intent(getActivity(), MatchesActivity.class);
        startActivity(intent);
        return;
    }


}
