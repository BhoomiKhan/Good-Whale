package com.fyp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fyp.R;

public class Splash extends AppCompatActivity {

    private final static int SPLASH_TIME_OUT=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sPref=getSharedPreferences("Registration",MODE_PRIVATE);
                int registrationCode=sPref.getInt("isRegister",0);
                if(registrationCode==0){
                    Intent i=new Intent(Splash.this,SingUp.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.signin_incoming_screen_right_to_mean_position,R.anim.signin_current_screen_move_mean_to_left);
                    finish();
                }
                else if(registrationCode==1){
                    Intent i=new Intent(Splash.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }
}
