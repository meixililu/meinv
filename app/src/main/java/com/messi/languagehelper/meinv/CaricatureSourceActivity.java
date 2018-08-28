package com.messi.languagehelper.meinv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcCaricatureHomeListAdapter;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.NumberUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.messi.languagehelper.meinv.util.XFYSAD;

import java.util.ArrayList;
import java.util.List;

public class CaricatureSourceActivity extends BaseActivity implements View.OnClickListener{

    private static final int NUMBER_OF_COLUMNS = 3;
    private RecyclerView category_lv;
    private FrameLayout source_web_btn;
    private RcCaricatureHomeListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<AVObject> mList;
    private XFYSAD mXFYSAD;
    private int skip = 0;
    private int page_size = 21;
    private int max_count = 2000;
    private boolean loading;
    private boolean hasMore = true;
    private boolean isNeedClear = true;
    private String search_text;
    private String source_url;
    private String ad_filte;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caricature_source);
        initViews();
        initSwipeRefresh();
        RequestAsyncTask();
    }

    private void initViews() {
        search_text = getIntent().getStringExtra(KeyUtil.SearchKey);
        source_url = getIntent().getStringExtra(KeyUtil.URL);
        ad_filte = getIntent().getStringExtra(KeyUtil.AdFilter);
        mXFYSAD = new XFYSAD(this, ADUtil.SecondaryPage);
        mList = new ArrayList<AVObject>();
        category_lv = (RecyclerView) findViewById(R.id.listview);
        source_web_btn = (FrameLayout) findViewById(R.id.source_web_btn);
        source_web_btn.setOnClickListener(this);
        category_lv.setHasFixedSize(true);
        mAdapter = new RcCaricatureHomeListAdapter(mXFYSAD);
        layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        HeaderSpanSizeLookup headerSpanSizeLookup = new HeaderSpanSizeLookup(mAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(headerSpanSizeLookup);
        category_lv.setLayoutManager(layoutManager);
        mAdapter.setHeader(new Object());
        mAdapter.setItems(mList);
        mXFYSAD.setAdapter(mAdapter);
        category_lv.setAdapter(mAdapter);
        setListOnScrollListener();
    }

    public void setListOnScrollListener() {
        category_lv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible = layoutManager.getChildCount();
                int total = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (!loading && hasMore) {
                    if ((visible + firstVisibleItem) >= total) {
                        RequestAsyncTask();
                    }
                }
            }
        });
    }

    @Override
    public void onSwipeRefreshLayoutRefresh() {
        randomPage();
        hasMore = true;
        RequestAsyncTask();
    }

    private void randomPage(){
        isNeedClear = true;
        if(max_count > 0){
            skip = NumberUtil.getRandomNumber(max_count);
            LogUtil.DefalutLog("spip:"+skip);
        }else {
            skip = 0;
        }
    }

    private void RequestAsyncTask() {
        showProgressbar();
        loading = true;
        AVQuery<AVObject> query = new AVQuery<>(AVOUtil.Caricature.Caricature);
        query.whereContains(AVOUtil.Caricature.source_name, search_text);
        query.orderByDescending(AVOUtil.Caricature.views);
        query.skip(skip);
        query.limit(page_size);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                hideProgressbar();
                loading = false;
                onSwipeRefreshLayoutFinish();
                if(list != null){
                    if(list.size() == 0){
                        hasMore = false;
                        hideFooterview();
                        if(skip == 0){
                            toSourceWebsite();
                        }
                    }else {
                        if(isNeedClear){
                            isNeedClear = false;
                            mList.clear();
                        }
                        mList.addAll(list);
                        mAdapter.notifyDataSetChanged();
                        if(list.size() < page_size){
                            hasMore = false;
                            hideFooterview();
                        }else {
                            skip += page_size;
                            hasMore = true;
                            showFooterview();
                        }
                    }
                }else{
                    ToastUtil.diaplayMesShort(CaricatureSourceActivity.this, "加载失败，下拉可刷新");
                }
            }
        });
    }

    private void hideFooterview(){
        mAdapter.hideFooter();
    }

    private void showFooterview(){
        mAdapter.showFooter();
    }

    private void toSourceWebsite(){
        Intent intent = new Intent(this, WebViewNoAdActivity.class);
        intent.putExtra(KeyUtil.URL, source_url);
        intent.putExtra(KeyUtil.AdFilter, ad_filte);
        intent.putExtra(KeyUtil.IsHideToolbar, true);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.source_web_btn){
            toSourceWebsite();
        }
    }
}
