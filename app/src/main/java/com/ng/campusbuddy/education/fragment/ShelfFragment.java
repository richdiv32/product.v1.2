package com.ng.campusbuddy.education.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ng.campusbuddy.R;

import java.util.HashMap;


public class ShelfFragment extends Fragment {
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_shelf, container, false);



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
                            hashMap.put("bookInfo", "");
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


}
