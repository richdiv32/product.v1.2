package com.ng.campusbuddy.profile;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.social.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileInfoFragment extends Fragment {
    Context context;
    View view;


    TextView Email, Bio, Birthday, Gender, Relationship_status
            , Institution, Faculty, Department,Telephone;

    FirebaseUser firebaseUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Init();
        Ref();
        return view;
    }

    private void Init() {
        Email = view.findViewById(R.id.email);
        Bio = view.findViewById(R.id.bio);
        Birthday = view.findViewById(R.id.birthday);
        Relationship_status = view.findViewById(R.id.relationship_status);
        Institution = view.findViewById(R.id.institution);
        Faculty = view.findViewById(R.id.faculty);
        Department = view.findViewById(R.id.department);
        Telephone = view.findViewById(R.id.telephone_number);
        Gender = view.findViewById(R.id.gender);
    }

    private void Ref() {
        final DatabaseReference Info_reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        Info_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Bio.setText(user.getBio());
                Birthday.setText(user.getBirthday());
                Gender.setText(user.getGender());
                Institution.setText(user.getInstitution());
                Faculty.setText(user.getFaculty());
                Department.setText(user.getDepartment());
                Telephone.setText(user.getTelephone());
                Relationship_status.setText(user.getRelationship_status());
                if (!dataSnapshot.child("email").exists()){
                    Info_reference.child("email").setValue(firebaseUser.getEmail());
                }
                else {
                    Email.setText(user.getEmail());
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
