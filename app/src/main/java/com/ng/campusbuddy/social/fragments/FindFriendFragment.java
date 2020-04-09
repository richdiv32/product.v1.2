package com.ng.campusbuddy.social.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.model.Contest;
import com.ng.campusbuddy.profile.UserProfileActivity;
import com.ng.campusbuddy.social.UserAdapter;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.tools.NearbyActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class FindFriendFragment extends Fragment {
  View view;

  private RecyclerView recyclerView;
  private UserAdapter userAdapter;
  private List<User> userList;

  EditText search_bar;

  TashieLoader PD;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.fragment_find_friend, container, false);

    recyclerView = view.findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    search_bar = view.findViewById(R.id.search_bar);

    userList = new ArrayList<>();
    userAdapter = new UserAdapter(getContext(), userList, true);
    recyclerView.setAdapter(userAdapter);

    readUsers();
    search_bar.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        searchUsers(charSequence.toString().toLowerCase());
      }

      @Override
      public void afterTextChanged(Editable editable) {

      }
    });

    Face_of_week();
    TapTarget();
    Nearby();

    return view;
  }

  private void Nearby() {
    CardView P_nearby = view.findViewById(R.id.nearby);
    P_nearby.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

          ToMap();
        }
        else {
          final String[] PERMISSIONS_STORAGE = {Manifest.permission.ACCESS_FINE_LOCATION};
          //Asking request Permissions
          ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, 1);
        }
      }
    });
  }

  private void ToMap() {
    startActivity(new Intent(getActivity(), NearbyActivity.class));
    Animatoo.animateFade(getActivity());
  }

  private void TapTarget() {
//        TapTargetView.showFor(getActivity(),                 // `this` is an Activity
//                TapTarget.forView(view.findViewById(R.id.face), "Personality of the week", "See the most influential student on the platform, It could be you...")
//                        .tintTarget(false)
//                        .outerCircleColor(R.color.colorPrimary));
  }

  private void Face_of_week() {
    final CardView layout = view.findViewById(R.id.face);
    final ImageView image = view.findViewById(R.id.face_of_week_bg);
    final TextView username = view.findViewById(R.id.username);
    final TextView department = view.findViewById(R.id.department);

    PD = view.findViewById(R.id.loader);

    DatabaseReference contestRef = FirebaseDatabase.getInstance().getReference()
            .child("Contest").child("Face_of_week");

    contestRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.exists()){

        }
        final Contest contest = dataSnapshot.getValue(Contest.class);

        Glide.with(getContext())
                .load(contest.getImageURL())
                .placeholder(R.drawable.placeholder)
                .listener(new RequestListener<Drawable>() {
                  @Override
                  public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            PD.setVisibility(View.GONE);
                    return false;
                  }

                  @Override
                  public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            PD.setVisibility(View.GONE);
                    return false;
                  }
                })
                .thumbnail(0.1f)
                .into(image);

        username.setText(contest.getUsername());


        final DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference()
                .child("Users");

        UserRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for (DataSnapshot ds : dataSnapshot.getChildren()){
              final User user = ds.getValue(User.class);

              if (contest.getGender().equals(user.getGender()) &&
                      contest.getTelephone().equals(user.getTelephone())){

                department.setText(user.getDepartment());

                layout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    getActivity().startActivity(new Intent(getContext(), UserProfileActivity.class));
                    Animatoo.animateSplit(getContext());
                  }
                });

              }
            }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
        });

      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void searchUsers(String s){
    Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
            .startAt(s)
            .endAt(s+"\uf8ff");

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userList.clear();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
          User user = snapshot.getValue(User.class);
          userList.add(user);
        }

        userAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  private void readUsers() {

    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

    reference.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (search_bar.getText().toString().equals("")) {
          userList.clear();
          for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = snapshot.getValue(User.class);

            userList.add(user);

          }

          userAdapter.notifyDataSetChanged();
        }
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    boolean permissionGranted = false;
    switch (requestCode){
      case 1:
        permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        break;
    }
    if (permissionGranted){
      ToMap();
    }
    else {
      Toast.makeText(getActivity(), "You've not allowed permission", Toast.LENGTH_SHORT).show();
    }
  }
}
