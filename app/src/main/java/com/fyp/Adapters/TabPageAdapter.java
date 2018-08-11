package com.fyp.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.fyp.Fragments.Confidence;
import com.fyp.Fragments.Custom;
import com.fyp.Fragments.Fitness;
import com.fyp.Fragments.Social;

public class TabPageAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabPageAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Fitness tab1 = new Fitness();
                return tab1;
            case 1:
                Confidence tab2 = new Confidence();
                return tab2;
            case 2:
                Social tab3 = new Social();
                return tab3;
            case 3:
                Custom tab4 = new Custom();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}