package com.example.pavinaveen.map;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShadowActivity_2 extends AppCompatActivity {

    FirebaseDatabase mFirebaseInstance;
    DatabaseReference myRef;


    AutoCompleteTextView autoCompleteTextView;
    CustomAdapter adapter = null;
    ArrayList<Places> places;

    String[] ar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow_2);


        places = new ArrayList<>();
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        myRef = mFirebaseInstance.getReference("Cuddalore");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;
                ar = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    ar[i] = child.getKey();

                    //Updates to Customer on every single changes
                    places.add(new Places(ar[i]));
                    adapter = new CustomAdapter(ShadowActivity_2.this, places);
                    autoCompleteTextView.setAdapter(adapter);
                    i++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myRef.child(autoCompleteTextView.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            Toast.makeText(ShadowActivity_2.this, child.getKey() + "" + child.getValue().toString(), Toast.LENGTH_SHORT).show();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

}




