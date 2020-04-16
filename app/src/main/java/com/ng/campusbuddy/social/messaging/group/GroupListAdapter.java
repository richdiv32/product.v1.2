package com.ng.campusbuddy.social.messaging.group;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private Context mContext;
    private List<Group> mGroups;

    private FirebaseUser firebaseUser;


    public GroupListAdapter(Context mContext, List<Group> mGroups) {
        this.mContext = mContext;
        this.mGroups = mGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_group_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Group group = mGroups.get(position);

        holder.title.setText(group.getGroup_title());


        if (group.getGroup_image().equals("")){
            holder.group_image.setImageResource(R.drawable.chat_bg);
        }
        else {

            Glide.with(mContext)
                    .load(group.getGroup_image())
                    .placeholder(R.drawable.chat_bg)
                    .thumbnail(0.1f)
                    .into(holder.group_image);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GroupChatActivity.class);
                intent.putExtra("groupid", group.getGroupid());
                mContext.startActivity(intent);
                Animatoo.animateZoom(mContext);

            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                // show delete message confirm dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Delete");
                builder.setMessage("Are you sure to delete this group?");
                //delete button
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteChat(group.getGroupid());

                    }
                });
                //cancel delete button
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.dismiss();
                    }
                });

                //create and show dialog
                builder.create().show();
                return false;
            }
        });



    }

        private void deleteChat(final String groupid) {
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DatabaseReference dbRef2 = FirebaseDatabase.getInstance().getReference("Groups")
                    .child(groupid).child("group_users");
            Query query2 = dbRef2.orderByChild("group_userid");
            query2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        if (ds.child("group_userid").getValue().equals(myUID)){

                            //To remove the message completly from chat
                            ds.getRef().removeValue();

                            Toast.makeText(mContext, "You left", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Grouplist")
                .child(myUID);
        Query query = dbRef.orderByChild("groupid");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("groupid").getValue().equals(groupid)){

                        //To remove the message completly from chat
                        ds.getRef().removeValue();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
