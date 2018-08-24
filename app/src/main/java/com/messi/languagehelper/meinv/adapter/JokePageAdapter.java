package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.messi.languagehelper.meinv.JokeFragment;
import com.messi.languagehelper.meinv.R;


public class JokePageAdapter extends FragmentPagerAdapter {

	public static String[] CONTENT;
    private JokeFragment mJokeFragment0;
    private JokeFragment mJokeFragment1;
    private JokeFragment mJokeFragment2;
    private JokeFragment mJokeFragment3;

    public JokePageAdapter(FragmentManager fm, Context mContext) {
        super(fm);
        CONTENT = new String[] { 
        		mContext.getResources().getString(R.string.title_meinv),
        		mContext.getResources().getString(R.string.title_meinv),
        		mContext.getResources().getString(R.string.title_meinv),
        		mContext.getResources().getString(R.string.title_meinv)
        };
    }

//    category 101 搞笑; 102 段子; 103 美女;
    @Override
    public Fragment getItem(int position) {
        if( position == 0 ){
            mJokeFragment0 = JokeFragment.newInstance("");
            return mJokeFragment0;
        }else if( position == 1 ){
            mJokeFragment1 = JokeFragment.newInstance("img");
            return mJokeFragment1;
        }else if( position == 2 ){
            mJokeFragment2 = JokeFragment.newInstance("video");
            return mJokeFragment2;
        }else if( position == 3 ){
            mJokeFragment3 = JokeFragment.newInstance("103");
            return mJokeFragment3;
        }
        return null;
    }

    public void onTabReselected(int index){
        if(index == 0){
            if(mJokeFragment0 != null){
                mJokeFragment0.onTabReselected(index);
            }
        }else if(index == 1){
            if(mJokeFragment1 != null){
                mJokeFragment1.onTabReselected(index);
            }
        }else if(index == 2){
            if(mJokeFragment2 != null){
                mJokeFragment2.onTabReselected(index);
            }
        }else if(index == 3){
            if(mJokeFragment3 != null){
                mJokeFragment3.onTabReselected(index);
            }
        }

    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position].toUpperCase();
    }
}