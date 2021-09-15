package com.example.pavinaveen.map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomStopDialog extends DialogFragment {

    private View view;

    private DatabaseReference myRef;
    private AutoCompleteTextView autoCompleteTextView;
    private CustomAdapter adapter = null;
    private ArrayList<Places> places;
    private String[] ar;

    Bundle returnBundle;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        view = getActivity().getLayoutInflater().inflate(R.layout.custom_stop_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Alarm by Stoppings");
        builder.setView(view);


        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        myRef = mFirebaseInstance.getReference("Places");

        places = new ArrayList<>();
        autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int i = 0;
                ar = new String[(int) dataSnapshot.getChildrenCount()];
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    ar[i] = child.getKey();

                    //Updates to Customer on every single changes
                    places.add(new Places(ar[i]));
                    adapter = new CustomAdapter(view.getContext(), places);
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
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                myRef.child(autoCompleteTextView.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int i=0;
                        double lattitude=0,longitude=0;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            if(i==0)
                                lattitude= (double) child.getValue();
                            else
                                longitude= (double) child.getValue();
                            i++;

                        }

                        ((MapsActivity)getActivity()).resultOfStopAC(autoCompleteTextView.getText().toString(),lattitude,longitude);
//                        Toast.makeText(view.getContext(),autoCompleteTextView.getText()+"Lat :"+lattitude+"Long"+longitude,Toast.LENGTH_SHORT ).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });



        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });


        return builder.create();

    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
