package com.example.pavinaveen.map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {


    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        imageView=findViewById(R.id.splashImage);

        Animation anim = new ScaleAnimation(
                0.95f, 1f, // Start and end values for the X axis scaling
                0.95f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(1000);
        anim.setRepeatMode(Animation.INFINITE);
        anim.setRepeatCount(Animation.INFINITE);
        imageView.startAnimation(anim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent home_intent=new Intent(SplashActivity.this,MapsActivity.class);
                startActivity(home_intent);
                finish();
            }
        }, 2000);
    }
}
