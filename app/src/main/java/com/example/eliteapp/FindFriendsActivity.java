package com.example.eliteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private RecyclerView mrecyclerview;
    private DatabaseReference userref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


        mtoolbar = findViewById(R.id.toolbar_findFriends);
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        setSupportActionBar(mtoolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mrecyclerview = findViewById(R.id.find_friends_list);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(userref,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ViewHolderContacts> contactsViewHolderContactsFirebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Contacts, ViewHolderContacts>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolderContacts holder, final int position, @NonNull Contacts model) {
                holder.username.setText(model.name);
                holder.userstatues.setText(model.statues);
                Picasso.get().load(model.image).placeholder(R.drawable.profile_image).into(holder.profileimage);


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String user_id = getRef(position).getKey();

                        Intent profileActivity = new Intent(FindFriendsActivity.this,ProfileActivity.class);
                        profileActivity.putExtra("userid",user_id);
                        startActivity(profileActivity);
                    }
                });

            }

            @NonNull
            @Override
            public ViewHolderContacts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent,false);
                return new ViewHolderContacts(v);
            }
        };

        mrecyclerview.setAdapter(contactsViewHolderContactsFirebaseRecyclerAdapter);
        contactsViewHolderContactsFirebaseRecyclerAdapter.startListening();

    }

    public static class ViewHolderContacts extends RecyclerView.ViewHolder{

        public TextView username , userstatues;
        public CircleImageView profileimage;

        public ViewHolderContacts(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.user_profile_name);
            userstatues = itemView.findViewById(R.id.user_statues);
            profileimage =  itemView.findViewById(R.id.users_profile_images);
        }
    }
}
