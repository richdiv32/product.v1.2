package com.ng.campusbuddy.social;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.adapter.ChatUserAdapter;
import com.ng.campusbuddy.model.Chatlist;
import com.ng.campusbuddy.model.User;

import java.util.ArrayList;
import java.util.List;


public class MessagesFragment extends Fragment {

    ImageButton Chat_tab, GroupChat_tab;

    RecyclerView Chats_recycler, GroupChats_recycler;

    private ChatUserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<Chatlist> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        Chat_tab = view.findViewById(R.id.tab_chat);
        GroupChat_tab = view.findViewById(R.id.tab_group_chat);

        GroupChats_recycler = view.findViewById(R.id.recycler_view_group_chat);



        Chats_recycler = view.findViewById(R.id.recycler_view_chat);
        Chats_recycler.setHasFixedSize(true);
        Chats_recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

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

//        updateToken(FirebaseInstanceId.getInstance().getToken());

        Chats_recycler.setVisibility(View.VISIBLE);
        GroupChats_recycler.setVisibility(View.GONE);

        Init();

        return view;
    }

    private void Init() {

        Chat_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupChats_recycler.setVisibility(View.GONE);
                Chats_recycler.setVisibility(View.VISIBLE);
            }
        });

        GroupChat_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupChats_recycler.setVisibility(View.VISIBLE);
                Chats_recycler.setVisibility(View.GONE);
            }
        });
    }

//    private void updateToken(String token){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1 = new Token(token);
//        reference.child(fuser.getUid()).setValue(token1);
//    }

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
                userAdapter = new ChatUserAdapter(getContext(), mUsers, true);
                Chats_recycler.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
