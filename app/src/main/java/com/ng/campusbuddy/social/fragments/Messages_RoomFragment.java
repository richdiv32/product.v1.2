package com.ng.campusbuddy.social.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.messaging.room.Room;
import com.ng.campusbuddy.social.messaging.room.RoomActivity;
import com.ng.campusbuddy.utils.CustomRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Messages_RoomFragment extends Fragment {
    View view;
    Context context;

    CustomRecyclerView UserRoom_recycler;
    private List<Room> usersRoomList;
    private UsersRoomAdapter usersRoomAdapter;
    private List<Room> mRooms;

    FirebaseUser fuser;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        view = inflater.inflate(R.layout.fragment_messages__room, container, false);

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        LoadRoomList();

        return view;
    }

    private void LoadRoomList() {
        UserRoom_recycler = view.findViewById(R.id.rooms_recycler);
        UserRoom_recycler.setEmptyView(view.findViewById(R.id.empty_item));
        UserRoom_recycler.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        UserRoom_recycler.setLayoutManager(mLayoutManager);
        usersRoomList = new ArrayList<>();


        reference = FirebaseDatabase.getInstance().getReference("Room_users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersRoomList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Room roomlist = snapshot.getValue(Room.class);
                    usersRoomList.add(roomlist);
                }

                RoomList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void RoomList() {
        mRooms = new ArrayList<>();
        usersRoomAdapter = new UsersRoomAdapter(getContext(), mRooms);
        UserRoom_recycler.setAdapter(usersRoomAdapter);
        reference = FirebaseDatabase.getInstance().getReference("Rooms");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mRooms.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Room room = snapshot.getValue(Room.class);
                    for (Room roomlist : usersRoomList){
                        if (room.getId_chatroom().equals(roomlist.getId_chatroom())){
                            mRooms.add(room);
                        }
                    }
                }

                Collections.reverse(mRooms);
                usersRoomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    class UsersRoomAdapter extends RecyclerView.Adapter<UsersRoomAdapter.ViewHolder> {

        private Context mContext;
        private List<Room> mRoom;


        public UsersRoomAdapter(Context mContext, List<Room> mRoom) {

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

                Glide.with(mContext)
                        .load(room.getImage_chatroom())
                        .thumbnail(0.1f)
                        .into(holder.image);

                Glide.with(mContext)
                        .load(room.getImage_chatroom())
                        .thumbnail(0.1f)
                        .into(holder.bg);
            }



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RoomActivity.class);
                    intent.putExtra("room_id", room.getId_chatroom());
                    intent.putExtra("roomlist_id", room.getId_parentroom());
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


