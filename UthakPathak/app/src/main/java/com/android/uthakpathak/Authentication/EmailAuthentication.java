package com.android.uthakpathak.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.uthakpathak.R;
import com.android.uthakpathak.databinding.ActivityEmailAuthenticationBinding;

public class EmailAuthentication extends AppCompatActivity {

    ActivityEmailAuthenticationBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=ActivityEmailAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

    }
}