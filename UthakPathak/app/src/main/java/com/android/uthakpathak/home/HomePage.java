package com.android.uthakpathak.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.CaseMap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.LocaleList;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.uthakpathak.R;
import com.android.uthakpathak.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javassist.compiler.KeywordTable;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback {
    final String TAG = "MainActivity";
    FirebaseAuth auth;
    String emailLink, email;
    SharedPreferences sharedPreferences;
    ActivityMainBinding mainBinding;
    HomeViewModel viewModel;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOC_PERMISSION_REQ_CODE = 1234;
    private static final Float DEFAULT_ZOOM = 15f;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Boolean permission_granted;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        auth=FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        Intent intent = getIntent();
        emailLink = intent.getDataString();
        sharedPreferences = getSharedPreferences("email", MODE_APPEND);
        email = sharedPreferences.getString("email", null);

        getLocationPermissions();

        //method call to sign in user using email link
        viewModel = new ViewModelProvider(getViewModelStore(), new HomeViewModelFactory(email, emailLink)).get(HomeViewModel.class);

        viewModel.getAuthenticationSuccesFull().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mainBinding.progressBar2.setVisibility(View.INVISIBLE);
                }
            }
        });

        //hide progress bar after login
        if (auth.getCurrentUser() != null) {
            mainBinding.progressBar2.setVisibility(View.INVISIBLE);
        }
    }

    //ask for permissions granted to app
    private void getLocationPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permission_granted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, LOC_PERMISSION_REQ_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOC_PERMISSION_REQ_CODE);
        }
    }

    //check if permission has been granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission_granted = false;
        switch (requestCode) {
            case LOC_PERMISSION_REQ_CODE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            permission_granted = false;
                            return;
                        }
                    }
                    permission_granted = true;
                    initMap();
                }
        }
    }

    //initialize map
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        mapFragment.getMapAsync(HomePage.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        //set current location of user and disable default location button
        if (permission_granted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            initialize();
        }
    }

    //get current location of user
    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permission_granted) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location currentlocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentlocation.getLatitude(),currentlocation.getLongitude()),DEFAULT_ZOOM,"My Location");
                        } else {
                            Toast.makeText(HomePage.this, "Unable to get current location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
        }
    }

    //set map to a particular location
    private void moveCamera(LatLng latLng, Float zoom, String title)
    {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        //create a marker and add to map
        MarkerOptions options=new MarkerOptions()
                .position(latLng)
                .title(title);
        googleMap.addMarker(options);
    }

    //set onclick listener on edittext
    private void initialize()
    {
        //set onclick listner on keyclick of pickup edt
        mainBinding.pickupLocEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
                if(actionid== EditorInfo.IME_ACTION_SEARCH
                           ||actionid==EditorInfo.IME_ACTION_DONE
                           ||actionid==EditorInfo.IME_ACTION_NEXT
                           ||keyEvent.getAction()== KeyEvent.ACTION_DOWN
                           ||keyEvent.getAction()==KeyEvent.KEYCODE_ENTER){
                    //method to locate entered location
                    geoLocate();
                }
                return false;
            }
        });
    }

    //method to locate user searched location
    private void geoLocate()
    {
        String pickup_loc=mainBinding.pickupLocEdt.getText().toString().trim();

        Geocoder geocoder=new Geocoder(HomePage.this);
        List<Address> list=new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(pickup_loc,1);
        }
        catch (IOException e) {
            Log.e("Homepage","geolocate:IO Exception: "+e.getMessage());
        }
        if(list.size()>0) {
            Address address = list.get(0);
            //move to user defined location
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

}