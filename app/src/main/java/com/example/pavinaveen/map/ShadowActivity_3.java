package com.example.pavinaveen.map;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class ShadowActivity_3 extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shadow_3);

    }

    public void openMile(View view) {

        CustomMilestoneDialog customLoginDialog = new CustomMilestoneDialog();
        customLoginDialog.show(getSupportFragmentManager(), "milestone_dialog");


    }

    public void openStop(View view) {

        CustomStopDialog customStopDialog=new CustomStopDialog();
        customStopDialog.show(getSupportFragmentManager(),"stop_dialog");

    }
}
