package com.messi.languagehelper.meinv;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.adapter.RcMeinvListAdapter;
import com.messi.languagehelper.meinv.bean.JsonRootBean;
import com.messi.languagehelper.meinv.bean.SougoItem;
import com.messi.languagehelper.meinv.http.LanguagehelperHttpClient;
import com.messi.languagehelper.meinv.http.UICallback;
import com.messi.languagehelper.meinv.impl.FragmentProgressbarListener;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.NumberUtil;
import com.messi.languagehelper.meinv.util.Settings;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class JokeFragment extends BaseFragment implements OnClickListener {

    private RecyclerView listview;
    private RcMeinvListAdapter mAdapter;
    private List<SougoItem> avObjects;
    private int skip;
    private IFLYNativeAd nativeAd;
    private int maxRandom;
    private boolean loading;
    private boolean hasMore = true;
    private SougoItem mADObject;
    private LinearLayoutManager mLinearLayoutManager;
    private SharedPreferences sp;
    private List<NativeExpressADView> mTXADList;
    private String tag;
    private View view;

    public static JokeFragment newInstance(String tag){
        JokeFragment fragment = new JokeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tag",tag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        this.tag = mBundle.getString("tag");
        sp = Settings.getSharedPreferences(getActivity());
        maxRandom = sp.getInt(tag,0);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mProgressbarListener = (FragmentProgressbarListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentProgressbarListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        if(view != null){
            ViewGroup parent = (ViewGroup)view.getParent();
            if(parent != null){
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.joke_picture_fragment, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void loadDataOnStart() {
        super.loadDataOnStart();
        random();
        loadAD();
        getData();
    }

    private void initViews(View view) {
        avObjects = new ArrayList<SougoItem>();
        mTXADList = new ArrayList<NativeExpressADView>();
        listview = (RecyclerView) view.findViewById(R.id.listview);
        initSwipeRefresh(view);
        mAdapter = new RcMeinvListAdapter();
        mAdapter.setItems(avObjects);
        mAdapter.setFooter(new Object());
        hideFooterview();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        listview.setLayoutManager(mLinearLayoutManager);
        listview.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(getContext())
                        .colorResId(R.color.text_tint)
                        .sizeResId(R.dimen.list_divider_size)
                        .build());
        listview.setAdapter(mAdapter);
        setListOnScrollListener();
    }

    public void setListOnScrollListener() {
        listview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible = mLinearLayoutManager.getChildCount();
                int total = mLinearLayoutManager.getItemCount();
                int firstVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if (!loading && hasMore) {
                    if ((visible + firstVisibleItem) >= total) {
                        loadAD();
                        getData();
                    }
                }
                isADInList(recyclerView, firstVisibleItem, visible);
            }
        });
    }

    private void isADInList(RecyclerView view, int first, int vCount){
        if(avObjects.size() > 3){
            for(int i=first;i< (first+vCount);i++){
                if(i < avObjects.size() && i > 0){
                    SougoItem mAVObject = avObjects.get(i);
                    if(mAVObject != null && mAVObject.getIsAdShow() > 0){
                        if(mAVObject.getIsAdShow() != 1 && misVisibleToUser){
                            NativeADDataRef mNativeADDataRef = mAVObject.getmNativeADDataRef();
                            if(mNativeADDataRef != null){
                                boolean isExposure = mNativeADDataRef.onExposured(view.getChildAt(i%vCount));
                                LogUtil.DefalutLog("isExposure:"+isExposure);
                                if(isExposure){
                                    mAVObject.setIsAdShow(2);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void random(){
        skip = (int) Math.round(Math.random()*maxRandom);
        LogUtil.DefalutLog("skip:"+skip);
    }

    @Override
    public void onSwipeRefreshLayoutRefresh() {
        loadAD();
        hideFooterview();
        random();
        avObjects.clear();
        mAdapter.notifyDataSetChanged();
        getData();
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

    private void getData(){
        loading = true;
        showProgressbar();
        String url = Settings.sougouApi+tag+"&start="+skip;
        LanguagehelperHttpClient.get(url,new UICallback(getActivity()){
            @Override
            public void onFailured() {
            }

            @Override
            public void onFinished() {
                loading = false;
                hideProgressbar();
                onSwipeRefreshLayoutFinish();
            }

            @Override
            public void onResponsed(String responseString) {
                if(!TextUtils.isEmpty(responseString)){
                    JsonRootBean mResult = JSON.parseObject(responseString,JsonRootBean.class);
                    setData(mResult);
                }
            }
        });
    }

    private void setData(JsonRootBean mResult){
        if(mResult != null && mResult.getAll_items() != null){
            maxRandom = mResult.getMaxEnd();
            if(mResult.getAll_items().size() == 0){
                ToastUtil.diaplayMesShort(getContext(), "没有了！");
                hideFooterview();
                hasMore = false;
            }else{
                if(avObjects != null && mAdapter != null){
                    avObjects.addAll(mResult.getAll_items());
                    if(addAD()){
                        mAdapter.notifyDataSetChanged();
                    }
                    skip += 10;
                    hasMore = true;
                    showFooterview();
                }
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
        mADObject = new SougoItem();
        mADObject.setIsAdShow(1);
        mADObject.setmNativeADDataRef(nad);
        if(!loading){
            addAD();
        }
    }

    private void onADFaile(){
        if(ADUtil.isHasLocalAd()){
            NativeADDataRef nad = ADUtil.getRandomAd();
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
                    mADObject = new SougoItem();
                    mADObject.setIsAdShow(1);
                    mADObject.setmADView(list.get(0));
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
        if(mADObject != null && avObjects != null && avObjects.size() > 0){
            int index = avObjects.size() - Settings.page_size + NumberUtil.randomNumberRange(1, 2);
            if(index < 1){
                index = 1;
            }
            avObjects.add(index,mADObject);
            mAdapter.notifyDataSetChanged();
            mADObject = null;
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void showFooterview() {
        mAdapter.showFooter();
    }

    private void hideFooterview() {
        mAdapter.hideFooter();
    }

    public void onTabReselected(int index) {
        listview.scrollToPosition(0);
        onSwipeRefreshLayoutRefresh();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Settings.saveSharedPreferences(sp,tag,maxRandom);
        if(mTXADList != null){
            for(NativeExpressADView adView : mTXADList){
                adView.destroy();
            }
        }
    }
}
