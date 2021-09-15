package com.example.pavinaveen.map.receivers;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.pavinaveen.map.ExitActivity;
import com.example.pavinaveen.map.MapsActivity;
import com.example.pavinaveen.map.R;

import static android.content.Context.AUDIO_SERVICE;
import static android.media.AudioManager.STREAM_RING;

public class AlarmReceiver extends BroadcastReceiver {


    NotificationCompat.Builder builder;

    public static AudioManager am;
    NotificationManager manager;
    Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {


        //this will update the UI with message
        String action = intent.getAction();
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (action != null && action.equals("Hello")) {

            if (manager != null) {
                manager.cancelAll();
                System.exit(0);
                Intent exitIntent = new Intent(context, ExitActivity.class);
                exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                context.startActivity(exitIntent);
            }

        } else {
            //this will update the UI with message


            Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show();
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {


                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }


            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(2000);

            showNotification(context);


            AudioManager am = (AudioManager) context.getSystemService(AUDIO_SERVICE);

            int volume_level = am.getStreamMaxVolume(STREAM_RING);

            am.setStreamVolume(
                    STREAM_RING,
                    volume_level,
                    0);
            Toast.makeText(context, "Alarm! " + volume_level, Toast.LENGTH_LONG).show();


            ringtone = RingtoneManager.getRingtone(context, alarmUri);
            ringtone.play();


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // mp.stop();
                    ringtone.stop();
                }
            }, 500 * 60); // runs stop() after 1 minute
        }

    }

    private void showNotification(Context context) {


        Intent new_intent = new Intent(context, AlarmReceiver.class);
        new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new_intent.setAction("Hello");
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, new_intent, PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MapsActivity.class), 0);

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_places)
                .setContentTitle("Reached Destination ")
                .setContentText("Please press cancel to stop the alarm")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pIntent)

                .addAction(R.drawable.cross, "Cancel", pIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        //create channel for android 8 and 9
        createChannel(manager);
        // Add as notification

        manager.notify(123, builder.build());

    }

    public void createChannel(NotificationManager notificationManager){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel channel = new NotificationChannel("channelID","name", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100,200,300,400,500,400,300,200,400});
            assert manager!=null;
            builder.setChannelId("channelID");
//            channel.setDescription("Description");
            manager.createNotificationChannel(channel);
        }

    }

}
