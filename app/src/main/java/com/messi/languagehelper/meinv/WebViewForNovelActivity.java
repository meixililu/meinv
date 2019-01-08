package com.messi.languagehelper.meinv;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
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

import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.List;

import cn.leancloud.AVException;
import cn.leancloud.AVObject;
import cn.leancloud.AVQuery;
import cn.leancloud.callback.FindCallback;
import cn.leancloud.convertor.ObserverBuilder;


public class WebViewForNovelActivity extends BaseActivity implements View.OnClickListener{
	
	private ProgressBar progressdeterminate;
	private WebView mWebView;
	private TextView tap_to_reload;
	private TextView ad_go_on;
	private RelativeLayout ad_layout;
	private LinearLayout ad_content;
    private String Url;
    private long lastClick;
	private String adFilte;
	private String ad_url;
	private String filter_source_name;
	private NativeExpressADView mTXADView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view_for_novel);
		setStatusbarColor(R.color.white);
		changeStatusBarTextColor(true);
		initData();
		initViews();
		getFilter();
	}
	
	private void initData(){
		Url = getIntent().getStringExtra(KeyUtil.URL);
		filter_source_name = getIntent().getStringExtra(KeyUtil.FilterName);
	}
	
	private void initViews(){
		progressdeterminate = (ProgressBar) findViewById(R.id.progressdeterminate);
		mWebView = (WebView) findViewById(R.id.refreshable_webview);
		tap_to_reload = (TextView) findViewById(R.id.tap_to_reload);
		ad_go_on = (TextView) findViewById(R.id.ad_go_on);
		ad_layout = (RelativeLayout) findViewById(R.id.ad_layout);
		ad_content = (LinearLayout) findViewById(R.id.ad_content);
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
					ADUtil.toAdActivity(WebViewForNovelActivity.this,uri);
					WebViewForNovelActivity.this.finish();
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
				final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewForNovelActivity.this);
				String message = "SSL Certificate error.";
				message += " Do you want to continue anyway?";
				builder.setTitle("SSL Certificate Error");
				builder.setMessage(message);
				builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.proceed();
					}
				});
				builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						handler.cancel();
					}
				});
				final AlertDialog dialog = builder.create();
				dialog.show();
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
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadTXAD();
			}
		});
	}

	private void loadTXAD() {
		ad_layout.setVisibility(View.VISIBLE);
		ad_content.removeAllViews();
		TXADUtil.showCDTZX(this, new NativeExpressAD.NativeExpressADListener() {
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
				query.findInBackground().subscribe(ObserverBuilder.buildSingleObserver(new FindCallback<AVObject>() {
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
				}));
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView.destroy();
		if (mTXADView != null) {
			mTXADView.destroy();
		}
	}
}
