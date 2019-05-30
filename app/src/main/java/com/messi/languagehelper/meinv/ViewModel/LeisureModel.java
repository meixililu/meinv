package com.messi.languagehelper.meinv.ViewModel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdDislike;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTBannerAd;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.BDADUtil;
import com.messi.languagehelper.meinv.util.CSJADUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;
import com.messi.languagehelper.meinv.util.SystemUtil;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import org.json.JSONObject;

import java.util.List;

public class LeisureModel {

    public static boolean misVisibleToUser;
    public int counter;
    public Activity mContext;
    public SharedPreferences sp;
    private NativeADDataRef mNativeADDataRef;
    private NativeExpressADView mTXADView;
    private long lastLoadAd;
    private boolean exposureXFAD;
    private String currentAD;
    private String BDADID = BDADUtil.BD_BANNer;
    private String XFADID = ADUtil.MRYJYSNRLAd;

    public FrameLayout xx_ad_layout;
    public FrameLayout ad_layout;
    public TextView ad_sign;
    public SimpleDraweeView adImg;

    public LeisureModel(Activity mContext){
        this.mContext = mContext;
        sp = Setings.getSharedPreferences(mContext);
    }

    public void setViews(TextView ad_sign, SimpleDraweeView adImg,
                         FrameLayout xx_ad_layout, FrameLayout ad_layout){
        this.adImg = adImg;
        this.ad_sign = ad_sign;
        this.xx_ad_layout = xx_ad_layout;
        this.ad_layout = ad_layout;
    }

    public void showAd(){
        if(ADUtil.IsShowAD){
            xx_ad_layout.setVisibility(View.VISIBLE);
            loadAD();
        }else {
            xx_ad_layout.setVisibility(View.GONE);
        }
    }

