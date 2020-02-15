package com.ng.campusbuddy.start;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import com.ng.campusbuddy.R;
import com.ng.campusbuddy.auth.LoginActivity;
import com.ng.campusbuddy.auth.SignUpActivity;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.social.SocialActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button LogIn,SignUp;

    FirebaseUser firebaseUser;


    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //check if user is null
        if (firebaseUser != null){
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Setup_Login();
        SetUp_SignUp();
        googleauth();
    }

    private void Setup_Login() {

        LogIn = (Button) findViewById(R.id.btn_sign_in);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);
                Animatoo.animateSlideRight(WelcomeActivity.this);
            }
        });
    }

    private void SetUp_SignUp() {
        SignUp = (Button) findViewById(R.id.btn_sign_up);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
                Animatoo.animateSlideLeft(WelcomeActivity.this);
            }
        });
    }

    private void Home() {
        startActivity(new Intent(this, HomeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        Animatoo.animateZoom(this);
        finish();
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

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
                            Toast.makeText(WelcomeActivity.this, "Login Failed.... ", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(WelcomeActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
