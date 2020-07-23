package com.android.uthakpathak.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.uthakpathak.home.HomePage;
import com.android.uthakpathak.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    //code sent on the phone
    private String verificationid;
    //reference to firebase auth
    private FirebaseAuth firebaseAuth;
    //reference to progressbar
    private ProgressBar progressBar;
    //reference to edittextotp
    private EditText edittextgetotp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);

        //get phoneno from intent
        String phoneno = getIntent().getStringExtra("phoneno");

        //get instance of firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        //reference to progress bar and edittextgetotp
        progressBar = findViewById(R.id.progressBar);
        edittextgetotp = (EditText) findViewById(R.id.editext_getotp);

        //method to send verfication code
        sendVerificationCode(phoneno);

        //set on click listener on phone login button to get manually entered code and call method for verification
        findViewById(R.id.phonelogin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get code entered by user
                String code = edittextgetotp.getText().toString().trim();
                //display error message if code is empty or not valid
                if (code.isEmpty() || code.length() < 6) {
                    edittextgetotp.setError("Enter code");
                    edittextgetotp.requestFocus();
                    return;
                }
                //call method for code verification
                verifyCode(code);
            }
        });
    }

    //method to send verification code
    private void sendVerificationCode(String phoneno) {
        //display progress bar
        progressBar.setVisibility(View.VISIBLE);
        //get instance of firebase phoneauthprovider and send and verify message
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneno, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }

    //callback after sending code
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        //get verification id after code is sent to user
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationid = s;
        }

        //to detect sent code automatically and login
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                //set code in edittextgetotp
                edittextgetotp.setText(code);
                //call method for code verification
                verifyCode(code);
            }
        }

        //show error message if verification fails
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(PhoneLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    //method to verify otp entered by user or automatically detected
    private void verifyCode(String code) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationid, code);
        //method to sign in with provided credentials
        signInWithCredential(phoneAuthCredential);
    }

    //method to sign in with given credentials
    private void signInWithCredential(PhoneAuthCredential credential) {
        //sign in and add listener to it
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Start MainActivity on successful signin
                            Intent intent = new Intent(PhoneLoginActivity.this, HomePage.class);
                             intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             startActivity(intent);
                        } else {
                            //display error message
                            Toast.makeText(PhoneLoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}