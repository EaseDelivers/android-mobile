package com.android.uthakpathak.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.uthakpathak.databinding.ActivityEmailAuthenticationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

public class EmailAuthentication extends AppCompatActivity {

    //get reference of firbase auth and aeabinding
    ActivityEmailAuthenticationBinding mainBinding;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityEmailAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        auth = FirebaseAuth.getInstance();

        sharedPreferences=getSharedPreferences("email",MODE_APPEND);
        editor=sharedPreferences.edit();


        //use actioncodesettings to create url and callback activity
        final ActionCodeSettings actionCodeSettings
                = ActionCodeSettings.newBuilder()
                .setUrl("https://utha.page.link/signIn")
                .setAndroidPackageName("com.android.uthakpathak", false, "19")
                .setHandleCodeInApp(true)
                .build();


        //set listener on continue button
        mainBinding.emailauthContinuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send verification link to email
                auth.sendSignInLinkToEmail(mainBinding.emailauthEmail.getText().toString(), actionCodeSettings)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    //show success notification

                                    editor.putString("email",mainBinding.emailauthEmail.getText().toString());
                                    editor.commit();
                                    Toast.makeText(EmailAuthentication.this, "Open Your Mail Application", Toast.LENGTH_SHORT).show();



                                }
                            }
                        });
            }
        });
    }
}