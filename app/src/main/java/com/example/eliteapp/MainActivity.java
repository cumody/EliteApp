package com.example.eliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Toolbar mtoolbar;
    ViewPager myviewPager;
    TabLayout mytablayout;
    TabAccessorAdapter mytabAccessorAdapter;
    FirebaseUser currentuser;
    FirebaseAuth mAuth;
    private DatabaseReference Rootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser();
        Rootref = FirebaseDatabase.getInstance().getReference();


        mtoolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("EliteApp");


        myviewPager = findViewById(R.id.main_tabs_pager);
        mytabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        myviewPager.setAdapter(mytabAccessorAdapter);

        mytablayout = findViewById(R.id.main_tabs);
        mytablayout.setupWithViewPager(myviewPager);


    }





    @Override
    protected void onStart() {
        super.onStart();
        if (currentuser  == null) {
            SendusertoLoginActivity();
        } else {
            VeriftyUserExistence();
        }
    }

    private void VeriftyUserExistence() {

        String currentUserID = mAuth.getCurrentUser().getUid();

        Rootref.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                } else {
                    SendusertoSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
       getMenuInflater().inflate(R.menu.options_menu,menu);

       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout_options) {
            mAuth.signOut();
            SendusertoLoginActivity();

        }

        if (item.getItemId() == R.id.settings_options) {
            SendusertoSettingsActivity();

        }

        if (item.getItemId() == R.id.find_friends_options) {
            SendusertoFindFriendsActivity();

        }
        if (item.getItemId() == R.id.create_group_options) {
            Requestnewgroup();

        }
        return true;

    }



    private void Requestnewgroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this , R.style.AlertDialog);
        builder.setTitle("Enter Group name : ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("Write your group name");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(MainActivity.this, "Please write group name", Toast.LENGTH_SHORT).show();
                } else {
                    Createnewgroup(groupName);

                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();


            }
        });

        builder.show();


    }

    private void Createnewgroup(final String groupName) {

        Rootref.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Group " + groupName + "Is created successuflly", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }



    private void SendusertoSettingsActivity() {

        Intent settings_intent = new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settings_intent);
    }
    private void SendusertoLoginActivity() {

        Intent login_intent = new Intent(MainActivity.this,LoginActivity.class);
        login_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login_intent);
        finish();
    }
    private void SendusertoFindFriendsActivity() {
        Intent FindAc_intent = new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(FindAc_intent);

    }



}
