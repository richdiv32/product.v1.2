package com.ng.campusbuddy.start;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ng.campusbuddy.R;
import com.ng.campusbuddy.auth.LoginActivity;
import com.ng.campusbuddy.auth.SignUpActivity;
import com.ng.campusbuddy.social.SocialActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button LogIn,SignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Setup_Login();
        SetUp_SignUp();
    }

    private void Setup_Login() {

        LogIn = (Button) findViewById(R.id.btn_sign_in);

        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void SetUp_SignUp() {
        SignUp = (Button) findViewById(R.id.btn_sign_up);

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
            }
        });
    }
}
