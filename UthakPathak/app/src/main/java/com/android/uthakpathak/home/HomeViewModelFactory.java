package com.android.uthakpathak.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

public class HomeViewModelFactory implements ViewModelProvider.Factory {


    String Email,EmailLink;


   public HomeViewModelFactory(String Email,String EmailLink){
        this.Email=Email;
        this.EmailLink=EmailLink;
    }



    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

       if(modelClass.isAssignableFrom(HomeViewModel.class)){
           return (T) new HomeViewModel(Email,EmailLink);
       }

       return null;
    }
}
