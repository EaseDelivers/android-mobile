package com.android.uthakpathak.VehicleType;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.uthakpathak.R;
import com.google.android.material.tabs.TabLayout;



public class VehicleType extends Fragment {
    private ViewPager viewPager;
private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_vehicle_type, container, false);

        viewPager=view.findViewById(R.id.viewPager);
        tabLayout=view.findViewById(R.id.tabLayout);
        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
    private void setViewPager(ViewPager viewPager){
        ViewPagerAdapter viewPagerAdapter= new ViewPagerAdapter(getFragmentManager());
        viewPagerAdapter.addFragment(new Motorcycle(),"Motorcycle");
        viewPagerAdapter.addFragment(new TataAce(),"Tata Ace");
        viewPagerAdapter.addFragment(new TataTruckFragment(),"Tata Truck");
        viewPagerAdapter.addFragment(new ThreeWheelerFragment(),"3 Wheeler");
        viewPagerAdapter.addFragment(new EcoFragment(),"Eco");
        viewPagerAdapter.addFragment(new Bolero(),"Bolero");
        viewPager.setAdapter(viewPagerAdapter);

    }
}