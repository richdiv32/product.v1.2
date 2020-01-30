package com.ng.campusbuddy.social.message;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.profile.UserProfileActivity;
import com.ng.campusbuddy.social.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private Context mContext;
    private List<Grouplist> mGroups;

    private FirebaseUser firebaseUser;


    public GroupListAdapter(Context mContext, List<Grouplist> mGroups) {
        this.mContext = mContext;
        this.mGroups = mGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Grouplist grouplist = mGroups.get(position);

        holder.title.setText(grouplist.getTitle());


        if (grouplist.getGroupimage().equals("")){
            holder.group_image.setImageResource(R.mipmap.ic_launcher);
        }
        else {

            Picasso.get()
                    .load(grouplist.getGroupimage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.group_image);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("groupid", grouplist.getGroupid());
                editor.apply();

                Intent intent = new Intent(mContext, GroupChatActivity.class);
                intent.putExtra("groupid", grouplist.getGroupid());
                mContext.startActivity(intent);
                Animatoo.animateZoom(mContext);

            }
        });



    }



    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public ImageView group_image;


        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.group_title);
            group_image = itemView.findViewById(R.id.group_image);
        }
    }
}
