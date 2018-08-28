package com.messi.languagehelper.meinv.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

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

import java.util.List;

public class XFYSAD {
	
	private Activity mContext;
	private View parentView;
	private FrameLayout xx_ad_layout;
	private TextView ad_aign;
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

	public XFYSAD(Activity mContext, View parentView, String adId){
		this.mContext = mContext;
		this.parentView = parentView;
		this.adId = adId;
		mInflater = LayoutInflater.from(mContext);
		ad_img = (SimpleDraweeView)parentView.findViewById(R.id.ad_img);
		xx_ad_layout = (FrameLayout)parentView.findViewById(R.id.xx_ad_layout);
		ad_aign = (TextView)parentView.findViewById(R.id.ad_sign);
	}

	public XFYSAD(Activity mContext, String adId){
		this.mContext = mContext;
		this.adId = adId;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setParentView(View parentView){
		LogUtil.DefalutLog("XFYSAD---setParentView");
		if(System.currentTimeMillis() - lastLoadAdTime > 1000*30){
			this.parentView = parentView;
			ad_img = (SimpleDraweeView)parentView.findViewById(R.id.ad_img);
			xx_ad_layout = (FrameLayout)parentView.findViewById(R.id.xx_ad_layout);
			ad_aign = (TextView)parentView.findViewById(R.id.ad_sign);
		}
	}
	
	public void isNeedReload(){
		if(System.currentTimeMillis() - lastLoadAdTime > 1000*7){
			lastLoadAdTime = System.currentTimeMillis();
			showAD();
		}
	}
	
	public void showAD(){
		LogUtil.DefalutLog("XFYSAD---showAD");
		if(ADUtil.IsShowAD){
			if(System.currentTimeMillis() - lastLoadAdTime > 4500){
				ad_aign.setVisibility(View.GONE);
				if(ADUtil.Advertiser.equals(ADUtil.Advertiser_XF)){
					loadXFAD();
				}else {
					loadTXAD();
				}
			}
		}else {
			if(parentView != null){
				parentView.setVisibility(View.GONE);
			}
			if(xx_ad_layout != null){
				xx_ad_layout.setVisibility(View.GONE);
			}
			if(ad_aign != null){
				ad_aign.setVisibility(View.GONE);
			}
		}
	}

	private void loadTXAD(){
		LogUtil.DefalutLog("---load TXAD Data---");
		lastLoadAdTime = System.currentTimeMillis();
		TXADUtil.showCDT(mContext, new NativeExpressAD.NativeExpressADListener() {
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
				if(mTXADView != null){
					mTXADView.destroy();
				}
				xx_ad_layout.setVisibility(View.VISIBLE);
				xx_ad_layout.removeAllViews();
				mTXADView = list.get(0);
				xx_ad_layout.addView(mTXADView);
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
				if(xx_ad_layout != null){
					xx_ad_layout.removeAllViews();
					xx_ad_layout.setVisibility(View.GONE);
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
		lastLoadAdTime = System.currentTimeMillis();
		nativeAd = new IFLYNativeAd(mContext, adId, new IFLYNativeListener() {
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
			public void onADLoaded(List<NativeADDataRef> arg0) {
				LogUtil.DefalutLog("---onADLoaded---");
				if(arg0 != null && arg0.size() > 0){
					mNativeADDataRef = arg0.get(0);
					setAdData();
				}
			}
		});
		nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		nativeAd.loadAd(ADUtil.adCount);
	}

	private void onADFaile(){
		parentView.setVisibility(View.GONE);
		hideHeader(true);
		if(ADUtil.isHasLocalAd()){
			mNativeADDataRef = ADUtil.getRandomAd();
			setAdData();
		}
	}
	
	private void setAdData(){
		try {
			hideHeader(false);
			ad_aign.setVisibility(View.VISIBLE);
			xx_ad_layout.setVisibility(View.GONE);
			parentView.setVisibility(View.VISIBLE);
			ad_img.setImageURI(mNativeADDataRef.getImage());
			if(isDirectExPosure){
                exposure = mNativeADDataRef.onExposured(parentView);
                LogUtil.DefalutLog("XFYSAD-setAdData-exposure:"+exposure);
            }
			parentView.setOnClickListener(new OnClickListener() {
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

	public void hideHeader(final boolean isFaile){
		if(parentView != null){
			parentView.setVisibility(View.GONE);
		}
		if(mAdapter != null){
			if(isFaile){
				mAdapter.hideHeader();
			}else {
				mAdapter.showHeader();
			}
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
	/**
	 * 判断view是否在屏幕范围内（是否曝光）。
	 * @param context
	 * @param view
	 * @return true view矩形在屏幕矩形内或相交
	 */
	public static boolean isInScreen(Context context,View view) {
		//获取屏幕宽高（px）以及矩形
		Rect screenRect = getScreenRect(context);
		//获取view矩形
		Rect viewRect=getViewRect(view);
		int viewHight= (int) ((viewRect.bottom-viewRect.top)*0.3);
		screenRect=new Rect(screenRect.left, screenRect.top+viewHight, screenRect.right, screenRect.bottom-viewHight);
		//判断是否相交或包含
		boolean intersects=Rect.intersects(screenRect, viewRect);
		boolean contains=screenRect.contains(viewRect);
		return intersects||contains;

	}

	///Ad_Android_SDK_v1/src/com/iflytek/voiceads/utils/AppState.java
	/**
	 * 获取屏幕矩形
	 * @param context
	 * @return screenRect
	 */
	public static Rect getScreenRect(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display=windowManager.getDefaultDisplay();
		int screenW=display.getWidth();
		int screenH=display.getHeight();
		Rect screenRect=new Rect(0,0,screenW,screenH);
		return screenRect;
	}

///Ad_Android_SDK_v1/src/com/iflytek/voiceads/utils/AppState.java
	/**
	 * 获取view矩形
	 * @param view
	 * @return viewRect
	 */
	public static Rect getViewRect(View view){
		int[] location=new int[2];
		view.getLocationOnScreen(location);
		Rect viewRect=new Rect(location[0],location[1],location[0]+view.getWidth(),location[1]+view.getHeight());
		return viewRect;
	}

}
