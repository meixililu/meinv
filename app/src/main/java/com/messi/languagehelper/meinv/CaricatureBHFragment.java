package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.messi.languagehelper.meinv.adapter.CaricatureBHAdapter;


public class CaricatureBHFragment extends BaseFragment{

    private TabLayout tablayout;
    private ViewPager viewpager;
    private FrameLayout more_btn;
    private CaricatureBHAdapter pageAdapter;

    public static CaricatureBHFragment getInstance() {
        return new CaricatureBHFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.tablayout_with_more_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        tablayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        more_btn = (FrameLayout) view.findViewById(R.id.more_btn);

        pageAdapter = new CaricatureBHAdapter(getChildFragmentManager(),getContext());
        viewpager.setAdapter(pageAdapter);
        viewpager.setOffscreenPageLimit(2);
        tablayout.setupWithViewPager(viewpager);
        more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toActivity(MoreActivity.class,null);
            }
        });
    }

}
