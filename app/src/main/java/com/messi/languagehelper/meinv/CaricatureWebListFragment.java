package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcCaricatureSourceListAdapter;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.messi.languagehelper.meinv.util.XFYSAD;
import com.messi.languagehelper.meinv.view.DividerGridItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class CaricatureWebListFragment extends BaseFragment{

    private static final int NUMBER_OF_COLUMNS = 3;
    private RecyclerView category_lv;
    private RcCaricatureSourceListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<AVObject> mList;
    private XFYSAD mXFYSAD;
    private int skip = 0;
    private boolean loading;
    private boolean hasMore = true;

    public static CaricatureWebListFragment newInstance(){
        CaricatureWebListFragment fragment = new CaricatureWebListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.caricature_web_list_fragment, null);
        initViews(view);
        initSwipeRefresh(view);
        RequestAsyncTask();
        return view;
    }

    private void initViews(View view) {
        mXFYSAD = new XFYSAD(getActivity(), ADUtil.SecondaryPage);
        mList = new ArrayList<AVObject>();
        category_lv = (RecyclerView) view.findViewById(R.id.listview);
        category_lv.setHasFixedSize(true);
        mAdapter = new RcCaricatureSourceListAdapter(mXFYSAD);
        layoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
        HeaderSpanSizeLookup headerSpanSizeLookup = new HeaderSpanSizeLookup(mAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(headerSpanSizeLookup);
        category_lv.setLayoutManager(layoutManager);
        category_lv.addItemDecoration(new DividerGridItemDecoration(1));
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isHasLoadData && mXFYSAD != null){
            if(mXFYSAD.lastLoadAdTime > 0){
                mXFYSAD.showAd();
            }
        }
    }

    @Override
    public void onSwipeRefreshLayoutRefresh() {
        skip = 0;
        hasMore = true;
        RequestAsyncTask();
        mXFYSAD.showAd();
    }


    private void RequestAsyncTask() {
        showProgressbar();
        loading = true;
        AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EnglishWebsite.EnglishWebsite);
        query.whereEqualTo(AVOUtil.EnglishWebsite.category, "caricature");
        query.orderByDescending(AVOUtil.EnglishWebsite.Order);
        query.skip(skip);
        query.limit(50);
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
                        if(list.size() < 30){
                            hasMore = false;
                            hideFooterview();
                        }else {
                            skip += 30;
                            hasMore = true;
                            showFooterview();
                        }
                    }
                }else{
                    ToastUtil.diaplayMesShort(getContext(), "加载失败，下拉可刷新");
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
