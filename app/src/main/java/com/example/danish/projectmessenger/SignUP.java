package com.example.danish.projectmessenger;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUP extends AppCompatActivity {

    private final static String TAG = "error";

    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private TextInputLayout mConfirmPassword;

    private Button mSignup;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;

    private DatabaseReference mDb;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();



        mProgressDialog = new ProgressDialog(this);

        mEmail = (TextInputLayout)findViewById(R.id.email_editText);
        mPassword = (TextInputLayout)findViewById(R.id.password_editText);
        mConfirmPassword = (TextInputLayout)findViewById(R.id.cnfrmPassword_editText);

        mSignup = (Button)findViewById(R.id.SignUp_button);

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signUp();
            }
        });

    }

    private void signUp(){
        String email = getText(mEmail);
        String password = getText(mPassword);

        if(isValidPassword()){
            setupProgressDialog("Registering User", "Please wait while user is being registered").show();


            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                addUser(user);

                                Intent mainActivity = new Intent(SignUP.this, MainActivity.class);
                                startActivity(mainActivity);
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                startActivity(new Intent(SignUP.this, UserAuthentication.class));
                                finish();
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast("Authentication failed.");

                            }

                            mProgressDialog.dismiss();
                        }
                    });
        }
    }

    private void addUser(FirebaseUser user) {
        mDb = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());

        HashMap<String, String> details = new HashMap<>();
        details.put("displayName", getString(R.string.default_display_name));
        details.put("status", getString(R.string.default_status));
        details.put("image", "default");
        details.put("thumbnail", "default");

        mDb.setValue(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast("success");
                }
                if (!task.isSuccessful()){
                    Toast("unsuccessful");
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUP.this, UserAuthentication.class));
        finish();
    }

    private boolean isValidPassword(){
        if(isNull(mEmail) || isNull(mPassword) || isNull(mConfirmPassword)){
            return false;
        }
        if(!isEqual(mPassword, mConfirmPassword)){
            return false;
        }

        return true;
    }

    private boolean isEqual(TextInputLayout one, TextInputLayout two){
        if(getText(one).equals(getText(two))){
            return true;
        }
        return false;
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
        Toast.makeText(SignUP.this, s, Toast.LENGTH_SHORT).show();
    }
}
