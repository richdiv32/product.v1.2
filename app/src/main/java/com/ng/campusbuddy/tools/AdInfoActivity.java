package com.ng.campusbuddy.tools;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.firebase.database.Query;
import com.ng.campusbuddy.model.AD;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.messaging.PhotoActivity;
import com.ng.campusbuddy.utils.ForceUpdateChecker;
import com.ng.campusbuddy.utils.SharedPref;


public class AdInfoActivity extends AppCompatActivity {


    Intent intent;
    String ad_id, context_name;
    String Tel, Web;

    ImageButton Call, Site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.AppDarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            //removes status bar with background
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        if (restorePrefData()) {
        }
        else {
            TapTarget();
        }
        setContentView(R.layout.activity_ad_info);


        intent = getIntent();

        ad_id = intent.getStringExtra("Ad_id");
        context_name = intent.getStringExtra("context");

        ImageButton Back = findViewById(R.id.back_btn);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final ImageView ADimage = findViewById(R.id.image);
        final TextView ADtitle = findViewById(R.id.title);
        final TextView ADdescription = findViewById(R.id.description);
        Call = findViewById(R.id.ad_call);
        Site = findViewById(R.id.ad_site);

        final DatabaseReference AD = FirebaseDatabase.getInstance().getReference().child("ADs")
                .child(context_name).child("Slides").child(ad_id);

        AD.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final AD ad = dataSnapshot.getValue(AD.class);

                Glide.with(AdInfoActivity.this)
                        .load(ad.getFull_image())
                        .placeholder(R.drawable.placeholder)
                        .thumbnail(0.1f)
                        .into(ADimage);

                ADtitle.setText(ad.getTitle());
                ADdescription.setText(ad.getDescription());


                Tel = ad.getTel();
                Web = ad.getSite();

                if (Web.isEmpty() || Web == null){
                    Site.setVisibility(View.GONE);
                }
                else if (Tel.isEmpty() || Tel == null){
                    Call.setVisibility(View.GONE);
                }
                else {
                    Site.setVisibility(View.VISIBLE);
                    Call.setVisibility(View.VISIBLE);
                }

                ADimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AdInfoActivity.this, PhotoActivity.class);
                        intent.putExtra("imageurl", ad.getFull_image());
                        startActivity(intent);
                        Animatoo.animateShrink(AdInfoActivity.this);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Init();
        AdMod();

    }

    private void Init() {
        Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(AdInfoActivity.this,
                        Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                    phoneCall();
                }
                else {
                    final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                    //Asking request Permissions
                    ActivityCompat.requestPermissions(AdInfoActivity.this, PERMISSIONS_STORAGE, 9);
                }

            }
        });
        Site.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Website();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = false;
        switch (requestCode){
            case 9:
                permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (permissionGranted){
            phoneCall();
        }
        else {
            Toast.makeText(this, "You've not allowed permission", Toast.LENGTH_SHORT).show();
        }
    }

    private void phoneCall(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + Uri.encode(Tel.trim())));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(callIntent);
        }
        else {
            Toast.makeText(this, "You haven't assigned permission", Toast.LENGTH_SHORT).show();
        }

    }

    private void Website() {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Web));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void AdMod() {
        MobileAds.initialize(this,
                getString(R.string.App_ID));
        AdView mAdview = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);

//        MobileAds.initialize(this);
//        AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
//                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
//                    @Override
//                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
//                        //native ad will be avaliable here
//
//                        UnifiedNativeAdView unifiedNativeAdView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.item_native_ad, null);
//                        mapUnifiedNativeAdToLayout(unifiedNativeAd, unifiedNativeAdView);
//
//                        FrameLayout nativeAdlayout = findViewById(R.id.native_ad);
//                        nativeAdlayout.removeAllViews();
//                        nativeAdlayout.addView(unifiedNativeAdView);
//                    }
//                })
//                .withAdListener(new AdListener(){
//                    @Override
//                    public void onAdClicked() {
//                        super.onAdClicked();
//                    }
//                })
//                .build();
//        adLoader.loadAd(new AdRequest.Builder().build());

//        NativeExpressAdView mAdview = findViewById(R.id.adView);
//        final VideoController videoController = mAdview.getVideoController();
//
//        mAdview.setVideoOptions(new VideoOptions.Builder()
//                .setStartMuted(true)
//                .build());
//
//        videoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
//            @Override
//            public void onVideoEnd() {
//                super.onVideoEnd();
//            }
//        });
//
//        mAdview.setAdListener(new AdListener(){
//            @Override
//            public void onAdLoaded(){
//                if (videoController.hasVideoContent()){
//
//                }
//                else {
//
//                }
//            }
//        });
//
//        mAdview.loadAd(new AdRequest.Builder().build());
    }

