package com.android.uthakpathak.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.android.uthakpathak.R;
import com.android.uthakpathak.signup.SignUpActivity;
import com.hbb20.CountryCodePicker;


public class SignUpScreen extends AppCompatActivity {
    //references to login and register button
    Button googlebutton;
    Button emailbutton;
    //references to otpbutton,editextphoneno and ccp
    EditText edittext_phoneno;
    Button button_sendotp;
    CountryCodePicker countrycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        //method for phone authentication
        phoneAuthentication();

        Button GoogleSignup = findViewById(R.id.googleButton);
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

    //method for phone authentication
    private void phoneAuthentication() {
        //get references
        countrycode = (CountryCodePicker) findViewById(R.id.ccp);
        button_sendotp = (Button) findViewById(R.id.button_sendotp);
        edittext_phoneno = (EditText) findViewById(R.id.edittext_phoneno);

        //set onclick listener to button_sendotp
        button_sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ccode = countrycode.getSelectedCountryCode();
                String edittextnumber = edittext_phoneno.getText().toString().trim();

                //check if edittext is empty or not a valid number
                if (edittextnumber.isEmpty() || edittextnumber.length() < 10) {
                    edittext_phoneno.setError("Invalid Number");
                    edittext_phoneno.requestFocus();
                    return;
                }
                //create complete phone number
                String phoneno = "+" + ccode + edittextnumber;

                //start new activity for otp verification
                Intent intent = new Intent(SignUpScreen.this, PhoneLoginActivity.class);
                intent.putExtra("phoneno", phoneno);
                startActivity(intent);
            }
        });
    }

    //prevent from going to previous activity
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}