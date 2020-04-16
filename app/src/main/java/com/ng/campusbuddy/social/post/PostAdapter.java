package com.ng.campusbuddy.social.post;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.FollowersActivity;
import com.ng.campusbuddy.profile.UserProfileActivity;
import com.ng.campusbuddy.social.User;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.ng.campusbuddy.utils.GlideExtentions.isValidContextForGlide;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
//        implements Filterable
{
    // A menu item view type.
    private static final int POST_ITEM_VIEW_TYPE = 0;
    // The Native Express ad view type.
    private static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    private Context mContext;
    private List<Object> mRecyclerViewItems;


    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Object> recyclerViewItems){
        mContext = context;
        mRecyclerViewItems = recyclerViewItems;
    }

    /**
     * Determines the view type for the given position.
     */
    @Override
    public int getItemViewType(int position) {

        Object recyclerViewItem = mRecyclerViewItems.get(position);
        if (recyclerViewItem instanceof UnifiedNativeAd) {
            return NATIVE_EXPRESS_AD_VIEW_TYPE;
        }
        return POST_ITEM_VIEW_TYPE;
    }

    /**
     * Creates a new view for a menu item view or a Native ad view
     * based on the viewType. This method is invoked by the layout manager.
     */
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                View unifiedNativeLayoutView = LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.item_native_ad,
                        parent, false);
                return new UnifiedNativeAdViewHolder(unifiedNativeLayoutView);
            case POST_ITEM_VIEW_TYPE:
            default:
                View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_post, parent, false);
                return new ImageViewHolder(menuItemLayoutView);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case NATIVE_EXPRESS_AD_VIEW_TYPE:
                UnifiedNativeAd nativeAd = (UnifiedNativeAd) mRecyclerViewItems.get(position);
                populateNativeAdView(nativeAd, ((UnifiedNativeAdViewHolder) holder).getAdView());
                break;

            case POST_ITEM_VIEW_TYPE:
            default:
                final ImageViewHolder post_holder = (ImageViewHolder) holder;
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                final Post post = (Post) mRecyclerViewItems.get(position);


                if (post.getDescription().equals("") && !post.getPostimage().equals("")){
                    post_holder.description.setVisibility(View.GONE);

                    post_holder.post_container.setVisibility(View.VISIBLE);
                    post_holder.post_text_container.setVisibility(View.GONE);

                    Glide.with(mContext)
                            .load(post.getPostimage())
                            .thumbnail(0.1f)
                            .into(post_holder.post_image);
                }
                else if (post.getPostimage().equals("") && !post.getDescription().equals("")){
                    post_holder.description.setVisibility(View.GONE);

                    post_holder.post_container.setVisibility(View.GONE);
                    post_holder.post_text_container.setVisibility(View.VISIBLE);

                    post_holder.post_text_container.setText(post.getDescription());
                    post_holder.save.setVisibility(View.GONE);
                }
                else {
                    post_holder.description.setVisibility(View.VISIBLE);
                    post_holder.description.setText(post.getDescription());
                    Glide.with(mContext)
                            .load(post.getPostimage())
                            .thumbnail(0.1f)
                            .into(post_holder.post_image);

                    post_holder.post_container.setVisibility(View.VISIBLE);
                    post_holder.post_text_container.setVisibility(View.GONE);
                }


                String timeStamp = post.getTimestamp();

                //converting time stamp to dd/mm/yyyy hh:mm am/pm
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(timeStamp));
                String dateTime = DateFormat.format("hh:mm aa, dd/MM", cal).toString();

                post_holder.time_date.setText(dateTime);

