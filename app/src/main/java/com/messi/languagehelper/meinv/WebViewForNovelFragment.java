package com.messi.languagehelper.meinv;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.List;


public class WebViewForNovelFragment extends BaseFragment implements View.OnClickListener{
	
	private ProgressBar progressdeterminate;
	private WebView mWebView;
	private TextView tap_to_reload;
	private TextView ad_go_on;
	private RelativeLayout ad_layout;
	private LinearLayout ad_content;
    public String Url;
	public String filter_source_name;
    private long lastClick;
	private String adFilte;
	private String ad_url;
	private NativeExpressADView mTXADView;

	public static WebViewForNovelFragment newInstance(String url, String source_name){
		WebViewForNovelFragment fragment = new WebViewForNovelFragment();
		fragment.Url = url;
		fragment.filter_source_name = source_name;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.web_view_for_novel, null);
		initViews(view);
		getFilter();
		return view;
	}
	
	private void initViews(View view){
		progressdeterminate = (ProgressBar) view.findViewById(R.id.progressdeterminate);
		mWebView = (WebView) view.findViewById(R.id.refreshable_webview);
		tap_to_reload = (TextView) view.findViewById(R.id.tap_to_reload);
		ad_go_on = (TextView) view.findViewById(R.id.ad_go_on);
		ad_layout = (RelativeLayout) view.findViewById(R.id.ad_layout);
		ad_content = (LinearLayout) view.findViewById(R.id.ad_content);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setBlockNetworkImage(false);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}
		tap_to_reload.setOnClickListener(this);
		ad_go_on.setOnClickListener(this);
		//当前页面加载
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				LogUtil.DefalutLog("WebViewClient:onPageStarted");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				LogUtil.DefalutLog("failingUrl:"+failingUrl);
				if(failingUrl.contains("openapp.jdmobile") || failingUrl.contains("taobao")){
					Uri uri = Uri.parse(failingUrl);
					view.loadUrl("");
					ADUtil.toAdActivity(getContext(),uri);
				}else {
					view.loadUrl("");
					if(System.currentTimeMillis() - lastClick < 500){
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								tap_to_reload.setVisibility(View.VISIBLE);
							}
						}, 600);
					}else{
						tap_to_reload.setVisibility(View.VISIBLE);
					}
				}
				LogUtil.DefalutLog("WebViewClient:onReceivedError---"+errorCode);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				hideAd(view);
				LogUtil.DefalutLog("WebViewClient:onPageFinished");
			}

			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				LogUtil.DefalutLog("uploadLogAdSDK");
				LogUtil.DefalutLog(url);
				int action = getAdAction(url);
				if(action == 1){
					return new WebResourceResponse(null, null, null);
				}else if(action == 2){
					showAD();
					return new WebResourceResponse(null, null, null);
				}
				return super.shouldInterceptRequest(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedSslError(WebView view,final SslErrorHandler handler, SslError error) {
//				final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//				String message = "SSL Certificate error.";
//				message += " Do you want to continue anyway?";
//				builder.setTitle("SSL Certificate Error");
//				builder.setMessage(message);
//				builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						handler.proceed();
//					}
//				});
//				builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						handler.cancel();
//					}
//				});
//				final AlertDialog dialog = builder.create();
//				dialog.show();
			}
		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
	        @Override
	        public void onProgressChanged(WebView view, int newProgress) {
	            if (newProgress == 100) {
	            	progressdeterminate.setVisibility(View.GONE);
	            	LogUtil.DefalutLog("WebViewClient:newProgress == 100");
	            } else {
	                if (progressdeterminate.getVisibility() == View.GONE)
	                	progressdeterminate.setVisibility(View.VISIBLE);
	                progressdeterminate.setProgress(newProgress);
	            }
	            super.onProgressChanged(view, newProgress);
	        }
	    });
		mWebView.loadUrl(Url);
	}

	private void showAD(){
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadTXAD();
			}
		});
	}

	private void loadTXAD() {
		ad_layout.setVisibility(View.VISIBLE);
		ad_content.removeAllViews();
		TXADUtil.showCDTZX(getActivity(), new NativeExpressAD.NativeExpressADListener() {
			@Override
			public void onNoAD(com.qq.e.comm.util.AdError adError) {
				LogUtil.DefalutLog(adError.getErrorMsg());
				ad_layout.setVisibility(View.GONE);
			}

			@Override
			public void onADLoaded(List<NativeExpressADView> list) {
				LogUtil.DefalutLog("onADLoaded");
				if (list != null && list.size() > 0) {
					ad_layout.setVisibility(View.VISIBLE);
					if (mTXADView != null) {
						mTXADView.destroy();
					}
					ad_content.removeAllViews();
					mTXADView = list.get(0);
					ad_content.addView(mTXADView);
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

	private void getFilter(){
		if(!TextUtils.isEmpty(filter_source_name)){
			try {
				AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.AdFilter.AdFilter);
				query.whereEqualTo(AVOUtil.AdFilter.name, filter_source_name);
				query.findInBackground(new FindCallback<AVObject>() {
					@Override
					public void done(List<AVObject> list, AVException e) {
						if(list != null && list.size() > 0){
							AVObject mAVObject = list.get(0);
							if(mAVObject != null){
								adFilte = mAVObject.getString(AVOUtil.AdFilter.filter);
								ad_url = mAVObject.getString(AVOUtil.AdFilter.ad_url);
								hideAd(mWebView);
							}
						}
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void hideAd(final WebView view){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(!TextUtils.isEmpty(adFilte)){
					String[] filters = adFilte.split("#");
					if(filters != null && filters.length > 0){
						for(final String items : filters){
							if(!TextUtils.isEmpty(items)){
								if(items.startsWith("id:") || items.startsWith("class:") ){
									String[] item = items.split(":");
									if(item != null && item.length > 1){
										if(item[0].equals("id")){
											view.loadUrl(
													"javascript:(function() { " +
															"var element = document.getElementById('"+item[1]+"');"
															+ "element.parentNode.removeChild(element);" + "})()");
										}else if(item[0].equals("class")){
											view.loadUrl(
													"javascript:(function() { " +
															"var element = document.getElementsByClassName('"+item[1]+"')[0];"
															+ "element.parentNode.removeChild(element);" + "})()");
										}
									}
								}else {
									view.loadUrl(items);
								}

							}
						}
					}
				}
			}
		},60);
	}
	//ad-api:1#v2/ad:2
	private int getAdAction(String url){
		int action = 0;
		if(!TextUtils.isEmpty(ad_url)){
			String[] filters = ad_url.split("#");
			if(filters != null && filters.length > 0){
				for(final String item : filters) {
					String[] fls = item.split(":");
					if(fls != null && fls.length == 2){
						if(url.contains(fls[0])){
							action = Integer.parseInt(fls[1]);
						}
					}
				}
			}
		}
		return action;
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.tap_to_reload){
			mWebView.loadUrl(Url);
			tap_to_reload.setVisibility(View.GONE);
			lastClick = System.currentTimeMillis();
		}else if(view.getId() == R.id.ad_go_on){
			ad_layout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mWebView.destroy();
		if (mTXADView != null) {
			mTXADView.destroy();
		}
	}
}
