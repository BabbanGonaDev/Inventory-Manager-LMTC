package com.bgenterprise.bglmtcinventory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    //This Adapter is used for the multiple tabs in the "Sync/Export" page.

    private int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs){
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                TabFragmentSync tab1 = new TabFragmentSync();
                return tab1;
            case 1:
                TabFragmentExport tab2 = new TabFragmentExport();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount(){
        return mNumOfTabs;
    }
}
