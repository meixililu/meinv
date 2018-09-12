package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcCaricatureHomeListAdapter;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.NumberUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.messi.languagehelper.meinv.util.XFYSAD;

import java.util.ArrayList;
import java.util.List;

public class CaricatureHomeFragment extends BaseFragment implements View.OnClickListener{

    private static final int NUMBER_OF_COLUMNS = 3;
    private RecyclerView category_lv;
    private FrameLayout search_btn;
    private Toolbar my_awesome_toolbar;
    private RcCaricatureHomeListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<AVObject> mList;
    private XFYSAD mXFYSAD;
    private int skip = 0;
    private int max_count = 5000;
    private int page_size = 21;
    private boolean loading;
    private boolean hasMore = true;
    private boolean isNeedClear = true;

    public static CaricatureHomeFragment newInstance(){
        CaricatureHomeFragment fragment = new CaricatureHomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.caricature_home_fragment, null);
        initViews(view);
        initSwipeRefresh(view);
        randomPage();
        RequestAsyncTask();
        return view;
    }

    private void initViews(View view) {
        mXFYSAD = new XFYSAD(getActivity(), ADUtil.SecondaryPage);
        mList = new ArrayList<AVObject>();
        my_awesome_toolbar = (Toolbar) view.findViewById(R.id.my_awesome_toolbar);
        search_btn = (FrameLayout) view.findViewById(R.id.search_btn);
        category_lv = (RecyclerView) view.findViewById(R.id.listview);
        my_awesome_toolbar.setTitle(R.string.recommend);
        search_btn.setOnClickListener(this);
        category_lv.setHasFixedSize(true);
        mAdapter = new RcCaricatureHomeListAdapter(mXFYSAD);
        layoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && isHasLoadData && mXFYSAD != null){
            if(mXFYSAD.lastLoadAdTime > 0){
                mXFYSAD.showAD();
            }
        }
    }

    @Override
    public void onSwipeRefreshLayoutRefresh() {
        randomPage();
        hasMore = true;
        RequestAsyncTask();
        mXFYSAD.showAD();
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
        AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.Caricature.Caricature);
//        query.whereEqualTo(AVOUtil.Caricature., category);
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.search_btn){
            toSearchActivity();
        }
    }

    private void toSearchActivity(){
        toActivity(CaricatureSearchActivity.class,null);
    }
}
