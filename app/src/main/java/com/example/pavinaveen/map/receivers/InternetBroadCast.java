package com.example.pavinaveen.map.receivers;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import android.view.animation.CycleInterpolator;
import android.widget.Button;
import android.widget.Toast;


import com.example.pavinaveen.map.R;


public class InternetBroadCast extends BroadcastReceiver {

    String LOG_TAG = "NetworkChangeReceiver";
    public boolean isConnected = false;
    private Boolean status;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.v(LOG_TAG, "Receieved notification about network status");


        status = isNetworkAvailable(context);

        if (!status) {

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.network_dialog);
            dialog.setTitle("No Internet Connection...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);

            Button dialogButton = (Button) dialog.findViewById(R.id.ok_button);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        if(!status)
                        {
                            dialog
                                    .getWindow()
                                    .getDecorView()
                                    .animate()
                                    .translationX(16f)
                                    .setInterpolator(new CycleInterpolator(7f));
                            Toast.makeText(context, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                        else
                            dialog.dismiss();
                    } catch (Exception e) {
                        Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
            try {

                dialog.show();

            }
            catch (WindowManager.BadTokenException e)
            {
                e.printStackTrace();
            }

        }
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {

            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if(!isConnected){
                            Log.v(LOG_TAG, "Now you are connected to Internet!");

                            isConnected = true;
                        }
                        return true;
                    }
                }
            }
        }
        Log.v(LOG_TAG, "You are not connected to Internet!");

        isConnected = false;
        return false;
    }

}
