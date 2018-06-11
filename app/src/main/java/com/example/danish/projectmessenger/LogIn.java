package com.example.danish.projectmessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;

    private Button mLogin;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mEmail = (TextInputLayout)findViewById(R.id.login_email_editText);
        mPassword = (TextInputLayout)findViewById(R.id.login_password_editText);
        mLogin = (Button)findViewById(R.id.logIn__button);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logIn();

            }
        });

    }

    private void logIn() {
        if(isValidInput()){
            setupProgressDialog("Login user", "Please wait for your authentication").show();
            mAuth.signInWithEmailAndPassword(getText(mEmail), getText(mPassword)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        Intent mainActivity = new Intent(LogIn.this, MainActivity.class);
                        startActivity(mainActivity);
                        finish();
                        Toast("Authentication successful:- user email >>>" + mAuth.getCurrentUser().getEmail());

                    }else{
                        startActivity(new Intent(LogIn.this, UserAuthentication.class));
                        finish();
                        Toast("Login failed");
                    }
                    mProgressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(LogIn.this, UserAuthentication.class));
        finish();
    }

    private boolean isValidInput(){
        if(isNull(mEmail) || isNull(mPassword)){
            return false;
        }
        return true;
    }

    private boolean isNull(TextInputLayout textInputLayout){
        if(getText(textInputLayout).equals("")){
            return true;
        }
        return false;
    }

    private String getText(TextInputLayout textInputLayout){
        return textInputLayout.getEditText().getText().toString().trim();
    }

    private ProgressDialog setupProgressDialog(String title, String message){
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }

    private void Toast(String s){
        Toast.makeText(LogIn.this, s, Toast.LENGTH_SHORT).show();
    }

}
