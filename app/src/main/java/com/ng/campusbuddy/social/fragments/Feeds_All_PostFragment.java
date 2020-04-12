package com.ng.campusbuddy.social.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.widget.PullRefreshLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.post.AllPostAdapter;
import com.ng.campusbuddy.social.post.Post;

import java.util.ArrayList;
import java.util.List;

public class Feeds_All_PostFragment extends Fragment {
    View view;

    private RecyclerView UsersPostrecyclerView;
    private AllPostAdapter AllpostAdapter;
    private List<Post> UserpostList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feeds__all__post, container, false);



        Refresh();
        loadAllPosts();

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
                        loadAllPosts();
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

    private void loadAllPosts() {
        UsersPostrecyclerView = view.findViewById(R.id.all_post_recycler);
        UsersPostrecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager uLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        uLayoutManager.setReverseLayout(true);
        UsersPostrecyclerView.setLayoutManager(uLayoutManager);
        UserpostList = new ArrayList<>();
        AllpostAdapter = new AllPostAdapter(getContext(), UserpostList);
        UsersPostrecyclerView.setAdapter(AllpostAdapter);


        //path of all posts
        final DatabaseReference UserPostref = FirebaseDatabase.getInstance().getReference("Posts");
        //get all data from this ref
        UserPostref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserpostList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    Post post = ds.getValue(Post.class);

                    UserpostList.add(post);

                }

                AllpostAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
