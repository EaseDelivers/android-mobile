package com.android.uthakpathak.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.uthakpathak.R;
import com.android.uthakpathak.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.libraries.places.widget.Autocomplete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback,TaskLoadedCallback {
    final String TAG = "MainActivity";
    FirebaseAuth auth;
    String emailLink, email;
    SharedPreferences sharedPreferences;
    ActivityMainBinding mainBinding;
    HomeViewModel viewModel;
    Polyline polyline;
    Marker current_marker;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOC_PERMISSION_REQ_CODE = 1234;
    private static int SRC_REQ_CODE = 1;
    private static int DEST_REQ_CODE = 2;
    private static final Float DEFAULT_ZOOM = 15f;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Boolean permission_granted;
    MarkerOptions place1,place2;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        auth = FirebaseAuth.getInstance();

        getSupportActionBar().hide();

        Intent intent = getIntent();
        emailLink = intent.getDataString();
        sharedPreferences = getSharedPreferences("email", MODE_APPEND);
        email = sharedPreferences.getString("email", null);

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

        //method call to check location permissions
        getLocationPermissions();

        //initialize place sdk
        Places.initialize(getApplicationContext(),getResources().getString(R.string.google_api_key));

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
        mainBinding.progressBar2.setVisibility(View.INVISIBLE);
        this.googleMap = googleMap;
        //set current location of user and disable default location button
        if (permission_granted) {
            //set current location on pickup button
            getDeviceLocation(1);
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            initialize();
        }
    }

    //get current location of user
    private void getDeviceLocation(final int reqtype) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (permission_granted) {
                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Location curr_location = (Location) task.getResult();
                            Geocoder geocoder = new Geocoder(HomePage.this);
                            List<Address> list = new ArrayList<>();
                            try {
                                list = geocoder.getFromLocation(curr_location.getLatitude(), curr_location.getLongitude(), 1);
                            } catch (IOException e) {
                                Log.e("Homepage", "geolocate:IO Exception: " + e.getMessage());
                            }
                            if (list.size() > 0) {
                                Address address = list.get(0);
                                if (reqtype == 1) {
                                    mainBinding.pickupLocBt.setText(address.getAddressLine(0));
                                    moveCamera(new LatLng(curr_location.getLatitude(), curr_location.getLongitude()), DEFAULT_ZOOM, "My Location");
                                }
                                else if(reqtype==2){
                                    mainBinding.dropLocBt.setText(address.getAddressLine(0));
                                }
                                else if(reqtype==3){
                                    mainBinding.pickupLocBt.setText(address.getAddressLine(0));
                                }
                                else {
                                    Toast.makeText(HomePage.this, "Unable to get current location", Toast.LENGTH_LONG).show();
                                }
                            }
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        if(current_marker!=null){
            current_marker.setPosition(latLng);
        }
        else {
            //create a marker and add to map
            MarkerOptions options=new MarkerOptions()
                    .position(latLng)
                    .title(title);
            current_marker=googleMap.addMarker(options);
        }
    }

    //set onclick listener on buttons
    private void initialize()
    {
        //set onclick listener on pickup button
        mainBinding.pickupLocBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(HomePage.this);
                    startActivityForResult(intent,200);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        //set onclick listener on drop button
        mainBinding.dropLocBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,fields).build(HomePage.this);
                    startActivityForResult(intent,400);
                }
                catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        mainBinding.pickupCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainBinding.dropLocBt.getText().toString().isEmpty()){
                    getDeviceLocation(1);
                    mainBinding.dropLocBt.requestFocus();
                }
                else
                {
                    getDeviceLocation(3);
                    drawTrack(mainBinding.pickupLocBt.getText().toString(),mainBinding.dropLocBt.getText().toString());
                }
            }
        });

        mainBinding.dropCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation(2);
                drawTrack(mainBinding.pickupLocBt.getText().toString(),mainBinding.dropLocBt.getText().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                String name = place.getName();
                mainBinding.pickupLocBt.setTag(name);
                if(!(mainBinding.dropLocBt.getText().toString().isEmpty())){
                    drawTrack(mainBinding.pickupLocBt.getText().toString(),mainBinding.dropLocBt.getText().toString());
                }
                else
                {
                    moveCamera(place.getLatLng(),DEFAULT_ZOOM,place.getName());
                    mainBinding.dropLocBt.requestFocus();
                }
            }

            if(requestCode==400){
                if(resultCode==RESULT_OK) {
                    Place place = Autocomplete.getPlaceFromIntent(data);
                    String name = place.getName();
                    mainBinding.dropLocBt.setText(name);
                    drawTrack(mainBinding.pickupLocBt.getText().toString(),mainBinding.dropLocBt.getText().toString());
                }
            }
        }
    }

    private void drawTrack(String source,String Destination){
        Geocoder geocoder=new Geocoder(this);
        List<Address> srcaddress=new ArrayList<>();
        try{
            srcaddress=geocoder.getFromLocationName(source,1);
        }
        catch (IOException e){}
        List<Address> destaddress=new ArrayList<>();
        try{
            destaddress=geocoder.getFromLocationName(Destination,1);
        }
        catch (IOException e){}

        if(srcaddress.size()>0&&destaddress.size()>0) {
            place1 = new MarkerOptions().position(new LatLng(srcaddress.get(0).getLatitude(), srcaddress.get(0).getLongitude())).
                    title(mainBinding.pickupLocBt.getText().toString());
            place2=new MarkerOptions().position(new LatLng(destaddress.get(0).getLatitude(), destaddress.get(0).getLongitude())).
                    title(mainBinding.dropLocBt.getText().toString());
            new FetchURL(HomePage.this)
                    .execute(getUrl(place1.getPosition(),place2.getPosition(),"driving"),"driving");
        }
    }

    private String getUrl(LatLng origin,LatLng dest,String drivingmode)
    {
        String str_origin="origin="+origin.latitude+","+origin.longitude;
        String str_dest="destination="+dest.latitude+","+dest.longitude;
        String mode="mode="+drivingmode;
        String param=str_origin+"&"+str_dest+"&"+mode;
        String format="json";
        String url="https://maps.googleapis.com/maps/api/directions/"+format+"?"+param+"&key=AIzaSyA_xCMiOYJqSzGxXlfVsi0dClikmX4-2KU";
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if(polyline!=null)
        {
            polyline.remove();
        }
        polyline=googleMap.addPolyline((PolylineOptions) values[0]);
    }
}