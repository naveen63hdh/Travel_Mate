package com.example.pavinaveen.map;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pavinaveen.map.receivers.AlarmReceiver;

import java.util.Calendar;

import static android.app.AlarmManager.RTC_WAKEUP;

public class RegisterPage extends AppCompatActivity {

    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    public void register(View view) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(cal.getTime());
        cal.add(Calendar.SECOND, 5);
        Toast.makeText(RegisterPage.this, "Alarm will ring at"+cal.getTime(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(RTC_WAKEUP,System.currentTimeMillis(), 60000, pendingIntent);
    }
}
