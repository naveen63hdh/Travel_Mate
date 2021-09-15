package com.example.pavinaveen.map;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CustomMilestoneDialog extends DialogFragment implements AdapterView.OnItemClickListener {

    private String[] units = { "m", "km"};
    private int dist,totDist;

    Spinner spinner;
    int pos=0;

    EditText distTxt;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_milestone_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Alarm by Milestone");

        builder.setView(view);

        spinner = (Spinner) view.findViewById(R.id.spinner);
        distTxt=view.findViewById(R.id.distTxt);


        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                dist=Integer.parseInt(distTxt.getText().toString());
                if(spinner.getSelectedItem().toString().equals("m"))
                    totDist=dist/1000;
                else
                    totDist=dist;


                Toast.makeText(getActivity(), "We will ring you "+totDist+" km ahead from your Destination", Toast.LENGTH_SHORT).show();

                ((MapsActivity)getActivity()).resultOfMileStone(totDist);

            }
        });


        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,units);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);


        return builder.create();

    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        Toast.makeText(getActivity(), "Dialog Canceld", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        pos=position;

    }
}
