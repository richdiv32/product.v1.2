package com.ng.campusbuddy.education;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.education.reader.BookDetailActivity;
import com.ng.campusbuddy.utils.SharedPref;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    List<Books> booksList ;
    LibraryAdapter libraryAdapter ;

    EditText search_bar;

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
        setContentView(R.layout.activity_library);

        booksList = new ArrayList<>();
        libraryAdapter = new LibraryAdapter(this, booksList);

        search_bar = findViewById(R.id.search_bar);
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchLibrary(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        LoadLibrary();
    }

    private void searchLibrary(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Books").orderByChild("title")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                booksList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Books books = snapshot.getValue(Books.class);
                    booksList.add(books);
                }

                libraryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void LoadLibrary() {

        RecyclerView Library_recycler = findViewById(R.id.library_recycler);
        Library_recycler.hasFixedSize();
        LinearLayoutManager layoutManager = new GridLayoutManager(this, 3);
        Library_recycler.setLayoutManager(layoutManager);

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




    private class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder>{

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
                    Intent intent = new Intent(mContext, BookDetailActivity.class);
                    intent.putExtra("book_id", books.getId());
                    mContext.startActivity(intent);
                    Toast.makeText(mContext, "You clicked on: " + books.getTitle() +
                            "book id: " + books.getId(), Toast.LENGTH_SHORT).show();
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
