package com.ng.campusbuddy.social.match;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.User;
import com.ng.campusbuddy.social.messaging.chat.ChatActivity;
import com.ng.campusbuddy.social.messaging.group.GroupListAdapter;
import com.ng.campusbuddy.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mMatchesLayoutManager;
    List<Match> resultsMatches;

    private String cusrrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cusrrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mMatchesLayoutManager = new GridLayoutManager(MatchesActivity.this, 3);
        mRecyclerView.setLayoutManager(mMatchesLayoutManager);
        resultsMatches = new ArrayList<>();





        getUserMatch();


    }

    private void getUserMatch() {

        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").
                child(cusrrentUserID).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                resultsMatches.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Match match = snapshot.getValue(Match.class);
                    resultsMatches.add(match);
                }

                FetchMatchInformation();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void FetchMatchInformation() {
        final List<User> mUser = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (Match match : resultsMatches){
                        if (user.getId().equals(match.getUserId())){
                            mUser.add(user);
                        }
                    }
                }
                mMatchesAdapter = new MatchesAdapter(mUser, MatchesActivity.this);
                mRecyclerView.setAdapter(mMatchesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






    public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder>{
        private List<User> matchesList;
        private Context context;


        public MatchesAdapter(List<User> matchesList, Context context){
            this.matchesList = matchesList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_matches, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            final User match = matchesList.get(position);

            holder.username.setText(match.getUsername());


            Picasso.get()
                    .load(match.getImageurl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.profile_image);



            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("userid", match.getId());
                    context.startActivity(intent);
                    Animatoo.animateZoom(context);

                }
            });



        }

        @Override
        public int getItemCount() {
            return matchesList.size();
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{

            public TextView matchId, username;
            public ImageView profile_image;


            public ViewHolder(View itemView) {
                super(itemView);

                matchId = itemView.findViewById(R.id.Matchid);
                username = itemView.findViewById(R.id.MatchName);
                profile_image = itemView.findViewById(R.id.MatchImage);
            }
        }
    }

}

