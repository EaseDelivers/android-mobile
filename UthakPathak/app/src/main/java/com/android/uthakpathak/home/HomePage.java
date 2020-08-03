package com.android.uthakpathak.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.uthakpathak.Authentication.SignUpScreen;
import com.android.uthakpathak.R;
import com.android.uthakpathak.databinding.ActivityMainBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback {
    final String TAG="MainActivity";
    FirebaseAuth auth;
    String emailLink,email;
    SharedPreferences sharedPreferences;
    ActivityMainBinding mainBinding;
    HomeViewModel viewModel;

    private static final String FINE_LOCATION=Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION=Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOC_PERMISSION_REQ_CODE=1234;
    private GoogleMap googleMap;

    Boolean permission_granted;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        init();

        getSupportActionBar().hide();

        Intent intent=getIntent();
        emailLink=intent.getDataString();
        sharedPreferences=getSharedPreferences("email",MODE_APPEND);
        email=sharedPreferences.getString("email",null);

        getLocationPermissions();

        //method call to sign in user using email link
        viewModel=new ViewModelProvider(getViewModelStore(),new HomeViewModelFactory(email,emailLink)).get(HomeViewModel.class);

        viewModel.getAuthenticationSuccesFull().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                 if(aBoolean){
                     mainBinding.progressBar2.setVisibility(View.INVISIBLE);
                 }
            }
        });

        //hide progress bar after login
        if(auth.getCurrentUser()!=null)
        {
            mainBinding.progressBar2.setVisibility(View.INVISIBLE);
        }
    }

    public void init(){
        auth=FirebaseAuth.getInstance();
    }

    //ask for permissions granted to app
    private void getLocationPermissions()
    {
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
                permission_granted=true;
            }
            else {
                ActivityCompat.requestPermissions(this,permissions,LOC_PERMISSION_REQ_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOC_PERMISSION_REQ_CODE);
        }
    }

    //check if permission has been granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission_granted=false;
        switch (requestCode)
        {
            case LOC_PERMISSION_REQ_CODE:
                if(grantResults.length>0)
                {
                    for(int i=0;i<grantResults.length;i++)
                    {
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED)
                        {
                            permission_granted=false;
                            return;
                        }
                    }
                    permission_granted=true;
                    initMap();
                }
        }
    }

    private void initMap()
    {
        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(HomePage.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
    }
}