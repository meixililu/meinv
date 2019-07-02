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
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcCaricatureHomeListAdapter;
import com.messi.languagehelper.meinv.box.CNWBean;
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
import java.util.List;

public class CaricatureHomeFragment extends BaseFragment implements View.OnClickListener{

    private static final int NUMBER_OF_COLUMNS = 3;
    private RecyclerView category_lv;
    private FrameLayout search_btn;
    private Toolbar my_awesome_toolbar;
    private RcCaricatureHomeListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<CNWBean> mList;
    private int skip = 0;
    private int max_count = 8000;
    private boolean loading;
    private boolean hasMore = true;
    private boolean isNeedClear = true;
    private IFLYNativeAd nativeAd;
    private CNWBean mADObject;
    private List<NativeExpressADView> mTXADList;

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
        loadAD();
        RequestAsyncTask();
        return view;
    }

    private void initViews(View view) {
        mList = new ArrayList<CNWBean>();
        mTXADList = new ArrayList<NativeExpressADView>();
        my_awesome_toolbar = (Toolbar) view.findViewById(R.id.my_awesome_toolbar);
        search_btn = (FrameLayout) view.findViewById(R.id.search_btn);
        category_lv = (RecyclerView) view.findViewById(R.id.listview);
        my_awesome_toolbar.setTitle(R.string.recommend);
        search_btn.setOnClickListener(this);
        category_lv.setHasFixedSize(true);
        mAdapter = new RcCaricatureHomeListAdapter();
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
        loadAD();
        randomPage();
        hasMore = true;
        RequestAsyncTask();
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
    public void onClick(View view) {
        if(view.getId() == R.id.search_btn){
            toActivity(CaricatureSearchActivity.class,null);
        }
    }

}
