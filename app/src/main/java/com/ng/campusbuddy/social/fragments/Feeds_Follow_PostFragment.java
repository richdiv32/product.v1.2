package com.ng.campusbuddy.social.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.AD;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.social.post.PostAdapter;
import com.ng.campusbuddy.social.post.story.Story;
import com.ng.campusbuddy.social.post.story.StoryAdapter;
import com.ng.campusbuddy.tools.AdInfoActivity;
import com.ng.campusbuddy.utils.CustomRecyclerView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class Feeds_Follow_PostFragment extends Fragment {
    Context context;
    View view;


    private CustomRecyclerView recyclerView;
    private PostAdapter postAdapter;
//    private List<Post> postList;

    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;


    private List<String> followingList;

    // The number of native ads to load.
    public static final int NUMBER_OF_ADS = 1;
    public static final int ITEM_PER_AD = 2;

    // The AdLoader used to load ads.
    private AdLoader adLoader;

    // List of MenuItems and native ads that populate the RecyclerView.
    private List<Object> post_ad_list = new ArrayList<>();

    // List of native ads that have been successfully loaded.
    private List<UnifiedNativeAd> mNativeAds = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //get fragment context
        context = container.getContext();
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_feeds__follow__post, container, false);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(getContext(), getActivity().getString(R.string.App_ID));


        ADimageslider();
        checkFollowing();
        loadNativeAds();

//        if (savedInstanceState == null) {
//
//            // Update the RecyclerView item's list with menu items.
//            checkFollowing();
//            // Update the RecyclerView item's list with native ads.
//            loadNativeAds();
//        }

//        loadNativeAds();


        return view;
    }

    private void insertAdsInPostItems() {
        Log.d("Feeds", "Getting Ads");
        if (mNativeAds.size() <= 0) {
            return;
        }

        int offset = (post_ad_list.size() / mNativeAds.size()) + 1;
        int index = 0;
        for (UnifiedNativeAd ad : mNativeAds) {
            post_ad_list.add(index, ad);
            index = index + offset;

            Log.d("Feeds", "Adding Ads");
        }
    }

    private void loadNativeAds() {

        AdLoader.Builder builder = new AdLoader.Builder(context,context.getString(R.string.native_unit_id));
        adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        // A native ad loaded successfully, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        mNativeAds.add(unifiedNativeAd);
                        if (!adLoader.isLoading()) {
                            insertAdsInPostItems();
                        }
                    }
                }).withAdListener(
                new AdListener() {
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
                                + " load another.");
                        if (!adLoader.isLoading()) {
                            insertAdsInPostItems();
                        }
                    }
                }).build();

        // Load the Native ads.
        adLoader.loadAds(new AdRequest.Builder().build(), NUMBER_OF_ADS);
    }

    private void ADimageslider() {
        final ArrayList<AD> sliderList = new ArrayList<>();
        final SliderView sliderView = view.findViewById(R.id.ADsSlider);
        final SliderAdapterADs adapter = new SliderAdapterADs(getActivity(), sliderList);
        sliderView.setSliderAdapter(adapter);

        DatabaseReference Adref= FirebaseDatabase.getInstance().getReference().child("ADs").child("Social")
                .child("Slides");

        Adref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sliderList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    AD ad = ds.getValue(AD.class);
                    sliderList.add(ad);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.RED);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
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

//                loadNativeAds();
                readPosts();
                readStory();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readPosts(){
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setEmptyView(view.findViewById(R.id.empty_item));
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
//        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity(), post_ad_list);
        recyclerView.setAdapter(postAdapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                post_ad_list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for (String id : followingList){
                        if (post.getPublisher().equals(id)){
                            post_ad_list.add(post);

                        }
                    }
                }

//                loadNativeAds();
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readStory(){
        recyclerView_story = view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView_story.setLayoutManager(linearLayoutManager);
        storyList = new ArrayList<>();
        storyAdapter = new StoryAdapter(getContext(), storyList);
        recyclerView_story.setAdapter(storyAdapter);

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



    private class SliderAdapterADs  extends SliderViewAdapter<SliderAdapterADs.SliderAdapterVH> {

        private Context context;
        private ArrayList<AD> sliderlist;

        public SliderAdapterADs(Context context, ArrayList<AD> sliderlist) {
            this.context = context;
            this.sliderlist = sliderlist;
        }

        @Override
        public SliderAdapterADs.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider_layout, null);
            return new SliderAdapterADs.SliderAdapterVH(inflate);
        }


        @Override
        public void onBindViewHolder(SliderAdapterADs.SliderAdapterVH viewHolder, final int position) {

            final AD ad = sliderlist.get(position);


            viewHolder.textViewDescription.setText(ad.getTitle());


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AdInfoActivity.class);
                    intent.putExtra("Ad_id", ad.getId());
                    intent.putExtra("context", "Social");
                    context.startActivity(intent);
                }
            });


            Glide.with(context)
                    .load(ad.getImage())
                    .thumbnail(0.1f)
                    .into(viewHolder.imageViewBackground);

        }



        @Override
        public int getCount() {
            return sliderlist.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            public View itemView;
            public ImageView imageViewBackground;
            public TextView textViewDescription;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
                textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
                this.itemView = itemView;
            }
        }


    }
}
