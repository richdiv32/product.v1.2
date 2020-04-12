package com.ng.campusbuddy.social.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.ng.campusbuddy.social.messaging.group.Group;
import com.ng.campusbuddy.social.messaging.group.GroupListAdapter;
import com.ng.campusbuddy.social.messaging.group.Grouplist;
import com.ng.campusbuddy.utils.CustomRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Messages_GroupFragment extends Fragment {
    View view;
    Context context;

    CustomRecyclerView GroupChats_recycler;
    private List<Grouplist> usersGroupList;
    private GroupListAdapter groupListAdapter;
    private List<Group> mGroup;

    FirebaseUser fuser;
    DatabaseReference reference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        view = inflater.inflate(R.layout.fragment_messages__group, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Refresh();
        LoadGrouplist();
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
                        LoadGrouplist();
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
}
