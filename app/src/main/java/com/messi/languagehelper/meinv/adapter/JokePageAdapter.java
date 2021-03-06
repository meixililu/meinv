package com.messi.languagehelper.meinv.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.messi.languagehelper.meinv.MeinvFragment;

import java.util.ArrayList;
import java.util.List;


public class JokePageAdapter extends FragmentPagerAdapter {

    private String category;
    private List<MeinvFragment> fragments;
    private String[] tags = {"推荐","明星","日韩","汽车","美女","可爱"};

    public JokePageAdapter(FragmentManager fm,String[] tags,String category) {
        super(fm);
        this.category = category;
        if(tags != null && tags.length > 0){
            this.tags = tags;
        }
        fragments = new ArrayList<MeinvFragment>();
        for(int i=0;i<tags.length;i++){
            fragments.add(getMeinvFragment(i));
        }
    }

    private MeinvFragment getMeinvFragment(int position){
        return MeinvFragment.newInstance(category,tags[position]);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    public void onTabReselected(int position){
        if(fragments.size() > position && fragments.get(position) != null){
            fragments.get(position).onSwipeRefreshLayoutRefresh();
        }
    }

    @Override
    public int getCount() {
        return tags.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tags[position];
    }
}