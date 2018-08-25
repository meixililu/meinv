package com.messi.languagehelper.meinv.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.meinv.JokeFragment;
import com.messi.languagehelper.meinv.util.AVOUtil;

import java.util.List;


public class JokePageAdapter extends FragmentPagerAdapter {

    private List<AVObject> list;

    public JokePageAdapter(FragmentManager fm, Context mContext,List<AVObject> list) {
        super(fm);
        this.list = list;
    }

    @Override
    public Fragment getItem(int position) {
        return JokeFragment.newInstance(list.get(position).getString(AVOUtil.Meinv.tag));
    }

    public void onTabReselected(int index){

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).getString(AVOUtil.Meinv.tag);
    }
}