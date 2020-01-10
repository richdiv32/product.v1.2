package com.ng.campusbuddy.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.textViewDescription.setText("This is slider item " + position);

        switch (position) {
            case 0:
                Glide.with(viewHolder.itemView)
                        .load("https://www.usnews.com/dims4/USNEWS/0bc5df8/2147483647/thumbnail/640x420/quality/85/?url=http%3A%2F%2Fcom-usnews-beam-media.s3.amazonaws.com%2F7c%2F97%2F0e47788848c0b14cff75b90d9b66%2F141017-studying-stock.jpg")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;
            case 1:
                Glide.with(viewHolder.itemView)
                        .load("https://www.fastweb.com/uploads/article_photo/photo/2037648/crop635w_iStock-629189720.jpg")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;
            case 2:
                Glide.with(viewHolder.itemView)
                        .load("https://scontent-lis1-1.xx.fbcdn.net/v/t1.0-9/41840059_1069527886547900_444387815006928896_n.jpg?_nc_cat=101&_nc_ohc=-oSvdGzrcHMAQmkoKVGjE8-oc00nXBs-u4EPU8KVMtVSr9cVJKmTiEwMQ&_nc_ht=scontent-lis1-1.xx&oh=bb63600bbaae951536618fba930410ce&oe=5E79A614")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 3:
                Glide.with(viewHolder.itemView)
                        .load("https://hackernoon.com/hn-images/1*j41hMsYft-ifSvXuWOb7Gg.png")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 4:
                Glide.with(viewHolder.itemView)
                        .load("https://pictures.dealer.com/c/cchorshamfordfd/1795/2c35f4890cabc51f7b68768cf84cd7f3x.jpg?impolicy=downsize&w=568")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 5:
                Glide.with(viewHolder.itemView)
                        .load("http://random3.net/images/header.jpg")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 6:
                Glide.with(viewHolder.itemView)
                        .load("https://i.ytimg.com/vi/ivgvRUlLHO8/maxresdefault.jpg")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 7:
                Glide.with(viewHolder.itemView)
                        .load("https://www.shoutmeloud.com/wp-content/uploads/2015/06/WordPress_Ad_Widget.png")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 8:
                Glide.with(viewHolder.itemView)
                        .load("https://wpdean.com/wp-content/uploads/2015/12/Advanced-Ads.jpg")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 9:
                Glide.with(viewHolder.itemView)
                        .load("https://www.currentschoolnews.com/wp-content/uploads/2019/06/College-Student.png")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 10:
                Glide.with(viewHolder.itemView)
                        .load("https://www.topuniversities.com/sites/default/files/styles/lead_article_image/public/blogs/lead-images/shutterstock_81549778.jpg")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            case 11:
                Glide.with(viewHolder.itemView)
                        .load("https://www.pewtrusts.org/-/media/post-launch-images/2018/08/sln_aug2_1/16x9_m.jpg?h=1024&w=1820&la=en&hash=15A02DDD8373CAA1864E8119FDD5D9AEF264F647")
                        .centerCrop()
                        .into(viewHolder.imageViewBackground);
                break;

            default:
                Glide.with(viewHolder.itemView)
                        .load("https://study.com/cimages/hub/Being%20a%20Full-Time%20vs.%20Part-Time%20Community%20College%20Student.jpg")
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
