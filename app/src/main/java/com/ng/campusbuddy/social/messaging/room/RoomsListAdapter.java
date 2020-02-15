package com.ng.campusbuddy.social.messaging.room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoomsListAdapter extends RecyclerView.Adapter<RoomsListAdapter.ViewHolder> {

    private Context mContext;
    private List<Room> mRoom;

    private FirebaseUser firebaseUser;
    private String roomlist_id;

    public RoomsListAdapter(Context mContext, List<Room> mRoom) {

        this.mContext = mContext;
        this.mRoom = mRoom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        final Room room = mRoom.get(position);

        holder.title.setText(room.getTitle_chatroom());


        if (room.getImage_chatroom().equals("")){
            holder.image.setImageResource(R.drawable.chat_bg);
        }
        else {

            Picasso.get()
                    .load(room.getImage_chatroom())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image);

            Picasso.get()
                    .load(room.getImage_chatroom())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.bg);
        }



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup(room.getId_chatroom());
            }
        });

    }


    @Override
    public int getItemCount() {
        return mRoom.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView title;
        public ImageView image, bg;
        private TextView count;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.chat_room_imgae);
            bg = itemView.findViewById(R.id.chat_room_imgae_bg);
            count = itemView.findViewById(R.id.count);
        }
    }

    private void showPopup(final String chatroom_id) {
        final  View popup_view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_room_recycler, null);
        final PopupWindow popupWindow = new PopupWindow(popup_view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
//        popupWindow.dismiss();

        RecyclerView recyclerView = popup_view.findViewById(R.id.room_recycler);
        final List<Room> mRoom = new ArrayList<>();
        final PopupRecyclerViewAdapter adapter = new PopupRecyclerViewAdapter(mContext, mRoom);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        popupWindow.showAtLocation(popup_view, Gravity.CENTER, 0, 0);

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Rooms")
                .child(chatroom_id);
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRoom.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Room room = snapshot.getValue(Room.class);
                    mRoom.add(room);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button Add = popup_view.findViewById(R.id.add);
        Add.setVisibility(View.GONE);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Create New Room");

                final EditText groupName = new EditText(mContext);
                groupName.setHint("Type room name... ");
                builder.setView(groupName);


                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String groupName_str = groupName.getText().toString();

                        if (TextUtils.isEmpty(groupName_str)){
                            Toast.makeText(mContext, "You can't create a room without a name", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference().child("Rooms")
                                    .child(chatroom_id);
                            String room_id = roomRef.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id_chatroom", room_id);
                            hashMap.put("image_chatroom", "");
                            hashMap.put("count", "");
                            hashMap.put("title_chatroom", groupName_str);

                            roomRef.child(room_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(mContext, groupName_str+ " has been created", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss dialog
                        dialog.cancel();
                    }
                });

                //create and show dialog
                builder.create().show();
            }
        });

        roomlist_id = chatroom_id;
    }









    public class PopupRecyclerViewAdapter extends RecyclerView.Adapter<PopupRecyclerViewAdapter.ViewHolder> {

        private Context mContext;
        private List<Room> mRoom;


        public PopupRecyclerViewAdapter(Context mContext, List<Room> mRoom) {

            this.mContext = mContext;
            this.mRoom = mRoom;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_room, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


            final Room room = mRoom.get(position);

            holder.title.setText(room.getTitle_chatroom());


            if (room.getImage_chatroom().equals("")){
                holder.image.setImageResource(R.drawable.chat_bg);
            }
            else {

                Picasso.get()
                        .load(room.getImage_chatroom())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.image);

                Picasso.get()
                        .load(room.getImage_chatroom())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.bg);
            }



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RoomActivity.class);
                    intent.putExtra("room_id", room.getId_chatroom());
                    intent.putExtra("roomlist_id", roomlist_id);
                    mContext.startActivity(intent);
                    Animatoo.animateZoom(mContext);

                }
            });

        }


        @Override
        public int getItemCount() {
            return mRoom.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{

            public TextView title;
            public ImageView image, bg;
            private TextView count;

            public ViewHolder(View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.title);
                bg = itemView.findViewById(R.id.chat_room_imgae_bg);
                image = itemView.findViewById(R.id.chat_room_imgae);
                count = itemView.findViewById(R.id.count);
            }
        }

    }
}
