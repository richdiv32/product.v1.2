package com.ng.campusbuddy.social.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.FollowersActivity;
import com.ng.campusbuddy.social.message.ChatListAdapter;
import com.ng.campusbuddy.social.message.Chatlist;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.message.GroupListAdapter;
import com.ng.campusbuddy.social.message.Grouplist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MessagesFragment extends Fragment {

    ImageButton Chat_tab, GroupChat_tab;

    RecyclerView Chats_recycler;
    private List<Chatlist> usersList;
    private ChatListAdapter userAdapter;

    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    RecyclerView GroupChats_recycler;
    private List<Grouplist> usersGroupList;
    private GroupListAdapter groupListAdapter;

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messages, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();


//        updateToken(FirebaseInstanceId.getInstance().getToken());



        Init();
        AddInit();
        LoadChatlist();
        LoadGrouplist();

        Chats_recycler.setVisibility(View.VISIBLE);
        GroupChats_recycler.setVisibility(View.GONE);

        return view;
    }




    private void Init() {
        Chat_tab = view.findViewById(R.id.tab_chat);
        GroupChat_tab = view.findViewById(R.id.tab_group_chat);

        Chat_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupChats_recycler.setVisibility(View.GONE);
                Chats_recycler.setVisibility(View.VISIBLE);

                chatList();
            }
        });

        GroupChat_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupChats_recycler.setVisibility(View.VISIBLE);
                Chats_recycler.setVisibility(View.GONE);

//                GroupList();
            }
        });
    }

    private void AddInit(){
        FloatingActionButton NewChat = view.findViewById(R.id.add_chat_fab);
        NewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser ID = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id", ID.getUid());
                intent.putExtra("title", "following");
                startActivity(intent);

                Toast.makeText(getContext(), "Start new chat", Toast.LENGTH_SHORT).show();

            }
        });

        FloatingActionButton NewGroup = view.findViewById(R.id.add_group_fab);
        NewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Create new group", Toast.LENGTH_SHORT).show();


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Create New Group");

                final EditText groupName = new EditText(getContext());
                groupName.setHint("Type group name... ");
                builder.setView(groupName);


                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String groupName_str = groupName.getText().toString();

                        if (TextUtils.isEmpty(groupName_str)){
                            Toast.makeText(getContext(), "You can't create a group without a name", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Group Created", Toast.LENGTH_SHORT).show();

                            CreateNewGroup(groupName_str);
                        }

                        GroupChats_recycler.setVisibility(View.VISIBLE);
                        Chats_recycler.setVisibility(View.GONE);

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

    private void CreateNewGroup(final String groupName_str) {

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Grouplist")
                .child(fuser.getUid());

        String group_id = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("groupid", group_id);
        hashMap.put("groupimage", "");
        hashMap.put("title", groupName_str);
        hashMap.put("users", "");
        hashMap.put("creator", fuser.getUid());

        groupRef.child(group_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                            Toast.makeText(getContext(), groupName_str+ " has been created", Toast.LENGTH_SHORT).show();
                        }
            }
        });
    }

//    private void updateToken(String token){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1 = new Token(token);
//        reference.child(fuser.getUid()).setValue(token1);
//    }

    private void LoadChatlist() {
        Chats_recycler = view.findViewById(R.id.recycler_view_chat);
        Chats_recycler.setHasFixedSize(true);
        Chats_recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        usersList = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadGrouplist() {
        GroupChats_recycler = view.findViewById(R.id.recycler_view_group_chat);
        GroupChats_recycler.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
        mLayoutManager.setReverseLayout(true);
        GroupChats_recycler.setLayoutManager(mLayoutManager);
        usersGroupList = new ArrayList<>();
        groupListAdapter = new GroupListAdapter(getContext(), usersGroupList);
        GroupChats_recycler.setAdapter(groupListAdapter);


        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Grouplist")
                .child(fuser.getUid());

        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersGroupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Grouplist grouplist = snapshot.getValue(Grouplist.class);
                    usersGroupList.add(grouplist);
                }

                groupListAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });






    }

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Chatlist chatlist : usersList){
                        if (user.getId().equals(chatlist.getId())){
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new ChatListAdapter(getContext(), mUsers, true);
                Chats_recycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void GroupList() {

//        usersGroupList = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("Grouplist");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersGroupList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Grouplist grouplist = snapshot.getValue(Grouplist.class);
//
//                    usersGroupList.add(grouplist);
//                }
//                groupListAdapter = new GroupListAdapter(getContext(), usersGroupList );
//                GroupChats_recycler.setAdapter(groupListAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

}
