package splashscreen;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import com.android.uthakpathak.R;

import Authentication.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //hide actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //create new handler for SplashActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //start the LoginActivity class for user to login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
        }, 2500);//open the new Activity after a delay of 2.5s
    }
}