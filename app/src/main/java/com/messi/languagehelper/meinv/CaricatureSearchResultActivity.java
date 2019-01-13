package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.messi.languagehelper.meinv.adapter.CaricatureSearchResultAdapter;
import com.messi.languagehelper.meinv.util.KeyUtil;

public class CaricatureSearchResultActivity extends BaseActivity {

    private TabLayout tablayout;
    private ViewPager viewpager;
    private String search_text;
    private String url;
    private CaricatureSearchResultAdapter pageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablayout_search_result_activity);
        initViews();
    }

    private void initViews(){
        search_text = getIntent().getStringExtra(KeyUtil.SearchKey);
        url = getIntent().getStringExtra(KeyUtil.SearchUrl);
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        pageAdapter = new CaricatureSearchResultAdapter(getSupportFragmentManager(),this,search_text,url);
        viewpager.setAdapter(pageAdapter);
        viewpager.setOffscreenPageLimit(2);
        tablayout.setupWithViewPager(viewpager);
    }

}
