package com.messi.languagehelper.meinv;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.ShareUtil;

public class WebViewNoAdActivity extends BaseActivity {
	
	private ProgressBar progressdeterminate;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private WebView mWebView;
	private TextView tap_to_reload;
    private String Url;
    private String title;
    private String ShareUrlMsg;
    private int ToolbarBackgroundColor;
    private boolean isReedPullDownRefresh;
    private boolean isHideToolbar;
    private long lastClick;
    private String adFilte;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		initData();
		initViews();
	}
	
	private void initData(){
		Url = getIntent().getStringExtra(KeyUtil.URL);
		title = getIntent().getStringExtra(KeyUtil.ActionbarTitle);
		ShareUrlMsg = getIntent().getStringExtra(KeyUtil.ShareUrlMsg);
		adFilte = getIntent().getStringExtra(KeyUtil.AdFilter);
		ToolbarBackgroundColor = getIntent().getIntExtra(KeyUtil.ToolbarBackgroundColorKey,0);
		isReedPullDownRefresh = getIntent().getBooleanExtra(KeyUtil.IsReedPullDownRefresh, true);
		isHideToolbar = getIntent().getBooleanExtra(KeyUtil.IsHideToolbar, false);
		LogUtil.DefalutLog("ToolbarBackgroundColor:"+ToolbarBackgroundColor);
		if(ToolbarBackgroundColor != 0){
			setToolbarBackground(ToolbarBackgroundColor);
		}
		if(!TextUtils.isEmpty(title)){
			getSupportActionBar().setTitle(title);
		}
		if (isHideToolbar) {
			setStatusbarColor(R.color.black);
			getSupportActionBar().hide();
		}
	}
	
	private void initViews(){
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		progressdeterminate = (ProgressBar) findViewById(R.id.progressdeterminate);
		mWebView = (WebView) findViewById(R.id.refreshable_webview);
		tap_to_reload = (TextView) findViewById(R.id.tap_to_reload);
		setScrollable(mSwipeRefreshLayout);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.getSettings().setBlockNetworkImage(false);
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
			mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		tap_to_reload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.loadUrl(Url);
				tap_to_reload.setVisibility(View.GONE);
				lastClick = System.currentTimeMillis();
			}
		});

		//当前页面加载
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url.contains("openapp.jdmobile") || url.contains("taobao")){
					Uri uri = Uri.parse(url);
					view.loadUrl("");
					ADUtil.toAdActivity(WebViewNoAdActivity.this,uri);
					WebViewNoAdActivity.this.finish();
					return true;
				}else if(url.contains("bilibili:")){
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				LogUtil.DefalutLog("WebViewClient:onPageStarted");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				LogUtil.DefalutLog("failingUrl:"+failingUrl);
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

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mSwipeRefreshLayout.setRefreshing(false);
				LogUtil.DefalutLog("WebViewClient:onPageFinished");
				hideAd(view);
			}

			@Override
			public void onReceivedSslError(WebView view,final SslErrorHandler handler, SslError error) {
//				final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewNoAdActivity.this);
//				String message = "SSL Certificate error.";
//				switch (error.getPrimaryError()) {
//					case SslError.SSL_UNTRUSTED:
//						message = "The certificate authority is not trusted.";
//						break;
//					case SslError.SSL_EXPIRED:
//						message = "The certificate has expired.";
//						break;
//					case SslError.SSL_IDMISMATCH:
//						message = "The certificate Hostname mismatch.";
//						break;
//					case SslError.SSL_NOTYETVALID:
//						message = "The certificate is not yet valid.";
//						break;
//				}
//				message += " Do you want to continue anyway?";
//
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
	            	mSwipeRefreshLayout.setRefreshing(false);
	            	LogUtil.DefalutLog("WebViewClient:newProgress == 100");

//					view.loadUrl("javascript:function setTop(){document.querySelector('section.app-guiding bottom-fixed no-close').style.display=\"none\";}setTop();");
//					view.loadUrl("javascript:function hidead2(){document.querySelector('div.slide-container js-container').style.display=\"none\";}hidead2();");
	            } else {
	                if (progressdeterminate.getVisibility() == View.GONE)
	                	progressdeterminate.setVisibility(View.VISIBLE);
	                progressdeterminate.setProgress(newProgress);
	            }
	            super.onProgressChanged(view, newProgress);
	        }
	    });
		
		mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, 
	            R.color.holo_green_light, 
	            R.color.holo_orange_light, 
	            R.color.holo_red_light);
		
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mWebView.reload();
			}
		});
		mSwipeRefreshLayout.setEnabled(false);
		mWebView.loadUrl(Url);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		try {
			menu.add(0,0,0,this.getResources().getString(R.string.menu_share)).setIcon(R.drawable.ic_share_white_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			if(!TextUtils.isEmpty(ShareUrlMsg)){
				ShareUtil.shareText(WebViewNoAdActivity.this, ShareUrlMsg);
			}else {
				ShareUtil.shareText(WebViewNoAdActivity.this,mWebView.getTitle() + " (share from:中英互译) " + Url);
			}
			break;
		}
       return super.onOptionsItemSelected(item);
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
		LogUtil.DefalutLog("Url:"+mWebView.getUrl());
		mWebView.destroy();
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
}
