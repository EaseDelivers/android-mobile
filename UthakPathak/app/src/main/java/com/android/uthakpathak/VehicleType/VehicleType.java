package com.android.uthakpathak.VehicleType;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import com.android.uthakpathak.R;
import com.google.android.material.tabs.TabLayout;

public class VehicleType extends AppCompatActivity {
    private ViewPager viewPager;
private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_type);

        viewPager=findViewById(R.id.viewPager);
        tabLayout=findViewById(R.id.tabLayout);
        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new Motorcycle(),"Motorcycle");
        viewPagerAdapter.addFragment(new TataAce(),"Tata Ace");
        viewPagerAdapter.addFragment(new TataTruckFragment(),"Tata Truck");
        viewPagerAdapter.addFragment(new ThreeWheelerFragment(),"3 Wheeler");
        viewPagerAdapter.addFragment(new EcoFragment(),"Eco");
        viewPagerAdapter.addFragment(new Bolero(),"Bolero");
        viewPager.setAdapter(viewPagerAdapter);

    }
}