package com.android.uthakpathak;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;






public class SplashActivity extends AppCompatActivity {
    //variable references for animation of appname and applogo
    Animation anim_translate_lefttoright;
    Animation anim_translate_righttoleft;
    //references to applogo and appname
    ImageView imageview_applogo;
    ImageView imageview_appname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //method to load animations for appname and applogo
        loadAnimations();


        //hide actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


//         //create new handler for SplashActivity
//         new Handler().postDelayed(new Runnable() {
//             @Override
//             public void run() {
//                 //start the LoginActivity class for user to login
//                 startActivity(new Intent(SplashActivity.this, LoginActivity.class));
//             }
//         }, 2500);//open the new Activity after a delay of 2.5s
//     }

        //create new handler for com.android.uthakpathak.SplashActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //start the com.android.uthakpathak.SelectActivity class for user to login or register
                startActivity(new Intent(SplashActivity.this, SelectActivity.class));
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

}
