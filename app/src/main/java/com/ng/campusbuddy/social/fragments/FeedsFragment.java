package com.ng.campusbuddy.social.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.AD;
import com.ng.campusbuddy.social.post.AllPostAdapter;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.social.post.PostActivity;
import com.ng.campusbuddy.social.post.PostAdapter;
import com.ng.campusbuddy.social.post.story.Story;
import com.ng.campusbuddy.social.post.story.StoryAdapter;
import com.ng.campusbuddy.tools.AdInfoActivity;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class FeedsFragment extends Fragment {
    View view;

    private static  final int ITEM_PER_AD = 5;
    private static final String BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";
    private int count = 0;


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

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permissions array
    String[] cameraPermissions;
    String[] storagePermissions;

    //for image
    Uri image_rui = null;

    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feeds, container, false);

        UsersPostrecyclerView = view.findViewById(R.id.users_post_recycler);
        UsersPostrecyclerView.setHasFixedSize(true);
//        LinearLayoutManager uLayoutManager = new LinearLayoutManager(getContext());
//        uLayoutManager.setStackFromEnd(true);
        StaggeredGridLayoutManager uLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        uLayoutManager.setReverseLayout(true);
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

        ADimageslider();
        Init();
        PostInit();
        TapTarget();

        return view;
    }

    private void TapTarget() {
        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(view.findViewById(R.id.post_fab), "Post Your Moments", "share your favourite moments with friends all over the country")
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorPrimary));
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

    private void PostInit() {
        final boolean[] isFABOpen = new boolean[1];

        FloatingActionButton post_fab = view.findViewById(R.id.post_fab);
        final FloatingActionButton post_camera_fab = view.findViewById(R.id.post_camera_fab);
        final FloatingActionButton post_note_fab = view.findViewById(R.id.post_note_fab);
        final FloatingActionButton post_gallery_fab = view.findViewById(R.id.post_gallery_fab);

        post_fab.setOnClickListener(new View.OnClickListener() {
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

                post_note_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_115));
                post_gallery_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_145));
                post_camera_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_175));

            }

            private void closeFABMenu() {
                isFABOpen[0] = false;

                post_note_fab.animate().translationY(0);
                post_gallery_fab.animate().translationY(0);
                post_camera_fab.animate().translationY(0);
            }
        });

        post_camera_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Camera", Toast.LENGTH_SHORT).show();

                //Carmera Clicked
                if (!checkCameraPermission()){
                    requestCameraPermission();
                }
                else {
                    pickFromCamera();
                }
            }
        });

        post_gallery_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Post Image", Toast.LENGTH_SHORT).show();

                //Gallery Clicked
                if (!checkStoragePermission()){
                    requestStoragePermission();
                }
                else {
                    pickFromGallery();
                }

//                startActivity(new Intent(getActivity(), PostActivity.class));
//                Animatoo.animateShrink(getContext());
            }
        });

        post_note_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Post Text", Toast.LENGTH_SHORT).show();

                SharedPreferences.Editor editor = getContext().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("Notes", "note");
                editor.apply();

                Intent intent = new Intent(getContext(), PostActivity.class);
                startActivity(intent);
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

//                            count++;
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

//    private void getBannerAds(){
//        for (int i = 0 ; i < postList.size(); i += ITEM_PER_AD){
//
//            final AdView adView = new AdView(getActivity());
//            adView.setAdSize(AdSize.BANNER);
//            adView.setAdUnitId(BANNER_AD_ID);
//            postList.add(adView);
//        }
//    }

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


    private void pickFromCamera(){
        //Intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, image_rui)
                , IMAGE_PICK_CAMERA_CODE);
        Animatoo.animateShrink(getContext());
    }

    private void pickFromGallery(){
        //Intent to pick image from gallery
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*")
                , IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkCameraPermission(){

        boolean result = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //image is picked from camera, get uri of image
                sendImagetoPost(image_rui);
            }
            else if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //image picked from Gallery, get uri of image
                image_rui = data.getData();

                //send uri to firebase storage
                sendImagetoPost(image_rui);

                //set uri to local output

            }
            else {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void sendImagetoPost(Uri image_rui) {
        //this will grab the image url and send it to the post activity through intent
        Intent intent = new Intent(getContext(), PostActivity.class);
        intent.putExtra("imagePath", image_rui.toString());
        startActivity(intent);

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
                    .placeholder(R.drawable.placeholder)
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
