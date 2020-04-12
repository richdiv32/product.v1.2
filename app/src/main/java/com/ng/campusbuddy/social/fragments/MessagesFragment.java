package com.ng.campusbuddy.social.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.messaging.chat.Chat;
import com.ng.campusbuddy.social.messaging.chat.NewChatActivity;

import java.util.ArrayList;
import java.util.HashMap;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.ng.campusbuddy.tools.AdActivity;

import static android.content.Context.MODE_PRIVATE;


public class MessagesFragment extends Fragment {
    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        if (restorePrefData()){

        }
        else {
            TapTarget();
        }

        AddInit();
//        BadgeInit();

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        final ViewPager MessagesPager = view.findViewById(R.id.messagesPager);
        final TabLayout tabLayout = view.findViewById(R.id.tab_layout);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final MessagesPagerAdapter mPagerViewAdapter = new MessagesPagerAdapter(getChildFragmentManager());
                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && !chat.isIsseen()){
                        unread++;
                    }
                }

                if (unread == 0){
                    mPagerViewAdapter.addFragment(new Messages_ChatFragment(), "Chats");
                }
                else {
                    mPagerViewAdapter.addFragment(new Messages_ChatFragment(), "("+unread+") Chats");
                }

                mPagerViewAdapter.addFragment(new Messages_GroupFragment(), "Groups");
                mPagerViewAdapter.addFragment(new Messages_RoomFragment(), "Rooms");

                MessagesPager.setAdapter(mPagerViewAdapter);
                tabLayout.setupWithViewPager(MessagesPager);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        mPagerViewAdapter.addFragment(new Messages_ChatFragment(), "Chats");
//        mPagerViewAdapter.addFragment(new Messages_GroupFragment(), "Groups");
//        mPagerViewAdapter.addFragment(new Messages_RoomFragment(), "Rooms");
//
//        MessagesPager.setAdapter(mPagerViewAdapter);
//        tabLayout.setupWithViewPager(MessagesPager);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;
    }

//    private void BadgeInit() {
//        final TextView Message_counter = view.findViewById(R.id.message_counter);
//        final TextView Group_message_counter = view.findViewById(R.id.counter2);
//
////        DatabaseReference group_reference = FirebaseDatabase.getInstance().getReference("Groups");
////        group_reference.addValueEventListener(new ValueEventListener() {
////            @Override
////            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                int unread = 0;
////                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
////                    Chat chat = snapshot.getValue(Chat.class);
////                    if (chat.getReceiver().equals(fuser.getUid()) && !chat.isIsseen()){
////                        unread++;
////                    }
////                }
////
////                if (unread == 0){
////                    Group_message_counter.setVisibility(View.GONE);
////
////                } else {
////                    Group_message_counter.setText(String.valueOf(unread));
////                }
////
////            }
////
////            @Override
////            public void onCancelled(@NonNull DatabaseError databaseError) {
////
////            }
////        });
//    }

    private void TapTarget() {
        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
                TapTarget.forView(view.findViewById(R.id.message_fab), "New Chat", "starting sending messages to friends, create and add friends to your group or speak to a Counselor if you are having difficulties on campus")
                        .tintTarget(false)
                        .outerCircleColor(R.color.colorPrimary),
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);
                        savePrefsData();

                        view.dismiss(true);
                    }
                });
    }

    private boolean restorePrefData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isTapTargetClickedBefore = pref.getBoolean("isTapOpnend",false);
        return  isTapTargetClickedBefore;

    }
    private void savePrefsData() {

        SharedPreferences pref = getActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isTapOpnend",true);
        editor.apply();

    }

    private void AddInit() {
        final boolean[] isFABOpen = new boolean[1];


        FloatingActionButton message_fab = view.findViewById(R.id.message_fab);
        final FloatingActionButton chat_fab = view.findViewById(R.id.add_chat_fab);
        final FloatingActionButton group_fab = view.findViewById(R.id.add_group_fab);
        final FloatingActionButton counsel_fab = view.findViewById(R.id.counsel_fab);

        message_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isFABOpen[0]){
                    showFABMenu();
                }
                else {
                    closeFABMenu();
                }
            }

            private void showFABMenu() {
                isFABOpen[0] = true;

                chat_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_115));
                group_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_145));
                counsel_fab.animate().translationY(-getResources().getDimension(R.dimen.margin_175));

            }

            private void closeFABMenu() {
                isFABOpen[0] = false;

                chat_fab.animate().translationY(0);
                group_fab.animate().translationY(0);
                counsel_fab.animate().translationY(0);
            }
        });

        chat_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser ID = FirebaseAuth.getInstance().getCurrentUser();

                Intent intent = new Intent(getContext(), NewChatActivity.class);
                intent.putExtra("id", ID.getUid());
                startActivity(intent);

                Toast.makeText(getContext(), "Start new chat", Toast.LENGTH_SHORT).show();
            }
        });

        group_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Create New Group");

                final EditText groupName = new EditText(getContext());
                groupName.setHint("Type group name... ");
                builder.setView(groupName);


                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String groupName_str = groupName.getText().toString();

                        if (TextUtils.isEmpty(groupName_str)){
                            Toast.makeText(getContext(), "You can't create a group without a name", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            CreateNewGroup(groupName_str);
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

        counsel_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Chat with a counselor", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), AdActivity.class));
            }
        });
    }

    private void CreateNewGroup(final String groupName_str) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        final String group_id = groupRef.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("groupid", group_id);
        hashMap.put("group_image", "");
        hashMap.put("group_title", groupName_str);
        hashMap.put("group_users", "");
        hashMap.put("group_creator", fuser.getUid());

        groupRef.child(group_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(getContext(), groupName_str+ " has been created", Toast.LENGTH_SHORT).show();

                    //add creator to group user list
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups")
                            .child(group_id).child("group_users").child(fuser.getUid());
                    ref.child("group_userid").setValue(fuser.getUid());

                    //add to message fragment
                    final DatabaseReference Ref1 = FirebaseDatabase.getInstance().getReference("Grouplist")
                            .child(fuser.getUid())
                            .child(group_id);

                    Ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()){
                                Ref1.child("groupid").setValue(group_id);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });



    }



    class MessagesPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        MessagesPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        // Ctrl + O

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