//                int post_height = post_holder.post_image.getHeight();
//                ViewGroup.LayoutParams heart_height = post_holder.heart.getLayoutParams();
//                heart_height.height = post_height;
//                post_holder.heart.setLayoutParams(heart_height);


                publisherInfo(post_holder.image_profile, post_holder.username, post_holder.publisher, post.getPublisher());
                isLiked(post.getPostid(), post_holder.like);
                isSaved(post.getPostid(), post_holder.save);
                nrLikes(post_holder.likes, post.getPostid());
                getCommetns(post.getPostid(), post_holder.comments);

                post_holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post_holder.like.getTag().equals("like")) {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).setValue(true);
                            addNotificationLike(post.getPublisher(), post.getPostid());



                            //plays sound
                            MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.pop);
                            mediaPlayer.start();


//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    post_holder.heart.setVisibility(View.INVISIBLE);
//                                    post_holder.bg.setVisibility(View.INVISIBLE);
//
//                                }
//                            }, 3000);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                    .child(firebaseUser.getUid()).removeValue();
                        }
                    }
                });

                post_holder.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (post_holder.save.getTag().equals("save")){
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                                    .child(post.getPostid()).setValue(true);
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                                    .child(post.getPostid()).removeValue();
                        }
                    }
                });

                post_holder.image_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", post.getPublisher());
                        editor.apply();

                        ((FragmentActivity)mContext).startActivity(new Intent(mContext, UserProfileActivity.class));
                        Animatoo.animateZoom(mContext);
                    }
                });

                post_holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", post.getPublisher());
                        editor.apply();

                        ((FragmentActivity)mContext).startActivity(new Intent(mContext, UserProfileActivity.class));
                        Animatoo.animateZoom(mContext);
                    }
                });

                post_holder.publisher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("profileid", post.getPublisher());
                        editor.apply();

                        ((FragmentActivity)mContext).startActivity(new Intent(mContext, UserProfileActivity.class));
                        Animatoo.animateZoom(mContext);
                    }
                });
//
                post_holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, PostDetailActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                        Animatoo.animateShrink(mContext);
                    }
                });

                post_holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        BitmapDrawable bitmapDrawable = (BitmapDrawable)post_holder.post_image.getDrawable();
                        if (bitmapDrawable == null){
                            // We work on this later
                        }
                        else {
                            // converting image to bitmap
                            Bitmap bitmap = bitmapDrawable.getBitmap();
                            shareImage_Text(post.getDescription(),bitmap);
                        }
                    }
                });

                post_holder.description .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, PostDetailActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                        Animatoo.animateShrink(mContext);
                    }
                });

                post_holder.comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, PostDetailActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                        Animatoo.animateShrink(mContext);
                    }
                });

                post_holder.post_text_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, PostDetailActivity.class);
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent);
                        Animatoo.animateShrink(mContext);
                    }
                });

                post_holder.post_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                        editor.putString("postid", post.getPostid());
                        editor.apply();

                        Intent intent = new Intent(mContext, FullscreenActivity.class);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, post_holder.post_image, ViewCompat.getTransitionName(post_holder.post_image));
                        intent.putExtra("postid", post.getPostid());
                        intent.putExtra("publisherid", post.getPublisher());
                        mContext.startActivity(intent, optionsCompat.toBundle());
//                        Animatoo.animateShrink(mContext);

                    }
                });

                post_holder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, FollowersActivity.class);
                        intent.putExtra("id", post.getPostid());
                        intent.putExtra("title", "likes");
                        mContext.startActivity(intent);
                        Animatoo.animateShrink(mContext);
                    }
                });

                post_holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PopupMenu popupMenu = new PopupMenu(mContext, view);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                switch (menuItem.getItemId()){
                                    case R.id.edit:
                                        editPost(post.getPostid());
                                        return true;
                                    case R.id.delete:
                                        final String id = post.getPostid();
                                        FirebaseDatabase.getInstance().getReference("Posts")
                                                .child(post.getPostid()).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            deleteNotifications(id, firebaseUser.getUid());
                                                        }
                                                    }
                                                });
                                        return true;
                                    case R.id.report:
                                        Toast.makeText(mContext, "Post Reported!", Toast.LENGTH_SHORT).show();
                                        return true;
                                    case R.id.save:
                                        downloadImage(post.getPostimage(), post.getPostid());
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popupMenu.inflate(R.menu.post_menu);
                        if (!post.getPublisher().equals(firebaseUser.getUid())){
                            popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                            popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                            if (post.getPostimage().isEmpty()){
                                popupMenu.getMenu().findItem(R.id.save).setVisible(false);
                            }
                        }
                        popupMenu.show();
                    }
                });

        }

    }


    @Override
    public int getItemCount() {
        return mRecyclerViewItems.size();
    }

