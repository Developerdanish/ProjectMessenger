package com.example.danish.projectmessenger;

import android.arch.core.executor.DefaultTaskExecutor;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mDB;

    private android.support.v7.widget.Toolbar mToolbar;

    private ViewPager mViewPager;

    private MainPageAdapter mMainPageAdapter;

    private TabLayout mTabLayout;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("MainActivity");

        mAuth = FirebaseAuth.getInstance();


        mViewPager = (ViewPager)findViewById(R.id.pager);
        mTabLayout = (TabLayout)findViewById(R.id.tabLayout);

        mMainPageAdapter = new MainPageAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mMainPageAdapter);

        mTabLayout.setupWithViewPager(mViewPager);



    }

    @Override
    protected void onStart() {
        super.onStart();
        if ((mAuth.getCurrentUser()) == null) {
            goToUserAuthentication();
        }else{
            mDB = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
            mDB.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mAuth.getCurrentUser() != null){
            mDB.child("online").setValue("false");
            mDB.child("time").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void goToUserAuthentication(){
        Intent userAuthenticationActivity = new Intent(MainActivity.this, UserAuthentication.class);
        startActivity(userAuthenticationActivity);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if(id == R.id.logout_menu){
            mAuth.signOut();
            mDB.child("online").setValue("false");
            mDB.child("time").setValue(ServerValue.TIMESTAMP);
            goToUserAuthentication();
        }else if(id == R.id.setting_menu){
            Intent accountSetting = new Intent(MainActivity.this, AccountSetting.class);
            startActivity(accountSetting);
        }else{
            Toast("error");
        }



        return true;

    }

    private void Toast(String s){
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }
}
