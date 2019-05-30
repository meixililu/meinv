package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcCaricatureCategoryAdapter;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.impl.AdapterStringListener;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.DataUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.NumberUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CaricatureCategoryFragment extends BaseFragment implements AdapterStringListener {

    private static final int NUMBER_OF_COLUMNS = 3;
    private RecyclerView category_lv;
    private RcCaricatureCategoryAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<CNWBean> mList;
    private List<AVObject> calist;
    private int skip = 0;
    private int max_count = 300;
    private boolean loading;
    private boolean hasMore = true;
    private boolean isNeedClear = true;
    private String search_text = "全部";
    private IFLYNativeAd nativeAd;
    private CNWBean mADObject;
    private List<NativeExpressADView> mTXADList;

    public static CaricatureCategoryFragment newInstance(){
        CaricatureCategoryFragment fragment = new CaricatureCategoryFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.caricature_category_fragment, null);
        initViews(view);
        initSwipeRefresh(view);
        randomPage();
        Request();
        return view;
    }

    private void Request(){
        RequestCategoryTask();
        loadAD();
        RequestAsyncTask();
    }

    private void initViews(View view) {
        mList = new ArrayList<CNWBean>();
        calist = new ArrayList<AVObject>();
        mTXADList = new ArrayList<NativeExpressADView>();
        category_lv = (RecyclerView) view.findViewById(R.id.listview);
        category_lv.setHasFixedSize(true);
        mAdapter = new RcCaricatureCategoryAdapter(calist,this);
        layoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
        HeaderSpanSizeLookup headerSpanSizeLookup = new HeaderSpanSizeLookup(mAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(headerSpanSizeLookup);
        category_lv.setLayoutManager(layoutManager);
        mAdapter.setFooter(new Object());
        mAdapter.setHeader(new Object());
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
                isADInList(recyclerView, firstVisibleItem, visible);
                if (!loading && hasMore) {
                    if ((visible + firstVisibleItem) >= total) {
                        loadAD();
                        RequestAsyncTask();
                    }
                }
            }
        });
    }

    private void isADInList(RecyclerView view,int first, int vCount){
        if(mList.size() > 3){
            for(int i=first;i< (first+vCount);i++){
                if(i < mList.size() && i > 0){
                    CNWBean mAVObject = mList.get(i);
                    if(mAVObject != null && mAVObject.getmNativeADDataRef() != null){
                        if(!mAVObject.isAdShow()){
                            NativeADDataRef mNativeADDataRef = mAVObject.getmNativeADDataRef();
                            boolean isExposure = mNativeADDataRef.onExposured(view.getChildAt(i%vCount));
                            mAVObject.setAdShow(isExposure);
                            LogUtil.DefalutLog("isExposure:"+isExposure);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onSwipeRefreshLayoutRefresh() {
        randomPage();
        hasMore = true;
        Request();
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

    private void RequestCategoryTask() {
        showProgressbar();
        AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.CaricatureSearchHot.CaricatureSearchHot);
        query.whereGreaterThan(AVOUtil.CaricatureSearchHot.click_time,98000080);
        query.orderByDescending(AVOUtil.CaricatureSearchHot.click_time);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                hideProgressbar();
                onSwipeRefreshLayoutFinish();
                if(list != null && list.size() > 0){
                    for(AVObject item : list){
                        item.put("selected","0");
                    }
                    calist.clear();
                    calist.addAll(list);
                    addTag();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addTag(){
        AVObject object = new AVObject();
        object.put(AVOUtil.CaricatureSearchHot.name,"全部");
        object.put("selected","1");
        calist.add(0,object);
    }

    private void RequestAsyncTask() {
        showProgressbar();
        loading = true;
        AVQuery<AVObject> query = null;
        if(!TextUtils.isEmpty(search_text) && !"全部".equals(search_text)){
            AVQuery<AVObject> priorityQuery = new AVQuery<>(AVOUtil.Caricature.Caricature);
            priorityQuery.whereContains(AVOUtil.Caricature.name, search_text);
            AVQuery<AVObject> statusQuery = new AVQuery<>(AVOUtil.Caricature.Caricature);
            statusQuery.whereContains(AVOUtil.Caricature.tag, search_text);
            query = AVQuery.or(Arrays.asList(priorityQuery,statusQuery));
        }else {
            query = new AVQuery<AVObject>(AVOUtil.Caricature.Caricature);
        }
        query.orderByDescending(AVOUtil.Caricature.views);
        query.skip(skip);
        query.limit(Setings.ca_psize);
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
                        mList.addAll(DataUtil.toCNWBeanList(list));
                        if(addAD()){
                            mAdapter.notifyDataSetChanged();
                        }
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
            }
        });
    }

    private void loadAD(){
        if(ADUtil.IsShowAD){
            if(ADUtil.Advertiser.equals(ADUtil.Advertiser_XF)){
                loadXFAD();
            }else {
                loadTXAD();
            }
        }
    }

    private void loadXFAD(){
        nativeAd = new IFLYNativeAd(getContext(), ADUtil.XXLAD, new IFLYNativeListener() {
            @Override
            public void onConfirm() {
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onAdFailed(AdError arg0) {
                LogUtil.DefalutLog("onAdFailed---"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
                if(ADUtil.Advertiser.equals(ADUtil.Advertiser_XF)){
                    loadTXAD();
                }else {
                    onADFaile();
                }
            }
            @Override
            public void onADLoaded(List<NativeADDataRef> adList) {
                LogUtil.DefalutLog("onADLoaded---");
                if(adList != null && adList.size() > 0){
                    NativeADDataRef nad = adList.get(0);
                    addXFAD(nad);
                }
            }
        });
        nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
        nativeAd.loadAd(1);
    }

    private void addXFAD(NativeADDataRef nad){
        mADObject = new CNWBean();
        mADObject.setmNativeADDataRef(nad);
        mADObject.setAdShow(false);
        if(!loading){
            addAD();
        }
    }

    private void onADFaile(){
        if(ADUtil.isHasLocalAd()){
            NativeADDataRef nad = ADUtil.getRandomAd(getActivity());
            addXFAD(nad);
        }
    }

    private void loadTXAD(){
        TXADUtil.showCDTZX(getActivity(), new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                LogUtil.DefalutLog(adError.getErrorMsg());
                if(ADUtil.Advertiser.equals(ADUtil.Advertiser_TX)){
                    loadXFAD();
                }else {
                    onADFaile();
                }
            }
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                LogUtil.DefalutLog("onADLoaded");
                if(list != null && list.size() > 0){
                    mTXADList.add(list.get(0));
                    mADObject = new CNWBean();
                    mADObject.setmTXADView(list.get(0));
                    if (!loading) {
                        addAD();
                    }
                }
            }
            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onRenderFail");
            }
            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onRenderSuccess");
            }
            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADExposure");
            }
            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADClicked");
            }
            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADClosed");
            }
            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADLeftApplication");
            }
            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADOpenOverlay");
            }
            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                LogUtil.DefalutLog("onADCloseOverlay");
            }
        });
    }

    private boolean addAD(){
        if(mADObject != null && mList != null && mList.size() > 0){
            int index = mList.size() - Setings.page_size + NumberUtil.randomNumberRange(1, 2);
            if(index < 1){
                index = 1;
            }
            mList.add(index,mADObject);
            mAdapter.notifyDataSetChanged();
            mADObject = null;
            return false;
        }else{
            return true;
        }
    }

    private void hideFooterview(){
        mAdapter.hideFooter();
    }

    private void showFooterview(){
        mAdapter.showFooter();
    }

    @Override
    public void OnItemClick(String item) {
        search_text = item;
        skip = 0;
        mList.clear();
        mAdapter.notifyDataSetChanged();
        RequestAsyncTask();
    }
}
