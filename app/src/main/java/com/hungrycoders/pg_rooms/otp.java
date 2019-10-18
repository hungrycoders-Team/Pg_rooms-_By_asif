package com.hungrycoders.pg_rooms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class otp extends AppCompatActivity {
    String number,strName ,StrEmail;
    Button BtncreateAccount;
    EditText editOtp;
    String verificationId;
    FirebaseAuth firebaseAuth;
    EditText email,name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        BtncreateAccount=findViewById(R.id.BTnCreateAccount);
        editOtp=findViewById(R.id.otpCode);
        email=findViewById(R.id.loginEmail);
        name=findViewById(R.id.loginName);
        strName=name.getText().toString();
        StrEmail=email.getText().toString();

        Intent i=getIntent();
        i.putExtra("name",strName);
        i.putExtra("email",StrEmail);
        number=i.getStringExtra("number");


        //number is obtained
        BtncreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode(number);
            }
        });


    }
    public void sendVerificationCode(String phonenumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber, 60, TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack


        );
    }
    private  PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code!=null){
                editOtp.setText(code);
                verifyCode(code);

            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(otp.this,e.getMessage(),Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId=s;
        }

    };
    private void verifyCode(String code){
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(credential);

    }
    private void signInWithCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(otp.this,"Succesful",Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Toast.makeText(otp.this,"failed",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }



}
