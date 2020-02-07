package com.example.eliteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.icu.text.Edits;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton Sendmessagebutton;
    private EditText usermessageInput;
    private ScrollView mscrollview;
    private TextView displayTextmessage;
    private DatabaseReference userref  , groupnameref , groupmessagekeyref;
    private String currentgroupname , currentUserId , currentusername , currentdata , currenttime ;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentgroupname = getIntent().getExtras().getString("groupname").toString();
        Toast.makeText(this, currentgroupname   , Toast.LENGTH_SHORT).show();


        mAuth = FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        groupnameref = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentgroupname);


        Inializefields();
        GetUserInfo();

        Sendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SavemessageIntodatabase();

                usermessageInput.setText("");
                mscrollview.fullScroll(ScrollView.FOCUS_DOWN);


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // now every time the user open any group chat we will show him the previous messages

        groupnameref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {
                    Displaymessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()) {
                    Displaymessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    private void Inializefields() {

        mToolbar = findViewById(R.id.group_chat_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentgroupname);

        Sendmessagebutton = findViewById(R.id.send_message_button);
        usermessageInput = findViewById(R.id.input_group_message);
        displayTextmessage = findViewById(R.id.group_chat_text);
        mscrollview = findViewById(R.id.my_scroll_view);
    }

    private void GetUserInfo() {

        userref.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentusername = dataSnapshot.child("name").getValue().toString();


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    private void SavemessageIntodatabase(){

        String message = usermessageInput.getText().toString();
        String messageKey = groupnameref.push().getKey();




        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please write message ", Toast.LENGTH_SHORT).show();
        } else {
            Calendar ccalfordata = Calendar.getInstance();
            SimpleDateFormat currentdataformat = new SimpleDateFormat("MMM dd,yyyy");
            currentdata = currentdataformat.format(ccalfordata.getTime());

            Calendar ccalfortime = Calendar.getInstance();
            SimpleDateFormat currenttimeformat = new SimpleDateFormat("hh:mm a");
            currenttime = currenttimeformat.format(ccalfortime.getTime());

            // 126 , 127 mlhomsh lazma zy mtwk3t
            Map<String, Object> groupmessagekey = new HashMap<>();
            groupnameref.updateChildren(groupmessagekey);


            groupmessagekeyref  =  groupnameref.child(messageKey);

            HashMap<String , Object> messageInfomap = new HashMap<>();
            messageInfomap.put("name",currentusername);
            messageInfomap.put("message",message);
            messageInfomap.put("date",currentdata);
            messageInfomap.put("time",currenttime);

            groupmessagekeyref.updateChildren(messageInfomap);



        }
    }
    private void Displaymessages(DataSnapshot dataSnapshot) {

        // now we get every single message line by line by this method
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()) {
            // we get the date
            String chatdate = (String) ((DataSnapshot)iterator.next()).getValue();
            // we get the message
            String chatMessage= (String) ((DataSnapshot)iterator.next()).getValue();
            // we get name
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            // we get time
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();

            displayTextmessage.append(chatName + " :\n" + chatMessage + " :\n" + chatTime + "       " + chatdate + "\n\n\n");

            // this for auto scroll down
            mscrollview.fullScroll(ScrollView.FOCUS_DOWN);

        }
    }
}
