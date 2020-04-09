package com.ng.campusbuddy.auth;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.Resource;

import java.util.ArrayList;
import java.util.Collection;

public class Relationship_adapter extends ArrayList<String> {

    Activity activity;
    ArrayList data;
    Resource res;
    Relationship_spin tempValues = null;
    LayoutInflater inflater;

    public Relationship_adapter( SetUpProfileActivity mContext, int initialCapacity, ArrayList data, Resource res) {
        super(initialCapacity);

        this.activity = mContext;
        this.data = data;
        this.res = res;

        inflater =(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

}
