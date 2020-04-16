package com.ng.campusbuddy.social.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

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
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.model.AD;
import com.ng.campusbuddy.social.post.AllPostAdapter;
import com.ng.campusbuddy.social.post.Post;
import com.ng.campusbuddy.social.post.PostActivity;
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

import static android.content.Context.MODE_PRIVATE;
import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class FeedsFragment extends Fragment {
    View view;

    ImageButton AllPost, FollowPost;

    Fragment selectedfragment = null;

    private static  final int ITEM_PER_AD = 5;
    private static final String BANNER_AD_ID = "ca-app-pub-3940256099942544/6300978111";
    private int count = 0;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;


    //for image
    Uri image_rui = null;

    private static String POST_STATE = "post_state";
    private Parcelable savedRecyclerLayoutState;
    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_layout";
    private ArrayList<? extends Post> postsInstance = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feeds, container, false);
        if (restorePrefData()){

        }
        else {
            TapTarget();
        }


        AllPost = view.findViewById(R.id.all_post);
        FollowPost = view.findViewById(R.id.follow_post);

        Init();
        PostInit();

        Home();

        return view;
    }

    private void Home() {
        ImageView home = view.findViewById(R.id.logo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));
            }
        });
    }

    private void TapTarget() {
        TapTargetView.showFor(getActivity(),
                TapTarget.forView(view.findViewById(R.id.post_fab),
                        "Post Your Moments",
                        "share your favourite moments with friends all over the country")
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorPrimary),
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        savePrefsData();

                        view.dismiss(true);
                    }
                });
    }

    private void Init() {



        final ViewPager FeedsPager = view.findViewById(R.id.feedsPager);
        FeedsPager.setOffscreenPageLimit(1);

        FeedsPagerViewAdapter mPagerViewAdapter = new FeedsPagerViewAdapter(getChildFragmentManager());
        FeedsPager.setAdapter(mPagerViewAdapter);

//        FeedsPager.setCurrentItem(0);
        FollowPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FeedsPager.setCurrentItem(0);

            }
        });

        AllPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FeedsPager.setCurrentItem(1);

            }
        });

        FeedsPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                changeTabs(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void changeTabs(int position) {

        if(position == 0){
            FollowPost.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
            AllPost.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));
        }

        if(position == 1){
            FollowPost.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey));
            AllPost.setColorFilter(ContextCompat.getColor(getContext(), R.color.white));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.feed_menu, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
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
                    //plays sound
                    MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.pop);
                    mediaPlayer.start();
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
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    pickFromCamera();
                }
                else {
                    final String[] PERMISSIONS_STORAGE = {Manifest.permission.CAMERA};
                    //Asking request Permissions
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, 1);
                }
            }
        });

        post_gallery_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    pickFromGallery();
                }
                else {
                    final String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    //Asking request Permissions
                    ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, 2);
                }

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

    private void pickFromCamera(){
        //Intent to pick image from camera
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr");
        image_rui = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Toast.makeText(getContext(), "Camera", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, image_rui)
                , IMAGE_PICK_CAMERA_CODE);
        Animatoo.animateShrink(getContext());
    }

    private void pickFromGallery(){
        Toast.makeText(getContext(), "Post Image", Toast.LENGTH_SHORT).show();

        //Intent to pick image from gallery
        startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*")
                , IMAGE_PICK_GALLERY_CODE);
        Animatoo.animateShrink(getContext());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = false;
        switch (requestCode){
            case 1:
                permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case 2:
                permissionGranted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (permissionGranted){
            pickFromCamera();
        }
        else if (permissionGranted){
            pickFromGallery();
        }
        else {
            Toast.makeText(getActivity(), "You've not allowed permission", Toast.LENGTH_SHORT).show();
        }
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

    private boolean restorePrefData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isTapTargetClickedBefore = pref.getBoolean("isTapOpnend",false);
        return  isTapTargetClickedBefore;

    }
    private void savePrefsData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isTapOpnend",true);
        editor.apply();

    }



    class FeedsPagerViewAdapter extends FragmentPagerAdapter {

        public FeedsPagerViewAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Feeds_Follow_PostFragment follow_postFragment = new Feeds_Follow_PostFragment();
                    return follow_postFragment;

                case 1:
                    Feeds_All_PostFragment feeds_all_postFragment = new Feeds_All_PostFragment();
                    return feeds_all_postFragment;

                default:
                    return null;

            }

        }

        @Override
        public int getCount() {
            return 2;
        }

    }
}
