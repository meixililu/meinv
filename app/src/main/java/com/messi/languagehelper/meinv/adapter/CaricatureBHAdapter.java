package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.messi.languagehelper.meinv.CaricatureBookShelfFragment;
import com.messi.languagehelper.meinv.CaricatureHistoryFragment;
import com.messi.languagehelper.meinv.R;


public class CaricatureBHAdapter extends FragmentPagerAdapter {

    public static String[] CONTENT;
    private Context mContext;

    public CaricatureBHAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        this.mContext = mContext;
        CONTENT = new String[] {
                mContext.getResources().getString(R.string.title_bookshelf),
                mContext.getResources().getString(R.string.title_history)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if( position == 0 ){
            return CaricatureBookShelfFragment.newInstance();
        }else if( position == 1 ){
            return CaricatureHistoryFragment.newInstance();
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