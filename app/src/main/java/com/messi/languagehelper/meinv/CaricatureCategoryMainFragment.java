package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.messi.languagehelper.meinv.adapter.CaricatureCategoryMainAdapter;

public class CaricatureCategoryMainFragment extends BaseFragment implements View.OnClickListener{

    private TabLayout tablayout;
    private ViewPager viewpager;
    private FrameLayout search_btn;
    private CaricatureCategoryMainAdapter pageAdapter;

    public static CaricatureCategoryMainFragment getInstance() {
        return new CaricatureCategoryMainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.tablayout_with_search_fragment, null);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        tablayout = (TabLayout) view.findViewById(R.id.tablayout);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
        search_btn = (FrameLayout) view.findViewById(R.id.search_btn);
        search_btn.setOnClickListener(this);

        pageAdapter = new CaricatureCategoryMainAdapter(getChildFragmentManager(),getContext());
        viewpager.setAdapter(pageAdapter);
        viewpager.setOffscreenPageLimit(2);
        tablayout.setupWithViewPager(viewpager);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.search_btn){
            toActivity(CaricatureSearchActivity.class,null);
        }
    }
}
