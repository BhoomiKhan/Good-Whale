package com.fyp.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import com.fyp.BradcastReciver.AlarmReceiver;
import com.fyp.Const.Constants;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities implements Constants {

    //it will get last task number from shared preference and increament by 1
    public static void upgradeTaskCounter(Context mContext, String tabName) {
        int taskNumber = getLastTaskNumber(mContext, tabName);
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences("TaskManagementSys", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt(tabName, ++taskNumber);
        Log.d(tabName, "afzal.. " + taskNumber);
        mEditor.commit();
    }

    //it will count which task in on the top of stack that will show inside the tab
    public static int getLastTaskNumber(Context mContext, String tabName) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences("TaskManagementSys", Context.MODE_PRIVATE);
        return mSharedPreferences.getInt(tabName, 0);
    }

    public static void resetAllKeys(Context mContext){
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences("TaskManagementSys", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt("Fitness", 0);
        mEditor.putInt("Confidence", 0);
        mEditor.putInt("Social", 0);
        mEditor.commit();
    }

    //when this method will call then alarm will set for next 24 hours
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setAlarm(Context mContext, String tabName, String tabTaskCounterName) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(mContext, AlarmReceiver.class);
        notificationIntent.putExtra("tab_name",tabName);
        notificationIntent.putExtra("tab_task_counter_name",tabTaskCounterName);
        PendingIntent broadcast = PendingIntent.getBroadcast(mContext, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 20);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
    }

    //it will update the status, means now tasks have been started
    public static void saveStatus(Context mContext, String separateKeyForActiveButtonInEachTab) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences("TaskManagementSys", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(separateKeyForActiveButtonInEachTab, "yes");
        mEditor.commit();
    }

    //it will check either active button has been pressed or not, means either tasks have been started or not
    public static boolean checkStatus(Context mContext, String separateKeyForActiveButtonInEachTab) {
        SharedPreferences mSharedPreferences = mContext.getSharedPreferences("TaskManagementSys", Context.MODE_PRIVATE);
        String status = mSharedPreferences.getString(separateKeyForActiveButtonInEachTab, "no");
        if (status.equals("yes")) {
            return true;
        } else if (status.equals("no")) {
            return false;
        }
        return true;
    }

    //it will do email validation
    public static boolean isEmailValid(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    //it will check the internet connection
    public static boolean haveNetworkConnection(Context mContext) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

}