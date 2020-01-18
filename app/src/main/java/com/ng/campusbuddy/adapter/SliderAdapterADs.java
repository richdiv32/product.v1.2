package com.ng.campusbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ng.campusbuddy.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

public class SliderAdapterADs  extends SliderViewAdapter<SliderAdapterADs.SliderAdapterVH> {

    private Context context;

    public SliderAdapterADs(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        viewHolder.textViewDescription.setText("This is slider item " + position);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked" + position, Toast.LENGTH_SHORT).show();
            }
        });

        switch (position) {
            case 0:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad1))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;
            case 1:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad2))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;
            case 2:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad3))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 3:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad4))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 4:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad5))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 5:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad6))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 6:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad7))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 7:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad8))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 8:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad9))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 9:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad10))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 10:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad11))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 11:
                Glide.with(viewHolder.itemView)
                        .load(context.getString(R.string.Home_Ad12))
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            default:
                Glide.with(viewHolder.itemView)
                        .load("")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

        }

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return 12;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
