package com.ng.campusbuddy.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Configure google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        //Build a GoogleSignInClient with the options specified by gso
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        auth = FirebaseAuth.getInstance();

        InitLogin();
        InitSignUp();
        InitForgotPassword();
//        googleauth();



        TextView verification = findViewById(R.id.resen_verifiaction_link);
        verification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setTitle("Sending Verification Code");
                pd.setMessage("Please wait.....");
                pd.show();
                pd.setCanceledOnTouchOutside(true);

                final EditText Email = findViewById(R.id.email);
                final EditText Password = findViewById(R.id.password);

                String str_email = Email.getText().toString();
                String str_password = Password.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(LoginActivity.this, "Input your email and password", Toast.LENGTH_SHORT).show();

                }
                else {

                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        SendVerificationEmail();
                                    }
                                    else {
                                        pd.dismiss();
                                        String message = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });


        googleauth();

    }

    private void InitForgotPassword() {
        TextView ForgotPassword = findViewById(R.id.forgot_password_link);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
                Animatoo.animateSwipeLeft(LoginActivity.this);
            }
        });
    }

    private void InitSignUp() {
        TextView SignUp = findViewById(R.id.signup_link);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                Animatoo.animateSwipeRight(LoginActivity.this);
                finish();
            }
        });
    }



    private void InitLogin() {

        final EditText Email = findViewById(R.id.email);
        final EditText Password = findViewById(R.id.password);

        Button LogIn = findViewById(R.id.btn_sign_in);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setTitle("Logging In ");
                pd.setMessage("Please wait.....");
                pd.show();
                pd.setCanceledOnTouchOutside(true);

                String str_email = Email.getText().toString();
                String str_password = Password.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {

                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        CheckVerification();
                                        pd.dismiss();
                                    }
                                    else {
                                        pd.dismiss();
                                        String message = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    private void CheckVerification() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Boolean emailCheck = user.isEmailVerified();

        if (emailCheck ){
            Home();
        }
        else {
            Toast.makeText(this, "Please check your mail to verify your account first", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    private void Home() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        Animatoo.animateZoom(this);
        finish();
    }

    private void SendVerificationEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(LoginActivity.this, "Weldon, We sent a verifcation mail to you. Please check your email...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                        Animatoo.animateSwipeRight(LoginActivity.this);
                        finish();
                        auth.signOut();
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
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
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Sign in success
                            FirebaseUser user = auth.getCurrentUser();
                            Home();

                        }
                        else {
                            //if sign in fails
//                            Snackbar.make(findViewById(R.id.), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "Login Failed.... ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
