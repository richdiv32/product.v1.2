package com.ng.campusbuddy.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.social.SocialActivity;
import com.ng.campusbuddy.utils.Token;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {


    FirebaseAuth mAuth;
    DatabaseReference reference;
    CardView pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        pd = findViewById(R.id.loader);

        InitLogIn();
        InitSignUp();
        googleauth();
    }

    private void InitLogIn() {
        LinearLayout LogIn = findViewById(R.id.login_link);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                Animatoo.animateSwipeLeft(SignUpActivity.this);
                finish();
            }
        });
    }

    private void InitSignUp() {

        final EditText Email = findViewById(R.id.email);
        final EditText Password = findViewById(R.id.password);
        final EditText Comfirm_password = findViewById(R.id.confirm_password);


        Button SignUp = findViewById(R.id.btn_sign_up);



        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pd.setVisibility(View.VISIBLE);

//                pd = new ProgressDialog(SignUpActivity.this);
//                pd.setTitle("Creating New Account");
//                pd.setMessage("Please wait, this will only take seconds");
//                pd.show();
//                pd.setCanceledOnTouchOutside(true);

                String str_email = Email.getText().toString();
                String str_password = Password.getText().toString();
                String str_confirm_password = Comfirm_password.getText().toString();


                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_confirm_password)){
                    pd.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();

                }
                else if(str_password.length() < 6){
                    pd.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Password must have 6 characters!", Toast.LENGTH_SHORT).show();

                }
                else if(!str_password.equals(str_confirm_password)){
                    pd.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, "Your password does not match with your confirm password", Toast.LENGTH_SHORT).show();
                }
                else {
                    register(str_email, str_password, str_confirm_password);
                }

            }
        });
    }

    private void register(final String Email, String Password,
                          String Confirm_password) {
        pd.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            SendVerificationEmail();
                        }
                        else {
                            pd.setVisibility(View.GONE);
                            String message = task.getException().getMessage();
                            Toast.makeText(SignUpActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void SendVerificationEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(SignUpActivity.this, "Weldon, We sent a verifcation mail to you. Please check your email...", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                        Animatoo.animateSwipeRight(SignUpActivity.this);
                        finish();
                        mAuth.signOut();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SignUpActivity.this, "Error: " + message, Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                }
            });
        }

    }

    private void Home() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if (firebaseUser != null) {
            final String current_uid = firebaseUser.getUid();

            DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

            UserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(current_uid)) {
                        startActivity(new Intent(SignUpActivity.this, SetUpProfileActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                    else {
                        String deviceToken = FirebaseInstanceId.getInstance().getToken();
                        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference()
                                .child("Tokens");
                        Token mtoken = new Token(deviceToken);
                        tokenRef.child(current_uid).setValue(mtoken )
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            startActivity(new Intent(SignUpActivity.this, SocialActivity.class)
                                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                            Animatoo.animateZoom(SignUpActivity.this);
                                            finish();
                                        }
                                    }
                                });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

    }

    static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;

    private void googleauth(){

        //Configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        ImageButton GoogleAuth = findViewById(R.id.google_btn);

        GoogleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Result returned from launching the Intent from GoogleSignInClient
        if (requestCode == RC_SIGN_IN){
            //The Task returned from this call is always completed, no nee to attach a listner
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Google sign in was successful, authenticate with firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            }
            catch (ApiException e){

            }

        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct){

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Sign in success
                            Home();

                        }
                        else {
                            //if sign in fails
//                            Snackbar.make(findViewById(R.id.), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(SignUpActivity.this, "Login Failed.... ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
