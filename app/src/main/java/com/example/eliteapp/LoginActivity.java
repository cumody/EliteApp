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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    /*FirebaseUser firebaseUser;*/
    Button login_button , phoneloginbutton;
    private EditText Useremail,Userpassword;
    private TextView NeednewAccountlink , ForgetPassword;
    FirebaseAuth mAuth;
    private ProgressDialog loadingbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        /*firebaseUser = mAuth.getCurrentUser();*/

        Initializefeild();

        NeednewAccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendusertoRegisterActivity();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Allowusertologin();
            }
        });

        phoneloginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneacitivty = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(phoneacitivty);

            }
        });
    }

    private void Allowusertologin() {

        String email = Useremail.getText().toString();
        String password = Userpassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else {

            loadingbar.setTitle("Sign in");
            loadingbar.setMessage("Please wait  ");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        SendusertoMainActivity();
                        Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();
                        loadingbar.dismiss();
                    } else{

                            String message = task.getException().toString();
                            Toast.makeText(LoginActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                    }
                }
            });


        }


        }

    private void Initializefeild() {
        login_button = findViewById(R.id.login_button);
        phoneloginbutton = findViewById(R.id.phone_button);
        Useremail = findViewById(R.id.login_email);
        Userpassword = findViewById(R.id.login_password);
        NeednewAccountlink = findViewById(R.id.need_new_account_link);
        ForgetPassword = findViewById(R.id.forgetpassword);

        loadingbar = new ProgressDialog(this);

    }


  /*  @Override
    protected void onStart() {
        super.onStart();

        if (firebaseUser != null) {
            SendusertoMainnActivity();
        }
    }*/

    private void SendusertoMainActivity() {


        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void SendusertoRegisterActivity() {

        Intent regisiteracitivty = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(regisiteracitivty);
    }
}
