package com.example.pavinaveen.map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShadowActivity_1 extends AppCompatActivity {

    FirebaseDatabase mFirebaseInstance;
    DatabaseReference myRef;
    ArrayList places;
    TextView tv;
    Button nxt_btn,sel_btn;
    int p_count, tot_p_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow_1);

        tv = findViewById(R.id.textView5);
        nxt_btn = findViewById(R.id.button);
        sel_btn=findViewById(R.id.button2);

        p_count = tot_p_count = 0;
        places = new ArrayList();

        FirebaseApp.initializeApp(getApplicationContext());
//        FirebaseApp.InitializeApp(Application.Context);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        myRef = mFirebaseInstance.getReference("Cuddalore");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    tot_p_count = (int) dataSnapshot.getChildrenCount();
                    String key = child.getKey();
                    places.add(key);
                }

                if (p_count < tot_p_count) {
                    tv.setText(places.get(p_count).toString());
                    p_count++;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        nxt_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (p_count < tot_p_count) {
                    tv.setText(places.get(p_count).toString());
                    p_count++;
                } else
                    Toast.makeText(ShadowActivity_1.this, "No More Places to Show", Toast.LENGTH_SHORT).show();
            }
        });

        sel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child(tv.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            Toast.makeText(ShadowActivity_1.this,child.getKey()+""+child.getValue().toString(), Toast.LENGTH_SHORT).show();

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