    public void loadAD(){
        try {
            currentAD = ADUtil.getAdProvider(counter);
            lastLoadAd = System.currentTimeMillis();
            if(!TextUtils.isEmpty(currentAD)){
                LogUtil.DefalutLog("------ad-------"+currentAD);
                if(ADUtil.GDT.equals(currentAD)){
                    loadTXAD();
                }else if(ADUtil.BD.equals(currentAD)){
                    loadBDAD();
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

    public void getAd(){
        counter++;
        loadAD();
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
                LogUtil.DefalutLog("LeisureModel-onAdFailed");
                getAd();
            }
            @Override
            public void onADLoaded(List<NativeADDataRef> adList) {
                LogUtil.DefalutLog("onADLoaded");
                if (adList != null && adList.size() > 0) {
                    exposureXFAD = false;
                    setAd(adList.get(0));
                }
            }
        });
        nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
        nativeAd.loadAd(1);
    }

    public void setAd(NativeADDataRef mADDataRef) {
        if (mADDataRef != null) {
            mNativeADDataRef = mADDataRef;
            ad_sign.setVisibility(View.VISIBLE);
            adImg.setVisibility(View.VISIBLE);
            ad_layout.setVisibility(View.GONE);
            DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                    .setAutoPlayAnimations(true)
                    .setUri(Uri.parse(mNativeADDataRef.getImage()))
                    .build();
            adImg.setController(mDraweeController);
            xx_ad_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onXFADClick();
                }
            });
            exposedXFAD();
        }
    }

    public void onXFADClick(){
        if (mNativeADDataRef != null) {
            boolean onClicked = mNativeADDataRef.onClicked(xx_ad_layout);
            LogUtil.DefalutLog("onClicked:" + onClicked);
        }
    }

    public void exposedXFAD() {
        if (!exposureXFAD && mNativeADDataRef != null) {
            exposureXFAD = mNativeADDataRef.onExposured(xx_ad_layout);
            LogUtil.DefalutLog("exposedAd-exposureXFAD:" + exposureXFAD);
        }
    }

    public void exposedAd() {
        LogUtil.DefalutLog("exposedAd");
        if (ADUtil.XF.equals(currentAD)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exposedXFAD();
                }
            }, 500);
        }
        if (misVisibleToUser && lastLoadAd > 0) {
            if (System.currentTimeMillis() - lastLoadAd > 16*1000) {
                currentAD = ADUtil.getAdProvider(counter);
                if(TextUtils.isEmpty(currentAD) ||
                        ADUtil.XBKJ.equals(currentAD) ||
                        ADUtil.XF.equals(currentAD)){
                    counter = 0;
                }
                loadAD();
            }
        }
    }

    private void loadTXAD() {
        TXADUtil.showCDT(mContext, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onNoAD(com.qq.e.comm.util.AdError adError) {
                LogUtil.DefalutLog("loadTXAD0-onNoAD");
                getAd();
            }

            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                LogUtil.DefalutLog("onADLoaded");
                if (list != null && list.size() > 0) {
                    if (mTXADView != null) {
                        mTXADView.destroy();
                    }
                    initFeiXFAD();
                    mTXADView = list.get(0);
                    ad_layout.addView(mTXADView);
                    mTXADView.render();
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {
                nativeExpressADView.render();
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
                getAd();
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

    public void loadXBKJ() {
        if (ADUtil.isHasLocalAd()) {
            setAd(ADUtil.getRandomAd(mContext));
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
                getAd();
                LogUtil.DefalutLog("BDAD-onAdFailed");
            }
            @Override
            public void onAdSwitch() {
                LogUtil.DefalutLog("BDAD-onAdSwitch");
            }
            @Override
            public void onAdClose(JSONObject jsonObject) {
                getAd();
                LogUtil.DefalutLog("BDAD-onAdClose");
            }
        });
        initFeiXFAD();
        int height = (int)(SystemUtil.SCREEN_WIDTH / 2);
        LinearLayout.LayoutParams rllp = new LinearLayout.LayoutParams(SystemUtil.SCREEN_WIDTH, height);
        ad_layout.addView(adView,rllp);
    }

    public void initFeiXFAD(){
        ad_sign.setVisibility(View.GONE);
        adImg.setVisibility(View.GONE);
        ad_layout.setVisibility(View.VISIBLE);
        ad_layout.removeAllViews();
    }

    public void loadCSJAD(){
        LogUtil.DefalutLog("loadCSJAD");
        TTAdNative mTTAdNative = CSJADUtil.get().createAdNative(mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(CSJADUtil.CSJ_BANNer2ID)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(690, 388)
                .build();
        mTTAdNative.loadBannerAd(adSlot, new TTAdNative.BannerAdListener() {
            @Override
            public void onError(int i, String s) {
                LogUtil.DefalutLog("loadCSJAD-onError");
                getAd();
            }
            @Override
            public void onBannerAdLoad(TTBannerAd ad) {
                if (ad == null) {
                    getAd();
                    return;
                }
                View bannerView = ad.getBannerView();
                if (bannerView == null) {
                    getAd();
                    return;
                }
                //设置轮播的时间间隔  间隔在30s到120秒之间的值，不设置默认不轮播
                ad.setSlideIntervalTime(30 * 1000);
                initFeiXFAD();
                int height = (int)(SystemUtil.SCREEN_WIDTH / 1.8);
                LinearLayout.LayoutParams rllp = new LinearLayout.LayoutParams(SystemUtil.SCREEN_WIDTH, height);
                ad_layout.addView(bannerView,rllp);
                //设置广告互动监听回调
                ad.setShowDislikeIcon(new TTAdDislike.DislikeInteractionCallback() {
                    @Override
                    public void onSelected(int position, String value) {
                        getAd();
                    }
                    @Override
                    public void onCancel() {
                    }
                });
            }
        });
    }

    public void onDestroy(){
        if (mTXADView != null) {
            mTXADView.destroy();
        }
    }

    public void setXFADID(String XFADID) {
        this.XFADID = XFADID;
    }

}
