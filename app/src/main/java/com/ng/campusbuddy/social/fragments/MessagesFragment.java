package com.ng.campusbuddy.social.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.messaging.chat.ChatListAdapter;
import com.ng.campusbuddy.social.messaging.chat.Chatlist;
import com.ng.campusbuddy.social.messaging.chat.NewChatActivity;
import com.ng.campusbuddy.social.messaging.group.Group;
import com.ng.campusbuddy.social.messaging.group.GroupListAdapter;
import com.ng.campusbuddy.social.messaging.group.Grouplist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class MessagesFragment extends Fragment {

    ImageButton Chat_tab, GroupChat_tab;

    RecyclerView Chats_recycler;
    private List<Chatlist> usersList;
    private ChatListAdapter userAdapter;

    private List<Group> mGroup;
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


    private void AddInit() {
        final boolean[] isFABOpen = new boolean[1];


        FloatingActionButton message_fab = view.findViewById(R.id.message_fab);
        final FloatingActionButton chat_fab = view.findViewById(R.id.add_chat_fab);
        final FloatingActionButton group_fab = view.findViewById(R.id.add_group_fab);
        final FloatingActionButton counsel_fab = view.findViewById(R.id.counsel_fab);

        message_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isFABOpen[0]){
                    showFABMenu();
                }
                else {
                    closeFABMenu();
                }
            }

            private void showFABMenu() {
                isFABOpen[0] = true;

                chat_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_115));
                group_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_145));
                counsel_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_175));

            }

            private void closeFABMenu() {
                isFABOpen[0] = false;

                chat_fab.animate().translationY(0);
                group_fab.animate().translationY(0);
                counsel_fab.animate().translationY(0);
            }
        });

        chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser ID = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent = new Intent(getContext(), NewChatActivity.class);
                intent.putExtra("id", ID.getUid());
                startActivity(intent);

                Toast.makeText(getContext(), "Start new chat", Toast.LENGTH_SHORT).show();
            }
        });

        group_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        counsel_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Chat with a counselor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CreateNewGroup(final String groupName_str) {

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        final String group_id = groupRef.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("groupid", group_id);
        hashMap.put("group_image", "");
        hashMap.put("group_title", groupName_str);
        hashMap.put("group_users", "");
        hashMap.put("group_creator", fuser.getUid());

        groupRef.child(group_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(getContext(), groupName_str+ " has been created", Toast.LENGTH_SHORT).show();

                    //add creator to group user list
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups")
                            .child(group_id).child("group_users").child(fuser.getUid());
                    ref.child("group_userid").setValue(fuser.getUid());

                    //add to message fragment
                    final DatabaseReference Ref1 = FirebaseDatabase.getInstance().getReference("Grouplist")
                            .child(fuser.getUid())
                            .child(group_id);

                    Ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()){
                                Ref1.child("groupid").setValue(group_id);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
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

                Collections.reverse(usersList);
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
        GroupChats_recycler.setLayoutManager(mLayoutManager);
        usersGroupList = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Grouplist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersGroupList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Grouplist grouplist = snapshot.getValue(Grouplist.class);
                    usersGroupList.add(grouplist);
                }

                Collections.reverse(usersGroupList);
                GroupList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void GroupList() {
         mGroup = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroup.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Group group = snapshot.getValue(Group.class);
                    for (Grouplist grouplist : usersGroupList){
                        if (group.getGroupid().equals(grouplist.getGroupid())){
                            mGroup.add(group);
                        }
                    }
                }
                groupListAdapter = new GroupListAdapter(getContext(), mGroup);
                GroupChats_recycler.setAdapter(groupListAdapter);
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
                        assert user != null;
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


}
