package com.android.uthakpathak.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.android.uthakpathak.R;
import com.android.uthakpathak.signup.SignUpActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hbb20.CountryCodePicker;


public class SignUpScreen extends AppCompatActivity {
    private static final int RC_SIGN_IN =101 ;
    //references to login and register button
    Button GoogleSignup;
    Button emailbutton;
    //references to otpbutton,editextphoneno and ccp
    EditText edittext_phoneno;
    Button button_sendotp;
    CountryCodePicker countrycode;

    GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        //method for phone authentication
        phoneAuthentication();

        //hide actionbar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //get references to email and google button
        GoogleSignup = (Button) findViewById(R.id.googleButton);
        emailbutton = (Button) findViewById(R.id.emailButton);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient   = GoogleSignIn.getClient(this, gso);

        //set onclick listener on googlebutton
        GoogleSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();

            }
        });
        //set onclick listener on emailbutton
        emailbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignUpScreen.this, SignUpActivity.class));
            }
        });
    }

    //method for phone authentication
    private void phoneAuthentication()
    {
        //get references
        countrycode= (CountryCodePicker) findViewById(R.id.ccp);
        button_sendotp=(Button)findViewById(R.id.button_sendotp);
        edittext_phoneno=(EditText)findViewById(R.id.edittext_phoneno);

        //set onclick listener to button_sendotp
        button_sendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ccode=countrycode.getSelectedCountryCode();
                String edittextnumber=edittext_phoneno.getText().toString().trim();

                //check if edittext is empty or not a valid number
                if(edittextnumber.isEmpty()||edittextnumber.length()<10)
                {
                    edittext_phoneno.setError("Invalid Number");
                    edittext_phoneno.requestFocus();
                    return;
                }
                //create complete phone number
                String phoneno="+"+ccode+edittextnumber;

                //start new activity for otp verification
                Intent intent=new Intent(SignUpScreen.this, PhoneLoginActivity.class);
                intent.putExtra("phoneno",phoneno);
                startActivity(intent);
            }
        });
    }



//google authentication
        private void signIn() {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
           // ...
                    Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
                }
            }
        }


        private void firebaseAuthWithGoogle(String idToken) {
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignUpScreen.this,user.getEmail()+user.getDisplayName(),Toast.LENGTH_LONG).show();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpScreen.this, task.getException().toString(),Toast.LENGTH_LONG).show();
                                updateUI(null);
                            }

                            // ...
                        }
                    });
        }

    private void updateUI(FirebaseUser user) {
        Intent intent= new Intent(SignUpScreen.this,LoginActivity.class);
        startActivity(intent);
    }


    //prevent from going to previous activity
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}