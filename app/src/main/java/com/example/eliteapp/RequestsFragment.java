package com.example.eliteapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private View fragmentview;
    private RecyclerView myrequestlist;
    private DatabaseReference chatrequestref , usersref , contactsref;
    private FirebaseAuth mAuth;
    private String currentuserid;





    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentview =  inflater.inflate(R.layout.fragment_requests, container, false);

        myrequestlist = fragmentview.findViewById(R.id.chat_requests_list);
        myrequestlist.setLayoutManager(new LinearLayoutManager(getContext()));
        usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        chatrequestref = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsref = FirebaseDatabase.getInstance().getReference().child("Contacts");
        mAuth = FirebaseAuth.getInstance();
        currentuserid = mAuth.getCurrentUser().getUid();



        return fragmentview;


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatrequestref.child(currentuserid),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,RequestViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestViewHolder requestViewHolder, int i, @NonNull Contacts contacts) {
                requestViewHolder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                requestViewHolder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                final String user_id = getRef(i).getKey();
                DatabaseReference gettypeRef = getRef(i).child("requests_type").getRef();

                gettypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String type = dataSnapshot.getValue().toString();
                            if (type.equals("received")){
                                usersref.child(user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")){


                                            final String requestuserimage = dataSnapshot.child("image").toString();

                                            Picasso.get().load(requestuserimage).into(requestViewHolder.profileimage);


                                        }
                                        final String requestusername = dataSnapshot.child("name").getValue().toString();
                                        final String requestuserstatues = dataSnapshot.child("statues").toString();

                                        requestViewHolder.username.setText(requestusername);
                                        requestViewHolder.userstaues.setText("wants to connect with you");




                                        requestViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                          "Accept" ,
                                                          "Cancel"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle( requestusername +"Chat request");

                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == 0){
                                                            contactsref.child(currentuserid).child(user_id).child("Contact")
                                                                    .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        chatrequestref.child(currentuserid).child(user_id)
                                                                                .removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {
                                                                                            chatrequestref.child(user_id).child(currentuserid)
                                                                                                    .removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();

                                                                                                            }

                                                                                                        }
                                                                                                    });

                                                                                        }

                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            });

                                                            }
                                                        if (which == 1){
                                                            chatrequestref.child(currentuserid).child(user_id)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                chatrequestref.child(user_id).child(currentuserid)
                                                                                        .removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    Toast.makeText(getContext(), "Contact deleted", Toast.LENGTH_SHORT).show();

                                                                                                }

                                                                                            }
                                                                                        });

                                                                            }

                                                                        }
                                                                    });



                                                        }

                                                    }
                                                });
                                                builder.show();

                                            }
                                        });

                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @NonNull
            @Override
            public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v  = LayoutInflater.from(getContext()).inflate(R.layout.users_display_layout,parent,false);
                return new RequestViewHolder(v);
            }
        };
        myrequestlist.setAdapter(adapter);
        adapter.startListening();

    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{
        TextView username,userstaues;
        CircleImageView profileimage;
        Button Acceptbutton , cancelbutton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            userstaues = itemView.findViewById(R.id.user_statues);
            profileimage = itemView.findViewById(R.id.users_profile_images);
            Acceptbutton = itemView.findViewById(R.id.request_accept_btn);
            Acceptbutton = itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}
