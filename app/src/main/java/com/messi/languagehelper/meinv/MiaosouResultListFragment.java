package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.karumi.headerrecyclerview.HeaderSpanSizeLookup;
import com.messi.languagehelper.meinv.adapter.RcMiaosouListAdapter;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.http.LanguagehelperHttpClient;
import com.messi.languagehelper.meinv.http.UICallback;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.HtmlParseUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.NumberUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.ArrayList;
import java.util.List;

public class MiaosouResultListFragment extends BaseFragment implements OnClickListener {

    private static final int NUMBER_OF_COLUMNS = 3;
    private RecyclerView category_lv;
    private RcMiaosouListAdapter mAdapter;
    private GridLayoutManager layoutManager;
    private List<CNWBean> mList;
    private IFLYNativeAd nativeAd;
    private boolean loading;
    private CNWBean mADObject;
    private List<NativeExpressADView> mTXADList;
    private String url;
    private View view;

    public static MiaosouResultListFragment newInstance(String url){
        MiaosouResultListFragment fragment = new MiaosouResultListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        this.url = mBundle.getString("url");
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
        loadAD();
        getData();
    }

    private void initViews(View view) {
        mList = new ArrayList<CNWBean>();
        mTXADList = new ArrayList<NativeExpressADView>();
        category_lv = (RecyclerView) view.findViewById(R.id.listview);
        initSwipeRefresh(view);
        mAdapter = new RcMiaosouListAdapter();
        mAdapter.setFooter(new Object());
        mAdapter.setItems(mList);
        layoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
        HeaderSpanSizeLookup headerSpanSizeLookup = new HeaderSpanSizeLookup(mAdapter, layoutManager);
        layoutManager.setSpanSizeLookup(headerSpanSizeLookup);
        category_lv.setLayoutManager(layoutManager);
        category_lv.setAdapter(mAdapter);
        setListOnScrollListener();
    }

    public void setListOnScrollListener() {
        category_lv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible = layoutManager.getChildCount();
                int firstVisibleItem = layoutManager.findFirstCompletelyVisibleItemPosition();
                isADInList(recyclerView, firstVisibleItem, visible);
            }
        });
    }

    private void isADInList(RecyclerView view, int first, int vCount){
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
        hideFooterview();
        mList.clear();
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
        showFooterview();
        if(TextUtils.isEmpty(url)){
            hideFooterview();
            ToastUtil.diaplayMesShort(getContext(), "没有找到");
            return;
        }
        LanguagehelperHttpClient.get(url,new UICallback(getActivity()){
            @Override
            public void onFailured() {
            }
            @Override
            public void onFinished() {
                loading = false;
                hideFooterview();
                onSwipeRefreshLayoutFinish();
            }
            @Override
            public void onResponsed(String responseString) {
                if(!TextUtils.isEmpty(responseString)){
                    LogUtil.DefalutLog("responseString:"+responseString);
                    List<CNWBean> tlist = HtmlParseUtil.parseMiaosouHtml(url,responseString);
                    setData(tlist);
                }
            }
        });
    }

    private void setData(List<CNWBean> list){
        if(list != null && list.size() > 0){
            if(mList != null && mAdapter != null){
                mList.clear();
                mList.addAll(list);
                if(addAD()){
                    mAdapter.notifyDataSetChanged();
                }
            }
        }else {
            ToastUtil.diaplayMesShort(getContext(), "没有找到");
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

    @Override
    public void onClick(View v) {

    }

    private void showFooterview() {
        mAdapter.showFooter();
    }

    private void hideFooterview() {
        mAdapter.hideFooter();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mTXADList != null){
            for(NativeExpressADView adView : mTXADList){
                adView.destroy();
            }
        }
    }
}