//    private void mapUnifiedNativeAdToLayout(UnifiedNativeAd adFromGoogle, UnifiedNativeAdView myAdView) {
//        MediaView mediaView = findViewById(R.id.ad_media);
//        myAdView.setMediaView(mediaView);
//
//        myAdView.setHeadlineView(myAdView.findViewById(R.id.ad_headline));
//        myAdView.setBodyView(myAdView.findViewById(R.id.ad_body));
//        myAdView.setCallToActionView(myAdView.findViewById(R.id.ad_call_to_action));
//        myAdView.setIconView(myAdView.findViewById(R.id.ad_icon));
//        myAdView.setPriceView(myAdView.findViewById(R.id.ad_price));
//        myAdView.setStarRatingView(myAdView.findViewById(R.id.ad_rating));
//        myAdView.setStoreView(myAdView.findViewById(R.id.ad_store));
//        myAdView.setAdvertiserView(myAdView.findViewById(R.id.ad_advertiser));
//        myAdView.setStarRatingView(myAdView.findViewById(R.id.ad_rating));
//
//        ((TextView)myAdView.getHeadlineView()).setText(adFromGoogle.getHeadline());
//
//        if (adFromGoogle.getBody() == null){
//            myAdView.getBodyView().setVisibility(View.GONE);
//        }
//        else {
//            ((TextView)myAdView.getBodyView()).setText(adFromGoogle.getBody());
//        }
//        if (adFromGoogle.getAdvertiser() == null){
//            myAdView.getAdvertiserView().setVisibility(View.GONE);
//        }
//        else {
//            ((TextView)myAdView.getAdvertiserView()).setText(adFromGoogle.getBody());
//        }
//        if (adFromGoogle.getCallToAction() == null){
//            myAdView.getCallToActionView().setVisibility(View.GONE);
//        }
//        else {
//            ((Button)myAdView.getCallToActionView()).setText(adFromGoogle.getBody());
//        }
//        if (adFromGoogle.getIcon() == null){
//            myAdView.getIconView().setVisibility(View.GONE);
//        }
//        else {
//            ((ImageView)myAdView.getIconView()).setImageDrawable(adFromGoogle.getIcon().getDrawable());
//        }
//        if (adFromGoogle.getStarRating() == null){
//            myAdView.getStarRatingView().setVisibility(View.GONE);
//        }
//        else {
//            ((RatingBar)myAdView.getStarRatingView()).setRating(adFromGoogle.getStarRating().floatValue());
//        }
//        if (adFromGoogle.getStore() == null){
//            myAdView.getStoreView().setVisibility(View.GONE);
//        }
//        else {
//            ((TextView)myAdView.getStoreView()).setText(adFromGoogle.getBody());
//        }
//        if (adFromGoogle.getPrice() == null){
//            myAdView.getPriceView().setVisibility(View.GONE);
//        }
//        else {
//            ((TextView)myAdView.getPriceView()).setText(adFromGoogle.getBody());
//        }
//
//        myAdView.setNativeAd(adFromGoogle);
//    }

    private void TapTarget() {

        // We have a sequence of targets, so lets build it!
        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        // Likewise, this tap target will target the search button
                        TapTarget.forView(findViewById(R.id.ad_call), "Call Advertiser", "Want to speak to a personnel and get more info on this AD, click this and you'll be connected directly")
                                .dimColor(R.color.semi_transparent)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.white)
                                .cancelable(false)
                                .tintTarget(false)
                                .id(1),
                        // Likewise, this tap target will target the search button
                        TapTarget.forView(findViewById(R.id.ad_site), "View Website", "To get more info click here to visit Ad's webpage")
                                .dimColor(R.color.semi_transparent)
                                .outerCircleColor(R.color.colorPrimary)
                                .targetCircleColor(R.color.white)
                                .textColor(android.R.color.white)
                                .cancelable(false)
                                .tintTarget(false)
                                .id(2)

                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        // Executes when sequence of instruction get completes.
                        savePrefsData();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        final AlertDialog dialog = new AlertDialog.Builder(AdInfoActivity.this)
                                .setTitle("Uh oh")
                                .setMessage("You canceled the sequence")
                                .setPositiveButton("OK", null).show();
                        TapTargetView.showFor(dialog,
                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
                                        .cancelable(false)
                                        .tintTarget(false), new TapTargetView.Listener() {
                                    @Override
                                    public void onTargetClick(TapTargetView view) {
                                        super.onTargetClick(view);
                                        dialog.dismiss();
                                    }
                                });
                    }
                });

        sequence.start();
    }

    private boolean restorePrefData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpnend",false);
        return  isIntroActivityOpnendBefore;

    }
    private void savePrefsData() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpnend",true);
        editor.apply();

    }

}
