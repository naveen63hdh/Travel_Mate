package com.example.pavinaveen.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheet extends BottomSheetDialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.activity_bottom_sheet,container,false);
//        View v = inflater.inflate(R.layout.activity_bottom_sheet, container, false);


        Button alarmBtn = v.findViewById(R.id.alarmBtn);
        TextView destLoc=v.findViewById(R.id.destText);

        RadioButton miles=v.findViewById(R.id.mileRadioBtn);
        RadioButton stops=v.findViewById(R.id.stopRadioBtn);


        Bundle bundle=getArguments();
        String dest=bundle.getString("destLoc");

        destLoc.setText(dest);


        miles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomMilestoneDialog customLoginDialog = new CustomMilestoneDialog();
                customLoginDialog.show(getActivity().getSupportFragmentManager(), "milestone_dialog");
            }
        });

        stops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomStopDialog customStopDialog=new CustomStopDialog();
                customStopDialog.show(getFragmentManager(),"stop_dialog");
            }
        });
        //calls the respective java classes
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ((MapsActivity)getActivity()).buttonClicked(true);
                dismiss();
            }
        });

        return v;

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
