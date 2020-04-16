package com.ng.campusbuddy.social.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.ng.campusbuddy.utils.CustomRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Messages_ChatFragment extends Fragment {
    View view;
    Context context;

    CustomRecyclerView Chats_recycler;
    private List<Chatlist> usersList;
    private ChatListAdapter userAdapter;


    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        view = inflater.inflate(R.layout.fragment_messages__chat, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Refresh();
        LoadChatlist();
        return view;
    }

    private void Refresh() {
        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //implement Handler to wait for 3 seconds and then update UI
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //cancel the visual indication of a refresh
                        swipeRefreshLayout.setRefreshing(false);
                        LoadChatlist();
                    }
                }, 3000);
            }
        });
//        final PullRefreshLayout pullRefreshLayout = view.findViewById(R.id.refreshLayout);
//        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                //implement Handler to wait for 3 seconds and then update UI
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //cancel the visual indication of a refresh
//                        pullRefreshLayout.setRefreshing(false);
//                        loadAllPosts();
//                    }
//                }, 3000);
//            }
//        });
    }

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

                Collections.reverse(usersList);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