//    @Override
//    public Filter getFilter() {
//        return filter;
//    }
//
//    Filter filter = new Filter() {
//        @Override
//        protected FilterResults performFiltering(CharSequence charSequence) {
//            List<Post> filteredList = new ArrayList<>();
//            if (charSequence.toString().isEmpty()){
//                filteredList.addAll(mPostsAll);
//            }
//            else {
//                for (Post post : mPostsAll){
//                    if (post.toString().toLowerCase().contains(charSequence.toString().toLowerCase())){
//                        filteredList.add(post);
//                    }
//                }
//            }
//
//            FilterResults filterResults = new FilterResults();
//            filterResults.values = filteredList;
//
//            return filterResults;
//        }
//
//        @Override
//        protected void publishResults(CharSequence charSequence, FilterResults results) {
//            mPosts.clear();
//            mPosts.addAll((Collection<? extends Post>) results.values);
//            notifyDataSetChanged();
//        }
//    };

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image, like, comment, save, more, share;
        public TextView username, time_date, likes, publisher, description, comments;
        public TextView post_text_container;
        public RelativeLayout post_container, post_layout;

        public ImageViewHolder(View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            time_date = itemView.findViewById(R.id.time_date);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            share = itemView.findViewById(R.id.share);
            save = itemView.findViewById(R.id.save);
            likes = itemView.findViewById(R.id.likes);
            publisher = itemView.findViewById(R.id.publisher);
            description = itemView.findViewById(R.id.description);
            comments = itemView.findViewById(R.id.comments);
            more = itemView.findViewById(R.id.more);


            post_text_container = itemView.findViewById(R.id.post_text_container);
            post_container = itemView.findViewById(R.id.post_container);
            post_layout = itemView.findViewById(R.id.post_layout);
        }
    }

    public class UnifiedNativeAdViewHolder extends RecyclerView.ViewHolder {

        private UnifiedNativeAdView adView;

        public UnifiedNativeAdView getAdView() {
            return adView;
        }

        UnifiedNativeAdViewHolder(View view) {
            super(view);
            adView = (UnifiedNativeAdView) view.findViewById(R.id.ad_view);

            // The MediaView will display a video asset if one is present in the ad, and the
            // first image asset otherwise.
            adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

            // Register the view used for each individual asset.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        }
    }


    //    public static class FanAdHolder extends RecyclerView.ViewHolder {
//        private View adContainer;
//        private ImageView adIcon;
//        private TextView adTitle;
//        private MediaView adMedia;
//        private TextView adSocialContext;
//        private Button adCallToAction;
//        private LinearLayout adChoicesContainer;
//
//        public FanAdHolder(View view) {
//            super(view);
//            adContainer = view.findViewById(R.id.native_ad_container);
//            adIcon = (ImageView) view.findViewById(R.id.native_ad_icon);
//            adTitle = (TextView) view.findViewById(R.id.native_ad_title);
//            adMedia = (MediaView) view.findViewById(R.id.native_ad_media);
//            adSocialContext = (TextView) view.findViewById(R.id.native_ad_social_context);
//            adCallToAction = (Button) view.findViewById(R.id.native_ad_cta);
//            adChoicesContainer = (LinearLayout) view.findViewById(R.id.native_ad_choices_container);
//        }
//
//        public void bindView(com.facebook.ads.NativeAd ad) {
//            adTitle.setText(ad.getAdTitle());
//            adMedia.setNativeAd(ad);
//            adSocialContext.setText(ad.getAdSocialContext());
//            adCallToAction.setText(ad.getAdCallToAction());
//            NativeAd.Image icon = ad.getAdIcon();
//            NativeAd.downloadAndDisplayImage(icon, adIcon);
//
//            AdChoicesView adChoicesView = new AdChoicesView(adContainer.getContext(), ad, true);
//            adChoicesContainer.removeAllViews();
//            adChoicesContainer.addView(adChoicesView);
//
//            List<View> clickableViews = new ArrayList<>();
//            clickableViews.add(adMedia);
//            clickableViews.add(adCallToAction);
//            ad.registerViewForInteraction(adContainer, clickableViews);
//        }
//    }
//
//    public static class MoPubAdHolder extends RecyclerView.ViewHolder {
//        private View adContainer;
//        public MoPubAdHolder(View view) {
//            super(view);
//            adContainer = view.findViewById(R.id.native_ad_container);
//        }
//
//        public void bindView(com.mopub.nativeads.NativeAd ad) {
//            ad.clear(adContainer);
//            ad.renderAdView(adContainer);
//            ad.prepare(adContainer);
//        }
//    }

    private long downloadImage(String url, String extention) {

        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(mContext, Environment.getExternalStorageDirectory().toString(), "CB_post"+ extention + ".jpg");


        Toast.makeText(mContext, "Downloaded to gallery", Toast.LENGTH_SHORT).show();
        return downloadManager.enqueue(request);
    }

    private void shareImage_Text(String Description, Bitmap bitmap){
        String shareBody = Description;

        Uri uri = saveImageToShare(bitmap);

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.setType("image/png");
        mContext.startActivity(Intent.createChooser(sIntent, "Share Via"));

    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(mContext.getCacheDir(), "images");

        Uri uri = null;
        try{
            imageFolder.mkdir();
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(mContext, "com.ng.campusbuddy.fileprovider", file);
        }
        catch (Exception e){
            Toast.makeText(mContext, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return uri;
    }

    private void addNotificationLike(String userid, final String postid){


        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);

        final String orderBy = "like"+firebaseUser.getUid();
        reference.orderByChild("orderby").equalTo(orderBy)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            dataSnapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    String notification_id = reference.push().getKey();

                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("userid", firebaseUser.getUid());
                                    hashMap.put("text", "liked your post");
                                    hashMap.put("postid", postid);
                                    hashMap.put("type", "post");
                                    hashMap.put("id", notification_id);
                                    hashMap.put("isseen", false);
                                    hashMap.put("orderby", orderBy);


                                    reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        }
                                    });
                                }
                            });
                        }
                        else {
                            String notification_id = reference.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("userid", firebaseUser.getUid());
                            hashMap.put("text", "liked your post");
                            hashMap.put("postid", postid);
                            hashMap.put("type", "post");
                            hashMap.put("id", notification_id);
                            hashMap.put("isseen", false);
                            hashMap.put("orderby", orderBy);


                            reference.child(notification_id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void deleteNotifications(final String postid, String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    if (snapshot.child("postid").getValue().equals(postid)){
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void nrLikes(final TextView likes, String postId){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getCommetns(String postId, final TextView comments){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                comments.setText(""+dataSnapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (isValidContextForGlide(mContext)){
                    Glide.with(mContext)
                            .load(user.getImageurl())
                            .thumbnail(0.1f)
                            .into(image_profile);
                    username.setText(user.getUsername());
                    publisher.setText(user.getUsername());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                } else{
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.ic_bookmarked);
                    imageView.setTag("saved");
                } else{
                    imageView.setImageResource(R.drawable.ic_bookmark);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void editPost(final String postid){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postid).updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    private void getText(String postid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void populateNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_rating));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.GONE);
        } else {
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((CircleImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.GONE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.GONE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }
}