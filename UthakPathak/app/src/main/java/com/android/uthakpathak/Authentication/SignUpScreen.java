package com.android.uthakpathak.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.android.uthakpathak.R;
import com.android.uthakpathak.signup.SignUpActivity;


public class SignUpScreen extends AppCompatActivity {
    //references to login and register button
    Button googlebutton;
    Button emailbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        Button GoogleSignup= findViewById(R.id.googleButton);
        //hide actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //get references to email and google button
        googlebutton = (Button) findViewById(R.id.googleButton);
        emailbutton = (Button) findViewById(R.id.emailButton);

        //set onclick listener on googlebutton
        googlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignUpScreen.this, LoginActivity.class));

            }
        });
        //set onclick listener on emailbutton
        emailbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                startActivity(new Intent(SignUpScreen.this, SignUpActivity.class));


            }
        });
    }

    //prevent from going to previous activity
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}