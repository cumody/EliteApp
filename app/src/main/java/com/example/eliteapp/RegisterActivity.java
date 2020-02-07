package com.example.eliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    Button CreateAccountbutton;
    private EditText Useremail, Userpassword;
    private TextView AlreadyHaveaccountlink;
    private ProgressDialog loadingbar;
    private DatabaseReference Rootref;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        Rootref = FirebaseDatabase.getInstance().getReference();

        Initializefeild();

        AlreadyHaveaccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendusertoLoginActivity();
            }
        });
        CreateAccountbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreatenewAccount();
            }
        });

    }

    private void CreatenewAccount() {
        String email = Useremail.getText().toString();
        String password = Userpassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
        } else {

            loadingbar.setTitle("Creating new account ");
            loadingbar.setMessage("Please wait , while we're creating your new account");
            loadingbar.setCanceledOnTouchOutside(true);
            loadingbar.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String message = mAuth.getCurrentUser().getUid();
                                Rootref.child("Users").child(message).setValue("");

                                SendtoMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                        }
                    });
        }
    }

    private void SendtoMainActivity() {

        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
    }

    private void Initializefeild() {
        CreateAccountbutton = findViewById(R.id.register_button);
        Useremail = findViewById(R.id.register_email);
        Userpassword = findViewById(R.id.register_password);
        AlreadyHaveaccountlink = findViewById(R.id.already_have_an_acount);
        loadingbar = new ProgressDialog(this);


    }

    private void SendusertoLoginActivity() {


        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
