package com.ng.campusbuddy.education.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.education.Browse;
import com.ng.campusbuddy.education.LibraryActivity;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.model.AD;
import com.ng.campusbuddy.tools.AdInfoActivity;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class BrowseFragment extends Fragment {
    View view;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_browse, container, false);


        ImageButton search_btn = view.findViewById(R.id.btn_search);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity()
//                        .getSupportFragmentManager()
//                        .beginTransaction()
//                        .replace(R.id.fragment_container, new LibraryFragment())
//                        .commit();

                startActivity(new Intent(getContext(), LibraryActivity.class));
            }
        });

        BannerADs();

        ADimageslider();
        FindCourse();
        RecentUpdates();

        return view;
    }

    private void BannerADs() {
        final ImageView RecentBanner = view.findViewById(R.id.recent_courses_iv);

        DatabaseReference recentRef = FirebaseDatabase.getInstance().getReference().child("ADs")
                .child("Browse").child("Banners");

        recentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String recent = dataSnapshot.child("Recent_Update").getValue().toString();

                Glide.with(getContext())
                        .load(recent)
                        .placeholder(R.drawable.placeholder)
                        .into(RecentBanner);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ADimageslider() {
        final ArrayList<AD> sliderList = new ArrayList<>();
        final SliderView sliderView = view.findViewById(R.id.ADsSlider);
        final SliderAdapterADs adapter = new SliderAdapterADs(getActivity(), sliderList);
        sliderView.setSliderAdapter(adapter);

        DatabaseReference Adref= FirebaseDatabase.getInstance().getReference().child("ADs").child("Browse").child("Slides");

        Adref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sliderList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    AD ad = ds.getValue(AD.class);
                    sliderList.add(ad);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(Color.RED);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    private void FindCourse() {

        RecyclerView FindCourse_recycler = view.findViewById(R.id.find_courses_recycler);
        List<Browse> mFindlist = new ArrayList<>();
        FindCourse_recycler.hasFixedSize();
        LinearLayoutManager mLayoutManger = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        FindCourse_recycler.setLayoutManager(mLayoutManger);
        FindCoursesAdapter findCoursesAdapter = new FindCoursesAdapter(getContext(), mFindlist);
        FindCourse_recycler.setAdapter(findCoursesAdapter);

        DatabaseReference CourseRef = FirebaseDatabase.getInstance().getReference().child("");
    }

    private void RecentUpdates() {

    }

    private class SliderAdapterADs  extends SliderViewAdapter<SliderAdapterADs.SliderAdapterVH> {

        private Context context;
        private ArrayList<AD> sliderlist;

        public SliderAdapterADs(Context context, ArrayList<AD> sliderlist) {
            this.context = context;
            this.sliderlist = sliderlist;
        }

        @Override
        public SliderAdapterADs.SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
            View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider_layout, null);
            return new SliderAdapterADs.SliderAdapterVH(inflate);
        }


        @Override
        public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

            final AD ad = sliderlist.get(position);


            viewHolder.textViewDescription.setText(ad.getTitle());


            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, AdInfoActivity.class);
                    intent.putExtra("Ad_id", ad.getId());
                    intent.putExtra("context", "Browse");
                    context.startActivity(intent);
                }
            });


            Glide.with(context)
                    .load(ad.getImage())
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.imageViewBackground);

        }



        @Override
        public int getCount() {
            return sliderlist.size();
        }

        class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

            public View itemView;
            public ImageView imageViewBackground;
            public TextView textViewDescription;

            public SliderAdapterVH(View itemView) {
                super(itemView);
                imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
                textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
                this.itemView = itemView;
            }
        }


    }

    public class FindCoursesAdapter extends RecyclerView.Adapter<FindCoursesAdapter.ViewHolder>{

        private Context mContext;
        private List<Browse> FindCourseList;

        public FindCoursesAdapter(Context mContext, List<Browse> findCourseList) {
            this.mContext = mContext;
            FindCourseList = findCourseList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_browse, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Browse browse = FindCourseList.get(position);

            holder.find_txt.setText(browse.getTitle());

            holder.FindCourses.setVisibility(View.VISIBLE);
            holder.PopularCourses.setVisibility(View.GONE);
            holder.Recommended.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return FindCourseList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView find_txt;
            CardView FindCourses, PopularCourses;
            LinearLayout Recommended;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                find_txt = itemView.findViewById(R.id.find_txt);

                FindCourses = itemView.findViewById(R.id.find_courses_tab_layout);
                PopularCourses = itemView.findViewById(R.id.popular_courses_layout);
                Recommended = itemView.findViewById(R.id.recommended_layout);
            }
        }
    }


}
