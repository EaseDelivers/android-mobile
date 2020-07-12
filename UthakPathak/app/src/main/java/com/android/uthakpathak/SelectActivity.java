package com.android.uthakpathak;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import login.LoginActivity;
import signup.SignUpActivity;

public class SelectActivity extends AppCompatActivity {
    //references to login and register button
    Button loginbutton;
    Button registerbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        //get references to login and register button
        loginbutton = (Button) findViewById(R.id.selectscreen_loginbutton);
        registerbutton = (Button) findViewById(R.id.selectscreen_registerbutton);

        //set onclick listener on registerbutton
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move to LoginActivity
                startActivity(new Intent(SelectActivity.this, LoginActivity.class));
            }
        });
        //set onclick listener on loginbutton
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //move to SignUpActivity
                startActivity(new Intent(SelectActivity.this, SignUpActivity.class));
            }
        });
    }

    //prevent from going to previous activity
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}