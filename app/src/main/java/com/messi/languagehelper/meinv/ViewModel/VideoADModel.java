package com.messi.languagehelper.meinv.ViewModel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTFeedAd;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.CSJADUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.Setings;

import java.util.List;

public class VideoADModel {

    public int counter;
    public Activity mContext;
    public SharedPreferences sp;

    public FrameLayout xx_ad_layout;
    public FrameLayout ad_layout;
    public ImageView ad_close_btn;

    public VideoADModel(Activity mContext,FrameLayout xx_ad_layout){
        this.mContext = mContext;
        sp = Setings.getSharedPreferences(mContext);
        this.xx_ad_layout = xx_ad_layout;
        ad_layout = xx_ad_layout.findViewById(R.id.ad_layout);
//        ad_close_btn = xx_ad_layout.findViewById(R.id.ad_close_btn);
        ad_close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xx_ad_layout.setVisibility(View.GONE);
            }
        });
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
            loadCSJAD();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCSJAD(){
        LogUtil.DefalutLog("loadCSJAD-Video");
        TTAdNative mTTAdNative = CSJADUtil.get().createAdNative(mContext);
        AdSlot adSlot = new AdSlot.Builder()
                .setCodeId(CSJADUtil.CSJ_XXLSP)
                .setSupportDeepLink(true)
                .setImageAcceptedSize(690, 388)
                .build();
        mTTAdNative.loadFeedAd(adSlot, new TTAdNative.FeedAdListener() {
            @Override
            public void onError(int i, String s) {
                LogUtil.DefalutLog("loadCSJAD-onError");
                xx_ad_layout.setVisibility(View.GONE);
            }

            @Override
            public void onFeedAdLoad(List<TTFeedAd> ads) {
                if (ads == null || ads.isEmpty()) {
                    xx_ad_layout.setVisibility(View.GONE);
                    return;
                }
                XXLRootModel.getCSJDView(mContext,ads.get(0),ad_layout);
            }
        });
    }

}
