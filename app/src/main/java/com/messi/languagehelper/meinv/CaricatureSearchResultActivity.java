package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcCaricatureHomeListAdapter;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.messi.languagehelper.meinv.util.XFYSAD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaricatureSearchResultActivity extends BaseActivity{

    private static final int NUMBER_OF_COLUMNS = 2;
    private RecyclerView category_lv;
    private RcCaricatureHomeListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<AVObject> mList;
    private XFYSAD mXFYSAD;
    private int skip = 0;
    private int page_size = 20;
    private boolean loading;
    private boolean hasMore = true;
    private String search_text;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caricature_search_result);
        initViews();
        initSwipeRefresh();
        RequestAsyncTask();
    }

    private void initViews() {
        search_text = getIntent().getStringExtra(KeyUtil.SearchKey);
        mXFYSAD = new XFYSAD(this, ADUtil.SecondaryPage);
        mList = new ArrayList<AVObject>();
        category_lv = (RecyclerView) findViewById(R.id.listview);
        category_lv.setHasFixedSize(true);
        mAdapter = new RcCaricatureHomeListAdapter(mXFYSAD);
        layoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        HeaderSpanSizeLookup headerSpanSizeLookup = new HeaderSpanSizeLookup(mAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(headerSpanSizeLookup);
        category_lv.setLayoutManager(layoutManager);
        if(ADUtil.IsShowAD){
            mAdapter.setHeader(new Object());
        }
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
        skip = 0;
        hasMore = true;
        RequestAsyncTask();
    }

    private void RequestAsyncTask() {
        showProgressbar();
        loading = true;
        AVQuery<AVObject> priorityQuery = new AVQuery<>(AVOUtil.Caricature.Caricature);
        priorityQuery.whereContains(AVOUtil.Caricature.name, search_text);

        AVQuery<AVObject> statusQuery = new AVQuery<>(AVOUtil.Caricature.Caricature);
        statusQuery.whereContains(AVOUtil.Caricature.tag, search_text);

        AVQuery<AVObject> query = AVQuery.or(Arrays.asList(priorityQuery, statusQuery));
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
                    }else {
                        if(skip == 0){
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
                    ToastUtil.diaplayMesShort(CaricatureSearchResultActivity.this, "加载失败，下拉可刷新");
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

}
