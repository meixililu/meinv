package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.messi.languagehelper.meinv.CaricatureCategoryFragment;
import com.messi.languagehelper.meinv.CaricatureWebListFragment;
import com.messi.languagehelper.meinv.R;


public class CaricatureCategoryMainAdapter extends FragmentPagerAdapter {

    public static String[] CONTENT;
    private Context mContext;

    public CaricatureCategoryMainAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
        CONTENT = new String[] {
                mContext.getResources().getString(R.string.title_category),
                mContext.getResources().getString(R.string.title_source)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if( position == 0 ){
            return CaricatureCategoryFragment.newInstance();
        }else if( position == 1 ){
            return CaricatureWebListFragment.newInstance();
        }
        return null;
    }

    public int getCount() {
        return CONTENT.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position].toUpperCase();
    }

}