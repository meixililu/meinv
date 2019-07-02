package com.messi.languagehelper.meinv.util;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.facebook.drawee.view.SimpleDraweeView;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.meinv.R;
import com.messi.languagehelper.meinv.adapter.HeaderFooterRecyclerViewAdapter;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import org.json.JSONObject;

import java.util.List;

public class XFYSAD {

	private Activity mContext;
	private View parentView;
	private FrameLayout ad_layout;
	private TextView ad_sign;
	private IFLYNativeAd nativeAd;
	private NativeADDataRef mNativeADDataRef;
	private NativeExpressADView mTXADView;
	private SimpleDraweeView ad_img;
	private LayoutInflater mInflater;
	private String adId;
	public long lastLoadAdTime;
	private boolean isDirectExPosure = true;
	private boolean exposure;
	private HeaderFooterRecyclerViewAdapter mAdapter;

	public int counter;
	public String BDADID = BDADUtil.BD_BANNer;

	public XFYSAD(Activity mContext, View parentView, String adId){
		this.mContext = mContext;
		this.parentView = parentView;
		this.adId = adId;
		mInflater = LayoutInflater.from(mContext);
		ad_img = (SimpleDraweeView)parentView.findViewById(R.id.ad_img);
		ad_layout = (FrameLayout)parentView.findViewById(R.id.ad_layout);
		ad_sign = (TextView)parentView.findViewById(R.id.ad_sign);
		ad_sign.setVisibility(View.GONE);
	}

	public XFYSAD(Activity mContext,String adId){
		this.mContext = mContext;
		this.adId = adId;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setParentView(View parentView){
		LogUtil.DefalutLog("XFYSAD---setParentView");
		if(System.currentTimeMillis() - lastLoadAdTime > 1000*30){
			this.parentView = parentView;
			ad_img = (SimpleDraweeView)parentView.findViewById(R.id.ad_img);
			ad_layout = (FrameLayout)parentView.findViewById(R.id.ad_layout);
			ad_sign = (TextView)parentView.findViewById(R.id.ad_sign);
			ad_sign.setVisibility(View.GONE);
		}
	}

	public void isNeedReload(){
		if(System.currentTimeMillis() - lastLoadAdTime > 1000*45){
			showAd();
		}
	}

	public void showAd(){
		if(ADUtil.IsShowAD){
			if(System.currentTimeMillis() - lastLoadAdTime > 1000*30){
				parentView.setVisibility(View.VISIBLE);
				lastLoadAdTime = System.currentTimeMillis();
				getXXLAd();
			}
		}else {
			hideADView();
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

	public void onLoadAdFaile(){
		counter++;
		getXXLAd();
	}

	private void loadTXAD(){
		LogUtil.DefalutLog("---load TXAD Data---");
		TXADUtil.showCDT(mContext, new NativeExpressAD.NativeExpressADListener() {
			@Override
			public void onNoAD(com.qq.e.comm.util.AdError adError) {
				onLoadAdFaile();
			}

			@Override
			public void onADLoaded(List<NativeExpressADView> list) {
				if(mTXADView != null){
					mTXADView.destroy();
				}
				initFeiXFAD();
				mTXADView = list.get(0);
				ad_layout.addView(mTXADView);
				mTXADView.render();
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
				if(ad_layout != null){
					ad_layout.removeAllViews();
					ad_layout.setVisibility(View.GONE);
				}
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

	private void loadXFAD(){
		LogUtil.DefalutLog("---load XFAD Data---");
		nativeAd = new IFLYNativeAd(mContext, adId, new IFLYNativeListener() {
			@Override
			public void onConfirm() {
			}
			@Override
			public void onCancel() {
			}
			@Override
			public void onAdFailed(AdError arg0) {
				onLoadAdFaile();
			}
			@Override
			public void onADLoaded(List<NativeADDataRef> arg0) {
				LogUtil.DefalutLog("---onADLoaded---");
				if(arg0 != null && arg0.size() > 0){
					mNativeADDataRef = arg0.get(0);
					setAdData(mNativeADDataRef);
				}
			}
		});
		nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		nativeAd.loadAd(ADUtil.adCount);
	}

	public void loadXBKJ() {
		if (ADUtil.isHasLocalAd()) {
			setAdData(ADUtil.getRandomAd(mContext));
		}
	}

	private void setAdData(NativeADDataRef mNativeADDataRef){
		try {
			ad_sign.setVisibility(View.VISIBLE);
			ad_layout.setVisibility(View.GONE);
			parentView.setVisibility(View.VISIBLE);
			ad_img.setImageURI(mNativeADDataRef.getImage());
			if(isDirectExPosure){
				exposure = mNativeADDataRef.onExposured(parentView);
				LogUtil.DefalutLog("XFYSAD-setAdData-exposure:"+exposure);
			}
			parentView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					boolean click = mNativeADDataRef.onClicked(view);
					LogUtil.DefalutLog("XFYSAD-onClick:"+click);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
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
				onLoadAdFaile();
				LogUtil.DefalutLog("BDAD-onAdFailed:");
			}
			@Override
			public void onAdSwitch() {
				LogUtil.DefalutLog("BDAD-onAdSwitch");
			}
			@Override
			public void onAdClose(JSONObject jsonObject) {
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
		ad_img.setVisibility(View.GONE);
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
				LogUtil.DefalutLog("loadCSJAD-onError:");
				onLoadAdFaile();
			}
			@Override
			public void onBannerAdLoad(TTBannerAd ad) {
				if (ad == null) {
					onLoadAdFaile();
					return;
				}
				View bannerView = ad.getBannerView();
				if (bannerView == null) {
					onLoadAdFaile();
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
						onLoadAdFaile();
					}
					@Override
					public void onCancel() {
					}
				});
			}
		});
	}


	public void hideADView(){
		try {
			if(parentView != null){
				parentView.setVisibility(View.GONE);
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(mAdapter != null){
						mAdapter.hideHeader();
					}
				}
			},100);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ExposureAD(){
		if(!exposure){
			exposure = mNativeADDataRef.onExposured(parentView);
			LogUtil.DefalutLog("XFYSAD-ExposureAD-exposure:"+exposure);
		}
	}

	public void setDirectExPosure(boolean directExPosure) {
		isDirectExPosure = directExPosure;
	}

	public void setAdapter(HeaderFooterRecyclerViewAdapter adapter){
		this.mAdapter = adapter;
	}

}