package com.example.danish.projectmessenger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class UserAuthentication extends AppCompatActivity {

    private Button mLogin;
    private Button mSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);

        mSignUp = (Button)findViewById(R.id.signUp_button);
        mLogin = (Button)findViewById(R.id.logIn_button);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(UserAuthentication.this, LogIn.class);
                startActivity(login);
                finish();
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(UserAuthentication.this, SignUP.class);
                startActivity(signUp);
                finish();

            }
        });
    }
}
