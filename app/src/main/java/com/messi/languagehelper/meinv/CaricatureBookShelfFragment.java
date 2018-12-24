package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.leancloud.AVObject;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcCaricatureBookShelfAdapter;
import com.messi.languagehelper.meinv.db.DataBaseUtil;
import com.messi.languagehelper.meinv.event.CaricatureEventAddBookshelf;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CaricatureBookShelfFragment extends BaseFragment implements View.OnClickListener{

    private static final int NUMBER_OF_COLUMNS = 3;
    private RecyclerView category_lv;
    private CardView action_delete_all;
    private CardView action_delete_btn;
    private TextView empty_tv;
    private RcCaricatureBookShelfAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<AVObject> mList;
    private int skip = 0;
    private boolean loading;
    private boolean hasMore = true;
    private boolean isDeleteModel;

    public static CaricatureBookShelfFragment newInstance(){
        CaricatureBookShelfFragment fragment = new CaricatureBookShelfFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.caricature_bookshelf_fragment, null);
        initViews(view);
        initSwipeRefresh(view);
        RequestAsyncTask();
        return view;
    }

    private void initViews(View view) {
        isRegisterBus = true;
        mList = new ArrayList<AVObject>();
        category_lv = (RecyclerView) view.findViewById(R.id.listview);
        action_delete_btn = (CardView) view.findViewById(R.id.action_delete_btn);
        action_delete_all = (CardView) view.findViewById(R.id.action_delete_all);
        empty_tv = (TextView) view.findViewById(R.id.empty_tv);
        action_delete_btn.setOnClickListener(this);
        action_delete_all.setOnClickListener(this);
        category_lv.setHasFixedSize(true);
        mAdapter = new RcCaricatureBookShelfAdapter();
        layoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
        HeaderSpanSizeLookup headerSpanSizeLookup = new HeaderSpanSizeLookup(mAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(headerSpanSizeLookup);
        category_lv.setLayoutManager(layoutManager);
        mAdapter.setFooter(new Object());
        mAdapter.setItems(mList);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CaricatureEventAddBookshelf scode){
        LogUtil.DefalutLog("onEvent--CaricatureEventAddBookshelf");
        onSwipeRefreshLayoutRefresh();
    }

    @Override
    public void onSwipeRefreshLayoutRefresh() {
        skip = 0;
        hasMore = true;
        RequestAsyncTask();
    }

    private void RequestAsyncTask(){
        showProgressbar();
        loading = true;
        Observable.create(new ObservableOnSubscribe<List<AVObject>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AVObject>> e) throws Exception {
                e.onNext(getData());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AVObject>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<AVObject> s) {
                        onFinishLoadData(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private List<AVObject> getData(){
        List<AVObject> list = DataBaseUtil.getInstance().getCaricaturesList(AVOUtil.Caricature.Caricature,
                skip, Setings.ca_psize,false,true);
        return list;
    }

    private void onFinishLoadData(List<AVObject> list) {
        onSwipeRefreshLayoutFinish();
        hideProgressbar();
        loading = false;
        if(list != null){
            if(list.size() == 0){
                hasMore = false;
                hideFooterview();
            }else {
                if(skip == 0){
                    mList.clear();
                }
                initDeleteModel(list);
                mList.addAll(list);
                mAdapter.notifyDataSetChanged();
                if(list.size() < Setings.ca_psize){
                    hasMore = false;
                    hideFooterview();
                }else {
                    skip += Setings.ca_psize;
                    hasMore = true;
                    showFooterview();
                }
            }
        }else{
            ToastUtil.diaplayMesShort(getContext(), "加载失败，下拉可刷新");
        }
        if(mList.size() > 0){
            empty_tv.setVisibility(View.GONE);
            action_delete_btn.setVisibility(View.VISIBLE);
        }else {
            action_delete_btn.setVisibility(View.GONE);
            empty_tv.setVisibility(View.VISIBLE);
        }

    }

    private void initDeleteModel(List<AVObject> list){
        if(isDeleteModel){
            setDeleteModel(list,"1");
        }else {
            setDeleteModel(list,"0");
        }
    }

    private void hideFooterview(){
        mAdapter.hideFooter();
    }

    private void showFooterview(){
        mAdapter.showFooter();
    }

    private void setDeleteModel(List<AVObject> list,String status){
        for(AVObject mAVObject : list){
            mAVObject.put(KeyUtil.DeleteModel,status);
            mAVObject.put(KeyUtil.isNeedDelete,"0");
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.action_delete_btn) {
            if(!isDeleteModel){
                isDeleteModel = true;
                action_delete_all.setVisibility(View.VISIBLE);
                setDeleteModel(mList,"1");
            }else {
                deleteBooks();
                isDeleteModel = false;
                setDeleteModel(mList,"0");
            }
        } else if(view.getId() == R.id.action_delete_all) {
            deleteAll();
        }
    }

    private void deleteAll(){
        DataBaseUtil.getInstance().clearAvobjectCollected(AVOUtil.Caricature.Caricature);
        mList.clear();
        mAdapter.notifyDataSetChanged();
        isDeleteModel = false;
        action_delete_all.setVisibility(View.GONE);
    }

    private void deleteBooks(){
        List<AVObject> newList = new ArrayList<AVObject>();
        for(AVObject mAVObject : mList){
            if(!TextUtils.isEmpty(mAVObject.getString(KeyUtil.isNeedDelete))){
                if("1".equals(mAVObject.getString(KeyUtil.isNeedDelete))){
                    DataBaseUtil.getInstance().updateAVObjectCollected(AVOUtil.Caricature.Caricature,
                            mAVObject,0);
                }else {
                    newList.add(mAVObject);
                }
            }
        }
        mList.clear();
        mList.addAll(newList);
        mAdapter.notifyDataSetChanged();
    }
}
