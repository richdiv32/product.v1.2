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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ng.campusbuddy.R;

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

        final EditText Username = findViewById(R.id.username);
        final EditText Email = findViewById(R.id.email);
        final EditText Password = findViewById(R.id.password);
        final EditText Comfirm_password = findViewById(R.id.confirm_password);


        Button SignUp = findViewById(R.id.btn_sign_up);



        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(SignUpActivity.this);
                pd.setTitle("Creating New Account");
                pd.setMessage("Please wait, this will only take seconds");
                pd.show();
                pd.setCanceledOnTouchOutside(true);

                String str_username = Username.getText().toString().toLowerCase();
                String str_email = Email.getText().toString();
                String str_password = Password.getText().toString();
                String str_confirm_password = Comfirm_password.getText().toString();


                if (TextUtils.isEmpty(str_username)  || TextUtils.isEmpty(str_email)
                        || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_confirm_password)){
                    Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
                else if(str_password.length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();
                }
                else if(!str_password.equals(str_confirm_password)){
                    Toast.makeText(SignUpActivity.this, "Your password does not match with your confirm password", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(str_username, str_email, str_password, str_confirm_password);
                }

            }
        });
    }

    private void register(final String Username, final String Email, String Password,
                          String Confirm_password) {

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
                            map.put("fullname", "");
                            map.put("email", Email);
                           map.put("imageurl", "");
                            map.put("online_status", "online");
                            map.put("search", Username.toLowerCase());
                            map.put("birthday", "");
                            map.put("telephone", "");
                            map.put("relationship_status", "");
                            map.put("institution", "");
                            map.put("faculty", "");
                            map.put("department", "");
                            map.put("bio", "");
                            map.put("gender", "");
                            map.put("profile_status", "Hey there, I am on Campus Buddy");


                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(SignUpActivity.this, SetUpProfileActivity.class);
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
