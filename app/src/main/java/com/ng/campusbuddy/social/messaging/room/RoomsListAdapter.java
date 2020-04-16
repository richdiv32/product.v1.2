package com.ng.campusbuddy.social.messaging.room;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;

import java.math.RoundingMode;
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
    holder.count.setVisibility(View.GONE);


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
    popupWindow.showAtLocation(popup_view, Gravity.CENTER, 0, 0);
//        popupWindow.dismiss();

    RecyclerView recyclerView = popup_view.findViewById(R.id.room_recycler);
    final List<Room> mRoom = new ArrayList<>();
    final PopupRecyclerViewAdapter adapter = new PopupRecyclerViewAdapter(mContext, mRoom);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
    recyclerView.setLayoutManager(mLayoutManager);
    recyclerView.setAdapter(adapter);



    DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Rooms");
    matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        mRoom.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
          Room room = snapshot.getValue(Room.class);
          if (room.getId_parentroom().equals(roomlist_id)){
            mRoom.add(room);
          }

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

              DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference().child("Rooms");
              String room_id = roomRef.push().getKey();

              HashMap<String, Object> hashMap = new HashMap<>();
              hashMap.put("id_chatroom", room_id);
              hashMap.put("image_chatroom", "");
              hashMap.put("title_chatroom", groupName_str);
              hashMap.put("id_parentroom", chatroom_id);

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
          FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

          DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference()
                  .child("Room_users").child(fUser.getUid());

          roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              int roomCount = (int) dataSnapshot.getChildrenCount();
              if (roomCount <= 4){
                roomIntent(room.getId_chatroom());
              }
              else {

                //inflate layout for dialog
                View view = LayoutInflater.from(mContext).inflate(R.layout.toast_layout, null);
                ImageView Image = view.findViewById(R.id.toast_img);
                TextView Text = view.findViewById(R.id.toast_txt);

                Text.setText("Maximum rooms reached, you can join 5 rooms. Exit other rooms to join new ones.");
                Image.setImageResource(R.drawable.emoji_samsung_1f60f);

                Toast toast = new Toast(mContext);
                toast.setGravity(Gravity.BOTTOM, 0 , 120);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
              }
//              String  count= String.valueOf(roomCount);
//              Toast.makeText(mContext, count, Toast.LENGTH_SHORT).show();
//              roomIntent(room.getId_chatroom());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
//          setParentId(room.getId_chatroom());

        }
      });

    }

    private void roomIntent(final String id_chatroom) {
      //progressDialog
      final ProgressDialog progressDialog = new ProgressDialog(mContext);
      progressDialog.setTitle("Joining Room");
      progressDialog.setMessage("loading room info....");
      progressDialog.show();
      progressDialog.setCanceledOnTouchOutside(false);

      FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

      DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference()
              .child("Room_users").child(fUser.getUid());


      HashMap<String, Object> hashMap = new HashMap<>();
      hashMap.put("id_chatroom", id_chatroom);
      hashMap.put("id_parentroom", roomlist_id);

      roomRef.child(id_chatroom).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          if (task.isSuccessful()){
            Toast.makeText(mContext, "Welcome...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, RoomActivity.class);
            intent.putExtra("room_id", id_chatroom);
            intent.putExtra("roomlist_id", roomlist_id);
            mContext.startActivity(intent);
            Animatoo.animateZoom(mContext);
            progressDialog.dismiss();
          }
        }
      });
    }

//    private void setParentId(String id_chatroom) {
//      DatabaseReference parentId = FirebaseDatabase.getInstance().getReference()
//              .child("Rooms").child(roomlist_id).child(id_chatroom).child("id_parentroom");
//
//      parentId.setValue(roomlist_id).addOnSuccessListener(new OnSuccessListener<Void>() {
//        @Override
//        public void onSuccess(Void aVoid) {
//          Toast.makeText(mContext, "parentroom ID added", Toast.LENGTH_SHORT).show();
//        }
//      });
//    }


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
