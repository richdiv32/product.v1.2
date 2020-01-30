package com.ng.campusbuddy.social.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.post.AllPostAdapter;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.social.post.PostActivity;
import com.ng.campusbuddy.social.post.PostAdapter;
import com.ng.campusbuddy.social.post.story.Story;
import com.ng.campusbuddy.social.post.story.StoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class FeedsFragment extends Fragment {
    View view;

    private RecyclerView UsersPostrecyclerView;
    private AllPostAdapter AllpostAdapter;
    private List<Post> UserpostList;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followingList;

//    ProgressBar progress_circular;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feeds, container, false);

        UsersPostrecyclerView = view.findViewById(R.id.users_post_recycler);
        UsersPostrecyclerView.setHasFixedSize(true);
        LinearLayoutManager uLayoutManager = new LinearLayoutManager(getContext());
        uLayoutManager.setReverseLayout(true);
        uLayoutManager.setStackFromEnd(true);
        UsersPostrecyclerView.setLayoutManager(uLayoutManager);
        UserpostList = new ArrayList<>();
        AllpostAdapter = new AllPostAdapter(getContext(), UserpostList);
        UsersPostrecyclerView.setAdapter(AllpostAdapter);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);


        checkFollowing();
        loadAllPosts();

        ADslider();
        Init();
        PostInit();

        return view;
    }

    private void Init() {

        ImageButton UsersPost = view.findViewById(R.id.users_post);
        ImageButton FollowPost = view.findViewById(R.id.follow_post);
        final ImageView Line_1 = view.findViewById(R.id.line_1);
        final ImageView Line_2 = view.findViewById(R.id.line_2);

        FollowPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_story.setVisibility(View.VISIBLE);
                UsersPostrecyclerView.setVisibility(View.GONE);
                Line_1.setVisibility(View.VISIBLE);
                Line_2.setVisibility(View.GONE);

                checkFollowing();

            }
        });

        UsersPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_story.setVisibility(View.GONE);
                UsersPostrecyclerView.setVisibility(View.VISIBLE);
                Line_1.setVisibility(View.GONE);
                Line_2.setVisibility(View.VISIBLE);

                loadAllPosts();
            }
        });

//        final SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//                //implement Handler to wait for 3 seconds and then update UI
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        //cancel the visual indication of a refresh
//                        swipeRefreshLayout.setRefreshing(false);
//
//                        checkFollowing();
//                        loadAllPosts();
//                    }
//                }, 3000);
//            }
//        });

    }

    private void PostInit() {
        FloatingActionButton post_fab = view.findViewById(R.id.post_fab);

        post_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PostActivity.class));
                Animatoo.animateShrink(getContext());
            }
        });
    }

    private void checkFollowing(){
        followingList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }

                readPosts();
                readStory();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readPosts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : followingList){
                        if (post.getPublisher().equals(id)){
                            postList.add(post);
                        }
                    }
                }

                postAdapter.notifyDataSetChanged();
//                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followingList) {
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                        story = snapshot.getValue(Story.class);
                        if (timecurrent > story.getTimestart() && timecurrent < story.getTimeend()) {
                            countStory++;
                        }
                    }
                    if (countStory > 0){
                        storyList.add(story);
                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadAllPosts() {
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

    private void ADslider() {

        ViewFlipper AdFlipper = view.findViewById(R.id.AD_filpper);
        final ImageView AD1_iv = view.findViewById(R.id.ad_1_iv);
        final ImageView AD2_iv = view.findViewById(R.id.ad_2_iv);
        final ImageView AD3_iv = view.findViewById(R.id.ad_3_iv);
        final ImageView AD4_iv = view.findViewById(R.id.ad_4_iv);
        final ImageView AD5_iv = view.findViewById(R.id.ad_5_iv);
        final ImageView AD6_iv = view.findViewById(R.id.ad_6_iv);
        final ImageView AD7_iv = view.findViewById(R.id.ad_7_iv);
        final ImageView AD8_iv = view.findViewById(R.id.ad_8_iv);
        final ImageView AD9_iv = view.findViewById(R.id.ad_9_iv);
        final ImageView AD10_iv = view.findViewById(R.id.ad_10_iv);
        final ImageView AD11_iv = view.findViewById(R.id.ad_11_iv);
        final ImageView AD12_iv = view.findViewById(R.id.ad_12_iv);

        AdFlipper.setFlipInterval(3000);
        AdFlipper.setAutoStart(true);
        AdFlipper.startFlipping();
        AdFlipper.setInAnimation(getActivity(), android.R.anim.slide_in_left);
        AdFlipper.setOutAnimation(getActivity(), android.R.anim.slide_out_right);


        DatabaseReference Nav_reference = FirebaseDatabase.getInstance().getReference().child("ADs");
        Nav_reference.child("Home").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String Ad1 = dataSnapshot.child("AD_1").getValue().toString();
                    String Ad2 = dataSnapshot.child("AD_2").getValue().toString();
                    String Ad3 = dataSnapshot.child("AD_3").getValue().toString();
                    String Ad4 = dataSnapshot.child("AD_1").getValue().toString();
                    String Ad5 = dataSnapshot.child("AD_3").getValue().toString();
                    String Ad6 = dataSnapshot.child("AD_2").getValue().toString();
                    String Ad7 = dataSnapshot.child("AD_1").getValue().toString();
                    String Ad8 = dataSnapshot.child("AD_2").getValue().toString();
                    String Ad9 = dataSnapshot.child("AD_3").getValue().toString();
                    String Ad10 = dataSnapshot.child("AD_2").getValue().toString();
                    String Ad11 = dataSnapshot.child("AD_1").getValue().toString();
                    String Ad12 = dataSnapshot.child("AD_3").getValue().toString();

                    Glide.with(getActivity()).load(Ad1).into(AD1_iv);
                    Glide.with(getActivity()).load(Ad2).into(AD2_iv);
                    Glide.with(getActivity()).load(Ad3).into(AD3_iv);
                    Glide.with(getActivity()).load(Ad4).into(AD4_iv);
                    Glide.with(getActivity()).load(Ad5).into(AD5_iv);
                    Glide.with(getActivity()).load(Ad6).into(AD6_iv);
                    Glide.with(getActivity()).load(Ad7).into(AD7_iv);
                    Glide.with(getActivity()).load(Ad8).into(AD8_iv);
                    Glide.with(getActivity()).load(Ad9).into(AD9_iv);
                    Glide.with(getActivity()).load(Ad10).into(AD10_iv);
                    Glide.with(getActivity()).load(Ad11).into(AD11_iv);
                    Glide.with(getActivity()).load(Ad12).into(AD12_iv);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
