package com.messi.languagehelper.meinv.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.bytedance.sdk.openadsdk.TTImage;
import com.bytedance.sdk.openadsdk.TTNativeAd;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.BDADUtil;
import com.messi.languagehelper.meinv.util.CSJADUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class XXLRootModel {

    public boolean loading;
    public boolean hasMore;

    public int counter;
    public Activity mContext;
    public SharedPreferences sp;
    public String XFADID = ADUtil.XXLAD;
    public String BDADID = BDADUtil.BD_BANNer;
    public String CSJADID = CSJADUtil.CSJ_XXL;
    public int TXADType = 2;
    public IFLYNativeAd nativeAd;
    public List<NativeExpressADView> mTXADList;

    public List avObjects;
    public RecyclerView.Adapter mAdapter;

    public XXLRootModel(Activity mContext){
        this.mContext = mContext;
        sp = Setings.getSharedPreferences(mContext);
        mTXADList = new ArrayList<NativeExpressADView>();
    }

    public void setAdapter(List avObjects, RecyclerView.Adapter mAdapter){
        this.avObjects = avObjects;
        this.mAdapter = mAdapter;
    }

    public void showAd(){
        if(ADUtil.IsShowAD){
            getXXLAd();
        }
    }

    public void getXXLAd(){
        try {
            String currentAD = ADUtil.getAdProvider(counter);
            if(!TextUtils.isEmpty(currentAD)){
                LogUtil.DefalutLog("------ad-------"+currentAD);
                if(ADUtil.GDT.equals(currentAD)){
                    loadTXAD();
                }else if(ADUtil.BD.equals(currentAD)){
                    loadCSJAD();
                }else if(ADUtil.CSJ.equals(currentAD)){
                    loadCSJAD();
                }else if(ADUtil.XF.equals(currentAD)){
                    loadXFAD();
                }else if(ADUtil.XBKJ.equals(currentAD)){
                    loadXBKJ();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onLoadAdFaile(){
        counter++;
        getXXLAd();
    }

    public void loadXFAD() {
        IFLYNativeAd nativeAd = new IFLYNativeAd(mContext, XFADID, new IFLYNativeListener() {
            @Override
            public void onConfirm() {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onAdFailed(AdError arg0) {
                LogUtil.DefalutLog("XXLModel-onAdFailed");
                onLoadAdFaile();
            }

            @Override
            public void onADLoaded(List<NativeADDataRef> adList) {
                LogUtil.DefalutLog("XXLModel-XFonADLoaded");
                if (adList != null && adList.size() > 0) {
                    NativeADDataRef nad = adList.get(0);
                    addXFAD(nad);
                    addAD();
                }
            }
        });
        nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
        nativeAd.loadAd(1);
    }

    public abstract void addXFAD(NativeADDataRef nad);

    public void loadTXAD() {
        TXADUtil.showXXLAD(mContext,TXADType, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                LogUtil.DefalutLog("TX-onNoAD");
                onLoadAdFaile();
            }
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                LogUtil.DefalutLog("TX-onADLoaded");
                if(list != null && list.size() > 0){
                    mTXADList.add(list.get(0));
                    addTXAD(list.get(0));
                    addAD();
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

    public abstract void addTXAD(NativeExpressADView mADView);

    public void loadXBKJ() {
        if (ADUtil.isHasLocalAd()) {
            addXFAD(ADUtil.getRandomAd(mContext));
        }
    }

    public void loadBDAD(){
        AdView adView = new AdView(mContext,BDADID);
        adView.setListener(new AdViewListener(){
            @Override
            public void onAdReady(AdView adView) {
                LogUtil.DefalutLog("BDAD-onAdReady");
            }
            @Override
            public void onAdShow(JSONObject jsonObject) {
                LogUtil.DefalutLog("BDAD-onAdShow");
            }
            @Override
            public void onAdClick(JSONObject jsonObject) {
                LogUtil.DefalutLog("BDAD-onAdClick");
            }
            @Override
            public void onAdFailed(String s) {
                LogUtil.DefalutLog("BDAD-onAdFailed");
                onLoadAdFaile();
            }
            @Override
            public void onAdSwitch() {
                LogUtil.DefalutLog("BDAD-onAdSwitch");
            }
            @Override
            public void onAdClose(JSONObject jsonObject) {
                LogUtil.DefalutLog("BDAD-onAdClose");
                onLoadAdFaile();
            }
        });
        addBDAD(adView);
        addAD();
    }

    public abstract void addBDAD(AdView adView);

    public void loadCSJAD(){
        LogUtil.DefalutLog("loadCSJAD");
        TTAdNative mTTAdNative = CSJADUtil.get().createAdNative(mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(CSJADID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(690, 388)
                .build();
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {
                LogUtil.DefalutLog("loadCSJAD-onError");
                onLoadAdFaile();
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    onLoadAdFaile();
                    return;
                }
                addCSJAD(ads.get(0));
                addAD();
            }
        });
    }

    public abstract void addCSJAD(TTFeedAd ad);

    public abstract boolean addAD();

    public void onDestroy(){
        if(mTXADList != null){
            for(NativeExpressADView adView : mTXADList){
                adView.destroy();
            }
        }
    }

    public void setXFADID(String XFADID) {
        this.XFADID = XFADID;
    }

    public static void getCSJDView(Context context, TTFeedAd ad, FrameLayout ad_layout){
        View view = null;
        if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_LARGE_IMG) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_ad_large_pic,null);
            initCSJLargePicItem(view,ad,ad_layout);
        } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_GROUP_IMG) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_ad_group_pic,null);
            initCSJGroupPicItem(view,ad,ad_layout);
        } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_SMALL_IMG) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_ad_small_pic,null);
            initCSJSmallPicItem(view,ad,ad_layout);
        } else if (ad.getImageMode() == TTAdConstant.IMAGE_MODE_VIDEO) {
            view = LayoutInflater.from(context).inflate(R.layout.listitem_ad_large_video,null);
            initCSJVideoItem(view,ad,ad_layout);
        }
    }

    public static void initCSJGroupPicItem(View view, TTFeedAd ttFeedAd, FrameLayout ad_layout){
        initBaseItem(view,ttFeedAd,ad_layout);
        SimpleDraweeView mGroupImage1 = (SimpleDraweeView) view.findViewById(R.id.iv_listitem_image1);
        SimpleDraweeView mGroupImage2 = (SimpleDraweeView) view.findViewById(R.id.iv_listitem_image2);
        SimpleDraweeView mGroupImage3 = (SimpleDraweeView) view.findViewById(R.id.iv_listitem_image3);
        if (ttFeedAd.getImageList() != null && ttFeedAd.getImageList().size() >= 3) {
            TTImage image1 = ttFeedAd.getImageList().get(0);
            TTImage image2 = ttFeedAd.getImageList().get(1);
            TTImage image3 = ttFeedAd.getImageList().get(2);
            if (image1 != null && image1.isValid()) {
                mGroupImage1.setImageURI(image1.getImageUrl());
            }
            if (image2 != null && image2.isValid()) {
                mGroupImage2.setImageURI(image2.getImageUrl());
            }
            if (image3 != null && image3.isValid()) {
                mGroupImage3.setImageURI(image3.getImageUrl());
            }
        }
    }

    public static void initCSJLargePicItem(View view, TTFeedAd ttFeedAd, FrameLayout ad_layout){
        initBaseItem(view,ttFeedAd,ad_layout);
        SimpleDraweeView mGroupImage1 = (SimpleDraweeView) view.findViewById(R.id.iv_listitem_image);

        if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
            TTImage image1 = ttFeedAd.getImageList().get(0);
            if (image1 != null && image1.isValid()) {
                DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                        .setAutoPlayAnimations(true)
                        .setUri(Uri.parse(image1.getImageUrl()))
                        .build();
                mGroupImage1.setController(mDraweeController);
            }
        }
    }

    public static void initCSJSmallPicItem(View view, TTFeedAd ttFeedAd, FrameLayout ad_layout){
        initBaseItem(view,ttFeedAd,ad_layout);
        SimpleDraweeView mGroupImage1 = (SimpleDraweeView) view.findViewById(R.id.iv_listitem_image);
        if (ttFeedAd.getImageList() != null && !ttFeedAd.getImageList().isEmpty()) {
            TTImage image1 = ttFeedAd.getImageList().get(0);
            if (image1 != null && image1.isValid()) {
                mGroupImage1.setImageURI(image1.getImageUrl());
            }
        }
    }

    public static void initCSJVideoItem(View view, TTFeedAd ttFeedAd, FrameLayout ad_layout){
        initBaseItem(view,ttFeedAd,ad_layout);
        FrameLayout videoView = (FrameLayout) view.findViewById(R.id.iv_listitem_video);
        ttFeedAd.setVideoAdListener(new TTFeedAd.VideoAdListener() {
            @Override
            public void onVideoLoad(TTFeedAd ad) {
                LogUtil.DefalutLog("CSJVideo-onVideoLoad");
            }

            @Override
            public void onVideoError(int errorCode, int extraCode) {
                LogUtil.DefalutLog("CSJVideo-onVideoError");
            }

            @Override
            public void onVideoAdStartPlay(TTFeedAd ad) {
                LogUtil.DefalutLog("CSJVideo-onVideoAdStartPlay");
            }

            @Override
            public void onVideoAdPaused(TTFeedAd ad) {
                LogUtil.DefalutLog("CSJVideo-onVideoAdPaused");
            }

            @Override
            public void onVideoAdContinuePlay(TTFeedAd ad) {
                LogUtil.DefalutLog("CSJVideo-onVideoAdContinuePlay");
            }
        });
        View video = ttFeedAd.getAdView();
        if (video != null) {
            if (video.getParent() != null) {
                ((ViewGroup) video.getParent()).removeView(video);
            }
            videoView.removeAllViews();
            videoView.addView(video);
        }

    }

    public static void initBaseItem(View view, TTFeedAd ad, FrameLayout ad_layout){
        TextView mTitle = (TextView) view.findViewById(R.id.tv_listitem_ad_title);
        TextView mSource = (TextView) view.findViewById(R.id.tv_listitem_ad_source);
        TextView mDescription = (TextView) view.findViewById(R.id.tv_listitem_ad_desc);
        mTitle.setText(ad.getDescription() == null ? "" : ad.getDescription());
        mDescription.setText(ad.getTitle());
        mSource.setText("广告");
        List<View> clickViewList = new ArrayList<>();
        clickViewList.add(view);
        List<View> creativeViewList = new ArrayList<>();
        creativeViewList.add(mDescription);
        ad.registerViewForInteraction((ViewGroup)view, clickViewList, creativeViewList, new TTNativeAd.AdInteractionListener() {
            @Override
            public void onAdClicked(View view, TTNativeAd ad) {
                LogUtil.DefalutLog("CSJXXLAD-onAdClicked");
            }

            @Override
            public void onAdCreativeClick(View view, TTNativeAd ad) {
                LogUtil.DefalutLog("CSJXXLAD-onAdCreativeClick");
            }

            @Override
            public void onAdShow(TTNativeAd ad) {
                LogUtil.DefalutLog("CSJXXLAD-onAdShow");
            }
        });
        ad_layout.removeAllViews();
        ad_layout.addView(view);
    }

}
