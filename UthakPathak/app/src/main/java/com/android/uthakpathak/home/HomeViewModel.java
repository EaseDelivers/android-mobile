package com.android.uthakpathak.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class HomeViewModel extends ViewModel{


    MutableLiveData<Boolean> authenticationSuccesFull=new MutableLiveData<>();
    FirebaseAuth auth;
    String Email;
    String EmailLink;


    public HomeViewModel(String email,String EmailLink){
        this.Email=email;
        this.EmailLink=EmailLink;

        auth=FirebaseAuth.getInstance();
    }


    public MutableLiveData<Boolean> getAuthenticationSuccesFull() {

        auth.signInWithEmailLink(Email,EmailLink).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isComplete()){
                    authenticationSuccesFull.setValue(true);
                }
                else{
                    authenticationSuccesFull.setValue(false);
                }
            }
        });

        return authenticationSuccesFull;
    }
}