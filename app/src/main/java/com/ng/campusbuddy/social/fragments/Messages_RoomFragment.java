package com.ng.campusbuddy.social.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ng.campusbuddy.R;

public class Messages_RoomFragment extends Fragment {
    View view;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        view = inflater.inflate(R.layout.fragment_messages__room, container, false);



        return view;
    }
}
