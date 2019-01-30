package com.messi.languagehelper.meinv;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.messi.languagehelper.meinv.box.BoxHelper;
import com.messi.languagehelper.meinv.box.CNWBean;
import com.messi.languagehelper.meinv.box.WebFilter;
import com.messi.languagehelper.meinv.util.ADUtil;
import com.messi.languagehelper.meinv.util.AVOUtil;
import com.messi.languagehelper.meinv.util.KeyUtil;
import com.messi.languagehelper.meinv.util.LogUtil;
import com.messi.languagehelper.meinv.util.TXADUtil;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import java.util.List;

public class WebViewForMiaosouActivity extends BaseActivity implements OnClickListener{

	private ProgressBar progressdeterminate;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private WebView mWebView;
	private TextView tap_to_reload;
    private TextView ad_go_on;
    private RelativeLayout ad_layout;
    private LinearLayout ad_content;

    private String Url;
    private String ShareUrlMsg;
    private long lastClick;
	private String adFilte;
	private boolean is_need_get_filter;
	private String filter_source_name;

	private boolean mExoPlayerFullscreen = false;
    private String ad_url;
    private NativeExpressADView mTXADView;
	private CNWBean mAVObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view_for_miaosou_activity);
		setStatusbarColor(R.color.white);
		changeStatusBarTextColor(true);
		initData();
		initViews();
	}

	private void initData(){
		mAVObject = getIntent().getParcelableExtra(KeyUtil.ObjectKey);
		if(mAVObject != null){
			mAVObject = BoxHelper.getNewestData(mAVObject);
			if(!TextUtils.isEmpty(mAVObject.getLast_read_url())){
				Url = mAVObject.getLast_read_url();
			}else {
				Url = mAVObject.getRead_url();
			}
			ShareUrlMsg = getIntent().getStringExtra(KeyUtil.ShareUrlMsg);
			adFilte = getIntent().getStringExtra(KeyUtil.AdFilter);
			filter_source_name = getIntent().getStringExtra(KeyUtil.FilterName);
			is_need_get_filter = getIntent().getBooleanExtra(KeyUtil.IsNeedGetFilter,false);
		}else {
			finish();
		}
	}

	private void initViews(){
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
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
		mSwipeRefreshLayout.setEnabled(false);
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
				LogUtil.DefalutLog("WebViewClient:onReceivedError---"+errorCode);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				hideAd(view);
				LogUtil.DefalutLog("WebViewClient:onPageFinished");
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.DefalutLog("OverrideUrl:"+url);
				Url = url;
				if(url.contains("openapp.jdmobile") || url.contains("taobao")){
					Uri uri = Uri.parse(url);
					ADUtil.toAdActivity(WebViewForMiaosouActivity.this,uri);
					WebViewForMiaosouActivity.this.finish();
					return true;
				}else if(url.contains("bilibili:")){
					return true;
				}
				return super.shouldOverrideUrlLoading(view, url);
			}

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                LogUtil.DefalutLog("Intercept:"+url);
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
			public void onReceivedSslError(WebView view,final SslErrorHandler handler, SslError error) {
//				final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewForMiaosouActivity.this);
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
				} else {
					if (progressdeterminate.getVisibility() == View.GONE)
						progressdeterminate.setVisibility(View.VISIBLE);
					progressdeterminate.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		if(is_need_get_filter){
			getFilter();
		}
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
			WebFilter mWebFilter = BoxHelper.findWebFilterByName(filter_source_name);
			if(mWebFilter != null){
				adFilte = mWebFilter.getAd_filter();
				ad_url = mWebFilter.getAd_url();
				hideAd(mWebView);
				LogUtil.DefalutLog("getFilter---WebFilter:"+adFilte+"--"+"ad_url:"+ad_url);
			}else {
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
									LogUtil.DefalutLog("getFilter---internet:"+adFilte+"--"+"ad_url:"+ad_url);
								}
							}
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mAVObject != null){
			LogUtil.DefalutLog("webview:"+mWebView.getUrl()+"--Url:"+Url);
			if(!TextUtils.isEmpty(mWebView.getUrl())){
				mAVObject.setLast_read_url(mWebView.getUrl());
			}else if(!TextUtils.isEmpty(Url)){
				mAVObject.setLast_read_url(Url);
			}
			mAVObject.setHistory(System.currentTimeMillis());
			mAVObject.setUpdateTime(System.currentTimeMillis());
			BoxHelper.updateCNWBean(mAVObject);
		}
		if(mWebView != null){
			mWebView.destroy();
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
                            break;
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
}
