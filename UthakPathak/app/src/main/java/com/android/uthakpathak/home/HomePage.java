package com.android.uthakpathak.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.uthakpathak.Authentication.SignUpScreen;
import com.android.uthakpathak.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {
    final String TAG = "MainActivity";
    FirebaseAuth auth;
    String emailLink, email;
    SharedPreferences sharedPreferences;
    ActionBar actionBar;
    ActivityMainBinding mainBinding;
    HomeViewModel viewModel;
    boolean flag;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        init();


        Intent intent = getIntent();
        flag = intent.getBooleanExtra("flag", true);

        if (flag) {

            emailLink = intent.getDataString();
            sharedPreferences = getSharedPreferences("email", MODE_APPEND);
            email = sharedPreferences.getString("email", null);
            viewModel = new ViewModelProvider(getViewModelStore(), new HomeViewModelFactory(email, emailLink)).get(HomeViewModel.class);
            viewModel.getAuthenticationSuccesFull().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        mainBinding.progressBar2.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else {
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show();
            mainBinding.progressBar2.setVisibility(View.INVISIBLE);
        }


    }


    public void init() {
        auth = FirebaseAuth.getInstance();

    }
}