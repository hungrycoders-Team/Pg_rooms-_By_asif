package com.hungrycoders.pg_rooms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.IndicatorView.draw.controller.DrawController;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    Button btnGoogle;
    ImageButton btnPhone;
    EditText editPhone;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN =1 ;
    private FirebaseAuth mAuth;
    ///verification confirmation string
    String verificationId;
    /////slider
    SliderView sliderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /////////////slider implement
        sliderView = findViewById(R.id.imageSlider);

        final SliderAdapterExample adapter = new SliderAdapterExample(this);
        adapter.setCount(5);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.SLIDE); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.rgb(244, 92, 67));
        sliderView.setIndicatorUnselectedColor(Color.rgb(251, 248, 248));
        sliderView.startAutoCycle();

        sliderView.setOnIndicatorClickListener(new DrawController.ClickListener() {
            @Override
            public void onIndicatorClicked(int position) {
                sliderView.setCurrentPagePosition(position);
            }
        });




        /////////main
        btnGoogle = findViewById(R.id.Maingooglebutton);
        btnPhone = findViewById(R.id.id_search_button);
        editPhone = findViewById(R.id.id_search_EditText);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Build a GoogleSignInClient with the options specified by gso.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        ///gso gmail signin
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();


            }
        });
        btnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking the number
                String number = editPhone.getText().toString().trim();
                if (number.isEmpty() || number.length() < 10) {
                    editPhone.setError("Enter a valid number");
                    editPhone.requestFocus();
                    return;
                }
                final String phonenumber = "+" + "91" + number;
                Intent i=new Intent(Login.this,otp.class);
                i.putExtra("number",phonenumber);
                startActivity(i);




            }
        });


    }

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
                firebaseAuthWithGoogle(account);
                Toast.makeText(Login.this,"loggedin",Toast.LENGTH_SHORT).show();
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(Login.this,"failed",Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }


    //update ui function

    private void updateUI(FirebaseUser currentUser) {
        Toast.makeText(Login.this, "logged in", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(Login.this, HomeActivity.class);
        startActivity(i);
    }





    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            finish();
//                            startActivity(new Intent(Login.this, HomeActivity.class));
                            updateUI(user);
                            Toast.makeText(Login.this,"success",Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            // Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);


                            Toast.makeText(Login.this, "error", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    //////////////////////////////////phone
//    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
//            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//        @Override
//        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//            String code = phoneAuthCredential.getSmsCode();
//            if (code != null) {
//                ed3.setText(code);
//                verifyCode(code);
//
//            }
//
//        }
//    }
//
//    @Override
//    public void onVerificationFailed(@NonNull FirebaseException e) {
//        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//        super.onCodeSent(s, forceResendingToken);
//        verificationId = s;
//    }
//
//    private void verifyCode(String code) {
//        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//        signInWithCredential(credential);
//
//    }
//
//    private void signInWithCredential(PhoneAuthCredential credential) {
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(Login.this, "Succesful", Toast.LENGTH_SHORT).show();
//
//                        } else {
//                            Toast.makeText(Login.this, "failed", Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//    }
//
//    public void sendVerificationCode(String phonenumber) {
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phonenumber, 60, TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                mCallBack
//
//
//        );
//
//
//    }
}


