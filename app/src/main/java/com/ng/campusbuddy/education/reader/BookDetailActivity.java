package com.ng.campusbuddy.education.reader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.education.Books;
import com.ng.campusbuddy.utils.SharedPref;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class BookDetailActivity extends AppCompatActivity {

    Intent intent;
    String book_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true){
            setTheme(R.style.AppDarkTheme);
        }
        else{
            setTheme(R.style.AppTheme);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            //removes status bar with background
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


//                startActivity(new Intent(BookDetailActivity.this , PDFActivity.class));
            }
        });


        intent = getIntent();
        book_id = intent.getStringExtra("book_id");


        final DatabaseReference booksRef = FirebaseDatabase.getInstance().getReference().child("Books").child(book_id);

        booksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Books books1 = dataSnapshot.getValue(Books.class);

                ImageView bookImage = findViewById(R.id.image);
                Picasso.get().load(books1.getCoverImage()).placeholder(R.drawable.placeholder).into(bookImage);

                TextView bookTitle = findViewById(R.id.title);
                bookTitle.setText(books1.getTitle());

                TextView bookInfo = findViewById(R.id.bookinfo);
                bookInfo.setText(books1.getBookInfo());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

//

