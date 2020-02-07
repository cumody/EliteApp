package com.example.eliteapp;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentview;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_groups = new ArrayList<>();
    private DatabaseReference Rootref;



    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentview =  inflater.inflate(R.layout.fragment_groups, container, false);

        Rootref = FirebaseDatabase.getInstance().getReference().child("Groups");

        Initalizefields();
        
        RetrieveandDisplaygroups();

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentgroupName = parent.getItemAtPosition(position).toString();
                Intent groupchatIntent = new Intent(getContext(),GroupChatActivity.class);
                groupchatIntent.putExtra("groupname" , currentgroupName);
                startActivity(groupchatIntent);
            }
        });


        return groupFragmentview;
    }




    private void Initalizefields() {

        list_view = groupFragmentview.findViewById(R.id.list_view);
        arrayAdapter = new ArrayAdapter<String> (getContext(), android.R.layout.simple_list_item_1, list_of_groups);
        list_view.setAdapter(arrayAdapter);

    }
    private void RetrieveandDisplaygroups() {

        Rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                // now we can read every child in groups "groups" database
                Iterator iterator = dataSnapshot.getChildren().iterator();


                while (iterator.hasNext()) {

                    set.add(((DataSnapshot)iterator.next()).getKey());

                }
                list_of_groups.clear();
                list_of_groups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}
