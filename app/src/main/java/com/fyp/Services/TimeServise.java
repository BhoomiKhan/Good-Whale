package com.fyp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class TimeServise extends Service {


    private int timecount = 86400;
    private int postcounter = 0;
    private Timer timer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    void startTimer()
    {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run()
            {
                timecount = timecount - 1;
                Intent intent = new Intent("timer");
                intent.putExtra("time",timecount);
                if(timecount == 0)
                {
                    postcounter = postcounter + 1;
                    timecount = 86400;
                }
                intent.putExtra("post",postcounter);
                sendBroadcast(intent);
            }
        },0,1000);
    }
    void stopTimer()
    {
        timer.cancel();
    }
}
