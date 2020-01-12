package com.ng.campusbuddy.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.social.SocialActivity;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        InitLogIn();
        InitSignUp();
    }

    private void InitLogIn() {
        LinearLayout LogIn = findViewById(R.id.login_link);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void InitSignUp() {

        final EditText Fullname = findViewById(R.id.fullname);
        final EditText Username = findViewById(R.id.username);
        final EditText Email = findViewById(R.id.email);
        final EditText Password = findViewById(R.id.password);
        final EditText Comfirm_password = findViewById(R.id.confirm_password);
        final EditText Birthday = findViewById(R.id.birthday);
        final EditText Relationship_status = findViewById(R.id.relationship_status);
        final EditText Telephone = findViewById(R.id.telephone_number);
        final EditText Institution = findViewById(R.id.institution);
        final EditText Faculty = findViewById(R.id.faculty);
        final EditText Department = findViewById(R.id.department);
        final EditText Bio = findViewById(R.id.bio);

        Button SignUp = findViewById(R.id.btn_sign_up);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(SignUpActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_fullname = Fullname.getText().toString();
                String str_username = Username.getText().toString();
                String str_email = Email.getText().toString();
                String str_password = Password.getText().toString();
                String str_confirm_password = Comfirm_password.getText().toString();
                String str_birthday = Birthday.getText().toString();
                String str_relationship_status = Relationship_status.getText().toString();
                String str_telephone = Telephone.getText().toString();
                String str_institution = Institution.getText().toString();
                String str_faculty = Faculty.getText().toString();
                String str_department = Department.getText().toString();
                String str_bio = Bio.getText().toString();


                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email)
                        || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_confirm_password) || TextUtils.isEmpty(str_birthday)
                        || TextUtils.isEmpty(str_relationship_status) || TextUtils.isEmpty(str_telephone) || TextUtils.isEmpty(str_institution)
                        || TextUtils.isEmpty(str_faculty) || TextUtils.isEmpty(str_department) || TextUtils.isEmpty(str_bio)){
                    Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                } else if(str_password.length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
                } else {
                    register(str_username, str_fullname, str_email, str_password, str_confirm_password,
                            str_birthday, str_relationship_status, str_telephone, str_institution,
                            str_faculty, str_department, str_bio);
                }

            }
        });
    }

    private void register(final String Username, final String Fullname, final String Email, String Password,
                          String Confirm_password, final String Birthday, final String Relationship_status,
                          final String Telephone, final String Institution, final String Faculty, final String Department,
                          final String Bio) {

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("username", Username.toLowerCase());
                            map.put("fullname", Fullname);
                            map.put("email", Email);
                            map.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/campus-buddy-2019.appspot.com/o/profile_image.svg?alt=media&token=549d47e6-6355-4367-bf69-0df75853401f");
                            map.put("status", "offline");
                            map.put("birthday", Birthday);
                            map.put("telephone", Telephone);
                            map.put("relations_status", Relationship_status);
                            map.put("institution", Institution);
                            map.put("faculty", Faculty);
                            map.put("department", Department);
                            map.put("bio", Bio);


                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(SignUpActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
