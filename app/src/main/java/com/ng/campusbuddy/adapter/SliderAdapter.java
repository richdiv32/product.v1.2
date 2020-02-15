package com.ng.campusbuddy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.AD;
import com.ng.campusbuddy.model.ScreenItem;
import com.ng.campusbuddy.tools.AdInfoActivity;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    List<ScreenItem> mListScreen;

    public SliderAdapter(Context context, List<ScreenItem> mListScreen) {
        this.context = context;
        this.mListScreen = mListScreen;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_screen, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(final SliderAdapterVH viewHolder, final int position) {


        viewHolder.title.setText(mListScreen.get(position).getTitle());
        viewHolder.description.setText(mListScreen.get(position).getDescription());
        viewHolder.imgSlide.setImageResource(mListScreen.get(position).getScreenImg());
//        switch (position) {
//            case 0:
//                Glide.with(viewHolder.itemView)
//                        .load(context.getString(R.string.Home_Ad1))
//                        .centerCrop()
//                        .into(viewHolder.imgSlide);
//                break;
//            case 1:
//                Glide.with(viewHolder.itemView)
//                        .load(context.getString(R.string.Home_Ad2))
//                        .centerCrop()
//                        .into(viewHolder.imgSlide);
//                break;
//            case 2:
//                Glide.with(viewHolder.itemView)
//                        .load(context.getString(R.string.Home_Ad3))
//                        .centerCrop()
//                        .into(viewHolder.imgSlide);
//                break;
//
//            case 3:
//                Glide.with(viewHolder.itemView)
//                        .load(context.getString(R.string.Home_Ad4))
//                        .centerCrop()
//                        .into(viewHolder.imageViewBackground);
//                break;
//
//            case 4:
//                Glide.with(viewHolder.itemView)
//                        .load(context.getString(R.string.Home_Ad5))
//                        .centerCrop()
//                        .into(viewHolder.imageViewBackground);
//                break;
//
//            default:
//                Glide.with(viewHolder.itemView)
//                        .load(R.drawable.placeholder)
//                        .centerCrop()
//                        .into(viewHolder.imageViewBackground);
//                break;
//
//        }

    }



    @Override
    public int getCount() {
        //slider view count could be dynamic size
//        return 4;
        return mListScreen.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imgSlide;
        TextView title, description;

        public SliderAdapterVH(View itemView) {
            super(itemView);

            imgSlide = itemView.findViewById(R.id.intro_img);
            title = itemView.findViewById(R.id.intro_title);
            description = itemView.findViewById(R.id.intro_description);


            this.itemView = itemView;
        }
    }

    
}
