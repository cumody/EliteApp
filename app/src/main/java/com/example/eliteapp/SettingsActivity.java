package com.example.eliteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button UpdateAcoountsettings;
    private EditText userName, userStatues;
    private CircleImageView userProfileimage;
    private String currentuserId;
    private FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    public static final int GALLERY_PICK = 1;
    private StorageReference UserProfileImagesRef;
    private ProgressBar loadingbar;
    private Toolbar mtoolbar;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Inatializefields();

        mtoolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        mAuth = FirebaseAuth.getInstance();
        currentuserId = mAuth.getCurrentUser().getUid();
        Rootref = FirebaseDatabase.getInstance().getReference();
        loadingbar = new ProgressBar(this);

        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        UpdateAcoountsettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Updatesettings();

            }
        });

        RetrieveUserInfo();


        userProfileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryintent = new Intent();
                galleryintent.setAction(Intent.ACTION_GET_CONTENT);
                galleryintent.setType("image/*");
                startActivityForResult(galleryintent,1);
            }
        });


    }




    private void Inatializefields() {

        UpdateAcoountsettings = findViewById(R.id.update_settings_button);
        userName = findViewById(R.id.set_user_name);
        userStatues = findViewById(R.id.set_user_statues);
        userProfileimage = findViewById(R.id.profile_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_PICK && resultCode == RESULT_OK && data != null){
            Uri Imageuri = data.getData();

            CropImage.activity(Imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }
      if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);


            if(resultCode == RESULT_OK)
            {



                // this result_Uri contains the crop image
                final Uri resultUri = result.getUri();



                final StorageReference filepath = UserProfileImagesRef.child(currentuserId + ".jpg");

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downloadUrl = uri.toString();
                                Rootref.child("Users").child(currentuserId).child("image").setValue(downloadUrl)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(SettingsActivity.this, "Profile image stored to firebase database successfully.", Toast.LENGTH_SHORT).show();

                                                } else {
                                                    String message = task.getException().getMessage();
                                                    Toast.makeText(SettingsActivity.this, "Error Occurred..." + message, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });


                            }
                        });

                    }
                });/*addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile image uploaded successfully", Toast.LENGTH_SHORT).show();

                            // now we get the image from databaseStorage and hold it in download_url
                            final String download_URL = task.getResult().getStorage().getDownloadUrl().toString();
                            // we put the image in a key which name is image and put the value download_url
                            Rootref.child("Users").child(currentuserId).child("image")
                            .setValue(download_URL)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SettingsActivity.this, "Image save in database successfully", Toast.LENGTH_SHORT).show();
                                        loadingbar.setVisibility(View.INVISIBLE);

                                    }else {
                                        String message = task.getException().toString();
                                        Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                        loadingbar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });


                        } else {
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error :" + message, Toast.LENGTH_SHORT).show();
                            loadingbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });*/
            }

        }

    }

    private void Updatesettings() {

        String setUsername = userName.getText().toString();
        String setUserstatues = userStatues.getText().toString();

        if (TextUtils.isEmpty(setUsername)) {
            Toast.makeText(this, "Please write your name first", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(setUserstatues)) {
            Toast.makeText(this, "Please write your statues", Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> profileMap = new HashMap<>();
            profileMap.put("Uid", currentuserId);
            profileMap.put("name", setUsername);
            profileMap.put("statues", setUserstatues);

            Rootref.child("Users").child(currentuserId).updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        SendusertoMainActivity();
                        Toast.makeText(SettingsActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void RetrieveUserInfo() {

        Rootref.child("Users").child(currentuserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image")))){

                    String retrieveUsername = dataSnapshot.child("name").getValue().toString();
                    String retrievestatues = dataSnapshot.child("statues").getValue().toString();
                    String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();


                    userName.setText(retrieveUsername);
                    userStatues.setText(retrievestatues);
                    Picasso.get().load(retrieveProfileImage).into(userProfileimage);



                } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){



                } else {

                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendusertoMainActivity() {


        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }



}
