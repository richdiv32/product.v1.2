package com.ng.campusbuddy.education;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;


public class BrowseFragment extends Fragment {

    ViewFlipper AdFlipper;
    ImageView AD1_iv,AD2_iv,AD3_iv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);




        AdFlipper = view.findViewById(R.id.AD_filpper);
        AD1_iv = view.findViewById(R.id.ad_1_iv);
        AD2_iv = view.findViewById(R.id.ad_2_iv);
        AD3_iv = view.findViewById(R.id.ad_3_iv);
        getAds();


        return view;
    }
    private void getAds(){

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

                    Glide.with(getActivity()).load(Ad1).into(AD1_iv);
                    Glide.with(getActivity()).load(Ad2).into(AD2_iv);
                    Glide.with(getActivity()).load(Ad3).into(AD3_iv);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
