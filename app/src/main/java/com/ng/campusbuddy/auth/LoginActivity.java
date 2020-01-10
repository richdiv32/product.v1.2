package com.ng.campusbuddy.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ng.campusbuddy.R;
import com.ng.campusbuddy.home.HomeActivity;
import com.ng.campusbuddy.social.SocialActivity;

public class LoginActivity extends AppCompatActivity {

    Button LogIn;
    TextView SignUp, ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitLogin();
        InitSignUp();
        InitForgotPassword();

    }

    private void InitForgotPassword() {
        ForgotPassword = findViewById(R.id.forgot_password_link);
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void InitSignUp() {
        SignUp = findViewById(R.id.signup_link);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }



    private void InitLogin() {

        LogIn = findViewById(R.id.btn_sign_in);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

            }
        });
    }
}
