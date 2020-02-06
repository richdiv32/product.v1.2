package com.ng.campusbuddy.education.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.education.Books;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ShelfFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shelf, container, false);


        LoadLibrary();


        TextView good = view.findViewById(R.id.textView2);
        good.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Create New Book");

                final EditText groupName = new EditText(getContext());
                groupName.setHint("Type book title... ");
                builder.setView(groupName);


                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final String groupName_str = groupName.getText().toString();

                        if (TextUtils.isEmpty(groupName_str)){
                            Toast.makeText(getContext(), "You can't create a room without a name", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference().child("Books");
                            String book_id = roomRef.push().getKey();

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", book_id);
                            hashMap.put("publisher", "");
                            hashMap.put("coverImage", "");
                            hashMap.put("pages", "");
                            hashMap.put("bookURL", "");
                            hashMap.put("title", groupName_str);

                            roomRef.child(book_id).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Toast.makeText(getContext(), groupName_str+ " has been created", Toast.LENGTH_SHORT).show();
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

        return view;
    }

    private void LoadLibrary() {
        final List<Books> booksList = new ArrayList<>();
        RecyclerView Library_recycler = view.findViewById(R.id.library_recycler);
        Library_recycler.hasFixedSize();
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        Library_recycler.setLayoutManager(layoutManager);
        final LibraryAdapter libraryAdapter = new LibraryAdapter(getContext(), booksList);
        Library_recycler.setAdapter(libraryAdapter);

        final DatabaseReference books = FirebaseDatabase.getInstance().getReference().child("Books");

        books.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                booksList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Books books1 = ds.getValue(Books.class);

                    booksList.add(books1);
                }

                libraryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder>{

        private Context mContext;
        private List<Books> booksList;

        public LibraryAdapter(Context mContext, List<Books> booksList) {
            this.mContext = mContext;
            this.booksList = booksList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_browse, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final Books books = booksList.get(position);

            holder.bookTitle.setText(books.getTitle());

            Glide.with(mContext)
                    .load(books.getCoverImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.bookImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Clicked" + books.getId(), Toast.LENGTH_SHORT).show();
                }
            });

            holder.FindCourses.setVisibility(View.GONE);
            holder.PopularCourses.setVisibility(View.GONE);
            holder.Recommended.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return booksList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView bookImage;
            TextView bookTitle;
            CardView FindCourses, PopularCourses;
            LinearLayout Recommended;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                bookImage = itemView.findViewById(R.id.rec_image);
                bookTitle = itemView.findViewById(R.id.rec_title);

                FindCourses = itemView.findViewById(R.id.find_courses_tab_layout);
                PopularCourses = itemView.findViewById(R.id.popular_courses_layout);
                Recommended = itemView.findViewById(R.id.recommended_layout);
            }
        }
    }
}
