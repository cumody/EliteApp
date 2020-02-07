package com.example.eliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    CircleImageView userprofileImage;
    private TextView userprofilename,userprofilestatues;
    private Button sendmessagerequestbutton , declincerequestbutton;
    private DatabaseReference userref , chatrequestref;
    private String reciver , Currentstat , senderuserId;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();

        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        chatrequestref = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        senderuserId = mAuth.getCurrentUser().getUid();




         reciver = getIntent().getExtras().get("userid").toString();


        userprofileImage = findViewById(R.id.visit_profile_image);
        userprofilename = findViewById(R.id.visit_user_name);
        userprofilestatues = findViewById(R.id.visit_profile_status);
        sendmessagerequestbutton = findViewById(R.id.send_message_request_button);
        declincerequestbutton = findViewById(R.id.decline_message_request_button);
        Currentstat = "new";

        
        Retrieveuserinfo();


    }

    private void Retrieveuserinfo() {

        userref.child(reciver).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){
                    String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatues = dataSnapshot.child("statues").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userprofileImage);
                    userprofilename.setText(userName);
                    userprofilestatues.setText(userStatues);

                    MangeChatRequest();


                } else
                    {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatues = dataSnapshot.child("statues").getValue().toString();

                    userprofilename.setText(userName);
                    userprofilestatues.setText(userStatues);

                    MangeChatRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MangeChatRequest() {

        // this if statement for cancel chat request butotn
        // because if we send message and go back from activity the cancel chat request button text will disappear

        chatrequestref.child(senderuserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(reciver)){
                            String request_type = dataSnapshot.child(reciver).child("requests_type").getValue().toString();
                            if (request_type.equals("sent")){
                                Currentstat = "request_sent";
                                sendmessagerequestbutton.setText("Cancel chat request");
                            } else if (request_type.equals("received")){
                                Currentstat = "request_received";
                                sendmessagerequestbutton.setText("Accept chat request");



                                declincerequestbutton.setVisibility(View.VISIBLE);
                                declincerequestbutton.setEnabled(true);
                                declincerequestbutton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelchatrequest();

                                    }
                                });
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (!senderuserId.equals(reciver)){

            sendmessagerequestbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendmessagerequestbutton.setEnabled(false);

                    if (Currentstat.equals("new")){
                        Sendchatrequest();
                    }

                    if (Currentstat.equals("request_sent")){
                        cancelchatrequest();
                    }


                }
            });


        } else {
            sendmessagerequestbutton.setVisibility(View.INVISIBLE);
        }


    }

    private void cancelchatrequest() {
        chatrequestref.child(senderuserId).child(reciver)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatrequestref.child(reciver).child(senderuserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                sendmessagerequestbutton.setEnabled(true);
                                                Currentstat.equals("new");
                                                sendmessagerequestbutton.setText("Send Message");

                                                declincerequestbutton.setVisibility(View.INVISIBLE);
                                                declincerequestbutton.setEnabled(false);
                                            }

                                        }
                                    });

                        }

                    }
                });

    }

    private void Sendchatrequest() {
        chatrequestref.child(senderuserId).child(reciver)
                .child("requests_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatrequestref.child(reciver).child(senderuserId)
                                    .child("requests_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                sendmessagerequestbutton.setEnabled(true);
                                                Currentstat = "request_sent";
                                                sendmessagerequestbutton.setText("Cancel chat request");
                                            }

                                        }
                                    });

                        }
                    }
                });
    }
}
