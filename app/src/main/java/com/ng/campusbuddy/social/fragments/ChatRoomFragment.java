package com.ng.campusbuddy.social.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.messaging.room.Room;
import com.ng.campusbuddy.social.messaging.room.RoomsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatRoomFragment extends Fragment {

    View view;

    RecyclerView Room_recycler;
    private List<Room> roomList;
    private RoomsListAdapter roomsListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat_room, container, false);


        LoadRoomlist();

        update();
        return view;
    }

    private void update() {
        TextView update = view.findViewById(R.id.title);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Create New Room");

                final EditText groupName = new EditText(getContext());
                groupName.setHint("Type room name... ");
                builder.setView(groupName);


                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String groupName_str = groupName.getText().toString();

                        if (TextUtils.isEmpty(groupName_str)){
                            Toast.makeText(getContext(), "You can't create a room without a name", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference().child("Roomlist");
                            String room_id = roomRef.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id_chatroom", room_id);
                            hashMap.put("image_chatroom", "");
                            hashMap.put("count", "");
                            hashMap.put("title_chatroom", groupName_str);

                            roomRef.child(room_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(getContext(), groupName_str+ " has been created", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
        });
    }

    private void LoadRoomlist() {
        Room_recycler = view.findViewById(R.id.roomlist_recycler);
        Room_recycler.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        Room_recycler.setLayoutManager(mLayoutManager);
        roomList = new ArrayList<>();
        roomsListAdapter = new RoomsListAdapter(getContext(), roomList);
        Room_recycler.setAdapter(roomsListAdapter);


        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Roomlist");

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                roomList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Room room = snapshot.getValue(Room.class);
                    roomList.add(room);
                }

                roomsListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

}
