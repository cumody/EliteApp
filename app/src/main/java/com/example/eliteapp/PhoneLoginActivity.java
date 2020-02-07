package com.example.eliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    Button  Send_verification_code , Verify_button;
    private EditText InputPhonenumber , InputVerificationcode;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;
    private PhoneAuthProvider.ForceResendingToken mResendToken;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        mAuth = FirebaseAuth.getInstance();
        loadingbar = new ProgressDialog(this);


        Send_verification_code = findViewById(R.id.send_verification_button);
        Verify_button = findViewById(R.id.verify_button);

        InputPhonenumber = findViewById(R.id.phone_number_input);
        InputVerificationcode = findViewById(R.id.verification_input);


        Send_verification_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String phoneNumber = InputPhonenumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Phone number enter your phone nubmer first", Toast.LENGTH_SHORT).show();


                } else {
                    loadingbar.setTitle("Phone verification");
                    loadingbar.setMessage("please wait , while authentication your phone");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);        // OnVerificationStateChangedCallbacks
                }


            }


        });

        Verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Send_verification_code.setVisibility(View.INVISIBLE);
                InputPhonenumber.setVisibility(View.INVISIBLE);

                String verificationcode = InputVerificationcode.getText().toString();

                if (TextUtils.isEmpty(verificationcode)){
                    Toast.makeText(PhoneLoginActivity.this, "Please write   ", Toast.LENGTH_SHORT).show();
                } else {

                    loadingbar.setTitle("Code verification");
                    loadingbar.setMessage("please wait , while verfication your phone");
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }



            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid Phone number with your country code ", Toast.LENGTH_SHORT).show();

                Send_verification_code.setVisibility(View.VISIBLE);
                InputPhonenumber.setVisibility(View.VISIBLE);


                Verify_button.setVisibility(View.INVISIBLE);
                InputVerificationcode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {


                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingbar.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code has sent", Toast.LENGTH_SHORT).show();

                // ...
            }
        };

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            loadingbar.dismiss();
                            Toast.makeText(PhoneLoginActivity.this, "Congratulations", Toast.LENGTH_SHORT).show();
                            Sendusertomainactivity();


                            // ...
                        } else {
                            String error = task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        }

                });
    }

    private void Sendusertomainactivity() {
        Intent mainintent = new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }

}
