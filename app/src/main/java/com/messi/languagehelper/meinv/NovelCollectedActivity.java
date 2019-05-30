package com.messi.languagehelper.meinv;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.adapter.RcNovelListAdapter;
import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.NumberUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.messi.languagehelper.meinv.util.ToastUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NovelCollectedActivity extends BaseActivity implements View.OnClickListener {

    private RecyclerView category_lv;
    private TextView manage_btn;
    private LinearLayout delete_layout;
    private TextView empty_btn;
    private TextView delete_btn;
    private RcNovelListAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<CNWBean> mList;
    private IFLYNativeAd nativeAd;
    private boolean loading;
    private CNWBean mADObject;
    private List<NativeExpressADView> mTXADList;
    private boolean isDeleteModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owllook_list_activity);
        init();
        initSwipeRefresh();
        loadAD();
        getData();
    }

    private void init() {
        mList = new ArrayList<CNWBean>();
        mTXADList = new ArrayList<NativeExpressADView>();
        initSwipeRefresh();
        category_lv = (RecyclerView) findViewById(R.id.listview);
        manage_btn = (TextView) findViewById(R.id.manage_btn);
        delete_layout = (LinearLayout) findViewById(R.id.delete_layout);
        empty_btn = (TextView) findViewById(R.id.empty_btn);
        delete_btn = (TextView) findViewById(R.id.delete_btn);
        manage_btn.setOnClickListener(this);
        empty_btn.setOnClickListener(this);
        delete_btn.setOnClickListener(this);
        mAdapter = new RcNovelListAdapter();
        mAdapter.setItems(mList);
        mLinearLayoutManager = new LinearLayoutManager(this);
        category_lv.setLayoutManager(mLinearLayoutManager);
        category_lv.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .colorResId(R.color.text_tint)
                        .sizeResId(R.dimen.padding_10)
                        .marginResId(R.dimen.padding_margin, R.dimen.padding_margin)
                        .build());
        category_lv.setAdapter(mAdapter);
        setListOnScrollListener();
    }

    public void setListOnScrollListener() {
        category_lv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible = mLinearLayoutManager.getChildCount();
                int firstVisibleItem = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
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
        showProgressbar();
        Observable.create(new ObservableOnSubscribe<List<CNWBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CNWBean>> e) throws Exception {
                e.onNext(getCollectedList());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CNWBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(List<CNWBean> list) {
                        setData(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        loading = false;
                        hideProgressbar();
                        onSwipeRefreshLayoutFinish();
                    }
                });
    }

    private List<CNWBean> getCollectedList(){
        return BoxHelper.getCollectedList(AVOUtil.Novel.Novel,0,0);
    }

    private void setData(List<CNWBean> list){
        if(list != null && list.size() > 0){
            if(mList != null && mAdapter != null){
                mList.clear();
                mList.addAll(list);
                initDeleteModel(list);
                if(addAD()){
                    mAdapter.notifyDataSetChanged();
                }
            }
        }else {
            ToastUtil.diaplayMesShort(this, "没有找到");
        }
    }

    private void loadXFAD(){
        nativeAd = new IFLYNativeAd(this, ADUtil.XXLAD, new IFLYNativeListener() {
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
            NativeADDataRef nad = ADUtil.getRandomAd(this);
            addXFAD(nad);
        }
    }

    private void loadTXAD(){
        TXADUtil.showCDT(this, new NativeExpressAD.NativeExpressADListener() {
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

    private void manageBtn(){
        if(!isDeleteModel){
            delete_layout.setVisibility(View.VISIBLE);
            manage_btn.setText(getString(R.string.dome));
            isDeleteModel = true;
            setDeleteModel(mList,"1");
        }else {
            delete_layout.setVisibility(View.GONE);
            manage_btn.setText(getString(R.string.manage));
            isDeleteModel = false;
            setDeleteModel(mList,"0");
        }
    }

    private void initDeleteModel(List<CNWBean> list){
        if(isDeleteModel){
            setDeleteModel(list,"1");
        }else {
            setDeleteModel(list,"0");
        }
    }

    private void setDeleteModel(List<CNWBean> list,String status){
        for(CNWBean mAVObject : list){
            mAVObject.setDelete_model(status);
            mAVObject.setIs_need_delete("0");
        }
        mAdapter.notifyDataSetChanged();
    }

    private void reset(){
        delete_layout.setVisibility(View.GONE);
        manage_btn.setText(getString(R.string.manage));
        isDeleteModel = false;
        setDeleteModel(mList,"0");
    }

    private void deleteAll(){
        BoxHelper.deleteAllData(AVOUtil.Novel.Novel);
        mList.clear();
        reset();
    }

    private void deleteBooks(){
        List<CNWBean> newList = new ArrayList<CNWBean>();
        for(CNWBean mAVObject : mList){
            if(!TextUtils.isEmpty(mAVObject.getIs_need_delete())){
                if("1".equals(mAVObject.getIs_need_delete())){
                    BoxHelper.deleteCNWBean(mAVObject);
                }else {
                    newList.add(mAVObject);
                }
            }
        }
        mList.clear();
        mList.addAll(newList);
        reset();
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.manage_btn){
            manageBtn();
        } else if(view.getId() == R.id.empty_btn) {
            deleteAll();
        } else if(view.getId() == R.id.delete_btn) {
            deleteBooks();
        }
    }
}
