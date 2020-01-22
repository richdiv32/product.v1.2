package com.ng.campusbuddy.social;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.Post;

import java.util.ArrayList;
import java.util.List;


public class MatchUpFragment extends Fragment {

    SwipeFlingAdapterView flingContainer;
    ArrayAdapter arrayAdapter;

    ImageButton Like_btn, Love_btn;

    List love;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match_up, container, false);

        Like_btn = view.findViewById(R.id.like_btn);
        Love_btn = view.findViewById(R.id.love_btn);

        Like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "like", Toast.LENGTH_SHORT).show();
            }
        });

        Love_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "love", Toast.LENGTH_SHORT).show();
            }
        });

        flingContainer = view.findViewById(R.id.match_card);


        love = new ArrayList<String>();
        love.add("Angela");
        love.add("Dami");
        love.add("Lily");
        love.add("Bumi");
        love.add("Cynthia");
        love.add("Dunsin");
        love.add("Ore");
        love.add("Shindy");
        love.add("Ife");
        love.add("Zaniab");

        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.match_up_item,R.id.match_text,love);

        //setting listner
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                love.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object o) {

                Toast.makeText(getActivity(), "Like", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object o) {
                Toast.makeText(getActivity(), "Love", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAdapterAboutToEmpty(int i) {

                Toast.makeText(getActivity(), "View More tomorrow", Toast.LENGTH_SHORT).show();
                //Ask for more data
//                love.add("View more tomorrow".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                i++;
            }

            @Override
            public void onScroll(float v) {

            }
        });

        //adding on ItemClickListner
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int i, Object o) {
                Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();
            }
        });




        return view;
    }


}
