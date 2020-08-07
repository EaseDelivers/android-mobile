package com.android.uthakpathak;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.uthakpathak.Authentication.SignUpScreen;

import com.android.uthakpathak.VehicleType.NavigationDrawer;
import com.android.uthakpathak.VehicleType.VehicleType;
import com.android.uthakpathak.home.HomePage;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SplashActivity extends AppCompatActivity {
    //variable references for animation of appname and applogo
    Animation anim_translate_lefttoright;
    Animation anim_translate_righttoleft;
    //references to applogo and appname
    ImageView imageview_applogo;
    ImageView imageview_appname;
    //reference of FirebaseAuth and FirebaseUser
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //method to load animations for appname and applogo
        loadAnimations();

        //hide actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //instantiate FirebaseAuth
        firebaseAuth=FirebaseAuth.getInstance();

        //create new handler for com.android.uthakpathak.SplashActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(firebaseUser!=null)
                {
                    //start Homepage if a user is already logged in
                   Intent intent=new Intent(SplashActivity.this, NavigationDrawer.class);
                   startActivity(intent);
                }
                else
                {
                    //start the SignUpScreenActivity for user to login or register
                    startActivity(new Intent(SplashActivity.this, HomePage.class));
                }

            }
        }, 2500);//open the new Activity after a delay of 2.5s
    }

    //method to load animations for appname and applogo
    private void loadAnimations()
    {
        //get references to applogo and appname
        imageview_applogo=(ImageView)findViewById(R.id.splashscreen_applogo);
        imageview_appname=(ImageView)findViewById(R.id.splashscreen_appnname);

        //load animations for applogo and appname and start animations
        anim_translate_lefttoright=AnimationUtils.loadAnimation(this,R.anim.translate_lefttoright);
        anim_translate_righttoleft=AnimationUtils.loadAnimation(this,R.anim.translate_righttoleft);
        imageview_applogo.startAnimation(anim_translate_lefttoright);
        imageview_appname.startAnimation(anim_translate_righttoleft);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //get current firebase user
        firebaseUser=firebaseAuth.getCurrentUser();

    }
}
