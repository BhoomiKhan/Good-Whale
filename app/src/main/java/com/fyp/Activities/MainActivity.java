package com.fyp.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.fyp.Adapters.TabPageAdapter;
import com.fyp.Utils.FCViewPager;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.fyp.R;

public class MainActivity extends AppCompatActivity {

    private FCViewPager viewPager;
    private BottomNavigationView navigation;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    //it will create the tabs
    private void init() {
        viewPager = (FCViewPager) findViewById(R.id.pager);
        //it will disable the swiping feature that's the reason we created custom view pager
        viewPager.setEnableSwipe(false);
        //setting adapter to ViewPager
        TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager(), 4);
        viewPager.setAdapter(adapter);
        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.fitness:
                        viewPager.setCurrentItem(0);
                        Log.d("tab","1");
                        break;
                    case R.id.confidence:
                        viewPager.setCurrentItem(1);
                        Log.d("tab","2");
                        break;
                    case R.id.social:
                        viewPager.setCurrentItem(2);
                        Log.d("tab","3");
                        break;
                    case R.id.custom_task:
                        viewPager.setCurrentItem(3);
                        Log.d("tab","4");
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (false) {
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}